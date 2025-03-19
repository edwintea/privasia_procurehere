package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpReminder;

public interface RfpReminderDao extends GenericDao<RfpReminder, String> {

	List<RfpReminder> getAllRfpEventReminderForEvent(String id);

	/**
	 * @return
	 */
	List<RfpReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfpReminder getRfpEventReminderById(String id);

}
