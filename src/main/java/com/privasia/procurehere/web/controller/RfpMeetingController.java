package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventMeetingContact;
import com.privasia.procurehere.core.entity.RfpEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEventMeetingReminder;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventDocumentPojo;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpMeetingService;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpMeetingController extends EventMeetingBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfpEventService rfpEventService;

	public RfpMeetingController() {
		super(RfxTypes.RFP);
	}

	@RequestMapping(path = "/meetingList/{eventId}", method = RequestMethod.GET)
	public ModelAndView rfpMeetingList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		List<RfpEventMeeting> meetingList = rfpMeetingService.getAllRfpMeetingByEventId(eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("meetingList", meetingList);
		model.addAttribute("eventId", eventId);
		RfpEvent eventObj = rfpEventService.getRfpEventByeventId(eventId);
		model.addAttribute("event", eventObj);
		return new ModelAndView("meetingList");
	}

	@RequestMapping(path = "/meetingList", method = RequestMethod.POST)
	public ModelAndView meetingList(Model model, @RequestParam("source") String source, @RequestParam String eventId, HttpSession session) {
		String view = "";
		if (source.equalsIgnoreCase("summary")) {

			view = "redirect:eventSummary/" + eventId;
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "redirect:meetingList/" + eventId;
		}
		RfpEvent event = rfpEventService.getEventById(eventId);
		model.addAttribute("event", event);
		model.addAttribute("source", source);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		session.removeAttribute("listMeetingDocument");
		return new ModelAndView(view);
	}

	@RequestMapping(value = "/meetingNext", method = RequestMethod.POST)
	public ModelAndView meetingNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfpEvent rftEvent = rfpEventService.getEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rftEvent.setMeetingCompleted(Boolean.TRUE);
			rfpEventService.updateEvent(rftEvent);
			return meetingNext(rftEvent);

		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/meetingPrevious", method = RequestMethod.POST)
	public String meetingPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		LOG.info("EVENT ID : " + eventId);
		RfpEvent rftEvent = rfpEventService.getEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return super.meetingPrevious(rftEvent);
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/viewMeeting", method = RequestMethod.POST)
	public ModelAndView viewMeeting(@RequestParam String meetingId) {
		return super.showMeeting(meetingId);
	}

	@RequestMapping(path = "/viewMeeting/{meetingId}", method = RequestMethod.GET)
	public ModelAndView viewRftMeeting(@PathVariable String meetingId, Model model) {
		LOG.info("view Rfp Meeting GET called");
		if (StringUtils.checkString(meetingId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfpEventMeeting eventMeeting = rfpMeetingService.getRfpMeetingById(meetingId);
		model.addAttribute("eventMeeting", eventMeeting);
		model.addAttribute("listSupplier", eventMeeting.getInviteSuppliers());
		model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventMeeting.getRfxEvent().getId()));
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventMeeting.getRfxEvent().getId()));

		return new ModelAndView("viewMeeting");
	}

	@RequestMapping(path = "/createMeeting", method = RequestMethod.GET)
	public ModelAndView createRftMeeting(Model model, @RequestParam("source") String source, @RequestParam(required = false) String eventId, HttpSession session) {
		LOG.info(" Create Rft Meeting called");
		String view = "";
		if (source.equalsIgnoreCase("summary")) {

			view = "summaryEventMeetingEdit";
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "createMeeting";
		}
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}

		RfpEventMeeting eventMeeting = new RfpEventMeeting();
		RfpEvent event = new RfpEvent();
		event.setId(eventId);
		eventMeeting.setRfxEvent(event);
		model.addAttribute("meetingType", MeetingType.values());
		session.removeAttribute("listMeetingDocument");
		RfpEvent rftEventObj = rfpEventService.getEventById(eventId);
		model.addAttribute("event", rftEventObj);
		model.addAttribute("listSuppliers", rfpMeetingService.getEventSuppliers(eventId));
		model.addAttribute("contactObject", new RfpEventMeetingContact());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		model.addAttribute("eventMeeting", eventMeeting);
		model.addAttribute("intervalType", IntervalType.values());
		return new ModelAndView(view);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/createMeeting", method = RequestMethod.POST)
	public ModelAndView saveRftMeeting(@ModelAttribute RfpEventMeeting eventMeeting, @RequestParam("source") String source, @RequestParam(value = "contactId[]") String[] contactId, @RequestParam(value = "contactName[]") String[] contactName, @RequestParam(value = "contactEmail[]") String[] contactEmail, @RequestParam(value = "contactNumber[]") String[] contactNumber, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, BindingResult result, RedirectAttributes redir, HttpSession session, @RequestParam(value = "selectAllSupplier", required = false) Boolean selectAllSupplier) {
		LOG.info("save Rft Meeting Called " + eventMeeting.getRfxEvent().getId());
		String errorView = "";
		String successView = "";
		if (source.equalsIgnoreCase("summary")) {
			errorView = "summaryEventMeetingEdit";
			successView = "redirect:eventSummary/" + eventMeeting.getRfxEvent().getId();
		} else if (source.equalsIgnoreCase("meeting")) {
			errorView = "createMeeting";
			successView = "redirect:meetingList/" + eventMeeting.getRfxEvent().getId();
		}

		String eventId = eventMeeting.getRfxEvent().getId();
		/**
		 * Put all attributes into model so that in case of error, these get re-displayed.
		 */
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
		dateTimeFormat.setTimeZone(timeZone);
		List<RfpEventMeetingContact> meetingContacts = reConstructContactList(eventMeeting, contactId, contactName, contactEmail, contactNumber);
		eventMeeting.setRfxEventMeetingContacts(meetingContacts);
		if (reminderDuration != null && reminderDuration.length > 0) {
			eventMeeting.setRfxEventMeetingReminder(constructReminderList(eventMeeting, reminderDuration, reminderDurationType, eventId, model, session));
		}
		if (StringUtils.checkString(eventMeeting.getId()).length() != 0) {
			List<RfpEventMeetingReminder> reminderList = rfpMeetingService.getAllRfpEventMeetingReminderForMeeting(eventMeeting.getId());
			LOG.info("masdasda asdasdas asd " + reminderList.size());
			model.addAttribute("reminderList", reminderList);
		}
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventMeeting", eventMeeting);
		model.addAttribute("source", source);
		model.addAttribute("eventId", eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		model.addAttribute("rftMeetingId", eventMeeting.getId());
		model.addAttribute("listSuppliers", rfpMeetingService.getEventSuppliers(eventId));
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				errMessages.add(error.getDefaultMessage());
			}
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
			return new ModelAndView(errorView);
		} else {
			try {
				if (validateMeeting(eventMeeting, model, eventId)) {
					List<RfpEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfpEventMeetingDocument eventMeetingDocument = new RfpEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", eventMeeting);
				}
				Date dateTime = DateUtil.combineDateTime(eventMeeting.getAppointmentDateTime(), eventMeeting.getAppointmentTime(), timeZone);
				eventMeeting.setAppointmentDateTime(dateTime);
				RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);

				if (eventMeeting.getAppointmentDateTime().before(new Date())) {
					if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishpastdate", new Object[] { dateTimeFormat.format(eventMeeting.getAppointmentDateTime()) }, Global.LOCALE));
					model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
					List<RfpEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfpEventMeetingDocument eventMeetingDocument = new RfpEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", eventMeeting);
				}
				if (eventMeeting.getAppointmentDateTime().before(event.getEventPublishDate())) {
					if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeFormat.format(eventMeeting.getAppointmentDateTime()), dateTimeFormat.format(event.getEventPublishDate()) }, Global.LOCALE));
					List<RfpEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfpEventMeetingDocument eventMeetingDocument = new RfpEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", eventMeeting);
				}
				if (event.getEventEnd().before(eventMeeting.getAppointmentDateTime())) {
					if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventenddate", new Object[] { dateTimeFormat.format(eventMeeting.getAppointmentDateTime()), dateTimeFormat.format(event.getEventEnd()) }, Global.LOCALE));
					List<RfpEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfpEventMeetingDocument eventMeetingDocument = new RfpEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", eventMeeting);
				}
				// Attach the documents
				List<RfpEventMeetingDocument> list = new ArrayList<RfpEventMeetingDocument>();
				if (session != null) {
					List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
						for (EventDocumentPojo document : oldListMeetingDocument) {
							if (StringUtils.checkString(document.getId()).length() == 0) {
								RfpEventMeetingDocument doc = new RfpEventMeetingDocument();
								doc.setCredContentType(document.getCredContentType());
								doc.setFileData(document.getFileData());
								doc.setFileName(document.getFileName());
								doc.setFileSizeInKb(document.getFileSize());
								doc.setTenantId(document.getTenantId());
								doc.setRfxEventMeeting(eventMeeting);
								list.add(doc);
								// LOG.info("Doc : "+ doc);
							}
						}
						// eventMeeting.setRfxEventMeetingDocument(list);
					}
				}

				if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.save.success", new Object[] {}, Global.LOCALE));
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.update.success", new Object[] {}, Global.LOCALE));
				}
				eventMeeting = rfpMeetingService.saveRfpMeeting(eventMeeting, list, selectAllSupplier, eventId, event.getEventVisibility());

				if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
					if (tatReport != null && tatReport.getEventFirstMeetingDate() == null) {
						tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), eventMeeting.getAppointmentDateTime());
					}
				}

				session.removeAttribute("listMeetingDocument");
				LOG.info("EVENT ID : " + eventMeeting.getRfxEvent().getId());
				try {
					rfpEventService.insertTimeLine(eventId);
				} catch (Exception e) {
					LOG.error("Error : " + e.getMessage(), e);
				}
			} catch (ApplicationException e) {

				if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
					model.addAttribute("btnValue", "Create");
				} else {
					model.addAttribute("btnValue", "Update");
				}
				model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
				model.addAttribute("error", e.getMessage());
				List<RfpEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(eventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfpEventMeetingDocument eventMeetingDocument = new RfpEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
							meetingDocumentList.add(eventMeetingDocument);
						}
					}
				}
				model.addAttribute("listMeetingDocument", meetingDocumentList);
				return new ModelAndView(errorView, "eventMeeting", eventMeeting);

			} catch (Exception e) {
				LOG.error("Error while storing rftMeeting : " + e.getMessage(), e);
				model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", messageSource.getMessage("rft.meeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
				List<RfpEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(eventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfpEventMeetingDocument eventMeetingDocument = new RfpEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
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

	private List<RfpEventMeetingReminder> constructReminderList(RfpEventMeeting rfpEventMeeting, Integer[] reminderDuration, IntervalType[] reminderDurationType, String eventId, Model model, HttpSession session) {
		List<RfpEventMeetingReminder> meetingRemindersList = new ArrayList<RfpEventMeetingReminder>();
		try {
			RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null)
				for (Integer reminderDurationValue : reminderDuration) {
					RfpEventMeetingReminder meetingReminder = new RfpEventMeetingReminder();
					Date meetingDateAndTime = DateUtil.combineDateTime(rfpEventMeeting.getAppointmentDateTime(), rfpEventMeeting.getAppointmentTime(), timeZone);
					Calendar cal = Calendar.getInstance();
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
					meetingReminder.setRfxEventMeeting(rfpEventMeeting);
					meetingReminder.setReminderDate(cal.getTime());
					meetingReminder.setRfxEvent(event);
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

	private List<RfpEventMeetingContact> reConstructContactList(RfpEventMeeting rftEventMeeting, String[] contactId, String[] contactName, String[] contactEmail, String[] contactNumber) {
		List<RfpEventMeetingContact> meetingContacts = new ArrayList<RfpEventMeetingContact>();
		LOG.info("contactName.length " + contactName.length);
		for (int idx = 0; idx < contactName.length; idx++) {
			RfpEventMeetingContact contact = new RfpEventMeetingContact(((contactId != null && contactId.length > 0) ? contactId[idx] : null), contactName[idx], contactEmail[idx], contactNumber[idx]);
			contact.setRfxEventMeeting(rftEventMeeting);
			meetingContacts.add(contact);
		}
		return meetingContacts;
	}

	@RequestMapping(path = "/editMeeting", method = RequestMethod.GET)
	public ModelAndView editMeetings(@RequestParam String meetingId, @RequestParam("source") String source, Model model, HttpSession session) {
		String view = "";

		if (source.equalsIgnoreCase("summary")) {
			view = "summaryEventMeetingEdit";
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "createMeeting";
		}
		LOG.info("edit meeting called meetingId. : " + meetingId);
		RfpEventMeeting eventMeeting = rfpMeetingService.getRfpMeetingById(meetingId);
		String eventId = eventMeeting.getRfxEvent().getId();
		List<EventDocumentPojo> pojos = new ArrayList<EventDocumentPojo>();
		List<RfpEventMeetingDocument> meetingDocumentList = rfpMeetingService.getPlainMeetingDocument(meetingId);
		if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
			for (EventMeetingDocument document : meetingDocumentList) {
				EventDocumentPojo pojo = new EventDocumentPojo(document);
				pojos.add(pojo);
			}
		}
		session.setAttribute("listMeetingDocument", pojos);
		model.addAttribute("listMeetingDocument", meetingDocumentList);
		List<RfpEventMeetingReminder> reminderList = rfpMeetingService.getAllRfpEventMeetingReminderForMeeting(meetingId);
		LOG.info("masdasda asdasdas asd " + reminderList.size());
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("source", source);
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		model.addAttribute("listSuppliers", rfpMeetingService.getEventSuppliers(eventId));
		model.addAttribute("event", rfpEventService.getRfpEventByeventId(eventId));
		model.addAttribute("btnValue", "Update");
		return new ModelAndView(view, "eventMeeting", eventMeeting);

	}

	@RequestMapping(value = "/removeInviteSupplier", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Supplier>> removeInviteSupplier(@RequestParam("removeSupId") String removeSupId, @RequestParam("meetingId") String meetingId) {
		LOG.info("removeSupId  :" + removeSupId + " meetingId :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeSupId).length() > 0) {
				Integer countSupplierForMeeting = rfpMeetingService.getCountOfSupplierForMeeting(meetingId);
				LOG.info("count " + countSupplierForMeeting);
				if (countSupplierForMeeting > 1) {
					List<Supplier> suppliers = rfpMeetingService.removeSupplier(meetingId, removeSupId);
					for (Supplier supplier : suppliers) {
						supplier.setCountries(null);
						supplier.setRegistrationOfCountry(null);
					}
					LOG.error("Invite Supplier removed Successfully... listSuppliers Size :" + suppliers.size());
					headers.add("success", messageSource.getMessage("rft.meeting.remove.InviteSupplier.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<Supplier>>(suppliers, headers, HttpStatus.OK);
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
	public void downloadMeetingDocument(@RequestParam String docId, @RequestParam String docRefId, HttpServletResponse response, HttpSession session) throws IOException {
		EventDocumentPojo docs = null;
		try {
			List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
			LOG.info("RFT Event Meeting Download  :: :: " + docId + "::::::");
			if (!docId.equals("undefined")) {
				LOG.info("RFT Event Meeting Download  :: :: " + docId);
				EventMeetingDocument doc = rfpMeetingService.getMeetingDocumentForDelete(docId);
				docs = new EventDocumentPojo(doc);
				super.setResponseForDownload(response, docs);
			} else if (!docRefId.equals("undefined") && CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
				for (EventDocumentPojo document : oldListMeetingDocument) {
					if (document.getRefId() != null && document.getRefId().equals(docRefId)) {
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
	public String meetingSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfpEvent rftEvent = rfpMeetingService.getRfpEventById(eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:meetingList/" + rftEvent.getId();
	}

	@RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> cancelMeeting(RedirectAttributes redir, @RequestParam("meetingId") String meetingId, @RequestParam(name = "cancelReason", required = true) String cancelReason) {
		LOG.info("cancel meeting called meeting Id :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			RfpEventMeeting rftEventMeeting = rfpMeetingService.getRfpMeetingById(meetingId);
			if (rftEventMeeting.getStatus() == MeetingStatus.CANCELLED) {
				headers.add("error", messageSource.getMessage("rft.meeting.already.cancel", new Object[] { rftEventMeeting.getTitle() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			rftEventMeeting.setCancelReason(cancelReason);
			rfpMeetingService.cancelMeeting(rftEventMeeting);
			headers.add("success", messageSource.getMessage("rft.meeting.cancel.success", new Object[] { rftEventMeeting.getTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while cancel meeting : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.meeting.cancel.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
		}
		LOG.info("meeting cancelled successfully");
		return new ResponseEntity<String>("CANCELLED", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<RfpEventMeetingReminder> addReminderOfEventMeeting(@RequestParam(value = "reminderDuration") Integer reminderDuration, @RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "reminderDateList[]", required = false) String[] reminderDateList, @RequestParam(value = "reminderId") String reminderId, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder in meetings : ");
		HttpHeaders headers = new HttpHeaders();
		RfpEventMeetingReminder eventMeetingReminder = new RfpEventMeetingReminder();
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
						return new ResponseEntity<RfpEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
								return new ResponseEntity<RfpEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
					}
					eventMeetingReminder.setReminderDate(cal.getTime());
					model.addAttribute("reminder", eventMeetingReminder.getReminderDate());
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Reminder for meeting" + e.getMessage(), e);
			headers.add("error", "Error While Save the Reminder for meeting");
			return new ResponseEntity<RfpEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("success", "Reminder added successfully");
		return new ResponseEntity<RfpEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<List<RfpEventMeetingReminder>> manageReminderOfEventMeeting(@RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "eventId", required = false) String eventId, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, HttpSession session) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		List<RfpEventMeetingReminder> meetingRemindersList = new ArrayList<RfpEventMeetingReminder>();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null) {
				for (Integer reminderDurationValue : reminderDuration) {
					RfpEventMeetingReminder meetingReminder = new RfpEventMeetingReminder();
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
						return new ResponseEntity<List<RfpEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
		return new ResponseEntity<List<RfpEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.OK);
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
		return "redirect:/buyer/RFP/meetingList/" + eventId;
	}
}