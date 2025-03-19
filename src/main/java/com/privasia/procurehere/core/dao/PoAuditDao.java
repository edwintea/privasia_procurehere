package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PoAudit;

/**
 * @author ravi
 */
public interface PoAuditDao extends GenericDao<PoAudit, String> {
	/**
	 * @param poId
	 * @return
	 */
	List<PoAudit> getPoAuditByPrId(String poId);

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

}
