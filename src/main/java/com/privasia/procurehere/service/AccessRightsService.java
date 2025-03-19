/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.enums.TenantType;

/**
 * @author Ravi
 *
 */
public interface AccessRightsService {

	List<AccessRights> getAccessRights();

	List<AccessRights> getParentAccessRights();

	AccessRights findAccessRights(String aclId);

	void saveAccessRights(AccessRights accessRight);

	void updateAccessRights(AccessRights accessRight);

	void deleteAccessRights(AccessRights accessRight);

	AccessRights findByProperty(String propertyName, Object value);



	/**
	 * 
	 * @param id
	 * @return
	 */
	AccessRights getAccessRightsById(String id);
	
	List<AccessRights> findChildAccessForId(String aclValue);

	/**
	 * 
	 * @param tenentType
	 * @param isEventBasedSubscription TODO
	 * @return
	 */
	List<AccessRights> getParentAccessControlListForType(TenantType tenentType, boolean isEventBasedSubscription);
}
