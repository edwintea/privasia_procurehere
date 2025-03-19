package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;

/**
 * @author pooja
 */
public interface SupplierFormSubmitionAuditService {
	/**
	 * @param audit
	 */
	void save(SupplierFormSubmitionAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<SupplierFormSubmitionAudit> getFormAuditById(String id);

	/**
	 * @param formSubId
	 * @return
	 */
	List<SupplierFormSubmitionAudit> getFormAuditByFormIdForBuyer(String formSubId);

	/**
	 * @param formSubId
	 * @return
	 */
	List<SupplierFormSubmitionAudit> getFormAuditByFormIdForSupplier(String formSubId);

}
