package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ProductContractReminder;

public interface ProductContractReminderDao extends GenericDao<ProductContractReminder, String> {

	/**
	 * @param contractId
	 * @return
	 */
	List<ProductContractReminder> getAllContractRemindersByContractId(String contractId);

	/**
	 * @return
	 */
	List<ProductContractReminder> getContractRemindersForNotification();

	/**
	 * @param interval
	 * @param contractId
	 * @return
	 */
	boolean isReminderPresentForSameInterval(int interval, String contractId);

}
