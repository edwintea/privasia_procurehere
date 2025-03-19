package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SubscriptionDao;
import com.privasia.procurehere.core.entity.Subscription;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Nitin Otageri
 */
@Repository
public class SubscriptionDaoImpl extends GenericDaoImpl<Subscription, String> implements SubscriptionDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(SubscriptionDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public Subscription getSubscriptionById(String subscriptionId) {
		final Query query = getEntityManager().createQuery("from Subscription s left outer join fetch s.buyer b inner join fetch s.plan p inner join fetch p.currency c where s.id = :subscriptionId ");
		query.setParameter("subscriptionId", subscriptionId);
		List<Subscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Subscription getCurrentSubscriptionForBuyer(String buyerId) {
		final Query query = getEntityManager().createQuery("from Subscription s left outer join fetch s.buyer b left outer join fetch s.nextSubscription nxt left outer join fetch nxt.plan nxtp left outer join fetch nxt.paymentTransaction nxtpt inner join fetch s.plan p left outer join fetch s.paymentTransaction pt left outer join fetch p.currency c left outer join fetch nxtp.currency nxtc where b.id = :buyerId ");
		query.setParameter("buyerId", buyerId);
		List<Subscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean isPlanInUse(String planId) {
		final Query query = getEntityManager().createQuery("select count(s) from Subscription s inner join s.plan p where p.id = :planId ");
		query.setParameter("planId", planId);
		return ((Number)query.getSingleResult()).longValue() > 0 ? true : false;
	}

}
