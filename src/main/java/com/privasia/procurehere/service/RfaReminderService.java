package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaReminder;

/**
 * @author ravi
 */

public interface RfaReminderService {

	/**
	 * @return
	 */
	List<RfaReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);
}
