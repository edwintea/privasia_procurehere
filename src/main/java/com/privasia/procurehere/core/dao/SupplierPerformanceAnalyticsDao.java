/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.pojo.SpAnalyticsPojo;

/**
 * @author jayshree
 *
 */
public interface SupplierPerformanceAnalyticsDao extends GenericDao<SupplierPerformanceForm, String>{

	/**
	 * @param startDate
	 * @param endDate
	 * @param tenantId
	 * @return
	 */
	List<SpAnalyticsPojo> getTopHighPerformanceSuppier(Date startDate, Date endDate, String tenantId);

	/**
	 * @param start
	 * @param end
	 * @param buId
	 * @param tenantId
	 * @return
	 */
	List<SpAnalyticsPojo> getTopHighPerformanceSuppierByBUnit(Date start, Date end, String buId, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param procCatId
	 * @param tenantId
	 * @return
	 */
	List<SpAnalyticsPojo> getTopHighPerformanceSuppierByProcCat(Date startDate, Date endDate, String procCatId, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param tenantId
	 * @return
	 */
	List<SpAnalyticsPojo> getTopLowPerformanceSuppier(Date startDate, Date endDate, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param buId
	 * @param tenantId
	 * @return
	 */
	List<SpAnalyticsPojo> getTopLowPerformanceSuppByBU(Date startDate, Date endDate, String buId, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param pcId
	 * @param tenantId
	 * @return
	 */
	List<SpAnalyticsPojo> getTopLowPerformanceSuppByProcCat(Date startDate, Date endDate, String pcId, String tenantId);
	
}
