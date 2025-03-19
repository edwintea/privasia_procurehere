package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.RfaReminderDao;
import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;

@Repository
public class RfaReminderDaoImpl extends GenericDaoImpl<RfaReminder, String> implements RfaReminderDao {


	@SuppressWarnings("unchecked")
	@Override
	public List<RfaReminder> getAllRfaEventReminderForEvent(String id, Boolean startReminder) throws ApplicationException {
		if (startReminder == null) {
			throw new ApplicationException("Must indicate the reminder type to fetch the reminder list.");
		}
		final Query query = getEntityManager().createQuery("from RfaReminder rem where rem.rfaEvent.id = :id and rem.startReminder = :startReminder order by rem.startReminder desc,rem.reminderDate");
		query.setParameter("id", id);
		query.setParameter("startReminder", startReminder);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaReminder> getEventRemindersForNotification() {
		StringBuilder hsql = new StringBuilder("from RfaReminder r inner join fetch r.rfaEvent as e where r.reminderSent = false and r.reminderDate <= :now and e.status in (:status) order by r.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE, EventStatus.APPROVED));
		List<RfaReminder> returnList = (List<RfaReminder>) query.getResultList();
		return returnList;
	}
	
	@Override
	public int updateImmediately(String reminderId) {
		String hql = "update RfaReminder re set re.reminderSent = :reminderSent where re.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("reminderSent", Boolean.TRUE);
		query.setParameter("id", reminderId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public RfaReminder getRfaEventReminderById(String id) {
		final Query query = getEntityManager().createQuery("from RfaReminder rem  inner join fetch rem.rfaEvent as e left outer join fetch e.eventOwner as eo left outer join fetch e.businessUnit as bu where rem.id = :id");
		query.setParameter("id", id);
		List<RfaReminder> list = (List<RfaReminder>) query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<RfaReminder> getAllRfaEventReminderForEvent(String id) {
		final Query query = getEntityManager().createQuery("from RfaReminder rem where rem.rfaEvent.id = :id  order by rem.reminderDate");
		query.setParameter("id", id);
		return query.getResultList();
	}
}
