package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface CostCenterDao extends GenericDao<CostCenter, String> {

	/**
	 * @param costCenter
	 * @param tenantId TODO
	 * @return
	 */
	boolean isExists(CostCenter costCenter, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<CostCenter> getAllActiveCostCentersForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public List<CostCenter> findCostCentersForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public long findTotalFilteredCostCentersForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	public long findTotalCostCentersForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */

	public List<CostCenter> getAllCostCentersByTenantId(String tenantId);

	/**
	 * @param costCenter
	 * @param tenantId TODO
	 * @return
	 */
	CostCenter getByCostCenter(String costCenter, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<CostCenter> getActiveCostCentersForTenant(String tenantId);

	/**
	 * @param costCenterName
	 * @param tenantId
	 * @return
	 */
	CostCenter getActiveCostCenterForTenantByCostCenterName(String costCenterName, String tenantId);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param businessUnitId TODO
	 * @return
	 */
	List<CostCenterPojo> fetchAllCostCenterForTenant(String tenantId, String searchValue, String businessUnitId);

	/**
	 * s@param tenantId
	 * @param businessUnitId TODO
	 * 
	 * @return
	 */
	long fetchFilterCountAllCostForTenant(String tenantId, String businessUnitId);

	/**
	 * @param id
	 * @param tenantId
	 * @return
	 */
	List<String> getCostCenterByBusinessId(String id);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param buId TODO
	 * @return
	 */
	List<CostCenterPojo> fetchAllCostCenterForTenantForUnit(String tenantId, String searchValue, List<String> assignedCostId, String buId);

	/**
	 * @param tenantId
	 * @param buId TODO
	 * @return
	 */
	long fetchFilterCountAllCostForTenantForUnit(String tenantId, List<String> assignedCostId, String buId);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<CostCenterPojo> findCostCenterListByTenantId(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @return
	 */
	long findTotalFilteredCostCenterForTenant(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds);

	/**
	 * @param id
	 * @return
	 */
	CostCenterPojo getCostCenterByCostId(String id);

	/**
	 * @param id
	 */
	public void deleteAssignedCostCenter(String id);

	/**
	 * @param assignedCostId
	 * @return
	 */
	long getCountOfInactiveCostCenter(List<String> assignedCostId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	List<CostCenter> findCostCenterListForTenantIdForCsv(String tenantId, int pageSize, int pageNo);

	/**
	 * @param tenantId
	 * @return
	 */
	long findAllCostCentersForTenant(String tenantId);

	List<CostCenter> getCostCentersByBusinessUnitIdForAwardScreen(String id);

}