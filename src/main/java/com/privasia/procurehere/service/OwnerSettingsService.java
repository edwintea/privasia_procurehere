package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.OwnerSettings;

public interface OwnerSettingsService {

	OwnerSettings getOwnerSettingsByTenantId(String TenantId);

	OwnerSettings updateSettings(OwnerSettings ownerSettings);

	/**
	 * @return
	 */
	OwnerSettings getOwnersettings();
	
	/**
	 * @return
	 */
	OwnerSettings getPlainOwnersettings();

	String getBuyerSubsMailFromOwnersettings();
}
