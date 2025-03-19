package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.PoSharingBuyer;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;

public interface SupplierSettingsService {

	SupplierSettings getSupplierSettingsByTenantId(String tenantId);

	SupplierSettings updateSettings(SupplierSettings supplierSettings);

	/**
	 * @param tenantId
	 * @return
	 */
	String getSupplierTimeZoneByTenantId(String tenantId);

	SupplierSettings saveSettings(SupplierSettings supplierSettings);

	void updateSupplierSettingsWithAudit(SupplierSettings supplierSettings, User loggedInUser, String string);

	SupplierSettings getSupplierSettingsForFinanceByTenantId(String id);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	SupplierSettings getSupplierSettingsByTenantIdWithFinance(String loggedInUserTenantId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<Buyer> getBuyersForPoSharing(String supplierId);

	PoSharingBuyer saveSharingBuyer(PoSharingBuyer poSharingBuyer);

	List<PoShareBuyerPojo> getPoSharingBuyers(String loggedInUserTenantId);

	void deleteSharingBuyer(PoSharingBuyer poSharingBuye);

	List<PoSharingBuyer> getPoSharingBuyersbySupplierId(String loggedInUserTenantId, String id);
}
