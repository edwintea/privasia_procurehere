package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventMeetingReminder;

public interface RfpMeetingReminderDao extends GenericDao<RfpEventMeetingReminder, String> {

	List<RfpEventMeetingReminder> getAllRfpMeetingReminderForMeeting(String meetingId);

	List<RfpEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfpEventMeetingReminder getRfpEventMeetingReminderById(String id);

}
