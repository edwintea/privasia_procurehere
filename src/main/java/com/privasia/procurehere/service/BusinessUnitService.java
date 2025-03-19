package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.BusinessUnitPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface BusinessUnitService {
	/**
	 * @param businessUnit
	 */
	void save(BusinessUnit businessUnit);

	/**
	 * @param id
	 * @return
	 */
	BusinessUnit getBusinessUnitById(String id);

	/**
	 * @param businessUnit
	 * @return
	 */
	BusinessUnit update(BusinessUnit businessUnit);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<BusinessUnit> findBusinessUnitsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredBusinessUnitsForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalBusinessUnitsForTenant(String loggedInUserTenantId);

	/**
	 * @param businessUnit
	 */
	void delete(BusinessUnit businessUnit);

	/**
	 * @param businessUnit
	 * @param tenantId TODO
	 * @return
	 */
	boolean isExists(BusinessUnit businessUnit, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<BusinessUnit> getPlainActiveBusinessUnitForTenant(String tenantId);

	/**
	 * @param id
	 * @return
	 */
	BusinessUnit getPlainBusinessUnitById(String id);

	/**
	 * @param tenantId
	 * @return
	 */
	List<BusinessUnit> getAllActiveBusinessUnitForMoblie(String tenantId);

	boolean isExistsUnitCode(String unitCode, String tenantId, String id);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 */
	void businessUnitDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId);

	void businessUnitExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder);

	/**
	 * @param tenantId TODO
	 * @param unitName
	 * @return
	 */

	BusinessUnit findBusinessUnitForTenantByUnitCode(String tenantId, String unitName);

	/**
	 * @param tenantId
	 * @return
	 */
	List<BusinessUnit> getPlainActiveBusinessUnitParentsForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<BusinessUnitPojo> fetchBusinessUnitByTenantId(String tenantId, String search);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<BusinessUnitPojo> getBusinessUnitIdByTenantId(String loggedInUserTenantId);

	/**
	 * @param persistObj
	 */
	void updateBusinessUnit(BusinessUnit persistObj);

	/**
	 * @param businessUnitId
	 * @param costCenterId
	 */
	void removeAssignCostCenter(String businessUnitId, String costCenterId);

	/**
	 * @param response
	 * @param file
	 * @param loggedInUserTenantId
	 */
	void downloadCsvFileForBusiness(HttpServletResponse response, File file, String loggedInUserTenantId);

	/**
	 * @param id
	 * @param status
	 * @return
	 */
	List<CostCenterPojo> getCostCentersByBusinessUnitId(String id, Status status);

	/**
	 * @param id
	 * @param status
	 * @return
	 */
	long getCountCostCentersByBusinessUnitId(String id, Status status);

	List<BusinessUnit> getBusinessForContractFromAwardDetails(String loggedInUserTenantId, BusinessUnit id);

	List<BusinessUnit> getBusinessUnitForContractFromAward(BusinessUnit businessUnit);
	List<String> getBusinessUnitIdByUserId(String userId);

}
