package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PrAuditDao;
import com.privasia.procurehere.core.entity.PrAudit;
import com.privasia.procurehere.service.PrAuditService;

/**
 * @author parveen
 */
@Service
@Transactional(readOnly = true)
public class PrAuditServiceImpl implements PrAuditService {

	public static final Logger LOG = LogManager.getLogger(PrAuditServiceImpl.class);

	@Autowired
	PrAuditDao prAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(PrAudit audit) {
		LOG.info("Pr Audit :" + audit.toLogString());
		prAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<PrAudit> getPrAuditByPrId(String prId) {
		return prAuditDao.getPrAuditByPrId(prId);
	}

}
