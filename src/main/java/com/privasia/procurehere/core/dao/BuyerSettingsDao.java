package com.privasia.procurehere.core.dao;

import java.util.Date;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;

public interface BuyerSettingsDao extends GenericDao<BuyerSettings, String> {

	BuyerSettings getBuyerSettingsByTenantId(String tenantId);

	/**
	 * @param buyer
	 * @param createdDate
	 * @param createdBy
	 */
	void createDefaultBuyerSettings(Buyer buyer, Date createdDate, User createdBy);

	/**
	 * @param tenantId
	 * @return
	 */
	String getBuyerTimeZoneByTenantId(String tenantId);

	BuyerSettings getTenantIdBybuyerKey(String buyerKey);

	/**
	 * @param buyerSettings
	 * @param tenantId
	 */
	void updateBuyerSettingsLogo(BuyerSettings buyerSettings, String tenantId);

	/**
	 * @param tenantId
	 * @param eventType
	 * @return
	 */
	String getPublishUrlFromBuyerSettingsByTenantId(String tenantId, RfxTypes eventType);

	/**
	 * @param tenantId
	 * @param rft
	 * @return
	 */
	String getPublishUpdateUrlFromBuyerSettingsByTenantId(String tenantId, RfxTypes eventType);

	/**
	 * @param tenantId
	 * @return
	 */
	Boolean isAutoPublishePoSettingsByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
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
