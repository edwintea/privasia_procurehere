package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventMeetingReminder;

public interface RfqMeetingReminderDao extends GenericDao<RfqEventMeetingReminder, String> {

	List<RfqEventMeetingReminder> getAllRfqMeetingReminderForMeeting(String meetingId);

	List<RfqEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * 
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfqEventMeetingReminder getRfqEventMeetingReminderById(String id);

}
