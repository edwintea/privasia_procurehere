package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftReminder;

public interface RftReminderDao extends GenericDao<RftReminder, String> {

	List<RftReminder> getAllRftEventReminderForEvent(String id);

	/**
	 * @return
	 */
	List<RftReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RftReminder getRftEventReminderById(String id);

}
