package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Ravi
 *
 */
public interface SupplierAuditTrailDao extends GenericDao<SupplierAuditTrail, String> {

	/**
	 * 
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param moduleType 
	 * @return
	 */
	List<SupplierAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * 
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param moduleType 
	 * @return
	 */
	long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * 
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalAuditTrailForTenant(String loggedInUserTenantId);


 
	
}
