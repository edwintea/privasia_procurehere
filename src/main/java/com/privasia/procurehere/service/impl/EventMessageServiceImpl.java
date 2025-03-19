package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

import freemarker.template.Configuration;

/**
 * @author Teja
 */
@Service
@Transactional(readOnly = true)
public class EventMessageServiceImpl implements EventMessageService {

	private static final Logger LOG = LogManager.getLogger(EventMessageServiceImpl.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	RftEventMessageDao rftMessageDao;

	@Autowired
	RfpEventMessageDao rfpMessageDao;

	@Autowired
	RfqEventMessageDao rfqMessageDao;

	@Autowired
	RfiEventMessageDao rfiMessageDao;

	@Autowired
	RfaEventMessageDao rfaMessageDao;

	@Autowired
	PoEventMessageDao poMessageDao;

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
	PoEventDao poEventDao;

	@Autowired
	UserDao userDao;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	PoEventService poEventService;

	@Autowired
	PoService poService;

	@Override
	@Transactional(readOnly = false)
	public RftEventMessage saveEventMessage(RftEventMessage eventMessage) {

		// In case if supplier is sending message to the Buyer, the buyer will not have been set yet.
		if (eventMessage.getBuyer() == null) {
			RftEvent event = rftEventDao.findById(eventMessage.getEvent().getId());
			Buyer buyer = new Buyer();
			buyer.setId(event.getTenantId());
			eventMessage.setBuyer(buyer);
		}

		eventMessage = rftMessageDao.saveOrUpdate(eventMessage);
		if (eventMessage != null && eventMessage.getEvent() != null) {
			eventMessage.getEvent().getEventName();
			eventMessage.getCreatedBy().getTenantId();
		}
		return eventMessage;
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEventMessage saveEventMessage(RfpEventMessage eventMessage) {

		// In case if supplier is sending message to the Buyer, the buyer will not have been set yet.
		if (eventMessage.getBuyer() == null) {
			RfpEvent event = rfpEventDao.findById(eventMessage.getEvent().getId());
			Buyer buyer = new Buyer();
			buyer.setId(event.getTenantId());
			eventMessage.setBuyer(buyer);
		}

		eventMessage = rfpMessageDao.saveOrUpdate(eventMessage);
		if (eventMessage != null && eventMessage.getEvent() != null) {
			eventMessage.getEvent().getEventName();
		}
		return eventMessage;
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEventMessage saveEventMessage(RfiEventMessage eventMessage) {

		// In case if supplier is sending message to the Buyer, the buyer will not have been set yet.
		if (eventMessage.getBuyer() == null) {
			RfiEvent event = rfiEventDao.findById(eventMessage.getEvent().getId());
			Buyer buyer = new Buyer();
			buyer.setId(event.getTenantId());
			eventMessage.setBuyer(buyer);
		}

		eventMessage = rfiMessageDao.saveOrUpdate(eventMessage);
		if (eventMessage != null && eventMessage.getEvent() != null) {
			eventMessage.getEvent().getEventName();
		}
		return eventMessage;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEventMessage saveEventMessage(RfqEventMessage eventMessage) {

		// In case if supplier is sending message to the Buyer, the buyer will not have been set yet.
		if (eventMessage.getBuyer() == null) {
			RfqEvent event = rfqEventDao.findById(eventMessage.getEvent().getId());
			Buyer buyer = new Buyer();
			buyer.setId(event.getTenantId());
			eventMessage.setBuyer(buyer);
		}

		eventMessage = rfqMessageDao.saveOrUpdate(eventMessage);
		if (eventMessage != null && eventMessage.getEvent() != null) {
			eventMessage.getEvent().getEventName();
		}
		return eventMessage;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEventMessage saveEventMessage(RfaEventMessage eventMessage) {

		// In case if supplier is sending message to the Buyer, the buyer will not have been set yet.
		if (eventMessage.getBuyer() == null) {
			RfaEvent event = rfaEventDao.findById(eventMessage.getEvent().getId());
			Buyer buyer = new Buyer();
			buyer.setId(event.getTenantId());
			eventMessage.setBuyer(buyer);
		}

		eventMessage = rfaMessageDao.saveOrUpdate(eventMessage);
		if (eventMessage != null && eventMessage.getEvent() != null) {
			eventMessage.getEvent().getEventName();
		}
		return eventMessage;
	}

	@Override
	@Transactional(readOnly = false)
	public void sendDashboardNotificationForEventMessage(RftEventMessage rftEventMessage) {
		try {
			rftEventMessage = rftMessageDao.findById(rftEventMessage.getId());
			RftEvent event = rftEventDao.findById(rftEventMessage.getEvent().getId());
			String timeZone = "GMT+8:00";
			// If message is from Buyer to Supplier
			if (rftEventMessage.getCreatedBy().getBuyer() != null) {
				String eventUrl = APP_URL + "/supplier/viewSupplierEventMessages/RFT/" + event.getId();
				List<RftEventSupplier> eventSuppliers = event.getSuppliers();
				for (Supplier supplier : rftEventMessage.getSuppliers()) {
					timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
					// Send message to Supplier Admin as well as its team members
					List<User> userList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
					for (User adminUser : userList) {
						NotificationMessage message = new NotificationMessage();
						message.setCreatedBy(rftEventMessage.getCreatedBy());
						message.setCreatedDate(rftEventMessage.getCreatedDate());
						message.setMessage(rftEventMessage.getMessage());
						message.setNotificationType(NotificationType.EVENT_MESSAGE);
						message.setMessageTo(adminUser);
						message.setSubject(rftEventMessage.getSubject());
						message.setTenantId(supplier.getId());
						message.setUrl(eventUrl);
						dashboardNotificationService.save(message);
						sendMessagesMail(adminUser, SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFT, rftEventMessage.getSubject(), rftEventMessage.getMessage());
					}
					// Find matching Event Supplier
					for (RftEventSupplier rftEventSupplier : eventSuppliers) {
						if (supplier.getId().equals(rftEventSupplier.getSupplier().getId())) {
							// Find the Assigned team members and send them dashboard message
							List<RftSupplierTeamMember> members = rftEventSupplier.getTeamMembers();
							for (RftSupplierTeamMember member : members) {
								NotificationMessage message = new NotificationMessage();
								message.setCreatedBy(rftEventMessage.getCreatedBy());
								message.setCreatedDate(rftEventMessage.getCreatedDate());
								message.setMessage(rftEventMessage.getMessage());
								message.setNotificationType(NotificationType.EVENT_MESSAGE);
								message.setMessageTo(member.getUser());
								message.setSubject(rftEventMessage.getSubject());
								message.setTenantId(supplier.getId());
								message.setUrl(eventUrl);
								dashboardNotificationService.save(message);
								sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFT, rftEventMessage.getSubject(), rftEventMessage.getMessage());
							}
						}
					}
				}
			} else {
				// If message is from Supplier to Buyer
				String eventUrl = APP_URL + "/buyer/RFT/viewMailBox/" + event.getId();
				String tenantId = event.getEventOwner().getTenantId();
				NotificationMessage message = new NotificationMessage();
				timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
				// User adminUser = userDao.getAdminUserForBuyer(event.getEventOwner().getBuyer());
				// // Send to Buyer Admin
				// message.setCreatedBy(rftEventMessage.getCreatedBy());
				// message.setCreatedDate(rftEventMessage.getCreatedDate());
				// message.setMessage(rftEventMessage.getMessage());
				// message.setNotificationType(NotificationType.EVENT_MESSAGE);
				// message.setMessageTo(adminUser);
				// message.setSubject(rftEventMessage.getSubject());
				// message.setTenantId(tenantId);
				// message.setUrl(eventUrl);
				// dashboardNotificationService.save(message);

				// Send to Event Owner
				message = new NotificationMessage();
				message.setCreatedBy(rftEventMessage.getCreatedBy());
				message.setCreatedDate(rftEventMessage.getCreatedDate());
				message.setMessage(rftEventMessage.getMessage());
				message.setNotificationType(NotificationType.EVENT_MESSAGE);
				message.setMessageTo(event.getEventOwner());
				message.setSubject(rftEventMessage.getSubject());
				message.setTenantId(tenantId);
				message.setUrl(eventUrl);
				dashboardNotificationService.save(message);
				sendMessagesMail(event.getEventOwner(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFT, rftEventMessage.getSubject(), rftEventMessage.getMessage());

				// Send to Event Team members
				List<RftTeamMember> members = event.getTeamMembers();
				for (RftTeamMember member : members) {
					message = new NotificationMessage();
					message.setCreatedBy(rftEventMessage.getCreatedBy());
					message.setCreatedDate(rftEventMessage.getCreatedDate());
					message.setMessage(rftEventMessage.getMessage());
					message.setNotificationType(NotificationType.EVENT_MESSAGE);
					message.setMessageTo(member.getUser());
					message.setSubject(rftEventMessage.getSubject());
					message.setTenantId(tenantId);
					message.setUrl(eventUrl);
					dashboardNotificationService.save(message);
					sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFT, rftEventMessage.getSubject(), rftEventMessage.getMessage());
				}

			}
		} catch (Exception e) {
			LOG.error("Error saving Dashboard Notification into database : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendNotificationForPoEventMessage(PoEventMessage poEventMessage,Boolean isBuyer) {
		try {
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneBySupplierSettings(poEventMessage.getSuppliers().get(0).getId(), timeZone);

			PoEvent poEvent =poEventDao.findById(poEventMessage.getEvent().getId());
			Po po = poEvent.getPo();
			String poId = po.getId();
			String poNumber = po.getPoNumber();

			String subject = poEventMessage.getSubject();
			String content = poEventMessage.getMessage();

			String fromUserName=SecurityLibrary.getLoggedInUser().getName();
			String fromUserCompanyName = isBuyer?SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName():SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName();

			String toUserName = isBuyer?po.getSupplier().getFullName():po.getBuyer().getFullName();
			String toUserMail = isBuyer?po.getSupplier().getSupplier().getCommunicationEmail():po.getBuyer().getCommunicationEmail();

			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

			String prKey = "";
			if(po.getPr()!=null){
				prKey = po.getPr().getId();
			}
			String url = isBuyer?APP_URL + "/supplier/supplierPrView/"+poId:APP_URL + "/buyer/poView/"+poId+"&prId="+prKey;

			map.put("date", df.format(new Date()));
			map.put("userName", toUserName);
			map.put("fromUserName", fromUserName);
			map.put("fromUserCompanyName", fromUserCompanyName);
			map.put("subject", subject);
			map.put("content", content);
			map.put("poNumber", poNumber);
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			sendEmail(toUserMail, subject, map, Global.PO_EVENT_MAILBOX_TEMPLATE);
			LOG.info(">>>>>>>>>>> BINDING URL WITH : "+url);
			LOG.info(">>>>>>>>>>> SEND EMAIL TO : "+toUserMail);
		} catch (Exception e) {
			LOG.error("Error Notification Mail to Supplier : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendDashboardNotificationForEventMessage(RfpEventMessage eventMessage) {
		try {
			eventMessage = rfpMessageDao.findById(eventMessage.getId());
			RfpEvent event = rfpEventDao.findById(eventMessage.getEvent().getId());
			String timeZone = "GMT+8:00";
			// If message is from Buyer to Supplier
			if (eventMessage.getCreatedBy().getBuyer() != null) {
				String eventUrl = APP_URL + "/supplier/viewSupplierEventMessages/RFP/" + event.getId();
				List<RfpEventSupplier> eventSuppliers = event.getSuppliers();
				for (Supplier supplier : eventMessage.getSuppliers()) {
					timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);

					// Send message to Supplier Admin as well
					List<User> userList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
					for (User adminUser : userList) {

						NotificationMessage message = new NotificationMessage();
						message.setCreatedBy(eventMessage.getCreatedBy());
						message.setCreatedDate(eventMessage.getCreatedDate());
						message.setMessage(eventMessage.getMessage());
						message.setNotificationType(NotificationType.EVENT_MESSAGE);
						message.setMessageTo(adminUser);
						message.setSubject(eventMessage.getSubject());
						message.setTenantId(supplier.getId());
						message.setUrl(eventUrl);
						dashboardNotificationService.save(message);
						sendMessagesMail(adminUser, SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFP, eventMessage.getSubject(), eventMessage.getMessage());
					}
					// Find matching Event Supplier
					for (RfpEventSupplier rftEventSupplier : eventSuppliers) {
						if (supplier.getId().equals(rftEventSupplier.getSupplier().getId())) {
							// Find the Assigned team members and send them dashboard message
							List<RfpSupplierTeamMember> members = rftEventSupplier.getTeamMembers();
							for (RfpSupplierTeamMember member : members) {
								NotificationMessage message = new NotificationMessage();
								message.setCreatedBy(eventMessage.getCreatedBy());
								message.setCreatedDate(eventMessage.getCreatedDate());
								message.setMessage(eventMessage.getMessage());
								message.setNotificationType(NotificationType.EVENT_MESSAGE);
								message.setMessageTo(member.getUser());
								message.setSubject(eventMessage.getSubject());
								message.setTenantId(supplier.getId());
								message.setUrl(eventUrl);
								dashboardNotificationService.save(message);
								sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFP, eventMessage.getSubject(), eventMessage.getMessage());
							}
						}
					}
				}
			} else {
				// If message is from Supplier to Buyer
				String eventUrl = APP_URL + "/buyer/RFP/viewMailBox/" + event.getId();
				String tenantId = event.getEventOwner().getTenantId();
				NotificationMessage message = new NotificationMessage();
				timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
				// Send to Buyer Admin
				// User adminUser = userDao.getAdminUserForBuyer(event.getEventOwner().getBuyer());
				// message.setCreatedBy(eventMessage.getCreatedBy());
				// message.setCreatedDate(eventMessage.getCreatedDate());
				// message.setMessage(eventMessage.getMessage());
				// message.setNotificationType(NotificationType.EVENT_MESSAGE);
				// message.setMessageTo(adminUser);
				// message.setSubject(eventMessage.getSubject());
				// message.setTenantId(tenantId);
				// message.setUrl(eventUrl);
				// dashboardNotificationService.save(message);

				// Send to Event Owner
				message = new NotificationMessage();
				message.setCreatedBy(eventMessage.getCreatedBy());
				message.setCreatedDate(eventMessage.getCreatedDate());
				message.setMessage(eventMessage.getMessage());
				message.setNotificationType(NotificationType.EVENT_MESSAGE);
				message.setMessageTo(event.getEventOwner());
				message.setSubject(eventMessage.getSubject());
				message.setTenantId(tenantId);
				message.setUrl(eventUrl);
				dashboardNotificationService.save(message);
				sendMessagesMail(event.getEventOwner(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFP, eventMessage.getSubject(), eventMessage.getMessage());

				// Send to Event Team members
				List<RfpTeamMember> members = event.getTeamMembers();
				for (RfpTeamMember member : members) {
					message = new NotificationMessage();
					message.setCreatedBy(eventMessage.getCreatedBy());
					message.setCreatedDate(eventMessage.getCreatedDate());
					message.setMessage(eventMessage.getMessage());
					message.setNotificationType(NotificationType.EVENT_MESSAGE);
					message.setMessageTo(member.getUser());
					message.setSubject(eventMessage.getSubject());
					message.setTenantId(tenantId);
					message.setUrl(eventUrl);
					dashboardNotificationService.save(message);
					sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFP, eventMessage.getSubject(), eventMessage.getMessage());
				}

			}
		} catch (Exception e) {
			LOG.error("Error saving RFP Dashboard Notification into database : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendDashboardNotificationForEventMessage(RfqEventMessage eventMessage) {
		try {
			String timeZone = "GMT+8:00";
			eventMessage = rfqMessageDao.findById(eventMessage.getId());
			RfqEvent event = rfqEventDao.findById(eventMessage.getEvent().getId());

			// If message is from Buyer to Supplier
			if (eventMessage.getCreatedBy().getBuyer() != null) {
				String eventUrl = APP_URL + "/supplier/viewSupplierEventMessages/RFQ/" + event.getId();
				List<RfqEventSupplier> eventSuppliers = event.getSuppliers();
				for (Supplier supplier : eventMessage.getSuppliers()) {
					timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);

					// Send message to Supplier Admin as well
					List<User> userList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
					if(CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							NotificationMessage message = new NotificationMessage();
							message.setCreatedBy(eventMessage.getCreatedBy());
							message.setCreatedDate(eventMessage.getCreatedDate());
							message.setMessage(eventMessage.getMessage());
							message.setNotificationType(NotificationType.EVENT_MESSAGE);
							message.setMessageTo(adminUser);
							message.setSubject(eventMessage.getSubject());
							message.setTenantId(supplier.getId());
							message.setUrl(eventUrl);
							dashboardNotificationService.save(message);
							sendMessagesMail(adminUser, SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFQ, eventMessage.getSubject(), eventMessage.getMessage());
						}
					}
					// Find matching Event Supplier
					for (RfqEventSupplier rftEventSupplier : eventSuppliers) {
						if (supplier.getId().equals(rftEventSupplier.getSupplier().getId())) {
							// Find the Assigned team members and send them dashboard message
							List<RfqSupplierTeamMember> members = rftEventSupplier.getTeamMembers();
							for (RfqSupplierTeamMember member : members) {
								NotificationMessage message = new NotificationMessage();
								message.setCreatedBy(eventMessage.getCreatedBy());
								message.setCreatedDate(eventMessage.getCreatedDate());
								message.setMessage(eventMessage.getMessage());
								message.setNotificationType(NotificationType.EVENT_MESSAGE);
								message.setMessageTo(member.getUser());
								message.setSubject(eventMessage.getSubject());
								message.setTenantId(supplier.getId());
								message.setUrl(eventUrl);
								dashboardNotificationService.save(message);
								sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFQ, eventMessage.getSubject(), eventMessage.getMessage());
							}
						}
					}
				}
			} else {
				// If message is from Supplier to Buyer
				String eventUrl = APP_URL + "/buyer/RFQ/viewMailBox/" + event.getId();
				String tenantId = event.getEventOwner().getTenantId();
				NotificationMessage message = new NotificationMessage();
				timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
				// // Send to Buyer Admin
				// User adminUser = userDao.getAdminUserForBuyer(event.getEventOwner().getBuyer());
				// message.setCreatedBy(eventMessage.getCreatedBy());
				// message.setCreatedDate(eventMessage.getCreatedDate());
				// message.setMessage(eventMessage.getMessage());
				// message.setNotificationType(NotificationType.EVENT_MESSAGE);
				// message.setMessageTo(adminUser);
				// message.setSubject(eventMessage.getSubject());
				// message.setTenantId(tenantId);
				// message.setUrl(eventUrl);
				// dashboardNotificationService.save(message);

				// Send to Event Owner
				message = new NotificationMessage();
				message.setCreatedBy(eventMessage.getCreatedBy());
				message.setCreatedDate(eventMessage.getCreatedDate());
				message.setMessage(eventMessage.getMessage());
				message.setNotificationType(NotificationType.EVENT_MESSAGE);
				message.setMessageTo(event.getEventOwner());
				message.setSubject(eventMessage.getSubject());
				message.setTenantId(tenantId);
				message.setUrl(eventUrl);
				dashboardNotificationService.save(message);
				sendMessagesMail(event.getEventOwner(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFQ, eventMessage.getSubject(), eventMessage.getMessage());

				// Send to Event Team members
				List<RfqTeamMember> members = event.getTeamMembers();
				for (RfqTeamMember member : members) {
					message = new NotificationMessage();
					message.setCreatedBy(eventMessage.getCreatedBy());
					message.setCreatedDate(eventMessage.getCreatedDate());
					message.setMessage(eventMessage.getMessage());
					message.setNotificationType(NotificationType.EVENT_MESSAGE);
					message.setMessageTo(member.getUser());
					message.setSubject(eventMessage.getSubject());
					message.setTenantId(tenantId);
					message.setUrl(eventUrl);
					dashboardNotificationService.save(message);
					sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFQ, eventMessage.getSubject(), eventMessage.getMessage());
				}

			}
		} catch (Exception e) {
			LOG.error("Error saving RFP Dashboard Notification into database : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendDashboardNotificationForEventMessage(RfiEventMessage eventMessage) {
		try {
			eventMessage = rfiMessageDao.findById(eventMessage.getId());
			RfiEvent event = rfiEventDao.findById(eventMessage.getEvent().getId());
			String timeZone = "GMT+8:00";
			// If message is from Buyer to Supplier
			if (eventMessage.getCreatedBy().getBuyer() != null) {
				String eventUrl = APP_URL + "/supplier/viewSupplierEventMessages/RFI/" + event.getId();
				List<RfiEventSupplier> eventSuppliers = event.getSuppliers();
				for (Supplier supplier : eventMessage.getSuppliers()) {
					timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);

					// Send message to Supplier Admin as well
					List<User> userList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							NotificationMessage message = new NotificationMessage();
							message.setCreatedBy(eventMessage.getCreatedBy());
							message.setCreatedDate(eventMessage.getCreatedDate());
							message.setMessage(eventMessage.getMessage());
							message.setNotificationType(NotificationType.EVENT_MESSAGE);
							message.setMessageTo(adminUser);
							message.setSubject(eventMessage.getSubject());
							message.setTenantId(supplier.getId());
							message.setUrl(eventUrl);
							dashboardNotificationService.save(message);
							sendMessagesMail(adminUser, SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFI, eventMessage.getSubject(), eventMessage.getMessage());
						}
					}
					// Find matching Event Supplier
					for (RfiEventSupplier rftEventSupplier : eventSuppliers) {
						if (supplier.getId().equals(rftEventSupplier.getSupplier().getId())) {
							// Find the Assigned team members and send them dashboard message
							List<RfiSupplierTeamMember> members = rftEventSupplier.getTeamMembers();
							for (RfiSupplierTeamMember member : members) {
								NotificationMessage message = new NotificationMessage();
								message.setCreatedBy(eventMessage.getCreatedBy());
								message.setCreatedDate(eventMessage.getCreatedDate());
								message.setMessage(eventMessage.getMessage());
								message.setNotificationType(NotificationType.EVENT_MESSAGE);
								message.setMessageTo(member.getUser());
								message.setSubject(eventMessage.getSubject());
								message.setTenantId(supplier.getId());
								message.setUrl(eventUrl);
								dashboardNotificationService.save(message);
								sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFI, eventMessage.getSubject(), eventMessage.getMessage());
							}
						}
					}
				}
			} else {
				// If message is from Supplier to Buyer
				String eventUrl = APP_URL + "/buyer/RFI/viewMailBox/" + event.getId();
				String tenantId = event.getEventOwner().getTenantId();
				NotificationMessage message = new NotificationMessage();
				timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

				// // Send to Buyer Admin
				// User adminUser = userDao.getAdminUserForBuyer(event.getEventOwner().getBuyer());
				// message.setCreatedBy(eventMessage.getCreatedBy());
				// message.setCreatedDate(eventMessage.getCreatedDate());
				// message.setMessage(eventMessage.getMessage());
				// message.setNotificationType(NotificationType.EVENT_MESSAGE);
				// message.setMessageTo(adminUser);
				// message.setSubject(eventMessage.getSubject());
				// message.setTenantId(tenantId);
				// message.setUrl(eventUrl);
				// dashboardNotificationService.save(message);

				// Send to Event Owner
				message = new NotificationMessage();
				message.setCreatedBy(eventMessage.getCreatedBy());
				message.setCreatedDate(eventMessage.getCreatedDate());
				message.setMessage(eventMessage.getMessage());
				message.setNotificationType(NotificationType.EVENT_MESSAGE);
				message.setMessageTo(event.getEventOwner());
				message.setSubject(eventMessage.getSubject());
				message.setTenantId(tenantId);
				message.setUrl(eventUrl);
				dashboardNotificationService.save(message);
				sendMessagesMail(event.getEventOwner(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFI, eventMessage.getSubject(), eventMessage.getMessage());

				// Send to Event Team members
				List<RfiTeamMember> members = event.getTeamMembers();
				for (RfiTeamMember member : members) {
					message = new NotificationMessage();
					message.setCreatedBy(eventMessage.getCreatedBy());
					message.setCreatedDate(eventMessage.getCreatedDate());
					message.setMessage(eventMessage.getMessage());
					message.setNotificationType(NotificationType.EVENT_MESSAGE);
					message.setMessageTo(member.getUser());
					message.setSubject(eventMessage.getSubject());
					message.setTenantId(tenantId);
					message.setUrl(eventUrl);
					dashboardNotificationService.save(message);
					sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFI, eventMessage.getSubject(), eventMessage.getMessage());
				}

			}
		} catch (Exception e) {
			LOG.error("Error saving RFI Dashboard Notification into database : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void sendDashboardNotificationForEventMessage(RfaEventMessage eventMessage) {
		try {
			eventMessage = rfaMessageDao.findById(eventMessage.getId());
			RfaEvent event = rfaEventDao.findById(eventMessage.getEvent().getId());
			String timeZone = "GMT+8:00";
			// If message is from Buyer to Supplier
			if (eventMessage.getCreatedBy().getBuyer() != null) {
				String eventUrl = APP_URL + "/supplier/viewSupplierEventMessages/RFA/" + event.getId();
				List<RfaEventSupplier> eventSuppliers = event.getSuppliers();
				for (Supplier supplier : eventMessage.getSuppliers()) {
					timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);

					// Send message to Supplier Admin as well
					List<User> userList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
					if (CollectionUtil.isNotEmpty(userList)) {
						for (User adminUser : userList) {
							NotificationMessage message = new NotificationMessage();
							message.setCreatedBy(eventMessage.getCreatedBy());
							message.setCreatedDate(eventMessage.getCreatedDate());
							message.setMessage(eventMessage.getMessage());
							message.setNotificationType(NotificationType.EVENT_MESSAGE);
							message.setMessageTo(adminUser);
							message.setSubject(eventMessage.getSubject());
							message.setTenantId(supplier.getId());
							message.setUrl(eventUrl);
							dashboardNotificationService.save(message);
							sendMessagesMail(adminUser, SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFA, eventMessage.getSubject(), eventMessage.getMessage());
						}
					}
					// Find matching Event Supplier
					for (RfaEventSupplier rftEventSupplier : eventSuppliers) {
						if (supplier.getId().equals(rftEventSupplier.getSupplier().getId())) {
							// Find the Assigned team members and send them dashboard message
							List<RfaSupplierTeamMember> members = rftEventSupplier.getTeamMembers();
							for (RfaSupplierTeamMember member : members) {
								NotificationMessage message = new NotificationMessage();
								message.setCreatedBy(eventMessage.getCreatedBy());
								message.setCreatedDate(eventMessage.getCreatedDate());
								message.setMessage(eventMessage.getMessage());
								message.setNotificationType(NotificationType.EVENT_MESSAGE);
								message.setMessageTo(member.getUser());
								message.setSubject(eventMessage.getSubject());
								message.setTenantId(supplier.getId());
								message.setUrl(eventUrl);
								dashboardNotificationService.save(message);
								sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getBuyer().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFA, eventMessage.getSubject(), eventMessage.getMessage());
							}
						}
					}
				}
			} else {
				// If message is from Supplier to Buyer
				String eventUrl = APP_URL + "/buyer/RFA/viewMailBox/" + event.getId();
				String tenantId = event.getEventOwner().getTenantId();
				NotificationMessage message = new NotificationMessage();
				timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

				// Send to Buyer Admin
				// User adminUser = userDao.getAdminUserForBuyer(event.getEventOwner().getBuyer());
				// message.setCreatedBy(eventMessage.getCreatedBy());
				// message.setCreatedDate(eventMessage.getCreatedDate());
				// message.setMessage(eventMessage.getMessage());
				// message.setNotificationType(NotificationType.EVENT_MESSAGE);
				// message.setMessageTo(adminUser);
				// message.setSubject(eventMessage.getSubject());
				// message.setTenantId(tenantId);
				// message.setUrl(eventUrl);
				// dashboardNotificationService.save(message);

				// Send to Event Owner
				message = new NotificationMessage();
				message.setCreatedBy(eventMessage.getCreatedBy());
				message.setCreatedDate(eventMessage.getCreatedDate());
				message.setMessage(eventMessage.getMessage());
				message.setNotificationType(NotificationType.EVENT_MESSAGE);
				message.setMessageTo(event.getEventOwner());
				message.setSubject(eventMessage.getSubject());
				message.setTenantId(tenantId);
				message.setUrl(eventUrl);
				dashboardNotificationService.save(message);
				sendMessagesMail(event.getEventOwner(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFA, eventMessage.getSubject(), eventMessage.getMessage());

				// Send to Event Team members
				List<RfaTeamMember> members = event.getTeamMembers();
				for (RfaTeamMember member : members) {
					message = new NotificationMessage();
					message.setCreatedBy(eventMessage.getCreatedBy());
					message.setCreatedDate(eventMessage.getCreatedDate());
					message.setMessage(eventMessage.getMessage());
					message.setNotificationType(NotificationType.EVENT_MESSAGE);
					message.setMessageTo(member.getUser());
					message.setSubject(eventMessage.getSubject());
					message.setTenantId(tenantId);
					message.setUrl(eventUrl);
					dashboardNotificationService.save(message);
					sendMessagesMail(member.getUser(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), event, eventUrl, timeZone, RfxTypes.RFA, eventMessage.getSubject(), eventMessage.getMessage());
				}
			}
		} catch (Exception e) {
			LOG.error("Error saving RFA Dashboard Notification into database : " + e.getMessage(), e);
		}
	}



	@Override
	public List<RftEventMessage> getRftEventMessages(String eventId, int page, int size, String search) {
		List<RftEventMessage> list = rftMessageDao.getEventMessages(eventId, page, size, search);
		// Deep touch it for JSON/AJAX rendering purpose

		// TODO: change to recursive
		touchRftMessageCollection(list);
		return list;
	}

	@Override
	public List<RfqEventMessage> getRfqEventMessages(String eventId, int page, int size, String search) {
		List<RfqEventMessage> list = rfqMessageDao.getEventMessages(eventId, page, size, search);
		// Deep touch it for JSON/AJAX rendering purpose

		// TODO: change to recursive
		touchRfqMessageCollection(list);
		return list;
	}

	@Override
	public List<RfpEventMessage> getRfpEventMessages(String eventId, int page, int size, String search) {
		List<RfpEventMessage> list = rfpMessageDao.getEventMessages(eventId, page, size, search);
		// Deep touch it for JSON/AJAX rendering purpose

		// TODO: change to recursive
		touchRfpMessageCollection(list);
		return list;
	}

	@Override
	public List<RfiEventMessage> getRfiEventMessages(String eventId, int page, int size, String search) {
		List<RfiEventMessage> list = rfiMessageDao.getEventMessages(eventId, page, size, search);
		// Deep touch it for JSON/AJAX rendering purpose

		// TODO: change to recursive
		touchRfiMessageCollection(list);
		return list;
	}

	@Override
	public List<RfaEventMessage> getRfaEventMessages(String eventId, int page, int size, String search) {
		List<RfaEventMessage> list = rfaMessageDao.getEventMessages(eventId, page, size, search);
		// Deep touch it for JSON/AJAX rendering purpose

		// TODO: change to recursive
		touchRfaMessageCollection(list);
		return list;
	}

	private void touchRftMessageCollection(List<RftEventMessage> list) {
		for (RftEventMessage rftEventMessage : list) {
			rftEventMessage.getMessage();
			if (rftEventMessage.getBuyer() != null) {
				rftEventMessage.getBuyer().getCompanyName();
			}
			if (CollectionUtil.isNotEmpty(rftEventMessage.getSuppliers())) {
				for (Supplier sup : rftEventMessage.getSuppliers()) {
					sup.getCommunicationEmail();
					sup.setCreatedBy(null);
					sup.setActiveInactive(null);
					sup.setCity(null);
					sup.setCompanyContactNumber(null);
					sup.setCountries(null);
					sup.setTermsOfUseAccepted(null);
					sup.setTermsOfUseAcceptedDate(null);
					sup.setDeclaration(null);
					sup.setLine1(null);
					sup.setLine2(null);
					sup.setRemarks(null);
					sup.setSupplierCompanyProfile(null);
					sup.setSupplierTrackDesc(null);
					sup.setActionDate(null);
				}
			}
			if (CollectionUtil.isNotEmpty(rftEventMessage.getReplies())) {
				for (RftEventMessage reply : rftEventMessage.getReplies()) {
					reply.getMessage();
					reply.getCreatedBy().getLoginId();
					if (reply.getBuyer() != null) {
						reply.getBuyer().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(reply.getSuppliers())) {
						for (Supplier sup : reply.getSuppliers()) {
							sup.getCommunicationEmail();
							sup.setCreatedBy(null);
							sup.setActiveInactive(null);
							sup.setCity(null);
							sup.setCompanyContactNumber(null);
							sup.setCountries(null);
							sup.setTermsOfUseAccepted(null);
							sup.setTermsOfUseAcceptedDate(null);
							sup.setDeclaration(null);
							sup.setLine1(null);
							sup.setLine2(null);
							sup.setRemarks(null);
							sup.setSupplierCompanyProfile(null);
							sup.setSupplierTrackDesc(null);
							sup.setActionDate(null);
						}
					}
					reply.setReplies(null);
				}
			}
		}
	}

	private void touchRfpMessageCollection(List<RfpEventMessage> list) {
		for (RfpEventMessage eventMessage : list) {
			eventMessage.getMessage();
			if (eventMessage.getBuyer() != null) {
				eventMessage.getBuyer().getCompanyName();
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getSuppliers())) {
				for (Supplier sup : eventMessage.getSuppliers()) {
					sup.getCommunicationEmail();
					sup.setCreatedBy(null);
					sup.setActiveInactive(null);
					sup.setCity(null);
					sup.setCompanyContactNumber(null);
					sup.setCountries(null);
					sup.setTermsOfUseAccepted(null);
					sup.setTermsOfUseAcceptedDate(null);
					sup.setDeclaration(null);
					sup.setLine1(null);
					sup.setLine2(null);
					sup.setRemarks(null);
					sup.setSupplierCompanyProfile(null);
					sup.setSupplierTrackDesc(null);
					sup.setActionDate(null);
				}
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getReplies())) {
				for (RfpEventMessage reply : eventMessage.getReplies()) {
					reply.getMessage();
					reply.getCreatedBy().getLoginId();
					if (reply.getBuyer() != null) {
						reply.getBuyer().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(reply.getSuppliers())) {
						for (Supplier sup : reply.getSuppliers()) {
							sup.getCommunicationEmail();
							sup.setCreatedBy(null);
							sup.setActiveInactive(null);
							sup.setCity(null);
							sup.setCompanyContactNumber(null);
							sup.setCountries(null);
							sup.setTermsOfUseAccepted(null);
							sup.setTermsOfUseAcceptedDate(null);
							sup.setDeclaration(null);
							sup.setLine1(null);
							sup.setLine2(null);
							sup.setRemarks(null);
							sup.setSupplierCompanyProfile(null);
							sup.setSupplierTrackDesc(null);
							sup.setActionDate(null);
						}
					}
					reply.setReplies(null);
				}
			}
		}
	}

	private void touchRfqMessageCollection(List<RfqEventMessage> list) {
		for (RfqEventMessage eventMessage : list) {
			eventMessage.getMessage();
			if (eventMessage.getBuyer() != null) {
				eventMessage.getBuyer().getCompanyName();
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getSuppliers())) {
				for (Supplier sup : eventMessage.getSuppliers()) {
					sup.getCommunicationEmail();
					sup.setCreatedBy(null);
					sup.setActiveInactive(null);
					sup.setCity(null);
					sup.setCompanyContactNumber(null);
					sup.setCountries(null);
					sup.setTermsOfUseAccepted(null);
					sup.setTermsOfUseAcceptedDate(null);
					sup.setDeclaration(null);
					sup.setLine1(null);
					sup.setLine2(null);
					sup.setRemarks(null);
					sup.setSupplierCompanyProfile(null);
					sup.setSupplierTrackDesc(null);
					sup.setActionDate(null);
				}
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getReplies())) {
				for (RfqEventMessage reply : eventMessage.getReplies()) {
					reply.getMessage();
					reply.getCreatedBy().getLoginId();
					if (reply.getBuyer() != null) {
						reply.getBuyer().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(reply.getSuppliers())) {
						for (Supplier sup : reply.getSuppliers()) {
							sup.getCommunicationEmail();
							sup.setCreatedBy(null);
							sup.setActiveInactive(null);
							sup.setCity(null);
							sup.setCompanyContactNumber(null);
							sup.setCountries(null);
							sup.setTermsOfUseAccepted(null);
							sup.setTermsOfUseAcceptedDate(null);
							sup.setDeclaration(null);
							sup.setLine1(null);
							sup.setLine2(null);
							sup.setRemarks(null);
							sup.setSupplierCompanyProfile(null);
							sup.setSupplierTrackDesc(null);
							sup.setActionDate(null);
						}
					}
					reply.setReplies(null);
				}
			}
		}
	}

