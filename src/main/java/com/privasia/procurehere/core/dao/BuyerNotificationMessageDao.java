package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.BuyerNotificationMessage;

public interface BuyerNotificationMessageDao extends GenericNotificationMessageDao<BuyerNotificationMessage, String> {

	void setBuyerNotificationMessageMarkAllRead();
}
