package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.FinanceNotificationMessage;

public interface FinanceNotificationMessageDao extends GenericNotificationMessageDao<FinanceNotificationMessage, String> {

	void getFinanceNotificationMessageMarkAllRead();
}
