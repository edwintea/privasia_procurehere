package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.supplier.dao.SupplierFinancialDocumentsDao;

@Repository("SupplierFinancialDocumentsDao")
public class SupplierFinancialDocumentsDaoImpl extends GenericDaoImpl<SupplierFinanicalDocuments, String> implements SupplierFinancialDocumentsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFinanicalDocuments> findAllDocumentsBySupplierId(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.SupplierFinanicalDocuments(a.id, a.fileName, a.uploadDate, a.description) from SupplierFinanicalDocuments a inner join a.supplier sp where sp.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from SupplierFinanicalDocuments a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public long findTotalFinancialDocumentsBySuppId(String supplierId) {
		StringBuilder hsql = new StringBuilder("select  count( distinct sp.id)  from SupplierFinanicalDocuments sp where sp.supplier.id= :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).longValue();
	}

}
