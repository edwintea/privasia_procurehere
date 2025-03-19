package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventMeeting;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventDocumentPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.RftEventEditor;

public class EventMeetingBase {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Resource
	MessageSource messageSource;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfqMeetingService rfqMeetingService;

	@Autowired
	RfaEventService rfaService;

	@Autowired
	RftEventService rftService;

	@Autowired
	RfiEventService rfiService;

	@Autowired
	RfpEventService rfpService;

	@Autowired
	RfqEventService rfqService;

	@Autowired
	RftEventEditor rftEventEditor;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	OwnerSettingsService ownerSettingsService;
	
	@Autowired
	TatReportService tatReportService;

	private RfxTypes eventType;

	public EventMeetingBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "4";
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "appointmentDateTime", new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(Date.class, "appointmentTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(List.class, "inviteSuppliers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					Supplier group = supplierService.findSuppById(id);
					return group;
				}
				return null;
			}
		});
	}

	/**
	 * @param rftEvent
	 * @return
	 */
	protected ModelAndView meetingNext(Event rftEvent) {
		if (Boolean.TRUE == rftEvent.getQuestionnaires()) {
			return new ModelAndView("redirect:eventCqList/" + rftEvent.getId());
		} else if (Boolean.TRUE == rftEvent.getBillOfQuantity()) {
			return new ModelAndView("redirect:createBQList/" + rftEvent.getId());
		} else if (Boolean.TRUE == rftEvent.getScheduleOfRate()) {
			return new ModelAndView("redirect:createSorList/" + rftEvent.getId());
		} else {
			return new ModelAndView("redirect:eventSummary/" + rftEvent.getId());
		}

	}

	/**
	 * @param rftEvent
	 * @return
	 */
	protected String meetingPrevious(Event rftEvent) {
		if (rftEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return "redirect:addSupplier/" + rftEvent.getId();
		} else if (Boolean.TRUE == rftEvent.getDocumentReq()) {
			return "redirect:createEventDocuments/" + rftEvent.getId();
		} else {
			return "redirect:eventDescription/" + rftEvent.getId();
		}
	}

	/**
	 * @param meetingId
	 * @return
	 */
	protected ModelAndView showMeeting(@RequestParam String meetingId) {
		return new ModelAndView("redirect:viewMeeting/" + meetingId);
	}

	/**
	 * @param file
	 * @param session
	 * @param fileName
	 * @param meetingDocuments
	 * @param headers
	 * @param bytes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<EventMeetingDocument> constructMeetingDocument(MultipartFile file, HttpSession session, String fileName, List<EventMeetingDocument> meetingDocuments, HttpHeaders headers, byte[] bytes) {
		if (session != null) {
			meetingDocuments = (List<EventMeetingDocument>) session.getAttribute("listMeetingDocument");
			if (CollectionUtil.isEmpty(meetingDocuments)) {
				meetingDocuments = new ArrayList<EventMeetingDocument>();
			}
			EventMeetingDocument rftEventMeetingDocument = new EventMeetingDocument();
			rftEventMeetingDocument.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			rftEventMeetingDocument.setFileName(fileName);
			rftEventMeetingDocument.setFileData(bytes);
			rftEventMeetingDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
			rftEventMeetingDocument.setCredContentType(file.getContentType());
			rftEventMeetingDocument.setRefId(String.valueOf(System.currentTimeMillis()));
			meetingDocuments.add(rftEventMeetingDocument);
			session.setAttribute("listMeetingDocument", meetingDocuments);
			headers.add("success", messageSource.getMessage("rft.meeting.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
			LOG.info(meetingDocuments.size() + " file uploded");
		}
		return meetingDocuments;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadMeetingFile", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventDocumentPojo>> uploadMeetingFile(@RequestParam("file") MultipartFile file, @RequestParam("id") String meetingId, HttpSession session) {
		String fileName = null;
		List<EventDocumentPojo> meetingDocuments = null;
		LOG.info("Meeting Id : " + meetingId);
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT   : " + bytes.length);
				if (session != null) {
					meetingDocuments = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isEmpty(meetingDocuments)) {
						meetingDocuments = new ArrayList<EventDocumentPojo>();
					}
					EventDocumentPojo rfxEventMeetingDocument = new EventDocumentPojo();
					rfxEventMeetingDocument.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					rfxEventMeetingDocument.setFileName(fileName);
					rfxEventMeetingDocument.setFileData(bytes);
					rfxEventMeetingDocument.setFileSize(bytes.length > 0 ? bytes.length / 1024 : 0);
					rfxEventMeetingDocument.setCredContentType(file.getContentType());
					rfxEventMeetingDocument.setRefId(String.valueOf(System.currentTimeMillis()));
					meetingDocuments.add(rfxEventMeetingDocument);
					session.setAttribute("listMeetingDocument", meetingDocuments);

					// Clear data before send back to UI
					List<EventDocumentPojo> clean = new ArrayList<EventDocumentPojo>();
					for (EventDocumentPojo doc : meetingDocuments) {
						EventDocumentPojo ndoc = new EventDocumentPojo();
						ndoc.setCredContentType(doc.getCredContentType());
						ndoc.setFileName(doc.getFileName());
						ndoc.setFileSize(doc.getFileSize());
						ndoc.setId(doc.getId());
						ndoc.setRefId(doc.getRefId());
						ndoc.setTenantId(doc.getTenantId());
						clean.add(ndoc);
					}
					meetingDocuments = clean;

					headers.add("success", messageSource.getMessage("rft.meeting.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
					LOG.info(meetingDocuments.size() + " file uploded");
				}

			} catch (Exception e) {
				LOG.error("Failed to upload " + fileName + ": " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("rft.meeting.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<EventDocumentPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			headers.add("error", messageSource.getMessage("rft.meeting.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<EventDocumentPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<EventDocumentPojo>>(meetingDocuments, headers, HttpStatus.OK);
	}

	public List<Supplier> removeSupplier(String removeSupId, String meetingId) {
		List<Supplier> list = null;

		switch (getEventType()) {
		case RFT:
			list = rftMeetingService.removeSupplier(meetingId, removeSupId);
			break;
		case RFI:
			list = rfiMeetingService.removeSupplier(meetingId, removeSupId);
			break;
		case RFP:
			list = rfpMeetingService.removeSupplier(meetingId, removeSupId);
			break;
		case RFA:
			list = rfaMeetingService.removeSupplier(meetingId, removeSupId);
			break;
		case RFQ:
			list = rfqMeetingService.removeSupplier(meetingId, removeSupId);
			break;

		default:
			break;
		}

		return list;
	}

	/**
	 * @param response
	 * @param docs
	 * @throws IOException
	 */
	protected void setResponseForDownload(HttpServletResponse response, EventDocumentPojo docs) throws IOException {
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/removeMeetingDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<EventDocumentPojo>> removeMeetingDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("removeDocRefId") String removeDocRefId, HttpSession session) {
		LOG.info("Meeting Doc Id  db:: :: " + removeDocId + ", Meeting Doc Id  :: :: " + removeDocRefId);
		HttpHeaders headers = new HttpHeaders();
		String filename = null;
		List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
		try {
			if (removeDocRefId != null && !removeDocRefId.equals("undefined") && CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
				for (EventDocumentPojo document : oldListMeetingDocument) {
					if (document.getRefId() != null && document.getRefId().equals(removeDocRefId)) {
						oldListMeetingDocument.remove(document);
						break;
					}
				}
			} else if (removeDocId != null && !removeDocId.equals("undefined") && CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
				EventMeetingDocument eventMeetingDocument = null;
				switch (getEventType()) {
				case RFA:
					eventMeetingDocument = rfaMeetingService.getRfaEventMeetingDocument(removeDocId);
					filename = eventMeetingDocument.getFileName();
					LOG.info("file name : " + filename);
					rfaMeetingService.removeMeetingDocs(eventMeetingDocument);
					for (EventDocumentPojo document : oldListMeetingDocument) {
						if (document.getId() != null && document.getId().equals(removeDocId)) {
							oldListMeetingDocument.remove(document);
							break;
						}
					}

					break;
				case RFT:
					eventMeetingDocument = rftMeetingService.getRftEventMeetingDocument(removeDocId);
					rftMeetingService.removeMeetingDocs(eventMeetingDocument);
					for (EventDocumentPojo document : oldListMeetingDocument) {
						if (document.getId() != null && document.getId().equals(removeDocId)) {
							oldListMeetingDocument.remove(document);
							break;
						}
					}
					filename = eventMeetingDocument.getFileName();
					break;
				case RFP:
					eventMeetingDocument = rfpMeetingService.getRfpEventMeetingDocument(removeDocId);
					rfpMeetingService.removeMeetingDocs(eventMeetingDocument);
					for (EventDocumentPojo document : oldListMeetingDocument) {
						if (document.getId() != null && document.getId().equals(removeDocId)) {
							oldListMeetingDocument.remove(document);
							break;
						}
					}
					break;
				case RFI:
					eventMeetingDocument = rfiMeetingService.getRfiEventMeetingDocument(removeDocId);
					rfiMeetingService.removeMeetingDocs(eventMeetingDocument);
					for (EventDocumentPojo document : oldListMeetingDocument) {
						if (document.getId() != null && document.getId().equals(removeDocId)) {
							oldListMeetingDocument.remove(document);
							break;
						}
					}
					break;
				case RFQ:
					eventMeetingDocument = rfqMeetingService.getRfqEventMeetingDocument(removeDocId);
					rfqMeetingService.removeMeetingDocs(eventMeetingDocument);
					for (EventDocumentPojo document : oldListMeetingDocument) {
						if (document.getId() != null && document.getId().equals(removeDocId)) {
							oldListMeetingDocument.remove(document);
							break;
						}
					}
					break;
				default:
					break;
				}
			}
			headers.add("success", messageSource.getMessage("rft.meeting.remove", new Object[] { filename }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while delete Document :" + e.getMessage());
			headers.add("error", messageSource.getMessage("rft.meeting.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<EventDocumentPojo>>(oldListMeetingDocument, headers, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<List<EventDocumentPojo>>(oldListMeetingDocument, headers, HttpStatus.OK);
	}

	/**
	 * @param model
	 * @param eventMeeting
	 * @param eventId
	 */
	protected Boolean validateMeeting(EventMeeting eventMeeting, Model model, String eventId) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting ....." + getEventType());
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		switch (getEventType()) {
		case RFA: {
			LOG.info("here to validate Rfa : ");
			Set<ConstraintViolation<RfaEventMeeting>> constraintViolations = validator.validate((RfaEventMeeting) eventMeeting, RfaEventMeeting.EventMeeting.class);
			for (ConstraintViolation<RfaEventMeeting> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				errorList.add(cv.getMessage());
			}
			break;
		}
		case RFI: {
			Set<ConstraintViolation<RfiEventMeeting>> constraintViolations = validator.validate((RfiEventMeeting) eventMeeting, RfiEventMeeting.EventMeeting.class);
			for (ConstraintViolation<RfiEventMeeting> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				errorList.add(cv.getMessage());
			}
			break;
		}
		case RFP: {
			Set<ConstraintViolation<RfpEventMeeting>> constraintViolations = validator.validate((RfpEventMeeting) eventMeeting, RfpEventMeeting.EventMeeting.class);
			for (ConstraintViolation<RfpEventMeeting> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				errorList.add(cv.getMessage());
			}
			break;
		}
		case RFQ: {
			Set<ConstraintViolation<RfqEventMeeting>> constraintViolations = validator.validate((RfqEventMeeting) eventMeeting, RfqEventMeeting.EventMeeting.class);
			for (ConstraintViolation<RfqEventMeeting> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				errorList.add(cv.getMessage());
			}
			break;
		}
		case RFT: {
			Set<ConstraintViolation<RftEventMeeting>> constraintViolations = validator.validate((RftEventMeeting) eventMeeting, RftEventMeeting.EventMeeting.class);
			for (ConstraintViolation<RftEventMeeting> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				errorList.add(cv.getMessage());
			}
			break;
		}
		default:
			break;
		}

		model.addAttribute("errors", errorList);
		if (errorList.isEmpty()) {
			return Boolean.FALSE;
		} else {
			if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
				model.addAttribute("btnValue", "Create");
			} else {
				model.addAttribute("btnValue", "Update");
			}
			Event eventObj = null;

			switch (getEventType()) {
			case RFA:
				eventObj = rfaService.getRfaEventById(eventId);
				model.addAttribute("event", eventObj);
				model.addAttribute("listSuppliers", rfaMeetingService.getEventSuppliers(eventId));
				break;
			case RFT:
				eventObj = rftService.getRftEventById(eventId);
				model.addAttribute("event", eventObj);
				model.addAttribute("listSuppliers", rftMeetingService.getEventSuppliers(eventId));
				break;
			case RFI:
				eventObj = rfiService.getRfiEventById(eventId);
				model.addAttribute("event", eventObj);
				model.addAttribute("listSuppliers", rfiMeetingService.getEventSuppliers(eventId));
				break;
			case RFP:
				eventObj = rfpService.getEventById(eventId);
				model.addAttribute("event", eventObj);
				model.addAttribute("listSuppliers", rfpMeetingService.getEventSuppliers(eventId));
				break;
			case RFQ:
				eventObj = rfqService.getEventById(eventId);
				model.addAttribute("event", eventObj);
				model.addAttribute("listSuppliers", rfqMeetingService.getEventSuppliers(eventId));
				break;
			default:
				break;
			}

			return Boolean.TRUE;
		}
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	/**
	 * @param meetingId
	 * @return
	 */
	public String deleteMeeting(String meetingId) {
		String eventId = null;
		switch (getEventType()) {
		case RFA:
			RfaEventMeeting rfaEventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
			eventId = rfaEventMeeting.getRfxEvent().getId();
			rfaMeetingService.deleteMeeting(rfaEventMeeting);
			break;
		case RFI:
			RfiEventMeeting rfiEventMeeting = rfiMeetingService.getRfiMeetingById(meetingId);
			eventId = rfiEventMeeting.getRfxEvent().getId();
			RfiEvent event = rfiService.getPlainEventById(eventId);
			rfiMeetingService.deleteMeeting(rfiEventMeeting);
			break;
		case RFP:
			RfpEventMeeting rfpEventMeeting = rfpMeetingService.getRfpMeetingById(meetingId);
			eventId = rfpEventMeeting.getRfxEvent().getId();
			rfpMeetingService.deleteMeeting(rfpEventMeeting);
			break;
		case RFQ:
			RfqEventMeeting rfqEventMeeting = rfqMeetingService.getMeetingById(meetingId);
			eventId = rfqEventMeeting.getRfxEvent().getId();
			rfqMeetingService.deleteMeeting(rfqEventMeeting);
			break;
		case RFT:
			RftEventMeeting rftEventMeeting = rftMeetingService.getRftMeetingById(meetingId);
			eventId = rftEventMeeting.getRfxEvent().getId();
			rftMeetingService.deleteMeeting(rftEventMeeting);
			break;
		default:
			break;

		}
		return eventId;
	}

	@RequestMapping(value = "/meetingSupplierList/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<Supplier>> eventReportList(@PathVariable String eventId, @RequestParam(name = "meetingId", required = false) String meetingId, @RequestParam(name = "selectedList") String selectedList, @RequestParam(name = "unSelectedList") String unSelectedList, @RequestParam(name = "checkedAll", required = false) Boolean checkedAll, HttpSession session, TableDataInput input) {
		TableData<Supplier> data = null;
		try {
			List<Supplier> list = rfqMeetingService.getEventSuppliersWithIdAndName(eventId, input, getEventType());

			if (CollectionUtil.isNotEmpty(list)) {
				if (StringUtils.checkString(meetingId).length() > 0) {
					List<String> meetingSupplier = rfqMeetingService.getMeetingSupplierIds(meetingId, getEventType());
					if (CollectionUtil.isNotEmpty(meetingSupplier)) {
						for (String supplierId : meetingSupplier) {
							for (Supplier supplier : list) {

								if (supplierId.equals(supplier.getId())) {
									supplier.setInMeeting(Boolean.TRUE);
									break;
								}
							}
						}
					}
				}
			}
			if (Boolean.TRUE.equals(checkedAll)) {
				for (Supplier supplier : list) {
					supplier.setInMeeting(Boolean.TRUE);
				}
			} else {
				if (selectedList != null) {
					for (String selectedSupplierId : selectedList.split(",")) {
						for (Supplier supplier : list) {
							if (selectedSupplierId.equals(supplier.getId())) {
								supplier.setInMeeting(Boolean.TRUE);
								break;
							}
						}
					}
				}
				if (unSelectedList != null) {
					for (String unSelectedSupplierId : unSelectedList.split(",")) {
						for (Supplier supplier : list) {
							if (unSelectedSupplierId.equals(supplier.getId())) {
								supplier.setInMeeting(Boolean.FALSE);
								break;
							}
						}
					}
				}
			}

			data = new TableData<Supplier>(list);
			data.setDraw(input.getDraw());

			long filterTotalCount = rfqMeetingService.getEventSuppliersFilterCount(eventId, input, getEventType());
			long totalCount = rfqMeetingService.getEventSuppliersCountWithIdAndName(eventId, getEventType());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(filterTotalCount);

		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Supplier>>(data, HttpStatus.OK);
	}

}