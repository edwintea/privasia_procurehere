package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.SupplierEventDetailDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.SupplierEventDetailService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.Builder;

import freemarker.template.Configuration;

@Service
@Transactional(readOnly = true)
public class SupplierEventDetailServiceImpl implements SupplierEventDetailService {

	protected static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	SupplierEventDetailDao supplierEventDetailDao;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	UserDao userDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftSupplierTeamMemberDao rftSupplierTeamMemberDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaSupplierTeamMemberDao rfaSupplierTeamMemberDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfiSupplierTeamMemberDao rfiSupplierTeamMemberDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpSupplierTeamMemberDao rfpSupplierTeamMemberDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfqSupplierTeamMemberDao rfqSupplierTeamMemberDao;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RftEventService rftEventService;

	@Override
	public List<RftBqItem> getAllBqItemsbyBqId(String bqId) {
		List<RftBqItem> rftBqItem = rftBqItemDao.getListBqItemsbyId(bqId);
		if (CollectionUtil.isNotEmpty(rftBqItem)) {
			for (RftBqItem rftBqItem2 : rftBqItem) {
				rftBqItem2.getChildren();
			}
		}

		return rftBqItem;
	}

	@Override
	@Transactional(readOnly = false)
	public void sendAcceptOrRejectNotifications(Event event, RfxTypes rfxType, String supplierCompanyName, boolean accepted, User loggedInUser) {
		LOG.info("company name :" + supplierCompanyName);
		String timeZone = "GMT+8:00";
		String eventId = event.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + rfxType.name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + rfxType.name() + "/eventSummary/" + eventId;
		String msg = "";
		if (accepted) {
			msg = messageSource.getMessage("common.supplier.event.accepted.notification.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
		} else {
			msg = messageSource.getMessage("common.supplier.event.rejected.notification.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
		}
		// sending event owner notifications
		User eventOwner = event.getCreatedBy().createStripCopy();
		timeZone = getTimeZoneByBuyerSettings(eventOwner, timeZone);
		sendAcceptOrRejectMail(eventOwner, supplierCompanyName, event, buyerUrl, timeZone, rfxType, msg, accepted);

		switch (rfxType) {
		case RFA:
			// send event notifications for buyer team members
			List<RfaTeamMember> rfaBuyermembers = ((RfaEvent) event).getTeamMembers();
			if (CollectionUtil.isNotEmpty(rfaBuyermembers)) {
				for (RfaTeamMember buyerTeamMember : rfaBuyermembers) {
					sendAcceptOrRejectMail(buyerTeamMember.getUser(), supplierCompanyName, event, buyerUrl, timeZone, rfxType, msg, accepted);
				}
			}

			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);

			// Sending Mails and Notifications for supplier admin users
			List<User> rfaAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfaAdminSuppList) {
				if (adminUser.getId().equals(loggedInUser.getId())) {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					}
				} else {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					}
				}
				sendAcceptOrRejectMail(loggedInUser, supplierCompanyName, event, suppUrl, timeZone, rfxType, msg, accepted);
			}
			break;
		case RFI:
			// send event notifications for buyer team members
			List<RfiTeamMember> rfiBuyermembers = ((RfiEvent) event).getTeamMembers();
			if (CollectionUtil.isNotEmpty(rfiBuyermembers)) {
				for (RfiTeamMember buyerTeamMember : rfiBuyermembers) {
					sendAcceptOrRejectMail(buyerTeamMember.getUser(), supplierCompanyName, event, buyerUrl, timeZone, rfxType, msg, accepted);
				}
			}
			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);

			// Sending Mails and Notifications for supplier admin users
			List<User> rfiAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfiAdminSuppList) {
				if (adminUser.getId().equals(loggedInUser.getId())) {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					}
				} else {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					}
				}
				sendAcceptOrRejectMail(loggedInUser, supplierCompanyName, event, suppUrl, timeZone, rfxType, msg, accepted);
			}
			break;
		case RFP:
			// send event notifications for buyer team members
			List<RfpTeamMember> rfpBuyermembers = ((RfpEvent) event).getTeamMembers();
			if (CollectionUtil.isNotEmpty(rfpBuyermembers)) {
				for (RfpTeamMember buyerTeamMember : rfpBuyermembers) {
					sendAcceptOrRejectMail(buyerTeamMember.getUser(), supplierCompanyName, event, buyerUrl, timeZone, rfxType, msg, accepted);
				}
			}

			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);

			// Sending Mails and Notifications for supplier admin users
			List<User> rfpAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfpAdminSuppList) {
				if (adminUser.getId().equals(loggedInUser.getId())) {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					}
				} else {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					}
				}
				sendAcceptOrRejectMail(loggedInUser, supplierCompanyName, event, suppUrl, timeZone, rfxType, msg, accepted);
			}
			break;
		case RFQ:
			// send event notifications for buyer team members
			List<RfqTeamMember> rfqBuyermembers = ((RfqEvent) event).getTeamMembers();
			if (CollectionUtil.isNotEmpty(rfqBuyermembers)) {
				for (RfqTeamMember buyerTeamMember : rfqBuyermembers) {
					sendAcceptOrRejectMail(buyerTeamMember.getUser(), supplierCompanyName, event, buyerUrl, timeZone, rfxType, msg, accepted);
				}
			}

			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);

			// Sending Mails and Notifications for supplier admin users
			List<User> rfqAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfqAdminSuppList) {
				if (adminUser.getId().equals(loggedInUser.getId())) {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					}
				} else {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					}
				}
				sendAcceptOrRejectMail(loggedInUser, supplierCompanyName, event, suppUrl, timeZone, rfxType, msg, accepted);
			}
			break;
		case RFT:
			// send event notifications for buyer team members
			List<RftTeamMember> rftBuyermembers = ((RftEvent) event).getTeamMembers();
			if (CollectionUtil.isNotEmpty(rftBuyermembers)) {
				for (RftTeamMember buyerTeamMember : rftBuyermembers) {
					sendAcceptOrRejectMail(buyerTeamMember.getUser(), supplierCompanyName, event, buyerUrl, timeZone, rfxType, msg, accepted);
				}
			}

			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);

			// Sending Mails and Notifications for supplier admin users
			List<User> rftAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rftAdminSuppList) {
				if (adminUser.getId().equals(loggedInUser.getId())) {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.message", new Object[] { event.getEventName() }, Global.LOCALE);
					}
				} else {
					if (accepted) {
						msg = messageSource.getMessage("common.event.accepted.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					} else {
						msg = messageSource.getMessage("common.event.rejected.notification.supplier.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
					}
				}
				sendAcceptOrRejectMail(adminUser, supplierCompanyName, event, suppUrl, timeZone, rfxType, msg, accepted);
			}
			break;
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage, boolean accepted) {
		try {
			NotificationMessage message = new NotificationMessage();
			message.setCreatedBy(null);
			message.setCreatedDate(new Date());
			message.setMessage(notificationMessage);
			message.setNotificationType(accepted ? NotificationType.ACCEPT_MESSAGE : NotificationType.REJECT_MESSAGE);
			message.setMessageTo(messageTo);
			message.setSubject(subject);
			message.setTenantId(messageTo.getTenantId());
			message.setUrl(url);
			dashboardNotificationService.save(message);
		} catch (Exception e) {
			LOG.error("Error while saving dashboard notification :" + e.getMessage(), e);
		}
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		notificationService.sendEmail(mailTo, subject, map, template);
	}

	private void sendAcceptOrRejectMail(User user, String supplierName, Event event, String url, String timeZone, RfxTypes eventType, String msg, boolean accepted) {
		String mailTo = "";
		String subject = "Supplier " + (accepted ? "Accepted" : "Rejected") + " Event";

		HashMap<String, Object> map;
		map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType.getValue());
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
			map.put("eventName", event.getEventName());
			map.put("msg", msg);
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			sendDashboardNotification(user, url, subject, msg, accepted);
		} catch (Exception e) {
			LOG.error("Error While sending dashboard notification SUPPLIER EVENT ACCEPTED REJECTED : " + e.getMessage(), e);
		}
		try {
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.SUPPLIER_EVENT_ACCEPTED_REJECTED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending email notification SUPPLIER EVENT ACCEPTED REJECTED : " + e.getMessage(), e);
		}
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

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
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

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
	private String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier setting time zone :" + e.getMessage(), e);
		}
		return timeZone;

	}

	@Override
	@Transactional(readOnly = false)
	public void sendSubmissionNotifications(Event event, RfxTypes eventType, String supplierCompanyName, boolean isRevisedBQ, User loggedInUser) {
		LOG.info("company name ****************************************************************** :" + supplierCompanyName);
		String timeZone = "GMT+8:00";
		String eventId = event.getId();
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId;
		String buyerUrl = APP_URL + "/buyer/" + eventType.name() + "/eventSummary/" + eventId;
		String msg = "";
		if (isRevisedBQ) {
			msg = messageSource.getMessage("common.event.finish.revised.notification.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
		} else {
			msg = messageSource.getMessage("common.event.finish.notification.message", new Object[] { supplierCompanyName, event.getEventName() }, Global.LOCALE);
		}
		// sending event owner notifications
		User eventOwner = event.getCreatedBy().createStripCopy();
		timeZone = getTimeZoneByBuyerSettings(eventOwner, timeZone);
		if(eventOwner.getEmailNotifications()) {
			sendSubmissionMail(eventOwner, event, buyerUrl, timeZone, eventType, msg, isRevisedBQ);
		}
		switch (eventType) {
		case RFA:
			// send event notifications for buyer team members
			List<User> rfaBuyerMembers = rfaEventDao.getUserBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfaBuyerMembers)) {
				for (User buyerTeamUser : rfaBuyerMembers) {
					sendSubmissionMail(buyerTeamUser, event, buyerUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			if (isRevisedBQ) {
				msg = messageSource.getMessage("common.event.finish.revised.message", new Object[] { event.getEventName() }, Global.LOCALE);
			} else {
				msg = messageSource.getMessage("common.event.finish.message", new Object[] { event.getEventName() }, Global.LOCALE);
			}
			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);
			// Sending Mails and Notifications for supplier team members
			List<User> rfaSupplierMembers = rfaSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(eventId, loggedInUser.getSupplier().getId());
			for (User supplierTeamUser : rfaSupplierMembers) {
				sendSubmissionMail(supplierTeamUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			// Sending Mails and Notifications for supplier admin users
			List<User> rfaAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfaAdminSuppList) {
				sendSubmissionMail(adminUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			break;
		case RFI:
			// send event notifications for buyer team members
			List<User> rfiBuyerMembers = rfiEventDao.getUserBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfiBuyerMembers)) {
				for (User buyerTeamUser : rfiBuyerMembers) {
					sendSubmissionMail(buyerTeamUser, event, buyerUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);
			if (isRevisedBQ) {
				msg = messageSource.getMessage("common.event.finish.revised.message", new Object[] { event.getEventName() }, Global.LOCALE);
			} else {
				msg = messageSource.getMessage("common.event.finish.message", new Object[] { event.getEventName() }, Global.LOCALE);
			}
			// Sending Mails and Notifications for supplier team members
			List<User> rfiSupplierMembers = rfiSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
			for (User supplierTeamUser : rfiSupplierMembers) {
				sendSubmissionMail(supplierTeamUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			// Sending Mails and Notifications for supplier admin users
			List<User> rfiAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfiAdminSuppList) {
				sendSubmissionMail(adminUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			break;
		case RFP:
			// send event notifications for buyer team members
			List<User> rfpBuyerMembers = rfpEventDao.getUserBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfpBuyerMembers)) {
				for (User buyerTeamUser : rfpBuyerMembers) {
					sendSubmissionMail(buyerTeamUser, event, buyerUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			if (isRevisedBQ) {
				msg = messageSource.getMessage("common.event.finish.revised.message", new Object[] { event.getEventName() }, Global.LOCALE);
			} else {
				msg = messageSource.getMessage("common.event.finish.message", new Object[] { event.getEventName() }, Global.LOCALE);
			}
			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);
			// Sending Mails and Notifications for supplier team members
			List<User> rfpSupplierMembers = rfpSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
			for (User supplierTeamUser : rfpSupplierMembers) {
				if (supplierTeamUser.getId().equals(loggedInUser.getId())) {
					sendSubmissionMail(supplierTeamUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			// Sending Mails and Notifications for supplier admin users
			List<User> rfpAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfpAdminSuppList) {
				sendSubmissionMail(adminUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			break;
		case RFQ:
			// send event notifications for buyer team members
			List<User> rfqBuyerMembers = rfqEventDao.getUserBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rfqBuyerMembers)) {
				for (User buyerTeamUser : rfqBuyerMembers) {
					sendSubmissionMail(buyerTeamUser, event, buyerUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			if (isRevisedBQ) {
				msg = messageSource.getMessage("common.event.finish.revised.message", new Object[] { event.getEventName() }, Global.LOCALE);
			} else {
				msg = messageSource.getMessage("common.event.finish.message", new Object[] { event.getEventName() }, Global.LOCALE);
			}
			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);
			// Sending Mails and Notifications for supplier team members
			List<User> rfqSupplierMembers = rfqSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
			for (User supplierTeamUser : rfqSupplierMembers) {
				sendSubmissionMail(supplierTeamUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			// Sending Mails and Notifications for supplier admin users
			List<User> rfqAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			for (User adminUser : rfqAdminSuppList) {
				sendSubmissionMail(adminUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
			}
			break;
		case RFT:
			// send event notifications for buyer team members
			List<User> rftBuyerMembers = rftEventDao.getUserBuyerTeamMemberByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rftBuyerMembers)) {
				for (User buyerTeamUser : rftBuyerMembers) {
					sendSubmissionMail(buyerTeamUser, event, buyerUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			if (isRevisedBQ) {
				msg = messageSource.getMessage("common.event.finish.revised.message", new Object[] { event.getEventName() }, Global.LOCALE);
			} else {
				msg = messageSource.getMessage("common.event.finish.message", new Object[] { event.getEventName() }, Global.LOCALE);
			}
			timeZone = getTimeZoneBySupplierSettings(loggedInUser.getSupplier().getId(), timeZone);
			// Sending Mails and Notifications for supplier team members
			List<User> rftSupplierMembers = rftSupplierTeamMemberDao.getUserSupplierTeamMemberByEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(rftSupplierMembers)) {
				for (User supplierTeamUser : rftSupplierMembers) {
					sendSubmissionMail(supplierTeamUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			// Sending Mails and Notifications for supplier admin users
			List<User> rftAdminSuppList = userDao.getAllAdminPlainUsersForSupplier(loggedInUser.getSupplier().getId());
			if (CollectionUtil.isNotEmpty(rftAdminSuppList)) {
				for (User adminUser : rftAdminSuppList) {
					sendSubmissionMail(adminUser, event, suppUrl, timeZone, eventType, msg, isRevisedBQ);
				}
			}
			break;
		default:
			break;
		}

	}

	private void sendSubmissionMail(User user, Event event, String url, String timeZone, RfxTypes eventType, String msg, boolean isRevisedBQ) {
		String mailTo = "";
		String subject = "Supplier Completed" + (isRevisedBQ ? " Revised" : "") + " Submission";

		HashMap<String, Object> map;
		map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType.getValue());
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
			map.put("msg", msg);
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			sendSubmissionDashboardNotification(user, url, subject, msg);
		} catch (Exception e) {
			LOG.error("Error while sending dashboard notification EVENT SUBMISSION :" + e.getMessage(), e);
		}

		try {
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.SUPPLIER_EVENT_SUBMISSION_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail notification EVENT SUBMISSION :" + e.getMessage(), e);
		}
	}

	private void sendSubmissionDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		try {
			NotificationMessage message = new NotificationMessage();
			message.setCreatedBy(null);
			message.setCreatedDate(new Date());
			message.setMessage(notificationMessage);
			message.setNotificationType(NotificationType.FINISH_MESSAGE);
			message.setMessageTo(messageTo);
			message.setSubject(subject);
			message.setTenantId(messageTo.getTenantId());
			message.setUrl(url);
			dashboardNotificationService.save(message);
		} catch (Exception e) {
			LOG.error("Error while saving dashboard notification :" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentIntentPojo initiateStripePayment(String paymentType, String eventId, RfxTypes eventType, String email) throws Exception, StripeException {
		try {
			LOG.info("Initiated payment for event:" + eventId);
			PaymentIntentCreateParams params = null;
			BigDecimal amount = null;
			String eventDescriptionId = null;
			String currencyCode = null;
			String apiKey = null;
			EventSupplier eventSupplier = null;

			switch (eventType) {
			case RFA:
				LOG.info("Searching in RFA.... " + eventId);
				RfaEvent existingRfaEvent = rfaEventDao.getEventDetailsForFeePayment(eventId);
				amount = existingRfaEvent.getParticipationFees();
				currencyCode = existingRfaEvent.getParticipationFeeCurrency().getCurrencyCode();
				eventDescriptionId = existingRfaEvent.getEventId();
				BuyerSettings rfaSettings = buyerSettingsDao.getBuyerSettingsByTenantId(existingRfaEvent.getCreatedBy().getBuyer().getId());
				eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				// PH-1853 production issue for slef invited suppliers
				if (eventSupplier == null && existingRfaEvent.getEventVisibility() != EventVisibilityType.PRIVATE) {
					RfaEventSupplier newSupplier = new RfaEventSupplier();
					newSupplier.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					newSupplier.setRfxEvent(existingRfaEvent);
					newSupplier.setSelfInvited(Boolean.TRUE);
					eventSupplier = rfaEventSupplierService.saveRfaEventSupplier(newSupplier);
				}

				apiKey = rfaSettings.getStripeSecretKey();
				break;
			case RFI:
				LOG.info("Searching in RFI.... " + eventId);
				RfiEvent existingRfiEvent = rfiEventDao.getEventDetailsForFeePayment(eventId);
				amount = existingRfiEvent.getParticipationFees();
				currencyCode = existingRfiEvent.getParticipationFeeCurrency().getCurrencyCode();
				eventDescriptionId = existingRfiEvent.getEventId();
				BuyerSettings rfiSettings = buyerSettingsDao.getBuyerSettingsByTenantId(existingRfiEvent.getCreatedBy().getBuyer().getId());
				eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null && existingRfiEvent.getEventVisibility() != EventVisibilityType.PRIVATE) {
					RfiEventSupplier newSupplier = new RfiEventSupplier();
					newSupplier.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					newSupplier.setRfxEvent(existingRfiEvent);
					newSupplier.setSelfInvited(Boolean.TRUE);
					eventSupplier = rfiEventSupplierService.saveRfiEventSupplier(newSupplier);
				}

				apiKey = rfiSettings.getStripeSecretKey();
				break;
			case RFP:
				LOG.info("Searching in RFP.... " + eventId);
				RfpEvent existingRfpEvent = rfpEventDao.getEventDetailsForFeePayment(eventId);
				amount = existingRfpEvent.getParticipationFees();
				currencyCode = existingRfpEvent.getParticipationFeeCurrency().getCurrencyCode();
				BuyerSettings rfpSettings = buyerSettingsDao.getBuyerSettingsByTenantId(existingRfpEvent.getCreatedBy().getBuyer().getId());
				eventDescriptionId = existingRfpEvent.getEventId();
				eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null && existingRfpEvent.getEventVisibility() != EventVisibilityType.PRIVATE) {
					RfpEventSupplier newSupplier = new RfpEventSupplier();
					newSupplier.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					newSupplier.setRfxEvent(existingRfpEvent);
					newSupplier.setSelfInvited(Boolean.TRUE);
					eventSupplier = rfpEventSupplierService.saveRfpEventSupplier(newSupplier);
				}

				apiKey = rfpSettings.getStripeSecretKey();
				break;
			case RFQ:
				LOG.info("Searching in RFQ.... " + eventId);
				RfqEvent existingRfqEvent = rfqEventDao.getEventDetailsForFeePayment(eventId);
				amount = existingRfqEvent.getParticipationFees();
				currencyCode = existingRfqEvent.getParticipationFeeCurrency().getCurrencyCode();
				BuyerSettings rfqSettings = buyerSettingsDao.getBuyerSettingsByTenantId(existingRfqEvent.getCreatedBy().getBuyer().getId());
				eventDescriptionId = existingRfqEvent.getEventId();
				eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null && existingRfqEvent.getEventVisibility() != EventVisibilityType.PRIVATE) {
					RfqEventSupplier newSupplier = new RfqEventSupplier();
					newSupplier.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					newSupplier.setRfxEvent(existingRfqEvent);
					newSupplier.setSelfInvited(Boolean.TRUE);
					eventSupplier = rfqEventSupplierService.saveRfqEventSupplier(newSupplier);
				}

				apiKey = rfqSettings.getStripeSecretKey();
				break;
			case RFT:
				LOG.info("Searching in RFT.... " + eventId);
				RftEvent existingRftEvent = rftEventDao.getEventDetailsForFeePayment(eventId);
				amount = existingRftEvent.getParticipationFees();
				currencyCode = existingRftEvent.getParticipationFeeCurrency().getCurrencyCode();
				eventDescriptionId = existingRftEvent.getEventId();
				BuyerSettings rftSettings = buyerSettingsDao.getBuyerSettingsByTenantId(existingRftEvent.getCreatedBy().getBuyer().getId());
				eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null && existingRftEvent.getEventVisibility() != EventVisibilityType.PRIVATE) {
					RftEventSupplier newSupplier = new RftEventSupplier();
					newSupplier.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					newSupplier.setRfxEvent(existingRftEvent);
					newSupplier.setSelfInvited(Boolean.TRUE);
					eventSupplier = rftEventSupplierService.saveRftEventSupplier(newSupplier);
				}

				apiKey = rftSettings.getStripeSecretKey();
				break;
			default:
				break;
			}
			Stripe.apiKey = apiKey;
			if (StringUtils.isBlank(apiKey)) {
				throw new ApplicationException("No Stripe details found. Please update the stripe details in the buyer profile.");
			}

			String description = "Participation Fee: " + currencyCode + " " + amount + " for Event ID: " + eventDescriptionId + " by " + eventSupplier.getSupplier().getCompanyName();

			if (StringUtils.equals(paymentType, "fpx")) {
				Builder b = PaymentIntentCreateParams.builder().setAmount(amount.multiply(new BigDecimal(100)).longValue()).setCurrency("myr").setDescription(description).addPaymentMethodType("fpx").setReceiptEmail(email);
				if (StringUtils.isNotBlank(email)) {
					b.setReceiptEmail(email);
				}
				params = b.build();
			} else if (StringUtils.equals(paymentType, "card")) {
				Builder b = PaymentIntentCreateParams.builder().setCurrency(currencyCode).setAmount(amount.multiply(new BigDecimal(100)).longValue()).setDescription(description).addPaymentMethodType("card").setReceiptEmail(email);
				if (StringUtils.isNotBlank(email)) {
					b.setReceiptEmail(email);
				}
				params = b.build();
			}
			PaymentIntent paymentIntent = PaymentIntent.create(params);
			PaymentIntentPojo response = new PaymentIntentPojo();
			response.setId(paymentIntent.getId());
			response.setClientSecret(paymentIntent.getClientSecret());
			response.setAmount(paymentIntent.getAmount());
			response.setCreatedAt(paymentIntent.getCreated());

			switch (eventType) {
			case RFA:
				RfaEventSupplier existingRfaSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUser().getSupplier().getId(), eventId);
				existingRfaSupplier.setFeeReference(paymentIntent.getId());
				existingRfaSupplier.setFeeReferenceClientId(paymentIntent.getClientSecret());
				rfaEventSupplierService.updateRfaEventSuppliers(existingRfaSupplier);
				break;
			case RFI:
				RfiEventSupplier existingRfiSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUser().getSupplier().getId(), eventId);
				existingRfiSupplier.setFeeReference(paymentIntent.getId());
				existingRfiSupplier.setFeeReferenceClientId(paymentIntent.getClientSecret());
				rfiEventSupplierService.updateEventSuppliers(existingRfiSupplier);
				break;
			case RFP:
				RfpEventSupplier existingRfpSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUser().getSupplier().getId(), eventId);
				existingRfpSupplier.setFeeReference(paymentIntent.getId());
				existingRfpSupplier.setFeeReferenceClientId(paymentIntent.getClientSecret());
				rfpEventSupplierService.updateEventSuppliers(existingRfpSupplier);
				break;
			case RFQ:
				RfqEventSupplier existingRfqSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUser().getSupplier().getId(), eventId);
				existingRfqSupplier.setFeeReference(paymentIntent.getId());
				existingRfqSupplier.setFeeReferenceClientId(paymentIntent.getClientSecret());
				rfqEventSupplierService.updateEventSuppliers(existingRfqSupplier);
				break;
			case RFT:
				RftEventSupplier existingRftSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUser().getSupplier().getId(), eventId);
				existingRftSupplier.setFeeReference(paymentIntent.getId());
				existingRftSupplier.setFeeReferenceClientId(paymentIntent.getClientSecret());
				rftEventSupplierService.updateEventSuppliers(existingRftSupplier);
				break;
			default:
				break;
			}

			LOG.info("Completed payment initiation for event:" + eventId);
			return response;
		} catch (StripeException e) {
			LOG.info("Got Stripe exception.....", e);
			throw new ApplicationException(e.getLocalizedMessage());
		} catch (Exception e) {
			LOG.info("Got Default exception.....", e);
			throw new ApplicationException(e.getMessage());
		}

	}

	@Override
	@Transactional(readOnly = false)
	public PaymentIntentPojo confirmStripePayment(String paymentId, boolean emailFlag, String recipientEmail) throws ApplicationException {

		try {
			LOG.info("Started payment confirmation for payment: " + paymentId);
			PaymentIntentPojo response = new PaymentIntentPojo();
			RfaEventSupplier existingRfaSupplier = rfaEventSupplierDao.getSupplierByStripePaymentId(paymentId);
			String timezone = null;
			// User u = null;
			if (existingRfaSupplier != null) {
				existingRfaSupplier.setFeePaid(Boolean.TRUE);
				existingRfaSupplier.setFeePaidDate(new Date());
				rfaEventSupplierService.updateRfaEventSuppliers(existingRfaSupplier);
				LOG.info("Updating Supplier...");
				if (Boolean.TRUE.equals(emailFlag)) {

					// Email to Admin users of supplier
					List<User> users = userDao.getAllAdminUsersForSupplier(existingRfaSupplier.getSupplier().getId());
					if (CollectionUtil.isNotEmpty(users)) {
						for (User user : users) {
							if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
								timezone = supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId());
								if(user.getEmailNotifications()) {
									rfaEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", existingRfaSupplier.getRfxEvent(), user.getName(), existingRfaSupplier.getFeeReference(), timezone);
								}
							} else {
								LOG.warn("Communication email not set");
							}
						}
					}
					LOG.info("Sent Email to supplier Admins...");

					// Email to owner
					if (StringUtils.isNotBlank(existingRfaSupplier.getRfxEvent().getEventOwner().getCommunicationEmail())) {
						timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(existingRfaSupplier.getRfxEvent().getEventOwner().getTenantId());
						if(existingRfaSupplier.getRfxEvent().getEventOwner().getEmailNotifications()) {
							rfaEventService.sendEmailAfterParticipationFees(existingRfaSupplier.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfaSupplier.getSupplierCompanyName(), existingRfaSupplier.getRfxEvent(), existingRfaSupplier.getRfxEvent().getEventOwner().getName(), existingRfaSupplier.getFeeReference(), timezone);
						}
					} else {
						LOG.warn("Communication email not set");
					}

					LOG.info("Sent Email to Event owner...");

					// Email to associated owner
					if (existingRfaSupplier.getRfxEvent().getTeamMembers() != null && existingRfaSupplier.getRfxEvent().getTeamMembers().size() > 0) {
						for (RfaTeamMember member : existingRfaSupplier.getRfxEvent().getTeamMembers()) {
							if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
								if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
									timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(member.getUser().getTenantId());
									if(member.getUser().getEmailNotifications()) {
										rfaEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfaSupplier.getSupplierCompanyName(), existingRfaSupplier.getRfxEvent(), member.getUser().getName(), existingRfaSupplier.getFeeReference(), timezone);
									}
								} else {
									LOG.warn("Communication email not set");
								}
							}
						}
					}

					LOG.info("Sent Email to Event associate owner...");

				}
				response.setSuccess("SUCCESS");
				LOG.info("Finished payment confirmation for RFA Event");
				return response;
			}

			RfiEventSupplier existingRfiSupplier = rfiEventSupplierDao.getSupplierByStripePaymentId(paymentId);
			if (existingRfiSupplier != null) {
				existingRfiSupplier.setFeePaid(Boolean.TRUE);
				existingRfiSupplier.setFeePaidDate(new Date());
				rfiEventSupplierService.updateEventSuppliers(existingRfiSupplier);
				LOG.info("Updating Supplier...");
				if (Boolean.TRUE.equals(emailFlag)) {

					// Email to Admin users of supplier
					List<User> users = userDao.getAllAdminUsersForSupplier(existingRfiSupplier.getSupplier().getId());
					if (CollectionUtil.isNotEmpty(users)) {
						for (User user : users) {
							if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
								timezone = supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId());
								if(user.getEmailNotifications()) {
									rfiEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", existingRfiSupplier.getRfxEvent(), user.getName(), existingRfiSupplier.getFeeReference(), timezone);
								}
							} else {
								LOG.warn("Communication email not set");
							}
						}
					}
					LOG.info("Sent Email to supplier Admins...");

					// Email to owner
					if (StringUtils.isNotBlank(existingRfiSupplier.getRfxEvent().getEventOwner().getCommunicationEmail())) {
						timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(existingRfiSupplier.getRfxEvent().getEventOwner().getTenantId());
						if(existingRfiSupplier.getRfxEvent().getEventOwner().getEmailNotifications()) {
							rfiEventService.sendEmailAfterParticipationFees(existingRfiSupplier.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfiSupplier.getSupplierCompanyName(), existingRfiSupplier.getRfxEvent(), existingRfiSupplier.getRfxEvent().getEventOwner().getName(), existingRfiSupplier.getFeeReference(), timezone);
						}
						} else {
						LOG.warn("Communication email not set");
					}

					LOG.info("Sent Email to Event owner...");

					// Email to associated owner
					if (existingRfiSupplier.getRfxEvent().getTeamMembers() != null && existingRfiSupplier.getRfxEvent().getTeamMembers().size() > 0) {
						for (RfiTeamMember member : existingRfiSupplier.getRfxEvent().getTeamMembers()) {
							if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
								if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
									timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(member.getUser().getTenantId());
									if(member.getUser().getEmailNotifications()) {
										rfiEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfiSupplier.getSupplierCompanyName(), existingRfiSupplier.getRfxEvent(), member.getUser().getName(), existingRfiSupplier.getFeeReference(), timezone);
									}
									} else {
									LOG.warn("Communication email not set");
								}
							}
						}
					}

					LOG.info("Sent Email to Event associate owner...");

				}
				response.setSuccess("SUCCESS");
				LOG.info("Finished payment confirmation for RFI Event");
				return response;
			}

			RfpEventSupplier existingRfpSupplier = rfpEventSupplierDao.getSupplierByStripePaymentId(paymentId);
			if (existingRfpSupplier != null) {
				existingRfpSupplier.setFeePaid(Boolean.TRUE);
				existingRfpSupplier.setFeePaidDate(new Date());
				rfpEventSupplierService.updateEventSuppliers(existingRfpSupplier);
				LOG.info("Updating Supplier...");
				if (Boolean.TRUE.equals(emailFlag)) {

					// Email to Admin users of supplier
					List<User> users = userDao.getAllAdminUsersForSupplier(existingRfpSupplier.getSupplier().getId());
					if (CollectionUtil.isNotEmpty(users)) {
						for (User user : users) {
							if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
								timezone = supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId());
								if(user.getEmailNotifications()) {
									rfpEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", existingRfpSupplier.getRfxEvent(), user.getName(), existingRfpSupplier.getFeeReference(), timezone);
								}
								} else {
								LOG.warn("Communication email not set");
							}
						}
					}
					LOG.info("Sent Email to supplier Admins...");

					// Email to owner
					if (StringUtils.isNotBlank(existingRfpSupplier.getRfxEvent().getEventOwner().getCommunicationEmail())) {
						timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(existingRfpSupplier.getRfxEvent().getEventOwner().getTenantId());
						if(existingRfpSupplier.getRfxEvent().getEventOwner().getEmailNotifications()) {
							rfpEventService.sendEmailAfterParticipationFees(existingRfpSupplier.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfpSupplier.getSupplierCompanyName(), existingRfpSupplier.getRfxEvent(), existingRfpSupplier.getRfxEvent().getEventOwner().getName(), existingRfpSupplier.getFeeReference(), timezone);
						}
						} else {
						LOG.warn("Communication email not set");
					}

					LOG.info("Sent Email to Event owner...");

					// Email to associated owner
					if (existingRfpSupplier.getRfxEvent().getTeamMembers() != null && existingRfpSupplier.getRfxEvent().getTeamMembers().size() > 0) {
						for (RfpTeamMember member : existingRfpSupplier.getRfxEvent().getTeamMembers()) {
							if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
								if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
									timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(member.getUser().getTenantId());
									if(member.getUser().getEmailNotifications()) {
										rfpEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfpSupplier.getSupplierCompanyName(), existingRfpSupplier.getRfxEvent(), member.getUser().getName(), existingRfpSupplier.getFeeReference(), timezone);
									}
								} else {
									LOG.warn("Communication email not set");
								}
							}
						}
					}

					LOG.info("Sent Email to Event associate owner...");

				}
				response.setSuccess("SUCCESS");
				LOG.info("Finished payment confirmation for RFP Event");
				return response;
			}

			RfqEventSupplier existingRfqSupplier = rfqEventSupplierDao.getSupplierByStripePaymentId(paymentId);
			if (existingRfqSupplier != null) {
				existingRfqSupplier.setFeePaid(Boolean.TRUE);
				existingRfqSupplier.setFeePaidDate(new Date());
				rfqEventSupplierService.updateEventSuppliers(existingRfqSupplier);
				LOG.info("Updating Supplier...");
				if (Boolean.TRUE.equals(emailFlag)) {

					// Email to Admin users of supplier
					List<User> users = userDao.getAllAdminUsersForSupplier(existingRfqSupplier.getSupplier().getId());
					if (CollectionUtil.isNotEmpty(users)) {
						for (User user : users) {
							if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
								timezone = supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId());
								if(user.getEmailNotifications()) {
									rfqEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", existingRfqSupplier.getRfxEvent(), user.getName(), existingRfqSupplier.getFeeReference(), timezone);
								}
								} else {
								LOG.warn("Communication email not set");
							}
						}
					}
					LOG.info("Sent Email to supplier Admins...");

					// Email to owner
					if (StringUtils.isNotBlank(existingRfqSupplier.getRfxEvent().getEventOwner().getCommunicationEmail())) {
						timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(existingRfqSupplier.getRfxEvent().getEventOwner().getTenantId());
						if(existingRfqSupplier.getRfxEvent().getEventOwner().getEmailNotifications()) {
							rfqEventService.sendEmailAfterParticipationFees(existingRfqSupplier.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfqSupplier.getSupplierCompanyName(), existingRfqSupplier.getRfxEvent(), existingRfqSupplier.getRfxEvent().getEventOwner().getName(), existingRfqSupplier.getFeeReference(), timezone);
						}
						} else {
						LOG.warn("Communication email not set");
					}

					LOG.info("Sent Email to Event owner...");

					// Email to associated owner
					if (existingRfqSupplier.getRfxEvent().getTeamMembers() != null && existingRfqSupplier.getRfxEvent().getTeamMembers().size() > 0) {
						for (RfqTeamMember member : existingRfqSupplier.getRfxEvent().getTeamMembers()) {
							if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
								if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
									timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(member.getUser().getTenantId());
									if(member.getUser().getEmailNotifications()) {
										rfqEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRfqSupplier.getSupplierCompanyName(), existingRfqSupplier.getRfxEvent(), member.getUser().getName(), existingRfqSupplier.getFeeReference(), timezone);
									}
									} else {
									LOG.warn("Communication email not set");
								}
							}
						}
					}

					LOG.info("Sent Email to Event associate owner...");

				}
				response.setSuccess("SUCCESS");
				LOG.info("Finished payment confirmation for RFQ Event");
				return response;
			}

			RftEventSupplier existingRftSupplier = rftEventSupplierDao.getSupplierByStripePaymentId(paymentId);
			if (existingRftSupplier != null) {
				existingRftSupplier.setFeePaid(Boolean.TRUE);
				existingRftSupplier.setFeePaidDate(new Date());
				rftEventSupplierService.updateEventSuppliers(existingRftSupplier);
				LOG.info("Updating Supplier...");
				if (Boolean.TRUE.equals(emailFlag)) {

					// Email to Admin users of supplier
					List<User> users = userDao.getAllAdminUsersForSupplier(existingRftSupplier.getSupplier().getId());
					if (CollectionUtil.isNotEmpty(users)) {
						for (User user : users) {
							if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
								timezone = supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId());
								if(user.getEmailNotifications()) {
									rftEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", existingRftSupplier.getRfxEvent(), user.getName(), existingRftSupplier.getFeeReference(), timezone);
								}
								} else {
								LOG.warn("Communication email not set");
							}
						}
					}
					LOG.info("Sent Email to supplier Admins...");

					// Email to owner
					if (StringUtils.isNotBlank(existingRftSupplier.getRfxEvent().getEventOwner().getCommunicationEmail())) {
						timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(existingRftSupplier.getRfxEvent().getEventOwner().getTenantId());
						if(existingRftSupplier.getRfxEvent().getEventOwner().getEmailNotifications()) {
							rftEventService.sendEmailAfterParticipationFees(existingRftSupplier.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRftSupplier.getSupplierCompanyName(), existingRftSupplier.getRfxEvent(), existingRftSupplier.getRfxEvent().getEventOwner().getName(), existingRftSupplier.getFeeReference(), timezone);
						}
					} else {
						LOG.warn("Communication email not set");
					}

					LOG.info("Sent Email to Event owner...");

					// Email to associated owner
					if (existingRftSupplier.getRfxEvent().getTeamMembers() != null && existingRftSupplier.getRfxEvent().getTeamMembers().size() > 0) {
						for (RftTeamMember member : existingRftSupplier.getRfxEvent().getTeamMembers()) {
							if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
								if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
									timezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(member.getUser().getTenantId());
									if(member.getUser().getEmailNotifications()) {
										rftEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + existingRftSupplier.getSupplierCompanyName(), existingRftSupplier.getRfxEvent(), member.getUser().getName(), existingRftSupplier.getFeeReference(), timezone);
									}
									} else {
									LOG.warn("Communication email not set");
								}
							}
						}
					}

					LOG.info("Sent Email to Event associate owner...");
				}
				response.setSuccess("SUCCESS");
				LOG.info("Finished payment confirmation for RFT Event");
				return response;
			}

			throw new ApplicationException("Unable to find supplier with the payment ID: " + paymentId);
		} catch (Exception e) {
			LOG.error("Error in payment confirmation: " + e.getMessage());
			throw new ApplicationException(e.getMessage());
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional(readOnly = false)
	public void handlePaymentWebhookEvent(com.stripe.model.Event event, boolean emailFlag) throws ApplicationException {
		try {
			StripeObject stripeObject = event.getData().getObject();
			switch (event.getType()) {
			case "charge.succeeded":
			case "payment_intent.succeeded":
				PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
				if (paymentIntent != null) {
					LOG.info("Payment succeeded for: " + paymentIntent.getDescription() != null ? paymentIntent.getDescription() : paymentIntent.getClientSecret());
					confirmStripePayment(paymentIntent.getId(), emailFlag, paymentIntent.getReceiptEmail());
				}
				break;
			case "charge.failed":
			case "payment_intent.payment_failed":
			case "payment_intent.canceled":
				PaymentIntent paymentFailIntent = (PaymentIntent) stripeObject;
				if (paymentFailIntent != null) {
					LOG.error("Payment failed for: " + paymentFailIntent.getDescription() != null ? paymentFailIntent.getDescription() : paymentFailIntent.getId());
				}
				break;
			default:
				LOG.info("Unhandled Event " + event.getType());
			}
		} catch (Exception e) {
			LOG.info("Exception in handling payment : " + e.getMessage(), e);
		}

	}

	@Override
	public String getPaymentStatus(String feeReference, String tenantId) throws ApplicationException {
		BuyerSettings bs = buyerSettingsDao.getBuyerSettingsByTenantId(tenantId);
		Stripe.apiKey = bs.getStripeSecretKey();
		try {
			LOG.info("Getting Payment status for : " + feeReference);
			PaymentIntent paymentIntent = PaymentIntent.retrieve(feeReference);
			if (Global.STRIPE_STATUS_SUCCESS.equals(paymentIntent.getStatus())) {
				String message = "Successfully made a payment of " + (paymentIntent.getCurrency()).toUpperCase() + " " + new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				// Passing false as email will be sent by webhook
				confirmStripePayment(paymentIntent.getId(), false, null);
				return message;
			} else if (Global.STRIPE_STATUS_PROCESSING.equals(paymentIntent.getStatus())) {
				String message = "Processing a payment of " + (paymentIntent.getCurrency()).toUpperCase() + " " + new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				return message;
			} else {
				throw new ApplicationException(paymentIntent.getLastPaymentError() != null ? paymentIntent.getLastPaymentError().getMessage() : "Payment Not completed.");
			}
		} catch (Exception e) {
			LOG.error("Error getting payment status application exception: " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
	}

	@Override
	public Boolean checkForPendingPayments(String feeReference, String tenantId) {
		BuyerSettings bs = buyerSettingsDao.getBuyerSettingsByTenantId(tenantId);
		Stripe.apiKey = bs.getStripeSecretKey();
		LOG.info("Checking Payment status for : " + feeReference);
		try {
			PaymentIntent paymentIntent = PaymentIntent.retrieve(feeReference);
			if (Global.STRIPE_STATUS_PROCESSING.equals(paymentIntent.getStatus())) {
				LOG.info("Payment is in processing state for intent: " + feeReference);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error while checking payment status " + e.getMessage(), e);
			return false;
		}
	}
}