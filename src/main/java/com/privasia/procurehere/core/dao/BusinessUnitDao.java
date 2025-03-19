package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.BusinessUnitPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface BusinessUnitDao extends GenericDao<BusinessUnit, String> {
	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<BusinessUnit> findBusinessUnitsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredBusinessUnitsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalBusinessUnitsForTenant(String tenantId);

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

	void updateBusinessUnitSequenceNumer(String businessUnitId, String sequenceType, Integer sequence);

	Boolean isEmptyUnitCode(String tenantId);

	boolean isExistsUnitCode(String unitCode, String tenantId, String id);

	List<BusinessUnit> getBusinessUnitForTenant(String tenantId);

	BusinessUnit findByUnitCode(String tenantId, String unitName);

	/**
	 * @param tenantId
	 * @return
	 */
	List<BusinessUnit> getPlainActiveBusinessUnitParentsForTenant(String tenantId);

	/**
	 * @param businessUnit
	 */
	void updateBudgetCheckForChildRecords(BusinessUnit businessUnit);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<BusinessUnitPojo> fetchAllActiveBusinessUnitForTenant(String tenantId, String search);

	/**
	 * @param tenantId
	 * @return
	 */
	long countConstructQueryToFetchBusinessUnit(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<BusinessUnitPojo> getBusinessUnitIdByTenantId(String tenantId);

	/**
	 * @param businessUnitId
	 * @param costCenterId
	 */
	void removeAssignCostCenter(String businessUnitId, String costCenterId);

	List<BusinessUnit> getBusinessUnitForCsv(String tenantId, int PAGE_SIZE, int pageNo);

	/**
	 * @param id
	 * @param status TODO
	 * @return
	 */
	List<CostCenterPojo> getCostCentersByBusinessUnitId(String id, Status status);

	/**
	 * @param id
	 * @param status
	 * @return
	 */
	long getCountCostCentersByBusinessUnitId(String id, Status status);

	List<BusinessUnit> getBusinessForContractFromAwardDetails(String tenantId, BusinessUnit id);

	List<BusinessUnit> getBusinessForContractFromAwardDetails(BusinessUnit businessUnit);

	/**
	 * @param businessUnit
	 */
	void updateSpmIntegrationForChildRecords(BusinessUnit businessUnit);
	List<String> getBusinessUnitIdByUserId(String userId);

}
