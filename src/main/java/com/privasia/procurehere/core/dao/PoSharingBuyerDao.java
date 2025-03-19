package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PoSharingBuyer;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;

public interface PoSharingBuyerDao extends GenericDao<PoSharingBuyer, String> {

	Boolean checkPoSharingToFinanceonBuyerSetting(String tenantId, String supplierId);

	void clearBuyerSetting(String supplierId);

	List<PoSharingBuyer> getPoSharingBuyersbySupplierId(String loggedInUserTenantId, String sid);

}
