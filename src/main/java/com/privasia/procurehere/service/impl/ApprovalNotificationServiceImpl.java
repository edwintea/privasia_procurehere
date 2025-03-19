/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

import com.privasia.procurehere.core.dao.ContractApprovalUserDao;
import com.privasia.procurehere.core.dao.ContractAuditDao;
import com.privasia.procurehere.core.dao.PoApprovalUserDao;
import com.privasia.procurehere.core.dao.PrApprovalUserDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.RfaApprovalUserDao;
import com.privasia.procurehere.core.dao.RfaEventApprovalDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiApprovalUserDao;
import com.privasia.procurehere.core.dao.RfiEventApprovalDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpApprovalUserDao;
import com.privasia.procurehere.core.dao.RfpEventApprovalDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqApprovalUserDao;
import com.privasia.procurehere.core.dao.RfqEventApprovalDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftApprovalUserDao;
import com.privasia.procurehere.core.dao.RftEventApprovalDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.SourcingFormApprovalUserRequestDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.ApprovalUser;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.ContractApproval;
import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.entity.ContractAudit;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoApproval;
import com.privasia.procurehere.core.entity.PoApprovalUser;
import com.privasia.procurehere.core.entity.PoAudit;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrApprovalUser;
import com.privasia.procurehere.core.entity.PrAudit;
import com.privasia.procurehere.core.entity.PrTeamMember;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.RfaApprovalUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventApproval;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfiApprovalUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventApproval;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpApprovalUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventApproval;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfqApprovalUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventApproval;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RftApprovalUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.FilterTypes;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PoAuditType;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;
import com.privasia.procurehere.core.enums.PrAuditType;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalNotificationService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PoAuditService;
import com.privasia.procurehere.service.PrAuditService;
import com.privasia.procurehere.service.SourcingFormRequestService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class ApprovalNotificationServiceImpl implements ApprovalNotificationService {

	private static final Logger LOG = LogManager.getLogger(ApprovalNotificationServiceImpl.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	PrApprovalUserDao prApprovalUserDao;

	@Autowired
	RftEventApprovalDao rftApprovalLevelDao;

	@Autowired
	RfaEventApprovalDao rfaApprovalLevelDao;

	@Autowired
	RfiEventApprovalDao rfiApprovalLevelDao;

	@Autowired
	RfpEventApprovalDao rfpApprovalLevelDao;

	@Autowired
	RfqEventApprovalDao rfqApprovalLevelDao;

	@Autowired
	RftApprovalUserDao rftApprovalUserDao;

	@Autowired
	RfaApprovalUserDao rfaApprovalUserDao;

	@Autowired
	RfiApprovalUserDao rfiApprovalUserDao;

	@Autowired
	RfpApprovalUserDao rfpApprovalUserDao;

	@Autowired
	RfqApprovalUserDao rfqApprovalUserDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	SourcingFormApprovalUserRequestDao sourcingFormApprovalUserRequestDao;

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	SourcingFormRequestService requestService;

	@Autowired
	PrDao prDao;

	@Autowired
	PoApprovalUserDao poApprovalUserDao;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	ContractApprovalUserDao contractApprovalUserDao;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void sendRfsApprovalNotifications() {
		String buyerTimeZone = null;

		// List<SourcingFormApprovalUserRequest> approvalUsers =
		// sourcingFormApprovalUserRequestDao.findRfsApprovelUserForNotification();
		List<SourcingFormApprovalRequest> levels = sourcingFormApprovalUserRequestDao.findRfsApprovelLevelsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			SourcingFormRequest sourcingFormRequest = null;
			int count = 0;
			for (SourcingFormApprovalRequest level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFS reminder notifications " + level.getSourcingFormRequest().getFormId());
				String usr = "";
				for (SourcingFormApprovalUserRequest appUser : level.getApprovalUsersRequest()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						sourcingFormRequest = appUser.getApprovalRequest().getSourcingFormRequest();
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, sourcingFormRequest.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						Integer reminderCount = sourcingFormRequest.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;
						sourcingFormApprovalUserRequestDao.update(appUser);
						sendEmailToRequestApprovers(sourcingFormRequest, appUser, buyerTimeZone, count);
						usr += appUser.getUser().getName() + ",";
					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						RequestAudit audit = new RequestAudit();
						audit.setAction(RequestAuditType.REMINDER);
						audit.setActionBy(sourcingFormRequest.getFormOwner());
						Buyer buyer = new Buyer();
						buyer.setId(sourcingFormRequest.getTenantId());
						audit.setBuyer(buyer);
						audit.setActionDate(new Date());
						audit.setReq(sourcingFormRequest);
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);
						audit.setDescription(messageSource.getMessage("rfs.pr.approval.reminder.audit.message", new Object[] { "RFS", count, usr }, Global.LOCALE));
						requestService.saveAudit(audit);
					} catch (Exception e) {
					}

				}
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void sendPrApprovalNotifications() {
		String buyerTimeZone = null;

		// List<PrApprovalUser> approvalUsers = prApprovalUserDao.findPrApprovelUserForNotification();

		List<PrApproval> levels = prApprovalUserDao.findPrApprovelLevelsForNotification();

		if (CollectionUtil.isNotEmpty(levels)) {
			Pr pr = null;
			int count = 0;
			for (PrApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending PR reminder notifications : " + level.getPr().getPrId());
				String usr = "";
				for (PrApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						pr = appUser.getApproval().getPr();
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, pr.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						prApprovalUserDao.update(appUser);

						Integer reminderCount = pr.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendEmailToPrApprovers(appUser, buyerTimeZone, count);
						usr += appUser.getUser().getName() + ",";

					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						PrAudit audit = new PrAudit();
						audit.setAction(PrAuditType.REMINDER);
						audit.setActionBy(pr.getCreatedBy());
						audit.setActionDate(new Date());
						audit.setBuyer(pr.getBuyer());
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);
						audit.setDescription(messageSource.getMessage("rfs.pr.approval.reminder.audit.message", new Object[] { "PR", count, usr }, Global.LOCALE));
						audit.setPr(pr);
						prAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
					}
				}
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfaApprovalNotifications() {
		String buyerTimeZone = null;

		List<RfaEventApproval> approvalUsers = rfaApprovalLevelDao.findApprovelLevelsForNotification();
		if (CollectionUtil.isNotEmpty(approvalUsers)) {
			RfaEvent event = null;
			int count = 0;
			for (RfaEventApproval levels : approvalUsers) {
				if (!levels.isActive()) {
					continue;
				}
				LOG.info("Sending RFA reminder notifications " + levels.getEvent().getEventId());
				String usr = "";
				for (RfaApprovalUser appUser : levels.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						event = appUser.getApproval().getEvent();
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, event.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());

						rfaApprovalUserDao.update(appUser);
						Integer reminderCount = event.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendRfxApprovalEmails(event, appUser, RfxTypes.RFA, count);
						usr += appUser.getUser().getName() + ",";

					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);

						RfaEventAudit audit = new RfaEventAudit(event, event.getEventOwner(), new Date(), AuditActionType.Reminder, messageSource.getMessage("approval.reminder.audit.message", new Object[] { count, usr }, Global.LOCALE));
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error while storing reminder audit " + e.getMessage(), e);
					}
				}
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void sendRftApprovalNotifications() {
		String buyerTimeZone = null;

		List<RftEventApproval> levels = rftApprovalLevelDao.findApprovelLevelsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			RftEvent event = null;
			int count = 0;
			for (RftEventApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFT reminder notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RftApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						event = appUser.getApproval().getEvent();
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, event.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						rftApprovalUserDao.update(appUser);
						Integer reminderCount = event.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendRfxApprovalEmails(event, appUser, RfxTypes.RFT, count);
						usr += appUser.getUser().getName() + ",";
					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);

						RftEventAudit audit = new RftEventAudit(event, event.getEventOwner(), new Date(), AuditActionType.Reminder, messageSource.getMessage("approval.reminder.audit.message", new Object[] { count, usr }, Global.LOCALE));
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error while storing reminder audit " + e.getMessage(), e);
					}
				}
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfiApprovalNotifications() {
		String buyerTimeZone = null;

		List<RfiEventApproval> levels = rfiApprovalLevelDao.findApprovelLevelsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			RfiEvent event = null;
			int count = 0;
			for (RfiEventApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFI reminder notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RfiApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						event = appUser.getApproval().getEvent();
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, event.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						rfiApprovalUserDao.update(appUser);
						Integer reminderCount = event.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendRfxApprovalEmails(event, appUser, RfxTypes.RFI, count);
						usr += appUser.getUser().getName() + ",";
					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						usr = usr.substring(0, usr.length() - 1);
						RfiEventAudit audit = new RfiEventAudit(event, event.getEventOwner(), new Date(), AuditActionType.Reminder, messageSource.getMessage("approval.reminder.audit.message", new Object[] { count, usr }, Global.LOCALE));
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error while storing reminder audit " + e.getMessage(), e);
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfpApprovalNotifications() {
		String buyerTimeZone = null;

		// List<RfpApprovalUser> approvalUsers = rfpApprovalUserDao.findApprovelUserForNotification();
		List<RfpEventApproval> levels = rfpApprovalLevelDao.findApprovelLevelsForNotification();

		if (CollectionUtil.isNotEmpty(levels)) {
			RfpEvent event = null;
			int count = 0;
			for (RfpEventApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFP reminder notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RfpApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						event = appUser.getApproval().getEvent();
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, event.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						rfpApprovalUserDao.update(appUser);
						Integer reminderCount = event.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendRfxApprovalEmails(event, appUser, RfxTypes.RFP, count);
						usr += appUser.getUser().getName() + ",";
					}
				}

				if (StringUtils.checkString(usr).length() > 0) {
					try {
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);

						RfpEventAudit audit = new RfpEventAudit(event, event.getEventOwner(), new Date(), AuditActionType.Reminder, messageSource.getMessage("approval.reminder.audit.message", new Object[] { count, usr }, Global.LOCALE));
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error while storing reminder audit " + e.getMessage(), e);
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfqApprovalNotifications() {
		String buyerTimeZone = null;

		// List<RfqApprovalUser> approvalUsers = rfqApprovalUserDao.findApprovelUserForNotification();
		List<RfqEventApproval> levels = rfqApprovalLevelDao.findApprovelLevelsForNotification();

		if (CollectionUtil.isNotEmpty(levels)) {
			RfqEvent event = null;
			int count = 0;
			for (RfqEventApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFQ reminder notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RfqApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						event = appUser.getApproval().getEvent();
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, event.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						rfqApprovalUserDao.update(appUser);
						Integer reminderCount = event.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendRfxApprovalEmails(event, appUser, RfxTypes.RFQ, count);
						usr += appUser.getUser().getName() + ",";
					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);

						RfqEventAudit audit = new RfqEventAudit(event, event.getEventOwner(), new Date(), AuditActionType.Reminder, messageSource.getMessage("approval.reminder.audit.message", new Object[] { count, usr }, Global.LOCALE));
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error while storing reminder audit " + e.getMessage(), e);
					}
				}
			}
		}
	}

	private void sendRfxApprovalEmails(Event event, ApprovalUser user, RfxTypes type, int count) {
		String mailTo = user.getUser().getCommunicationEmail();
		String subject = "Event Approval reminder " + count;
		String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("approverName", user.getUser().getName());
		map.put("remCount", count);
		map.put("eventName", event.getEventName());
		map.put("eventType", type.name());
		map.put("bUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
		map.put("referenceNumber", event.getReferanceNumber());

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = null;
		if (timeZone == null) {
			timeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId());
		}

		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.APPROVEL_REMINDER_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + user.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("event.approval.reminder.notification.message", new Object[] { type.name(), event.getEventName() }, Global.LOCALE);
		sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

		if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + user.getUser().getCommunicationEmail() + "' and device Id :" + user.getUser().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", type.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getUser().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event approve Mobile push notification to '" + user.getUser().getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getUser().getCommunicationEmail() + "' Device Id is Null");
		}
	}

	private void sendRfxApprovalEscalationEmail(Event event, String buyerTimeZone, int approvalLevel, String approvers, RfxTypes type) {
		LOG.info("Sending email to Rfx Escalation");
		List<EventTeamMember> teamMember = findAssociateOwnerOfEvent(event.getId(), type);

		String mailTo = event.getCreatedBy().getCommunicationEmail();
		String subject = "Event Approval Level Inaction ";
		String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("eventOwner", event.getCreatedBy().getName());
		map.put("eventName", event.getEventName());
		map.put("approvalLevel", approvalLevel);
		map.put("approvers", approvers);
		map.put("reminderAfterHour", event.getReminderAfterHour());
		map.put("reminderCount", event.getReminderCount());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = null;
		if (timeZone == null) {
			timeZone = getTimeZoneByBuyerSettings(buyerTimeZone);
		}
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo).length() > 0 && event.getCreatedBy().getEmailNotifications()) {
			map.put("eventOwner", event.getCreatedBy().getName());
			LOG.info("sending email reminder email inaction" + mailTo);
			sendEmail(mailTo, subject, map, Global.EVENT_OWNER_APPROVAL_REMINDER_TEMPLATE);

		} else {
			LOG.warn("No communication email configured for user : " + event.getCreatedBy().getLoginId() + "... Not going to send email notification");
		}

		if (CollectionUtil.isNotEmpty(teamMember)) {
			for (EventTeamMember assOwn : teamMember) {
				LOG.info("sending email reminder email inaction" + assOwn.getUser().getName());
				map.put("eventOwner", assOwn.getUser().getName());
				if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0 && assOwn.getUser().getEmailNotifications()) {
					sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.EVENT_OWNER_APPROVAL_REMINDER_TEMPLATE);
				}
			}
		}
		String notificationMessage = messageSource.getMessage("event.approval.reminder.notification.message", new Object[] { type.name(), event.getEventName() }, Global.LOCALE);
		sendDashboardNotification(event.getCreatedBy(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

		if (StringUtils.checkString(event.getCreatedBy().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + event.getCreatedBy().getCommunicationEmail() + "' and device Id :" + event.getCreatedBy().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", type.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(event.getCreatedBy().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event approve Mobile push notification to '" + event.getCreatedBy().getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + event.getCreatedBy().getCommunicationEmail() + "' Device Id is Null");
		}
	}

	private List<EventTeamMember> findAssociateOwnerOfEvent(String id, RfxTypes type) {

		List<EventTeamMember> teamMember = new ArrayList<EventTeamMember>();
		switch (type) {
		case RFA:
			teamMember = rfaEventDao.findAssociateOwnerOfRfa(id, TeamMemberType.Associate_Owner);
			break;
		case RFI:
			teamMember = rfiEventDao.findAssociateOwnerOfRfi(id, TeamMemberType.Associate_Owner);
			break;
		case RFP:
			teamMember = rfpEventDao.findAssociateOwnerOfRfp(id, TeamMemberType.Associate_Owner);
			break;
		case RFQ:
			teamMember = rfqEventDao.findAssociateOwnerOfRfq(id, TeamMemberType.Associate_Owner);
			break;
		case RFT:
			teamMember = rftEventDao.findAssociateOwnerOfRft(id, TeamMemberType.Associate_Owner);
			break;
		default:
			break;
		}
		return teamMember;

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

	private String getTimeZoneByBuyerSettings(String tenantId) {
		String timeZone = "GMT+8:00";
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendEmailToRequestApprovers(SourcingFormRequest sourcingFormRequest, SourcingFormApprovalUserRequest nextLevelUser, String buyerTimeZone, int count) {
		String mailTo = nextLevelUser.getUser().getCommunicationEmail();
		String subject = "RFS Approval reminder " + count;
		String url = APP_URL + "/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("approverName", nextLevelUser.getUser().getName());
		map.put("remCount", count);
		map.put("eventName", sourcingFormRequest.getSourcingFormName());
		map.put("bUnit", StringUtils.checkString(getBusineessUnitnamerequest(sourcingFormRequest.getId())));
		map.put("referenceNumber", (sourcingFormRequest.getReferanceNumber() == null ? "" : sourcingFormRequest.getReferanceNumber()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0 && nextLevelUser.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.RFS_APPROVEL_REMINDER_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + nextLevelUser.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("rfs.approval.reminder.notification.message", new Object[] { sourcingFormRequest.getSourcingFormName() }, Global.LOCALE);
		sendDashboardNotification(nextLevelUser.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(nextLevelUser.getUser().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + nextLevelUser.getUser().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", sourcingFormRequest.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.PR.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(nextLevelUser.getUser().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	private void sendApprovalEscalationEmail(SourcingFormRequest sourcingFormRequest, String buyerTimeZone, int approvalLevel, String approvers) {
		LOG.info("Sending email to Rfs Escalation");
		List<SourcingFormTeamMember> teamMember = sourcingFormRequestDao.findAssociateOwnerOfRfs(sourcingFormRequest.getId(), TeamMemberType.Associate_Owner);

		String mailTo = sourcingFormRequest.getCreatedBy().getCommunicationEmail();
		String subject = "RFS  Approval Level Inaction ";
		String url = APP_URL + "/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("eventOwner", sourcingFormRequest.getCreatedBy().getName());
		map.put("eventName", sourcingFormRequest.getSourcingFormName());
		map.put("refNumber", sourcingFormRequest.getReferanceNumber());
		map.put("approvalLevel", approvalLevel);
		map.put("approvers", approvers);
		map.put("reminderAfterHour", sourcingFormRequest.getReminderAfterHour());
		map.put("reminderCount", sourcingFormRequest.getReminderCount());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		if (StringUtils.checkString(mailTo).length() > 0 && sourcingFormRequest.getCreatedBy().getEmailNotifications()) {
			LOG.info("sendinng email to RFS owner :" + mailTo);
			map.put("eventOwner", sourcingFormRequest.getCreatedBy().getName());
			sendEmail(mailTo, subject, map, Global.RFS_OWNER_APPROVAL_REMINDER_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + sourcingFormRequest.getCreatedBy().getCommunicationEmail() + "... Not going to send approval escalation email notification");
		}

		if (CollectionUtil.isNotEmpty(teamMember)) {
			for (SourcingFormTeamMember assOwn : teamMember) {
				if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0) {
					if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0) {
						LOG.info("sendinng email to RFS associate owner :" + assOwn.getUser().getName());
						LOG.info("sendinng email to RFS associate owner :" + assOwn.getUser().getName());
						map.put("eventOwner", assOwn.getUser().getName());
						if(assOwn.getUser().getEmailNotifications()) {
							sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.RFS_OWNER_APPROVAL_REMINDER_TEMPLATE);
						}
					}
				}
			}
		}
		String notificationMessage = messageSource.getMessage("rfs.approval.reminder.notification.message", new Object[] { sourcingFormRequest.getSourcingFormName() }, Global.LOCALE);
		sendDashboardNotification(sourcingFormRequest.getCreatedBy(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(sourcingFormRequest.getCreatedBy().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + sourcingFormRequest.getCreatedBy().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", sourcingFormRequest.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.PR.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(sourcingFormRequest.getCreatedBy().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	private String getBusineessUnitnamerequest(String id) {
		String displayName = null;
		displayName = sourcingFormRequestDao.getBusineessUnitnamerequest(id);
		return StringUtils.checkString(displayName);
	}

	private void sendEmailToPrApprovers(PrApprovalUser user, String buyerTimeZone, int count) {
		Pr pr = user.getApproval().getPr();
		String mailTo = user.getUser().getCommunicationEmail();
		String subject = "PR Approval reminder " + count;
		String url = APP_URL + "/buyer/prView/" + pr.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("approverName", user.getUser().getName());
		map.put("prName", pr.getName());
		map.put("remCount", count);
		map.put("bUnit", StringUtils.checkString(pr.getBusinessUnit().getDisplayName()));
		map.put("referenceNumber", (pr.getReferenceNumber() == null ? "" : pr.getReferenceNumber()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.PR_APPROVEL_REMINDER_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + user.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("pr.approval.reminder.notification.message", new Object[] { pr.getName() }, Global.LOCALE);
		sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + user.getUser().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", pr.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.PR.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getUser().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	private void sendPrApprovalEscalationEmail(Pr pr, String buyerTimeZone, int approvalLevel, String approvers) {
		LOG.info("Sending email to PR Escalation");
		List<PrTeamMember> teamMember = prDao.findAssociateOwnerOfPr(pr.getId(), TeamMemberType.Associate_Owner);
		// String assOwn=teamMember.getUser().getCommunicationEmail();
		String mailTo = pr.getCreatedBy().getCommunicationEmail();
		String subject = "PR Approval Level Inaction";
		String url = APP_URL + "/buyer/prView/" + pr.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("eventOwner", pr.getCreatedBy().getName());
		map.put("eventName", pr.getName());
		map.put("approvalLevel", approvalLevel);
		map.put("approvers", approvers);
		map.put("reminderAfterHour", pr.getReminderAfterHour());
		map.put("reminderCount", pr.getReminderCount());
		map.put("bUnit", StringUtils.checkString(pr.getBusinessUnit().getDisplayName()));
		map.put("referenceNumber", (pr.getReferenceNumber() == null ? "" : pr.getReferenceNumber()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0 && pr.getCreatedBy().getEmailNotifications()) {
			LOG.info("sendinng email to pr event owner :" + mailTo);
			map.put("eventOwner", pr.getCreatedBy().getName());
			sendEmail(mailTo, subject, map, Global.PR_OWNER_APPROVAL_REMINDER_TEMPLATE);

		} else {
			LOG.warn("No communication email configured for user : " + pr.getCreatedBy().getLoginId() + "... Not going to send approval escalation email notification");
		}
		if (CollectionUtil.isNotEmpty(teamMember)) {
			for (PrTeamMember assOwn : teamMember) {
				if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0 && assOwn.getUser().getEmailNotifications()) {
					LOG.info("sendinng email to PR associate owner :" + assOwn.getUser().getName());
					map.put("eventOwner", assOwn.getUser().getName());
					sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.PR_OWNER_APPROVAL_REMINDER_TEMPLATE);
				}
			}
		}

		String notificationMessage = messageSource.getMessage("pr.approval.reminder.notification.message", new Object[] { pr.getName() }, Global.LOCALE);
		sendDashboardNotification(pr.getCreatedBy(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(pr.getCreatedBy().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + pr.getCreatedBy().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", pr.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.PR.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(pr.getCreatedBy().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Escalation Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	private void
	sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfsEscalationNotifications() {

		String buyerTimeZone = null;
		List<SourcingFormApprovalRequest> levels = sourcingFormApprovalUserRequestDao.findRfsApprovelEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (SourcingFormApprovalRequest level : levels) {
				SourcingFormRequest sourcingFormRequest = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFS reminder notifications " + level.getSourcingFormRequest().getFormId());
				String usr = "";
				for (SourcingFormApprovalUserRequest appUser : level.getApprovalUsersRequest()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}

						if (sourcingFormRequest == null) {
							sourcingFormRequest = appUser.getApprovalRequest().getSourcingFormRequest();
						}

						usr += appUser.getUser().getName();

					}
				}
				sendApprovalEscalationEmail(sourcingFormRequest, buyerTimeZone, level.getLevel(), usr);
				level.setEscalated(Boolean.TRUE);
				sourcingFormApprovalUserRequestDao.updateSourcingFormApproval(level);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendPrEscalationNotifications() {

		String buyerTimeZone = null;
		List<PrApproval> levels = prApprovalUserDao.findPrApprovelEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (PrApproval level : levels) {
				Pr pr = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending PR escalation notifications : " + level.getPr().getPrId());
				String usr = "";
				for (PrApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (pr == null) {
							pr = appUser.getApproval().getPr();
						}
						usr += appUser.getUser().getName();
					}
				}
				sendPrApprovalEscalationEmail(pr, buyerTimeZone, level.getLevel(), usr);
				level.setEscalated(Boolean.TRUE);
				prApprovalUserDao.updatePrApproval(level);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfaEscalationNotifications() {

		String buyerTimeZone = null;
		List<RfaEventApproval> approvalUsers = rfaApprovalLevelDao.findRfaApprovelEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(approvalUsers)) {
			for (RfaEventApproval levels : approvalUsers) {
				RfaEvent event = null;
				if (!levels.isActive()) {
					continue;
				}
				LOG.info("Sending RFA reminder notifications " + levels.getEvent().getEventId());
				String usr = "";
				for (RfaApprovalUser appUser : levels.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + levels.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (event == null) {
							event = appUser.getApproval().getEvent();
						}
						usr += appUser.getUser().getName();
					}
				}
				sendRfxApprovalEscalationEmail(event, buyerTimeZone, levels.getLevel(), usr, RfxTypes.RFA);
				levels.setEscalated(Boolean.TRUE);
				rfaApprovalLevelDao.update(levels);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfiEscalationNotifications() {

		String buyerTimeZone = null;
		List<RfiEventApproval> levels = rfiApprovalLevelDao.findRfiApprovelEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (RfiEventApproval level : levels) {
				RfiEvent event = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFI escalation notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RfiApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (event == null) {
							event = appUser.getApproval().getEvent();
						}
						usr += appUser.getUser().getName();

					}
				}
				sendRfxApprovalEscalationEmail(event, buyerTimeZone, level.getLevel(), usr, RfxTypes.RFI);
				level.setEscalated(Boolean.TRUE);
				rfiApprovalLevelDao.updateRfiApproval(level);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfpEscalationNotifications() {

		String buyerTimeZone = null;
		List<RfpEventApproval> levels = rfpApprovalLevelDao.findRfpApprovelEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (RfpEventApproval level : levels) {
				RfpEvent event = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFP escalation notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RfpApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (event == null) {
							event = appUser.getApproval().getEvent();
						}
						usr += appUser.getUser().getName();

					}
				}
				sendRfxApprovalEscalationEmail(event, buyerTimeZone, level.getLevel(), usr, RfxTypes.RFP);
				level.setEscalated(Boolean.TRUE);
				rfpApprovalLevelDao.updateRfpApproval(level);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRfqEscalationNotifications() {

		String buyerTimeZone = null;
		List<RfqEventApproval> levels = rfqApprovalLevelDao.findRfqApprovalEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (RfqEventApproval level : levels) {
				RfqEvent event = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFQ escalation notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RfqApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (event == null) {
							event = appUser.getApproval().getEvent();
						}
						usr += appUser.getUser().getName();

					}
				}
				sendRfxApprovalEscalationEmail(event, buyerTimeZone, level.getLevel(), usr, RfxTypes.RFQ);
				level.setEscalated(Boolean.TRUE);
				rfqApprovalLevelDao.updateRfqApproval(level);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendRftEscalationNotifications() {

		String buyerTimeZone = null;
		List<RftEventApproval> levels = rftApprovalLevelDao.findRftApprovalEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (RftEventApproval level : levels) {
				RftEvent event = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending RFT escalation notifications " + level.getEvent().getEventId());
				String usr = "";
				for (RftApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (event == null) {
							event = appUser.getApproval().getEvent();
						}
						usr += appUser.getUser().getName();

					}
				}
				sendRfxApprovalEscalationEmail(event, buyerTimeZone, level.getLevel(), usr, RfxTypes.RFT);
				level.setEscalated(Boolean.TRUE);
				rftApprovalLevelDao.updateRftApproval(level);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendPoEscalationNotifications() {

		String buyerTimeZone = null;
		List<PoApproval> levels = poApprovalUserDao.findPoApprovalEscalationsForNotification();
		if (CollectionUtil.isNotEmpty(levels)) {
			for (PoApproval level : levels) {
				Po po = null;
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending PO escalation notifications : " + level.getPo().getPoNumber());
				String usr = "";
				for (PoApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {

						if (usr.length() > 0) {
							usr += " " + level.getApprovalType().name() + " ";
						}

						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						if (po == null) {
							po = appUser.getApproval().getPo();
						}
						usr += appUser.getUser().getName();
					}
				}
				sendPoApprovalEscalationEmail(po, buyerTimeZone, level.getLevel(), usr);
				level.setEscalated(Boolean.TRUE);
				poApprovalUserDao.updatePoApproval(level);
			}
		}
	}

	private void sendPoApprovalEscalationEmail(Po po, String buyerTimeZone, int approvalLevel, String approvers) {
		LOG.info("Sending Revise PO Escalation email .... ");
		String mailTo = po.getCreatedBy().getCommunicationEmail();
		String subject = "Revise PO Approval Level Inaction";
		String url = APP_URL + "/buyer/poView/" + po.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("po", po);
		map.put("approvalLevel", approvalLevel);
		map.put("approvers", approvers);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0 && po.getCreatedBy().getEmailNotifications()) {
			LOG.info("sendinng email to po event owner :" + mailTo);
			map.put("eventOwner", po.getCreatedBy().getName());
			sendEmail(mailTo, subject, map, Global.REVISE_PO_APPROVAL_INACTION_TEMPLATE);

		} else {
			LOG.warn("No communication email configured for user : " + po.getCreatedBy().getLoginId() + "... Not going to send approval escalation email notification");
		}

		String notificationMessage = messageSource.getMessage("pr.approval.reminder.notification.message", new Object[] { po.getName() }, Global.LOCALE);
		sendDashboardNotification(po.getCreatedBy(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(po.getCreatedBy().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + po.getCreatedBy().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", po.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.PR.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(po.getCreatedBy().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Escalation Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendPoApprovalNotifications() {
		String buyerTimeZone = null;

		List<PoApproval> levels = poApprovalUserDao.findPoApprovalLevelsForNotification();

		if (CollectionUtil.isNotEmpty(levels)) {
			Po po = null;
			int count = 0;
			for (PoApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending PO reminder notifications : " + level.getPo().getPoNumber());
				String usr = "";
				for (PoApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						po = appUser.getApproval().getPo();
						appUser.setReminderCount(appUser.getReminderCount() - 1);
						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, po.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						poApprovalUserDao.update(appUser);

						Integer reminderCount = po.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendEmailToPoApprovers(appUser, buyerTimeZone, count);
						usr += appUser.getUser().getName() + ",";

					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						PoAudit audit = new PoAudit();
						audit.setAction(PoAuditType.REMINDER);
						audit.setActionBy(po.getCreatedBy());
						audit.setActionDate(new Date());
						audit.setBuyer(po.getBuyer());
						audit.setVisibilityType(PoAuditVisibilityType.BUYER);
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);
						audit.setDescription(messageSource.getMessage("rfs.pr.approval.reminder.audit.message", new Object[] { "Revise PO", count, usr }, Global.LOCALE));
						audit.setPo(po);
						poAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
					}
				}
			}
		}

	}

	private void sendEmailToPoApprovers(PoApprovalUser user, String buyerTimeZone, int count) {
		Po po = user.getApproval().getPo();
		String mailTo = user.getUser().getCommunicationEmail();
		String subject = "Revise PO Approval Reminder " + count;
		String url = APP_URL + "/buyer/poView/" + po.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("approverName", user.getUser().getName());
		map.put("po", po);
		map.put("remCount", count);
		map.put("businessUnit", StringUtils.checkString(po.getBusinessUnit().getDisplayName()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("poDate", df.format(po.getCreatedDate()));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.REVISE_PO_APPROVAL_REMINDER_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + user.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("pr.approval.reminder.notification.message", new Object[] { po.getName() }, Global.LOCALE);
		sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + user.getUser().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", po.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.PR.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getUser().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendContractApprovalNotifications() {
		String buyerTimeZone = null;

		List<ContractApproval> levels = contractApprovalUserDao.findContractApprovelLevelsForNotification();

		if (CollectionUtil.isNotEmpty(levels)) {
			ProductContract contract = null;
			int count = 0;
			for (ContractApproval level : levels) {
				if (!level.isActive()) {
					continue;
				}
				LOG.info("Sending Contract reminder notifications : " + level.getProductContract().getContractId());
				String usr = "";
				for (ContractApprovalUser appUser : level.getApprovalUsers()) {
					if (appUser.getApprovalStatus() == ApprovalStatus.PENDING) {
						if (buyerTimeZone == null) {
							buyerTimeZone = getTimeZoneByBuyerSettings(appUser.getUser().getTenantId());
						}
						contract = appUser.getApproval().getProductContract();

						appUser.setReminderCount(appUser.getReminderCount() - 1);

						// This is last reminder - send notification to Owner that all reminders are done
						if (appUser.getReminderCount() == 0 && Boolean.TRUE == contract.getNotifyEventOwner()) {
							sendEmailToContractOwner(contract, contract.getContractCreator(), appUser, buyerTimeZone, count);
						}

						Calendar nextFireTime = Calendar.getInstance();
						nextFireTime.add(Calendar.HOUR, contract.getReminderAfterHour());
						nextFireTime.add(Calendar.MINUTE, -1);
						appUser.setNextReminderTime(nextFireTime.getTime());
						contractApprovalUserDao.update(appUser);

						Integer reminderCount = contract.getReminderCount();
						Integer sentCount = appUser.getReminderCount();
						count = reminderCount - sentCount;

						sendEmailToContractApprovers(appUser, buyerTimeZone, count);
						usr += appUser.getUser().getName() + ",";

					}
				}
				if (StringUtils.checkString(usr).length() > 0) {
					try {
						ContractAudit audit = new ContractAudit();
						audit.setAction(AuditActionType.Reminder);
						audit.setActionBy(contract.getCreatedBy());
						audit.setActionDate(new Date());
						audit.setBuyer(contract.getBuyer());
						usr = usr.substring(0, usr.length() - 1);
						LOG.info("Users : " + usr);
						audit.setDescription(messageSource.getMessage("rfs.pr.approval.reminder.audit.message", new Object[] { "Contract", count, usr }, Global.LOCALE));
						audit.setProductContract(contract);
						contractAuditDao.save(audit);
					} catch (Exception e) {
						LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
					}
				}
			}
		}

	}

	private void sendEmailToContractApprovers(ContractApprovalUser user, String buyerTimeZone, int count) {
		ProductContract contract = user.getApproval().getProductContract();
		String mailTo = user.getUser().getCommunicationEmail();
		String subject = "Contract Approval Reminder " + count;
		String url = APP_URL + "/buyer/contractView/" + contract.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("approverName", user.getUser().getName());
		map.put("contractId", contract.getContractId());
		map.put("contractName", contract.getContractName());
		map.put("referenceNumber", contract.getContractReferenceNumber());
		map.put("remCount", count);
		map.put("businessUnit", StringUtils.checkString(contract.getBusinessUnit().getDisplayName()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.CONTRACT_APPROVAL_REMINDER);
		} else {
			LOG.warn("No communication email configured for user : " + user.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("contract.approval.reminder.notification.message", new Object[] { contract.getContractName() }, Global.LOCALE);
		sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
		if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo + "' and device Id :" + user.getUser().getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", contract.getId());
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", FilterTypes.CONTRACT.toString());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getUser().getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo + "' Device Id is Null");
		}
	}

	private void sendEmailToContractOwner(ProductContract contract, User user, ContractApprovalUser approverUser, String buyerTimeZone, int count) {
		String mailTo = user.getCommunicationEmail();
		// String subject = "Contract Approval Reminder completed for Approver " + approverUser.getUser().getName();
		String subject = "Contract Approval Level Inaction";
		String url = APP_URL + "/buyer/contractView/" + contract.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("approverName", approverUser.getUser().getName());
		map.put("approverLevel", approverUser.getApproval().getLevel());
		map.put("userName", user.getName());
		map.put("contractId", contract.getContractId());
		map.put("contractName", contract.getContractName());
		map.put("remCount", count);
		String msg = "Every " + contract.getReminderAfterHour() + " hours up to " + contract.getReminderCount() + " Reminders";
		map.put("msg", msg);
		map.put("businessUnit", StringUtils.checkString(contract.getBusinessUnit().getDisplayName()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(mailTo).length() > 0  && user.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.CONTRACT_APPROVAL_REMINDER_COMPLETE);
		} else {
			LOG.warn("No communication email configured for user : " + user.getLoginId() + "... Not going to send email notification");
		}
	}
}
