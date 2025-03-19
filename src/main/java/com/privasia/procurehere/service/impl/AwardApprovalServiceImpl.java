/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfaEventAwardDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventAwardDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventAwardDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventAwardDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.ApprovalUser;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfaAwardComment;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventAward;
import com.privasia.procurehere.core.entity.RfaEventAwardApproval;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfpAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfpAwardComment;
import com.privasia.procurehere.core.entity.RfpComment;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventAward;
import com.privasia.procurehere.core.entity.RfpEventAwardApproval;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfqAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfqAwardComment;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventAward;
import com.privasia.procurehere.core.entity.RfqEventAwardApproval;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RftAwardApprovalUser;
import com.privasia.procurehere.core.entity.RftAwardComment;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventAward;
import com.privasia.procurehere.core.entity.RftEventAwardApproval;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AwardApprovalService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.EventAwardAuditService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaAwardService;
import com.privasia.procurehere.service.RfaEventMeetingService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpAwardService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqAwardService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftAwardService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.TatReportService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Aishwarya
 */

@Service
@Transactional(readOnly = true)
public class AwardApprovalServiceImpl implements AwardApprovalService {

	public static final Logger LOG = LogManager.getLogger(AwardApprovalServiceImpl.class);

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	BuyerService buyerService;

	@Autowired
	RfaEventMeetingService rfaEventMeetingService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	EventAwardAuditService eventAwardAuditService;

	@Autowired
	RfqEventAwardDao rfqEventAwardDao;

	@Autowired
	RfqAwardService rfqAwardService;

	@Autowired
	RfpEventAwardDao rfpEventAwardDao;

	@Autowired
	RfpAwardService rfpAwardService;

	@Autowired
	RftEventAwardDao rftEventAwardDao;

	@Autowired
	RftAwardService rftAwardService;

	@Autowired
	RfaEventAwardDao rfaEventAwardDao;

	@Autowired
	RfaAwardService rfaAwardService;

