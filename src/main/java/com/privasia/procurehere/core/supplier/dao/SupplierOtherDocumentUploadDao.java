package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierOtherDocuments;

/**
 * @author Priyanka Singh
 */

public interface SupplierOtherDocumentUploadDao extends GenericDao<SupplierOtherDocuments, String> {

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierOtherDocuments> findAllOtherDocumentBySupplierId(String supplierId);

	/**
	 * @param id
	 */
	void deleteById(String id);

	SupplierOtherDocuments findOtherDocumentById(String id);

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierOtherDocuments> findAllOtherDocumentBySupplierIdWithData(String supplierId);

	/**
	 * @param id
	 * @return
	 */
	long findTotalOtherDocumentsBySupplierId(String id);

}
