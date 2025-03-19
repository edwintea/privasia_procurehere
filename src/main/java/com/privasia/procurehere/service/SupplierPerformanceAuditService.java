package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;

/**
 * @author anshul
 */
public interface SupplierPerformanceAuditService {

	/**
	 * @param audit
	 */
	void save(SupplierPerformanceAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormId(String formId);

	/**
	 * @param id
	 * @return
	 */
	SupplierPerformanceAudit getAuditByFormId(String id);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForOwner(String formId);

	/**
	 * @param formId
	 * @param evaluatorUserId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForEvaluater(String formId, String evaluatorUserId);

	/**
	 * @param formId
	 * @param evaluatorUserId
	 * @return
	 */
	List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForApprover(String formId, String evaluatorUserId);
}
