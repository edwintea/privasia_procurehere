package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ProductContractReminder;

public interface ProductContractReminderService {
	/**
	 * @param contractReminder
	 */
	public void saveProductContractReminder(ProductContractReminder contractReminder);

	/**
	 * @param id
	 * @return
	 */
	public List<ProductContractReminder> getAllContractRemindersByContractId(String id);

	/**
	 * @param reminderId
	 * @return
	 */
	public ProductContractReminder getContractReminderById(String reminderId);

	/**
	 * @param productContractReminder
	 */
	public void deleteContractReminder(ProductContractReminder productContractReminder);

	/**
	 * @param interval
	 * @param contractId
	 * @return
	 */
	public boolean isReminderPresentForSameInterval(int interval, String contractId);

}
