/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;
import com.privasia.procurehere.core.supplier.dao.SupplierCompanyProfileDao;

/**
 * @author Arc
 */
@Repository
public class SupplierCompanyProfileDaoImpl extends GenericDaoImpl<SupplierCompanyProfile, String> implements SupplierCompanyProfileDao {

	@Override
	public void deleteById(String id) {
		StringBuilder hsql = new StringBuilder("delete from SupplierCompanyProfile scp where scp.id= :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public long findCompanyDocumentCountBySupplierId(String supplierId) {
		StringBuilder hsql = new StringBuilder("select  count( distinct sp.id)  from SupplierCompanyProfile sp where sp.supplier.id= :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).longValue();
	}

}
