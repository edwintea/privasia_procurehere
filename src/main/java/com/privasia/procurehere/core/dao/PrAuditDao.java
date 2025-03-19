package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PrAudit;

/**
 * @author parveen
 */
public interface PrAuditDao extends GenericDao<PrAudit, String> {
	/**
	 * @param prId
	 * @return
	 */
	List<PrAudit> getPrAuditByPrId(String prId);

}
