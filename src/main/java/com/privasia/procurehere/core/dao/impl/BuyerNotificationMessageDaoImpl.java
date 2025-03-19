package com.privasia.procurehere.core.dao.impl;

import java.util.Date;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BuyerNotificationMessageDao;
import com.privasia.procurehere.core.entity.BuyerNotificationMessage;

@Repository
public class BuyerNotificationMessageDaoImpl extends GenericNotificationMessageDaoImpl<BuyerNotificationMessage, String> implements BuyerNotificationMessageDao {

	@Override
	public void setBuyerNotificationMessageMarkAllRead() {
		StringBuilder hsql = new StringBuilder("update BuyerNotificationMessage m set m.processed = true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.executeUpdate();
		LOG.info("Updated : BuyerNotificationMessageMarkAllRead : " + new Date());
	}

}
