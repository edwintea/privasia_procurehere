/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Nitin Otageri
 *
 */
import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericNotificationMessageDao;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.NotificationMessageBase;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericNotificationMessageDaoImpl<T extends NotificationMessageBase, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericNotificationMessageDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericNotificationMessageDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<NotificationMessage> getNotificationsForTenant(String tenantId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as n left outer join fetch n.messageTo as t where n.tenantId = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NotificationMessage> getNotificationsForUser(String userId, int page, int size) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.NotificationMessage(n.id, n.subject, n.message, n.createdDate, n.tenantId, n.url, n.notificationType, n.processed, n.processedDate) from " + entityClass.getSimpleName() + " as n  join n.messageTo as t where t.id = :userId" +
				" and n.processed = false order by n.createdDate desc");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("userId", userId);
		query.setFirstResult(page * size);
		query.setMaxResults(size);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUnprocessedNotifications() {
		StringBuilder hsql = new StringBuilder("select n.id from " + entityClass.getSimpleName() + " as n where n.processed = false");
		final Query query = getEntityManager().createQuery(hsql.toString());
		return query.getResultList();
	}

	@Override
	@Transactional
	public void clearAllNotificationMessages(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from " + entityClass.getSimpleName() +
				" m where m.messageTo.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		int count = query.executeUpdate();
		LOG.info("deleted : clearAllNotificationMessages : " + count);
	}

}
