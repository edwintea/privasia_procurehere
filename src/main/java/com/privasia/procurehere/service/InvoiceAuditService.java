package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.InvoiceAudit;

/**
 * @author ravi
 */
public interface InvoiceAuditService {
	/**
	 * @param audit
	 */
	void save(InvoiceAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<InvoiceAudit> getInvoiceAuditForBuyerByInvoiceId(String id);

	/**
	 * @param doId
	 * @return
	 */
	List<InvoiceAudit> getInvoiceAuditForSupplierByInvoiceId(String doId);

}
