package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;

public interface SupplierFinancialDocumentsDao extends GenericDao<SupplierFinanicalDocuments, String> {

	List<SupplierFinanicalDocuments> findAllDocumentsBySupplierId(String id);

	void deleteById(String id);

	long findTotalFinancialDocumentsBySuppId(String id);

}
