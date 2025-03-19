package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.OwnerSettings;

public interface OwnerSettingsDao extends GenericDao<OwnerSettings, String> {
	/**
	 * @param tenantId
	 * @return
	 */
	OwnerSettings getOwnersettingsByTenantId(String tenantId);

	/**
	 * @return
	 */
	OwnerSettings getOwnersettings();

	/**
	 * @return
	 */
	OwnerSettings getPlainOwnersettings();

	/**
	 * @return
	 */
	List<Integer> getSupplierBuyerExpiryReminder();

	String getBuyerSubsMailFromOwnersettings();

}
