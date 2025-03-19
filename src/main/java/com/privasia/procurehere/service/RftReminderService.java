package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftReminder;

/**
 * @author ravi
 */

public interface RftReminderService {

	/**
	 * @return
	 */
	List<RftReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);
}
