package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;
import com.privasia.procurehere.core.supplier.dao.SupplierProfileUploadDao;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Giridhar
 */

@Repository("supplierProfileUploadDao")
public class SupplierProfileUploadDaoImpl extends GenericDaoImpl<String, SupplierCompanyProfile>
		implements SupplierProfileUploadDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierCompanyProfile> findCompanyProfileAll() {
		final Query query = getEntityManager().createQuery("from SupplierCompanyProfile a ");
		return query.getResultList();
	}

	@Override
	public SupplierCompanyProfile findCompanyProfileById(String id) {
		final Query query = getEntityManager().createQuery("from SupplierCompanyProfile a  where a.id =:id");
		query.setParameter("id", id);
		return (SupplierCompanyProfile) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<SupplierCompanyProfile> findAllCompanyProfileBySupplierId(String supplierId) {
		final Query query = getEntityManager()
				.createQuery("from SupplierCompanyProfile a left outer join fetch a.supplier as sp where sp.id =:id");
		query.setParameter("id", supplierId);
		return query.getResultList();
	}

}
