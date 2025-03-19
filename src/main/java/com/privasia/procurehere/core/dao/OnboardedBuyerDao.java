package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.OnboardedBuyer;

/**
 * @author pooja
 */
public interface OnboardedBuyerDao extends GenericDao<OnboardedBuyer, String> {
	/**
	 * @param buyerId
	 * @return
	 */
	long findOnboardedBuyerForInvoiceRequest(String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	OnboardedBuyer getOnboardedBuyerByBuyerId(String buyerId);

}
