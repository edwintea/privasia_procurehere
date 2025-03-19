/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;

/**
 * @author Jayshree
 *
 */
public interface SupplierPerformanceCriteriaDao extends GenericDao<SupplierPerformanceCriteria, String> {

	/**
	 * @param formId
	 * @param evalUserId
	 * @return
	 */
	List<SupplierPerformanceCriteria> getSPCriteriaByFormIdAndUserId(String formId, String evalUserId);

	/**
	 * @param formId
	 * @param userId
	 * @return
	 */
	List<SupplierPerformanceCriteria> getSPCriteriaForAppByFormIdAndUserId(String formId, String userId);

}
