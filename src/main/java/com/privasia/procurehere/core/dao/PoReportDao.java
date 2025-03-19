package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.PoReport;

/**
 * @author sana
 */
public interface PoReportDao extends GenericDao<PoReport, String> {

	/**
	 * @param id
	 * @param tenantId
	 * @return
	 */
	PoReport findReportByPoId(String id);

	/**
	 * @param poId
	 */
	void deletePoReportByPoId(String poId);

}
