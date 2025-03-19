package com.privasia.procurehere.core.dao.impl;

import java.util.Date;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FinanceNotificationMessageDao;
import com.privasia.procurehere.core.entity.FinanceNotificationMessage;

@Repository
public class FinanceNotificationMessageDaoImpl extends GenericNotificationMessageDaoImpl<FinanceNotificationMessage, String> implements FinanceNotificationMessageDao {

	@Override
	public void getFinanceNotificationMessageMarkAllRead() {
		StringBuilder hsql = new StringBuilder("update FinanceNotificationMessage m set m.processed = true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.executeUpdate();
		LOG.info("Updated : FinanaceNotificationMessageMarkAllRead : " + new Date());
	}

}