	private void touchRfiMessageCollection(List<RfiEventMessage> list) {
		for (RfiEventMessage eventMessage : list) {
			eventMessage.getMessage();
			if (eventMessage.getBuyer() != null) {
				eventMessage.getBuyer().getCompanyName();
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getSuppliers())) {
				for (Supplier sup : eventMessage.getSuppliers()) {
					sup.getCommunicationEmail();
					sup.setCreatedBy(null);
					sup.setActiveInactive(null);
					sup.setCity(null);
					sup.setCompanyContactNumber(null);
					sup.setCountries(null);
					sup.setTermsOfUseAccepted(null);
					sup.setTermsOfUseAcceptedDate(null);
					sup.setDeclaration(null);
					sup.setLine1(null);
					sup.setLine2(null);
					sup.setRemarks(null);
					sup.setSupplierCompanyProfile(null);
					sup.setSupplierTrackDesc(null);
					sup.setActionDate(null);
				}
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getReplies())) {
				for (RfiEventMessage reply : eventMessage.getReplies()) {
					reply.getMessage();
					reply.getCreatedBy().getLoginId();
					if (reply.getBuyer() != null) {
						reply.getBuyer().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(reply.getSuppliers())) {
						for (Supplier sup : reply.getSuppliers()) {
							sup.getCommunicationEmail();
							sup.setCreatedBy(null);
							sup.setActiveInactive(null);
							sup.setCity(null);
							sup.setCompanyContactNumber(null);
							sup.setCountries(null);
							sup.setTermsOfUseAccepted(null);
							sup.setTermsOfUseAcceptedDate(null);
							sup.setDeclaration(null);
							sup.setLine1(null);
							sup.setLine2(null);
							sup.setRemarks(null);
							sup.setSupplierCompanyProfile(null);
							sup.setSupplierTrackDesc(null);
							sup.setActionDate(null);
						}
					}
					reply.setReplies(null);
				}
			}
		}
	}

	private void touchRfaMessageCollection(List<RfaEventMessage> list) {
		for (RfaEventMessage eventMessage : list) {
			eventMessage.getMessage();
			if (eventMessage.getBuyer() != null) {
				eventMessage.getBuyer().getCompanyName();
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getSuppliers())) {
				for (Supplier sup : eventMessage.getSuppliers()) {
					sup.getCommunicationEmail();
					sup.setCreatedBy(null);
					sup.setActiveInactive(null);
					sup.setCity(null);
					sup.setCompanyContactNumber(null);
					sup.setCountries(null);
					sup.setTermsOfUseAccepted(null);
					sup.setTermsOfUseAcceptedDate(null);
					sup.setDeclaration(null);
					sup.setLine1(null);
					sup.setLine2(null);
					sup.setRemarks(null);
					sup.setSupplierCompanyProfile(null);
					sup.setSupplierTrackDesc(null);
					sup.setActionDate(null);
				}
			}
			if (CollectionUtil.isNotEmpty(eventMessage.getReplies())) {
				for (RfaEventMessage reply : eventMessage.getReplies()) {
					reply.getMessage();
					reply.getCreatedBy().getLoginId();
					if (reply.getBuyer() != null) {
						reply.getBuyer().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(reply.getSuppliers())) {
						for (Supplier sup : reply.getSuppliers()) {
							sup.getCommunicationEmail();
							sup.setCreatedBy(null);
							sup.setActiveInactive(null);
							sup.setCity(null);
							sup.setCompanyContactNumber(null);
							sup.setCountries(null);
							sup.setTermsOfUseAccepted(null);
							sup.setTermsOfUseAcceptedDate(null);
							sup.setDeclaration(null);
							sup.setLine1(null);
							sup.setLine2(null);
							sup.setRemarks(null);
							sup.setSupplierCompanyProfile(null);
							sup.setSupplierTrackDesc(null);
							sup.setActionDate(null);
						}
					}
					reply.setReplies(null);
				}
			}
		}
	}

	@Override
	public long getTotalFilteredRftEventMessageCount(String eventId, String search) {
		return rftMessageDao.getTotalFilteredEventMessageCount(eventId, search);
	}

	@Override
	public long getTotalRftEventMessageCount(String eventId) {
		return rftMessageDao.getTotalEventMessageCount(eventId);
	}

	@Override
	public List<RftEventMessage> getRftEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search) {
		List<RftEventMessage> list = rftMessageDao.getEventMessagesForSupplier(eventId, supplierId, page, size, search);
		touchRftMessageCollection(list);
		return list;
	}

	@Override
	public long getTotalFilteredRftEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		return rftMessageDao.getTotalFilteredEventMessageCountForSupplier(eventId, supplierId, search);
	}

	@Override
	public long getTotalRftEventMessageCountForSupplier(String eventId, String supplierId) {
		return rftMessageDao.getTotalEventMessageCountForSupplier(eventId, supplierId);
	}

	@Override
	public long getTotalFilteredRfpEventMessageCount(String eventId, String search) {
		return rfpMessageDao.getTotalFilteredEventMessageCount(eventId, search);
	}

	@Override
	public long getTotalRfpEventMessageCount(String eventId) {
		return rfpMessageDao.getTotalEventMessageCount(eventId);
	}

	@Override
	public List<RfpEventMessage> getRfpEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search) {
		List<RfpEventMessage> list = rfpMessageDao.getEventMessagesForSupplier(eventId, supplierId, page, size, search);
		touchRfpMessageCollection(list);
		return list;
	}

	@Override
	public long getTotalFilteredRfpEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		return rfpMessageDao.getTotalFilteredEventMessageCountForSupplier(eventId, supplierId, search);
	}

	@Override
	public long getTotalRfpEventMessageCountForSupplier(String eventId, String supplierId) {
		return rfpMessageDao.getTotalEventMessageCountForSupplier(eventId, supplierId);
	}

	@Override
	public long getTotalFilteredRfqEventMessageCount(String eventId, String search) {
		return rfqMessageDao.getTotalFilteredEventMessageCount(eventId, search);
	}

	@Override
	public long getTotalRfqEventMessageCount(String eventId) {
		return rfqMessageDao.getTotalEventMessageCount(eventId);
	}

	@Override
	public List<RfqEventMessage> getRfqEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search) {
		List<RfqEventMessage> list = rfqMessageDao.getEventMessagesForSupplier(eventId, supplierId, page, size, search);
		touchRfqMessageCollection(list);
		return list;
	}

	@Override
	public long getTotalFilteredRfqEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		return rfqMessageDao.getTotalFilteredEventMessageCountForSupplier(eventId, supplierId, search);
	}

	@Override
	public long getTotalRfqEventMessageCountForSupplier(String eventId, String supplierId) {
		return rfqMessageDao.getTotalEventMessageCountForSupplier(eventId, supplierId);
	}

	@Override
	public long getTotalFilteredRfiEventMessageCount(String eventId, String search) {
		return rfiMessageDao.getTotalFilteredEventMessageCount(eventId, search);
	}

	@Override
	public long getTotalRfiEventMessageCount(String eventId) {
		return rfiMessageDao.getTotalEventMessageCount(eventId);
	}

	@Override
	public List<RfiEventMessage> getRfiEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search) {
		List<RfiEventMessage> list = rfiMessageDao.getEventMessagesForSupplier(eventId, supplierId, page, size, search);
		touchRfiMessageCollection(list);
		return list;
	}

	@Override
	public List<RfiEventMessage> getRfiEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		List<RfiEventMessage> list = rfiMessageDao.getEventMessagesForSupplierByEventId(eventId, supplierId, page, size, search);
		touchRfiEventMessageForSupplier(eventId, list);
		return list;
	}

	@Override
	public List<RfpEventMessage> getRfpEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		List<RfpEventMessage> list = rfpMessageDao.getEventMessagesForSupplierByEventId(eventId, supplierId, page, size, search);
		touchRfpEventMessageForSupplier(eventId, list);
		return list;
	}

	@Override
	public List<RfqEventMessage> getRfqEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		List<RfqEventMessage> list = rfqMessageDao.getEventMessagesForSupplierByEventId(eventId, supplierId, page, size, search);
		touchRfqEventMessageForSupplier(eventId, list);
		return list;
	}

	@Override
	public List<RfaEventMessage> getRfaEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		List<RfaEventMessage> list = rfaMessageDao.getEventMessagesForSupplierByEventId(eventId, supplierId, page, size, search);
		touchRfaEventMessageForSupplier(eventId, list);
		return list;
	}

	@Override
	public List<RftEventMessage> getRftEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		List<RftEventMessage> list = rftMessageDao.getEventMessagesForSupplierByEventId(eventId, supplierId, page, size, search);
		touchRftEventMessageForSupplier(eventId, list);
		return list;
	}

	@Override
	public List<PoEventMessage> getPoEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		List<PoEventMessage> list = poMessageDao.getEventMessagesForSupplierByEventId(eventId, supplierId, page, size, search);
		touchPoEventMessageForSupplier(eventId, list);
		return list;
	}

	@Override
	public long getTotalPoEventMessageCountForSupplier(String eventId, String supplierId) {
		return poMessageDao.getTotalEventMessageCountForSupplier(eventId, supplierId);
	}

	@Override
	public long getTotalFilteredPoEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		return poMessageDao.getTotalFilteredEventMessageCountForSupplier(eventId, supplierId, search);
	}

	@Override
	public long getTotalFilteredRfiEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		return rfiMessageDao.getTotalFilteredEventMessageCountForSupplier(eventId, supplierId, search);
	}

	@Override
	public long getTotalRfiEventMessageCountForSupplier(String eventId, String supplierId) {
		return rfiMessageDao.getTotalEventMessageCountForSupplier(eventId, supplierId);
	}

	@Override
	public long getTotalFilteredRfaEventMessageCount(String eventId, String search) {
		return rfaMessageDao.getTotalFilteredEventMessageCount(eventId, search);
	}

	@Override
	public long getTotalRfaEventMessageCount(String eventId) {
		return rfaMessageDao.getTotalEventMessageCount(eventId);
	}

	@Override
	public List<RfaEventMessage> getRfaEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search) {
		List<RfaEventMessage> list = rfaMessageDao.getEventMessagesForSupplier(eventId, supplierId, page, size, search);
		touchRfaMessageCollection(list);
		return list;
	}

	@Override
	public long getTotalFilteredRfaEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		return rfaMessageDao.getTotalFilteredEventMessageCountForSupplier(eventId, supplierId, search);
	}

	@Override
	public long getTotalRfaEventMessageCountForSupplier(String eventId, String supplierId) {
		return rfaMessageDao.getTotalEventMessageCountForSupplier(eventId, supplierId);
	}

	@Override
	public RftEventMessage getRftEventMessageById(String messageId) {
		return rftMessageDao.findById(messageId);
	}

	@Override
	public RfqEventMessage getRfqEventMessageById(String messageId) {
		return rfqMessageDao.findById(messageId);
	}

	@Override
	public RfpEventMessage getRfpEventMessageById(String messageId) {
		return rfpMessageDao.findById(messageId);
	}

	@Override
	public RfiEventMessage getRfiEventMessageById(String messageId) {
		return rfiMessageDao.findById(messageId);
	}

	@Override
	public RfaEventMessage getRfaEventMessageById(String messageId) {
		return rfaMessageDao.findById(messageId);
	}

	private void sendMessagesMail(User userTo, String fromUserName, String fromUserCompanyName, Event event, String url, String timeZone, RfxTypes eventType, String subject, String content) {
		String mailTo = userTo.getCommunicationEmail();
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", userTo.getName());
		map.put("fromUserName", fromUserName);
		map.put("fromUserCompanyName", fromUserCompanyName);
		map.put("eventType", eventType.getValue());
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("content", content);
		map.put("subject", subject);
		map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), eventType)));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(userTo.getEmailNotifications()) {
			sendEmail(mailTo, subject, map, Global.EVENT_MAILBOX_TEMPLATE);
		}
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		notificationService.sendEmail(mailTo, subject, map, template);
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

	@Override
	public List<RfiEventMessage> getRfiEventMessagesByEventId(String eventId, int page, int size, String search) {
		List<RfiEventMessage> list = rfiMessageDao.getRfiEventMessagesByEventId(eventId, page, size, search);
		touchRfiEventMessage(eventId, list);
		return list;
	}

	private void touchRfiEventMessageForSupplier(String eventId, List<RfiEventMessage> list) {
		List<RfiEventMessage> replies = rfiMessageDao.getRfiEventMessagesRepliesByEventId(eventId);
		List<RfiEventMessage> eventMessageReplies = new ArrayList<RfiEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfiEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfiEventMessage rfiEventMessage : list) {
			// Supplier need not to know that other which suppliers got the message
			/*
			 * List<Supplier> messagesupp = rfiMessageDao.getRfiEventMessagesSupplierById(rfiEventMessage.getId()); if
			 * (CollectionUtil.isNotEmpty(messagesupp)) { rfiEventMessage.setSuppliers(messagesupp); }
			 */
			rfiEventMessage.setBuyer(rfiEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfiEventMessage> replyList = new ArrayList<>();
				for (RfiEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfiEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfiEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfiEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	private void touchRfiEventMessage(String eventId, List<RfiEventMessage> list) {
		List<RfiEventMessage> replies = rfiMessageDao.getRfiEventMessagesRepliesByEventId(eventId);
		List<RfiEventMessage> eventMessageReplies = new ArrayList<RfiEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfiEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfiEventMessage rfiEventMessage : list) {
			List<Supplier> messagesupp = rfiMessageDao.getRfiEventMessagesSupplierById(rfiEventMessage.getId());
			if (CollectionUtil.isNotEmpty(messagesupp)) {
				rfiEventMessage.setSuppliers(messagesupp);
			}
			rfiEventMessage.setBuyer(rfiEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfiEventMessage> replyList = new ArrayList<>();
				for (RfiEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfiEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfiEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfiEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	@Override
	public List<RftEventMessage> getRftEventMessagesByEventId(String eventId, int page, int size, String search) {
		List<RftEventMessage> list = rftMessageDao.getRftEventMessagesByEventId(eventId, page, size, search);
		touchRftEventMessage(eventId, list);
		return list;
	}

	private void touchRftEventMessageForSupplier(String eventId, List<RftEventMessage> list) {
		List<RftEventMessage> replies = rftMessageDao.getRftEventMessagesRepliesByEventId(eventId);

		List<RftEventMessage> eventMessageReplies = new ArrayList<RftEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RftEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RftEventMessage rftEventMessage : list) {
			// Supplier need not to know that other which suppliers got the message
			List<Supplier> messagesupp = rftMessageDao.getRftEventMessagesSupplierById(rftEventMessage.getId());
			rftEventMessage.setBuyer(rftEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RftEventMessage> replyList = new ArrayList<>();
				for (RftEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rftEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rftEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rftEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	private void touchRftEventMessage(String eventId, List<RftEventMessage> list) {
		List<RftEventMessage> replies = rftMessageDao.getRftEventMessagesRepliesByEventId(eventId);

		List<RftEventMessage> eventMessageReplies = new ArrayList<RftEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RftEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RftEventMessage rftEventMessage : list) {
			List<Supplier> messagesupp = rftMessageDao.getRftEventMessagesSupplierById(rftEventMessage.getId());
			if (CollectionUtil.isNotEmpty(messagesupp)) {
				rftEventMessage.setSuppliers(messagesupp);
			}
			rftEventMessage.setBuyer(rftEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RftEventMessage> replyList = new ArrayList<>();
				for (RftEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rftEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rftEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rftEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	@Override
	public List<RfqEventMessage> getRfqEventMessagesByEventId(String eventId, int page, int size, String search) {
		List<RfqEventMessage> list = rfqMessageDao.getRfqEventMessagesByEventId(eventId, page, size, search);
		touchRfqEventMessage(eventId, list);
		return list;
	}

	private void touchRfqEventMessageForSupplier(String eventId, List<RfqEventMessage> list) {
		List<RfqEventMessage> replies = rfqMessageDao.getRfqEventMessagesRepliesByEventId(eventId);
		List<RfqEventMessage> eventMessageReplies = new ArrayList<RfqEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfqEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfqEventMessage rfqEventMessage : list) {
			// Supplier need not to know that other which suppliers got the message
			/*
			 * List<Supplier> messageSupp = rfqMessageDao.getRfqEventMessagesSupplierById(rfqEventMessage.getId()); if
			 * (CollectionUtil.isNotEmpty(messageSupp)) { rfqEventMessage.setSuppliers(messageSupp); }
			 */
			rfqEventMessage.setBuyer(rfqEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfqEventMessage> replyList = new ArrayList<>();
				for (RfqEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfqEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfqEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfqEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	private void touchRfqEventMessage(String eventId, List<RfqEventMessage> list) {
		List<RfqEventMessage> replies = rfqMessageDao.getRfqEventMessagesRepliesByEventId(eventId);
		List<RfqEventMessage> eventMessageReplies = new ArrayList<RfqEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfqEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfqEventMessage rfqEventMessage : list) {
			List<Supplier> messageSupp = rfqMessageDao.getRfqEventMessagesSupplierById(rfqEventMessage.getId());
			if (CollectionUtil.isNotEmpty(messageSupp)) {
				rfqEventMessage.setSuppliers(messageSupp);
			}
			rfqEventMessage.setBuyer(rfqEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfqEventMessage> replyList = new ArrayList<>();
				for (RfqEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfqEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfqEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfqEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	private void touchPoEventMessageForSupplier(String eventId, List<PoEventMessage> list) {
		List<PoEventMessage> replies = poMessageDao.getPoEventMessagesRepliesByEventId(eventId);
		List<PoEventMessage> eventMessageReplies = new ArrayList<PoEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (PoEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (PoEventMessage poEventMessage : list) {
			// Supplier need not to know that other which suppliers got the message
			/*
			 * List<Supplier> messageSupp = rfqMessageDao.getRfqEventMessagesSupplierById(rfqEventMessage.getId()); if
			 * (CollectionUtil.isNotEmpty(messageSupp)) { rfqEventMessage.setSuppliers(messageSupp); }
			 */
			poEventMessage.setBuyer(poEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<PoEventMessage> replyList = new ArrayList<>();
				for (PoEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && poEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						poEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				poEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	@Override
	public List<RfaEventMessage> getRfaEventMessagesByEventId(String eventId, int page, int size, String search) {
		List<RfaEventMessage> list = rfaMessageDao.getRfaEventMessagesByEventId(eventId, page, size, search);
		touchRfaEventMessage(eventId, list);
		return list;
	}

	private void touchRfaEventMessageForSupplier(String eventId, List<RfaEventMessage> list) {
		List<RfaEventMessage> replies = rfaMessageDao.getRfaEventMessagesRepliesByEventId(eventId);
		List<RfaEventMessage> eventMessageReplies = new ArrayList<RfaEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfaEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfaEventMessage rfaEventMessage : list) {

			// Supplier need not to know that other which suppliers got the message

			/*
			 * List<Supplier> messageSupp = rfaMessageDao.getRfaEventMessagesSupplierById(rfaEventMessage.getId()); if
			 * (CollectionUtil.isNotEmpty(messageSupp)) { LOG.info("messageSupp............"+messageSupp.toString());
			 * rfaEventMessage.setSuppliers(messageSupp); }
			 */
			rfaEventMessage.setBuyer(rfaEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfaEventMessage> replyList = new ArrayList<>();
				for (RfaEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfaEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfaEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfaEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	private void touchRfaEventMessage(String eventId, List<RfaEventMessage> list) {
		List<RfaEventMessage> replies = rfaMessageDao.getRfaEventMessagesRepliesByEventId(eventId);
		List<RfaEventMessage> eventMessageReplies = new ArrayList<RfaEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfaEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfaEventMessage rfaEventMessage : list) {
			List<Supplier> messageSupp = rfaMessageDao.getRfaEventMessagesSupplierById(rfaEventMessage.getId());
			if (CollectionUtil.isNotEmpty(messageSupp)) {
				rfaEventMessage.setSuppliers(messageSupp);
			}
			rfaEventMessage.setBuyer(rfaEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfaEventMessage> replyList = new ArrayList<>();
				for (RfaEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfaEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfaEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfaEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	@Override
	public List<RfpEventMessage> getRfpEventMessagesByEventId(String eventId, int page, int size, String search) {
		List<RfpEventMessage> list = rfpMessageDao.getRfpEventMessagesByEventId(eventId, page, size, search);
		touchRfpEventMessage(eventId, list);
		return list;
	}

	private void touchRfpEventMessageForSupplier(String eventId, List<RfpEventMessage> list) {
		List<RfpEventMessage> replies = rfpMessageDao.getRfpEventMessagesRepliesByEventId(eventId);

		List<RfpEventMessage> eventMessageReplies = new ArrayList<RfpEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfpEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfpEventMessage rfpEventMessage : list) {
			// Supplier need not to know that other which suppliers got the message
			/*
			 * List<Supplier> messageSupp = rfpMessageDao.getRfpEventMessagesSupplierById(rfpEventMessage.getId()); if
			 * (CollectionUtil.isNotEmpty(messageSupp)) { rfpEventMessage.setSuppliers(messageSupp); }
			 */
			rfpEventMessage.setBuyer(rfpEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfpEventMessage> replyList = new ArrayList<>();
				for (RfpEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfpEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfpEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfpEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	private void touchRfpEventMessage(String eventId, List<RfpEventMessage> list) {
		List<RfpEventMessage> replies = rfpMessageDao.getRfpEventMessagesRepliesByEventId(eventId);

		List<RfpEventMessage> eventMessageReplies = new ArrayList<RfpEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (RfpEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (RfpEventMessage rfpEventMessage : list) {
			List<Supplier> messageSupp = rfpMessageDao.getRfpEventMessagesSupplierById(rfpEventMessage.getId());
			if (CollectionUtil.isNotEmpty(messageSupp)) {
				rfpEventMessage.setSuppliers(messageSupp);
			}
			rfpEventMessage.setBuyer(rfpEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<RfpEventMessage> replyList = new ArrayList<>();
				for (RfpEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && rfpEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						rfpEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				rfpEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	//PH-4113 for mailbox
	@Override
	public List<PoEventMessage> getPoEventMessagesByEventId(String eventId, int page, int size, String search) {
		List<PoEventMessage> list = poMessageDao.getPoEventMessagesByEventId(eventId, page, size, search);
		touchPoEventMessage(eventId, list);
		return list;
	}

	private void touchPoEventMessage(String eventId, List<PoEventMessage> list) {
		List<PoEventMessage> replies = poMessageDao.getPoEventMessagesRepliesByEventId(eventId);
		List<PoEventMessage> eventMessageReplies = new ArrayList<PoEventMessage>();
		if (CollectionUtil.isNotEmpty(replies)) {
			for (PoEventMessage reply : replies) {
				reply.setReplies(null);
				eventMessageReplies.add(reply);
			}
		}

		for (PoEventMessage poEventMessage : list) {
			List<Supplier> messageSupp = poMessageDao.getPoEventMessagesSupplierById(poEventMessage.getId());
			if (CollectionUtil.isNotEmpty(messageSupp)) {
				poEventMessage.setSuppliers(messageSupp);
			}
			poEventMessage.setBuyer(poEventMessage.getBuyer().createShallowCopy());
			if (CollectionUtil.isNotEmpty(eventMessageReplies)) {
				List<PoEventMessage> replyList = new ArrayList<>();
				for (PoEventMessage replyEventMessage : eventMessageReplies) {
					if (replyEventMessage.getParent() != null && poEventMessage.getId().equals(replyEventMessage.getParent().getId())) {
						replyList.add(replyEventMessage);
						poEventMessage.setReplies(replyList);

					}
					replyEventMessage.setSuppliers(null);
				}
			} else {
				poEventMessage.setReplies(new ArrayList<>());
			}
		}
	}

	//PH-4113
	@Override
	public long getTotalFilteredPoEventMessageCount(String eventId, String search) {
		return poMessageDao.getTotalFilteredEventMessageCount(eventId, search);
	}

	@Override
	public long getTotalPoEventMessageCount(String eventId) {
		return poMessageDao.getTotalEventMessageCount(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public PoEventMessage saveEventMessage(PoEventMessage eventMessage) {

		// In case if supplier is sending message to the Buyer, the buyer will not have been set yet.
		if (eventMessage.getBuyer() == null) {
			PoEvent event = poEventDao.findById(eventMessage.getEvent().getId());
			Buyer buyer = new Buyer();
			buyer.setId(event.getTenantId());
			eventMessage.setBuyer(buyer);
		}

		eventMessage = poMessageDao.saveOrUpdate(eventMessage);
		if (eventMessage != null && eventMessage.getEvent() != null) {
			eventMessage.getEvent().getEventName();
		}
		return eventMessage;
	}

	@Override
	public PoEventMessage getPoEventMessageById(String messageId) {
		return poMessageDao.findById(messageId);
	}
}
