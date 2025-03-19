package com.privasia.procurehere.core.supplier.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.supplier.dao.SupplierFinancialDocUploadDao;

@Repository
public class SupplierFinancialDocUploadDaoImpl extends GenericDaoImpl<SupplierFinanicalDocuments, String>
		implements SupplierFinancialDocUploadDao {

	@Override
	public SupplierFinanicalDocuments findFinancialDocumentById(String id) {
		final Query query = getEntityManager().createQuery("from SupplierFinanicalDocuments a  where a.id =:id");
		query.setParameter("id", id);
		return (SupplierFinanicalDocuments) query.getSingleResult();
	}

}
