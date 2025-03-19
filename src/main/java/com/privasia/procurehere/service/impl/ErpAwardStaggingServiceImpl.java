package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ErpAwardStaggingDao;
import com.privasia.procurehere.core.entity.ErpAwardStaging;
import com.privasia.procurehere.service.ErpAwardStaggingService;

/**
 * @author yogesh
 */
@Service
@Transactional(readOnly = true)
public class ErpAwardStaggingServiceImpl implements ErpAwardStaggingService {

	@Autowired
	ErpAwardStaggingDao erpAwardStaggingDao;

	@Override
	@Transactional(readOnly = false)
	public ErpAwardStaging saveErpAwardStaging(ErpAwardStaging stagging) {
		return erpAwardStaggingDao.save(stagging);
	}

	@Override
	public List<ErpAwardStaging> getStaggingData(String tenantId, String refranceNo) {
		return erpAwardStaggingDao.getStaggingData(tenantId, refranceNo);
	}

	@Override
	@Transactional(readOnly = false)
	public ErpAwardStaging update(ErpAwardStaging erpAwardStaging) {
		return erpAwardStaggingDao.update(erpAwardStaging);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateOldAwardStagingFlag(String docNo, String tenantId, String id) {
		erpAwardStaggingDao.updateOldAwardStagingFlag(docNo, tenantId, id);

	}

	@Override
	public ErpAwardStaging findAwardStaggingByEventID(String eventId, String tenantId) {
		return erpAwardStaggingDao.findAwardStaggingByEventID(eventId, tenantId);
	}

}
