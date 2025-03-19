/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.ApprovalUser;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfaSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfaSuspensionComment;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfiEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfiSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfiSuspensionComment;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfpComment;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfpSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfpSuspensionComment;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfqSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfqSuspensionComment;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RftSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RftSuspensionComment;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.PublishEventService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaEventMeetingService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.SuspensionApprovalService;
import com.privasia.procurehere.service.TatReportService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author jayshree
 */

@Service
@Transactional(readOnly = true)
public class SuspensionApprovalServiceImpl implements SuspensionApprovalService {

	public static final Logger LOG = LogManager.getLogger(SuspensionApprovalServiceImpl.class);

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
	PublishEventService publishEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public RfaEvent doApproval(RfaEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) throws Exception {
		try {
			event = rfaEventService.loadRfaEventById(event.getId());
			List<RfaEventSuspensionApproval> approvalList = event.getSuspensionApprovals(); // rfaEventDao.getAllApprovalsForEvent(event.getId());
			if (CollectionUtil.isEmpty(approvalList) && Boolean.FALSE == event.getEnableSuspensionApproval()) {
				event.setStatus(EventStatus.ACTIVE);
				LOG.info("status :" + event.getStatus());

				// snapShotAuditService.doRfaAudit(event, session, event, loggedInUser, AuditActionType.Approve,
				// "event.audit.approved", virtualizer);
			} else {
				if (event.getStatus() == EventStatus.SUSPENDED) {
					// event.setStatus(EventStatus.PENDING);
					for (RfaEventSuspensionApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfaEventSuspensionApproval currentLevel = null;
					for (RfaEventSuspensionApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfaSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, model);
					}
				}

				// Just send emails to users.
				for (RfaEventSuspensionApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RfaSuspensionApprovalUser user : approval.getApprovalUsers()) {
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
		String subject = "Suspended Event Approval Request";
		String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", user.getUser().getName());
		map.put("event", event);
		map.put("eventType", type.name());
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.SUSPEND_EVENT_APPROVAL_REQUEST_TEMPLATE);
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

	private void setNextOrAllDone(User actionBy, List<RfaEventSuspensionApproval> approvalList, RfaEventSuspensionApproval currentLevel, RfaEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) throws ApplicationException {
		currentLevel.setDone(true);
		currentLevel.setActive(false);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		dateTimeFormat.setTimeZone(timeZone);

		// Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			final RfaEvent theEvent = event;

			if ((event.getAuctionResumeDateTime()) != null) {
				// Send Resume notification to suppliers
				try {
					if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {
						try {
							// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
							jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
								@Override
								public Message createMessage(Session session) throws JMSException {
									TextMessage objectMessage = session.createTextMessage();
									objectMessage.setText(theEvent.getId());
									return objectMessage;
								}
							});
						} catch (Exception e) {
							LOG.error("Error sending message to queue : " + e.getMessage(), e);
						}
					}
				} catch (Exception e) {
					LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
				}
				try {

					try {
						jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(theEvent.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}

				} catch (Exception e) {
					LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
				}
			} else {
				if (event.getEventStart() != null && event.getEventStart().after(new Date())) {

					event.setStatus(EventStatus.ACTIVE);
					event.setSuspendRemarks(null);
					event = rfaEventService.updateRfaEvent(event);

					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						snapShotAuditService.doRfaAudit(event, session, event, loggedInUser, AuditActionType.Resume, "rfaevent.audit.resume", virtualizer);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event'" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}

					// Resume the Dutch Auction
					if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
						AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(event.getId());
						LOG.info("auctionRules:" + rfaEventService.getAuctionRulesByEventId(event.getId()));
						try {
							rfaEventService.scheduleAuction(auctionRules);
						} catch (ApplicationException e) {
							LOG.error("Error while scheduleAuction .... " + e.getMessage());
						}
					}

					// Send Resume notification to suppliers
					try {
						if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {
							try {
								// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
								jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
									@Override
									public Message createMessage(Session session) throws JMSException {
										TextMessage objectMessage = session.createTextMessage();
										objectMessage.setText(theEvent.getId());
										return objectMessage;
									}
								});
							} catch (Exception e) {
								LOG.error("Error sending message to queue : " + e.getMessage(), e);
							}
						}
					} catch (Exception e) {
						LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
					}

					// Send notification to new suppliers
					try {
						try {
							// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
							jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
								@Override
								public Message createMessage(Session session) throws JMSException {
									TextMessage objectMessage = session.createTextMessage();
									objectMessage.setText(theEvent.getId());
									return objectMessage;
								}
							});
						} catch (Exception e) {
							LOG.error("Error sending message to queue : " + e.getMessage(), e);
						}

					} catch (Exception e) {
						LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
					}
					model.addAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));
				}
			}

			event.setStatus(EventStatus.ACTIVE);
			event.setSuspendRemarks(null);
			resumeAuction(event);
			event = rfaEventService.updateRfaEvent(event);

			if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(event.getId());
				try {
					rfaEventService.scheduleAuction(auctionRules);
				} catch (ApplicationException e) {
					LOG.error("Error while scheduleAuction .... " + e.getMessage(), e);
				}
			}

			LOG.info("-------------------------------BEFORE AUDIT ---------------------------------------------");
			snapShotAuditService.doRfaAudit(event, session, event, loggedInUser, AuditActionType.Resume, "rfaEvent.resume", virtualizer);

			LOG.info("-------------------------------AFTER AUDIT---------------------------------------------");

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event'" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			try {
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
			} catch (Exception e) {
				LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
			}

			if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
				tatReportService.updateTatReportEventStatus(event.getEventId(), event.getPreviousRequestId(), loggedInUser.getTenantId(), EventStatus.ACTIVE);
			}
			LOG.info("All approvals for this RFA is in Approved Mode.");
		} else {
			for (RfaEventSuspensionApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfaSuspensionApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFA);
					}
					break;
				}
			}
		}
	}

	private void resumeAuction(RfaEvent event) {

		AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(event.getId());

		RfaEvent eventObj = rfaEventService.getRfaEventByeventId(event.getId());
		if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
			BigDecimal differenceAmount = BigDecimal.ZERO;
			LOG.info("AUction Rules : " + auctionRules.getDutchMinimumPrice());
			if (Boolean.TRUE == auctionRules.getFowardAuction()) {
				differenceAmount = auctionRules.getDutchAuctionCurrentStepAmount().subtract(auctionRules.getDutchMinimumPrice());
			} else {
				differenceAmount = auctionRules.getDutchMinimumPrice().subtract(auctionRules.getDutchAuctionCurrentStepAmount());
			}
			LOG.info(" differenceAmount : " + differenceAmount);

			BigDecimal amountDivide = (differenceAmount.divide(auctionRules.getAmountPerIncrementDecrement(), (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2)));
			LOG.info("RESUME amountDivide : " + amountDivide);
			int amountDivision = amountDivide.setScale(0, RoundingMode.CEILING).intValue();
			LOG.info("RESUME amount : " + amountDivision);
			int totalDuration = ((amountDivision * auctionRules.getInterval()) + auctionRules.getInterval());
			LOG.info("RESUME total duration : " + event.getEventEnd());
			event.setAuctionDuration(totalDuration);
			auctionRules.setDutchAuctionTotalStep(totalDuration / auctionRules.getInterval());
			LOG.info("RESUME total step : " + auctionRules.getDutchAuctionTotalStep());

			LOG.info("Revised auction end time : " + event.getEventEnd());
			eventObj = rfaEventService.updateRfaEvent(event);

			// auctionRules.setDutchAuctionTotalStep(event.getAuctionDuration() / auctionRules.getInterval());
			auctionRules.setDutchAuctionCurrentStep(0);
			auctionRules.setDutchStartPrice(auctionRules.getDutchAuctionCurrentStepAmount());
			auctionRules.setDutchAuctionCurrentStepAmount(auctionRules.getDutchAuctionCurrentStepAmount());
		}
		auctionRules.setEvent(eventObj);
		auctionRules.setAuctionStarted(Boolean.FALSE);
		rfaEventService.updateAuctionRules(auctionRules);

		try {
			if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

				try {
					// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
					jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(event.getId());
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}

			}
		} catch (Exception e) {
			LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvent doApproval(RfiEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) throws Exception {
		try {
			event = rfiEventService.loadRfiEventById(event.getId());
			List<RfiEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
			if (CollectionUtil.isEmpty(approvalList) && Boolean.FALSE == event.getEnableSuspensionApproval()) {
				event.setStatus(EventStatus.ACTIVE);
				LOG.info("status :" + event.getStatus());

				// snapShotAuditService.doRfpAudit(event, session, event, loggedInUser, AuditActionType.Approve,
				// "event.audit.approved", virtualizer);

			} else {
				if (event.getStatus() == EventStatus.SUSPENDED) {
					// event.setStatus(EventStatus.ACTIVE);
					for (RfiEventSuspensionApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfiEventSuspensionApproval currentLevel = null;
					for (RfiEventSuspensionApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfiSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, model);
					}
				}

				// Just send emails to users.
				for (RfiEventSuspensionApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RfiSuspensionApprovalUser user : approval.getApprovalUsers()) {
							if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
								LOG.info("send mail to pending approvers ");
								sendRfxApprovalEmails(event, user, RfxTypes.RFI);
							}
						}
					}
				}
			}
			event = rfiEventDao.update(event);
		} catch (Exception e) {
			LOG.info("ERROR While Approving RFI :" + e.getMessage(), e);
			throw new Exception("ERROR While Approving RFI :" + e.getMessage());
		}
		return event;
	}

	private void setNextOrAllDone(User actionBy, List<RfiEventSuspensionApproval> approvalList, RfiEventSuspensionApproval currentLevel, RfiEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) {
		currentLevel.setDone(true);
		currentLevel.setActive(false);
		// Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			LOG.info("All approvals for this RFi is done!!!. Going to Approved Mode.");

			rfiEventService.resumeEvent(event);

			LOG.info("---------------------------------BEFORE AUDIT-----------------------------------");
			User user = SecurityLibrary.getLoggedInUser();
			snapShotAuditService.doRfiAudit(event, session, event, user, AuditActionType.Resume, "event.audit.resume", virtualizer);

			LOG.info("---------------------------------AFTER AUDIT-----------------------------------");

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event'" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			try {
				LOG.info("publishing rfi resume event to epiportal");
				publishEventService.pushRfiEvent(event.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), event.getStatus() == EventStatus.APPROVED ? true : false);
			} catch (Exception e) {
				LOG.error("Error while publishing RFI event to EPortal:" + e.getMessage(), e);
			}
			try {
				// Send notification to suppliers
				try {
					// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
					jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(event.getId());
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}

			} catch (Exception e) {
				LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
			}
			try {
				// Send Resume notification to suppliers
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {

							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}

				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
			}
			try {
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
			} catch (Exception e) {
				LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
			}

			if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
				tatReportService.updateTatReportEventStatus(event.getEventId(), event.getPreviousRequestId(), loggedInUser.getTenantId(), EventStatus.ACTIVE);
			}
			model.addAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));

		} else {
			for (RfiEventSuspensionApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfiSuspensionApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFI);
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent doApproval(RfpEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) throws Exception {
		try {
			event = rfpEventService.loadRfpEventById(event.getId());
			List<RfpEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
			if (CollectionUtil.isEmpty(approvalList) && Boolean.FALSE == event.getEnableSuspensionApproval()) {
				event.setStatus(EventStatus.ACTIVE);
				LOG.info("status :" + event.getStatus());

				// snapShotAuditService.doRfpAudit(event, session, event, loggedInUser, AuditActionType.Approve,
				// "event.audit.approved", virtualizer);

			} else {
				if (event.getStatus() == EventStatus.SUSPENDED) {
					// event.setStatus(EventStatus.ACTIVE);
					for (RfpEventSuspensionApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfpEventSuspensionApproval currentLevel = null;
					for (RfpEventSuspensionApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfpSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, model);
					}
				}

				// Just send emails to users.
				for (RfpEventSuspensionApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RfpSuspensionApprovalUser user : approval.getApprovalUsers()) {
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

	private void setNextOrAllDone(User actionBy, List<RfpEventSuspensionApproval> approvalList, RfpEventSuspensionApproval currentLevel, RfpEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) {
		currentLevel.setDone(true);
		currentLevel.setActive(false); // Check if all approvals are done
		// Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {

			rfpEventService.resumeEvent(event);
			LOG.info("-----------------------------------BEFORE LOG--------------------------------------------");
			User user = SecurityLibrary.getLoggedInUser();
			snapShotAuditService.doRfpAudit(event, session, event, user, AuditActionType.Resume, "event.audit.resume", virtualizer);
			LOG.info("-----------------------------------AFTER LOG--------------------------------------------");

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event'" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			try {
				// Send notification to suppliers
				LOG.info("in finish");

				try {
					// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
					jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(event.getId());
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}

				try {
					simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
				} catch (Exception e) {
					LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
			}
			try {
				// Send Resume notification to suppliers
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}

				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
			}

			if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
				tatReportService.updateTatReportEventStatus(event.getEventId(), event.getPreviousRequestId(), loggedInUser.getTenantId(), EventStatus.ACTIVE);
			}
			model.addAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));

		} else {
			for (RfpEventSuspensionApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfpSuspensionApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFP);
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent doApproval(RfqEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) throws Exception {
		try {
			event = rfqEventService.loadRfqEventById(event.getId());
			List<RfqEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
			if (CollectionUtil.isEmpty(approvalList) && Boolean.FALSE == event.getEnableSuspensionApproval()) {
				event.setStatus(EventStatus.ACTIVE);
				LOG.info("status :" + event.getStatus());

				// snapShotAuditService.doRfpAudit(event, session, event, loggedInUser, AuditActionType.Approve,
				// "event.audit.approved", virtualizer);

			} else {
				if (event.getStatus() == EventStatus.SUSPENDED) {
					// event.setStatus(EventStatus.ACTIVE);
					for (RfqEventSuspensionApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RfqEventSuspensionApproval currentLevel = null;
					for (RfqEventSuspensionApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RfqSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, model);
					}
				}

				// Just send emails to users.
				if (CollectionUtil.isNotEmpty(approvalList)) {
					for (RfqEventSuspensionApproval approval : approvalList) {
						if (approval.isActive()) {
							for (RfqSuspensionApprovalUser user : approval.getApprovalUsers()) {
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

	private void setNextOrAllDone(User actionBy, List<RfqEventSuspensionApproval> approvalList, RfqEventSuspensionApproval currentLevel, RfqEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) {
		currentLevel.setDone(true);
		currentLevel.setActive(false);
		// Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			LOG.info("All approvals for this RFq is done!!!. Going to Approved Mode.");

			rfqEventService.resumeEvent(event);
			try {
				LOG.info("publishing rfq resume event to epiportal");
				publishEventService.pushRfqEvent(event.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), event.getStatus() == EventStatus.APPROVED ? true : false);
			} catch (Exception e) {
				LOG.error("Error while publishing RFQ event to EPortal:" + e.getMessage(), e);
			}
			try {
				// Send notification to suppliers
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
			}
			try {
				// Send Resume notification to suppliers
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
			}
			LOG.info("-------------BEFORE AUDIT ASCYNC------------------- ");
			User loggedInuser = SecurityLibrary.getLoggedInUser();
			snapShotAuditService.doRfqAudit(event, session, event, loggedInuser, AuditActionType.Resume, "event.audit.resume", virtualizer);
			LOG.info("-------------AFTER AUDIT ASCYNC------------------- ");

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event'" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			try {
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
			} catch (Exception e) {
				LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
			}

			if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
				tatReportService.updateTatReportEventStatus(event.getEventId(), event.getPreviousRequestId(), loggedInuser.getTenantId(), EventStatus.ACTIVE);
			}
			model.addAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));

		} else {
			for (RfqEventSuspensionApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);
					for (RfqSuspensionApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFQ);
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent doApproval(RftEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) throws Exception {
		try {
			event = rftEventService.loadRftEventById(event.getId());
			List<RftEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
			if (CollectionUtil.isEmpty(approvalList) && Boolean.FALSE == event.getEnableSuspensionApproval()) {
				event.setStatus(EventStatus.ACTIVE);
				LOG.info("status :" + event.getStatus());

				// snapShotAuditService.doRfpAudit(event, session, event, loggedInUser, AuditActionType.Approve,
				// "event.audit.approved", virtualizer);

			} else {
				if (event.getStatus() == EventStatus.SUSPENDED) {
					// event.setStatus(EventStatus.ACTIVE);
					for (RftEventSuspensionApproval approval : approvalList) {
						if (approval.getLevel() == 1) {
							approval.setActive(true);
							break;
						}
					}
				} else {
					RftEventSuspensionApproval currentLevel = null;
					for (RftEventSuspensionApproval approval : approvalList) {
						if (approval.isActive()) {
							currentLevel = approval;
							break;
						}
					}
					boolean allUsersDone = true;
					if (currentLevel != null) {
						for (RftSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
							if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
								LOG.info("All users of this level have not approved the Event.");
								allUsersDone = false;
								break;
							}
						}
					}
					if (allUsersDone) {
						setNextOrAllDone(null, approvalList, currentLevel, event, session, loggedInUser, virtualizer, model);
					}
				}

				// Just send emails to users.
				for (RftEventSuspensionApproval approval : approvalList) {
					if (approval.isActive()) {
						for (RftSuspensionApprovalUser user : approval.getApprovalUsers()) {
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

	private void setNextOrAllDone(User actionBy, List<RftEventSuspensionApproval> approvalList, RftEventSuspensionApproval currentLevel, RftEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Model model) {
		currentLevel.setDone(true);
		currentLevel.setActive(false);
		// Check if all approvals are done
		if (currentLevel.getLevel() == approvalList.size()) {
			// all approvals done
			LOG.info("All approvals for this RFt is done!!!. Going to Approved Mode.");
			event.setStatus(EventStatus.ACTIVE);
			event.setActionBy(actionBy);
			event.setActionDate(new Date());

			rftEventService.resumeEvent(event);
			LOG.info("--------------BEFORE AUDIT------------------");
			snapShotAuditService.doRftAudit(event, session, event, loggedInUser, AuditActionType.Resume, "event.audit.resume", virtualizer);
			LOG.info("--------------AFTER AUDIT------------------");

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event'" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			try {
				LOG.info("publishing rft suspended event to epiportal");
				publishEventService.pushRftEvent(event.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), false);
			} catch (Exception e) {
				LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
			}
			try {
				// Send notification to suppliers

				try {
					// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
					jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(event.getId());
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}

			} catch (Exception e) {
				LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
			}
			try {
				// Send Resume notification to suppliers
				LOG.info("Inside with the finish method");
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {

							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
				}
				try {
					simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
				} catch (Exception e) {
					LOG.error("Error while sending RESUME event to MQ : " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
			}

			if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
				tatReportService.updateTatReportEventStatus(event.getEventId(), event.getPreviousRequestId(), loggedInUser.getTenantId(), EventStatus.ACTIVE);
			}
			model.addAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));
		} else {
			for (RftEventSuspensionApproval approval : approvalList) {
				if (approval.getLevel() == currentLevel.getLevel() + 1) {
					LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
					approval.setActive(true);

					for (RftSuspensionApprovalUser nextLevelUser : approval.getApprovalUsers()) {
						sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFT);
					}
					break;
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent doApproval(RfaEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException, ApplicationException {

		byte[] summarySnapshot = null;
		event = rfaEventService.loadRfaEventById(event.getId());
		List<RfaEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
		List<RfaTeamMember> teamMembers = event.getTeamMembers();
		List<User> associateOwns = rfaEventService.getAssociateOwnersOfEventsByEventId(event.getId(), TeamMemberType.Associate_Owner);

		// Identify Current Approval Level
		RfaEventSuspensionApproval currentLevel = null;
		for (RfaEventSuspensionApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfaSuspensionApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfaSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
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
			event.setSuspensionComment(new ArrayList<RfaSuspensionComment>());
		}
		RfaSuspensionComment comment = new RfaSuspensionComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getSuspensionComment().add(comment);

		// If rejected
		if (!approved) {

			// Reset all approvals for re-approval as the Event is rejected.
			for (RfaEventSuspensionApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfaSuspensionApprovalUser user : approval.getApprovalUsers()) {
					if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
						// Send rejection email
						sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
					}

					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfaTeamMember teamMember : teamMembers) {
				// Send rejection email
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info("Error while sending the suspended rejected email to team member : " + e.getMessage());
				}
			}
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Suspension Rejected the RFA : " + event.getEventName());
			event.setStatus(EventStatus.SUSPENDED);
			// rftEventDao.update(event);

			snapShotAuditService.doRfaAudit(event, session, event, actionBy, AuditActionType.SuspendReject, "event.susp.audit.rejected", virtualizer);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_REJECT, "Suspended Event '" + event.getEventId() + "'is Rejected", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
				}

				// Send to Associate Owner
				if (CollectionUtil.isNotEmpty(associateOwns)) {
					for (User assOwner : associateOwns) {
						sentRfxRejectionEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
					}
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
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, true, event.getReferanceNumber());

			// Send to Associate Owner
			if (CollectionUtil.isNotEmpty(associateOwns)) {
				for (User assOwner : associateOwns) {
					sentRfxApprovalEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
				}
			}
			
			for(RfaTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info(" Error while sending email to team member " + e.getMessage());
				}
			}
			
			try {
				JasperPrint eventSummary = rfaEventService.getEvaluationSummaryPdf(event, actionBy, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
				RfaEventAudit audit = new RfaEventAudit(actionBy.getBuyer(), event, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.SuspendApprove, messageSource.getMessage("event.susp.audit.approved", new Object[] { event.getEventName() }, Global.LOCALE), summarySnapshot);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_APPROVE, "Suspended Event '" + event.getEventId() + "' is Approved", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("***********Suspended RFA Approved and Event Audit Saved Successfully ************ ");
			} catch (Exception e) {
				LOG.info("Error saving RFA Event Audit " + e.getMessage());
			}

			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfaSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFA.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFA.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
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
	private void sentRfxApprovalEmail(User mailTo, String eventId, String eventName, User actionBy, String remarks, User ownerUser, RfxTypes type, boolean self, String referenceNumber) {
		LOG.info("Sending approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Suspended Event Approved";
		String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + eventId;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("actionByName", actionBy.getName());
		map.put("eventName", eventName);
		map.put("eventType", type.name());
		map.put("remarks", remarks);
		map.put("referenceNumber", referenceNumber);
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(eventId, type)));
		if (self) {
			map.put("message", "You have Approved the following Suspended Event");
		} else {
			map.put("message", actionBy.getName() + " has Approved the following Suspended Event");
		}
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.SUSPENDED_EVENT_APPROVAL_TEMPLATE);
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
	private void sentRfxRejectionEmail(User mailTo, String eventId, String eventName, User actionBy, String remarks, User ownerUser, RfxTypes type, String referanceNumber) {
		LOG.info("Sending Suspended rejected email notifi to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Suspended Event Rejected";
		String url = APP_URL + "/buyer/" + type.name() + "/viewSummary/" + eventId;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("actionByName", actionBy.getName());
		map.put("eventName", eventName);
		map.put("eventType", type.name());
		map.put("remarks", remarks);
		map.put("referanceNumber", referanceNumber);
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(eventId, type)));
		if (mailTo.getId().equals(actionBy.getId())) {
			map.put("message", "You have Rejected the following Suspended Event");
		} else {
			map.put("message", actionBy.getName() + " has Rejected the following Suspended Event");
		}
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.SUSPEND_EVENT_REJECT_TEMPLATE);
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
	public RfiEvent doApproval(RfiEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException {
		LOG.info("********************************** ");
		byte[] summarySnapshot = null;
		event = rfiEventService.loadRfiEventById(event.getId());
		List<RfiEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
		List<RfiTeamMember> teamMembers = event.getTeamMembers();
		List<User> associateOwns = rfiEventDao.getAssociateOwnersOfEventsByEventId(event.getId(), TeamMemberType.Associate_Owner);

		// Identify Current Approval Level
		RfiEventSuspensionApproval currentLevel = null;
		for (RfiEventSuspensionApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfiSuspensionApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfiSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
				if (user.getUser().getId().equals(actionBy.getId())) {
					actionUser = user;
					LOG.info("Approval being done by : " + actionBy.getLoginId());
				}
			}
		}
		if (actionUser == null) {
			// throw error
			LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFI '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
			throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFI '" + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
		}

		if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
			// throw error
			LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFI at : " + actionUser.getActionDate());
			throw new NotAllowedException("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFI at : " + actionUser.getActionDate());
		}

		// adding remarks into comments
		if (event.getComment() == null) {
			event.setSuspensionComment(new ArrayList<RfiSuspensionComment>());
		}
		RfiSuspensionComment comment = new RfiSuspensionComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getSuspensionComment().add(comment);

		// If rejected
		if (!approved) {
			LOG.info("++++++++++++++++++++++++++++========================= rejection ");

			// Reset all approvals for re-approval as the Event is rejected.
			for (RfiEventSuspensionApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfiSuspensionApprovalUser user : approval.getApprovalUsers()) {
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());

					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfiTeamMember teamMember : teamMembers) {
				// Send rejection email
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info("Error while sending the suspended rejected email to team member : " + e.getMessage());
				}
			}
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Suspension Rejected the RFI : " + event.getEventName());
			event.setStatus(EventStatus.SUSPENDED);
			// rftEventDao.update(event);

			snapShotAuditService.doRfiAudit(event, session, event, actionBy, AuditActionType.SuspendReject, "event.susp.audit.rejected", virtualizer);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_REJECT, "Suspended Event '" + event.getEventId() + "' is Rejected", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());
				}

				// Send to Associate Owner
				if (CollectionUtil.isNotEmpty(associateOwns)) {
					for (User assOwner : associateOwns) {
						sentRfxRejectionEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());
					}
				}
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.info("User " + actionBy.getName() + " has Approved the RFI : " + event.getEventName());
			actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			// Send email notification to Creator
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, true, event.getReferanceNumber());

			// Send to Associate Owner
			if (CollectionUtil.isNotEmpty(associateOwns)) {
				for (User assOwner : associateOwns) {
					sentRfxApprovalEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, false, event.getReferanceNumber());
				}
			}
			
			for(RfiTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFI, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info(" Error while sending email to team member " + e.getMessage());
				}
			}

			try {
				JasperPrint eventSummary = rfiEventService.getEvaluationSummaryPdf(event, actionBy, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
				RfiEventAudit audit = new RfiEventAudit(actionBy.getBuyer(), event, actionBy, new java.util.Date(), AuditActionType.SuspendApprove, messageSource.getMessage("event.susp.audit.approved", new Object[] { event.getEventName() }, Global.LOCALE), summarySnapshot);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_APPROVE, "Suspended Event '" + event.getEventId() + "' is Approved", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("***********Suspended RFI Approved and Event Audit Saved Successfully ************ ");
			} catch (Exception e) {
				LOG.info("Error saving RFI Event Audit " + e.getMessage());
			}

			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfiSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFI.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFI.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rfiEventDao.update(event);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent doApproval(RfpEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException {
		LOG.info("********************************** ");
		byte[] summarySnapshot = null;
		event = rfpEventService.loadRfpEventById(event.getId());
		List<RfpEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
		List<RfpTeamMember> teamMembers = event.getTeamMembers();
		List<User> associateOwns = rfpEventDao.getAssociateOwnersOfEventsByEventId(event.getId(), TeamMemberType.Associate_Owner);

		// Identify Current Approval Level
		RfpEventSuspensionApproval currentLevel = null;
		for (RfpEventSuspensionApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfpSuspensionApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfpSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
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
		RfpSuspensionComment comment = new RfpSuspensionComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getSuspensionComment().add(comment);

		// If rejected
		if (!approved) {
			LOG.info("++++++++++++++++++++++++++++========================= rejection ");

			// Reset all approvals for re-approval as the Event is rejected.
			for (RfpEventSuspensionApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfpSuspensionApprovalUser user : approval.getApprovalUsers()) {
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());

					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfpTeamMember teamMember : teamMembers) {
				// Send rejection email
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info("Error while sending the suspended rejected email to team member : " + e.getMessage());
				}
			}
			
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Suspension Rejected the RFP : " + event.getEventName());
			event.setStatus(EventStatus.SUSPENDED);
			// rftEventDao.update(event);

			snapShotAuditService.doRfpAudit(event, session, event, actionBy, AuditActionType.SuspendReject, "event.susp.audit.rejected", virtualizer);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_REJECT, "Suspended Event '" + event.getEventId() + "' is Rejected", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
				}

				// Send to Associate Owner
				if (CollectionUtil.isNotEmpty(associateOwns)) {
					for (User assOwner : associateOwns) {
						sentRfxRejectionEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
					}
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
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, true, event.getReferanceNumber());

			// Send to Associate Owner
			if (CollectionUtil.isNotEmpty(associateOwns)) {
				for (User assOwner : associateOwns) {
					sentRfxApprovalEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());
				}
			}
			
			for(RfpTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info(" Error while sending email to team member " + e.getMessage());
				}
			}

			try {
				JasperPrint eventSummary = rfpEventService.getEvaluationSummaryPdf(event, actionBy, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
				RfpEventAudit audit = new RfpEventAudit(actionBy.getBuyer(), event, actionBy, new java.util.Date(), AuditActionType.SuspendApprove, messageSource.getMessage("event.susp.audit.approved", new Object[] { event.getEventName() }, Global.LOCALE), summarySnapshot);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_APPROVE, "Suspended Event '" + event.getEventId() + "' is Approved", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("***********Suspended RFP Approved and Event Audit Saved Successfully ************ ");
			} catch (Exception e) {
				LOG.info("Error saving RFP Event Audit " + e.getMessage());
			}

			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfpSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFP.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFP.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rfpEventDao.update(event);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent doApproval(RfqEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException {
		LOG.info("********************************** ");

		byte[] summarySnapshot = null;
		event = rfqEventService.loadRfqEventById(event.getId());
		List<RfqEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
		List<RfqTeamMember> teamMembers = event.getTeamMembers();
		List<User> associateOwns = rfqEventDao.getAssociateOwnersOfEventsByEventId(event.getId(), TeamMemberType.Associate_Owner);

		// Identify Current Approval Level
		RfqEventSuspensionApproval currentLevel = null;
		for (RfqEventSuspensionApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RfqSuspensionApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RfqSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
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
			event.setSuspensionComment(new ArrayList<RfqSuspensionComment>());
		}
		RfqSuspensionComment comment = new RfqSuspensionComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getSuspensionComment().add(comment);

		// If rejected
		if (!approved) {
			LOG.info("++++++++++++++++++++++++++++========================= rejection ");

			// Reset all approvals for re-approval as the Event is rejected.
			for (RfqEventSuspensionApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RfqSuspensionApprovalUser user : approval.getApprovalUsers()) {
					// Sending rejected email notification to Approver
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RfqTeamMember teamMember : teamMembers) {
				// Send rejection email
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info("Error while sending the suspended rejected email to team member : " + e.getMessage());
				}
			}
			
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Suspension Rejected the RFQ : " + event.getEventName());
			event.setStatus(EventStatus.SUSPENDED);
			// rftEventDao.update(event);

			snapShotAuditService.doRfqAudit(event, session, event, actionBy, AuditActionType.SuspendReject, "event.susp.audit.rejected", virtualizer);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_REJECT, "Suspended Event '" + event.getEventId() + "' is Rejected", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					LOG.info("Sending rejected request email to owner : " + event.getCreatedBy().getCommunicationEmail());
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
				}

				// Send to Associate Owner
				if (CollectionUtil.isNotEmpty(associateOwns)) {
					for (User assOwner : associateOwns) {
						sentRfxRejectionEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
					}
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
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, true, event.getReferanceNumber());

			// Send to Associate Owner
			if (CollectionUtil.isNotEmpty(associateOwns)) {
				for (User assOwner : associateOwns) {
					sentRfxApprovalEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());
				}
			}
			
			for(RfqTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info(" Error while sending email to team member " + e.getMessage());
				}
			}

			try {
				JasperPrint eventSummary = rfqEventService.getEvaluationSummaryPdf(event, actionBy, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
				RfqEventAudit audit = new RfqEventAudit(actionBy.getBuyer(), event, actionBy, new java.util.Date(), AuditActionType.SuspendApprove, messageSource.getMessage("event.susp.audit.approved", new Object[] { event.getEventName() }, Global.LOCALE), summarySnapshot);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_APPROVE, "Suspended Event '" + event.getEventId() + "' is Approved", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("*********** Suspended RFQ Approved and Event Audit Saved Successfully ************ ");
			} catch (Exception e) {
				LOG.info("Error saving RFQ Event Audit " + e.getMessage());
			}

			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RfqSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFQ.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFQ.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rfqEventDao.update(event);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent doApproval(RftEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException {
		LOG.info("********************************** ");
		byte[] summarySnapshot = null;
		event = rftEventService.loadRftEventById(event.getId());
		List<RftEventSuspensionApproval> approvalList = event.getSuspensionApprovals();
		List<RftTeamMember> teamMembers = event.getTeamMembers();
		List<User> associateOwns = rftEventDao.getAssociateOwnersOfEventsByEventId(event.getId(), TeamMemberType.Associate_Owner);

		// Identify Current Approval Level
		RftEventSuspensionApproval currentLevel = null;
		for (RftEventSuspensionApproval approval : approvalList) {
			if (approval.isActive()) {
				currentLevel = approval;
				LOG.info("Current Approval Level : " + currentLevel.getLevel());
				break;
			}
		}

		// Identify actionUser in the ApprovalUser of current level
		RftSuspensionApprovalUser actionUser = null;
		if (currentLevel != null) {
			for (RftSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
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
			event.setSuspensionComment(new ArrayList<RftSuspensionComment>());
		}
		RftSuspensionComment comment = new RftSuspensionComment();
		comment.setComment(remarks);
		comment.setApproved(approved);
		comment.setCreatedBy(actionBy);
		comment.setCreatedDate(new Date());
		comment.setRfxEvent(event);
		comment.setApprovalUserId(actionUser.getId());
		event.getSuspensionComment().add(comment);

		// If rejected
		if (!approved) {
			LOG.info("++++++++++++++++++++++++++++========================= rejection ");

			// Reset all approvals for re-approval as the Event is rejected.
			for (RftEventSuspensionApproval approval : approvalList) {
				approval.setDone(false);
				approval.setActive(false);
				for (RftSuspensionApprovalUser user : approval.getApprovalUsers()) {
					sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
					user.setActionDate(null);
					user.setApprovalStatus(ApprovalStatus.PENDING);
					user.setRemarks(null);
					user.setActionDate(null);
				}
			}
			
			for(RftTeamMember teamMember : teamMembers) {
				// Send rejection email
				try {
					sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info("Error while sending the suspended rejected email to team member : " + e.getMessage());
				}
			}
			// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
			actionUser.setActionDate(new Date());
			actionUser.setRemarks(remarks);

			LOG.info("User " + actionBy.getName() + " has Suspension Rejected the RFT : " + event.getEventName());
			event.setStatus(EventStatus.SUSPENDED);
			// rftEventDao.update(event);

			snapShotAuditService.doRftAudit(event, session, event, actionBy, AuditActionType.SuspendReject, "event.susp.audit.rejected", virtualizer);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_REJECT, "Suspended Event '" + event.getEventId() + "' is Rejected", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				if (event.getCreatedBy() != null) {
					sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
				}

				// Send to Associate Owner
				if (CollectionUtil.isNotEmpty(associateOwns)) {
					for (User assOwner : associateOwns) {
						sentRfxRejectionEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
					}
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
			sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());

			// Send email notification to actionBy
			sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, true, event.getReferanceNumber());

			// Send to Associate Owner
			if (CollectionUtil.isNotEmpty(associateOwns)) {
				for (User assOwner : associateOwns) {
					sentRfxApprovalEmail(assOwner, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());
				}
			}
			
			for(RftTeamMember teamMember : teamMembers) {
				try {
					sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());
				} catch (Exception e) {
					LOG.info(" Error while sending email to team member " + e.getMessage());
				}
			}

			try {
				JasperPrint eventSummary = rftEventService.getEvaluationSummaryPdf(event, actionBy, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
				RftEventAudit audit = new RftEventAudit(actionBy.getBuyer(), event, actionBy, new java.util.Date(), AuditActionType.SuspendApprove, messageSource.getMessage("event.susp.audit.approved", new Object[] { event.getEventName() }, Global.LOCALE), summarySnapshot);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND_APPROVE, "Suspended Event '" + event.getEventId() + "' is Approved", actionBy.getTenantId(), actionBy, new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("*********** Suspended RFT Approved and Event Audit Saved Successfully ************ ");
			} catch (Exception e) {
				LOG.info("Error saving RFT Event Audit " + e.getMessage());
			}

			if (ApprovalType.OR == currentLevel.getApprovalType()) {
				LOG.info("This level has OR set for approval. Marking level as done");
				setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
			} else {
				// AND Operation
				LOG.info("This level has AND set for approvals");
				boolean allUsersDone = true;
				if (currentLevel != null) {
					for (RftSuspensionApprovalUser user : currentLevel.getApprovalUsers()) {
						if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
							allUsersDone = false;
							LOG.info("All users of this level have not approved the RFT.");
							break;
						}
					}
				}
				if (allUsersDone) {
					LOG.info("All users of this level have approved the RFT.");
					setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy, virtualizer, model);
				}
			}
		}
		// event.setApprovals(approvalList);
		return rftEventDao.update(event);
	}

}
