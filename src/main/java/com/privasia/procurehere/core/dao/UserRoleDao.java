package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Ravi
 */
public interface UserRoleDao extends GenericDao<UserRole, String> {

	boolean isExist(UserRole userRole, String tenantId);

	/**
	 * @param id
	 * @return
	 */
	UserRole loadById(String id);

	/**
	 * @param tenantId TODO
	 * @return
	 */
	List<UserRole> findAllActiveRoles(String tenantId);

	/**
	 * @param value
	 * @param tenantId
	 * @return
	 */
	UserRole findByUserRoleAndTenantId(String value, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalUserRoleListForTenant(String tenantId);

	/**
	 * @param tableParams
	 * @param tenantId
	 * @return
	 */
	long findTotalFilteredUserRoleListForTenant(TableDataInput tableParams, String tenantId);

	/**
	 * @param tableParams
	 * @param tenantId
	 * @return
	 */
	List<UserRole> findAllUserRoleListForTenant(TableDataInput tableParams, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<UserRole> findAllUserRolesForTenant(String tenantId);

}
