/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;

/**
 * @author jayshree
 *
 */
public interface RfxDaoForRfxAnalytics extends GenericDao<RfaEvent, String>{

	/**
	 * @param startDate
	 * @param endDate
	 * @param eventType
	 * @param eventStatus
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfxVolumeByCategoryForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId);

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
	 * @param startDate
	 * @param endDate
	 * @param eventType
	 * @param tenantId
	 * @return
	 */
	List<RfxAnalyticsPojo> getTopRfxAwardValueByCategoryForForCurrentYear(Date startDate, Date endDate, String eventType, String tenantId);

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
	 * @param startDate
	 * @param endDate
	 * @param eventType
	 * @param eventStatus
	 * @param tenantId
	 * @return
	 */
	long getCountOfRfxVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId);

}
