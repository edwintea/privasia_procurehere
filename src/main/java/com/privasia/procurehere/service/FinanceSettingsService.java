package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.User;

public interface FinanceSettingsService {

	String saveFinanceSettings(FinanceCompanySettings financeSettings, User loggedInUser);

	void updateFinanceSettings(FinanceCompanySettings financeSettings, User loggedInUser);

	void deleteFinanceSettings(FinanceCompanySettings financeSettings);

	FinanceCompanySettings getFinanceSettingsById(String id);

	FinanceCompanySettings getFinanceSettingsByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	String getFinanceTimeZoneByTenantId(String tenantId);

	/**
	 * @param financeCompanySettings
	 */
	void updateFinanceSettingsSeqNumber(FinanceCompanySettings financeCompanySettings);

}
