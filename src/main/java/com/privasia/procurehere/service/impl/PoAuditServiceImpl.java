package com.privasia.procurehere.service.impl;

import java.util.List;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PoAuditDao;
import com.privasia.procurehere.core.entity.PoAudit;
import com.privasia.procurehere.service.PoAuditService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class PoAuditServiceImpl implements PoAuditService {

	public static final Logger LOG = LogManager.getLogger(PoAuditServiceImpl.class);

	@Autowired
	PoAuditDao poAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(PoAudit audit) {
		LOG.info("Po Audit :" + audit.toLogString());
		poAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<PoAudit> getPoAuditByPrId(String prId) {
		return poAuditDao.getPoAuditByPrId(prId);
	}

	@Override
	public List<PoAudit> getPoAuditByPoIdForBuyer(String poId) {
		return poAuditDao.getPoAuditByPoIdForBuyer(poId);
	}

	@Override
	public List<PoAudit> getPoAuditByPoIdForSupplier(String poId) {
		return poAuditDao.getPoAuditByPoIdForSupplier(poId);
	}

	@Override
	public PoAudit getPoAuditById(String id) {
		return poAuditDao.findById(id);
	}
}
