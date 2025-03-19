package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;

public interface SupplierSettingsDao extends GenericDao<SupplierSettings, String> {

	SupplierSettings getSupplierSettingsByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	String getSupplierTimeZoneByTenantId(String tenantId);

	SupplierSettings getSupplierSettingsForFinanceByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	SupplierSettings getSupplierSettingsByTenantIdWithFinance(String tenantId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<Buyer> getBuyersForPoSharing(String supplierId);

	List<PoShareBuyerPojo> getPoSharingBuyers(String tenantId);
}
