package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface BuyerSubscriptionDao extends GenericDao<BuyerSubscription, String> {
	/**
	 * @param subscriptionId
	 * @return
	 */
	BuyerSubscription getBuyerSubscriptionById(String subscriptionId);

	/**
	 * @param buyerId
	 * @return
	 */
	PlanType getBuyerSubscriptionPlanTypeByBuyerID(String buyerId);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<BuyerSubscription> findBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalBuyerSubscriptionHistoryForBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	BuyerSubscription getCurrentBuyerSubscriptionForBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	BuyerSubscription getLastBuyerSubscriptionForBuyer(String tenantId);

	/**
	 * @param profileId
	 * @return
	 */
	BuyerSubscription getActiveBuyerSubscriptionByPaymentProfileId(String profileId);

	/**
	 * @param id
	 * @return
	 */
	BuyerSubscription getBuyerSubscriptionBySubscriptionId(String id);

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	List<BuyerSubscription> getAllBuyerSubscriptionForBuyerForExcel(String tenantId);

	List<BuyerSubscription> getNonActiveBuyerSubscriptionList(String tenantId);

}
