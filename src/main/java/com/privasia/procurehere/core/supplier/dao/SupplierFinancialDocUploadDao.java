package com.privasia.procurehere.core.supplier.dao;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;

public interface SupplierFinancialDocUploadDao extends GenericDao<SupplierFinanicalDocuments, String> {
	
	SupplierFinanicalDocuments findFinancialDocumentById(String supplierId);

}
