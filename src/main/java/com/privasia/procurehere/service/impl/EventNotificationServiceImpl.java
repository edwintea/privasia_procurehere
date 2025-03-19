/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfiEventAuditDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfpEventAuditDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfqEventAuditDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RftEventAuditDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventMeeting;
import com.privasia.procurehere.core.entity.EventMeetingContact;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventNotificationService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RftEnvelopService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
//@EnableAsync
public class EventNotificationServiceImpl implements EventNotificationService {

	private static final Logger LOG = LogManager.getLogger(Global.EMAIL_LOG);

	@Autowired
	MessageSource messageSource;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Autowired
	RftSupplierTeamMemberDao rftSupplierTeamMemberDao;

	@Autowired
	RfqSupplierTeamMemberDao rfqSupplierTeamMemberDao;

	@Autowired
	RfpSupplierTeamMemberDao rfpSupplierTeamMemberDao;

	@Autowired
	RfiSupplierTeamMemberDao rfiSupplierTeamMemberDao;

	@Autowired
	RfaSupplierTeamMemberDao rfaSupplierTeamMemberDao;

	@Autowired
	UserDao userDao;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RfaEnvelopService rfaEnvelopService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	RfaEventAuditDao rfaEventAuditDao;

	@Autowired
	RfpEventAuditDao rfpEventAuditDao;

	@Autowired
	RfqEventAuditDao rfqEventAuditDao;

	@Autowired
	RftEventAuditDao rftEventAuditDao;

	@Autowired
	RfiEventAuditDao rfiEventAuditDao;

