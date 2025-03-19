package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.RfpReminderDao;
import com.privasia.procurehere.core.entity.RfpReminder;
import com.privasia.procurehere.core.enums.EventStatus;

@Repository
public class RfpReminderDaoImpl extends GenericDaoImpl<RfpReminder, String> implements RfpReminderDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpReminder> getAllRfpEventReminderForEvent(String id) {
		final Query query = getEntityManager().createQuery("from RfpReminder rem where rem.rfxEvent.id = :id order by rem.startReminder desc,rem.reminderDate");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpReminder> getEventRemindersForNotification() {
		StringBuilder hsql = new StringBuilder("from RfpReminder r inner join fetch r.rfxEvent as e where r.reminderSent = false and r.reminderDate <= :now and e.status in (:status) order by r.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE, EventStatus.APPROVED));
		List<RfpReminder> returnList = (List<RfpReminder>) query.getResultList();
		return returnList;
	}

	@Override
	public int updateImmediately(String reminderId) {
		String hql = "update RfpReminder re set re.reminderSent = :reminderSent where re.id = :id ";
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
	public RfpReminder getRfpEventReminderById(String id) {
		final Query query = getEntityManager().createQuery("from RfpReminder rem  inner join fetch rem.rfxEvent as e left outer join fetch e.eventOwner as eo left outer join fetch e.businessUnit as bu where rem.id = :id");
		query.setParameter("id", id);
		List<RfpReminder> list = (List<RfpReminder>) query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
