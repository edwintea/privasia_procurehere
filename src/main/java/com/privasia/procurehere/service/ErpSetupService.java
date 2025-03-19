package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.ErpSetup;

/**
 * @author parveen
 */
public interface ErpSetupService {

	/**
	 * @param erpSetup
	 * @return
	 */
	ErpSetup save(ErpSetup erpSetup);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	ErpSetup findErpByWithTepmlateTenantId(String loggedInUserTenantId);
	
	
	/**
	 * @param tenantId
	 * @return
	 */
	ErpSetup getErpConfigBytenantId(String tenantId);

	String genrateSquanceNumber();

}
