package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.PrAudit;

/**
 * @author parveen
 */
public interface PrAuditService {
	/**
	 * @param audit
	 */
	void save(PrAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<PrAudit> getPrAuditByPrId(String id);

}
