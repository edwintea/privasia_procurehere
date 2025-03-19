package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FinanceAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Ravi
 *
 */
public interface FinanceAuditTrailDao extends GenericDao<FinanceAuditTrail, String> {

	/**
	 * 
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<FinanceAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * 
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * 
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalAuditTrailForTenant(String loggedInUserTenantId);


 
	
}
