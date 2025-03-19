package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiReminder;

/**
 * @author ravi
 */

public interface RfiReminderService {

	/**
	 * @return
	 */
	List<RfiReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);
}
