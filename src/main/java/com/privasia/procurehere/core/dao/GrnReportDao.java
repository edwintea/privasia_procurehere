package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.GrnReport;

/**
 * @author pooja
 */
public interface GrnReportDao extends GenericDao<GrnReport, String> {

	/**
	 * @param id
	 * @param tenantId
	 * @return
	 */
	GrnReport findReportByGrnId(String id, String tenantId);

}
