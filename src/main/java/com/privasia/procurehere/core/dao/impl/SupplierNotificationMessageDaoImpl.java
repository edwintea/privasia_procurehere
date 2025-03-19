package com.privasia.procurehere.core.dao.impl;

import java.util.Date;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierNotificationMessageDao;
import com.privasia.procurehere.core.entity.SupplierNotificationMessage;

@Repository
public class SupplierNotificationMessageDaoImpl extends GenericNotificationMessageDaoImpl<SupplierNotificationMessage, String> implements SupplierNotificationMessageDao {

	@Override
	public void setSupplierNotificationMessageMarkAllRead() {
		StringBuilder hsql = new StringBuilder("update SupplierNotificationMessage m set m.processed = true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.executeUpdate();
		LOG.info("Updated : SupplierNotificationMessageMarkAllRead : " + new Date());
	}
}
