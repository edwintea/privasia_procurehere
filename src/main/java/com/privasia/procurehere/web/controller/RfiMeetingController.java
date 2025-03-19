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
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventMeetingReminder;
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
import com.privasia.procurehere.service.RfiEventService;

@Controller
@RequestMapping("/buyer/RFI")
public class RfiMeetingController extends EventMeetingBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfiEventService rfiEventService;

	public RfiMeetingController() {
		super(RfxTypes.RFI);
	}

	@RequestMapping(path = "/meetingList/{eventId}", method = RequestMethod.GET)
	public ModelAndView rftMeetingList(@PathVariable String eventId, Model model) {
		LOG.info(" rft Meeting List called");
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		List<RfiEventMeeting> meetingList = rfiMeetingService.getAllRfiMeetingByEventId(eventId);
		model.addAttribute("meetingList", meetingList);
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("eventId", eventId);
		RfiEvent rftEventObj = rfiEventService.getRfiEventById(eventId);
		model.addAttribute("event", rftEventObj);
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
		LOG.info(" meetingList post called" + eventId);
		session.removeAttribute("listMeetingDocument");
		model.addAttribute("source", source);
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		return new ModelAndView(view);
	}

	@RequestMapping(value = "/meetingNext", method = RequestMethod.POST)
	public ModelAndView meetingNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfiEvent event = rfiMeetingService.getRfiEventById(eventId);
		if (event != null) {
			event.setMeetingCompleted(Boolean.TRUE);
			model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rfiEventService.updateRfiEvent(event);
			return meetingNext(event);

		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/meetingPrevious", method = RequestMethod.POST)
	public String meetingPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) {
		RfiEvent rfiEvent = rfiMeetingService.getRfiEventById(eventId);
		if (rfiEvent != null) {
			model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return super.meetingPrevious(rfiEvent);
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/viewMeeting", method = RequestMethod.POST)
	public ModelAndView viewMeeting(Model model, @RequestParam String meetingId) {
		return super.showMeeting(meetingId);
	}

	@RequestMapping(path = "/viewMeeting/{meetingId}", method = RequestMethod.GET)
	public ModelAndView viewRftMeeting(@PathVariable String meetingId, Model model) {
		LOG.info("view Rft Meeting GET called");
		LOG.info("Meeting ID : " + meetingId);
		if (StringUtils.checkString(meetingId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfiEventMeeting rfiEventMeeting = rfiMeetingService.getRfiMeetingById(meetingId);
		model.addAttribute("eventMeeting", rfiEventMeeting);
		RfiEvent rfiEvent = rfiEventService.getRfiEventById(rfiEventMeeting.getRfxEvent().getId());
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEventMeeting.getRfxEvent().getId()));
		model.addAttribute("event", rfiEvent);
		model.addAttribute("listSupplier", rfiEventMeeting.getInviteSuppliers());
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
			LOG.info("Event ID not found in request... ");
			return new ModelAndView("redirect:/400_error");
		}
		RfiEventMeeting eventMeeting = new RfiEventMeeting();
		RfiEvent event = new RfiEvent();
		event.setId(eventId);
		event.setId(eventId);
		eventMeeting.setRfxEvent(event);
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("meetingType", MeetingType.values());
		model.addAttribute("intervalType", IntervalType.values());
		session.removeAttribute("listMeetingDocument");
		RfiEvent rftEventObj = rfiEventService.getRfiEventById(eventId);
		model.addAttribute("event", rftEventObj);
		model.addAttribute("listSuppliers", rfiMeetingService.getEventSuppliers(eventId));
		model.addAttribute("contactObject", new RfiEventMeetingContact());
		model.addAttribute("btnValue", "Create");
		model.addAttribute("eventMeeting", eventMeeting);
		return new ModelAndView(view);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/createMeeting", method = RequestMethod.POST)
	public ModelAndView saveRftMeeting(@ModelAttribute RfiEventMeeting eventMeeting, @RequestParam("source") String source, @RequestParam(value = "contactId[]") String[] contactId, @RequestParam(value = "contactName[]") String[] contactName, @RequestParam(value = "contactEmail[]") String[] contactEmail, @RequestParam(value = "contactNumber[]") String[] contactNumber, @RequestParam(value = "reminderDateAndTime[]", required = false) String[] reminderDate, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, BindingResult result, RedirectAttributes redir, HttpSession session, @RequestParam(value = "selectAllSupplier", required = false) Boolean selectAllSupplier) {
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
		SimpleDateFormat dateTimeformatter = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
		dateTimeformatter.setTimeZone(timeZone);
		List<RfiEventMeetingContact> meetingContacts = reConstructContactList(eventMeeting, contactId, contactName, contactEmail, contactNumber);
		eventMeeting.setRfxEventMeetingContacts(meetingContacts);
		LOG.info("here we need to xheck : " + reminderDate);
		if (reminderDate != null && reminderDate.length > 0) {
			LOG.info("here we need to xheck");
			eventMeeting.setRfxEventMeetingReminder(constructReminderList(eventMeeting, reminderDuration, reminderDurationType, eventId, model, session));
		}
		if (StringUtils.checkString(eventMeeting.getId()).length() != 0) {
			List<RfiEventMeetingReminder> reminderList = rfiMeetingService.getAllRfiEventMeetingReminderForMeeting(eventMeeting.getId());
			LOG.info("masdasda asdasdas asd " + reminderList.size());
			model.addAttribute("reminderList", reminderList);
		}
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("eventMeeting", eventMeeting);
		model.addAttribute("source", source);
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventMeeting.getRfxEvent().getId()));
		model.addAttribute("rftMeetingId", eventMeeting.getId());
		model.addAttribute("listSuppliers", rfiMeetingService.getEventSuppliers(eventId));
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				errMessages.add(error.getDefaultMessage());
			}
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue", "Create");
			model.addAttribute("event", rfiEventService.getRfiEventByeventId(eventId));
			return new ModelAndView(errorView);
		} else {
			try {
				if (validateMeeting(eventMeeting, model, eventId)) {
					List<RfiEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfiEventMeetingDocument eventMeetingDocument = new RfiEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", eventMeeting);
				}

				Date dateTime = DateUtil.combineDateTime(eventMeeting.getAppointmentDateTime(), eventMeeting.getAppointmentTime(), timeZone);
				eventMeeting.setAppointmentDateTime(dateTime);
				RfiEvent event = rfiEventService.getRfiEventByeventId(eventId);

				if (eventMeeting.getAppointmentDateTime().before(new Date())) {
					if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
						model.addAttribute("btnValue", "Create");
					} else {
						model.addAttribute("btnValue", "Update");
					}
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishpastdate", new Object[] { dateTimeformatter.format(eventMeeting.getAppointmentDateTime()) }, Global.LOCALE));
					model.addAttribute("event", rfiEventService.getRfiEventByeventId(eventId));
					List<RfiEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfiEventMeetingDocument eventMeetingDocument = new RfiEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
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
					model.addAttribute("event", rfiEventService.getRfiEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeformatter.format(eventMeeting.getAppointmentDateTime()), dateTimeformatter.format(event.getEventPublishDate()) }, Global.LOCALE));
					List<RfiEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfiEventMeetingDocument eventMeetingDocument = new RfiEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
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
					model.addAttribute("event", rfiEventService.getRfiEventByeventId(eventId));
					model.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventenddate", new Object[] { dateTimeformatter.format(eventMeeting.getAppointmentDateTime()), dateTimeformatter.format(event.getEventEnd()) }, Global.LOCALE));
					List<RfiEventMeetingDocument> meetingDocumentList = new ArrayList<>();
					if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
						meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(eventMeeting.getId());
					} else {
						List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
						if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
							for (EventDocumentPojo doc : eventDocumentPojoList) {
								RfiEventMeetingDocument eventMeetingDocument = new RfiEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
								meetingDocumentList.add(eventMeetingDocument);
							}
						}
					}
					model.addAttribute("listMeetingDocument", meetingDocumentList);
					return new ModelAndView(errorView, "eventMeeting", eventMeeting);
				}
				// Attach the documents
				List<RfiEventMeetingDocument> list = new ArrayList<RfiEventMeetingDocument>();
				if (session != null) {
					List<EventDocumentPojo> oldListMeetingDocument = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
						for (EventDocumentPojo document : oldListMeetingDocument) {
							if (StringUtils.checkString(document.getId()).length() == 0) {
								RfiEventMeetingDocument doc = new RfiEventMeetingDocument();
								doc.setCredContentType(document.getCredContentType());
								doc.setFileData(document.getFileData());
								doc.setFileSizeInKb(document.getFileSize());
								doc.setFileName(document.getFileName());
								doc.setTenantId(document.getTenantId());
								doc.setRfxEventMeeting(eventMeeting);
								list.add(doc);
								// LOG.info("DOCS : " + doc.toString());

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
				eventMeeting = rfiMeetingService.saveRfiMeeting(eventMeeting, list, selectAllSupplier, eventId, event.getEventVisibility());

				session.removeAttribute("listMeetingDocument");
				try {
					rfiEventService.insertTimeLine(eventId);
				} catch (Exception e) {
					LOG.error("Error : " + e.getMessage(), e);
				}

			} catch (ApplicationException e) {

				if (StringUtils.checkString(eventMeeting.getId()).length() == 0) {
					model.addAttribute("btnValue", "Create");
				} else {
					model.addAttribute("btnValue", "Update");
				}
				model.addAttribute("event", rfiEventService.getRfiEventByeventId(eventId));
				model.addAttribute("error", e.getMessage());
				List<RfiEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(eventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfiEventMeetingDocument eventMeetingDocument = new RfiEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
							meetingDocumentList.add(eventMeetingDocument);
						}
					}
				}
				model.addAttribute("listMeetingDocument", meetingDocumentList);
				return new ModelAndView(errorView, "eventMeeting", eventMeeting);

			} catch (Exception e) {
				LOG.error("Error while storing rftMeeting : " + e.getMessage(), e);
				model.addAttribute("event", rfiEventService.getRfiEventByeventId(eventId));
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", messageSource.getMessage("rft.meeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
				List<RfiEventMeetingDocument> meetingDocumentList = new ArrayList<>();
				if (StringUtils.checkString(eventMeeting.getId()).length() > 0) {
					meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(eventMeeting.getId());
				} else {
					List<EventDocumentPojo> eventDocumentPojoList = (List<EventDocumentPojo>) session.getAttribute("listMeetingDocument");
					if (CollectionUtil.isNotEmpty(eventDocumentPojoList)) {
						for (EventDocumentPojo doc : eventDocumentPojoList) {
							RfiEventMeetingDocument eventMeetingDocument = new RfiEventMeetingDocument(doc.getId(), doc.getFileName(), doc.getCredContentType(), doc.getTenantId(), doc.getFileSize());
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

	// private List<RfiEventMeetingReminder> constructReminderList(RfiEventMeeting rftEventMeeting, String[]
	// reminderDate) {
	// List<RfiEventMeetingReminder> meetingReminders = new ArrayList<RfiEventMeetingReminder>();
	// try {
	// for (int idx = 0; idx < reminderDate.length; idx++) {
	// SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
	// Date remindDate = null;
	// remindDate = formatter.parse(reminderDate[idx]);
	// RfiEventMeetingReminder reminder = new RfiEventMeetingReminder(remindDate);
	// reminder.setRfxEventMeeting(rftEventMeeting);
	// reminder.setRfiEvent(rftEventMeeting.getRfxEvent());
	// meetingReminders.add(reminder);
	// }
	// } catch (Exception e) {
	// LOG.info("Error while Storing reminderDate " + e);
	// }
	// return meetingReminders;
	// }

	private List<RfiEventMeetingReminder> constructReminderList(RfiEventMeeting rfiEventMeeting, Integer[] reminderDuration, IntervalType[] reminderDurationType, String eventId, Model model, HttpSession session) {
		List<RfiEventMeetingReminder> meetingRemindersList = new ArrayList<RfiEventMeetingReminder>();
		try {
			RfiEvent event = rfiEventService.getRfiEventByeventId(eventId);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null)
				for (Integer reminderDurationValue : reminderDuration) {
					RfiEventMeetingReminder meetingReminder = new RfiEventMeetingReminder();
					Date meetingDateAndTime = DateUtil.combineDateTime(rfiEventMeeting.getAppointmentDateTime(), rfiEventMeeting.getAppointmentTime(), timeZone);
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
					meetingReminder.setRfxEventMeeting(rfiEventMeeting);
					meetingReminder.setReminderDate(cal.getTime());
					meetingReminder.setRfiEvent(event);

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

	private List<RfiEventMeetingContact> reConstructContactList(RfiEventMeeting rftEventMeeting, String[] contactId, String[] contactName, String[] contactEmail, String[] contactNumber) {
		List<RfiEventMeetingContact> meetingContacts = new ArrayList<RfiEventMeetingContact>();
		LOG.info("contactName.length " + contactName.length);
		for (int idx = 0; idx < contactName.length; idx++) {
			RfiEventMeetingContact contact = new RfiEventMeetingContact(((contactId != null && contactId.length > 0) ? contactId[idx] : null), contactName[idx], contactEmail[idx], contactNumber[idx]);
			contact.setRfxEventMeeting(rftEventMeeting);
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
		RfiEventMeeting rftEventMeeting = rfiMeetingService.getRfiMeetingById(meetingId);
		String eventId = rftEventMeeting.getRfxEvent().getId();
		List<EventDocumentPojo> pojos = new ArrayList<EventDocumentPojo>();

		List<RfiEventMeetingDocument> meetingDocumentList = rfiMeetingService.getPlainMeetingDocument(meetingId);
		if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
			for (EventMeetingDocument document : meetingDocumentList) {
				EventDocumentPojo pojo = new EventDocumentPojo(document);
				pojos.add(pojo);
			}
		}
		session.setAttribute("listMeetingDocument", pojos);
		model.addAttribute("listMeetingDocument", meetingDocumentList);
		List<RfiEventMeetingReminder> reminderList = rfiMeetingService.getAllRfiEventMeetingReminderForMeeting(meetingId);
		LOG.info("masdasda asdasdas asd " + reminderList.size());
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("source", source);
		model.addAttribute("listSuppliers", rfiMeetingService.getEventSuppliers(eventId));
		model.addAttribute("event", rfiEventService.getRfiEventById(eventId));
		model.addAttribute("btnValue", "Update");
		return new ModelAndView(view, "eventMeeting", rftEventMeeting);
	}

	@RequestMapping(value = "/removeInviteSupplier", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Supplier>> removeInviteSupplier(@RequestParam("removeSupId") String removeSupId, @RequestParam("meetingId") String meetingId) {
		LOG.info("removeSupId  :" + removeSupId + " meetingId :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeSupId).length() > 0) {
				Integer countSupplierForMeeting = rfiMeetingService.getCountOfSupplierForMeeting(meetingId);
				LOG.info("count " + countSupplierForMeeting);
				if (countSupplierForMeeting > 1) {
					List<Supplier> suppliers = rfiMeetingService.removeSupplier(meetingId, removeSupId);
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
				EventMeetingDocument doc = rfiMeetingService.getMeetingDocumentForDelete(docId);
				docs = new EventDocumentPojo(doc);
			} else if (!docRefId.equals("undefined") && CollectionUtil.isNotEmpty(oldListMeetingDocument)) {
				for (EventDocumentPojo document : oldListMeetingDocument) {
					if (document.getRefId() != null && document.getRefId().equals(docRefId)) {
						docs = document;
						break;
					}
				}
			}
			super.setResponseForDownload(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFT Event Meeting Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/meetingSaveDraft", method = RequestMethod.POST)
	public String meetingSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfiEvent rftEvent = rfiMeetingService.getRfiEventById(eventId);
		model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:meetingList/" + rftEvent.getId();
	}

	@RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> cancelMeeting(RedirectAttributes redir, @RequestParam("meetingId") String meetingId, @RequestParam(name = "cancelReason", required = true) String cancelReason) {
		LOG.info("cancel meeting called meeting Id :" + meetingId);
		HttpHeaders headers = new HttpHeaders();
		try {
			RfiEventMeeting rftEventMeeting = rfiMeetingService.getRfiMeetingById(meetingId);
			if (rftEventMeeting.getStatus() == MeetingStatus.CANCELLED) {
				LOG.info("===rftEventMeeting.getStatus()========" + rftEventMeeting.getStatus() + "============");
				headers.add("error", messageSource.getMessage("rft.meeting.already.cancel", new Object[] { rftEventMeeting.getTitle() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			rftEventMeeting.setCancelReason(cancelReason);
			rfiMeetingService.cancelMeeting(rftEventMeeting);
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
	public ResponseEntity<RfiEventMeetingReminder> addReminderOfEventMeeting(@RequestParam(value = "reminderDuration") Integer reminderDuration, @RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "reminderDateList[]", required = false) String[] reminderDateList, @RequestParam(value = "reminderId") String reminderId, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder in meetings : ");
		HttpHeaders headers = new HttpHeaders();
		RfiEventMeetingReminder eventMeetingReminder = new RfiEventMeetingReminder();
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
						return new ResponseEntity<RfiEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
								return new ResponseEntity<RfiEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
			return new ResponseEntity<RfiEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("success", "Reminder added successfully");
		return new ResponseEntity<RfiEventMeetingReminder>(eventMeetingReminder, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOfEventMeeting", method = RequestMethod.POST)
	public ResponseEntity<List<RfiEventMeetingReminder>> manageReminderOfEventMeeting(@RequestParam(value = "meetingDate") String meetingDate, @RequestParam(value = "meetingTime") String meetingTime, @RequestParam(value = "eventId", required = false) String eventId, @RequestParam(value = "reminderDuration[]", required = false) Integer[] reminderDuration, @RequestParam(value = "reminderDurationType[]", required = false) IntervalType[] reminderDurationType, Model model, HttpSession session) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		List<RfiEventMeetingReminder> meetingRemindersList = new ArrayList<RfiEventMeetingReminder>();
		try {

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			int index = 0;
			if (reminderDuration != null) {
				for (Integer reminderDurationValue : reminderDuration) {
					RfiEventMeetingReminder meetingReminder = new RfiEventMeetingReminder();
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
						return new ResponseEntity<List<RfiEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
		return new ResponseEntity<List<RfiEventMeetingReminder>>(meetingRemindersList, headers, HttpStatus.OK);
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
		return "redirect:/buyer/RFI/meetingList/" + eventId;
	}
}