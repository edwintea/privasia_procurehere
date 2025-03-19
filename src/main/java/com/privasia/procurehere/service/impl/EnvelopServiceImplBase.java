package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.annotation.Resource;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EnvelopServiceBase;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.TatReportService;

public abstract class EnvelopServiceImplBase implements EnvelopServiceBase {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Resource
	MessageSource messageSource;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	ApprovalService approvalService;
	
	@Autowired
	TatReportService tatReportService;

	@Override
	public void sendEnvelopOpenNotification(Envelop envelop, RfxTypes eventType, String eventId, User loggedInUser, Boolean isAllOpen) {
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + eventType.name() + "/eventSummary/" + eventId;
		String ownerMsg = "";
		String openerMsg = "";
		try {
			switch (eventType) {
			case RFA:
				RfaEnvelop rfaEnvelop = (RfaEnvelop) envelop;
				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully opened \"" + rfaEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopOpenedNotification(loggedInUser, loggedInUser.getName(), rfaEvent, url, timeZone, eventType, rfaEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllOpen) {
					LOG.info("Sending mail to Owner and Evaluators: ");
					ownerMsg = "Envelope \"" + rfaEnvelop.getEnvelopTitle() + "\" has been opened";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has opened envelope \"" +
					// rfaEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfaOwner = rfaEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfaOwner, timeZone);
					sendEnvelopOpenedNotification(rfaOwner, loggedInUser.getName(), rfaEvent, url, timeZone, eventType, rfaEnvelop.getEnvelopTitle(), ownerMsg);
					sendEventReadyEvaluationNotification(rfaOwner, rfaEvent, url, timeZone, eventType, rfaEnvelop.getEnvelopTitle());

					// sending envelop ready for evaluation mail and dashboard to envelop evaluators
					if (CollectionUtil.isNotEmpty(rfaEnvelop.getEvaluators())) {
						for (RfaEvaluatorUser evaluatorUser : rfaEnvelop.getEvaluators()) {
							sendEventReadyEvaluationNotification(evaluatorUser.getUser(), rfaEvent, url, timeZone, eventType, rfaEnvelop.getEnvelopTitle());
						}
					}
				}
				break;
			case RFI:
				RfiEnvelop rfiEnvelop = (RfiEnvelop) envelop;
				RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully opened \"" + rfiEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopOpenedNotification(loggedInUser, loggedInUser.getName(), rfiEvent, url, timeZone, eventType, rfiEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllOpen) {
					ownerMsg = "Envelope \"" + rfiEnvelop.getEnvelopTitle() + "\" has been opened";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has opened envelope \"" +
					// rfiEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfiOwner = rfiEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfiOwner, timeZone);
					sendEnvelopOpenedNotification(rfiOwner, loggedInUser.getName(), rfiEvent, url, timeZone, eventType, rfiEnvelop.getEnvelopTitle(), ownerMsg);
					sendEventReadyEvaluationNotification(rfiOwner, rfiEvent, url, timeZone, eventType, rfiEnvelop.getEnvelopTitle());

					// sending envelop open and ready for evaluation mail and dashboard to envelop evaluators
					if (CollectionUtil.isNotEmpty(rfiEnvelop.getEvaluators())) {
						for (RfiEvaluatorUser evaluatorUser : rfiEnvelop.getEvaluators()) {
							sendEventReadyEvaluationNotification(evaluatorUser.getUser(), rfiEvent, url, timeZone, eventType, rfiEnvelop.getEnvelopTitle());
						}
					}
				}
				break;
			case RFP:
				RfpEnvelop rfpEnvelop = (RfpEnvelop) envelop;
				RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully opened \"" + rfpEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopOpenedNotification(loggedInUser, loggedInUser.getName(), rfpEvent, url, timeZone, eventType, rfpEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllOpen) {
					ownerMsg = "Envelope \"" + rfpEnvelop.getEnvelopTitle() + "\" has been opened";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has opened envelope \"" +
					// rfpEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfpOwner = rfpEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfpOwner, timeZone);
					sendEnvelopOpenedNotification(rfpOwner, loggedInUser.getName(), rfpEvent, url, timeZone, eventType, rfpEnvelop.getEnvelopTitle(), ownerMsg);
					sendEventReadyEvaluationNotification(rfpOwner, rfpEvent, url, timeZone, eventType, rfpEnvelop.getEnvelopTitle());

					// sending envelop open and ready for evaluation mail and dashboard to envelop evaluators
					if (CollectionUtil.isNotEmpty(rfpEnvelop.getEvaluators())) {
						for (RfpEvaluatorUser evaluatorUser : rfpEnvelop.getEvaluators()) {
							sendEventReadyEvaluationNotification(evaluatorUser.getUser(), rfpEvent, url, timeZone, eventType, rfpEnvelop.getEnvelopTitle());
						}
					}
				}
				break;
			case RFQ:
				RfqEnvelop rfqEnvelop = (RfqEnvelop) envelop;
				RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully opened \"" + rfqEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopOpenedNotification(loggedInUser, loggedInUser.getName(), rfqEvent, url, timeZone, eventType, rfqEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllOpen) {
					ownerMsg = "Envelope \"" + rfqEnvelop.getEnvelopTitle() + "\" has been opened";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has opened envelope \"" +
					// rfqEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfqOwner = rfqEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfqOwner, timeZone);
					sendEnvelopOpenedNotification(rfqOwner, loggedInUser.getName(), rfqEvent, url, timeZone, eventType, rfqEnvelop.getEnvelopTitle(), ownerMsg);
					sendEventReadyEvaluationNotification(rfqOwner, rfqEvent, url, timeZone, eventType, rfqEnvelop.getEnvelopTitle());

					// sending envelop open and ready for evaluation mail and dashboard to envelop evaluators
					if (CollectionUtil.isNotEmpty(rfqEnvelop.getEvaluators())) {
						for (RfqEvaluatorUser evaluatorUser : rfqEnvelop.getEvaluators()) {
							sendEventReadyEvaluationNotification(evaluatorUser.getUser(), rfqEvent, url, timeZone, eventType, rfqEnvelop.getEnvelopTitle());
						}
					}
				}
				break;
			case RFT:
				RftEnvelop rftEnvelop = (RftEnvelop) envelop;
				RftEvent rftEvent = rftEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully opened \"" + rftEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopOpenedNotification(loggedInUser, loggedInUser.getName(), rftEvent, url, timeZone, eventType, rftEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllOpen) {
					ownerMsg = "Envelope \"" + rftEnvelop.getEnvelopTitle() + "\" has been opened";
					// ownerMsg = " \"" + loggedInUser.getName() + "\" has opened envelope \"" +
					// rftEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rftOwner = rftEventService.getPlainEventOwnerByEventId(rftEvent.getId());
					timeZone = getTimeZoneByBuyerSettings(rftOwner, timeZone);
					sendEnvelopOpenedNotification(rftOwner, loggedInUser.getName(), rftEvent, url, timeZone, eventType, rftEnvelop.getEnvelopTitle(), ownerMsg);
					sendEventReadyEvaluationNotification(rftOwner, rftEvent, url, timeZone, eventType, rftEnvelop.getEnvelopTitle());

					// sending envelop open and ready for evaluation mail and dashboard to envelop evaluators
					if (CollectionUtil.isNotEmpty(rftEnvelop.getEvaluators())) {
						for (RftEvaluatorUser evaluatorUser : rftEnvelop.getEvaluators()) {
							sendEventReadyEvaluationNotification(evaluatorUser.getUser(), rftEvent, url, timeZone, eventType, rftEnvelop.getEnvelopTitle());
						}
					}
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while sending evaluation notification :" + e.getMessage(), e);
		}

	}

	private void sendEnvelopOpenedNotification(User user, String LoggedInUserName, Event event, String url, String timeZone, RfxTypes eventType, String envelopTitle, String msg) {
		String mailTo = "";
		String subject = "Envelope has opened";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType.getValue());
			map.put("businessUnit", StringUtils.checkString(approvalService.findBusinessUnit(event.getId(), eventType)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.ENVELOPE_OPENED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail on Event Envelop open : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = msg;
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on Event Envelop open : " + e.getMessage(), e);
		}
	}

	private void sendEnvelopClosedNotification(User user, String LoggedInUserName, Event event, String url, String timeZone, RfxTypes eventType, String envelopTitle, String msg) {
		String mailTo = "";
		String subject = "Envelope has closed";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {

			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType.getValue());
			map.put("businessUnit", StringUtils.checkString(approvalService.findBusinessUnit(event.getId(), eventType)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.ENVELOPE_OPENED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail on Event Envelop close : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = msg;
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on Event Envelop close: " + e.getMessage(), e);
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	private void sendEventReadyEvaluationNotification(User user, Event event, String url, String timeZone, RfxTypes eventType, String envelopTitle) {
		String mailTo = "";
		String subject = "Event is ready for evaluation";
		String msg = "\"" + envelopTitle + "\" is ready to evaluate for event \"" + event.getReferanceNumber() + "\"";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("msg", msg);
			map.put("eventType", eventType.getValue());
			map.put("businessUnit", StringUtils.checkString(approvalService.findBusinessUnit(event.getId(), eventType)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.EVENT_EVALUATION_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail on Event Envelop Evaluation : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("event.ready.evaluation.notification.message", new Object[] { envelopTitle, event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on Event Envelop Evaluation : " + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
	private String getTimeZoneByBuyerSettings(User user, String timeZone) {
		try {
			if (StringUtils.checkString(user.getTenantId()).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(user.getTenantId());
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	@Override
	public void sendEnvelopCloseNotification(Envelop envelop, RfxTypes eventType, String eventId, User loggedInUser, Boolean isAllClosed) {
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + eventType.name() + "/eventSummary/" + eventId;
		String ownerMsg = "";
		String openerMsg = "";
		try {
			switch (eventType) {
			case RFA:
				RfaEnvelop rfaEnvelop = (RfaEnvelop) envelop;
				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully closed \"" + rfaEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopClosedNotification(loggedInUser, loggedInUser.getName(), rfaEvent, url, timeZone, eventType, rfaEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllClosed) {
					ownerMsg = "Envelope \"" + rfaEnvelop.getEnvelopTitle() + "\" has been closed";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has closed envelope \"" +
					// rfaEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfaOwner = rfaEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfaOwner, timeZone);
					sendEnvelopClosedNotification(rfaOwner, loggedInUser.getName(), rfaEvent, url, timeZone, eventType, rfaEnvelop.getEnvelopTitle(), ownerMsg);
				}
				break;
			case RFI:
				RfiEnvelop rfiEnvelop = (RfiEnvelop) envelop;
				RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully closed \"" + rfiEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopClosedNotification(loggedInUser, loggedInUser.getName(), rfiEvent, url, timeZone, eventType, rfiEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllClosed) {
					ownerMsg = "Envelope \"" + rfiEnvelop.getEnvelopTitle() + "\" has been closed";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has closed envelope \"" +
					// rfiEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfiOwner = rfiEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfiOwner, timeZone);
					sendEnvelopClosedNotification(rfiOwner, loggedInUser.getName(), rfiEvent, url, timeZone, eventType, rfiEnvelop.getEnvelopTitle(), ownerMsg);

					// sending envelop open and ready for evaluation mail and dashboard to envelop evaluators
				}
				break;
			case RFP:
				RfpEnvelop rfpEnvelop = (RfpEnvelop) envelop;
				RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully closed \"" + rfpEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopClosedNotification(loggedInUser, loggedInUser.getName(), rfpEvent, url, timeZone, eventType, rfpEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllClosed) {
					ownerMsg = "Envelope \"" + rfpEnvelop.getEnvelopTitle() + "\" has been closed";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has closed envelope \"" +
					// rfpEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfpOwner = rfpEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfpOwner, timeZone);
					sendEnvelopClosedNotification(rfpOwner, loggedInUser.getName(), rfpEvent, url, timeZone, eventType, rfpEnvelop.getEnvelopTitle(), ownerMsg);
				}
				break;
			case RFQ:
				RfqEnvelop rfqEnvelop = (RfqEnvelop) envelop;
				RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully closed \"" + rfqEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopClosedNotification(loggedInUser, loggedInUser.getName(), rfqEvent, url, timeZone, eventType, rfqEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllClosed) {
					ownerMsg = "Envelope \"" + rfqEnvelop.getEnvelopTitle() + "\" has been closed";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has closed envelope \"" +
					// rfqEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rfqOwner = rfqEventService.getPlainEventOwnerByEventId(eventId);
					timeZone = getTimeZoneByBuyerSettings(rfqOwner, timeZone);
					sendEnvelopClosedNotification(rfqOwner, loggedInUser.getName(), rfqEvent, url, timeZone, eventType, rfqEnvelop.getEnvelopTitle(), ownerMsg);
				}
				break;
			case RFT:
				RftEnvelop rftEnvelop = (RftEnvelop) envelop;
				RftEvent rftEvent = rftEventService.getPlainEventById(eventId);

				openerMsg = "You have succesfully closed \"" + rftEnvelop.getEnvelopTitle() + "\"";
				// sending envelop open mail and dashboard to envelop opener
				sendEnvelopClosedNotification(loggedInUser, loggedInUser.getName(), rftEvent, url, timeZone, eventType, rftEnvelop.getEnvelopTitle(), openerMsg);

				if (isAllClosed) {
					ownerMsg = "Envelope \"" + rftEnvelop.getEnvelopTitle() + "\" has been closed";
					// ownerMsg = "\"" + loggedInUser.getName() + "\" has closed envelope \"" +
					// rftEnvelop.getEnvelopTitle() + "\"";

					// sending envelop open and ready for evaluation mail and dashboard to event owner
					User rftOwner = rftEventService.getPlainEventOwnerByEventId(rftEvent.getId());
					timeZone = getTimeZoneByBuyerSettings(rftOwner, timeZone);
					sendEnvelopClosedNotification(rftOwner, loggedInUser.getName(), rftEvent, url, timeZone, eventType, rftEnvelop.getEnvelopTitle(), ownerMsg);
					break;
				}
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while sending evaluation notification :" + e.getMessage(), e);
		}

	}

}
