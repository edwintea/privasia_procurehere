/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;

/**
 * @author Jayshree
 *
 */
public interface SupplierPerformanceFormCriteriaService {

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceFormCriteria> getSPFormCriteriaByFormId(String formId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param formId
	 * @param supplierId
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndFormId(Date startDate, Date endDate, String formId, String supplierId, String tenantId);

}
