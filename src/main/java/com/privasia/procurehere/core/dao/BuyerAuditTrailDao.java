package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Ravi
 */
public interface BuyerAuditTrailDao extends GenericDao<BuyerAuditTrail, String> {

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<BuyerAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalAuditTrailForTenant(String loggedInUserTenantId);

}
