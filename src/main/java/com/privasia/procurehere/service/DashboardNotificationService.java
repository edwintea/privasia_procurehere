package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.BuyerNotificationMessage;
import com.privasia.procurehere.core.entity.FinanceNotificationMessage;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.OwnerNotificationMessage;
import com.privasia.procurehere.core.entity.SupplierNotificationMessage;

public interface DashboardNotificationService {

	/**
	 * @return
	 */
	List<String> getUnprocessedNotifications();

	/**
	 * @param page TODO
	 * @param size TODO
	 * @param tenantId
	 * @return
	 */
	List<NotificationMessage> getNotificationsForBuyerUser(String userId, int page, int size);

	/**
	 * @param userId
	 * @param page TODO
	 * @param size TODO
	 * @return
	 */
	List<NotificationMessage> getNotificationsForSupplierUser(String userId, int page, int size);

	/**
	 * @param userId
	 * @param page TODO
	 * @param size TODO
	 * @return
	 */
	List<NotificationMessage> getNotificationsForOwnerUser(String userId, int page, int size);

	/**
	 * @param notificationMessage
	 * @return
	 */
	NotificationMessage update(NotificationMessage notificationMessage);

	/**
	 * @param notificationMessage
	 * @return
	 */
	NotificationMessage save(NotificationMessage notificationMessage);

	/**
	 * @param buyerNotificationMessage
	 * @return
	 */
	BuyerNotificationMessage update(BuyerNotificationMessage buyerNotificationMessage);

	/**
	 * @param ownerNotificationMessage
	 * @return
	 */
	OwnerNotificationMessage update(OwnerNotificationMessage ownerNotificationMessage);

	/**
	 * @param supplierNotificationMessage
	 * @return
	 */
	SupplierNotificationMessage update(SupplierNotificationMessage supplierNotificationMessage);

	/**
	 * @param ownerNotificationMessage
	 * @return
	 */
	OwnerNotificationMessage saveOwnerNotificationMessage(OwnerNotificationMessage ownerNotificationMessage);

	/**
	 * @param supplerNotificationMessage
	 * @return
	 */
	SupplierNotificationMessage saveSupplierNotificationMessage(SupplierNotificationMessage supplerNotificationMessage);

	/**
	 * @param buyerNotificationMessage
	 * @return
	 */
	BuyerNotificationMessage saveBuyerNotificationMessage(BuyerNotificationMessage buyerNotificationMessage);

	BuyerNotificationMessage getBuyerNotificationMessageByMessageId(String messageId);

	SupplierNotificationMessage getSupplierNotificationMessageByMessageId(String messageId);

	OwnerNotificationMessage getOwnerNotificationMessageByMessageId(String messageId);

	void setBuyerNotificationMessageMarkAllRead();

	void setSupplierNotificationMessageMarkAllRead();

	void getOwnerNotificationMessageMarkAllRead();

	void clearAllBuyerNotificationMessage(String loggedInUserTenantId);

	void clearAllSupplierNotificationMessage(String loggedInUserTenantId);

	void clearAllOwnerNotificationMessage(String loggedInUserTenantId);

	List<NotificationMessage> getNotificationsForFinanceUser(String userId, int page, int size);

	void saveFinanceNotification(FinanceNotificationMessage message);

	/**
	 * @param notificationId
	 */
	void updateNotificationAsProccessed(String notificationId);

}
