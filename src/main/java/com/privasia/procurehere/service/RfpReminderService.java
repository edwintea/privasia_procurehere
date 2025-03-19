package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpReminder;

/**
 * @author ravi
 */

public interface RfpReminderService {

	/**
	 * @return
	 */
	List<RfpReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);
}
