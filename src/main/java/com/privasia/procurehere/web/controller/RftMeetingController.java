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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeetingContact;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEventMeetingReminder;
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
import com.privasia.procurehere.service.RftEventService;

@Controller
@RequestMapping("/buyer/RFT")
public class RftMeetingController extends EventMeetingBase {

	@Autowired
	RftEventService rftEventService;

	public RftMeetingController() {
		super(RfxTypes.RFT);
	}

	@RequestMapping(path = "/meetingList/{eventId}", method = RequestMethod.GET)
	public ModelAndView rftMeetingList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		List<RftEventMeeting> meetingList = rftMeetingService.getRftMeetingByEventId(eventId);
		model.addAttribute("meetingList", meetingList);
		model.addAttribute("eventId", eventId);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		RftEvent rftEventObj = rftEventService.getRftEventByeventId(eventId);
		model.addAttribute("event", rftEventObj);
		return new ModelAndView("meetingList");
	}

	@RequestMapping(path = "/meetingList", method = RequestMethod.POST)
	public ModelAndView meetingList(Model model, @RequestParam(name = "eventId") String eventId, @RequestParam(name = "source") String source, HttpSession session) {
		String view = "";
		LOG.info("========EventId======================" + eventId + "=============================");
		if (source.equalsIgnoreCase("summary")) {

			view = "redirect:eventSummary/" + eventId;
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "redirect:meetingList/" + eventId;
		}
		RftEvent rftEventObj = rftEventService.getRftEventByeventId(eventId);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("event", rftEventObj);
		model.addAttribute("source", source);
		session.removeAttribute("listMeetingDocument");
		return new ModelAndView(view);
	}

	@RequestMapping(value = "/meetingNext", method = RequestMethod.POST)
	public ModelAndView meetingNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RftEvent rftEvent = rftEventService.getRftEventByeventId(eventId);
		if (rftEvent != null) {
			rftEvent.setMeetingCompleted(Boolean.TRUE);
			rftEventService.updateRftEvent(rftEvent);
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return meetingNext(rftEvent);

		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/meetingPrevious", method = RequestMethod.POST)
	public String meetingPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		LOG.info("EVENT ID : " + eventId);
		RftEvent rftEvent = rftEventService.getRftEventByeventId(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
		LOG.info("view Rft Meeting GET called");
		if (StringUtils.checkString(meetingId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RftEventMeeting rftEventMeeting = rftMeetingService.getRftMeetingById(meetingId);
		model.addAttribute("eventMeeting", rftEventMeeting);
		model.addAttribute("listSupplier", rftEventMeeting.getInviteSuppliers());
		model.addAttribute("event", rftEventService.getPlainEventWithOwnerById(rftEventMeeting.getRfxEvent().getId()));
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEventMeeting.getRfxEvent().getId()));

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
		RftEventMeeting eventMeeting = new RftEventMeeting();
		RftEvent event = new RftEvent();
		event.setId(eventId);
		eventMeeting.setRfxEvent(event);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("meetingType", MeetingType.values());
		session.removeAttribute("listMeetingDocument");
		model.addAttribute("intervalType", IntervalType.values());
		RftEvent rftEventObj = rftEventService.getRftEventByeventId(eventId);
		model.addAttribute("event", rftEventObj);
		model.addAttribute("listSuppliers", rftMeetingService.getEventSuppliers(eventId));
		model.addAttribute("contactObject", new RftEventMeetingContact());
		model.addAttribute("btnValue", "Create");
		model.addAttribute("eventMeeting", eventMeeting);
		return new ModelAndView(view);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/createMeeting", method = RequestMethod.POST)
	public ModelAndView saveRftMeeting(@ModelAttribute("eventMeeting") RftEventMeeting rftEventMeeting, @RequestParam(name = "source") String source, @RequestParam(value = "contactId[]") String[] contactId, @RequestParam(value = "contactName[]") String[] contactName, @RequestParam(value = "contactEmail[]") String[] contactEmail, @RequestParam(value = "contactNumber[]") String[] contactNumber, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, BindingResult result, RedirectAttributes redir, HttpSession session, @RequestParam(value = "selectAllSupplier", required = false) Boolean selectAllSupplier) {
		LOG.info("save Rft Meeting Called " + rftEventMeeting.getId());
		LOG.info("save Rft Meeting source :" + source);

		String errorView = "";
		String successView = "";
		if (source.equalsIgnoreCase("summary")) {
			errorView = "summaryEventMeetingEdit";
			successView = "redirect:eventSummary/" + rftEventMeeting.getRfxEvent().getId();
		} else if (source.equalsIgnoreCase("meeting")) {
			errorView = "createMeeting";
			successView = "redirect:meetingList/" + rftEventMeeting.getRfxEvent().getId();
		}

		String eventId = rftEventMeeting.getRfxEvent().getId();
		LOG.info("Event Id :" + eventId);
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
		List<RftEventMeetingContact> meetingContacts = reConstructContactList(rftEventMeeting, contactId, contactName, contactEmail, contactNumber);
		rftEventMeeting.setRfxEventMeetingContacts(meetingContacts);
		if (reminderDuration != null && reminderDuration.length > 0) {
			LOG.info("reminder dtae with save");
			rftEventMeeting.setRfxEventMeetingReminder(constructReminderList(rftEventMeeting, reminderDuration, reminderDurationType, eventId, model, session));
		}
		if (StringUtils.checkString(rftEventMeeting.getId()).length() != 0) {
			List<RftEventMeetingReminder> reminderList = rftMeetingService.getAllRftEventMeetingReminderForMeeting(rftEventMeeting.getId());
			LOG.info("masdasda asdasdas asd " + reminderList.size());
			model.addAttribute("reminderList", reminderList);
		}
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventMeeting", rftEventMeeting);
		model.addAttribute("rftMeetingId", rftEventMeeting.getId());
		model.addAttribute("source", source);
		model.addAttribute("listSuppliers", rftMeetingService.getEventSuppliers(eventId));
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				errMessages.add(error.getDefaultMessage());
			}
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue", "Create");
			model.addAttribute("event", rftEventService.getRftEventByeventId(eventId));
			return new ModelAndView(errorView);
		} else {
			try {
				if (validateMeeting(rftEventMeeting, model, eventId)) {
					List<RftEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rftEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rftMeetingService.getPlainMeetingDocument(rftEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RftEventMeetingDocument eventMeetingDocument = new RftEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rftEventMeeting);

				}
				Date dateTime = DateUtil.combineDateTime(rftEventMeeting.getAppointmentDateTime(), rftEventMeeting.getAppointmentTime(), timeZone);
				rftEventMeeting.setAppointmentDateTime(dateTime);
				LOG.info("dateTime: *******************" + dateTime);
				RftEvent event = rftEventService.getRftEventByeventId(eventId);

				if (rftEventMeeting.getAppointmentDateTime().before(new Date())) {
					if (StringUtils.checkString(rftEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishpastdate", new Object[] { dateTimeFormat.format(rftEventMeeting.getAppointmentDateTime()) }, Global.LOCALE));
					model.addAttribute("event", rftEventService.getRftEventByeventId(eventId));
					List<RftEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rftEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rftMeetingService.getPlainMeetingDocument(rftEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RftEventMeetingDocument eventMeetingDocument = new RftEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rftEventMeeting);
				}
				if (rftEventMeeting.getAppointmentDateTime().before(event.getEventPublishDate())) {
					if (StringUtils.checkString(rftEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("event", rftEventService.getRftEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeFormat.format(rftEventMeeting.getAppointmentDateTime()), dateTimeFormat.format(event.getEventPublishDate()) }, Global.LOCALE));
					List<RftEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rftEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rftMeetingService.getPlainMeetingDocument(rftEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RftEventMeetingDocument eventMeetingDocument = new RftEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rftEventMeeting);
				}
				if (event.getEventEnd().before(rftEventMeeting.getAppointmentDateTime())) {
					if (StringUtils.checkString(rftEventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("event", rftEventService.getRftEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventenddate", new Object[] { dateTimeFormat.format(rftEventMeeting.getAppointmentDateTime()), dateTimeFormat.format(event.getEventEnd()) }, Global.LOCALE));
					List<RftEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(rftEventMeeting.getId()).length() > 0) {
						meetingDocumentList = rftMeetingService.getPlainMeetingDocument(rftEventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RftEventMeetingDocument eventMeetingDocument = new RftEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", rftEventMeeting);
				}
				// Attach the documents
				List<RftEventMeetingDocument> list = new ArrayList<RftEventMeetingDocument>();
				if (session != null) {
					List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
						for (EventDocumentPojo document : oldListMeetingDocument) {
							if (StringUtils.checkString(document.getId()).length() == 0) {
								RftEventMeetingDocument doc = new RftEventMeetingDocument();
								doc.setCredContentType(document.getCredContentType());
								doc.setFileData(document.getFileData());
								doc.setFileSizeInKb(document.getFileSize());
								doc.setFileName(document.getFileName());
								doc.setTenantId(document.getTenantId());
								doc.setRfxEventMeeting(rftEventMeeting);
								list.add(doc);
								// LOG.info("DOCS : " + doc);
							}
						}
						// rftEventMeeting.setRfxEventMeetingDocument(list);
						session.setAttribute("listMeetingDocument", list);
					}
				}
				if (StringUtils.checkString(rftEventMeeting.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.save.success", new Object[] {}, Global.LOCALE));
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.meeting.update.success", new Object[] {}, Global.LOCALE));
				}
				rftEventMeeting = rftMeetingService.saveRftMeeting(rftEventMeeting, list, selectAllSupplier, eventId, event.getEventVisibility());
				
				session.removeAttribute("listMeetingDocument");
				LOG.info("EVENT ID : " + rftEventMeeting.getRfxEvent().getId());
				try {
					rftEventService.insertTimeLine(eventId);
				} catch (Exception e) {
					LOG.error("Error : " + e.getMessage(), e);
				}
			} catch (ApplicationException e) {

				if (StringUtils.checkString(rftEventMeeting.getId()).length() == 0) {
					model.addAttribute("btnValue", "Create");
				} else {
					model.addAttribute("btnValue", "Update");
				}
				model.addAttribute("error", messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] { dateTimeFormat.format(rftEventMeeting.getAppointmentDateTime()) }, Global.LOCALE));
				model.addAttribute("event", rftEventService.getRftEventByeventId(eventId));
				List<RftEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(rftEventMeeting.getId()).length() > 0) {
					meetingDocumentList = rftMeetingService.getPlainMeetingDocument(rftEventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RftEventMeetingDocument eventMeetingDocument = new RftEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
							meetingDocumentList.add(eventMeetingDocument);
						}
					}
				}
				model.addAttribute("listMeetingDocument", meetingDocumentList);
				return new ModelAndView(errorView, "eventMeeting", rftEventMeeting);

			} catch (Exception e) {
				LOG.error("Error while storing rftMeeting : " + e.getMessage(), e);
				model.addAttribute("event", rftEventService.getRftEventByeventId(eventId));
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", messageSource.getMessage("rft.meeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
				List<RftEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(rftEventMeeting.getId()).length() > 0) {
					meetingDocumentList = rftMeetingService.getPlainMeetingDocument(rftEventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RftEventMeetingDocument eventMeetingDocument = new RftEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
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

	private List<RftEventMeetingReminder> constructReminderList(RftEventMeeting rftEventMeeting, Integer[] reminderDuration, IntervalType[] reminderDurationType, String eventId, Model model, HttpSession session) {
		List<RftEventMeetingReminder> meetingRemindersList = new ArrayList<RftEventMeetingReminder>();
		try {
			RftEvent event = rftEventService.getRftEventByeventId(eventId);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			for (Integer reminderDurationValue : reminderDuration) {
				RftEventMeetingReminder meetingReminder = new RftEventMeetingReminder();
				Date meetingDateAndTime = DateUtil.combineDateTime(rftEventMeeting.getAppointmentDateTime(), rftEventMeeting.getAppointmentTime(), timeZone);
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
				meetingReminder.setRfxEventMeeting(rftEventMeeting);
				meetingReminder.setReminderDate(cal.getTime());
				meetingReminder.setRftEvent(event);
				meetingRemindersList.add(meetingReminder);

				if (meetingReminder.getReminderDate().after(meetingDateAndTime)) {
					// model.addAttribute("error", "Reminder date should not be before than Meeting date");
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

	private List<RftEventMeetingContact> reConstructContactList(RftEventMeeting rftEventMeeting, String[] contactId, String[] contactName, String[] contactEmail, String[] contactNumber) {
		List<RftEventMeetingContact> meetingContacts = new ArrayList<RftEventMeetingContact>();
		LOG.info("contactName.length " + contactName.length);
		for (int idx = 0; idx < contactName.length; idx++) {
			RftEventMeetingContact contact = new RftEventMeetingContact(((contactId != null && contactId.length > 0) ? contactId[idx] : null), contactName[idx], contactEmail[idx], contactNumber[idx]);
			contact.setRfxEventMeeting(rftEventMeeting);
			meetingContacts.add(contact);
		}
		return meetingContacts;
	}

	@RequestMapping(path = "/editMeeting", method = RequestMethod.GET)
	public ModelAndView editMeetings(@RequestParam String meetingId, @RequestParam(name = "source") String source, Model model, HttpSession session) {
		String view = "";

		if (source.equalsIgnoreCase("summary")) {
			view = "summaryEventMeetingEdit";
		} else if (source.equalsIgnoreCase("meeting")) {
			view = "createMeeting";
		}

		LOG.info("edit meeting called meetingId. : " + meetingId);
		RftEventMeeting rftEventMeeting = rftMeetingService.getRftMeetingById(meetingId);
		String eventId = rftEventMeeting.getRfxEvent().getId();
		List<EventDocumentPojo> pojos = new ArrayList<EventDocumentPojo>();

		List<RftEventMeetingDocument> meetingDocumentList = rftMeetingService.getPlainMeetingDocument(meetingId);
		if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
			for (EventMeetingDocument document : meetingDocumentList) {
				EventDocumentPojo pojo = new EventDocumentPojo(document);
				pojos.add(pojo);
			}
		}
		session.setAttribute("listMeetingDocument", pojos);

		List<RftEventMeetingReminder> reminderList = rftMeetingService.getAllRftEventMeetingReminderForMeeting(meetingId);
		model.addAttribute("listMeetingDocument", meetingDocumentList);
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("source", source);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("listSuppliers", rftMeetingService.getEventSuppliers(eventId));
		LOG.info("rftMeetingService.getEventSuppliers(eventId) :" + rftMeetingService.getEventSuppliers(eventId));
		model.addAttribute("event", rftEventService.getRftEventByeventId(rftEventMeeting.getRfxEvent().getId()));
		model.addAttribute("btnValue", "Update");
		return new ModelAndView(view, "eventMeeting", rftEventMeeting);
	}

	@RequestMapping(value = "/removeInviteSupplier", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Supplier>> removeInviteSupplier(@RequestParam("removeSupId") String removeSupId, @RequestParam("meetingId") String meetingId) {
		LOG.info("removeSupId  :" + removeSupId + " meetingId :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeSupId).length() > 0) {
				Integer countSupplierForMeeting = rftMeetingService.getCountOfSupplierForMeeting(meetingId);
				LOG.info("count " + countSupplierForMeeting);
				if (countSupplierForMeeting > 1) {
					LOG.info("count  more then 1 " + countSupplierForMeeting);
					List<Supplier> suppliers = rftMeetingService.removeSupplier(meetingId, removeSupId);
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
		LOG.info("RFT Event Meeting Download docRefId :: :: " + docRefId + ":::docId:::" + docId);
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
			LOG.error("Error while downloaded RFT Event Meeting Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/meetingSaveDraft", method = RequestMethod.POST)
	public String meetingSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		RftEvent rftEvent = rftEventService.getRftEventByeventId(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:meetingList/" + rftEvent.getId();
	}

	@RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> cancelMeeting(RedirectAttributes redir, @RequestParam("meetingId") String meetingId, @RequestParam(name = "cancelReason", required = true) String cancelReason) {
		LOG.info("cancel meeting called meeting Id :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			RftEventMeeting rftEventMeeting = rftMeetingService.getRftMeetingById(meetingId);
			if (rftEventMeeting.getStatus() == MeetingStatus.CANCELLED) {
				headers.add("error", messageSource.getMessage("rft.meeting.already.cancel", new Object[] { rftEventMeeting.getTitle() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			rftEventMeeting.setCancelReason(cancelReason);
			rftMeetingService.cancelMeeting(rftEventMeeting);
			headers.add("success", messageSource.getMessage("rft.meeting.cancel.success", new Object[] { rftEventMeeting.getTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while cancel meeting : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.meeting.cancel.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>("CANCELLED", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<RftEventMeetingReminder> addReminderOfEventMeeting(@RequestParam(value = "reminderDuration") Integer reminderDuration, @RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "reminderDateList[]", required = false) String[] reminderDateList, @RequestParam(value = "reminderId") String reminderId, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder in meetings : ");
		HttpHeaders headers = new HttpHeaders();
		RftEventMeetingReminder eventMeetingReminder = new RftEventMeetingReminder();
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
						return new ResponseEntity<RftEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
								return new ResponseEntity<RftEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
			return new ResponseEntity<RftEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("success", "Reminder added successfully");
		return new ResponseEntity<RftEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<List<RftEventMeetingReminder>> manageReminderOfEventMeeting(@RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "eventId", required = false) String eventId, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, HttpSession session) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		List<RftEventMeetingReminder> meetingRemindersList = new ArrayList<RftEventMeetingReminder>();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null) {
				for (Integer reminderDurationValue : reminderDuration) {
					RftEventMeetingReminder meetingReminder = new RftEventMeetingReminder();
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
						return new ResponseEntity<List<RftEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
		return new ResponseEntity<List<RftEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/saveSummaryMeeting", method = RequestMethod.POST)
	public ResponseEntity<Void> saveSummaryMeeting(@RequestBody RftEventMeeting meeting, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		@SuppressWarnings("unused")
		String eventId = meeting.getRfxEvent().getId();
		/**
		 * Put all attributes into model so that in case of error, these get re-displayed.
		 */
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		// List<RftEventMeetingContact> meetingContacts = reConstructContactList(meeting, contactId, contactName,
		// contactEmail, contactNumber);
		// meeting.setRfxEventMeetingContacts(meetingContacts);
		// if (reminderDuration != null && reminderDuration.length > 0) {
		// LOG.info("reminder dtae with save");
		// meeting.setRfxEventMeetingReminder(constructReminderList(meeting, reminderDuration, reminderDurationType,
		// eventId, model, session));
		// }

		try {
			Date dateTime = DateUtil.combineDateTime(meeting.getAppointmentDateTime(), meeting.getAppointmentTime(), timeZone);
			meeting.setAppointmentDateTime(dateTime);
			// Attach the documents
			if (session != null) {
				List<RftEventMeetingDocument> list = null;
				List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
				if (CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
					list = new ArrayList<RftEventMeetingDocument>();
					for (EventDocumentPojo document : oldListMeetingDocument) {
						RftEventMeetingDocument doc = new RftEventMeetingDocument();
						doc.setCredContentType(document.getCredContentType());
						doc.setFileData(document.getFileData());
						doc.setFileSizeInKb(document.getFileSize());
						doc.setFileName(document.getFileName());
						doc.setTenantId(document.getTenantId());
						doc.setRfxEventMeeting(meeting);
						// LOG.info("DOCS : " + doc.toString());
						list.add(doc);
					}
					meeting.setRfxEventMeetingDocument(list);
					session.setAttribute("listMeetingDocument", list);
				}
			}
			LOG.info("Updating meeting : " + meeting.toLogString());
			// meeting = rftMeetingService.saveRftMeeting(meeting);
			session.removeAttribute("listMeetingDocument");
			redir.addAttribute("success", "Meeting updated successfully");
			LOG.info("EVENT ID : " + meeting.getRfxEvent().getId());
		} catch (Exception e) {
			LOG.error("Error while storing rftMeeting : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rft.meeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Void>(HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
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
		return "redirect:/buyer/RFT/meetingList/" + eventId;
	}

}
