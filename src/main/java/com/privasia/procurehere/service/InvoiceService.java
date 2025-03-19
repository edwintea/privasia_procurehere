package com.privasia.procurehere.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author pooja
 */
public interface InvoiceService {
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
	 * @param loggedInUser
	 * @param po
	 * @return
	 * @throws ApplicationException
	 * @throws EmailException
	 */
	Invoice createInvoice(User loggedInUser, Po po) throws ApplicationException, EmailException;

	/**
	 * @param invoiceId
	 * @return
	 */
	Invoice getInvoiceByIdForSupplierView(String invoiceId);

	/**
	 * @param invoiceId
	 * @return
	 */
	List<InvoiceItem> findAllInvoiceItemByInvoiceIdForSummary(String invoiceId);

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
	 * @param invoiceId
	 * @param loggedInUser
	 * @param buyerRemark
	 * @return
	 * @throws EmailException
	 */
	Invoice declineInvoice(String invoiceId, User loggedInUser, String buyerRemark) throws EmailException;

	/**
	 * @param invoiceId
	 * @param loggedInUser
	 * @param buyerRemark
	 * @return
	 * @throws EmailException
	 * @throws ApplicationException
	 */
	Invoice acceptInvoice(String invoiceId, User loggedInUser, String buyerRemark) throws EmailException, ApplicationException;

	/**
	 * @param invoiceId
	 * @param loggedInUser
	 * @return
	 */
	Invoice finishInvoice(String invoiceId, User loggedInUser);

	/**
	 * @param invoiceId
	 * @param loggedInUser
	 * @param supplierRemark
	 * @return
	 * @throws EmailException
	 */
	Invoice cancelInvoice(String invoiceId, User loggedInUser, String supplierRemark) throws EmailException;

	/**
	 * @param invoiceId
	 * @return
	 */
	List<InvoiceItem> findAllInvoiceItemByInvoiceId(String invoiceId);

	/**
	 * @param invoiceId
	 * @return
	 */
	Invoice findByInvoiceId(String invoiceId);

	/**
	 * @param tenantId
	 * @param invoiceIds
	 * @param invoiceSupplierPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param sdf
	 * @return
	 */
	List<InvoiceSupplierPojo> getAllInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	/**
	 * @param tenantId
	 * @param invoiceIds
	 * @param invoiceSupplierPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param sdf
	 * @return
	 */
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
	 * @param poId
	 * @return
	 */
	long findTotalBuyerInvoiceForPo(String poId);

	/**
	 * @param response
	 * @param file
	 * @param invIds
	 * @param startDate
	 * @param endDate
	 * @param invoiceSupplierPojo
	 * @param select_all
	 * @param tenantId
	 */
	void downloadCsvFileForInvoice(HttpServletResponse response, File file, String[] invIds, Date startDate, Date endDate, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, String tenantId, HttpSession session);

	/**
	 * @param response
	 * @param file
	 * @param invIds
	 * @param startDate
	 * @param endDate
	 * @param invoiceSupplierPojo
	 * @param select_all
	 * @param tenantId
	 */
	void downloadCsvFileForSupplierInvoices(HttpServletResponse response, File file, String[] invIds, Date startDate, Date endDate, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, String tenantId,HttpSession session);

	/**
	 * @param response
	 * @param invoice
	 */
	void generateInvoiceReport(HttpServletResponse response, Invoice invoice);

	/**
	 * @param invoiceObj
	 * @return 
	 */
	JasperPrint saveInvoicePdf(Invoice invoiceObj);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalBuyerInvoiceByPoId(String poId);

}