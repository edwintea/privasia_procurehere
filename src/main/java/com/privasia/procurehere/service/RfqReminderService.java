package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqReminder;

/**
 * @author ravi
 */

public interface RfqReminderService {

	/**
	 * @return
	 */
	List<RfqReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);
}
