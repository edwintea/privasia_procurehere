package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PoSharingBuyerDao;
import com.privasia.procurehere.core.dao.SupplierAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.PoSharingBuyer;
import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.SupplierSettingsService;

@Service
@Transactional(readOnly = true)
public class SupplierSettingsServiceImpl implements SupplierSettingsService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	@Autowired
	PoSharingBuyerDao poSharingBuyerDao;

	@Override
	public SupplierSettings getSupplierSettingsByTenantId(String tenantId) {
		return supplierSettingsDao.getSupplierSettingsByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierSettings updateSettings(SupplierSettings supplierSettings) {
		supplierSettings = supplierSettingsDao.update(supplierSettings);
		SupplierAuditTrail ownerAuditTrail = new SupplierAuditTrail(AuditTypes.UPDATE, " Supplier settings updated ", supplierSettings.getModifiedBy().getTenantId(), supplierSettings.getModifiedBy(), new Date(), ModuleType.SupplierSettings);
		supplierAuditTrailDao.save(ownerAuditTrail);
		if (supplierSettings != null && supplierSettings.getFinanceCompany() != null) {
			supplierSettings.getFinanceCompany().getCompanyName();
		}
		return supplierSettings;
	}

	@Override
	public String getSupplierTimeZoneByTenantId(String tenantId) {
		return supplierSettingsDao.getSupplierTimeZoneByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierSettings saveSettings(SupplierSettings supplierSettings) {
		return supplierSettingsDao.save(supplierSettings);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSupplierSettingsWithAudit(SupplierSettings supplierSettings, User user, String msg) {
		SupplierAuditTrail ownerAuditTrail = new SupplierAuditTrail(AuditTypes.REQUEST, msg, user.getTenantId(), user, new Date(), ModuleType.SupplierSettings);
		supplierAuditTrailDao.save(ownerAuditTrail);
		supplierSettingsDao.update(supplierSettings);

	}

	@Override
	public SupplierSettings getSupplierSettingsForFinanceByTenantId(String tenantId) {
		return supplierSettingsDao.getSupplierSettingsForFinanceByTenantId(tenantId);
	}

	@Override
	public SupplierSettings getSupplierSettingsByTenantIdWithFinance(String tenantId) {
		return supplierSettingsDao.getSupplierSettingsByTenantIdWithFinance(tenantId);
	}

	@Override
	public List<Buyer> getBuyersForPoSharing(String supplierId) {
		return supplierSettingsDao.getBuyersForPoSharing(supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public PoSharingBuyer saveSharingBuyer(PoSharingBuyer poSharingBuyer) {
		return poSharingBuyerDao.save(poSharingBuyer);
	}

	@Override
	public List<PoShareBuyerPojo> getPoSharingBuyers(String tenantId) {
		return supplierSettingsDao.getPoSharingBuyers(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSharingBuyer(PoSharingBuyer poSharingBuyer) {
		poSharingBuyerDao.delete(poSharingBuyer);
	}

	@Override
	public List<PoSharingBuyer> getPoSharingBuyersbySupplierId(String loggedInUserTenantId, String sid) {
		return poSharingBuyerDao.getPoSharingBuyersbySupplierId(loggedInUserTenantId, sid);
	}
}
