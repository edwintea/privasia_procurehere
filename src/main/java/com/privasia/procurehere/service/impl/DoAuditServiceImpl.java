package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.DeliveryOrderAuditDao;
import com.privasia.procurehere.core.entity.DeliveryOrderAudit;
import com.privasia.procurehere.service.DoAuditService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class DoAuditServiceImpl implements DoAuditService {

	public static final Logger LOG = LogManager.getLogger(DoAuditServiceImpl.class);

	@Autowired
	DeliveryOrderAuditDao doAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(DeliveryOrderAudit audit) {
		LOG.info("Pr Audit :" + audit.toLogString());
		doAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<DeliveryOrderAudit> getDoAuditForBuyerByDoId(String doId) {
		return doAuditDao.getDoAuditByDoIdForBuyer(doId);
	}

	@Override
	public List<DeliveryOrderAudit> getDoAuditForSupplierByDoId(String doId) {
		return doAuditDao.getDoAuditByDoIdForSupplier(doId);
	}

}
