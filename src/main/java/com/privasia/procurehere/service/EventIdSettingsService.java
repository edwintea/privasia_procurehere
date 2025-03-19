package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface EventIdSettingsService {

	List<IdSettings> getAllSettings(String loggedInUserTenantId);

	IdSettings createIdSettings(IdSettings idSettings);

	List<IdSettings> findAllIdSettingsList(String TenantId, TableDataInput input);

	long findTotalFilteredCountryList(String tenantId,TableDataInput input);

 
	long findTotalIdSetList(String loggedInUserTenantId);

	IdSettings getIdSettingsById(String id);

	IdSettings updateIdSettings(IdSettings persistObj);

	boolean isExists(IdSettings idSettings, String tenantId);

	Boolean isEmptyUnitCode(String loggedInUserTenantId);

	boolean isRequiredCode(String loggedInUserTenantId);

	Boolean isBusinessSettingEnable(String loggedInUserTenantId,String type);

	String generateEventIdByBusinessUnit(String tenantId, String string, BusinessUnit businessUnit) throws ApplicationException;

	/**
	 * @param tenantId
	 * @param idType
	 * @return
	 */
	IdSettings getIdSettingsByIdTypeForTenanatId(String tenantId, String idType);

	}