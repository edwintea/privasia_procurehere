package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.PoAudit;
import com.privasia.procurehere.core.enums.AuditTypes;

/**
 * @author ravi
 */
public interface PoAuditService {
	/**
	 * @param audit
	 */
	void save(PoAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<PoAudit> getPoAuditByPrId(String id);

	/**
	 * @param poId
	 * @return
	 */
	List<PoAudit> getPoAuditByPoIdForBuyer(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoAudit> getPoAuditByPoIdForSupplier(String poId);

	/**
	 * @param poId
	 * @return
	 */
	PoAudit getPoAuditById(String poId);

}
