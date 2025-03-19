package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.NotificationMessage;

public interface NotificationMessageDao extends GenericNotificationMessageDao<NotificationMessage, String> {

	/**
	 * 
	 * @param notificationId
	 */
	void updateNotificationAsProccessed(String notificationId);
}
