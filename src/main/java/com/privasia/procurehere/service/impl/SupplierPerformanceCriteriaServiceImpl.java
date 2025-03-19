/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierPerformanceCriteriaDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;
import com.privasia.procurehere.service.SupplierPerformanceCriteriaService;

/**
 * @author Jayshree
 *
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceCriteriaServiceImpl implements SupplierPerformanceCriteriaService {

	@Autowired
	SupplierPerformanceCriteriaDao supplierPerformanceCriteriaDao;

	@Override
	public List<SupplierPerformanceCriteria> getSPCriteriaByFormIdAndUserId(String formId, String evalUserId) {
		return supplierPerformanceCriteriaDao.getSPCriteriaByFormIdAndUserId(formId, evalUserId);
	}
	
	@Override
	public List<SupplierPerformanceCriteria> getSPCriteriaForAppByFormIdAndUserId(String formId, String userId) {
		return supplierPerformanceCriteriaDao.getSPCriteriaForAppByFormIdAndUserId(formId, userId);
	}

}
