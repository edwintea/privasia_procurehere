package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Ravi
 * @author parveen
 */
public interface OwnerAuditTrailDao extends GenericDao<OwnerAuditTrail, String> {
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate 
	 * @param endDate
	 * @param moduleType 
	 * @return
	 */
	List<OwnerAuditTrail> findOwnerAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate 
	 * @param endDate 
	 * @param moduleType 
	 * @return
	 */
	long findTotalFilteredOwnerAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalOwnerAuditTrailForTenant(String loggedInUserTenantId);

}
