package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.OwnerNotificationMessage;

public interface OwnerNotificationMessageDao extends GenericNotificationMessageDao<OwnerNotificationMessage, String> {

	void getOwnerNotificationMessageMarkAllRead();
}
