package com.privasia.procurehere.core.dao.impl;

import java.util.Date;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.OwnerNotificationMessageDao;
import com.privasia.procurehere.core.entity.OwnerNotificationMessage;

@Repository
public class OwnerNotificationMessageDaoImpl extends GenericNotificationMessageDaoImpl<OwnerNotificationMessage, String> implements OwnerNotificationMessageDao {

	@Override
	public void getOwnerNotificationMessageMarkAllRead() {
		StringBuilder hsql = new StringBuilder("update OwnerNotificationMessage m set m.processed = true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.executeUpdate();
		LOG.info("Updated : OwnerNotificationMessageMarkAllRead : " + new Date());
	}

}
