/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierPerformanceFormCriteriaDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.service.SupplierPerformanceFormCriteriaService;

/**
 * @author Jayshree
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceFormCriteriaServiceImpl implements SupplierPerformanceFormCriteriaService {

	@Autowired
	SupplierPerformanceFormCriteriaDao supplierPerformanceFormCriteriaDao;

	@Override
	public List<SupplierPerformanceFormCriteria> getSPFormCriteriaByFormId(String formId) {
		return supplierPerformanceFormCriteriaDao.getSPFormCriteriaByFormId(formId);
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndFormId(Date startDate, Date endDate, String formId, String supplierId, String tenantId) {
		return supplierPerformanceFormCriteriaDao.getOverallScoreByCriteriaAndFormId(startDate, endDate, formId, supplierId, tenantId);
	}

}
