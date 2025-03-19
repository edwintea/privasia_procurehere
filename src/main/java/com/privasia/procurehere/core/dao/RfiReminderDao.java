package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiReminder;

public interface RfiReminderDao extends GenericDao<RfiReminder, String> {

	List<RfiReminder> getAllRfiEventReminderForEvent(String id);

	/**
	 * @return
	 */
	List<RfiReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfiReminder getRfiEventReminderById(String id);

}
