/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.enums.TenantType;

/**
 * @author Ravi
 */
public interface AccessRightsDao extends GenericDao<AccessRights, String> {

	/**
	 * @return
	 */
	List<AccessRights> getParentAccessControlList();

	/**
	 * @param value
	 * @return
	 */
	AccessRights findByAccessControl(String value);

	/**
	 * @param value
	 * @return
	 */
	AccessRights findByACLValue(String value);

	/**
	 * @param list
	 * @param createdBy TODO
	 */
	void checkAccessControlListMasterData(List<AccessRights> list);

	/**
	 * @param includeViewOnly TODO
	 * @return
	 */
	List<AccessRights> getAccessControlListForSupplier(boolean includeViewOnly);

	/**
	 * @param aclValue
	 * @return
	 */
	List<AccessRights> findChildAccessForId(String aclValue);

	/**
	 * @param tenentType
	 * @param isEventBasedSubscription TODO
	 * @return
	 */
	List<AccessRights> getParentAccessControlListForType(TenantType tenentType, boolean isEventBasedSubscription);

	/**
	 * @param includeViewOnly TODO
	 * @param isEventBasedSubscription TODO
	 * @return
	 */
	List<AccessRights> getAccessControlListForBuyer(boolean includeViewOnly, boolean isEventBasedSubscription);

	/**
	 * @param includeReadOnly TODO
	 * @return
	 */
	List<AccessRights> getAccessControlListForOwner(boolean includeReadOnly);

	/**
	 * @param aclValues
	 * @return
	 */
	List<AccessRights> findCustomeAccessForBuyer(String[] aclValues);

	/**
	 * @return
	 */
	List<AccessRights> getPrAccessControlListForBuyer();

	List<AccessRights> getAccessControlListForFinanceComapany(boolean includeViewOnly);

	List<AccessRights> findCustomeAccessForFinance(String[] financeDefaultSystemAdminSettingAclValues);
 
}
