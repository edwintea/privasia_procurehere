package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierSubscription;

/**
 * @author Nitin Otageri
 */
public interface SupplierSubscriptionDao extends GenericDao<SupplierSubscription, String> {

	/**
	 * @param subscriptionId
	 * @return
	 */
	SupplierSubscription getSubscriptionById(String subscriptionId);

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierSubscription getCurrentSubscriptionForSupplier(String supplierId);

	/**
	 * @param planId
	 * @return
	 */
	boolean isPlanInUse(String planId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	Supplier getSupplierWithSupplierPackagePlanByTenantId(String loggedInUserTenantId);

	List<String> getUsedPromoCodeBySupplierId(String supplierId);

	List<SupplierSubscription> getSupplierSubscriptionValidityBySupplierId(String supplierId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierSubscription> getSupplierFutureSubscriptionByCreatedDate(String supplierId);

	/* String getActivePromoCodeBySupplierId(String supplierId); */

}
