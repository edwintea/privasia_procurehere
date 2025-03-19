package com.privasia.procurehere.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FinanceAuditTrailDao;
import com.privasia.procurehere.core.dao.FinanceSettingsDao;
import com.privasia.procurehere.core.entity.FinanceAuditTrail;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.FinanceSettingsService;

@Service
@Transactional(readOnly = true)
public class FinanceSettingsServiceImpl implements FinanceSettingsService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	FinanceSettingsDao financeSettingsDao;

	@Autowired
	FinanceAuditTrailDao financeAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public String saveFinanceSettings(FinanceCompanySettings financeCompanySettings, User loggedInUser) {
		try {
			LOG.info("FinanceCompanySettings " + financeCompanySettings.getId());
			FinanceAuditTrail ownerAuditTrail = new FinanceAuditTrail(AuditTypes.CREATE, "User created Finance Company settings ", financeCompanySettings.getTenantId(), loggedInUser, new Date());
			financeAuditTrailDao.save(ownerAuditTrail);
			financeCompanySettings = financeSettingsDao.saveOrUpdate(financeCompanySettings);
		} catch (Exception e) {
			LOG.error("ERROR while Finance Setting storing .. " + e.getMessage(), e);
		}
		return (financeCompanySettings != null ? financeCompanySettings.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateFinanceSettings(FinanceCompanySettings financeCompanySettings, User loggedInUser) {
		FinanceAuditTrail ownerAuditTrail = new FinanceAuditTrail(AuditTypes.UPDATE, "User updated Finance Company settings ", financeCompanySettings.getTenantId(), loggedInUser, new Date());
		financeAuditTrailDao.save(ownerAuditTrail);
		financeSettingsDao.update(financeCompanySettings);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteFinanceSettings(FinanceCompanySettings financeCompanySettings) {
		FinanceAuditTrail ownerAuditTrail = new FinanceAuditTrail(AuditTypes.DELETE, "User deleted Finance Company settings ", financeCompanySettings.getTenantId(), financeCompanySettings.getModifiedBy(), new Date());
		financeAuditTrailDao.save(ownerAuditTrail);

		financeSettingsDao.delete(financeCompanySettings);
	}

	@Override
	public FinanceCompanySettings getFinanceSettingsById(String id) {
		return financeSettingsDao.findById(id);
	}

	@Override
	public FinanceCompanySettings getFinanceSettingsByTenantId(String tenantId) {
		return financeSettingsDao.getFinanceSettingsByTenantId(tenantId);
	}

	@Override
	public String getFinanceTimeZoneByTenantId(String tenantId) {
		return financeSettingsDao.getFinanceTimeZoneByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateFinanceSettingsSeqNumber(FinanceCompanySettings financeCompanySettings) {
		financeSettingsDao.update(financeCompanySettings);
	}
	
}
