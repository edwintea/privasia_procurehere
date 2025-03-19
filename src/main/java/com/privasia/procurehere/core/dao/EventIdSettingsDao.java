package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface EventIdSettingsDao extends GenericDao<IdSettings, String> {

	/**
	 * @param tenantId
	 * @param idType
	 * @return
	 */
	@Deprecated
	String generateEventId(String tenantId, String idType);

	List<IdSettings> getAllSettings(String loggedInUserTenantId);

	List<IdSettings> findAllIdSettingsList(String tenantId, TableDataInput input);

	long findTotalFilteredCountryList(String tenantId, TableDataInput input);

	long findTotalIdSetList(String loggedInUserTenantId);

	boolean isExists(IdSettings idSettings, String tenantId);

	String generateEventIdByBusinessUnit(String tenantId, String string, BusinessUnit businessUnit) throws ApplicationException;

	boolean isRequiredCode(String tenantId);

	Boolean isBusinessSettingEnable(String tenantId, String type);

	/**
	 * @param tenantId
	 * @param idType
	 * @return
	 */
	IdSettings getIdSettingsByIdTypeForTenanatId(String tenantId, String idType);

}