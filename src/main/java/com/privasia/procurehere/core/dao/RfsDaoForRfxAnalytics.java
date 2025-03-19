/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;

/**
 * @author jayshree
 *
 */
public interface RfsDaoForRfxAnalytics extends GenericDao<SourcingFormRequest, String>{

	/**
	 * @param startDate
	 * @param endDate
	 * @param rfsStatus
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfsVolumeByProcurementCategoryForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param rfsStatus
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfsVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param rfsStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfTopRfsByProcurementCategoryForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param rfsStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfTopRfsByBusinessUnitForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId);

}
