package com.privasia.procurehere.core.dao.impl;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.NotificationMessageDao;
import com.privasia.procurehere.core.entity.NotificationMessage;

@Repository
public class NotificationMessageDaoImpl extends GenericNotificationMessageDaoImpl<NotificationMessage, String> implements NotificationMessageDao {

	@Override
	public void updateNotificationAsProccessed(String notificationId) {
		String hql = "update NotificationMessage set processed = :processed , processedDate = :processedDate where id = :id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("processed", true);
		query.setParameter("processedDate", new Date());
		query.setParameter("id", notificationId);
		query.executeUpdate();

	}

	public void deleteNotificationOlderThan15Days(String notificationId) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -15);

		String hql = "delete NotificationMessage where processedDate < :processedDate  and processed = :processed";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("processed", true);
		query.setParameter("processedDate", cal.getTime());
		query.setParameter("id", notificationId);
		query.executeUpdate();

	}
}