	private void sendEventClosedNotification(User user, RfxView event, String url, String timeZone, RfxTypes eventType) {
		String mailTo = user.getCommunicationEmail();
		String subject = "Event Ended";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", eventType.getValue());
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
		map.put("eventEndDateTime", df.format(event.getEventEnd()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.EVENT_CLOSED_TEMPLATE);
		}
		String notificationMessage = messageSource.getMessage("common.event.closed.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);

		if (StringUtils.checkString(user.getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + user.getCommunicationEmail() + "' and device Id :" + user.getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.FINISH_MESSAGE.toString());
				payload.put("eventType", eventType.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
		}

	}

	private void sendEnvelopOpenedNotification(User user, Event event, String url, String timeZone, String eventType, List<String> envelopTitleList, String envelopTitle) {

		String mailTo = user.getCommunicationEmail();
		String subject = "Envelope is ready to be opened";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("envelopTitle", CollectionUtil.isNotEmpty(envelopTitleList) ? envelopTitleList.toString() : envelopTitle);
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", eventType);
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		map.put("businessUnit", findBusinessUnit(event.getId(), eventType));
		if(user.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.ENVELOPE_READY_OPENED_TEMPLATE);
		}
		String notificationMessage = messageSource.getMessage("common.envelop.opened.notification.message", new Object[] { event.getReferanceNumber(), CollectionUtil.isNotEmpty(envelopTitleList) ? envelopTitleList.toString() : envelopTitle }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);

		if (StringUtils.checkString(user.getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + user.getCommunicationEmail() + "' and device Id :" + user.getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.EVENT_EVALUATION_MESSAGE.toString());
				payload.put("eventType", RfxTypes.fromString(eventType).name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
		}

	}

	private void sendEventReadyEvaluationNotification(User user, Event event, String url, String timeZone, RfxTypes eventType) {
		String subject = "Event is ready for evaluation";
		try {
			String mailTo = user.getCommunicationEmail();
			String msg = "Event \"" + event.getReferanceNumber() + "\" is ready for evaluation";
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("msg", msg);
			map.put("eventType", eventType.getValue());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_EVALUATION_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail Event is ready for evaluation : " + e.getMessage(), e);
		}
		String notificationMessage = messageSource.getMessage("common.event.ready.evaluation.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
		try {
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error while sending dashborad notification Event is ready for evaluation : " + e.getMessage(), e);
		}

		if (StringUtils.checkString(user.getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + user.getCommunicationEmail() + "' and device Id :" + user.getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.EVENT_EVALUATION_MESSAGE.toString());
				payload.put("eventType", eventType.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendActiveEventNotificationToSupplier(String timeZone, RfxView rfxView, String id, List<EventSupplierPojo> invited) {

		if (CollectionUtil.isNotEmpty(invited)) {

			LOG.info("==================================send notification===============================================>");

			for (EventSupplierPojo eventSupplierPojo : invited) {
				try {
					String mailTo = "";
					String subject = "Invitation to participate";
					String url = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + rfxView.getId();
					HashMap<String, Object> map = new HashMap<String, Object>();
					try {
						mailTo = eventSupplierPojo.getCommunicationEmail();
						map.put("userName", eventSupplierPojo.getUserName());
						map.put("event", rfxView);
						map.put("eventStatus", "upcoming");
						map.put("eventType", rfxView.getType().name());
						map.put("businessUnit", StringUtils.checkString(eventSupplierPojo.getBusinessUnit()));
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
						df.setTimeZone(TimeZone.getTimeZone(eventSupplierPojo.getTimeZone() != null ? eventSupplierPojo.getTimeZone() : timeZone));
						map.put("date", df.format(new Date()));
						map.put("appUrl", url);
						map.put("loginUrl", APP_URL + "/login");
						sendEmail(mailTo, subject, map, Global.EVENT_INVITATION_TEMPLATE);
					} catch (Exception e) {
						LOG.error("Error While sending mail For Event INVITATION :" + e.getMessage(), e);
					}
					try {
						String notificationMessage = messageSource.getMessage("event.invite.upcoming.notification.message", new Object[] { rfxView.getReferanceNumber() }, Global.LOCALE);
						sendDashboardNotification(eventSupplierPojo, url, subject, notificationMessage);
					} catch (Exception e) {
						LOG.error("Error While sending dashboard notification For Event INVITATION :" + e.getMessage(), e);
					}

					try {
						String notificationMessage = messageSource.getMessage("event.invite.upcoming.notification.message", new Object[] { rfxView.getReferanceNumber() }, Global.LOCALE);
						sendPushNotificationToSupplier(eventSupplierPojo, notificationMessage, id, rfxView.getType());
					} catch (Exception e) {
						LOG.error("Error While sending dashboard notification For Event INVITATION :" + e.getMessage(), e);
					}

					switch (rfxView.getType()) {
					case RFA:
						rfaEventSupplierDao.updateEventSuppliersNotificationFlag(eventSupplierPojo.getId());
						break;
					case RFI:
						rfiEventSupplierDao.updateEventSuppliersNotificationFlag(eventSupplierPojo.getId());
						break;
					case RFP:
						rfpEventSupplierDao.updateEventSuppliersNotificationFlag(eventSupplierPojo.getId());
						break;
					case RFQ:
						rfqEventSupplierDao.updateEventSuppliersNotificationFlag(eventSupplierPojo.getId());
						break;
					case RFT:
						rftEventSupplierDao.updateEventSuppliersNotificationFlag(eventSupplierPojo.getId());
						break;
					default:
						break;
					}
				} catch (Exception e) {
					LOG.error("Error while sending supplier invitation mail :" + e.getMessage(), e);
				}
			}

		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendRfqEventCloseNotification(RfxView rfxView) {
		List<EventSupplierPojo> suppIdList = null;
		String timeZone = "GMT+8:00";
		String eventId = rfxView.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" + eventId;

		Event event = new Event();
		event.setId(rfxView.getId());
		event.setEventName(rfxView.getEventName());
		event.setReferanceNumber(rfxView.getReferanceNumber());
		event.setEventPublishDate(rfxView.getEventPublishDate());
		event.setEventEnd(rfxView.getEventEnd());

		try {
			suppIdList = rfqEventDao.getEventSuppliersAndTimeZone(rfxView.getId());
			if (CollectionUtil.isNotEmpty(suppIdList)) {
				for (EventSupplierPojo eventSupplier : suppIdList) {
					// Sending Mails and Notifications for supplier admin users
					List<User> userList = userDao.getAllAdminPlainUsersForSupplierNotification(eventSupplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							sendEventClosedNotification(adminUser, rfxView, suppUrl, eventSupplier.getTimeZone() != null ? eventSupplier.getTimeZone() : timeZone, RfxTypes.RFQ);
						}
					}
				}
			}

			// Sending Mails and Notifications for Event owner
			User rfqEventOwner = rfqEventDao.getPlainEventOwnerByEventId(rfxView.getId());
			timeZone = getTimeZoneByBuyerSettings(rfqEventOwner, timeZone);
			sendEventClosedNotification(rfqEventOwner, rfxView, buyerUrl, timeZone, RfxTypes.RFQ);

			// Sending Mails and Notifications for event buyer team members
			List<RfqTeamMember> rfqBuyermembers = rfqEventDao.getBuyerTeamMemberByEventId(rfxView.getId());
			if (CollectionUtil.isNotEmpty(rfqBuyermembers)) {
				for (RfqTeamMember buyerTeamMember : rfqBuyermembers) {
					sendEventClosedNotification(buyerTeamMember.getUser(), rfxView, buyerUrl, timeZone, RfxTypes.RFQ);
				}
			}
			int countClosedEnvelop = rfqEnvelopService.getcountClosedEnvelop(rfxView.getId());

			if (countClosedEnvelop > 0) {
				List<RfqEnvelop> envelopList = rfqEnvelopService.getAllClosedEnvelopAndOpener(rfxView.getId());
				if (CollectionUtil.isNotEmpty(envelopList)) {
					List<String> envelopTitleList = new ArrayList<>();
					for (RfqEnvelop rfqEnvelop : envelopList) {
						sendEnvelopOpenedNotification(rfqEnvelop.getOpener(), event, buyerUrl, timeZone, RfxTypes.RFQ.getValue(), null, rfqEnvelop.getEnvelopTitle());
						envelopTitleList.add(rfqEnvelop.getEnvelopTitle());
					}
					sendEnvelopOpenedNotification(rfqEventOwner, event, buyerUrl, timeZone, RfxTypes.RFQ.getValue(), envelopTitleList, null);
				}
			} else {
				List<User> evaluatorList = rfqEnvelopService.getAllEnvelopEvaluatorUsers(rfxView.getId());
				if (CollectionUtil.isNotEmpty(evaluatorList)) {
					for (User evaluator : evaluatorList) {
						sendEventReadyEvaluationNotification(evaluator, event, buyerUrl, timeZone, RfxTypes.RFQ);
					}
				}
				sendEventReadyEvaluationNotification(rfqEventOwner, event, buyerUrl, timeZone, RfxTypes.RFQ);
			}
		} catch (Exception e) {
			LOG.error("Error while Sending notification for event active to close :" + e.getMessage(), e);
		}

	}

	private String getTimeZoneByBuyerSettings(User user, String timeZone) {
		try {
			if (StringUtils.checkString(user.getTenantId()).length() > 0) {
				String time = buyerSettingsDao.getBuyerTimeZoneByTenantId(user.getTenantId());
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
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendRfpEventCloseNotification(RfxView rfxView) {
		List<EventSupplierPojo> suppIdList = null;
		String timeZone = "GMT+8:00";
		String eventId = rfxView.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" + eventId;

		Event event = new Event();
		event.setId(rfxView.getId());
		event.setEventName(rfxView.getEventName());
		event.setReferanceNumber(rfxView.getReferanceNumber());
		event.setEventPublishDate(rfxView.getEventPublishDate());
		event.setEventEnd(rfxView.getEventEnd());

		try {
			suppIdList = rfpEventDao.getEventSuppliersAndTimeZone(eventId);
			if (CollectionUtil.isNotEmpty(suppIdList)) {
				for (EventSupplierPojo eventSupplier : suppIdList) {
					// timeZone = getTimeZoneBySupplierSettings(eventSupplier.getId(),
					// eventSupplier.getTimeZone());

					// Sending Mails and Notifications for supplier admin users
					List<User> userList = userDao.getAllAdminPlainUsersForSupplierNotification(eventSupplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							sendEventClosedNotification(adminUser, rfxView, suppUrl, eventSupplier.getTimeZone() != null ? eventSupplier.getTimeZone() : timeZone, RfxTypes.RFP);
						}
					}
				}
			}

			// Sending Mails and Notifications for Event owner
			User rfpEventOwner = rfpEventDao.getPlainEventOwnerByEventId(eventId);
			timeZone = getTimeZoneByBuyerSettings(rfpEventOwner, timeZone);
			sendEventClosedNotification(rfpEventOwner, rfxView, buyerUrl, timeZone, RfxTypes.RFP);

			// Sending Mails and Notifications for event buyer team members
			List<RfpTeamMember> rfpBuyermembers = rfpEventDao.getBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfpBuyermembers)) {
				for (RfpTeamMember buyerTeamMember : rfpBuyermembers) {
					sendEventClosedNotification(buyerTeamMember.getUser(), rfxView, buyerUrl, timeZone, RfxTypes.RFP);
				}
			}
			int countClosedEnvelop = rfpEnvelopService.getcountClosedEnvelop(eventId);

			if (countClosedEnvelop > 0) {
				List<RfpEnvelop> envelopList = rfpEnvelopService.getAllClosedEnvelopAndOpener(eventId);
				if (CollectionUtil.isNotEmpty(envelopList)) {
					List<String> envelopTitleList = new ArrayList<>();
					for (RfpEnvelop rfpEnvelop : envelopList) {
						sendEnvelopOpenedNotification(rfpEnvelop.getOpener(), event, buyerUrl, timeZone, RfxTypes.RFP.getValue(), null, rfpEnvelop.getEnvelopTitle());
						envelopTitleList.add(rfpEnvelop.getEnvelopTitle());
					}
					sendEnvelopOpenedNotification(rfpEventOwner, event, buyerUrl, timeZone, RfxTypes.RFP.getValue(), envelopTitleList, null);
				}
			} else {
				List<User> evaluatorList = rfpEnvelopService.getAllEnvelopEvaluatorUsers(eventId);
				if (CollectionUtil.isNotEmpty(evaluatorList)) {
					for (User evaluator : evaluatorList) {
						sendEventReadyEvaluationNotification(evaluator, event, buyerUrl, timeZone, RfxTypes.RFP);
					}
				}
				sendEventReadyEvaluationNotification(rfpEventOwner, event, buyerUrl, timeZone, RfxTypes.RFP);
			}
		} catch (Exception e) {
			LOG.error("Error while Sending notification for event active to close :" + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendRfiEventCloseNotification(RfxView rfxView) {
		List<EventSupplierPojo> suppIdList = null;
		String timeZone = "GMT+8:00";
		String eventId = rfxView.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" + eventId;

		Event event = new Event();
		event.setId(rfxView.getId());
		event.setEventName(rfxView.getEventName());
		event.setReferanceNumber(rfxView.getReferanceNumber());
		event.setEventPublishDate(rfxView.getEventPublishDate());
		event.setEventEnd(rfxView.getEventEnd());

		try {
			suppIdList = rfiEventDao.getEventSuppliersAndTimeZone(eventId);
			if (CollectionUtil.isNotEmpty(suppIdList)) {
				for (EventSupplierPojo eventSupplier : suppIdList) {
					// timeZone = getTimeZoneBySupplierSettings(eventSUpplier.getId(),
					// eventSUpplier.getTimeZone());

					// Sending Mails and Notifications for supplier admin users
					List<User> userList = userDao.getAllAdminPlainUsersForSupplierNotification(eventSupplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							sendEventClosedNotification(adminUser, rfxView, suppUrl, eventSupplier.getTimeZone() != null ? eventSupplier.getTimeZone() : timeZone, RfxTypes.RFI);
						}
					}
				}
			}

			// Sending Mails and Notifications for Event owner
			User rfiEventOwner = rfiEventDao.getPlainEventOwnerByEventId(eventId);
			timeZone = getTimeZoneByBuyerSettings(rfiEventOwner, timeZone);
			sendEventClosedNotification(rfiEventOwner, rfxView, buyerUrl, timeZone, RfxTypes.RFI);

			// Sending Mails and Notifications for event buyer team members
			List<RfiTeamMember> rfiBuyermembers = rfiEventDao.getBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfiBuyermembers)) {
				for (RfiTeamMember buyerTeamMember : rfiBuyermembers) {
					sendEventClosedNotification(buyerTeamMember.getUser(), rfxView, buyerUrl, timeZone, RfxTypes.RFI);
				}
			}
			int countClosedEnvelop = rfiEnvelopService.getcountClosedEnvelop(eventId);

			if (countClosedEnvelop > 0) {
				List<RfiEnvelop> envelopList = rfiEnvelopService.getAllClosedEnvelopAndOpener(eventId);
				if (CollectionUtil.isNotEmpty(envelopList)) {
					List<String> envelopTitleList = new ArrayList<>();
					for (RfiEnvelop rfiEnvelop : envelopList) {
						sendEnvelopOpenedNotification(rfiEnvelop.getOpener(), event, buyerUrl, timeZone, RfxTypes.RFI.getValue(), null, rfiEnvelop.getEnvelopTitle());
						envelopTitleList.add(rfiEnvelop.getEnvelopTitle());
					}
					sendEnvelopOpenedNotification(rfiEventOwner, event, buyerUrl, timeZone, RfxTypes.RFI.getValue(), envelopTitleList, null);
				}
			} else {
				List<User> evaluatorList = rfiEnvelopService.getAllEnvelopEvaluatorUsers(eventId);
				if (CollectionUtil.isNotEmpty(evaluatorList)) {
					for (User evaluator : evaluatorList) {
						sendEventReadyEvaluationNotification(evaluator, event, buyerUrl, timeZone, RfxTypes.RFI);
					}
				}
				sendEventReadyEvaluationNotification(rfiEventOwner, event, buyerUrl, timeZone, RfxTypes.RFI);
			}
		} catch (Exception e) {
			LOG.error("Error while Sending notification for event active to close :" + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendRftEventCloseNotification(RfxView rfxView) {
		List<EventSupplierPojo> suppIdList = null;
		String timeZone = "GMT+8:00";
		String eventId = rfxView.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" + eventId;

		Event event = new Event();
		event.setId(rfxView.getId());
		event.setEventName(rfxView.getEventName());
		event.setReferanceNumber(rfxView.getReferanceNumber());
		event.setEventPublishDate(rfxView.getEventPublishDate());
		event.setEventEnd(rfxView.getEventEnd());

		try {
			suppIdList = rftEventDao.getEventSuppliersAndTimeZone(eventId);
			if (CollectionUtil.isNotEmpty(suppIdList)) {
				for (EventSupplierPojo eventSupplier : suppIdList) {
					// timeZone = getTimeZoneBySupplierSettings(eventSupplier.getId(),
					// eventSupplier.getTimeZone());

					// Sending Mails and Notifications for supplier admin users
					List<User> userList = userDao.getAllAdminPlainUsersForSupplierNotification(eventSupplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							try {
								sendEventClosedNotification(adminUser, rfxView, suppUrl, eventSupplier.getTimeZone() != null ? eventSupplier.getTimeZone() : timeZone, RfxTypes.RFT);
							} catch (Exception e) {
								LOG.error("Error while sending nitification to : " + adminUser.getName() + ", " + e.getMessage(), e);
							}

						}
					}

				}
			}

			// Sending Mails and Notifications for Event owner
			User rftEventOwner = rftEventDao.getPlainEventOwnerByEventId(eventId);
			timeZone = getTimeZoneByBuyerSettings(rftEventOwner, timeZone);
			try {
				sendEventClosedNotification(rftEventOwner, rfxView, buyerUrl, timeZone, RfxTypes.RFT);
			} catch (Exception e) {
				LOG.error("Error while sending nitification to : " + rftEventOwner.getName() + ", " + e.getMessage(), e);
			}

			// Sending Mails and Notifications for event buyer team members
			List<RftTeamMember> rftBuyermembers = rftEventDao.getBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rftBuyermembers)) {
				for (RftTeamMember buyerTeamMember : rftBuyermembers) {
					try {
						sendEventClosedNotification(buyerTeamMember.getUser(), rfxView, buyerUrl, timeZone, RfxTypes.RFT);
					} catch (Exception e) {
						LOG.error("Error while sending nitification to : " + buyerTeamMember.getUser().getName() + ", " + e.getMessage(), e);
					}

				}
			}
			int countClosedEnvelop = rftEnvelopService.getcountClosedEnvelop(eventId);

			if (countClosedEnvelop > 0) {
				List<RftEnvelop> envelopList = rftEnvelopService.getAllClosedEnvelopAndOpener(eventId);
				if (CollectionUtil.isNotEmpty(envelopList)) {
					List<String> envelopTitleList = new ArrayList<>();
					for (RftEnvelop rftEnvelop : envelopList) {
						try {
							sendEnvelopOpenedNotification(rftEnvelop.getOpener(), event, buyerUrl, timeZone, RfxTypes.RFT.getValue(), null, rftEnvelop.getEnvelopTitle());
							envelopTitleList.add(rftEnvelop.getEnvelopTitle());
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + rftEnvelop.getOpener().getName() + ", " + e.getMessage(), e);
						}

					}
					try {
						sendEnvelopOpenedNotification(rftEventOwner, event, buyerUrl, timeZone, RfxTypes.RFT.getValue(), envelopTitleList, null);
					} catch (Exception e) {
						LOG.error("Error while sending nitification to : " + rftEventOwner.getName() + ", " + e.getMessage(), e);
					}

				}
			} else {
				List<User> evaluatorList = rftEnvelopService.getAllEnvelopEvaluatorUsers(eventId);
				if (CollectionUtil.isNotEmpty(evaluatorList)) {
					for (User evaluator : evaluatorList) {
						try {
							sendEventReadyEvaluationNotification(evaluator, event, buyerUrl, timeZone, RfxTypes.RFT);
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + evaluator.getName() + ", " + e.getMessage(), e);
						}

					}
				}
				try {
					sendEventReadyEvaluationNotification(rftEventOwner, event, buyerUrl, timeZone, RfxTypes.RFT);
				} catch (Exception e) {
					LOG.error("Error while sending nitification to : " + rftEventOwner.getName() + ", " + e.getMessage(), e);
				}

			}
		} catch (Exception e) {
			LOG.error("Error while Sending notification for event active to close :" + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendRfaEventCloseNotification(RfxView rfxView) {
		List<EventSupplierPojo> suppIdList = null;
		String timeZone = "GMT+8:00";
		String eventId = rfxView.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" + eventId;

		Event event = new Event();
		event.setId(rfxView.getId());
		event.setEventName(rfxView.getEventName());
		event.setReferanceNumber(rfxView.getReferanceNumber());
		event.setEventPublishDate(rfxView.getEventPublishDate());
		event.setEventEnd(rfxView.getEventEnd());

		try {
			suppIdList = rfaEventDao.getEventSuppliersAndTimeZone(eventId);
			if (CollectionUtil.isNotEmpty(suppIdList)) {
				for (EventSupplierPojo eventSupplier : suppIdList) {
					// timeZone = getTimeZoneBySupplierSettings(eventSupplier.getId(),
					// eventSupplier.getTimeZone());

					// Sending Mails and Notifications for supplier admin users
					List<User> userList = userDao.getAllAdminPlainUsersForSupplierNotification(eventSupplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							try {
								sendEventClosedNotification(adminUser, rfxView, suppUrl, eventSupplier.getTimeZone() != null ? eventSupplier.getTimeZone() : timeZone, RfxTypes.RFA);
							} catch (Exception e) {
								LOG.error("Error while sending nitification to : " + adminUser.getName() + ", " + e.getMessage(), e);
							}
						}
					}
				}
			}
			// Sending Mails and Notifications for Event owner
			User rfaEventOwner = rfaEventDao.getPlainEventOwnerByEventId(eventId);
			timeZone = getTimeZoneByBuyerSettings(rfaEventOwner, timeZone);
			sendEventClosedNotification(rfaEventOwner, rfxView, buyerUrl, timeZone, RfxTypes.RFA);
			// Sending Mails and Notifications for event buyer team members
			List<RfaTeamMember> rfaBuyermembers = rfaEventDao.getBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfaBuyermembers)) {
				for (RfaTeamMember buyerTeamMember : rfaBuyermembers) {
					try {
						sendEventClosedNotification(buyerTeamMember.getUser(), rfxView, buyerUrl, timeZone, RfxTypes.RFA);
					} catch (Exception e) {
						LOG.error("Error while sending nitification to : " + buyerTeamMember.getUser().getName() + ", " + e.getMessage(), e);
					}
				}
			}
			int countClosedEnvelop = rfaEnvelopService.getcountClosedEnvelop(eventId);
			if (countClosedEnvelop > 0) {
				List<RfaEnvelop> envelopList = rfaEnvelopService.getAllClosedEnvelopAndOpener(eventId);
				if (CollectionUtil.isNotEmpty(envelopList)) {
					List<String> envelopTitleList = new ArrayList<>();
					for (RfaEnvelop rfaEnvelop : envelopList) {
						try {
							sendEnvelopOpenedNotification(rfaEnvelop.getOpener(), event, buyerUrl, timeZone, RfxTypes.RFA.getValue(), null, rfaEnvelop.getEnvelopTitle());
							envelopTitleList.add(rfaEnvelop.getEnvelopTitle());
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + rfaEnvelop.getOpener().getName() + ", " + e.getMessage(), e);
						}
					}
					try {
						sendEnvelopOpenedNotification(rfaEventOwner, event, buyerUrl, timeZone, RfxTypes.RFA.getValue(), envelopTitleList, null);
					} catch (Exception e) {
						LOG.error("Error while sending nitification to : " + rfaEventOwner.getName() + ", " + e.getMessage(), e);
					}
				}
			} else {
				List<User> evaluatorList = rfaEnvelopService.getAllEnvelopEvaluatorUsers(eventId);
				if (CollectionUtil.isNotEmpty(evaluatorList)) {
					for (User evaluator : evaluatorList) {
						try {
							sendEventReadyEvaluationNotification(evaluator, event, buyerUrl, timeZone, RfxTypes.RFA);
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + evaluator.getName() + ", " + e.getMessage(), e);
						}
					}
				}
				try {
					sendEventReadyEvaluationNotification(rfaEventOwner, event, buyerUrl, timeZone, RfxTypes.RFA);
				} catch (Exception e) {
					LOG.error("Error while sending nitification to : " + rfaEventOwner.getName() + ", " + e.getMessage(), e);
				}

			}
		} catch (Exception e) {
			LOG.error("Error while Sending notification for event active to close :" + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendMeetingReminder(Event event, EventMeeting meeting, List<EventMeetingContact> contactList, User user, RfxTypes rfxType, String businessUnit) {
		String timeZone = "GMT+8:00";
		Map<String, String> mailMap = new HashMap<String, String>();

		mailMap.put("appUrl", APP_URL + "/supplier/viewSupplierEvent/" + rfxType.name() + "/" + event.getId());
		mailMap.put("eventType", rfxType.getValue());

		List<Object[]> invites = null;
		switch (rfxType) {
		case RFA:
			invites = rfaEventMeetingDao.getNotRejectedMeetingByMeetingId(meeting.getId());
			break;
		case RFI:
			invites = rfiEventMeetingDao.getNotRejectedMeetingByMeetingId(meeting.getId());
			break;
		case RFP:
			invites = rfpEventMeetingDao.getNotRejectedMeetingByMeetingId(meeting.getId());
			break;
		case RFQ:
			invites = rfqEventMeetingDao.getNotRejectedMeetingByMeetingId(meeting.getId());
			break;
		case RFT:
			invites = rftEventMeetingDao.getNotRejectedMeetingByMeetingId(meeting.getId());
			break;
		default:
			break;
		}

		LOG.info(rfxType.getValue() + " Invites Size : " + (invites != null ? invites.size() : -1));
		for (Object[] supp : invites) {
			if (supp[1] != null) {
				timeZone = (String) supp[1];
			}
			List<User> userList = userDao.getAllAdminUsersForSupplier((String) supp[0]);
			mailMap.put("timeZone", timeZone);
			for (User adminUser : userList) {
				sendMeetingReminder(adminUser, event, meeting, contactList, mailMap, rfxType, businessUnit);
			}

			// Find the Assigned team members and send them dashboard message
			List<User> members = null;
			switch (rfxType) {
			case RFA:
				members = rfaSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(event.getId(), (String) supp[0]);
				break;
			case RFI:
				members = rfiSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(event.getId(), (String) supp[0]);
				break;
			case RFP:
				members = rfpSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(event.getId(), (String) supp[0]);
				break;
			case RFQ:
				members = rfqSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(event.getId(), (String) supp[0]);
				break;
			case RFT:
				members = rftSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(event.getId(), (String) supp[0]);
				break;
			default:
				break;
			}

			if (CollectionUtil.isNotEmpty(members)) {
				for (User member : members) {
					sendMeetingReminder(member, event, meeting, contactList, mailMap, rfxType, businessUnit);
				}
			}
		}

		try {
			if (StringUtils.checkString(user.getTenantId()).length() > 0) {
				BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
				if (settings != null && settings.getTimeZone() != null && (settings.getTimeZone().getTimeZone()).length() > 0) {
					timeZone = settings.getTimeZone().getTimeZone();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		mailMap.put("appUrl", APP_URL + "/buyer/" + rfxType.name() + "/eventSummary/" + event.getId());
		mailMap.put("timeZone", timeZone);
		sendMeetingReminder(user, event, meeting, contactList, mailMap, rfxType, businessUnit);

		// Sending mails to meeting contacts
		for (EventMeetingContact eventMeetingContact : contactList) {
			sendMeetingContactReminder(eventMeetingContact, event, meeting, contactList, mailMap, rfxType, businessUnit);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendEventReminder(Event event, User eventOwner, RfxTypes rfxTypes, String businessUnit, Boolean startReminder) {
		String timeZone = "GMT+8:00";
		String eventId = event.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxTypes.name() + "/" + eventId;

		List<EventSupplierPojo> eventSuppliers = null;
		List<User> suppMembers = null;
		List<User> buyermembers = null;
		switch (rfxTypes) {
		case RFA: {
			eventSuppliers = rfaEventDao.getEventSuppliersAndTimeZone(eventId);
			suppMembers = rfaSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(eventId);
			buyermembers = rfaEventDao.getUserBuyerTeamMemberByEventId(eventId);
		}
			break;
		case RFI: {
			eventSuppliers = rfiEventDao.getEventSuppliersAndTimeZone(eventId);
			suppMembers = rfiSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(eventId);
			buyermembers = rfiEventDao.getUserBuyerTeamMemberByEventId(eventId);
		}
			break;
		case RFP: {
			eventSuppliers = rfpEventDao.getEventSuppliersAndTimeZone(eventId);
			suppMembers = rfpSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(eventId);
			buyermembers = rfpEventDao.getUserBuyerTeamMemberByEventId(eventId);
		}
			break;
		case RFQ: {
			eventSuppliers = rfqEventDao.getEventSuppliersAndTimeZone(eventId);
			suppMembers = rfqSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(eventId);
			buyermembers = rfqEventDao.getUserBuyerTeamMemberByEventId(eventId);
		}
			break;
		case RFT: {
			eventSuppliers = rftEventDao.getEventSuppliersAndTimeZone(eventId);
			suppMembers = rftSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(eventId);
			buyermembers = rftEventDao.getUserBuyerTeamMemberByEventId(eventId);
		}
			break;
		default:
			break;
		}

		if (CollectionUtil.isNotEmpty(eventSuppliers)) {
			for (EventSupplierPojo eventSupplier : eventSuppliers) {
				timeZone = eventSupplier.getTimeZone();
				// Sending Mails and Notifications for supplier admin users
				List<User> userList = userDao.getAllAdminUsersForSupplier(eventSupplier.getId());
				for (User adminUser : userList) {
					if (Boolean.TRUE == startReminder) {
						sendEventStartReminder(adminUser, event, suppUrl, eventSupplier.getTimeZone(), rfxTypes, businessUnit);
					} else {
						sendEventReminder(adminUser, event, suppUrl, eventSupplier.getTimeZone(), rfxTypes, businessUnit);
					}
				}
			}
		}

		// Sending Mails and Notifications for event supplier team members
		if (CollectionUtil.isNotEmpty(suppMembers)) {
			for (User supplierTeamMember : suppMembers) {
				if (Boolean.TRUE == startReminder) {
					sendEventStartReminder(supplierTeamMember, event, suppUrl, timeZone, rfxTypes, businessUnit);
				} else {
					sendEventReminder(supplierTeamMember, event, suppUrl, timeZone, rfxTypes, businessUnit);
				}
			}
		}

		// Sending Mails and Notifications for Event owner
		try {
			if (StringUtils.checkString(eventOwner.getTenantId()).length() > 0) {
				BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(eventOwner.getTenantId());
				if (settings != null && settings.getTimeZone() != null && (settings.getTimeZone().getTimeZone()).length() > 0) {
					timeZone = settings.getTimeZone().getTimeZone();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}

		// Buyer Related Notification
		String buyerUrl = APP_URL + "/buyer/" + rfxTypes.name() + "/eventSummary/" + eventId;
		if (Boolean.TRUE == startReminder) {
			sendEventStartReminder(eventOwner, event, buyerUrl, timeZone, rfxTypes, businessUnit);
		} else {
			sendEventReminder(eventOwner, event, buyerUrl, timeZone, rfxTypes, businessUnit);
		}

		// Sending Mails and Notifications for event buyer team members
		if (CollectionUtil.isNotEmpty(buyermembers)) {
			for (User buyerTeamMember : buyermembers) {
				if (Boolean.TRUE == startReminder) {
					sendEventStartReminder(buyerTeamMember, event, buyerUrl, timeZone, rfxTypes, businessUnit);
				} else {
					sendEventReminder(buyerTeamMember, event, buyerUrl, timeZone, RfxTypes.RFT, businessUnit);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Async
	public void sendEventStartNotification(RfxView rfxView) {
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxView.getType().name() + "/" + rfxView.getId();
		String buyerUrl = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" + rfxView.getId();
		String timeZone = "GMT+8:00";
		List<EventSupplierPojo> eventSuppliers = null;
		List<User> buyermembers = null;
		try {
			switch (rfxView.getType()) {
			case RFA:
				eventSuppliers = rfaEventDao.getEventSuppliersAndTimeZone(rfxView.getId());
				buyermembers = rfaEventDao.getUserBuyerTeamMemberByEventId(rfxView.getId());
				break;
			case RFI:
				eventSuppliers = rfiEventDao.getEventSuppliersAndTimeZone(rfxView.getId());
				buyermembers = rfiEventDao.getUserBuyerTeamMemberByEventId(rfxView.getId());
				break;
			case RFP:
				eventSuppliers = rfpEventDao.getEventSuppliersAndTimeZone(rfxView.getId());
				buyermembers = rfpEventDao.getUserBuyerTeamMemberByEventId(rfxView.getId());
				break;
			case RFQ:
				eventSuppliers = rfqEventDao.getEventSuppliersAndTimeZone(rfxView.getId());
				buyermembers = rfqEventDao.getUserBuyerTeamMemberByEventId(rfxView.getId());
				break;
			case RFT:
				eventSuppliers = rftEventDao.getEventSuppliersAndTimeZone(rfxView.getId());
				buyermembers = rftEventDao.getUserBuyerTeamMemberByEventId(rfxView.getId());

				break;
			default:
				break;
			}

			if (CollectionUtil.isNotEmpty(eventSuppliers)) {
				for (EventSupplierPojo eventSupplier : eventSuppliers) {
					timeZone = eventSupplier.getTimeZone();

					// Sending Mails and Notifications for supplier admin users
					List<User> userList = userDao.getAllAdminPlainUsersForSupplierNotification(eventSupplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							sendEventStartNotificationSupplier(adminUser, rfxView, suppUrl, timeZone, rfxView.getType());
						}
					}
					timeZone = "GMT+8:00";
				}

			}

			// Sending Mails and Notifications for Event owner
			// User rfaEventOwner = rfxView//rfaEventDao.getPlainEventOwnerByEventId(rfxView.getId());
			timeZone = getTimeZoneByBuyerSettings(rfxView.getEventOwner(), timeZone);
			sendEventStartNotification(rfxView.getEventOwner(), rfxView, buyerUrl, timeZone, rfxView.getType());

			// Sending Mails and Notifications for event buyer team members

			if (CollectionUtil.isNotEmpty(buyermembers)) {
				for (User buyerTeamMember : buyermembers) {
					sendEventStartNotification(buyerTeamMember, rfxView, buyerUrl, timeZone, rfxView.getType());
				}
			}

		} catch (Exception e) {
			LOG.error("Error while sending Notification on event start :" + e.getMessage(), e);
		}
		try {
			switch (rfxView.getType()) {
			case RFA:
				RfaEvent rfaEvent = new RfaEvent();
				rfaEvent.setId(rfxView.getId());
				RfaEventAudit rfaAudit = new RfaEventAudit(rfaEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
				rfaEventAuditDao.save(rfaAudit);
				break;
			case RFI:
				RfiEvent rfiEvent = new RfiEvent();
				rfiEvent.setId(rfxView.getId());
				RfiEventAudit rfiAudit = new RfiEventAudit(rfiEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
				rfiEventAuditDao.save(rfiAudit);

				break;
			case RFP:
				RfpEvent rfpEvent = new RfpEvent();
				rfpEvent.setId(rfxView.getId());
				RfpEventAudit rfpAudit = new RfpEventAudit(rfpEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
				rfpEventAuditDao.save(rfpAudit);

				break;
			case RFQ:
				RfqEvent rfqEvent = new RfqEvent();
				rfqEvent.setId(rfxView.getId());
				RfqEventAudit rfqAudit = new RfqEventAudit(rfqEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
				rfqEventAuditDao.save(rfqAudit);

				break;
			case RFT:
				RftEvent rftEvent = new RftEvent();
				rftEvent.setId(rfxView.getId());
				RftEventAudit rftAudit = new RftEventAudit(rftEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
				rftEventAuditDao.save(rftAudit);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error While audit event start :" + e.getMessage(), e);
		}
	}

	private void sendEventStartNotification(User user, RfxView event, String url, String timeZone, RfxTypes eventType) {
		String subject = "";
		try {
			String mailTo = user.getCommunicationEmail();
			subject = "Event Started";
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType.name());
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("eventStartDateTime", df.format(event.getEventStart()));
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_START_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Event Start mail :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("common.event.start.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error while sending Event Start dash board Notification :" + e.getMessage(), e);
		}
	}

	private void sendEventStartNotificationSupplier(User user, RfxView event, String url, String timeZone, RfxTypes eventType) {

		String subject = "";
		try {
			String mailTo = user.getCommunicationEmail();
			subject = "Event Started";
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType.name());
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("eventStartDateTime", df.format(event.getEventStart()));
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_START_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Event Start mail :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("common.event.start.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error while sending Event Start dash board Notification :" + e.getMessage(), e);
		}
		try {
			if (StringUtils.checkString(event.getReferanceNumber()).length() > 0) {
				String notificationMessage = messageSource.getMessage("common.event.start.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
				sendPushNotificationToSupplier(user, notificationMessage, event, eventType);
			}
		} catch (Exception e) {
			LOG.error("Error While sending Supplier notification For Event START :" + e.getMessage(), e);
		}
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		try {
			notificationService.sendEmail(mailTo, subject, map, template);
		} catch (Exception e) {
			LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
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

	private void sendDashboardNotification(EventSupplierPojo messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(new User(messageTo.getUserId()));
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	private String findBusinessUnit(String eventId, RfxTypes rfxTypes) {
		String displayName = null;
		switch (rfxTypes) {
		case RFA:
			displayName = rfaEventDao.findBusinessUnitName(eventId);
			break;
		case RFI:
			displayName = rfiEventDao.findBusinessUnitName(eventId);
			break;
		case RFP:
			displayName = rfpEventDao.findBusinessUnitName(eventId);
			break;
		case RFQ:
			displayName = rfqEventDao.findBusinessUnitName(eventId);
			break;
		case RFT:
			displayName = rftEventDao.findBusinessUnitName(eventId);
			break;
		default:
			break;
		}
		return displayName;
	}

	private String findBusinessUnit(String eventId, String eventType) {
		String displayName = null;
		RfxTypes rfxTypes = RfxTypes.fromString(eventType);
		switch (rfxTypes) {
		case RFA:
			displayName = rfaEventDao.findBusinessUnitName(eventId);
			break;
		case RFI:
			displayName = rfiEventDao.findBusinessUnitName(eventId);
			break;
		case RFP:
			displayName = rfpEventDao.findBusinessUnitName(eventId);
			break;
		case RFQ:
			displayName = rfqEventDao.findBusinessUnitName(eventId);
			break;
		case RFT:
			displayName = rftEventDao.findBusinessUnitName(eventId);
			break;
		default:
			break;
		}
		return StringUtils.checkString(displayName);
	}

	private void sendPushNotificationToSupplier(EventSupplierPojo user, String notificationMessage, String eventId, RfxTypes eventType) {

		if (StringUtils.checkString(user.getDiviceId()).length() > 0) {
			try {
				LOG.info("User '" + user.getCommunicationEmail() + "' and device Id :" + user.getDiviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", eventId);
				payload.put("messageType", NotificationType.EVENT_EVALUATION_MESSAGE.toString());
				payload.put("eventType", eventType.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getDiviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
		}
	}

	private void sendPushNotificationToSupplier(User user, String notificationMessage, Event event, RfxTypes eventType) {
		if (StringUtils.checkString(user.getDeviceId()).length() > 0) {
			try {
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.START_MESSAGE.toString());
				payload.put("eventType", eventType.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
		}
	}

	private void sendMeetingReminder(User user, Event event, EventMeeting meeting, List<EventMeetingContact> contactList, Map<String, String> mailMap, RfxTypes type, String businessUnit) {
		String mailTo = user.getCommunicationEmail();

		String subject = "Meeting Reminder";
		String url = mailMap.get("appUrl");
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(mailMap.get("timeZone")));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", mailMap.get("eventType"));
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("meetingType", meeting.getMeetingType());
		map.put("businessUnit", StringUtils.checkString(businessUnit));
		map.put("appointmentDateTime", df.format(meeting.getAppointmentDateTime()));
		map.put("contactPerson", contactList);
		map.put("venue", meeting.getVenue());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.MEETING_REMINDER_TEMPLATE);
		}
		String notificationMessage = messageSource.getMessage("common.reminder.notification.message", new Object[] { meeting.getTitle(), event.getEventName() }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);
	}

	private void sendMeetingContactReminder(EventMeetingContact meetingContact, Event event, EventMeeting meeting, List<EventMeetingContact> contactList, Map<String, String> mailMap, RfxTypes type, String businessUnit) {
		String mailTo = meetingContact.getContactEmail();
		if (StringUtils.checkString(mailTo).length() > 0 && StringUtils.checkString(meetingContact.getContactName()).length() > 0) {
			String subject = "Meeting Reminder";
			String url = mailMap.get("appUrl");
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(TimeZone.getTimeZone(mailMap.get("timeZone")));
			map.put("date", df.format(new Date()));
			map.put("userName", meetingContact.getContactName());
			map.put("eventType", mailMap.get("eventType"));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("meetingType", meeting.getMeetingType());
			map.put("appointmentDateTime", df.format(meeting.getAppointmentDateTime()));
			map.put("contactPerson", contactList);
			map.put("businessUnit", StringUtils.checkString(businessUnit));
			map.put("venue", meeting.getVenue());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			sendEmail(mailTo, subject, map, Global.MEETING_REMINDER_TEMPLATE);
		}
	}

	private void sendEventStartReminder(User user, Event event, String url, String timeZone, RfxTypes eventType, String businessUnit) {
		String mailTo = user.getCommunicationEmail();
		String subject = "Event Start Reminder";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", eventType.name());
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("businessUnit", StringUtils.checkString(businessUnit));
		map.put("eventStartDateTime", df.format(event.getEventStart()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.EVENT_START_REMINDER_TEMPLATE);
		}
		String notificationMessage = messageSource.getMessage("common.eventStart.reminder.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);
	}

	private void sendEventReminder(User user, Event event, String url, String timeZone, RfxTypes eventType, String businessUnit) {
		String mailTo = user.getCommunicationEmail();
		String subject = "";
		if (eventType != RfxTypes.RFA) {
			subject = "Event End Reminder";
		} else {
			subject = "Event Reminder";
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", eventType.name());
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("eventEndDateTime", df.format(event.getEventEnd()));
		map.put("businessUnit", StringUtils.checkString(businessUnit));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.EVENT_REMINDER_TEMPLATE);
		}
		String notificationMessage = messageSource.getMessage("common.event.reminder.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);
	}

}
