package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.InvoiceFinanceRequest;
import com.privasia.procurehere.core.pojo.InvoiceFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author nitin
 */
public interface InvoiceFinanceRequestDao extends GenericDao<InvoiceFinanceRequest, String> {

	/**
	 * @param invoiceId
	 * @return
	 */
	InvoiceFinanceRequest findInvoiceFinanceRequestByInvoiceId(String invoiceId);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredInvoiceFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalInvoiceFinanceRequestsForBuyer(String tenantId);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredInvoiceFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalInvoiceFinanceRequestsForSupplier(String tenantId);

}
