package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventMeetingReminder;

public interface RfiMeetingReminderDao extends GenericDao<RfiEventMeetingReminder, String> {

	List<RfiEventMeetingReminder> getAllRfiMeetingReminderForMeeting(String meetingId);

	List<RfiEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfiEventMeetingReminder getRfiEventMeetingReminderById(String id);

}
