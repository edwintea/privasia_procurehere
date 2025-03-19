package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqReminder;

public interface RfqReminderDao extends GenericDao<RfqReminder, String> {

	List<RfqReminder> getAllEventReminderForEvent(String id);

	/**
	 * @return
	 */
	List<RfqReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfqReminder getRfqEventReminderById(String id);

}
