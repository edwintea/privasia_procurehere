package com.privasia.procurehere.core.dao;

import java.util.Date;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.User;

public interface FinanceSettingsDao extends GenericDao<FinanceCompanySettings, String> {

	FinanceCompanySettings getFinanceSettingsByTenantId(String tenantId);

	/**
	 * @param finance
	 * @param createdDate
	 * @param createdBy
	 */
	void createDefaultFinanceSettings(FinanceCompany company, Date createdDate, User createdBy);

	/**
	 * @param tenantId
	 * @return
	 */
	String getFinanceTimeZoneByTenantId(String tenantId);
}
