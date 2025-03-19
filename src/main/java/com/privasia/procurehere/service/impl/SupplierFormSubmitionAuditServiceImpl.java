package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierFormSubmitionAuditDao;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.service.SupplierFormSubmitionAuditService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class SupplierFormSubmitionAuditServiceImpl implements SupplierFormSubmitionAuditService {

	public static final Logger LOG = LogManager.getLogger(SupplierFormSubmitionAuditServiceImpl.class);

	@Autowired
	SupplierFormSubmitionAuditDao formAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(SupplierFormSubmitionAudit audit) {
		formAuditDao.save(audit);
	}

	@Override
	public List<SupplierFormSubmitionAudit> getFormAuditById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SupplierFormSubmitionAudit> getFormAuditByFormIdForBuyer(String formSubId) {
		return formAuditDao.getFormAuditByFormIdForBuyer(formSubId);
	}

	@Override
	public List<SupplierFormSubmitionAudit> getFormAuditByFormIdForSupplier(String formSubId) {
		return formAuditDao.getFormAuditByFormIdForSupplier(formSubId);
	}

}
