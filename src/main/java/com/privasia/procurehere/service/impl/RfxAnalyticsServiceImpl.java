/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfsDaoForRfxAnalytics;
import com.privasia.procurehere.core.dao.RfxDaoForRfxAnalytics;
import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;
import com.privasia.procurehere.service.RfxAnalyticsService;

/**
 * @author jayshree
 */
@Service
@Transactional(readOnly = true)
public class RfxAnalyticsServiceImpl implements RfxAnalyticsService {

	@Autowired
	RfsDaoForRfxAnalytics rfsDaoForRfxAnalytics;

	@Autowired
	RfxDaoForRfxAnalytics rfxDaoForRfxAnalytics;

	@Override
	public List<RfxAnalyticsPojo> getTopRfsVolumeByProcurementCategoryForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		List<RfxAnalyticsPojo> list = rfsDaoForRfxAnalytics.getTopRfsVolumeByProcurementCategoryForCurrentYear(startDate, endDate, rfsStatus, tenantId);
		// FOR total Count
		return list;
	}

	@Override
	public List<RfxAnalyticsPojo> getTopRfsVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		List<RfxAnalyticsPojo> list = rfsDaoForRfxAnalytics.getTopRfsVolumeByBusinessUnitForCurrentYear(startDate, endDate, rfsStatus, tenantId);
		return list;
	}

	@Override
	public List<RfxAnalyticsPojo> getTopRfxVolumeByCategoryForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		List<RfxAnalyticsPojo> list = rfxDaoForRfxAnalytics.getTopRfxVolumeByCategoryForCurrentYear(startDate, endDate, eventType, eventStatus, tenantId);
		return list;
	}

	@Override
	public List<RfxAnalyticsPojo> getTopRfxVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		List<RfxAnalyticsPojo> list = rfxDaoForRfxAnalytics.getTopRfxVolumeByBusinessUnitForCurrentYear(startDate, endDate, eventType, eventStatus, tenantId);
		return list;
	}
	
	@Override
	public List<RfxAnalyticsPojo> getTopRfxAwardValueByCategoryForForCurrentYear(Date startDate, Date endDate, String eventType, String tenantId) {
		List<RfxAnalyticsPojo> list = rfxDaoForRfxAnalytics.getTopRfxAwardValueByCategoryForForCurrentYear(startDate, endDate, eventType, tenantId);
		return list;
	}

	@Override
	public List<RfxAnalyticsPojo> getTopRfxAwardValueByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String tenantId) {
		List<RfxAnalyticsPojo> list = rfxDaoForRfxAnalytics.getTopRfxAwardValueByBusinessUnitForCurrentYear(startDate, endDate, eventType, tenantId);
		return list;
	}

	@Override
	public long getCountOfTopRfsByProcurementCategoryForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		return rfsDaoForRfxAnalytics.getCountOfTopRfsByProcurementCategoryForCurrentYear(startDate, endDate, rfsStatus, tenantId);
	}

	@Override
	public long getCountOfTopRfsByBusinessUnitForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		return rfsDaoForRfxAnalytics.getCountOfTopRfsByBusinessUnitForCurrentYear(startDate, endDate, rfsStatus, tenantId);
	}

	@Override
	public long getCountOfRfxVolumeByCategoryForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		return rfxDaoForRfxAnalytics.getCountOfRfxVolumeByCategoryForCurrentYear(startDate, endDate, eventType, eventStatus, tenantId);
	}

	@Override
	public long getCountOfRfxVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		return rfxDaoForRfxAnalytics.getCountOfRfxVolumeByBusinessUnitForCurrentYear(startDate, endDate, eventType, eventStatus, tenantId);
	}

}
