/**
 * 
 */
package com.privasia.procurehere.job;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;

/**
 * @author Nitin Otageri
 */
@Component
public class EventStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(EventStatusMonitorJob.class);

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
	RfxViewDao rfxViewDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		return;
		// changeFromApprovedToActive();
		// changeFromActiveToFinish();
	}

	private void changeFromActiveToFinish() {
		EventStatus status = EventStatus.CLOSED;
		// LOG.info("changeFromActiveToFinish");
		try {
			List<RfxView> list = rfxViewDao.getAllActiveEvents();
			Date now = new Date();
			for (RfxView rfxView : list) {
				String eventId = rfxView.getId();
				if (rfxView.getEventEnd() != null && now.after(rfxView.getEventEnd())) {
					LOG.info("Changing Event status to CLOSED for (" + rfxView.getType() + ") - " + rfxView.getEventName());

					switch (rfxView.getType()) {
					case RFA: {
						RfaEvent event = rfaEventDao.findById(eventId);

						// For dutch auction there is a possibility for auction without bq and cq. so further no
						// evaluation required after event end
						// so directly setting status to complete...
						if (!event.getBillOfQuantity() && !event.getQuestionnaires()) {
							status = EventStatus.COMPLETE;
						}
						// event.setStatus(status);
						// if (event.getAuctionComplitationTime() == null) {
						// event.setAuctionComplitationTime(new Date());
						// }
						// event = rfaEventDao.update(event);

						rfaEventService.updateImmediately(eventId, status, event.getAuctionComplitationTime() == null ? new Date() : null);

						RfaEventAudit audit = new RfaEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
						eventAuditService.save(audit);
						
						// try {
						// eventNotificationService.sendRfaEventCloseNotification(rfxView);
						// } catch (Exception e) {
						// LOG.error("Error while Sending notification for event active to close :" + e.getMessage(),
						// e);
						// }
						break;
					}
					case RFI: {
						// RfiEvent event = rfiEventDao.findById(eventId);
						RfiEvent event = new RfiEvent();
						event.setId(rfxView.getId());
						event.setEventName(rfxView.getEventName());
						event.setReferanceNumber(rfxView.getReferanceNumber());
						event.setEventPublishDate(rfxView.getEventPublishDate());
						event.setEventEnd(rfxView.getEventEnd());

						// event.setStatus(status);
						// event = rfiEventDao.update(event);
						rfiEventService.updateImmediately(eventId, status);
						RfiEventAudit audit = new RfiEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
						eventAuditService.save(audit);

						// try {
						// eventNotificationService.sendRfiEventCloseNotification(rfxView);
						// } catch (Exception e) {
						// LOG.error("Error while Sending notification for event active to close :" + e.getMessage(),
						// e);
						// }
						break;
					}
					case RFP: {
						// RfpEvent event = rfpEventDao.findById(eventId);
						RfpEvent event = new RfpEvent();
						event.setId(rfxView.getId());
						event.setEventName(rfxView.getEventName());
						event.setReferanceNumber(rfxView.getReferanceNumber());
						event.setEventPublishDate(rfxView.getEventPublishDate());
						event.setEventEnd(rfxView.getEventEnd());

						// event.setStatus(status);
						// event = rfpEventDao.update(event);
						rfpEventService.updateImmediately(eventId, status);
						RfpEventAudit audit = new RfpEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
						eventAuditService.save(audit);

						// try {
						// eventNotificationService.sendRfpEventCloseNotification(rfxView);
						// } catch (Exception e) {
						// LOG.error("Error while Sending notification for event active to close :" + e.getMessage(),
						// e);
						// }
						break;
					}
					case RFQ: {
						RfqEvent event = new RfqEvent();
						event.setId(rfxView.getId());
						event.setEventName(rfxView.getEventName());
						event.setReferanceNumber(rfxView.getReferanceNumber());
						event.setEventPublishDate(rfxView.getEventPublishDate());
						event.setEventEnd(rfxView.getEventEnd());

						// RfqEvent event = rfqEventDao.findById(eventId);
						// event.setStatus(status);
						// event = rfqEventDao.update(event);
						rfqEventService.updateImmediately(eventId, status);
						RfqEventAudit audit = new RfqEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
						eventAuditService.save(audit);
						// try {
						// eventNotificationService.sendRfqEventCloseNotification(rfxView);
						// } catch (Exception e) {
						// LOG.error("Error while Sending notification for event active to close :" + e.getMessage(),
						// e);
						// }
						break;
					}
					case RFT: {
						RftEvent event = new RftEvent();
						event.setId(rfxView.getId());
						event.setEventName(rfxView.getEventName());
						event.setReferanceNumber(rfxView.getReferanceNumber());
						event.setEventPublishDate(rfxView.getEventPublishDate());
						event.setEventEnd(rfxView.getEventEnd());

						// RftEvent event = rftEventDao.findById(eventId);
						// event.setStatus(status);
						// event = rftEventDao.update(event);
						rftEventService.updateImmediately(eventId, status);

						RftEventAudit audit = new RftEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
						eventAuditService.save(audit);
						// try {
						// eventNotificationService.sendRftEventCloseNotification(rfxView);
						// } catch (Exception e) {
						// LOG.error("Error while Sending notification for event active to close :" + e.getMessage(),
						// e);
						// }
						break;
					}
					default:
						break;
					}

					try {
//						jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.CLOSED");
						jmsTemplate.send("QUEUE.EVENT.CLOSED", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(rfxView.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change of event status from Active to Close : " + e.getMessage(), e);
		}
	}

	private void changeFromApprovedToActive() {

		try {
			// LOG.info("changeFromApprovedToActive");
			List<RfxView> list = rfxViewDao.getAllApprovedEventsforJob();
			if (CollectionUtil.isNotEmpty(list)) {
				Date now = new Date();
				EventStatus status = EventStatus.ACTIVE;
				// String timeZone = "GMT+8:00";
				for (RfxView rfxView : list) {
					String id = rfxView.getId();
					if (rfxView.getEventPublishDate() != null && now.after(rfxView.getEventPublishDate())) {
						LOG.info("Changing Event status to ACTIVE for (" + rfxView.getType() + ") - " + rfxView.getEventName());
//						jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ACTIVE");
						switch (rfxView.getType()) {
						case RFA: {
							rfaEventService.updateImmediately(id, status, null);
							RfaEvent event = new RfaEvent();
							event.setId(id);
							RfaEventAudit audit = new RfaEventAudit(event, null, new Date(), AuditActionType.Active, messageSource.getMessage("event.audit.active", new Object[] { rfxView != null ? rfxView.getEventName() : "" }, Global.LOCALE));
							eventAuditService.save(audit);
							// eventNotificationService.sendActiveEventNotificationToSupplier(timeZone, rfxView, id,
							// rfaEventSupplierService.getAllDetailsForSendInvitation(id));
							break;

						}
						case RFI: {
							rfiEventService.updateImmediately(id, status);
							RfiEvent event = new RfiEvent();
							event.setId(id);
							RfiEventAudit audit = new RfiEventAudit(event, null, new Date(), AuditActionType.Active, messageSource.getMessage("event.audit.active", new Object[] { rfxView != null ? rfxView.getEventName() : "" }, Global.LOCALE));
							eventAuditService.save(audit);
							// eventNotificationService.sendActiveEventNotificationToSupplier(timeZone, rfxView, id,
							// rfiEventSupplierService.getAllDetailsForSendInvitation(id));
							break;
						}
						case RFP: {
							rfpEventService.updateImmediately(id, status);
							RfpEvent event = new RfpEvent();
							event.setId(id);
							RfpEventAudit audit = new RfpEventAudit(event, null, new Date(), AuditActionType.Active, messageSource.getMessage("event.audit.active", new Object[] { rfxView != null ? rfxView.getEventName() : "" }, Global.LOCALE));
							eventAuditService.save(audit);
							// eventNotificationService.sendActiveEventNotificationToSupplier(timeZone, rfxView, id,
							// rfpEventSupplierService.getAllDetailsForSendInvitation(id));

							break;
						}
						case RFQ: {
							rfqEventService.updateImmediately(id, status);
							RfqEvent event = new RfqEvent();
							event.setId(id);
							RfqEventAudit audit = new RfqEventAudit(event, null, new Date(), AuditActionType.Active, messageSource.getMessage("event.audit.active", new Object[] { rfxView != null ? rfxView.getEventName() : "" }, Global.LOCALE));
							eventAuditService.save(audit);
							// eventNotificationService.sendActiveEventNotificationToSupplier(timeZone, rfxView, id,
							// rfqEventSupplierService.getAllDetailsForSendInvitation(id));
							break;
						}
						case RFT: {
							rftEventService.updateImmediately(id, status);
							RftEvent event = new RftEvent();
							event.setId(id);
							RftEventAudit audit = new RftEventAudit(event, null, new Date(), AuditActionType.Active, messageSource.getMessage("event.audit.active", new Object[] { rfxView != null ? rfxView.getEventName() : "" }, Global.LOCALE));
							eventAuditService.save(audit);
							// eventNotificationService.sendActiveEventNotificationToSupplier(timeZone, rfxView, id,
							// rftEventSupplierService.getAllDetailsForSendInvitation(id));
							break;
						}
						default:
							break;

						}
						try {
							jmsTemplate.send("QUEUE.EVENT.ACTIVE",new MessageCreator() {
								@Override
								public Message createMessage(Session session) throws JMSException {
									TextMessage objectMessage = session.createTextMessage();
									objectMessage.setText(id);
									return objectMessage;
								}
							});
						} catch (Exception e) {
							LOG.error("Error sending message to queue : " + e.getMessage(), e);
						}

						// try {
						// if (StringUtils.checkString(rfxView.getEventName()).length() == 0) {
						// rfxView.setEventName("N/A");
						// }
						// String mailTo = rfxView.getCreatedBy().getCommunicationEmail();
						// String subject = "Event Published";
						// String url = APP_URL + "/buyer/" + rfxView.getType().name() + "/eventSummary/" +
						// rfxView.getId();
						// HashMap<String, Object> map = new HashMap<String, Object>();
						// map.put("userName", rfxView.getCreatedBy().getName());
						// map.put("event", rfxView);
						// map.put("eventType", rfxView.getType().name());
						// map.put("businessUnit", StringUtils.checkString(rfxView.getBusinessUnit() != null ?
						// rfxView.getBusinessUnit().getDisplayName() : ""));
						// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
						// try {
						// if (StringUtils.checkString(rfxView.getTenantId()).length() > 0) {
						// String time = buyerSettingsDao.getBuyerTimeZoneByTenantId(rfxView.getTenantId());
						// if (time != null) {
						// timeZone = time;
						// }
						// }
						// } catch (Exception e) {
						// }
						// df.setTimeZone(TimeZone.getTimeZone(timeZone));
						// map.put("date", df.format(new Date()));
						// map.put("appUrl", url);
						// map.put("loginUrl", APP_URL + "/login");
						// sendEmail(mailTo, subject, map, Global.EVENT_ACTIVE_TEMPLATE);
						// String notificationMessage = messageSource.getMessage("event.active.notification.message",
						// new Object[] { rfxView.getReferanceNumber(), rfxView.getEventName() }, Global.LOCALE);
						// sendDashboardNotification(rfxView.getCreatedBy(), url, subject, notificationMessage);
						// } catch (Exception e) {
						// LOG.error("Error while sending notification to event creator during change of event status
						// from Approved to Active " + e.getMessage(), e);
						// }
					}
				}
			}
		} catch (

		Exception e) {
			LOG.error("Error during change of event status from Approved to Active : " + e.getMessage(), e);
		}
	}

	// private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
	// try {
	// notificationService.sendEmail(mailTo, subject, map, template);
	// } catch (Exception e) {
	// LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
	// }
	// }
	//
	// private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
	// NotificationMessage message = new NotificationMessage();
	// message.setCreatedBy(null);
	// message.setCreatedDate(new Date());
	// message.setMessage(notificationMessage);
	// message.setNotificationType(NotificationType.EVENT_MESSAGE);
	// message.setMessageTo(messageTo);
	// message.setSubject(subject);
	// message.setTenantId(messageTo.getTenantId());
	// message.setUrl(url);
	// dashboardNotificationService.save(message);
	// }

	// protected void sendSupplierInvitationEmails(Event event, RfxTypes type) {
	// String timeZone = "GMT+8:00";
	// switch (type) {
	// case RFA: {
	// List<EventSupplier> suppliers = rfaEventSupplierService.getAllSuppliersByEventId(event.getId());
	// if (CollectionUtil.isNotEmpty(suppliers)) {
	// for (EventSupplier supplier : suppliers) {
	// timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
	// if (Boolean.TRUE == supplier.getNotificationSent()) {
	// continue;
	// }
	// sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
	// supplier.setNotificationSent(Boolean.TRUE);
	// rfaEventSupplierService.updateRfaEventSuppliers((RfaEventSupplier) supplier);
	// }
	// }
	// break;
	// }
	// case RFI: {
	// List<EventSupplier> suppliers = rfiEventSupplierService.getAllSuppliersByEventId(event.getId());
	// if (CollectionUtil.isNotEmpty(suppliers)) {
	// for (EventSupplier supplier : suppliers) {
	// timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
	// if (Boolean.TRUE == supplier.getNotificationSent()) {
	// continue;
	// }
	// sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
	// supplier.setNotificationSent(Boolean.TRUE);
	// rfiEventSupplierService.updateEventSuppliers((RfiEventSupplier) supplier);
	// }
	// }
	// break;
	// }
	// case RFP: {
	// List<EventSupplier> suppliers = rfpEventSupplierService.getAllSuppliersByEventId(event.getId());
	// if (CollectionUtil.isNotEmpty(suppliers)) {
	// for (EventSupplier supplier : suppliers) {
	// timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
	// if (Boolean.TRUE == supplier.getNotificationSent()) {
	// continue;
	// }
	// sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
	// supplier.setNotificationSent(Boolean.TRUE);
	// rfpEventSupplierService.updateEventSuppliers((RfpEventSupplier) supplier);
	// }
	// }
	// break;
	// }
	// case RFQ: {
	// List<EventSupplier> suppliers = rfqEventSupplierService.getAllSuppliersByEventId(event.getId());
	// if (CollectionUtil.isNotEmpty(suppliers)) {
	// for (EventSupplier supplier : suppliers) {
	// timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
	// if (Boolean.TRUE == supplier.getNotificationSent()) {
	// continue;
	// }
	// sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
	// supplier.setNotificationSent(Boolean.TRUE);
	// rfqEventSupplierService.updateEventSuppliers((RfqEventSupplier) supplier);
	// }
	// }
	// break;
	// }
	// case RFT: {
	// List<EventSupplier> suppliers = rftEventSupplierService.getAllSuppliersByEventId(event.getId());
	// if (CollectionUtil.isNotEmpty(suppliers)) {
	// for (EventSupplier supplier : suppliers) {
	// timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
	// if (Boolean.TRUE == supplier.getNotificationSent()) {
	// continue;
	// }
	// sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
	// supplier.setNotificationSent(Boolean.TRUE);
	// rftEventSupplierService.updateEventSuppliers((RftEventSupplier) supplier);
	// }
	// }
	// break;
	// }
	// default:
	// break;
	// }
	//
	// }

	/**
	 * @param event
	 * @param type
	 * @param supplier
	 * @param timeZone
	 */
	// private void sendNotificationToSupplier(Event event, RfxTypes type, Supplier supplier, String timeZone) {
	// List<User> supUsers = userService.getAllAdminPlainUsersForSupplierNotification(supplier.getId());
	// if (CollectionUtil.isNotEmpty(supUsers)) {
	// for (User user : supUsers) {
	//
	// String mailTo = "";
	// String subject = "Invitation to participate";
	// String url = APP_URL + "/supplier/viewSupplierEvent/" + type.name() + "/" + event.getId();
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// try {
	// mailTo = user.getCommunicationEmail();
	// map.put("userName", user.getName());
	// map.put("event", event);
	// map.put("eventStatus", "upcoming");
	// map.put("eventType", type.name());
	// map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
	// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
	// df.setTimeZone(TimeZone.getTimeZone(timeZone));
	// map.put("date", df.format(new Date()));
	// map.put("appUrl", url);
	// map.put("loginUrl", APP_URL + "/login");
	// sendEmail(mailTo, subject, map, Global.EVENT_INVITATION_TEMPLATE);
	// } catch (Exception e) {
	// LOG.error("Error While sending mail For Event INVITATION :" + e.getMessage(), e);
	// }
	// try {
	// String notificationMessage = messageSource.getMessage("event.invite.upcoming.notification.message", new Object[]
	// { event.getReferanceNumber() }, Global.LOCALE);
	// sendDashboardNotification(user, url, subject, notificationMessage);
	// } catch (Exception e) {
	// LOG.error("Error While sending dashboard notification For Event INVITATION :" + e.getMessage(), e);
	// }
	//
	// try {
	// String notificationMessage = messageSource.getMessage("event.invite.upcoming.notification.message", new Object[]
	// { event.getReferanceNumber() }, Global.LOCALE);
	// sendPushNotificationToSupplier(user, notificationMessage, event, type);
	// } catch (Exception e) {
	// LOG.error("Error While sending dashboard notification For Event INVITATION :" + e.getMessage(), e);
	// }
	//
	// }
	// }
	// }

	// private void sendPushNotificationToSupplier(User user, String notificationMessage, Event event, RfxTypes
	// eventType) {
	//
	// if (StringUtils.checkString(user.getDeviceId()).length() > 0) {
	// try {
	// LOG.info("User '" + user.getCommunicationEmail() + "' and device Id :" + user.getDeviceId());
	// Map<String, String> payload = new HashMap<String, String>();
	// payload.put("id", event.getId());
	// payload.put("messageType", NotificationType.EVENT_EVALUATION_MESSAGE.toString());
	// payload.put("eventType", eventType.name());
	// notificationService.pushOneSignalNotification(notificationMessage, null, payload,
	// Arrays.asList(user.getDeviceId()));
	// } catch (Exception e) {
	// LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() +
	// "' : " + e.getMessage(), e);
	// }
	// } else {
	// LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
	// }
	// }

}
