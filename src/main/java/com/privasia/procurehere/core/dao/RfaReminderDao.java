package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.core.exceptions.ApplicationException;

public interface RfaReminderDao extends GenericDao<RfaReminder, String> {

	/**
	 * @param id
	 * @param startReminder
	 * @return
	 * @throws ApplicationException
	 */
	List<RfaReminder> getAllRfaEventReminderForEvent(String id, Boolean startReminder) throws ApplicationException;

	/**
	 * @return
	 */
	List<RfaReminder> getEventRemindersForNotification();

	/**
	 * @param reminderId
	 * @return
	 */
	int updateImmediately(String reminderId);

	/**
	 * @param id
	 * @return
	 */
	RfaReminder getRfaEventReminderById(String id);


	List<RfaReminder> getAllRfaEventReminderForEvent(String id);

}
