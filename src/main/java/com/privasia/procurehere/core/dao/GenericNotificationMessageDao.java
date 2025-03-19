package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.NotificationMessageBase;

/**
 * @author Nitin Otageri
 */
public interface GenericNotificationMessageDao<T extends NotificationMessageBase, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param tenantId
	 * @return
	 */
	List<NotificationMessage> getNotificationsForTenant(String tenantId);

	/**
	 * @param page TODO
	 * @param size TODO
	 * @param tenantId
	 * @return
	 */
	List<NotificationMessage> getNotificationsForUser(String userId, int page, int size);

	/**
	 * @return
	 */
	List<String> getUnprocessedNotifications();

	/**
	 * @param tenantId
	 * 
	 */
	void clearAllNotificationMessages(String tenantId);
}
