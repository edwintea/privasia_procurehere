package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerNotificationMessageDao;
import com.privasia.procurehere.core.dao.FinanceNotificationMessageDao;
import com.privasia.procurehere.core.dao.NotificationMessageDao;
import com.privasia.procurehere.core.dao.OwnerNotificationMessageDao;
import com.privasia.procurehere.core.dao.SupplierNotificationMessageDao;
import com.privasia.procurehere.core.entity.BuyerNotificationMessage;
import com.privasia.procurehere.core.entity.FinanceNotificationMessage;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.OwnerNotificationMessage;
import com.privasia.procurehere.core.entity.SupplierNotificationMessage;
import com.privasia.procurehere.service.DashboardNotificationService;

@Service
@Transactional(readOnly = true)
public class DashboardNotificationServiceImpl implements DashboardNotificationService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(DashboardNotificationServiceImpl.class);

	@Autowired
	NotificationMessageDao notificationMessageDao;

	@Autowired
	OwnerNotificationMessageDao ownerNotificationMessageDao;

	@Autowired
	FinanceNotificationMessageDao financeNotificationMessageDao;

	@Autowired
	SupplierNotificationMessageDao supplierNotificationMessageDao;

	@Autowired
	BuyerNotificationMessageDao buyerNotificationMessageDao;

	@Override
	public List<NotificationMessage> getNotificationsForBuyerUser(String userId, int page, int size) {
		List<NotificationMessage> list = buyerNotificationMessageDao.getNotificationsForUser(userId, page, size);
		for (NotificationMessage message : list) {
			message.setCreatedBy(null);
			message.setMessageTo(null);
		}
		return list;
	}

	@Override
	public List<NotificationMessage> getNotificationsForSupplierUser(String userId, int page, int size) {
		List<NotificationMessage> list = supplierNotificationMessageDao.getNotificationsForUser(userId, page, size);
		for (NotificationMessage message : list) {
			message.setCreatedBy(null);
			message.setMessageTo(null);
		}
		return list;
	}

	@Override
	public List<NotificationMessage> getNotificationsForOwnerUser(String userId, int page, int size) {
		List<NotificationMessage> list = ownerNotificationMessageDao.getNotificationsForUser(userId, page, size);
		for (NotificationMessage message : list) {
			message.setCreatedBy(null);
			message.setMessageTo(null);
		}
		return list;
	}

	@Override
	public List<NotificationMessage> getNotificationsForFinanceUser(String userId, int page, int size) {
		List<NotificationMessage> list = financeNotificationMessageDao.getNotificationsForUser(userId, page, size);
		for (NotificationMessage message : list) {
			message.setCreatedBy(null);
			message.setMessageTo(null);
		}
		return list;
	}

	@Override
	public List<String> getUnprocessedNotifications() {
		return notificationMessageDao.getUnprocessedNotifications();
	}

	@Override
	@Transactional(readOnly = false)
	public NotificationMessage update(NotificationMessage notificationMessage) {
		notificationMessage.setProcessed(true);
		notificationMessage.setProcessedDate(new Date());
		return notificationMessageDao.update(notificationMessage);
	}

	@Override
	@Transactional(readOnly = false)
	public BuyerNotificationMessage update(BuyerNotificationMessage buyerNotificationMessage) {
		buyerNotificationMessage.setProcessed(true);
		buyerNotificationMessage.setProcessedDate(new Date());
		return buyerNotificationMessageDao.update(buyerNotificationMessage);
	}

	@Override
	@Transactional(readOnly = false)
	public OwnerNotificationMessage update(OwnerNotificationMessage ownerNotificationMessage) {
		ownerNotificationMessage.setProcessed(true);
		ownerNotificationMessage.setProcessedDate(new Date());
		return ownerNotificationMessageDao.update(ownerNotificationMessage);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierNotificationMessage update(SupplierNotificationMessage supplierNotificationMessage) {
		supplierNotificationMessage.setProcessed(true);
		supplierNotificationMessage.setProcessedDate(new Date());
		return supplierNotificationMessageDao.update(supplierNotificationMessage);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public NotificationMessage save(NotificationMessage notificationMessage) {
		return notificationMessageDao.saveOrUpdate(notificationMessage);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public OwnerNotificationMessage saveOwnerNotificationMessage(OwnerNotificationMessage ownerNotificationMessage) {
		return ownerNotificationMessageDao.saveOrUpdate(ownerNotificationMessage);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SupplierNotificationMessage saveSupplierNotificationMessage(SupplierNotificationMessage supplerNotificationMessage) {
		return supplierNotificationMessageDao.saveOrUpdate(supplerNotificationMessage);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public BuyerNotificationMessage saveBuyerNotificationMessage(BuyerNotificationMessage buyerNotificationMessage) {
		return buyerNotificationMessageDao.saveOrUpdate(buyerNotificationMessage);
	}

	@Override
	public BuyerNotificationMessage getBuyerNotificationMessageByMessageId(String messageId) {
		return buyerNotificationMessageDao.findById(messageId);
	}

	@Override
	public SupplierNotificationMessage getSupplierNotificationMessageByMessageId(String messageId) {
		return supplierNotificationMessageDao.findById(messageId);
	}

	@Override
	public OwnerNotificationMessage getOwnerNotificationMessageByMessageId(String messageId) {
		return ownerNotificationMessageDao.findById(messageId);
	}

	@Override
	@Transactional(readOnly = false)
	public void setBuyerNotificationMessageMarkAllRead() {
		buyerNotificationMessageDao.setBuyerNotificationMessageMarkAllRead();
	}

	@Override
	@Transactional(readOnly = false)
	public void setSupplierNotificationMessageMarkAllRead() {
		supplierNotificationMessageDao.setSupplierNotificationMessageMarkAllRead();
	}

	@Override
	@Transactional(readOnly = false)
	public void getOwnerNotificationMessageMarkAllRead() {
		ownerNotificationMessageDao.getOwnerNotificationMessageMarkAllRead();
	}

	@Override
	@Transactional(readOnly = false)
	public void clearAllBuyerNotificationMessage(String loggedInUserTenantId) {
		buyerNotificationMessageDao.clearAllNotificationMessages(loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void clearAllSupplierNotificationMessage(String loggedInUserTenantId) {
		supplierNotificationMessageDao.clearAllNotificationMessages(loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void clearAllOwnerNotificationMessage(String loggedInUserTenantId) {
		ownerNotificationMessageDao.clearAllNotificationMessages(loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveFinanceNotification(FinanceNotificationMessage message) {
		financeNotificationMessageDao.saveOrUpdate(message);

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateNotificationAsProccessed(String notificationId) {
		notificationMessageDao.updateNotificationAsProccessed(notificationId);
	}
}
