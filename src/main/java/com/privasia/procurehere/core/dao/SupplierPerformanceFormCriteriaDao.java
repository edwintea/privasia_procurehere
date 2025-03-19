/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;

/**
 * @author Jayshree
 *
 */
public interface SupplierPerformanceFormCriteriaDao extends GenericDao<SupplierPerformanceFormCriteria, String> {

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
