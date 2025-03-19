package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TimeZonePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.TimeZoneService;

@Service
@Transactional(readOnly = true)
public class TimeZoneServiceImpl implements TimeZoneService {

	@Autowired
	TimeZoneDao timeZoneDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;
	
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Override
	public List<TimeZone> findAllActiveTimeZone() {
		List<TimeZone> list = timeZoneDao.getAllTimeZone();
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public String createTimeZone(TimeZone timeZone) {
		timeZone = timeZoneDao.saveOrUpdate(timeZone);
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'"+timeZone.getTimeZone()+"' Time Zone settings created", timeZone.getCreatedBy().getTenantId(), timeZone.getCreatedBy(), new Date(),ModuleType.TimeZone);
		ownerAuditTrailDao.save(ownerAuditTrail);
		return (timeZone != null ? timeZone.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteTimeZone(TimeZone timeZone) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'"+timeZone.getTimeZone()+"' Time Zone settings updated",timeZone.getModifiedBy().getTenantId(), timeZone.getModifiedBy(), new Date(),ModuleType.TimeZone);
		ownerAuditTrailDao.save(ownerAuditTrail);
		timeZoneDao.delete(timeZone);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTimeZone(TimeZone timeZone) {
		String timezoneName=timeZone.getTimeZone();
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'"+timezoneName+"' Time Zone settings deleted", timeZone.getModifiedBy().getTenantId(), timeZone.getModifiedBy(), new Date(),ModuleType.TimeZone);
		ownerAuditTrailDao.save(ownerAuditTrail);
		timeZoneDao.update(timeZone);
	}

	@Override
	public boolean isExists(TimeZone timeZone) {
		return timeZoneDao.isExists(timeZone);
	}

	@Override
	public TimeZone getTimeZoneById(String id) {
		TimeZone timeZone = timeZoneDao.findById(id);
		if (timeZone.getCountry() != null)
			timeZone.getCountry().getCountryCode();
		return timeZone;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TimeZonePojo> getAllTimeZonePojo() {
		List<TimeZonePojo> returnList = new ArrayList<TimeZonePojo>();

		List<TimeZone> list = timeZoneDao.getAllTimeZone();

		if (CollectionUtil.isNotEmpty(list)) {
			for (TimeZone timeZone : list) {
				if (timeZone.getCreatedBy() != null)
					timeZone.getCreatedBy().getLoginId();
				if (timeZone.getModifiedBy() != null)
					timeZone.getModifiedBy().getLoginId();

				TimeZonePojo tp = new TimeZonePojo(timeZone);
				returnList.add(tp);
			}
		}

		return returnList;

	}

	@Override
	public List<TimeZone> findAllTimezones(TableDataInput tableParams) {
		return timeZoneDao.findAllTimezones(tableParams);
	}

	@Override
	public long findTotalFilteredTimeZones(TableDataInput tableParams) {
		return timeZoneDao.findTotalFilteredTimeZones(tableParams);
	}

	@Override
	public long findTotalTimeZones() {
		return timeZoneDao.findTotalTimeZones();
	}

	@Override
	public List<TimeZone> findTimeZonesForTenantId(String tenantId, String search) {
		List<TimeZone> list = timeZoneDao.findTimeZonesForTenantId(tenantId, search);
		long count = timeZoneDao.countConstructQueryToFetchTimeZones(tenantId);
		
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list != null && count > list.size()) {
				TimeZone more = new TimeZone();
				more.setTimeZoneDescription("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		
		return list;
	}
}
