package com.privasia.procurehere.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerSettingsService;

@Service
@Transactional(readOnly = true)
public class BuyerSettingsServiceImpl implements BuyerSettingsService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public String saveBuyerSettings(BuyerSettings buyerSettings, User user) {
		try {
			LOG.info("BuyerSettings " + buyerSettings.getId());
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + buyerSettings.getTimeZone() + " General Settings updated ", user.getTenantId(), user, new Date(), ModuleType.BuyerSettings);
			buyerAuditTrailDao.save(ownerAuditTrail);
			buyerSettings = buyerSettingsDao.saveOrUpdate(buyerSettings);
		} catch (Exception e) {
			LOG.error("ERROR while Buyer Setting storing .. " + e.getMessage(), e);
		}
		return (buyerSettings != null ? buyerSettings.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public BuyerSettings updateBuyerSettings(BuyerSettings buyerSettings, User user) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, " General Settings updated ", user.getTenantId(), user, new Date(), ModuleType.BuyerSettings);
		buyerAuditTrailDao.save(ownerAuditTrail);
		return buyerSettingsDao.update(buyerSettings);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBuyerSettings(BuyerSettings buyerSettings) {
		TimeZone buyerTimezone = buyerSettings.getTimeZone();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + buyerTimezone + " General settings updated ", buyerSettings.getModifiedBy().getTenantId(), buyerSettings.getModifiedBy(), new Date(), ModuleType.BuyerSettings);
		buyerAuditTrailDao.save(ownerAuditTrail);
		buyerSettingsDao.delete(buyerSettings);

	}

	@Override
	public BuyerSettings getBuyerSettingsById(String id) {
		return buyerSettingsDao.findById(id);
	}

	@Override
	public BuyerSettings getBuyerSettingsByTenantId(String tenantId) {
		return buyerSettingsDao.getBuyerSettingsByTenantId(tenantId);
	}

	@Override
	public String getBuyerTimeZoneByTenantId(String tenantId) {
		return buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBuyerSettingsWithAudit(BuyerSettings buyerSettings, User user, String Auditmsg) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.REQUEST, Auditmsg, user.getTenantId(), user, new Date(), ModuleType.BuyerSettings);
		buyerAuditTrailDao.save(ownerAuditTrail);
		buyerSettingsDao.update(buyerSettings);

	}

	@Override
	public BuyerSettings getTenantIdBybuyerKey(String buyerKey) {
		return buyerSettingsDao.getTenantIdBybuyerKey(buyerKey);
	}

	@Override
	@Transactional(readOnly = false)
	public BuyerAuditTrail updateBuyerSettingsForPublishedProfile(User user, String description) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, description, user.getTenantId(), user, new Date(), ModuleType.PublishedProfile);
		buyerAuditTrailDao.save(ownerAuditTrail);
		return buyerAuditTrailDao.save(ownerAuditTrail);
	}

	@Override
	public Boolean isAutoPublishePoSettingsByTenantId(String tenantId) {
		return buyerSettingsDao.isAutoPublishePoSettingsByTenantId(tenantId);
	}

	@Override
	public Boolean isAutoCreatePoSettingsByTenantId(String tenantId) {
		return buyerSettingsDao.isAutoCreatePoSettingsByTenantId(tenantId);
	}

	@Override
	public Boolean isEnableUnitAndCostCorrelationByTenantId(String tenantId) {
		return buyerSettingsDao.isEnableUnitAndCostCorrelationByTenantId(tenantId);
	}
	
	@Override
	public Boolean isEnableUnitAndGroupCodeCorrelationByTenantId(String tenantId) {
		return buyerSettingsDao.isEnableUnitAndGroupCodeCorrelationByTenantId(tenantId);
	}

}
