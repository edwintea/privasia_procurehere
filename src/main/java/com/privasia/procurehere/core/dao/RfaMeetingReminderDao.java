package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventMeetingReminder;

public interface RfaMeetingReminderDao extends GenericDao<RfaEventMeetingReminder, String> {

	List<RfaEventMeetingReminder> getAllRfaMeetingReminderForMeeting(String meetingId);

	List<RfaEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfaEventMeetingReminder getRfaEventMeetingReminderById(String id);

}
