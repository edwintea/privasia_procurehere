package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.DeliveryOrderAudit;

/**
 * @author ravi
 */
public interface DoAuditService {
	/**
	 * @param audit
	 */
	void save(DeliveryOrderAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<DeliveryOrderAudit> getDoAuditForBuyerByDoId(String id);

	/**
	 * 
	 * @param doId
	 * @return
	 */
	List<DeliveryOrderAudit> getDoAuditForSupplierByDoId(String doId);

}
