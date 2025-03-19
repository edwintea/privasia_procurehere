package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;

/**
 * @author pooja
 */
public interface SupplierFormSubmitionAuditDao extends GenericDao<SupplierFormSubmitionAudit, String> {
	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormSubmitionAudit> getFormAuditByFormId(String poId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormSubmitionAudit> getFormAuditByFormIdForBuyer(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormSubmitionAudit> getFormAuditByFormIdForSupplier(String formId);

}
