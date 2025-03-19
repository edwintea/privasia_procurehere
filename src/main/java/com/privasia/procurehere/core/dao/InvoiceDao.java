package com.privasia.procurehere.core.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface InvoiceDao extends GenericDao<Invoice, String> {
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceSupplierPojo> findAllSearchFilterInvoiceForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterInvoiceForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalInvoiceForSupplier(String tenantId);

	/**
	 * @param invoiceId
	 * @return
	 */
	Invoice findByInvoiceId(String invoiceId);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceSupplierPojo> findAllSearchFilterInvoiceForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterInvoiceForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalInvoiceForBuyer(String tenantId);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalInvoiceForPo(String poId);

	List<InvoiceSupplierPojo> getAllInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate);

	List<InvoiceSupplierPojo> getAllBuyerInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	/**
	 * @param poId
	 * @return
	 */
	List<InvoiceSupplierPojo> getInvoicesByPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<InvoiceSupplierPojo> getInvoicesByPoIdForBuyer(String poId);

	/**
	 * 
	 * @param poId
	 * @return
	 */
	long findTotalBuyerInvoiceForPo(String poId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param invIds
	 * @param invoiceSupplierPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceSupplierPojo> findBuyerInvoicesForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] invIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param invIds
	 * @param invoiceSupplierPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceSupplierPojo> findSupplierInvoicesForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] invIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalBuyerInvoiceByPoId(String poId);

}
