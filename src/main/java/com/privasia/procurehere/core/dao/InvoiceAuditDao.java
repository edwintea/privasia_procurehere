package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.InvoiceAudit;

/**
 * @author pooja
 */
public interface InvoiceAuditDao extends GenericDao<InvoiceAudit, String> {

	/**
	 * @param invoiceId
	 * @return
	 */
	List<InvoiceAudit> getInvoiceAuditByInvoiceIdForSupplier(String invoiceId);

	/**
	 * @param invoiceId
	 * @return
	 */
	List<InvoiceAudit> getInvoiceAuditByInvoiceIdForBuyer(String invoiceId);

}
