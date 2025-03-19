package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.User;

public interface BuyerSettingsService {

	String saveBuyerSettings(BuyerSettings buyerSettings, User user);

	BuyerSettings updateBuyerSettings(BuyerSettings buyerSettings, User user);

	void deleteBuyerSettings(BuyerSettings buyerSettings);

	BuyerSettings getBuyerSettingsById(String id);

	BuyerSettings getBuyerSettingsByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	String getBuyerTimeZoneByTenantId(String tenantId);

	void updateBuyerSettingsWithAudit(BuyerSettings buyerSettings, User loggedInUser, String Auditmsg);

	BuyerSettings getTenantIdBybuyerKey(String buyerKey);

	public BuyerAuditTrail updateBuyerSettingsForPublishedProfile(User user, String description);

	Boolean isAutoPublishePoSettingsByTenantId(String tenantId);

	Boolean isAutoCreatePoSettingsByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	Boolean isEnableUnitAndCostCorrelationByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	Boolean isEnableUnitAndGroupCodeCorrelationByTenantId(String tenantId);

}
