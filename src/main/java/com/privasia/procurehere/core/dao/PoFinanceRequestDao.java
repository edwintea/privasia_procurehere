package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.PoFinanceRequest;
import com.privasia.procurehere.core.entity.PoFinanceRequestDocuments;
import com.privasia.procurehere.core.pojo.PoFinanceRequestDocumentsPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author nitin
 */
public interface PoFinanceRequestDao extends GenericDao<PoFinanceRequest, String> {

	/**
	 * @param invoiceId
	 * @return
	 */
	PoFinanceRequest findPoFinanceRequestByPoId(String invoiceId);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<PoFinanceRequestPojo> findAllPoFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredPoFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalPoFinanceRequestsForBuyer(String tenantId);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<PoFinanceRequestPojo> findAllPoFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param input
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredPoFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalPoFinanceRequestsForSupplier(String tenantId);

	/**
	 * @param poId
	 * @return
	 */
	PoFinanceRequestPojo getPoFinanceRequestPojoByPoId(String poId);

	/**
	 * @param requestId
	 * @return
	 */
	List<PoFinanceRequestDocumentsPojo> getPoFinanceRequestDocumentsForRequestForSupplier(String requestId);

	/**
	 * @param documentId
	 * @return
	 */
	PoFinanceRequestDocuments getPoFinanceRequestDocumentById(String documentId);

}
