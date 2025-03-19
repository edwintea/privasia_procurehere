package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierOtherCredentials;
import com.privasia.procurehere.core.supplier.dao.SupplierOtherCredentialUploadDao;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Giridhar
 */

@Repository("supplierOtherCredentialUploadDao")
public class SupplierOtherCredentialUploadDaoImpl extends GenericDaoImpl<SupplierOtherCredentials, String> implements SupplierOtherCredentialUploadDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierOtherCredentials> findOtherCredentialAll() {
		final Query query = getEntityManager().createQuery("from SupplierOtherCredentials a");
		return query.getResultList();
	}

	@Override
	public SupplierOtherCredentials findOtherCredentialById(String id) {
		final Query query = getEntityManager().createQuery("from SupplierOtherCredentials a  where a.id =:id");
		query.setParameter("id", id);
		return (SupplierOtherCredentials) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<SupplierOtherCredentials> findAllOtherCredentialBySupplierId(String supplierId) {
		final Query query = getEntityManager().createQuery("from SupplierOtherCredentials a inner join fetch a.supplier sp where sp.id =:id");
		query.setParameter("id", supplierId);
		return query.getResultList();
	}

	@Override
	public void  deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from SupplierOtherCredentials a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}
	
}
