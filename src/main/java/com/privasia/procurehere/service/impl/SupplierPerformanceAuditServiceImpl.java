package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierPerformanceAuditDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;

/**
 * @author anshul
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceAuditServiceImpl implements SupplierPerformanceAuditService {

	public static final Logger LOG = LogManager.getLogger(SupplierPerformanceAuditServiceImpl.class);

	@Autowired
	SupplierPerformanceAuditDao formAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(SupplierPerformanceAudit audit) {
		formAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormId(String formId) {
		return formAuditDao.getSupplierPerformanceAuditByFormId(formId);
	}

	@Override
	public SupplierPerformanceAudit getAuditByFormId(String id) {
		return formAuditDao.findById(id);
	}

	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForOwner(String formId) {
		return formAuditDao.getSupplierPerformanceAuditByFormIdForOwner(formId);
	}

	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForEvaluater(String formId, String evaluatorUserId) {
		return formAuditDao.getSupplierPerformanceAuditByFormIdForEvaluater(formId, evaluatorUserId);
	}

	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForApprover(String formId, String evaluatorUserId) {
		return formAuditDao.getSupplierPerformanceAuditByFormIdForApprover(formId, evaluatorUserId);
	}

}
