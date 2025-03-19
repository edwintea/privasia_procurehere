package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SupplierNotificationMessage;

public interface SupplierNotificationMessageDao extends GenericNotificationMessageDao<SupplierNotificationMessage, String> {

	void setSupplierNotificationMessageMarkAllRead();
}
