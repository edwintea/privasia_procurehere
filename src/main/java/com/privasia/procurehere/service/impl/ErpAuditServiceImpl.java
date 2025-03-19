package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ErpAuditDao;
import com.privasia.procurehere.core.entity.ErpAudit;
import com.privasia.procurehere.core.enums.ErpAuditType;
import com.privasia.procurehere.service.ErpAuditService;

/**
 * @author parveen
 */
@Service
@Transactional(readOnly = true)
public class ErpAuditServiceImpl implements ErpAuditService {

	@Autowired
	ErpAuditDao erpAuditDao;

	@Override
	@Transactional(readOnly = false)
	public ErpAudit save(ErpAudit erpAudit) {
		return erpAuditDao.saveOrUpdate(erpAudit);
	}

	@Override
	public List<ErpAudit> getAllAuditByTenantIdAndActionType(String tenantId, ErpAuditType type) {
		return erpAuditDao.getAllAuditByTenantIdAndActionType(tenantId, type);
	}

	@Override
	public ErpAudit findById(String auditId) {
		return erpAuditDao.findById(auditId);
	}

	@Override
	public boolean isExists(String prNo, String tenantId) {
		return erpAuditDao.isExists(prNo, tenantId);
	}

}
