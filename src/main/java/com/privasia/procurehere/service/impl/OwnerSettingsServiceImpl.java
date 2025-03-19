package com.privasia.procurehere.service.impl;

import java.util.Date;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.service.OwnerSettingsService;

@Service
@Transactional(readOnly = true)
public class OwnerSettingsServiceImpl implements OwnerSettingsService {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(OwnerSettings.class);

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Override
	public OwnerSettings getOwnerSettingsByTenantId(String tenantId) {
		return ownerSettingsDao.getOwnersettingsByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public OwnerSettings updateSettings(OwnerSettings ownerSettings) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'"+ownerSettings.getTimeZone()+"' Owner Settings updated ", ownerSettings.getModifiedBy().getTenantId(), ownerSettings.getModifiedBy(), new Date(),ModuleType.OwnerSettings);
		ownerAuditTrailDao.save(ownerAuditTrail);
		return ownerSettingsDao.update(ownerSettings);
	}

	@Override
	public OwnerSettings getOwnersettings() {
		return ownerSettingsDao.getOwnersettings();
	}

	@Override
	public OwnerSettings getPlainOwnersettings() {
		return ownerSettingsDao.getPlainOwnersettings();
	}

	@Override
	public String getBuyerSubsMailFromOwnersettings() {
		return ownerSettingsDao.getBuyerSubsMailFromOwnersettings();
	}
	
}
