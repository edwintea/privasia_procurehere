package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.PasswordSettings;

public interface PasswordSettingService {

	PasswordSettings getPasswordSettingsByTenantId(String tenantId);

	PasswordSettings saveOrUpdate(PasswordSettings defaultSettings);

	PasswordSettings getPasswordRegex(String tenantId);

	String buildPasswordRegexMessage(String tenantId);

}
