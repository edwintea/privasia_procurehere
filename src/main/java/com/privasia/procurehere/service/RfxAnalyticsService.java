/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;

/**
 * @author jayshree
 *
 */
public interface RfxAnalyticsService {

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
	 * @param start
	 * @param end
	 * @param eventType
	 * @param eventStatus
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfxVolumeByCategoryForCurrentYear(Date start, Date end, String eventType, String eventStatus, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param eventType
	 * @param eventStatus
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfxVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param eventType
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfxAwardValueByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String tenantId);

	/**
	 * @param start
	 * @param end
	 * @param eventType
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfxAwardValueByCategoryForForCurrentYear(Date start, Date end, String eventType, String tenantId);

	/**
	 * @param start
	 * @param end
	 * @param rfsStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfTopRfsByProcurementCategoryForCurrentYear(Date start, Date end, String rfsStatus, String tenantId);

	/**
	 * @param start
	 * @param end
	 * @param rfsStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfTopRfsByBusinessUnitForCurrentYear(Date start, Date end, String rfsStatus, String tenantId);

	/**
	 * @param startDate
	 * @param endDate
	 * @param eventType
	 * @param eventStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfRfxVolumeByCategoryForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId);

	/**
	 * @param start
	 * @param end
	 * @param eventType
	 * @param eventStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfRfxVolumeByBusinessUnitForCurrentYear(Date start, Date end, String eventType, String eventStatus, String tenantId);

}
