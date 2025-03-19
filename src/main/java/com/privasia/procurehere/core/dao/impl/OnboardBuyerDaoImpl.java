package com.privasia.procurehere.core.dao.impl;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.OnboardedBuyerDao;
import com.privasia.procurehere.core.entity.OnboardedBuyer;

@Repository("onboardedBuyerDao")
public class OnboardBuyerDaoImpl extends GenericDaoImpl<OnboardedBuyer, String> implements OnboardedBuyerDao {

	private static final Logger LOG = LogManager.getLogger(OnboardBuyerDaoImpl.class);

	@Override
	public long findOnboardedBuyerForInvoiceRequest(String buyerId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from OnboardedBuyer i where i.buyer.id = :buyerId and i.active = true");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", buyerId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public OnboardedBuyer getOnboardedBuyerByBuyerId(String buyerId) {
		StringBuilder hsql = new StringBuilder("select i from OnboardedBuyer i where i.buyer.id = :buyerId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", buyerId);
		return ((OnboardedBuyer) query.getSingleResult());
	}
	
}

