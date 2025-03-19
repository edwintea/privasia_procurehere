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

import org.springframework.beans.factory.annotation.Autowired;
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
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventMeetingContact;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEventMeetingReminder;
import com.privasia.procurehere.core.entity.Supplier;
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
import com.privasia.procurehere.service.RfqEventService;

@Controller
@RequestMapping("/buyer/RFQ")
public class RfqMeetingController extends EventMeetingBase {

	@Autowired
	RfqEventService rfqEventService;

	public RfqMeetingController() {
		super(RfxTypes.RFQ);
	}

	@RequestMapping(path = "/meetingList/{eventId}", method = RequestMethod.GET)
	public ModelAndView rfqMeetingList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		List<RfqEventMeeting> meetingList = rfqMeetingService.getAllMeetingByEventId(eventId);
		model.addAttribute("meetingList", meetingList);
		model.addAttribute("eventId", eventId);
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		RfqEvent rfqEventObj = rfqEventService.getEventById(eventId);
		model.addAttribute("event", rfqEventObj);
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
		RfqEvent rfqEventObj = rfqEventService.getEventById(eventId);
		model.addAttribute("event", rfqEventObj);
		model.addAttribute("source", source);
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		session.removeAttribute("listMeetingDocument");
		return new ModelAndView(view);
	}

	@RequestMapping(value = "/meetingNext", method = RequestMethod.POST)
	public ModelAndView meetingNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfqEvent rfqEvent = rfqMeetingService.getRfqEventById(eventId);
		if (rfqEvent != null) {
			rfqEvent.setMeetingCompleted(Boolean.TRUE);
			model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rfqEventService.updateEvent(rfqEvent);
			return meetingNext(rfqEvent);

		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/meetingPrevious", method = RequestMethod.POST)
	public String meetingPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		LOG.info("EVENT ID : " + eventId);
		RfqEvent rfqEvent = rfqMeetingService.getRfqEventById(eventId);
		if (rfqEvent != null) {
			model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return super.meetingPrevious(rfqEvent);
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
	public ModelAndView viewRfqMeeting(@PathVariable String meetingId, Model model) {
		LOG.info("view Rft Meeting GET called");
		if (StringUtils.checkString(meetingId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfqEventMeeting rfqEventMeeting = rfqMeetingService.getMeetingById(meetingId);
		model.addAttribute("eventMeeting", rfqEventMeeting);
		model.addAttribute("listSupplier", rfqEventMeeting.getInviteSuppliers());
		model.addAttribute("event", rfqEventService.getEventById(rfqEventMeeting.getRfxEvent().getId()));
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfqEventMeeting.getRfxEvent().getId()));

		return new ModelAndView("viewMeeting");
	}

	@RequestMapping(path = "/createMeeting", method = RequestMethod.GET)
	public ModelAndView createRfqMeeting(Model model, @RequestParam("source") String source, @RequestParam(required = false) String eventId, HttpSession session) {
		LOG.info(" Create Rfq Meeting called");
		String view = "";
		if (source.equalsIgnoreCase("summary")) {

			view = "summaryEventMeetingEdit";
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "createMeeting";
		}
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfqEventMeeting eventMeeting = new RfqEventMeeting();
		RfqEvent event = new RfqEvent();
		event.setId(eventId);
		eventMeeting.setRfxEvent(event);
		model.addAttribute("meetingType", MeetingType.values());
		session.removeAttribute("listMeetingDocument");
		model.addAttribute("intervalType", IntervalType.values());
		RfqEvent rfqEventObj = rfqEventService.getEventById(eventId);
		model.addAttribute("event", rfqEventObj);
		model.addAttribute("listSuppliers", rfqMeetingService.getEventSuppliersWithIdAndName(eventId));
		model.addAttribute("contactObject", new RfqEventMeetingContact());
		model.addAttribute("btnValue", "Create");
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("eventMeeting", eventMeeting);
		return new ModelAndView(view);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/createMeeting", method = RequestMethod.POST)
	public ModelAndView saveRfqMeeting(@ModelAttribute RfqEventMeeting rfqEventMeeting, @RequestParam("source") String source, @RequestParam(value = "contactId[]") String[] contactId, @RequestParam(value = "contactName[]") String[] contactName, @RequestParam(value = "contactEmail[]") String[] contactEmail, @RequestParam(value = "contactNumber[]") String[] contactNumber, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "selectAllSupplier", required = false) Boolean selectAllSupplier, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		LOG.info("save Rfq Meeting Called " + rfqEventMeeting.getRfxEvent().getId());
		String errorView = "";
		String successView = "";
		if (source.equalsIgnoreCase("summary")) {
			errorView = "summaryEventMeetingEdit";
			successView = "redirect:eventSummary/" + rfqEventMeeting.getRfxEvent().getId();
		} else if (source.equalsIgnoreCase("meeting")) {
			errorView = "createMeeting";
			successView = "redirect:meetingList/" + rfqEventMeeting.getRfxEvent().getId();
		}

		String eventId = rfqEventMeeting.getRfxEvent().getId();
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
		List<RfqEventMeetingContact> meetingContacts = reConstructContactList(rfqEventMeeting, contactId, contactName, contactEmail, contactNumber);
		rfqEventMeeting.setRfxEventMeetingContacts(meetingContacts);
		if (reminderDuration != null && reminderDuration.length > 0) {
			LOG.info("reminder dtae with save");
			rfqEventMeeting.setRfxEventMeetingReminder(constructReminderList(rfqEventMeeting, reminderDuration, reminderDurationType, eventId, model, session));
		}
		if (StringUtils.checkString(rfqEventMeeting.getId()).length() != 0) {
			List<RfqEventMeetingReminder> reminderList = rfqMeetingService.getAllRfqEventMeetingReminderForMeeting(rfqEventMeeting.getId());
			model.addAttribute("reminderList", reminderList);
		}
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfqEventMeeting.getRfxEvent().getId()));
		model.addAttribute("eventMeeting", rfqEventMeeting);
		model.addAttribute("source", source);
		model.addAttribute("rftMeetingId", rfqEventMeeting.getId());
		model.addAttribute("listSuppliers", rfqMeetingService.getEventSuppliersWithIdAndName(eventId));
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				errMessages.add(error.getDefaultMessage());
			}
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue", "Create");
			model.addAttribute("event", rfqEventService.getRfqEventByeventId(eventId));
			return new ModelAndView(errorView);
		} else {
			try {
				if (validateMeeting(rfqEventMeeting, model, eventId)) {
					List<RfqEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(rfqEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfqEventMeetingDocument eventMeetingDocument = new RfqEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfqEventMeeting);

				}
				Date dateTime = DateUtil.combineDateTime(rfqEventMeeting.getAppointmentDateTime(), rfqEventMeeting.getAppointmentTime(), timeZone);
				rfqEventMeeting.setAppointmentDateTime(dateTime);
				RfqEvent event = rfqEventService.getRfqEventByeventId(eventId);

				if (rfqEventMeeting.getAppointmentDateTime().before(new Date())) {
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishpastdate", new Object[] { dateTimeFormat.format(rfqEventMeeting.getAppointmentDateTime()) }, Global.LOCALE));
					model.addAttribute("event", rfqEventService.getRfqEventByeventId(eventId));
					List<RfqEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(rfqEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfqEventMeetingDocument eventMeetingDocument = new RfqEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfqEventMeeting);
				}
				if (rfqEventMeeting.getAppointmentDateTime().before(event.getEventPublishDate())) {
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("event", rfqEventService.getRfqEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeFormat.format(rfqEventMeeting.getAppointmentDateTime()), dateTimeFormat.format(event.getEventPublishDate()) }, Global.LOCALE));
					List<RfqEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(rfqEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfqEventMeetingDocument eventMeetingDocument = new RfqEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfqEventMeeting);
				}
				if (event.getEventEnd().before(rfqEventMeeting.getAppointmentDateTime())) {
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("event", rfqEventService.getRfqEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventenddate", new Object[] { dateTimeFormat.format(rfqEventMeeting.getAppointmentDateTime()), dateTimeFormat.format(event.getEventEnd()) }, Global.LOCALE));
					List<RfqEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rfqEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(rfqEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfqEventMeetingDocument eventMeetingDocument = new RfqEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rfqEventMeeting);
				}

				List<RfqEventMeetingDocument> list = new ArrayList<RfqEventMeetingDocument>();
				if (session != null) {
					List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
						for (EventDocumentPojo document : oldListMeetingDocument) {
							if (StringUtils.checkString(document.getId()).length() == 0) {
								RfqEventMeetingDocument doc = new RfqEventMeetingDocument();
								doc.setCredContentType(document.getCredContentType());
								doc.setFileData(document.getFileData());
								doc.setFileName(document.getFileName());
								doc.setFileSizeInKb(document.getFileSize());
								doc.setTenantId(document.getTenantId());
								doc.setRfxEventMeeting(rfqEventMeeting);
								// LOG.info("DOCS : " + doc.toString());
								list.add(doc);
							}
						}
						// rfqEventMeeting.setRfxEventMeetingDocument(list);
					}
				}
				if (StringUtils.checkString(rfqEventMeeting.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.save.success", new Object[] {}, Global.LOCALE));
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.update.success", new Object[] {}, Global.LOCALE));
				}
				rfqEventMeeting = rfqMeetingService.saveMeeting(rfqEventMeeting, list, selectAllSupplier, eventId, event.getEventVisibility());
				
				session.removeAttribute("listMeetingDocument");
				LOG.info("EVENT ID : " + rfqEventMeeting.getRfxEvent().getId());
				try {
					rfqEventService.insertTimeLine(eventId);
				} catch (Exception e) {
					LOG.error("Error : " + e.getMessage(), e);
				}
			} catch (ApplicationException e) {

				if (StringUtils.checkString(rfqEventMeeting.getId()).length() == 0) {
					model.addAttribute("btnValue", "Create");
				} else {
					model.addAttribute("btnValue", "Update");
				}
				model.addAttribute("event", rfqEventService.getRfqEventByeventId(eventId));
				model.addAttribute("error", e.getMessage());
				List<RfqEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(rfqEventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(rfqEventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfqEventMeetingDocument eventMeetingDocument = new RfqEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
							meetingDocumentList.add(eventMeetingDocument);
						}
					}
				}
				model.addAttribute("listMeetingDocument", meetingDocumentList);
				return new ModelAndView(errorView, "eventMeeting", rfqEventMeeting);
			} catch (Exception e) {
				LOG.error("Error while storing rftMeeting : " + e.getMessage(), e);
				model.addAttribute("event", rfqEventService.getRfqEventByeventId(eventId));
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", messageSource.getMessage("rft.meeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
				List<RfqEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(rfqEventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(rfqEventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfqEventMeetingDocument eventMeetingDocument = new RfqEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
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

	private List<RfqEventMeetingReminder> constructReminderList(RfqEventMeeting rfqEventMeeting, Integer[] reminderDuration, IntervalType[] reminderDurationType, String eventId, Model model, HttpSession session) {
		List<RfqEventMeetingReminder> meetingRemindersList = new ArrayList<RfqEventMeetingReminder>();
		try {
			RfqEvent event = rfqEventService.getRfqEventByeventId(eventId);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null)
				for (Integer reminderDurationValue : reminderDuration) {
					RfqEventMeetingReminder meetingReminder = new RfqEventMeetingReminder();
					Date meetingDateAndTime = DateUtil.combineDateTime(rfqEventMeeting.getAppointmentDateTime(), rfqEventMeeting.getAppointmentTime(), timeZone);
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
					meetingReminder.setRfxEventMeeting(rfqEventMeeting);
					meetingReminder.setReminderDate(cal.getTime());
					meetingReminder.setRfxEvent(event);
					meetingRemindersList.add(meetingReminder);

					if (meetingReminder.getReminderDate().after(meetingDateAndTime)) {
						model.addAttribute("error", messageSource.getMessage("rfq.reminder.before.meetingdate", new Object[] {}, Global.LOCALE));
						new NotAllowedException("Reminder date should not be before than Meeting date date. [" + meetingReminder.getReminderDate() + "]");
					}
					index++;
				}
		} catch (Exception e) {
			LOG.info("Error while Storing reminderDate " + e);
			throw e;
		}
		return meetingRemindersList;
	}

	private List<RfqEventMeetingContact> reConstructContactList(RfqEventMeeting rfqEventMeeting, String[] contactId, String[] contactName, String[] contactEmail, String[] contactNumber) {
		List<RfqEventMeetingContact> meetingContacts = new ArrayList<RfqEventMeetingContact>();
		LOG.info("contactName.length " + contactName.length);
		for (int idx = 0; idx < contactName.length; idx++) {
			RfqEventMeetingContact contact = new RfqEventMeetingContact(((contactId != null && contactId.length > 0) ? contactId[idx] : null), contactName[idx], contactEmail[idx], contactNumber[idx]);
			contact.setRfxEventMeeting(rfqEventMeeting);
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
		RfqEventMeeting rfqEventMeeting = rfqMeetingService.getMeetingById(meetingId);
		String eventId = rfqEventMeeting.getRfxEvent().getId();
		List<EventDocumentPojo> pojos = new ArrayList<EventDocumentPojo>();
		List<RfqEventMeetingDocument> meetingDocumentList = rfqMeetingService.getPlainMeetingDocument(meetingId);
		if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
			for (EventMeetingDocument document : meetingDocumentList) {
				EventDocumentPojo pojo = new EventDocumentPojo(document);
				pojos.add(pojo);
			}
		}
		session.setAttribute("listMeetingDocument", pojos);
		model.addAttribute("listMeetingDocument", meetingDocumentList);

		List<RfqEventMeetingReminder> reminderList = rfqMeetingService.getAllRfqEventMeetingReminderForMeeting(meetingId);
		LOG.info("masdasda asdasdas asd " + reminderList.size());
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("source", source);
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("listSuppliers", rfqMeetingService.getEventSuppliersWithIdAndName(eventId));
		model.addAttribute("event", rfqEventService.getEventById(rfqEventMeeting.getRfxEvent().getId()));
		model.addAttribute("btnValue", "Update");
		return new ModelAndView(view, "eventMeeting", rfqEventMeeting);
	}

	@RequestMapping(value = "/removeInviteSupplier", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Supplier>> removeInviteSupplier(@RequestParam("removeSupId") String removeSupId, @RequestParam("meetingId") String meetingId) {
		LOG.info("removeSupId  :" + removeSupId + " meetingId :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeSupId).length() > 0) {
				Integer countSupplierForMeeting = rfqMeetingService.getCountOfSupplierForMeeting(meetingId);
				LOG.info("count " + countSupplierForMeeting);
				if (countSupplierForMeeting > 1) {
					LOG.info("count  more then 1 " + countSupplierForMeeting);
					List<Supplier> suppliers = rfqMeetingService.removeSupplier(meetingId, removeSupId);
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
		LOG.info("RFQ Event Meeting Download docRefId :: :: " + docRefId + ":::docId:::" + docId);
		EventDocumentPojo docs = null;
		List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
		try {
			if (!docId.equals("undefined")) {
				LOG.info("RFT Event Meeting Download  :: :: " + docId);
				EventMeetingDocument doc = rftMeetingService.getMeetingDocumentForDelete(docId);
				docs = new EventDocumentPojo(doc);
				super.setResponseForDownload(response, docs);
			} else if (!docRefId.equals("undefined") && CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
				for (EventDocumentPojo document : oldListMeetingDocument) {
					LOG.info("UI REF ID : " + docRefId + " SESSION REF ID : " + document.getRefId());
					if (document.getRefId() != null && document.getRefId().equals(docRefId)) {
						docs = document;
						break;
					}
				}
				super.setResponseForDownload(response, docs);
			}

		} catch (Exception e) {
			LOG.error("Error while downloaded RFQ Event Meeting Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/meetingSaveDraft", method = RequestMethod.POST)
	public String meetingSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfqEvent rfqEvent = rfqMeetingService.getRfqEventById(eventId);
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfqEvent.getEventName() != null ? rfqEvent.getEventName() : rfqEvent.getEventId()) }, Global.LOCALE));
		return "redirect:meetingList/" + rfqEvent.getId();
	}

	@RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> cancelMeeting(RedirectAttributes redir, @RequestParam("meetingId") String meetingId, @RequestParam(name = "cancelReason", required = true) String cancelReason) {
		LOG.info("cancel meeting called meeting Id :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			RfqEventMeeting rfqEventMeeting = rfqMeetingService.getMeetingById(meetingId);
			if (rfqEventMeeting.getStatus() == MeetingStatus.CANCELLED) {
				headers.add("error", messageSource.getMessage("rft.meeting.already.cancel", new Object[] { rfqEventMeeting.getTitle() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			rfqEventMeeting.setCancelReason(cancelReason);
			rfqMeetingService.cancelMeeting(rfqEventMeeting);
			headers.add("success", messageSource.getMessage("rft.meeting.cancel.success", new Object[] { rfqEventMeeting.getTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while cancel meeting : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.meeting.cancel.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>("CANCELLED", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<RfqEventMeetingReminder> addReminderOfEventMeeting(@RequestParam(value = "reminderDuration") Integer reminderDuration, @RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "reminderDateList[]", required = false) String[] reminderDateList, @RequestParam(value = "reminderId") String reminderId, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder in meetings : ");
		HttpHeaders headers = new HttpHeaders();
		RfqEventMeetingReminder eventMeetingReminder = new RfqEventMeetingReminder();
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
						return new ResponseEntity<RfqEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
								return new ResponseEntity<RfqEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
			return new ResponseEntity<RfqEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("success", "Reminder added successfully");
		return new ResponseEntity<RfqEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<List<RfqEventMeetingReminder>> manageReminderOfEventMeeting(@RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "eventId", required = false) String eventId, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, HttpSession session) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		List<RfqEventMeetingReminder> meetingRemindersList = new ArrayList<RfqEventMeetingReminder>();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null) {
				for (Integer reminderDurationValue : reminderDuration) {
					RfqEventMeetingReminder meetingReminder = new RfqEventMeetingReminder();
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
						return new ResponseEntity<List<RfqEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
		return new ResponseEntity<List<RfqEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.OK);
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
		return "redirect:/buyer/RFQ/meetingList/" + eventId;
	}
}
