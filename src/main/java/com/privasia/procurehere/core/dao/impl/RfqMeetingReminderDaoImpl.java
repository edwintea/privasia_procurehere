package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.RfqMeetingReminderDao;
import com.privasia.procurehere.core.entity.RfqEventMeetingReminder;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;

@Repository
public class RfqMeetingReminderDaoImpl extends GenericDaoImpl<RfqEventMeetingReminder, String> implements RfqMeetingReminderDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventMeetingReminder> getAllRfqMeetingReminderForMeeting(String meetingId) {
		final Query query = getEntityManager().createQuery("from RfqEventMeetingReminder rem where rem.rfxEventMeeting.id = :meetingId");
		query.setParameter("meetingId", meetingId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventMeetingReminder> getMeetingRemindersForNotification() {
		StringBuilder hsql = new StringBuilder("select mr from RfqEventMeetingReminder mr inner join fetch mr.rfxEvent as e inner join fetch mr.rfxEventMeeting m where mr.reminderSent = false and mr.reminderDate <= :now and e.status in (:status) and m.status <> :meetingStatus order by mr.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		EventStatus status[] = { EventStatus.ACTIVE, EventStatus.CLOSED };
		query.setParameter("status", Arrays.asList(status));
		query.setParameter("meetingStatus", MeetingStatus.CANCELLED);
		List<RfqEventMeetingReminder> returnList = (List<RfqEventMeetingReminder>) query.getResultList();
		return returnList;
	}

	@Override
	public int updateImmediately(String reminderId) {
		String hql = "update RfqEventMeetingReminder re set re.reminderSent = :reminderSent where re.id = :id ";
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
	public RfqEventMeetingReminder getRfqEventMeetingReminderById(String id) {
		final Query query = getEntityManager().createQuery("select rem from RfqEventMeetingReminder rem  inner join fetch rem.rfxEvent as e  inner join fetch rem.rfxEventMeeting left outer join fetch e.eventOwner as eo left outer join fetch e.businessUnit as bu where rem.id = :id");
		query.setParameter("id", id);
		List<RfqEventMeetingReminder> list = (List<RfqEventMeetingReminder>) query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}


}
