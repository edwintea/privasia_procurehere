package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierTagsDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.SupplierTagsService;

@Service
@Transactional(readOnly = true)
public class SupplierTagsServiceImpl implements SupplierTagsService {

	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	SupplierTagsDao supplierTagsDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;
	
	@Override
	@Transactional(readOnly = false)
	public void saveSupplierTags(SupplierTags supplierTags) {
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Supplier Tags '"+supplierTags.getSupplierTags()+ "' created", supplierTags.getCreatedBy().getTenantId(), supplierTags.getCreatedBy(), new Date(), ModuleType.SupplierTags);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		supplierTagsDao.saveOrUpdate(supplierTags);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSupplierTags(SupplierTags supplierTags) {
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Supplier Tags '"+supplierTags.getSupplierTags()+ "' updated", supplierTags.getModifiedBy().getTenantId(), supplierTags.getModifiedBy(), new Date(), ModuleType.SupplierTags);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		supplierTagsDao.update(supplierTags);
	}

	@Override
	public SupplierTags getSupplierTagsById(String id) {
		SupplierTags supplierTags = supplierTagsDao.findById(id);

		if (supplierTags != null && supplierTags.getBuyer() != null)
			supplierTags.getBuyer().getFullName();

		return supplierTags;
	}

	@Override
	public boolean isExists(SupplierTags supplierTags, String tenantId) {
		return supplierTagsDao.isExists(supplierTags, tenantId);
	}

	@Override
	public List<SupplierTags> findSupplierTagsForTenant(String tenantId, TableDataInput tableParams) {
		return supplierTagsDao.findSupplierTagsForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalFilteredSupplierTagsForTenant(String tenantId, TableDataInput tableParams) {
		return supplierTagsDao.findTotalFilteredSupplierTagsForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalSupplierTagsForTenant(String tenantId) {
		return supplierTagsDao.findTotalSupplierTagsForTenant(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplierTags(SupplierTags supplierTags) {
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "Supplier Tags '"+supplierTags.getSupplierTags()+"' deleted", supplierTags.getModifiedBy().getTenantId(), supplierTags.getModifiedBy(), new Date(), ModuleType.SupplierTags);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		supplierTagsDao.delete(supplierTags);
	}

	@Override
	public List<SupplierTags> searchAllActiveSupplierTagsForTenant(String loggedInUserTenantId) {
		return supplierTagsDao.searchAllActiveSupplierTagsForTenant(loggedInUserTenantId);
	}

	@Override
	public List<SupplierTags> getAllSupplierTagsOnlyByIds(List<String> supplierTags) {
		return supplierTagsDao.getAllSupplierTagsOnlyByIds(supplierTags);
	}

	@Override
	public SupplierTags getSuppliertagsAndTenantId(String suppliertags, String tenantId) {
		return supplierTagsDao.getSuppliertagsAndTenantId(suppliertags, tenantId);
	}

	@Override
	public SupplierTags getSuppliertagsDescriptionAndTenantId(String description, String tenantId) {
		return supplierTagsDao.getSuppliertagsDescriptionAndTenantId(description, tenantId);
	}
}
