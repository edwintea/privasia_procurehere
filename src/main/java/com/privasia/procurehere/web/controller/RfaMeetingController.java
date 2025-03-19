package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;
import javax.validation.Validator;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingContact;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventMeetingReminder;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventDocumentPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.RfaEventEditor;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaMeetingController extends EventMeetingBase {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unused")
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Resource
	MessageSource messageSource;

	@Autowired
	RfaEventEditor rfaEventEditor;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfaEventService rfaEventService;

	public RfaMeetingController() {
		super(RfxTypes.RFA);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Date.class, "appointmentDateTime", new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(Date.class, "appointmentTime", new CustomDateEditor(new SimpleDateFormat("hh:mm a"), true));
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

	@RequestMapping(path = "/meetingList/{eventId}", method = RequestMethod.GET)
	public ModelAndView rfaMeetingList(@PathVariable String eventId, Model model) {
		LOG.info(" rfa Meeting List called");
		if (eventId == null) {
			return new ModelAndView("redirect:/400_error");
		}
		List<RfaEventMeeting> meetingList = rfaMeetingService.getAllRfaMeetingByEventId(eventId);
		model.addAttribute("meetingList", meetingList);
		model.addAttribute("eventId", eventId);
		RfaEvent rftEventObj = rfaEventService.getRfaEventByeventId(eventId);
		model.addAttribute("event", rftEventObj);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		return new ModelAndView("meetingList");
	}

	@RequestMapping(path = "/meetingList", method = RequestMethod.POST)
	public ModelAndView meetingList(Model model, @RequestParam String eventId, @RequestParam("source") String source, RedirectAttributes redir, HttpSession session) {
		String view = "";
		if (source.equalsIgnoreCase("summary")) {

			view = "redirect:eventSummary/" + eventId;
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "redirect:meetingList/" + eventId;
		}
		LOG.info(" meetingList post called" + eventId);
		session.removeAttribute("listMeetingDocument");
		model.addAttribute("source", source);
		redir.addFlashAttribute("eventId", eventId);
		return new ModelAndView(view);
	}

	@RequestMapping(value = "/meetingNext", method = RequestMethod.POST)
	public ModelAndView meetingNext(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			rfaEvent.setMeetingCompleted(Boolean.TRUE);
			rfaEventService.updateRfaEvent(rfaEvent);
			if (Boolean.TRUE == rfaEvent.getQuestionnaires()) {
				return new ModelAndView("redirect:eventCqList/" + eventId);
			} else if (Boolean.TRUE == rfaEvent.getBillOfQuantity()) {
				return new ModelAndView("redirect:createBQList/" + eventId);
			} else {
				return new ModelAndView("redirect:eventSummary/" + eventId);
			}
		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/meetingPrevious", method = RequestMethod.POST)
	public String meetingPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			if (rfaEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
				return "redirect:addSupplier/" + eventId;
			} else if (Boolean.TRUE == rfaEvent.getDocumentReq()) {
				return "redirect:createEventDocuments/" + eventId;
			} else {
				return "redirect:auctionRules/" + rfaEvent.getId();
			}
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/viewMeeting", method = RequestMethod.POST)
	public ModelAndView viewMeeting(@RequestParam String meetingId) {
		LOG.info(" view Meeting post called meetingId : " + meetingId);
		return super.showMeeting(meetingId);
	}

	@RequestMapping(path = "/viewMeeting/{meetingId}", method = RequestMethod.GET)
	public ModelAndView viewRfaMeeting(@PathVariable String meetingId, Model model) {
		LOG.info("view Rfa Meeting GET called");
		if (StringUtils.checkString(meetingId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfaEventMeeting rfaEventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
		// rfaEventMeeting.setRfxEventId(rfaEventMeeting.getRfxEvent().getId());// TODO: may be problem occure
		model.addAttribute("eventMeeting", rfaEventMeeting);
		model.addAttribute("listSupplier", rfaEventMeeting.getInviteSuppliers());
		// model.addAttribute("eventId", rfaEventMeeting.getRfxEvent().getId());
		model.addAttribute("event", rfaEventService.getRfaEventByeventId(rfaEventMeeting.getRfxEvent().getId()));
		return new ModelAndView("viewMeeting");
	}

	@RequestMapping(path = "/createMeeting", method = RequestMethod.GET)
	public ModelAndView createRfaMeeting(Model model, @RequestParam("source") String source, @RequestParam(required = false) String eventId, HttpSession session) {
		LOG.info(" Create Rfa Meeting called");
		String view = "";
		if (source.equalsIgnoreCase("summary")) {
			view = "summaryEventMeetingEdit";
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "createMeeting";
		}
		// if (StringUtils.checkString(eventId).length() == 0) {
		// return new ModelAndView("redirect:/400_error");
		// }
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfaEventMeeting eventMeeting = new RfaEventMeeting();
		RfaEvent event = new RfaEvent();
		event.setId(eventId);
		eventMeeting.setRfxEvent(event);
		model.addAttribute("meetingType", MeetingType.values());
		// model.addAttribute("eventId", eventId);
		session.removeAttribute("listMeetingDocument");
		RfaEvent rfaEventObj = rfaEventService.getRfaEventByeventId(eventId);
		model.addAttribute("event", rfaEventObj);
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("listSuppliers", rfaMeetingService.getEventSuppliers(eventId));
		model.addAttribute("contactObject", new RfaEventMeetingContact());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("eventMeeting", eventMeeting);
		return new ModelAndView(view);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/createMeeting", method = RequestMethod.POST)
	public ModelAndView saveRfaMeeting(@ModelAttribute RfaEventMeeting rfaEventMeeting, @RequestParam("source") String source, @RequestParam(value = "contactId[]") String[] contactId, @RequestParam(value = "contactName[]") String[] contactName, @RequestParam(value = "contactEmail[]") String[] contactEmail, @RequestParam(value = "contactNumber[]") String[] contactNumber, @RequestParam(value = "reminderDateAndTime[]", required = false) String[] reminderDateAndTime, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, BindingResult result, RedirectAttributes redir, HttpSession session, @RequestParam(value = "selectAllSupplier", required = false) Boolean selectAllSupplier) {
		String errorView = "";
		String successView = "";
		if (source.equalsIgnoreCase("summary")) {
			errorView = "summaryEventMeetingEdit";
			successView = "redirect:eventSummary/" + rfaEventMeeting.getRfxEvent().getId();
		} else if (source.equalsIgnoreCase("meeting")) {
			errorView = "createMeeting";
			successView = "redirect:meetingList/" + rfaEventMeeting.getRfxEvent().getId();
		}
		String eventId = rfaEventMeeting.getRfxEvent().getId();
		/**
		 * Put all attributes into model so that in case of error, these get re-displayed.
		 */
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat eventmeetingAppDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
		eventmeetingAppDate.setTimeZone(timeZone);

		List<RfaEventMeetingContact> meetingContacts = reConstructContactList(rfaEventMeeting, contactId, contactName, contactEmail, contactNumber);
		rfaEventMeeting.setRfxEventMeetingContacts(meetingContacts);
		if (reminderDateAndTime != null && reminderDateAndTime.length > 0) {
			LOG.info("event Reminder for meeting : ");
			rfaEventMeeting.setRfxEventMeetingReminder(constructReminderList(rfaEventMeeting, reminderDuration, reminderDurationType, eventId, model, session));
		}
		if (StringUtils.checkString(rfaEventMeeting.getId()).length() != 0) {
			List<RfaEventMeetingReminder> reminderList = rfaMeetingService.getAllRfaEventMeetingReminderForMeeting(rfaEventMeeting.getId());
			LOG.info("masdasda asdasdas asd " + reminderList.size());
			model.addAttribute("reminderList", reminderList);
		}
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventMeeting", rfaEventMeeting);
		model.addAttribute("eventId", eventId);
		model.addAttribute("source", source);
		model.addAttribute("rftMeetingId", rfaEventMeeting.getId());
		model.addAttribute("listSuppliers", rfaMeetingService.getEventSuppliers(eventId));
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				errMessages.add(error.getDefaultMessage());
			}
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
			return new ModelAndView(errorView);
		} else {
			try {
				if (validateMeeting(rfaEventMeeting, model, eventId)) {
					List<RfaEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(rfaEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfaEventMeetingDocument eventMeetingDocument = new RfaEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfaEventMeeting);

				}
				Date dateTime = DateUtil.combineDateTime(rfaEventMeeting.getAppointmentDateTime(), rfaEventMeeting.getAppointmentTime(), timeZone);
				rfaEventMeeting.setAppointmentDateTime(dateTime);
				RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);

				if (rfaEventMeeting.getAppointmentDateTime().before(new Date())) {
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishpastdate", new Object[] { eventmeetingAppDate.format(rfaEventMeeting.getAppointmentDateTime()) }, Global.LOCALE));
					model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
					List<RfaEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(rfaEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfaEventMeetingDocument eventMeetingDocument = new RfaEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfaEventMeeting);
				}
				if (rfaEventMeeting.getAppointmentDateTime().before(event.getEventPublishDate())) {
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { eventmeetingAppDate.format(rfaEventMeeting.getAppointmentDateTime()), eventmeetingAppDate.format(event.getEventPublishDate()) }, Global.LOCALE));
					model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
					List<RfaEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(rfaEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfaEventMeetingDocument eventMeetingDocument = new RfaEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfaEventMeeting);
				}
				if (event.getEventEnd().before(rfaEventMeeting.getAppointmentDateTime())) {
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventenddate", new Object[] { eventmeetingAppDate.format(rfaEventMeeting.getAppointmentDateTime()), eventmeetingAppDate.format(event.getEventEnd()) }, Global.LOCALE));
					model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
					List<RfaEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfaEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(rfaEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfaEventMeetingDocument eventMeetingDocument = new RfaEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfaEventMeeting);
				}
				// Attach the documents
				List<RfaEventMeetingDocument> list = new ArrayList<RfaEventMeetingDocument>();
				if (session != null) {
					List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
						for (EventDocumentPojo document : oldListMeetingDocument) {
							if (StringUtils.checkString(document.getId()).length() == 0) {
								RfaEventMeetingDocument doc = new RfaEventMeetingDocument();
								doc.setCredContentType(document.getCredContentType());
								doc.setFileData(document.getFileData());
								doc.setFileName(document.getFileName());
								doc.setFileSizeInKb(document.getFileSize());
								doc.setTenantId(document.getTenantId());
								doc.setRfaEventMeeting(rfaEventMeeting);
								// LOG.info("DOCS : " + doc.toString());
								list.add(doc);
							}
						}
						// rfaEventMeeting.setRfxEventMeetingDocument(list);
					}
				}

				if (StringUtils.checkString(rfaEventMeeting.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.save.success", new Object[] {}, Global.LOCALE));
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.update.success", new Object[] {}, Global.LOCALE));
				}

				rfaEventMeeting = rfaMeetingService.saveRfaMeeting(rfaEventMeeting, list, selectAllSupplier, eventId, event.getEventVisibility());
 				
				session.removeAttribute("listMeetingDocument");
				try {
					rfaEventService.insertTimeLine(event.getId());
				} catch (Exception e) {
					LOG.error("Error : " + e.getMessage(), e);
				}
				redir.addFlashAttribute("rfaMeetingId", rfaEventMeeting.getId());
				redir.addFlashAttribute("eventId", rfaEventMeeting.getRfxEvent().getId());
				redir.addFlashAttribute("eventMeeting", rfaEventMeeting);
				/*
				 * try{ rfaEventService.insertTimeLine(event.getId()); }catch(Exception e){ LOG.error("Error : " +
				 * e.getMessage(), e); }
				 */
			} catch (ApplicationException e) {
				if (StringUtils.checkString(rfaEventMeeting.getId()).length() == 0) {
					model.addAttribute("btnValue", "Create");
				} else {
					model.addAttribute("btnValue", "Update");
				}
				model.addAttribute("error", e.getMessage());
				model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
				List<RfaEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(rfaEventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(rfaEventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfaEventMeetingDocument eventMeetingDocument = new RfaEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
							meetingDocumentList.add(eventMeetingDocument);
						}
					}
				}
				model.addAttribute("listMeetingDocument", meetingDocumentList);
				return new ModelAndView(errorView, "eventMeeting", rfaEventMeeting);
			} catch (Exception e) {
				LOG.error("Error while storing rfaMeeting : " + e.getMessage(), e);
				model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", messageSource.getMessage("rft.meeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
				List<RfaEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(rfaEventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(rfaEventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfaEventMeetingDocument eventMeetingDocument = new RfaEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
							meetingDocumentList.add(eventMeetingDocument);
						}
					}
				}
				model.addAttribute("listMeetingDocument", meetingDocumentList);
				return new ModelAndView(errorView);
			}
		}
		return new ModelAndView(successView);
	}

	// private List<RfaEventMeetingReminder> constructReminderList(RfaEventMeeting rfaEventMeeting, String[]
	// reminderDate, String eventId) {
	// List<RfaEventMeetingReminder> meetingReminders = new ArrayList<RfaEventMeetingReminder>();
	// try {
	// for (int idx = 0; idx < reminderDate.length; idx++) {
	// SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
	// Date remindDate = null;
	// remindDate = formatter.parse(reminderDate[idx]);
	// RfaEventMeetingReminder reminder = new RfaEventMeetingReminder(remindDate);
	// reminder.setRfaEventMeeting(rfaEventMeeting);
	// RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
	// reminder.setRfaEvent(rfaEvent);
	// meetingReminders.add(reminder);
	// }
	// } catch (Exception e) {
	// LOG.info("Error while Storing reminderDate " + e);
	// }
	// return meetingReminders;
	// }

	private List<RfaEventMeetingReminder> constructReminderList(RfaEventMeeting rfaEventMeeting, Integer[] reminderDuration, IntervalType[] reminderDurationType, String eventId, Model model, HttpSession session) {
		List<RfaEventMeetingReminder> meetingRemindersList = new ArrayList<RfaEventMeetingReminder>();
		try {
			RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null)
				for (Integer reminderDurationValue : reminderDuration) {
					RfaEventMeetingReminder meetingReminder = new RfaEventMeetingReminder();
					Date meetingDateAndTime = DateUtil.combineDateTime(rfaEventMeeting.getAppointmentDateTime(), rfaEventMeeting.getAppointmentTime(), timeZone);
					Calendar cal = Calendar.getInstance(timeZone);
					cal.setTime(meetingDateAndTime);
					meetingReminder.setInterval(reminderDurationValue);
					meetingReminder.setIntervalType(reminderDurationType[index]);
					switch (reminderDurationType[index]) {
					case DAYS:
						cal.add(Calendar.DATE, -reminderDurationValue);
						break;
					case HOURS:
						cal.add(Calendar.HOUR, -reminderDurationValue);
						break;
					}
					meetingReminder.setRfaEventMeeting(rfaEventMeeting);
					meetingReminder.setReminderDate(cal.getTime());
					meetingReminder.setRfaEvent(event);
					meetingRemindersList.add(meetingReminder);

					if (meetingReminder.getReminderDate().after(meetingDateAndTime)) {
						// model.addAttribute("error", "Reminder date should not be less than Meeting date date");
						model.addAttribute("error", messageSource.getMessage("reminder.not.lessthan.meetingdate", new Object[] {}, Global.LOCALE));
						new NotAllowedException("Reminder date should not be less than Meeting date date. [" + meetingReminder.getReminderDate() + "]");
					}
					index++;
				}
		} catch (Exception e) {
			LOG.info("Error while Storing reminderDate " + e);
			throw e;
		}
		return meetingRemindersList;
	}

	private List<RfaEventMeetingContact> reConstructContactList(RfaEventMeeting rfaEventMeeting, String[] contactId, String[] contactName, String[] contactEmail, String[] contactNumber) {
		List<RfaEventMeetingContact> meetingContacts = new ArrayList<RfaEventMeetingContact>();
		LOG.info("contactName.length " + contactName.length);
		for (int idx = 0; idx < contactName.length; idx++) {
			RfaEventMeetingContact contact = new RfaEventMeetingContact(((contactId != null && contactId.length > 0) ? contactId[idx] : null), contactName[idx], contactEmail[idx], contactNumber[idx]);
			contact.setRfaEventMeeting(rfaEventMeeting);
			meetingContacts.add(contact);
		}
		return meetingContacts;
	}

	@RequestMapping(path = "/editMeeting", method = RequestMethod.GET)
	public ModelAndView editMeetings(@RequestParam String meetingId, @RequestParam("source") String source, Model model, HttpSession session) {
		LOG.info("edit meeting called meetingId. : " + meetingId);
		String view = "";
		if (source.equalsIgnoreCase("summary")) {
			view = "summaryEventMeetingEdit";
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "createMeeting";
		}
		RfaEventMeeting rfaEventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
		String eventId = rfaEventMeeting.getRfxEvent().getId();
		List<EventDocumentPojo> pojos = new ArrayList<EventDocumentPojo>();
		List<RfaEventMeetingDocument> meetingDocumentList = rfaMeetingService.getPlainMeetingDocument(meetingId);
		if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
			for (EventMeetingDocument document : meetingDocumentList) {
				EventDocumentPojo pojo = new EventDocumentPojo(document);
				pojos.add(pojo);
			}
		}
		session.setAttribute("listMeetingDocument", pojos);
		model.addAttribute("listMeetingDocument", meetingDocumentList);

		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		List<RfaEventMeetingReminder> reminderList = rfaMeetingService.getAllRfaEventMeetingReminderForMeeting(meetingId);
		LOG.info("masdasda asdasdas asd " + reminderList.size());
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("source", source);
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("event", rfaEventService.getRfaEventByeventId(rfaEventMeeting.getRfxEvent().getId()));
		model.addAttribute("listSuppliers", rfaMeetingService.getEventSuppliers(eventId));
		model.addAttribute("eventId", eventId);
		model.addAttribute("btnValue", "Update");
		return new ModelAndView(view, "eventMeeting", rfaEventMeeting);
	}

	@RequestMapping(value = "/removeInviteSupplier", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Supplier>> removeInviteSupplier(@RequestParam("removeSupId") String removeSupId, @RequestParam("meetingId") String meetingId) {
		LOG.info("removeSupId  :" + removeSupId + " meetingId :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeSupId).length() > 0) {
				Integer countSupplierForMeeting = rfaMeetingService.getCountOfSupplierForMeeting(meetingId);
				LOG.info("count " + countSupplierForMeeting);
				if (countSupplierForMeeting > 1) {
					Supplier selectedSupplier = supplierService.findSuppById(removeSupId);
					RfaEventMeeting rfaEventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
					if (CollectionUtil.isNotEmpty(rfaEventMeeting.getInviteSuppliers())) {
						if (rfaEventMeeting.getInviteSuppliers().contains(selectedSupplier)) {
							rfaEventMeeting.getInviteSuppliers().remove(selectedSupplier);
						}
					}
					rfaMeetingService.updateRfaMeeting(rfaEventMeeting);
					rfaEventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
					List<Supplier> listSuppliers = rfaEventMeeting.getInviteSuppliers();
					LOG.error("Invite Supplier removed Successfully... listSuppliers Size :" + listSuppliers.size());
					headers.add("success", messageSource.getMessage("rft.meeting.remove.InviteSupplier.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<Supplier>>(listSuppliers, headers, HttpStatus.OK);
				} else {
					LOG.info("count  less  then 1 " + messageSource.getMessage("rft.meeting.remove.minInviteSupplier.error", new Object[] {}, Global.LOCALE));
					headers.add("error", messageSource.getMessage("rft.meeting.remove.minInviteSupplier.error", new Object[] {}, "error", Global.LOCALE));
					return new ResponseEntity<List<Supplier>>(null, headers, HttpStatus.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while removing supplier : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.meeting.remove.InviteSupplier.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Supplier>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<List<Supplier>>(null, headers, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/downloadMeetingDocument", method = RequestMethod.GET)
	public void downloadMeetingDocument(@RequestParam String docId, HttpServletResponse response, HttpSession session) throws IOException {
		EventDocumentPojo docs = null;
		List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
		try {
			if (!docId.equals("undefined")) {
				LOG.info("RFA Event Meeting Download  :: :: " + docId);
				EventMeetingDocument doc = rfaMeetingService.getMeetingDocumentForDelete(docId);
				docs = new EventDocumentPojo(doc);
				super.setResponseForDownload(response, docs);
			} else if (!docId.equals("undefined") && CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
				for (EventDocumentPojo document : oldListMeetingDocument) {
					LOG.info("UI REF ID : " + docId + " SESSION REF ID : " + document.getRefId());
					if (document.getRefId() != null && document.getRefId().equals(docId)) {
						docs = document;
						break;
					}
				}
				super.setResponseForDownload(response, docs);
			}

		} catch (Exception e) {
			LOG.error("Error while downloaded RFT Event Meeting Document : " + e.getMessage(), e);
		}

	}

	@RequestMapping(value = "/meetingSaveDraft", method = RequestMethod.POST)
	public String meetingSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
		return "redirect:meetingList/" + rfaEvent.getId();
	}

	@RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> cancelMeeting(RedirectAttributes redir, @RequestParam("meetingId") String meetingId, @RequestParam(name = "cancelReason", required = true) String cancelReason) {
		LOG.info("cancel meeting called meeting Id :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		RfaEventMeeting rfaEventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
		try {
			if (rfaEventMeeting.getStatus() == MeetingStatus.CANCELLED) {
				headers.add("error", messageSource.getMessage("rft.meeting.cancel.error", new Object[] { rfaEventMeeting.getTitle() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			rfaEventMeeting.setCancelReason(cancelReason);
			rfaEventMeeting.setStatus(MeetingStatus.CANCELLED);
			rfaEventMeeting.setModifiedBy(SecurityLibrary.getLoggedInUser());
			rfaEventMeeting.setModifiedDate(new Date());
			rfaMeetingService.cancelMeeting(rfaEventMeeting);
			headers.add("success", messageSource.getMessage("rft.meeting.cancel.success", new Object[] { rfaEventMeeting.getTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while cancel meeting : " + e.getMessage(), e);
		}
		LOG.info("meeting cancelled successfully");
		return new ResponseEntity<String>("CANCELLED", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<RfaEventMeetingReminder> addReminderOfEventMeeting(@RequestParam(value = "reminderDuration") Integer reminderDuration, @RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "reminderDateList[]", required = false) String[] reminderDateList, @RequestParam(value = "reminderId") String reminderId, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder in meetings : ");
		HttpHeaders headers = new HttpHeaders();
		RfaEventMeetingReminder eventMeetingReminder = new RfaEventMeetingReminder();
		eventMeetingReminder.setInterval(reminderDuration);
		eventMeetingReminder.setIntervalType(reminderDurationType);
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			LOG.info("evennt in reminder save :  ");
			if (StringUtils.checkString(reminderId).length() == 0) {
				if (StringUtils.checkString(meetingDate).length() > 0) {
					String fullDateStr = meetingDate + " " + meetingTime;
					DateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					dateTimeFormatter.setTimeZone(timeZone);
					Date meetingOnDate = dateTimeFormatter.parse(fullDateStr);
					Calendar cal = Calendar.getInstance(timeZone);
					cal.setTime(meetingOnDate);
					if (reminderDurationType == IntervalType.DAYS) {
						cal.add(Calendar.DATE, -reminderDuration);
						LOG.info("Reminder : " + dateTimeFormatter.format(cal.getTime()));
					} else {
						cal.add(Calendar.HOUR, -reminderDuration);
						LOG.info("Reminder  Hous: " + dateTimeFormatter.format(cal.getTime()));
					}
					if ((cal.getTime()).compareTo(new Date()) < 0) {
						headers.add("error", "Reminder cannot set on past dates");
						return new ResponseEntity<RfaEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}

					if (reminderDateList != null && reminderDateList.length > 0) {
						LOG.info("here to check the data : ");

						for (String reminderDateData : reminderDateList) {
							LOG.info("here to check the data  new : " + reminderDateData);
							Date oldReminderDate = (Date) dateTimeFormatter.parse(reminderDateData);
							LOG.info("here to check the data  if old : " + oldReminderDate + " " + cal.getTime());
							if (oldReminderDate.compareTo(cal.getTime()) == 0) {
								LOG.info("here to check the data  if error : ");
								headers.add("error", "Reminder already exists");
								return new ResponseEntity<RfaEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
					}
					eventMeetingReminder.setReminderDate(cal.getTime());
					LOG.info("reminder time : " + eventMeetingReminder.getReminderDate());
					model.addAttribute("reminder", eventMeetingReminder.getReminderDate());
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Reminder for meeting" + e.getMessage(), e);
			headers.add("error", "Error While Save the Reminder for meeting");
			return new ResponseEntity<RfaEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("success", "Reminder added successfully");
		return new ResponseEntity<RfaEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<List<RfaEventMeetingReminder>> manageReminderOfEventMeeting(@RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "eventId", required = false) String eventId, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, HttpSession session) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		List<RfaEventMeetingReminder> meetingRemindersList = new ArrayList<RfaEventMeetingReminder>();
		try {

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null) {
				for (Integer reminderDurationValue : reminderDuration) {
					RfaEventMeetingReminder meetingReminder = new RfaEventMeetingReminder();
					String fullDateStr = meetingDate + " " + meetingTime;
					DateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					dateTimeFormatter.setTimeZone(timeZone);
					Date meetingOnDate = dateTimeFormatter.parse(fullDateStr);
					Calendar cal = Calendar.getInstance(timeZone);
					cal.setTime(meetingOnDate);
					meetingReminder.setIntervalType(reminderDurationType[index]);
					meetingReminder.setInterval(reminderDurationValue);
					switch (reminderDurationType[index]) {
					case DAYS:
						cal.add(Calendar.DATE, -reminderDurationValue);
						break;
					case HOURS:
						cal.add(Calendar.HOUR, -reminderDurationValue);
						break;
					}

					if (cal.getTime().compareTo(new Date()) < 0) {
						headers.add("error", "Reminder cannot set on past dates");
						return new ResponseEntity<List<RfaEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					meetingReminder.setReminderDate(cal.getTime());
					meetingRemindersList.add(meetingReminder);
					index++;
				}
			}
		} catch (Exception e) {
			LOG.info("Error while Storing reminderDate " + e.getMessage(), e);
			headers.add("error", "Error while Storing reminderDate ");
		}
		headers.add("success", "reminderDate Computed");
		return new ResponseEntity<List<RfaEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteMeeting", method = RequestMethod.GET)
	public String deleteMeeting(RedirectAttributes redir, @RequestParam("meetingId") String meetingId) {
		String eventId = null;
		try {
			eventId = super.deleteMeeting(meetingId);
			redir.addFlashAttribute("success", messageSource.getMessage("common.meeting.success", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While Delete Meeting :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("common.delete.meeting.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/RFA/meetingList/" + eventId;
	}
}