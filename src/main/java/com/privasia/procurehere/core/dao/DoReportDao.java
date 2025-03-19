package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.DoReport;

/**
 * @author sana
 */
public interface DoReportDao extends GenericDao<DoReport, String> {

	/**
	 * @param id
	 * @param tenantId
	 * @return
	 */
	DoReport findReportByDoId(String id, String tenantId);

}
