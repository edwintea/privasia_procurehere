package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.InvoiceReport;

/**
 * @author sana
 */
public interface InvoiceReportDao extends GenericDao<InvoiceReport, String> {

	/**
	 * @param id
	 * @return
	 */
	InvoiceReport findReportByInvoiceId(String id);

}
