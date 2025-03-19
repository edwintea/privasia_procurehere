package com.privasia.procurehere.core.dao.impl;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.OnboardedSupplierDao;
import com.privasia.procurehere.core.entity.OnboardedSupplier;
import com.privasia.procurehere.core.enums.SupplierOnboardingStatus;

@Repository("onboardedSupplierDao")
public class OnboardSupplierDaoImpl extends GenericDaoImpl<OnboardedSupplier, String> implements OnboardedSupplierDao {

	private static final Logger LOG = LogManager.getLogger(OnboardSupplierDaoImpl.class);

	@Override
	public long findOnboardedSupplierForFinancingRequest(String supplierId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from OnboardedSupplier i where i.supplier.id = :supplierId and i.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", SupplierOnboardingStatus.APPROVED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public OnboardedSupplier getOnboardedSupplierBySupplierId(String supplierId) {
		StringBuilder hsql = new StringBuilder("select i from OnboardedSupplier i where i.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((OnboardedSupplier) query.getSingleResult());
	}
	
}

