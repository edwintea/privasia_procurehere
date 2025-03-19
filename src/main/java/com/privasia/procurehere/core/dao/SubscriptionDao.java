package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.Subscription;

/**
 * @author Nitin Otageri
 */
public interface SubscriptionDao extends GenericDao<Subscription, String> {

	/**
	 * @param subscriptionId
	 * @return
	 */
	Subscription getSubscriptionById(String subscriptionId);

	/**
	 * @param buyerId
	 * @return
	 */
	Subscription getCurrentSubscriptionForBuyer(String buyerId);

	/**
	 * @param planId
	 * @return
	 */
	boolean isPlanInUse(String planId);

}
