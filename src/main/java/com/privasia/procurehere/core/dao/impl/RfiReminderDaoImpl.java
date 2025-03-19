package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.RfiReminderDao;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.core.enums.EventStatus;

@Repository
public class RfiReminderDaoImpl extends GenericDaoImpl<RfiReminder, String> implements RfiReminderDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiReminder> getAllRfiEventReminderForEvent(String id) {
		final Query query = getEntityManager().createQuery("from RfiReminder rem where rem.rfiEvent.id = :id  order by rem.startReminder desc,rem.reminderDate");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiReminder> getEventRemindersForNotification() {
		StringBuilder hsql = new StringBuilder("from RfiReminder r inner join fetch r.rfiEvent as e where r.reminderSent = false and r.reminderDate <= :now and e.status in (:status) order by r.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE, EventStatus.APPROVED));
		List<RfiReminder> returnList = (List<RfiReminder>) query.getResultList();
		return returnList;
	}

	@Override
	public int updateImmediately(String reminderId) {
		String hql = "update RfiReminder re set re.reminderSent = :reminderSent where re.id = :id ";
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
	public RfiReminder getRfiEventReminderById(String id) {
		final Query query = getEntityManager().createQuery("from RfiReminder rem  inner join fetch rem.rfiEvent as e left outer join fetch e.eventOwner as eo left outer join fetch e.businessUnit as bu where rem.id = :id");
		query.setParameter("id", id);
		List<RfiReminder> list = (List<RfiReminder>) query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
