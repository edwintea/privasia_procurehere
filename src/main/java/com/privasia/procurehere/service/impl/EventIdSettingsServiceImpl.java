package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.EventIdSettingsService;

@Service
@Transactional(readOnly = true)
public class EventIdSettingsServiceImpl implements EventIdSettingsService {

	private static final Logger LOG = LogManager.getLogger(EventIdSettingsServiceImpl.class);

	@Autowired(required = true)
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	BusinessUnitDao businessUnitDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public List<IdSettings> getAllSettings(String loggedInUserTenantId) {
		LOG.info("eventIdSettingsServiceImpl getAllSettings(String loggedInUserTenantId) called");
		return eventIdSettingsDao.getAllSettings(loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public IdSettings createIdSettings(IdSettings idSettings) {
		LOG.info("idSettings : " + idSettings.toLogString());
		idSettings.setIdSequence(idSettings.getIdSequence());
		idSettings.setIdPerfix(idSettings.getIdPerfix());
		idSettings.setIdDelimiter(idSettings.getIdDelimiter());
		idSettings.setIdType(idSettings.getIdType());
		idSettings.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
		idSettings.setIdDatePattern(idSettings.getIdDatePattern());

		idSettings = eventIdSettingsDao.saveOrUpdate(idSettings);

		return idSettings;
	}

	@Override
	public List<IdSettings> findAllIdSettingsList(String tenantId, TableDataInput input) {
		LOG.info("findAllIdSettingsList(tenantId,input)");
		return eventIdSettingsDao.findAllIdSettingsList(tenantId, input);
	}

	@Override
	public long findTotalFilteredCountryList(String tenantId, TableDataInput input) {
		LOG.info("findTotalFilteredCountryList(tenantId,input)");
		return eventIdSettingsDao.findTotalFilteredCountryList(tenantId, input);
	}

	@Override
	public long findTotalIdSetList(String loggedInUserTenantId) {
		LOG.info("findTotalIdSetList(loggedInUserTenantId)");
		return eventIdSettingsDao.findTotalIdSetList(loggedInUserTenantId);
	}

	@Override
	public IdSettings getIdSettingsById(String id) {
		LOG.info("findTotalIdSetList(getIdSettingsById(id))");
		return eventIdSettingsDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public IdSettings updateIdSettings(IdSettings persistObj) {
		LOG.info("Id Setting Updated " + persistObj.toLogString());

		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + persistObj.getIdType() + "' Id Setting Updated", persistObj.getTenantId(), persistObj.getModifiedBy(), new Date(), ModuleType.IdSetting);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while updating Id settings " + e.getMessage(), e);
		}

		return eventIdSettingsDao.update(persistObj);
	}

	@Override
	public boolean isExists(IdSettings idSettings, String tenantId) {
		return eventIdSettingsDao.isExists(idSettings, tenantId);
	}

	@Override
	public Boolean isEmptyUnitCode(String tenantId) {
		return businessUnitDao.isEmptyUnitCode(tenantId);
	}

	@Override
	public boolean isRequiredCode(String tenantId) {
		// TODO Auto-generated method stub
		return eventIdSettingsDao.isRequiredCode(tenantId);
	}

	@Override
	public Boolean isBusinessSettingEnable(String tenantId, String type) {
		return eventIdSettingsDao.isBusinessSettingEnable(tenantId, type);
	}

	@Override
	@Transactional(readOnly = false)
	public String generateEventIdByBusinessUnit(String tenantId, String string, BusinessUnit businessUnit) throws ApplicationException {
		return eventIdSettingsDao.generateEventIdByBusinessUnit(tenantId, string, businessUnit);
	}

	@Override
	public IdSettings getIdSettingsByIdTypeForTenanatId(String tenantId, String idType) {
		return eventIdSettingsDao.getIdSettingsByIdTypeForTenanatId(tenantId, idType);
	}

}