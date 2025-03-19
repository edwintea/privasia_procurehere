package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FavoutireSupplierAuditDao;
import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;

@Repository
public class FavoutireSupplierAuditDaoImpl extends GenericDaoImpl<FavouriteSupplierStatusAudit, String> implements FavoutireSupplierAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplierStatusAudit> findAuditTrailForTenant(String tenantId, String favSuppId) {
		String hql = "from FavouriteSupplierStatusAudit a where a.tenantId=:tenantId and a.supplier.id=:favSuppId order by a.actionDate asc";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("favSuppId", favSuppId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

}
