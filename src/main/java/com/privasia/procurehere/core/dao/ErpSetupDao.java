package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.pojo.MobileEventPojo;

/**
 * @author parveen
 */
public interface ErpSetupDao extends GenericDao<ErpSetup, String> {
	/**
	 * @param tenantId
	 * @return
	 */
	ErpSetup getErpConfigBytenantId(String tenantId);

	/**
	 * @param appId
	 * @return
	 */
	ErpSetup getErpConfigWithTepmlateByAppId(String appId);

	/**
	 * @param appId
	 * @return
	 */
	ErpSetup getErpConfigByAppId(String appId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	ErpSetup findErpByWithTepmlateTenantId(String loggedInUserTenantId);

	/**
	 * @param prNo
	 * @param tanentId 
	 * @return
	 */
	List<MobileEventPojo> getEventTypeFromPrNo(String prNo, String tenantId);

}
