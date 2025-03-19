package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.RfaMeetingReminderDao;
import com.privasia.procurehere.core.entity.RfaEventMeetingReminder;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;

@Repository
public class RfaMeetingReminderDaoImpl extends GenericDaoImpl<RfaEventMeetingReminder, String> implements RfaMeetingReminderDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeetingReminder> getAllRfaMeetingReminderForMeeting(String meetingId) {
		final Query query = getEntityManager().createQuery("from RfaEventMeetingReminder rem where rem.rfaEventMeeting.id = :meetingId");
		query.setParameter("meetingId", meetingId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeetingReminder> getMeetingRemindersForNotification() {
		StringBuilder hsql = new StringBuilder("select mr from RfaEventMeetingReminder mr inner join fetch mr.rfaEvent as e inner join fetch mr.rfaEventMeeting m where mr.reminderSent = false and mr.reminderDate <= :now and e.status in (:status) and m.status <> :meetingStatus order by mr.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		EventStatus status[] = { EventStatus.ACTIVE, EventStatus.CLOSED };
		query.setParameter("status", Arrays.asList(status));
		query.setParameter("meetingStatus", MeetingStatus.CANCELLED);
		List<RfaEventMeetingReminder> returnList = (List<RfaEventMeetingReminder>) query.getResultList();
		return returnList;
	}

	@Override
	public int updateImmediately(String reminderId) {
		String hql = "update RfaEventMeetingReminder re set re.reminderSent = :reminderSent where re.id = :id ";
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
	public RfaEventMeetingReminder getRfaEventMeetingReminderById(String id) {
		final Query query = getEntityManager().createQuery("select rem from RfaEventMeetingReminder rem  inner join fetch rem.rfaEvent as e  inner join fetch rem.rfaEventMeeting left outer join fetch e.eventOwner as eo left outer join fetch e.businessUnit as bu where rem.id = :id");
		query.setParameter("id", id);
		List<RfaEventMeetingReminder> list = (List<RfaEventMeetingReminder>) query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
