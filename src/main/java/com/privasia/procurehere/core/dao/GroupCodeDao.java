/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author jayshree
 *
 */
public interface GroupCodeDao extends GenericDao<GroupCode, String> {

	/**
	 * @param groupCode
	 * @param tenantId
	 * @return
	 */
	boolean isExists(GroupCode groupCode, String tenantId);

	/**
	 * @param input
	 * @param tenantId
	 * @return
	 */
	List<GroupCode> findGroupCodesForTenant(TableDataInput input, String tenantId);

	/**
	 * @param input
	 * @param tenantId
	 * @return
	 */
	long findTotalFilteredGroupCodesForTenant(TableDataInput input, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalGroupCodesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findCountOfGroupCodesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	List<GroupCode> findGroupCodeListForTenantIdForCsv(String tenantId, int pageSize, int pageNo);

	/**
	 * @param groupCode
	 * @param tenantId
	 * @return
	 */
	GroupCode getByGroupCode(String groupCode, String tenantId);

	/**
	 * @param tenantId
	 * @param searchVal
	 * @return
	 */
	List<GroupCode> fetchAllGroupCodesForTenant(String tenantId, String searchVal);

	/**
	 * @param buId
	 * @return
	 */
	List<GroupCode> getGroupCodeIdByBusinessUnitId(String buId);

	/**
	 * @param tenantId
	 * @param searchVal
	 * @param buId
	 * @return
	 */
	List<GroupCode> fetchAllCostCenterForTenantForUnit(String tenantId, String searchVal, String buId);

	/**
	 * @param tenantId
	 * @param buId
	 * @return
	 */
	long fetchFilterCountAllCostForTenantForUnit(String tenantId, String buId);

	/**
	 * @param buId
	 * @return
	 */
	long getCountOfInactiveGroupCode(String buId);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @param groupCodeIds
	 * @param removeIds
	 * @return
	 */
	List<GroupCode> findGroupCodeListByTenantId(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @param groupCodeIds
	 * @param removeIds
	 * @return
	 */
	long findTotalFilteredGroupCodeForTenant(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds);

	/**
	 * @param tenantId
	 * @return
	 */
	List<GroupCode> fetchAllActiveGroupCodeForTenantID(String tenantId);

	/**
	 * @param buId
	 * @return
	 */
	List<String> getGroupCodeByBusinessId(String buId);

	/**
	 * @param gcId
	 * @return
	 */
	GroupCode getGroupCodeById(String gcId);

	/**
	 * 
	 * @param gcIds
	 * @return
	 */
	List<GroupCode> getGroupCodedByIds(List<String> gcIds);

	/**
	 * 
	 * @param buId
	 * @return
	 */
	List<GroupCode> getAllGroupCodeIdByBusinessUnitId(String buId);

}
