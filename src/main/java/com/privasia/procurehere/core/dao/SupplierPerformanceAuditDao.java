package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;

/**
 * @author anshul
 */
public interface SupplierPerformanceAuditDao extends GenericDao<SupplierPerformanceAudit, String> {
	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormId(String formId);

	/**
	 * @param formId
	 * @param evaluatorUserId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForEvaluater(String formId, String evaluatorUserId);

	/**
	 * 
	 * @param formId
	 * @param evaluatorUserId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForApprover(String formId, String evaluatorUserId);

	/**
	 * 
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForOwner(String formId);

}
