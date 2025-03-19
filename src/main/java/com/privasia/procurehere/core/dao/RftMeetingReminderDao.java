package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventMeetingReminder;

public interface RftMeetingReminderDao extends GenericDao<RftEventMeetingReminder, String> {

	/**
	 * @param meetingId
	 * @return
	 */
	List<RftEventMeetingReminder> getAllRftMeetingReminderForMeeting(String meetingId);

	/**
	 * @return
	 */
	List<RftEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RftEventMeetingReminder getRftEventMeetingReminderById(String id);

}
