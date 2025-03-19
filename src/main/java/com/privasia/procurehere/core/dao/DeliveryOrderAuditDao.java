package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.DeliveryOrderAudit;

/**
 * @author ravi
 */
public interface DeliveryOrderAuditDao extends GenericDao<DeliveryOrderAudit, String> {
	/**
	 * @param doId
	 * @return
	 */
	List<DeliveryOrderAudit> getDoAuditByDoIdForSupplier(String doId);

	/**
	 * @param doId
	 * @return
	 */
	List<DeliveryOrderAudit> getDoAuditByDoIdForBuyer(String doId);

}
