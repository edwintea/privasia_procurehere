package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ProductContractReminderDao;
import com.privasia.procurehere.core.entity.ProductContractReminder;
import com.privasia.procurehere.service.ProductContractReminderService;

/**
 * @author pooja
 */
@Service
@Transactional(readOnly = true)
public class ProductContractReminderServiceImpl implements ProductContractReminderService {

	@Autowired
	ProductContractReminderDao productContractReminderDao;

	@Override
	@Transactional(readOnly = false)
	public void saveProductContractReminder(ProductContractReminder contractReminder) {
		productContractReminderDao.saveOrUpdate(contractReminder);
	}

	@Override
	public List<ProductContractReminder> getAllContractRemindersByContractId(String id) {
		return productContractReminderDao.getAllContractRemindersByContractId(id);
	}

	@Override
	public ProductContractReminder getContractReminderById(String reminderId) {
		return productContractReminderDao.findById(reminderId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteContractReminder(ProductContractReminder productContractReminder) {
		productContractReminderDao.delete(productContractReminder);
	}

	@Override
	public boolean isReminderPresentForSameInterval(int interval, String contractId) {
		return productContractReminderDao.isReminderPresentForSameInterval(interval, contractId);
	}
}