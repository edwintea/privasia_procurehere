package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Nitin Otageri
 */
@Repository
public class SupplierSubscriptionDaoImpl extends GenericDaoImpl<SupplierSubscription, String> implements SupplierSubscriptionDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(SupplierSubscriptionDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public SupplierSubscription getSubscriptionById(String subscriptionId) {
		final Query query = getEntityManager().createQuery("from SupplierSubscription s left outer join fetch s.supplier su inner join fetch s.supplierPlan p inner join fetch p.currency c where s.id = :subscriptionId ");
		query.setParameter("subscriptionId", subscriptionId);
		List<SupplierSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierSubscription getCurrentSubscriptionForSupplier(String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct(s) from SupplierSubscription s left outer join fetch s.supplier su left outer join fetch s.supplierPlan p left outer join fetch s.paymentTransaction pt left outer join fetch p.currency c where su.id = :supplierId order by s.createdDate desc");
		query.setParameter("supplierId", supplierId);
		List<SupplierSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean isPlanInUse(String planId) {
		final Query query = getEntityManager().createQuery("select count(s) from SupplierSubscription s inner join s.supplierPlan p where p.id = :planId ");
		query.setParameter("planId", planId);
		return ((Number) query.getSingleResult()).longValue() > 0 ? true : false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Supplier getSupplierWithSupplierPackagePlanByTenantId(String loggedInUserTenantId) {
		final Query query = getEntityManager().createQuery("from Supplier s left outer join fetch s.associatedBuyers ab left outer join fetch s.supplierPackage spk  left outer join fetch spk.supplierPlan sp left outer join fetch sp.currency spc left outer join fetch s.registrationOfCountry src where s.id = :tenantId");
		query.setParameter("tenantId", loggedInUserTenantId);
		List<Supplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUsedPromoCodeBySupplierId(String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct(s.promoCode) from SupplierSubscription s left outer join s.supplier sup where sup.id = :supplierId ");
		query.setParameter("supplierId", supplierId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierSubscription> getSupplierSubscriptionValidityBySupplierId(String supplierId) {
		final Query query = getEntityManager().createQuery("select s from SupplierSubscription s left outer join  s.supplier sup left outer join fetch s.supplierPlan where sup.id = :supplierId and s.subscriptionStatus is not null order by s.startDate desc");
		query.setParameter("supplierId", supplierId);
//		query.setParameter("status", SubscriptionStatus.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierSubscription> getSupplierFutureSubscriptionByCreatedDate(String supplierId) {
		final Query query = getEntityManager().createQuery("select s from SupplierSubscription s left outer join  s.supplier sup left outer join fetch s.supplierPlan where sup.id = :supplierId and s.subscriptionStatus = :status order by s.startDate");
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", SubscriptionStatus.FUTURE);
		return query.getResultList();
	}

}
