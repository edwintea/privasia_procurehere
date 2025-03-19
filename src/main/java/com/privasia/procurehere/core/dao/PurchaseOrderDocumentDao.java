package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PurchaseOrderDocument;

/**
 * @author ravi
 */
public interface PurchaseOrderDocumentDao extends GenericDao<PurchaseOrderDocument, String> {

	/**
	 * @param poId
	 * @return
	 */
	List<PurchaseOrderDocument> findAllPoDocsbyPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PurchaseOrderDocument> findAllPlainPoDocsbyPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PurchaseOrderDocument> findAllPoDocsNameAndId(String poId);

	/**
	 * @param docId
	 * @param poId TODO
	 */
	void deleteDocument(String docId, String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PurchaseOrderDocument> findAllPlainPoDocsbyPoIdForSupplier(String poId);

}
