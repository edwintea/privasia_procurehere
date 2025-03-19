package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository("supplierSettingsDao")
public class SupplierSettingsDaoImpl extends GenericDaoImpl<SupplierSettings, String> implements SupplierSettingsDao {

	@SuppressWarnings("unchecked")
	@Override
	public SupplierSettings getSupplierSettingsByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from SupplierSettings ss left outer join fetch ss.timeZone as tz inner join fetch ss.supplier s left outer join fetch ss.modifiedBy as mb  left outer join fetch ss.requestedBy as rrb where s.id = :id");
		query.setParameter("id", tenantId);
		List<SupplierSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSupplierTimeZoneByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("select tz.timeZone from SupplierSettings ss left outer join ss.timeZone as tz  where ss.supplier.id = :id");
		query.setParameter("id", tenantId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierSettings getSupplierSettingsForFinanceByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from SupplierSettings ss left outer join fetch ss.financeCompany as fc   left outer join fetch ss.timeZone as tz  inner join fetch ss.supplier s left outer join fetch ss.modifiedBy as mb where s.id = :id");
		query.setParameter("id", tenantId);
		List<SupplierSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierSettings getSupplierSettingsByTenantIdWithFinance(String tenantId) {
		final Query query = getEntityManager().createQuery("from SupplierSettings ss left outer join fetch ss.timeZone as tz left outer join fetch ss.financeCompany as fc inner join fetch ss.supplier s left outer join fetch ss.modifiedBy as mb where s.id = :id");
		query.setParameter("id", tenantId);
		List<SupplierSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> getBuyersForPoSharing(String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct b from Po po  left outer join po.buyer as b left outer join po.supplier as fs  left outer join fs.supplier as s where s.id = :supplierId");
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoShareBuyerPojo> getPoSharingBuyers(String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.PoShareBuyerPojo(psb.id,fc.companyName,b.companyName,b.id) from PoSharingBuyer psb  left outer join  psb.buyer as b left outer join psb.supplier as s left outer join  psb.financeCompany as fc where s.id = :tenantId");
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}
}
