package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.OnboardedSupplier;

/**
 * @author pooja
 */
public interface OnboardedSupplierDao extends GenericDao<OnboardedSupplier, String> {
	/**
	 * @param supplierId
	 * @return
	 */
	long findOnboardedSupplierForFinancingRequest(String supplierId);

	/**
	 * @param supplierId
	 * @return
	 */
	OnboardedSupplier getOnboardedSupplierBySupplierId(String supplierId);

}
