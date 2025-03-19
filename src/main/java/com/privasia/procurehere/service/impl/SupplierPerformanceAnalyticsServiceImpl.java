/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierPerformanceAnalyticsDao;
import com.privasia.procurehere.core.pojo.SpAnalyticsPojo;
import com.privasia.procurehere.service.SupplierPerformanceAnalyticsService;

/**
 * @author jayshree
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceAnalyticsServiceImpl implements SupplierPerformanceAnalyticsService {

	@Autowired
	SupplierPerformanceAnalyticsDao supplierPerformanceAnalyticsDao;
	
	@Override
	public List<SpAnalyticsPojo> getTopHighPerformanceSuppier(Date startDate, Date endDate, String tenantId) {
		return supplierPerformanceAnalyticsDao.getTopHighPerformanceSuppier(startDate, endDate, tenantId);
	}

	@Override
	public List<SpAnalyticsPojo> getTopHighPerformanceSuppierByBUnit(Date start, Date end, String buId, String tenantId) {
		return supplierPerformanceAnalyticsDao.getTopHighPerformanceSuppierByBUnit(start, end, buId, tenantId);
	}

	@Override
	public List<SpAnalyticsPojo> getTopHighPerformanceSuppierByProcCat(Date startDate, Date endDate, String procCatId, String tenantId) {
		return supplierPerformanceAnalyticsDao.getTopHighPerformanceSuppierByProcCat(startDate, endDate, procCatId, tenantId);
	}

	@Override
	public List<SpAnalyticsPojo> getTopLowPerformanceSuppier(Date startDate, Date endDate, String tenantId) {
		return supplierPerformanceAnalyticsDao.getTopLowPerformanceSuppier(startDate, endDate, tenantId);
	}

	@Override
	public List<SpAnalyticsPojo> getTopLowPerformanceSuppByBU(Date startDate, Date endDate, String buId, String tenantId) {
		return supplierPerformanceAnalyticsDao.getTopLowPerformanceSuppByBU(startDate, endDate, buId, tenantId);
	}

	@Override
	public List<SpAnalyticsPojo> getTopLowPerformanceSuppByProcCat(Date startDate, Date endDate, String pcId, String tenantId) {
		return supplierPerformanceAnalyticsDao.getTopLowPerformanceSuppByProcCat(startDate, endDate, pcId, tenantId);
	}

}