	@Override
	@Transactional(readOnly = false)
	public RfaEvent doApproval(RfaEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception {
		try {
			RfaEventAward eventAward = rfaEventAwardDao.findById(awardId);
			event = rfaEventService.loadRfaEventById(event.getId());
			List<RfaEventAwardApproval> approvalList = event.getAwardApprovals(); // rfaEventDao.getAllApprovalsForEvent(event.getId());
			if (CollectionUtil.isEmpty(approvalList)) {
				event.setStatus(EventStatus.FINISHED);
				event.setActionBy(loggedInUser);
				event.setActionDate(new Date());
				event.setAwardStatus(AwardStatus.APPROVED);
				event.setAwarded(Boolean.TRUE);
				try {
					RfaEventAudit eaudit = new RfaEventAudit();
					eaudit.setActionDate(new Date());
					eaudit.setActionBy(loggedInUser);
					eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
					eaudit.setEvent(event);
					eaudit.setAction(AuditActionType.Conclude);
					eventAuditService.save(eaudit);

					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = rfaAwardService.getAwardSnapShotPdf(eventAward, session, loggedInUser, awardResponsePojo, false, true);
					ByteArrayOutputStream workbook = rfaAwardService.getAwardSnapShotXcel(eventAward, session, loggedInUser, awardResponsePojo, false, true);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfaEventAwardAudit audit = null;
						audit = new RfaEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfaAwardAudit(audit);
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}

				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { eventAward.getRfxEvent().getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
				}

				try {
					tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
				} catch (Exception e) {
					LOG.error("Error updating Tat Report " + e.getMessage(), e);
				}

				LOG.info("status :" + event.getStatus());
			} else {
				if (eventAward != null) {
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setDescription(messageSource.getMessage("event.award.sent.for.approval", new Object[] { eventAward.getRfxEvent().getEventName() }, Global.LOCALE));
						audit.setEvent(eventAward.getRfxEvent());
						audit.setAction(AuditActionType.ConcludeAward);
						eventAuditService.save(audit);

						try {
							RfaEventAwardAudit awardAudit = new RfaEventAwardAudit();
							awardAudit.setActionDate(new Date());
							awardAudit.setBuyer(loggedInUser.getBuyer());
							awardAudit.setActionBy(loggedInUser);
							awardAudit.setEvent(eventAward.getRfxEvent());
							awardAudit.setDescription(messageSource.getMessage("audit.event.sent.for.approval", new Object[] {}, Global.LOCALE));
							eventAwardAuditService.saveRfaAwardAudit(awardAudit);
						} catch (Exception e) {
							LOG.error("Error while capturing award audit " + e.getMessage(), e);
						}

						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDEAWARD, messageSource.getMessage("event.award.sent.for.approval", new Object[] { eventAward.getRfxEvent().getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while capturing award audit for buyer audit trail " + e.getMessage(), e);
						}
						// eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				if (event.getStatus() == EventStatus.COMPLETE && Boolean.TRUE == isConclude) {
					for (RfaEventAwardApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfaEventAwardApproval currentLevel = null;
					for (RfaEventAwardApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfaAwardApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, eventAward);
					}
				}

				// Just send emails to users.
				for (RfaEventAwardApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RfaAwardApprovalUser user : approval.getApprovalUsers()) {
							if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
								LOG.info("send mail to pending approvers ");
								sendRfxApprovalEmails(event, user, RfxTypes.RFA);

							}
						}
					}
				}
			}
			event = rfaEventDao.update(event);
		} catch (Exception e) {
			LOG.info("ERROR While Approving RFA :" + e.getMessage(), e);
			throw new Exception("ERROR While Approving RFA :" + e.getMessage());
		}
		return event;
	}

	private void sendRfxApprovalEmails(Event event, ApprovalUser user, RfxTypes type) {
		String mailTo = user.getUser().getCommunicationEmail();
		String subject = "Award Approval Request";
		String url = APP_URL + "/buyer/" + type.name() + "/eventAward/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", user.getUser().getName());
		map.put("event", event);
		map.put("eventType", type.name());
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));

		TimeZone timeZone = TimeZone.getDefault();
		String timeZoneStr = "GMT+8:00";
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		try {
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(event.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZoneStr = settings.getTimeZone().getTimeZone();
			} else {
				timeZoneStr = "GMT+8:00";
			}
			timeZone = TimeZone.getTimeZone(timeZoneStr);
			sdf.setTimeZone(timeZone);
		} catch (Exception e) {
		} finally {
			sdf.setTimeZone(timeZone);
		}
		map.put("date", sdf.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.AWARD_EVENT_APPROVAL_REQUEST_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + user.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("event.approval.request.notification.message", new Object[] { type.name(), event.getEventName() }, Global.LOCALE);
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

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
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

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
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

	@Override
	public String findBusinessUnit(String eventId, RfxTypes rfxTypes) {
		String displayName = null;
		switch (rfxTypes) {
		case RFA:
			displayName = rfaEventDao.findBusinessUnitName(eventId);
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

	private void setNextOrAllDone(User actionBy, List<RfaEventAwardApproval> approvalList, RfaEventAwardApproval currentLevel, RfaEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, RfaEventAward eventAward) throws ApplicationException {
		currentLevel.setDone(true);
		currentLevel.setActive(false); // Check if all approvals are done
		// Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			// all approvals done
			LOG.info("All approvals for this RFA is done!!!. Going to Approved Mode.");
			event.setStatus(EventStatus.FINISHED);
			event.setActionBy(actionBy);
			event.setActionDate(new Date());
			event.setAwardStatus(AwardStatus.APPROVED);
			event.setAwarded(Boolean.TRUE);

			try {
				RfaEventAudit eaudit = new RfaEventAudit();
				eaudit.setActionDate(new Date());
				eaudit.setActionBy(actionBy);
				eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
				eaudit.setEvent(event);
				eaudit.setAction(AuditActionType.Conclude);
				eventAuditService.save(eaudit);

				AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
				JasperPrint eventAward1 = rfaAwardService.getAwardSnapShotPdf(eventAward, session, loggedInUser, awardResponsePojo, false, true);
				ByteArrayOutputStream workbook = rfaAwardService.getAwardSnapShotXcel(eventAward, session, loggedInUser, awardResponsePojo, false, true);
				if (workbook != null) {
					byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
					byte[] excelSnapshot = workbook.toByteArray();

					LOG.info("SUSPEND FILE Size : " + snapshot.length);
					RfaEventAwardAudit audit = null;
					audit = new RfaEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
					eventAwardAuditService.saveRfaAwardAudit(audit);
				}
			} catch (Exception e) {
				LOG.error("Error while :" + e.getMessage(), e);
			}

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { eventAward.getRfxEvent().getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
			}

			try {
				tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
			} catch (Exception e) {
				LOG.error("Error updating Tat Report " + e.getMessage(), e);
			}

		} else {
			for (RfaEventAwardApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfaAwardApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFA);
						if (Boolean.TRUE == event.getEnableApprovalReminder()) {
							Integer reminderHr = event.getReminderAfterHour();
							Integer reminderCpunt = event.getReminderCount();
							if (reminderHr != null && reminderCpunt != null) {
								Calendar now = Calendar.getInstance();
								now.add(Calendar.HOUR, reminderHr);
								nextLevelUser.setNextReminderTime(now.getTime());
								nextLevelUser.setReminderCount(reminderCpunt);
							}
						}
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent doApproval(RfpEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception {
		try {
			RfpEventAward eventAward = rfpEventAwardDao.findById(awardId);
			event = rfpEventService.loadRfpEventById(event.getId());
			List<RfpEventAwardApproval> approvalList = event.getAwardApprovals();
			if (CollectionUtil.isEmpty(approvalList)) {
				event.setStatus(EventStatus.FINISHED);
				event.setActionBy(loggedInUser);
				event.setActionDate(new Date());
				event.setAwardStatus(AwardStatus.APPROVED);
				event.setAwarded(Boolean.TRUE);

				try {
					RfpEventAudit eaudit = new RfpEventAudit();
					eaudit.setActionDate(new Date());
					eaudit.setActionBy(loggedInUser);
					eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
					eaudit.setEvent(event);
					eaudit.setAction(AuditActionType.Conclude);
					eventAuditService.save(eaudit);

					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = rfpAwardService.getAwardSnapShotPdf(eventAward, session, loggedInUser, awardResponsePojo, false, true);
					ByteArrayOutputStream workbook = rfpAwardService.getAwardSnapShotXcel(eventAward, session, loggedInUser, awardResponsePojo, false, true);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfpEventAwardAudit audit = null;
						audit = new RfpEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfpAwardAudit(audit);
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}

				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { eventAward.getRfxEvent().getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
				}

				try {
					tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
				} catch (Exception e) {
					LOG.error("Error updating Tat Report " + e.getMessage(), e);
				}

				LOG.info("status :" + event.getStatus());
			} else {

				if (eventAward != null) {
					try {
						RfpEventAudit audit = new RfpEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setDescription(messageSource.getMessage("event.award.sent.for.approval", new Object[] { eventAward.getRfxEvent().getEventName() }, Global.LOCALE));
						audit.setEvent(eventAward.getRfxEvent());
						audit.setAction(AuditActionType.ConcludeAward);
						eventAuditService.save(audit);

						try {
							RfpEventAwardAudit awardAudit = new RfpEventAwardAudit();
							awardAudit.setActionDate(new Date());
							awardAudit.setActionBy(loggedInUser);
							awardAudit.setBuyer(loggedInUser.getBuyer());
							awardAudit.setEvent(eventAward.getRfxEvent());
							awardAudit.setDescription(messageSource.getMessage("audit.event.sent.for.approval", new Object[] {}, Global.LOCALE));
							eventAwardAuditService.saveRfpAwardAudit(awardAudit);
						} catch (Exception e) {
							LOG.error("Error while capturing award audit " + e.getMessage(), e);
						}

						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDEAWARD, messageSource.getMessage("event.award.sent.for.approval", new Object[] { eventAward.getRfxEvent().getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
						}
						// eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				if (event.getStatus() == EventStatus.COMPLETE && Boolean.TRUE == isConclude) {
					for (RfpEventAwardApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfpEventAwardApproval currentLevel = null;
					for (RfpEventAwardApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfpAwardApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, eventAward);
					}
				}

				// Just send emails to users.
				for (RfpEventAwardApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RfpAwardApprovalUser user : approval.getApprovalUsers()) {
							if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
								LOG.info("send mail to pending approvers ");
								sendRfxApprovalEmails(event, user, RfxTypes.RFP);

							}
						}
					}
				}

			}
			event = rfpEventDao.update(event);
		} catch (Exception e) {
			LOG.info("ERROR While Approving RFP :" + e.getMessage(), e);
			throw new Exception("ERROR While Approving RFP :" + e.getMessage());
		}
		return event;
	}

	private void setNextOrAllDone(User actionBy, List<RfpEventAwardApproval> approvalList, RfpEventAwardApproval currentLevel, RfpEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, RfpEventAward eventAward) {
		// Check if all approvals are done
		currentLevel.setDone(true);
		currentLevel.setActive(false); // Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			// all approvals done
			LOG.info("All approvals for this RFP is done!!!. Going to Approved Mode.");
			event.setStatus(EventStatus.FINISHED);
			event.setActionBy(actionBy);
			event.setActionDate(new Date());
			event.setAwardStatus(AwardStatus.APPROVED);
			event.setAwarded(Boolean.TRUE);
			LOG.info("All approvals for this RFP is in Approved Mode.");

			try {
				RfpEventAudit eaudit = new RfpEventAudit();
				eaudit.setActionDate(new Date());
				eaudit.setActionBy(SecurityLibrary.getLoggedInUser());
				eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
				eaudit.setEvent(event);
				eaudit.setAction(AuditActionType.Conclude);
				eventAuditService.save(eaudit);

				AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
				JasperPrint eventAward1 = rfpAwardService.getAwardSnapShotPdf(eventAward, session, loggedInUser, awardResponsePojo, false, true);
				ByteArrayOutputStream workbook = rfpAwardService.getAwardSnapShotXcel(eventAward, session, loggedInUser, awardResponsePojo, false, true);
				if (workbook != null) {
					byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
					byte[] excelSnapshot = workbook.toByteArray();

					LOG.info("SUSPEND FILE Size : " + snapshot.length);
					RfpEventAwardAudit audit = null;
					audit = new RfpEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
					eventAwardAuditService.saveRfpAwardAudit(audit);
				}
			} catch (Exception e) {
				LOG.error("Error while :" + e.getMessage(), e);
			}
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { event.getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
			}

			try {
				tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
			} catch (Exception e) {
				LOG.error("Error updating Tat Report " + e.getMessage(), e);
			}

		} else {
			for (RfpEventAwardApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfpAwardApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFP);
						if (Boolean.TRUE == event.getEnableApprovalReminder()) {
							Integer reminderHr = event.getReminderAfterHour();
							Integer reminderCpunt = event.getReminderCount();
							if (reminderHr != null && reminderCpunt != null) {
								Calendar now = Calendar.getInstance();
								now.add(Calendar.HOUR, reminderHr);
								nextLevelUser.setNextReminderTime(now.getTime());
								nextLevelUser.setReminderCount(reminderCpunt);
							}
						}
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent doApproval(RfqEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception {
		try {
			event = rfqEventService.loadRfqEventById(event.getId());
			RfqEventAward dbRfqEventAward = rfqEventAwardDao.findById(awardId);

			List<RfqEventAwardApproval> approvalList = event.getAwardApprovals();
			if (CollectionUtil.isEmpty(approvalList)) {
				event.setStatus(EventStatus.FINISHED);

				event.setActionBy(loggedInUser);
				event.setActionDate(new Date());
				event.setAwardStatus(AwardStatus.APPROVED);
				event.setAwarded(Boolean.TRUE);

				try {
					RfqEventAudit eaudit = new RfqEventAudit();
					eaudit.setActionDate(new Date());
					eaudit.setActionBy(SecurityLibrary.getLoggedInUser());
					eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
					eaudit.setEvent(event);
					eaudit.setAction(AuditActionType.Conclude);
					eventAuditService.save(eaudit);

					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = rfqAwardService.getAwardSnapShotPdf(dbRfqEventAward, session, loggedInUser, awardResponsePojo, false, true);
					ByteArrayOutputStream workbook = rfqAwardService.getAwardSnapShotXcel(dbRfqEventAward, session, loggedInUser, awardResponsePojo, false, true);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfqEventAwardAudit audit = null;
						audit = new RfqEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
						if (dbRfqEventAward.getAttachment() != null) {
							LOG.info("File Name:-------->" + dbRfqEventAward.getAttachment().getOriginalFilename());
							try {
								audit.setFileData(dbRfqEventAward.getAttachment().getBytes());
							} catch (IOException e) {
								LOG.error("Eroor While getting file data" + e.getMessage(), e);
							}
							audit.setFileName(dbRfqEventAward.getAttachment().getOriginalFilename());
							audit.setCredContentType(dbRfqEventAward.getAttachment().getContentType());
						}
						eventAwardAuditService.saveRfqAwardAudit(audit);
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}

				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { event.getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
				}

				try {
					tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
				} catch (Exception e) {
					LOG.error("Error updating Tat Report " + e.getMessage(), e);
				}

				LOG.info("status :" + event.getStatus());
			} else {
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription(messageSource.getMessage("event.award.sent.for.approval", new Object[] { event.getEventName() }, Global.LOCALE));
					audit.setEvent(event);
					audit.setAction(AuditActionType.ConcludeAward);
					eventAuditService.save(audit);

					try {
						RfqEventAwardAudit awardAudit = new RfqEventAwardAudit();
						awardAudit.setActionDate(new Date());
						awardAudit.setActionBy(loggedInUser);
						awardAudit.setBuyer(loggedInUser.getBuyer());
						awardAudit.setEvent(event);
						awardAudit.setDescription(messageSource.getMessage("audit.event.sent.for.approval", new Object[] {}, Global.LOCALE));
						eventAwardAuditService.saveRfqAwardAudit(awardAudit);
					} catch (Exception e) {
						LOG.error("Error while capturing award audit " + e.getMessage(), e);
					}

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDEAWARD, messageSource.getMessage("event.award.sent.for.approval", new Object[] { event.getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while  capturing award audit for buyer audit trail  " + e.getMessage(), e);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				if (event.getStatus() == EventStatus.COMPLETE && Boolean.TRUE == isConclude) {
					for (RfqEventAwardApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfqEventAwardApproval currentLevel = null;
					for (RfqEventAwardApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfqAwardApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, dbRfqEventAward);
					}
				}

				// Just send emails to users.
				if (CollectionUtil.isNotEmpty(approvalList)) {
					for (RfqEventAwardApproval approval : approvalList) {
						if (approval.isActive()) {
							for (RfqAwardApprovalUser user : approval.getApprovalUsers()) {
								if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
									LOG.info("send mail to pending approvers ");
									sendRfxApprovalEmails(event, user, RfxTypes.RFQ);

								}
							}
						}
					}
				}
			}
			event = rfqEventDao.update(event);
		} catch (Exception e) {
			LOG.info("ERROR While Approving RFQ :" + e.getMessage(), e);
			throw new Exception("ERROR While Approving RFq :" + e.getMessage());
		}
		return event;
	}

	private void setNextOrAllDone(User actionBy, List<RfqEventAwardApproval> approvalList, RfqEventAwardApproval currentLevel, RfqEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, RfqEventAward dbRfqEventAward) {
		// Check if all approvals are done
		currentLevel.setDone(true);
		currentLevel.setActive(false); // Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			// all approvals done
			LOG.info("All approvals for this RFP is done!!!. Going to Approved Mode.");
			event.setStatus(EventStatus.FINISHED);
			event.setActionBy(actionBy);
			event.setActionDate(new Date());
			event.setAwardStatus(AwardStatus.APPROVED);
			event.setAwarded(Boolean.TRUE);
			LOG.info("All approvals for this RFQ is in Approved Mode.");

			try {
				RfqEventAudit eaudit = new RfqEventAudit();
				eaudit.setActionDate(new Date());
				eaudit.setActionBy(SecurityLibrary.getLoggedInUser());
				eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
				eaudit.setEvent(event);
				eaudit.setAction(AuditActionType.Conclude);
				eventAuditService.save(eaudit);

				AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
				JasperPrint eventAward1 = rfqAwardService.getAwardSnapShotPdf(dbRfqEventAward, session, loggedInUser, awardResponsePojo, false, true);
				ByteArrayOutputStream workbook = rfqAwardService.getAwardSnapShotXcel(dbRfqEventAward, session, loggedInUser, awardResponsePojo, false, true);
				if (workbook != null) {
					byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
					byte[] excelSnapshot = workbook.toByteArray();

					LOG.info("SUSPEND FILE Size : " + snapshot.length);
					RfqEventAwardAudit audit = null;
					audit = new RfqEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
					eventAwardAuditService.saveRfqAwardAudit(audit);
				}
			} catch (Exception e) {
				LOG.error("Error while :" + e.getMessage(), e);
			}
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { event.getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
			}

			try {
				tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
			} catch (Exception e) {
				LOG.error("Error updating Tat Report " + e.getMessage(), e);
			}
		} else {
			for (RfqEventAwardApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfqAwardApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFQ);
						if (Boolean.TRUE == event.getEnableApprovalReminder()) {
							Integer reminderHr = event.getReminderAfterHour();
							Integer reminderCpunt = event.getReminderCount();
							if (reminderHr != null && reminderCpunt != null) {
								Calendar now = Calendar.getInstance();
								now.add(Calendar.HOUR, reminderHr);
								nextLevelUser.setNextReminderTime(now.getTime());
								nextLevelUser.setReminderCount(reminderCpunt);
							}
						}
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent doApproval(RftEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception {
		try {
			RftEventAward eventAward = rftEventAwardDao.findById(awardId);
			event = rftEventService.loadRftEventById(event.getId());
			List<RftEventAwardApproval> approvalList = event.getAwardApprovals();
			if (CollectionUtil.isEmpty(approvalList)) {
				event.setStatus(EventStatus.FINISHED);
				event.setActionBy(loggedInUser);
				event.setActionDate(new Date());
				event.setAwardStatus(AwardStatus.APPROVED);
				event.setAwarded(Boolean.TRUE);

				try {
					RftEventAudit eaudit = new RftEventAudit();
					eaudit.setActionDate(new Date());
					eaudit.setActionBy(SecurityLibrary.getLoggedInUser());
					eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
					eaudit.setEvent(event);
					eaudit.setAction(AuditActionType.Conclude);
					eventAuditService.save(eaudit);

					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = rftAwardService.getAwardSnapShotPdf(eventAward, session, loggedInUser, awardResponsePojo, false, true);
					ByteArrayOutputStream workbook = rftAwardService.getAwardSnapshotExcel(eventAward, session, loggedInUser, awardResponsePojo, false, true);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RftEventAwardAudit audit = null;
						audit = new RftEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
						eventAwardAuditService.saveRftAwardAudit(audit);
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { event.getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
				}

				try {
					tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
				} catch (Exception e) {
					LOG.error("Error updating Tat Report " + e.getMessage(), e);
				}
				LOG.info("status :" + event.getStatus());
			} else {

				if (eventAward != null) {
					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setDescription(messageSource.getMessage("event.award.sent.for.approval", new Object[] { eventAward.getRfxEvent().getEventName() }, Global.LOCALE));
						audit.setEvent(eventAward.getRfxEvent());
						audit.setAction(AuditActionType.ConcludeAward);
						eventAuditService.save(audit);

						try {
							RftEventAwardAudit awardAudit = new RftEventAwardAudit();
							awardAudit.setActionDate(new Date());
							awardAudit.setActionBy(loggedInUser);
							awardAudit.setBuyer(loggedInUser.getBuyer());
							awardAudit.setEvent(eventAward.getRfxEvent());
							awardAudit.setDescription(messageSource.getMessage("audit.event.sent.for.approval", new Object[] {}, Global.LOCALE));
							eventAwardAuditService.saveRftAwardAudit(awardAudit);
						} catch (Exception e) {
							LOG.error("Error while capturing award audit " + e.getMessage(), e);
						}

						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDEAWARD, messageSource.getMessage("event.award.sent.for.approval", new Object[] { eventAward.getRfxEvent().getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while  capturing award audit for buyer audit trail  " + e.getMessage(), e);
						}
						// eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				if (event.getStatus() == EventStatus.COMPLETE && Boolean.TRUE == isConclude) {
					for (RftEventAwardApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RftEventAwardApproval currentLevel = null;
					for (RftEventAwardApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RftAwardApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, eventAward);
					}
				}

				// Just send emails to users.
				for (RftEventAwardApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RftAwardApprovalUser user : approval.getApprovalUsers()) {
							if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
								LOG.info("send mail to pending approvers ");
								sendRfxApprovalEmails(event, user, RfxTypes.RFT);
							}
						}
					}
				}
			}
			event = rftEventDao.update(event);
		} catch (Exception e) {
			LOG.info("ERROR While Approving RFt :" + e.getMessage(), e);
			throw new Exception("ERROR While Approving RFt :" + e.getMessage());
		}
		return event;
	}

	private void setNextOrAllDone(User actionBy, List<RftEventAwardApproval> approvalList, RftEventAwardApproval currentLevel, RftEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, RftEventAward eventAward) {
		// Check if all approvals are done
		currentLevel.setDone(true);
		currentLevel.setActive(false); // Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			// all approvals done
			LOG.info("All approvals for this RFP is done!!!. Going to Approved Mode.");
			event.setStatus(EventStatus.FINISHED);
			event.setActionBy(actionBy);
			event.setActionDate(new Date());
			event.setAwardStatus(AwardStatus.APPROVED);
			event.setAwarded(Boolean.TRUE);
			LOG.info("All approvals for this RFP is in Approved Mode.");

			try {
				RftEventAudit eaudit = new RftEventAudit();
				eaudit.setActionDate(new Date());
				eaudit.setActionBy(SecurityLibrary.getLoggedInUser());
				eaudit.setDescription("Event '" + event.getEventName() + "' is awarded & concluded.");
				eaudit.setEvent(event);
				eaudit.setAction(AuditActionType.Conclude);
				eventAuditService.save(eaudit);

				AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
				JasperPrint eventAward1 = rftAwardService.getAwardSnapShotPdf(eventAward, session, loggedInUser, awardResponsePojo, false, true);
				ByteArrayOutputStream workbook = rftAwardService.getAwardSnapshotExcel(eventAward, session, loggedInUser, awardResponsePojo, false, true);
				if (workbook != null) {
					byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
					byte[] excelSnapshot = workbook.toByteArray();

					LOG.info("SUSPEND FILE Size : " + snapshot.length);
					RftEventAwardAudit audit = null;
					audit = new RftEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Concluded", snapshot, excelSnapshot);
					eventAwardAuditService.saveRftAwardAudit(audit);
				}
			} catch (Exception e) {
				LOG.error("Error while :" + e.getMessage(), e);
			}

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, messageSource.getMessage("buyer.audit.conclude", new Object[] { event.getEventId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while  capturing award audit for buyer audit trail " + e.getMessage(), e);
			}

			try {
				tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
			} catch (Exception e) {
				LOG.error("Error updating Tat Report " + e.getMessage(), e);
			}

		} else {
			for (RftEventAwardApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RftAwardApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFT);
						if (Boolean.TRUE == event.getEnableApprovalReminder()) {
							Integer reminderHr = event.getReminderAfterHour();
							Integer reminderCpunt = event.getReminderCount();
							if (reminderHr != null && reminderCpunt != null) {
								Calendar now = Calendar.getInstance();
								now.add(Calendar.HOUR, reminderHr);
								nextLevelUser.setNextReminderTime(now.getTime());
								nextLevelUser.setReminderCount(reminderCpunt);
							}
						}
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent doApproval(RfaEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException, ApplicationException {
		event = rfaEventService.loadRfaEventById(event.getId());
		List<RfaEventAwardApproval> approvalList = event.getAwardApprovals();
		List<RfaTeamMember> teamMembers = event.getTeamMembers();

		// Identify Current Approval Level
		RfaEventAwardApproval currentLevel = null;
		for (RfaEventAwardApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfaAwardApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfaAwardApprovalUser user : currentLevel.getApprovalUsers()) {
				if (user.getUser().getId().equals(actionBy.getId())) {
					actionUser = user;
					LOG.info("Approval being done by : " + actionBy.getLoginId());
				}
			}
		}

		if (actionUser == null) {
			// throw error
			LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFA '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
			throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFA '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
		}

		if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
			// throw error
			LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFA at : " + actionUser.getActionDate());
			throw new NotAllowedException("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFA at : " + actionUser.getActionDate());
		}

		// adding remarks into comments
		if (event.getComment() == null) {
			event.setAwardComment(new ArrayList<RfaAwardComment>());
		}
		RfaAwardComment comment = new RfaAwardComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getAwardComment().add(comment);

		// If rejected
		if (!approved) {

			// Reset all approvals for re-approval as the Event is rejected.
			for (RfaEventAwardApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfaAwardApprovalUser user : approval.getApprovalUsers()) {
					if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
						// Send rejection email
						sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
					}

					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfaTeamMember teamMember : teamMembers) {
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
			}
			
			
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Award Rejected the RFA : " + event.getEventName());
			event.setStatus(EventStatus.COMPLETE);
			event.setAwardStatus(AwardStatus.DRAFT);
			event.setAwarded(Boolean.FALSE);
			// rftEventDao.update(event);

			try {
				// RfaEventAudit audit = new RfaEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.rejected", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Reject);
				// eventAuditService.save(audit);

				RfaEventAwardAudit awardAudit = new RfaEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setEvent(event);
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.rejected", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRfaAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_REJECT, messageSource.getMessage("event.award.rejected", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
				}
			} catch (Exception e) {
				LOG.info("ERROR while rejected Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.info("User " + actionBy.getName() + " has Approved the RFA : " + event.getEventName());
			actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			// Send email notification to Creator
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, true, event.getReferanceNumber());
			// Send to Associate Owner
			// if (CollectionUtil.isNotEmpty(associateOwns)) {
			// for (User assOwner : associateOwns) {
			// sentRfxApprovalEmail(assOwner, event.getId(), event.getEventName(), event.getEventId(), actionBy,
			// remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
			// }
			// }
			
			
			for(RfaTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
			}

			try {

				// RfaEventAudit audit = new RfaEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.approved", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Approve);
				// eventAuditService.save(audit);

				RfaEventAwardAudit awardAudit = new RfaEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.approved", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRfaAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_APPROVE, messageSource.getMessage("event.award.approved", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFA Event Audit " + e.getMessage());
			}

			RfaEventAward eventAward = rfaAwardService.rfaEventAwardDetailsByEventIdandBqId(event.getId(), bqId);
			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfaAwardApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFA.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFA.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rfaEventDao.update(event);
	}

	/**
	 * @param eventId
	 * @param eventName
	 * @param actionBy
	 * @param remarks
	 * @param ownerUser
	 * @param type
	 * @param self
	 */
	private void sentRfxApprovalEmail(User mailTo, String id, String eventName, String eventId, User actionBy, String remarks, User ownerUser, RfxTypes type, boolean self, String referenceNumber) {
		LOG.info("Sending approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Award Approved";
		String url = APP_URL + "/buyer/" + type.name() + "/eventAward/" + id;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("actionByName", actionBy.getName());
		map.put("eventName", eventName);
		map.put("eventType", type.name());
		map.put("eventId", eventId);
		map.put("remarks", remarks);
		map.put("referenceNumber", referenceNumber);
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(id, type)));
		if (self) {
			map.put("message", "You have approved the award for following event");
		} else {
			map.put("message", actionBy.getName() + " has approved the award for following event");
		}
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.AWARD_APPROVAL_REQUEST_APPROVAL);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("event.approval.notification.message", new Object[] { actionBy.getName(), type.name(), eventName, remarks }, Global.LOCALE);
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

		if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", eventId);
				payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
				payload.put("eventType", type.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(mailTo.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event approve Mobile push notification to '" + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
		}
	}

	/**
	 * @param eventId
	 * @param eventName
	 * @param actionBy
	 * @param remarks
	 * @param ownerUser
	 * @param type
	 */
	private void sentRfxRejectionEmail(User mailTo, String id, String eventName, String eventId, User actionBy, String remarks, User ownerUser, RfxTypes type, String referanceNumber) {
		LOG.info("Sending Award rejected email notifi to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Award Rejected";
		String url = APP_URL + "/buyer/" + type.name() + "/eventAward/" + eventId;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("eventId", eventId);
		map.put("actionByName", actionBy.getName());
		map.put("eventName", eventName);
		map.put("eventType", type.name());
		map.put("remarks", remarks);
		map.put("referanceNumber", referanceNumber);
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(id, type)));
		if (mailTo.getId().equals(actionBy.getId())) {
			map.put("message", "You have rejected the award for the following event");
		} else {
			map.put("message", actionBy.getName() + " has rejected the award for following event");
		}
		TimeZone timeZone = TimeZone.getDefault();
		String timeZoneStr = "GMT+8:00";
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		try {
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(actionBy.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZoneStr = settings.getTimeZone().getTimeZone();
			} else {
				timeZoneStr = "GMT+8:00";
			}
			timeZone = TimeZone.getTimeZone(timeZoneStr);
			sdf.setTimeZone(timeZone);
		} catch (Exception e) {
		} finally {
			sdf.setTimeZone(timeZone);
		}
		map.put("date", sdf.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.AWARD_EVENT_REJECT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("event.rejection.notification.message", new Object[] { actionBy.getName(), type.name(), eventName, remarks }, Global.LOCALE);
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

		if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", eventId);
				payload.put("messageType", NotificationType.REJECT_MESSAGE.toString());
				payload.put("eventType", type.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(mailTo.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Suspended Event reject Mobile push notification to '" + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent doApproval(RfpEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException {
		event = rfpEventService.loadRfpEventById(event.getId());
		List<RfpEventAwardApproval> approvalList = event.getAwardApprovals();
		List<RfpTeamMember> teamMembers = event.getTeamMembers();

		// Identify Current Approval Level
		RfpEventAwardApproval currentLevel = null;
		for (RfpEventAwardApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfpAwardApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfpAwardApprovalUser user : currentLevel.getApprovalUsers()) {
				if (user.getUser().getId().equals(actionBy.getId())) {
					actionUser = user;
					LOG.info("Approval being done by : " + actionBy.getLoginId());
				}
			}
		}
		if (actionUser == null) {
			// throw error
			LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFP '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
			throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFP '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
		}

		if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
			// throw error
			LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFP at : " + actionUser.getActionDate());
			throw new NotAllowedException("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFP at : " + actionUser.getActionDate());
		}

		// adding remarks into comments
		if (event.getComment() == null) {
			event.setComment(new ArrayList<RfpComment>());
		}
		RfpAwardComment comment = new RfpAwardComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getAwardComment().add(comment);

		// If rejected
		if (!approved) {
			// Reset all approvals for re-approval as the Event is rejected.
			for (RfpEventAwardApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfpAwardApprovalUser user : approval.getApprovalUsers()) {
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());

					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfpTeamMember teamMember : teamMembers) {
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
			}
			
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Award Rejected the RFP : " + event.getEventName());
			event.setStatus(EventStatus.COMPLETE);
			event.setAwardStatus(AwardStatus.DRAFT);
			event.setAwarded(Boolean.FALSE);

			try {
				// RfpEventAudit audit = new RfpEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.rejected", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Reject);
				// eventAuditService.save(audit);

				RfpEventAwardAudit awardAudit = new RfpEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.rejected", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRfpAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_REJECT, messageSource.getMessage("event.award.rejected", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
				}

			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.info("User " + actionBy.getName() + " has Approved the RFP : " + event.getEventName());
			actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			// Send email notification to Creator
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(),event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, true, event.getReferanceNumber());
			
			for(RfpTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
			}

			try {
				// RfpEventAudit audit = new RfpEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.approved", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Approve);
				// eventAuditService.save(audit);

				RfpEventAwardAudit awardAudit = new RfpEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.approved", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRfpAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_APPROVE, messageSource.getMessage("event.award.approved", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFP Event Audit " + e.getMessage());
			}

			RfpEventAward eventAward = rfpAwardService.rfpEventAwardDetailsByEventIdandBqId(event.getId(), bqId);
			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfpAwardApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFP.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFP.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rfpEventDao.update(event);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent doApproval(RfqEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException {
		event = rfqEventService.loadRfqEventById(event.getId());
		List<RfqEventAwardApproval> approvalList = event.getAwardApprovals();
		List<RfqTeamMember> teamMembers = event.getTeamMembers();

		// Identify Current Approval Level
		RfqEventAwardApproval currentLevel = null;
		for (RfqEventAwardApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfqAwardApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfqAwardApprovalUser user : currentLevel.getApprovalUsers()) {
				if (user.getUser().getId().equals(actionBy.getId())) {
					actionUser = user;
					LOG.info("Approval being done by : " + actionBy.getLoginId());
				}
			}
		}
		if (actionUser == null) {
			// throw error
			LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFQ '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
			throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFQ '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
		}

		if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
			// throw error
			LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFQ at : " + actionUser.getActionDate());
			throw new NotAllowedException("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFQ at : " + actionUser.getActionDate());
		}

		// adding remarks into comments
		if (event.getComment() == null) {
			event.setAwardComment(new ArrayList<RfqAwardComment>());
		}
		RfqAwardComment comment = new RfqAwardComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getAwardComment().add(comment);

		// If rejected
		if (!approved) {
			LOG.info("++++++++++++++++++++++++++++========================= rejection ");

			// Reset all approvals for re-approval as the Event is rejected.
			for (RfqEventAwardApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfqAwardApprovalUser user : approval.getApprovalUsers()) {
					// Sending rejected email notification to Approver
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfqTeamMember teamMember : teamMembers) {
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
				
			}
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Award Rejected the RFQ : " + event.getEventName());
			event.setStatus(EventStatus.COMPLETE);
			event.setAwardStatus(AwardStatus.DRAFT);
			event.setAwarded(Boolean.FALSE);
			// rftEventDao.update(event);

			try {
				// RfqEventAudit audit = new RfqEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.rejected", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Reject);
				// eventAuditService.save(audit);

				RfqEventAwardAudit awardAudit = new RfqEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.rejected", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRfqAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_REJECT, messageSource.getMessage("event.award.rejected", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
				}
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.info("User " + actionBy.getName() + " has Approved the RFQ : " + event.getEventName());
			actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			// Send email notification to Creator
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, true, event.getReferanceNumber());
			
			for(RfqTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
				
			}

			try {
				// RfqEventAudit audit = new RfqEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.approved", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Approve);
				// eventAuditService.save(audit);

				RfqEventAwardAudit awardAudit = new RfqEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.approved", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRfqAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_APPROVE, messageSource.getMessage("event.award.approved", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFQ Event Audit " + e.getMessage());
			}

			RfqEventAward eventAward = rfqAwardService.rfqEventAwardDetailsByEventIdandBqId(event.getId(), bqId);
			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfqAwardApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFQ.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFQ.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rfqEventDao.update(event);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent doApproval(RftEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException {
		LOG.info("********************************** ");
		event = rftEventService.loadRftEventById(event.getId());
		List<RftEventAwardApproval> approvalList = event.getAwardApprovals();
		List<RftTeamMember> teamMembers = event.getTeamMembers();

		// Identify Current Approval Level
		RftEventAwardApproval currentLevel = null;
		for (RftEventAwardApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RftAwardApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RftAwardApprovalUser user : currentLevel.getApprovalUsers()) {
				if (user.getUser().getId().equals(actionBy.getId())) {
					actionUser = user;
					LOG.info("Approval being done by : " + actionBy.getLoginId());
				}
			}
		}
		if (actionUser == null) {
			// throw error
			LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFT '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
			throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFT '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
		}

		if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
			// throw error
			LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFT at : " + actionUser.getActionDate());
			throw new NotAllowedException("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFT at : " + actionUser.getActionDate());
		}

		// adding remarks into comments
		if (event.getComment() == null) {
			event.setAwardComment(new ArrayList<RftAwardComment>());
		}
		RftAwardComment comment = new RftAwardComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getAwardComment().add(comment);

		// If rejected
		if (!approved) {
			LOG.info("++++++++++++++++++++++++++++========================= rejection ");

			// Reset all approvals for re-approval as the Event is rejected.
			for (RftEventAwardApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RftAwardApprovalUser user : approval.getApprovalUsers()) {
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RftTeamMember teamMember : teamMembers) {
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
				
			}
			
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Award Rejected the RFT : " + event.getEventName());
			event.setStatus(EventStatus.COMPLETE);
			event.setAwardStatus(AwardStatus.DRAFT);
			event.setAwarded(Boolean.FALSE);

			try {
				// RftEventAudit audit = new RftEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.rejected", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Reject);
				// eventAuditService.save(audit);

				RftEventAwardAudit awardAudit = new RftEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.rejected", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRftAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_REJECT, messageSource.getMessage("event.award.rejected", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
				}
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.info("User " + actionBy.getName() + " has Approved the RFT : " + event.getEventName());
			actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			// Send email notification to Creator
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, true, event.getReferanceNumber());
			
			for(RftTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), event.getEventId(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());
					
				} catch (Exception e) {
					LOG.error("Error while sending email to team member " + e.getMessage(), e);
				}
				
			}

			try {

				// RftEventAudit audit = new RftEventAudit();
				// audit.setActionDate(new Date());
				// audit.setActionBy(actionBy);
				// audit.setDescription(messageSource.getMessage("event.award.approved", new Object[] {
				// event.getEventName(), remarks }, Global.LOCALE));
				// audit.setEvent(event);
				// audit.setAction(AuditActionType.Approve);
				// eventAuditService.save(audit);

				RftEventAwardAudit awardAudit = new RftEventAwardAudit();
				awardAudit.setActionDate(new Date());
				awardAudit.setActionBy(actionBy);
				awardAudit.setEvent(event);
				awardAudit.setBuyer(actionBy.getBuyer());
				awardAudit.setDescription(messageSource.getMessage("audit.event.award.approved", new Object[] {}, Global.LOCALE));
				eventAwardAuditService.saveRftAwardAudit(awardAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD_APPROVE, messageSource.getMessage("event.award.approved", new Object[] { event.getEventId(), remarks }, Global.LOCALE), actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFT Event Audit " + e.getMessage());
			}

			RftEventAward eventAward = rftAwardService.rftEventAwardDetailsByEventIdandBqId(event.getId(), bqId);
			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RftAwardApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFT.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFT.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, eventAward);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rftEventDao.update(event);
	}

}
