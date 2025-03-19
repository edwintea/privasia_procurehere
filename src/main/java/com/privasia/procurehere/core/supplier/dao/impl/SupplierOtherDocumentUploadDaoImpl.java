package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierOtherDocuments;
import com.privasia.procurehere.core.supplier.dao.SupplierOtherDocumentUploadDao;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Priyanka Singh
 */

@Repository
public class SupplierOtherDocumentUploadDaoImpl extends GenericDaoImpl<SupplierOtherDocuments, String> implements SupplierOtherDocumentUploadDao {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@SuppressWarnings("unchecked")
	public List<SupplierOtherDocuments> findAllOtherDocumentBySupplierId(String supplierId) {
		LOG.info("Suuplier Id : " + supplierId);
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierOtherDocuments(a.id, a.fileName, a.description, a.uploadDate, a.expiryDate, a.contentType, ub) from SupplierOtherDocuments a left outer join a.uploadBy ub where a.supplier.id =:id");
		query.setParameter("id", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SupplierOtherDocuments> findAllOtherDocumentBySupplierIdWithData(String supplierId) {
		LOG.info("Suuplier Id : " + supplierId);
		final Query query = getEntityManager().createQuery("select a from SupplierOtherDocuments a left outer join a.uploadBy ub where a.supplier.id =:id");
		query.setParameter("id", supplierId);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from SupplierOtherDocuments a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public SupplierOtherDocuments findOtherDocumentById(String id) {
		final Query query = getEntityManager().createQuery("from SupplierOtherDocuments a  where a.id =:id");
		query.setParameter("id", id);
		return (SupplierOtherDocuments) query.getSingleResult();
	}

	@Override
	public long findTotalOtherDocumentsBySupplierId(String supplierId) {
		StringBuilder hsql = new StringBuilder("select  count( distinct sp.id)  from SupplierOtherDocuments sp where sp.supplier.id= :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).longValue();
	}

}
