/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserRolePojo;

/**
 * @author Ravi
 */
public interface UserRoleService {

	/*
	 * New
	 * @author Javed & kapil
	 */

	String saveRole(UserRole userRole,User user);

	void updateRole(UserRole userRole,User user);

	void deleteRole(UserRole userRole,User user);

	boolean isExists(UserRole userRole, String tenantId);

	List<UserRolePojo> getAllUserRolePojo(String tenantId);

	UserRole getUserRoleById(String id);

	/*
	 * End
	 */

	List<UserRole> getUserRoles();

	UserRole findUserRoleById(String userRoleId);

	void saveUserRole(UserRole userRole, List<AccessRights> accessControlLists);

	void updateUserRole(UserRole userRole, List<AccessRights> accessControlLists);

	UserRole loadUserRoleById(String userRoleId);

	List<AccessRights> getAssignedAccessControlList(String userRoleId);

	/**
	 * @param tenentType
	 * @param isEventBasedSubscription TODO
	 * @return
	 */
	List<AccessRights> getParentAccessControlListForType(TenantType tenentType, boolean isEventBasedSubscription);

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

	UserRole findByUserRoleAndTenantId(String roleName, String tenantId);

	UserRole saveUserRole(UserRole userRole, User user);
	
	

}
