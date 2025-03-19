/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Ravi
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

import com.privasia.procurehere.core.dao.GenericEventDao;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericEventDaoImpl<T extends Event, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericEventDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public void suspendEvent(String eventId, SuspensionType suspensionType, String supensionRemarks) {
		StringBuilder hsql = new StringBuilder("update " + entityClass.getSimpleName() + "  set  status = :status , suspensionType =:suspensionType , suspendRemarks = :supensionRemarks  where id =:eventId ");
		LOG.info("HQL : " + hsql.toString());
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", EventStatus.SUSPENDED);
		query.setParameter("suspensionType", suspensionType);
		query.setParameter("supensionRemarks", supensionRemarks);
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	public void resumeEvent(String eventId) {
		StringBuilder hsql = new StringBuilder("update " + entityClass.getSimpleName() + "  set  status = :status , suspensionType = null , suspendRemarks = null  where id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	public String findBusinessUnitName(String eventId) {
		StringBuilder hsql = new StringBuilder("select bu.displayName from " + entityClass.getSimpleName() + " a  left outer join a.businessUnit bu where a.id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (String) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventTimerPojo getTimeEventByeventId(String eventId) {
		final Query query = getEntityManager().createQuery("select NEW com.privasia.procurehere.core.pojo.EventTimerPojo(r.id, r.eventStart,r.eventEnd,r.status) from " + entityClass.getSimpleName() + " r where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTimerPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		StringBuilder hsql = new StringBuilder("select NEW com.privasia.procurehere.core.entity.Event(a.id, a.eventId, a.eventPublishDate, a.eventName, a.eventEnd, a.eventStart, a.status, a.referanceNumber, cb.id, cb.name,cb.communicationEmail, cb.emailNotifications )from " + entityClass.getSimpleName() + " a  left outer join a.createdBy cb where a.id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (Event) query.getSingleResult();
	}

}
