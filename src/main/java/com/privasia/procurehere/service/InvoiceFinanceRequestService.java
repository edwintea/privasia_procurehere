/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceFinanceRequest;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoFinanceRequest;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.InvoiceFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestDocumentsPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author nitin
 */
public interface InvoiceFinanceRequestService {

	/**
	 * @param invoiceFinanceRequest
	 * @return
	 */
	InvoiceFinanceRequest save(InvoiceFinanceRequest invoiceFinanceRequest);

	/**
	 * @param invoiceFinanceRequest
	 * @return
	 */
	InvoiceFinanceRequest update(InvoiceFinanceRequest invoiceFinanceRequest);

	InvoiceFinanceRequest findInvoiceFinanceRequestById(String id);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalInvoiceFinanceRequestsForBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalInvoiceFinanceRequestsForSupplier(String tenantId);

	/**
	 * @param tableParams
	 * @param tenantId
	 * @return
	 */
	long findTotalFilteredInvoiceFinanceRequestsForBuyer(TableDataInput tableParams, String tenantId);

	/**
	 * @param tableParams
	 * @param tenantId
	 * @return
	 */
	long findTotalFilteredInvoiceFinanceRequestsForSupplier(TableDataInput tableParams, String tenantId);

	/**
	 * @param tableParams
	 * @param tenantId
	 * @return
	 */
	List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForBuyer(TableDataInput tableParams, String tenantId);

	/**
	 * @param tableParams
	 * @param tenantId
	 * @return
	 */
	List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForSupplier(TableDataInput tableParams, String tenantId);

	/**
	 * @param invoiceId
	 * @return
	 */
	InvoiceFinanceRequest findInvoiceFinanceRequestByInvoiceId(String invoiceId);

	/**
	 * @param invoiceId
	 * @param actionBy
	 * @return
	 * @throws ApplicationException
	 */
	Invoice requestFinancing(String invoiceId, User actionBy) throws ApplicationException;

	/**
	 * @param invoiceId
	 * @param actionBy
	 * @return
	 * @throws ApplicationException
	 */
	InvoiceFinanceRequest acceptFinancingRequest(String invoiceId, User actionBy) throws ApplicationException;

	/**
	 * @param invoiceId
	 * @param actionBy
	 * @return
	 * @throws ApplicationException
	 */
	InvoiceFinanceRequest declineFinancingRequest(String invoiceId, User actionBy) throws ApplicationException;

	/**
	 * @param buyerId
	 * @return
	 */
	long findOnboardedBuyerForInvoiceRequest(String buyerId);

	/**
	 * @param supplierId
	 * @return
	 */
	long findOnboardedSupplierForFinancingRequest(String supplierId);

	/**
	 * @param poId
	 * @return
	 */
	PoFinanceRequest findPoFinanceRequestByPoId(String poId);

	/**
	 * @param poId
	 * @param actionBy
	 * @return
	 * @throws ApplicationException
	 */
	Po requestFinancingForPo(String poId, User actionBy) throws ApplicationException;

	/**
	 * @param poId
	 * @return
	 */
	PoFinanceRequestPojo getPoFinanceRequestPojoByPoId(String poId);

	/**
	 * @param requestId
	 * @param response
	 */
	void downloadFinansHereOfferDocument(String requestId, HttpServletResponse response);

	/**
	 * @param poId
	 * @param actionBy
	 * @return
	 * @throws ApplicationException
	 */
	PoFinanceRequest acceptFinanshereOffer(String poId, User actionBy) throws ApplicationException;

	/**
	 * @param poId
	 * @param actionBy
	 * @return
	 * @throws ApplicationException
	 */
	PoFinanceRequest declineFinanshereOffer(String poId, User actionBy) throws ApplicationException;

	/**
	 * @param requestId
	 * @return
	 */
	List<PoFinanceRequestDocumentsPojo> getPoFinanceRequestDocumentsForRequestForSupplier(String requestId);

	/**
	 * @param docId
	 * @param response
	 */
	void downloadFinansHerePoDocument(String docId, HttpServletResponse response);

}
