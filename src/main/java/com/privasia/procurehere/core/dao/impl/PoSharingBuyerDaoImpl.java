package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoSharingBuyerDao;
import com.privasia.procurehere.core.entity.PoSharingBuyer;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;

@Repository("PoSharingBuyerDaoImpl")
public class PoSharingBuyerDaoImpl extends GenericDaoImpl<PoSharingBuyer, String> implements PoSharingBuyerDao {

	@Override
	public Boolean checkPoSharingToFinanceonBuyerSetting(String tenantId, String supplierId) {
		final Query query = getEntityManager().createQuery("select count(psb) from PoSharingBuyer psb left outer join psb.supplier ps left outer join psb.buyer pb where pb.id = :tenantId and ps.id= :supplierId");
		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public void clearBuyerSetting(String supplierId) {
		final Query query = getEntityManager().createQuery("delete PoSharingBuyer psb where  psb.supplier.id = :supplierId");
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoSharingBuyer> getPoSharingBuyersbySupplierId(String loggedInUserTenantId, String sid) {
		final Query query = getEntityManager().createQuery("select distinct(psb) from PoSharingBuyer psb left outer join psb.supplier ps left outer join psb.buyer pb where pb.id = :tenantId and ps.id= :supplierId");
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("supplierId", sid);

		return query.getResultList();
	}
}
