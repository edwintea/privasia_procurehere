package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.InvoiceAuditDao;
import com.privasia.procurehere.core.entity.InvoiceAudit;
import com.privasia.procurehere.service.InvoiceAuditService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class InvoiceAuditServiceImpl implements InvoiceAuditService {

	public static final Logger LOG = LogManager.getLogger(InvoiceAuditServiceImpl.class);

	@Autowired
	InvoiceAuditDao invoiceAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(InvoiceAudit audit) {
		LOG.info("Invoice Audit :" + audit.toLogString());
		invoiceAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<InvoiceAudit> getInvoiceAuditForBuyerByInvoiceId(String doId) {
		return invoiceAuditDao.getInvoiceAuditByInvoiceIdForBuyer(doId);
	}

	@Override
	public List<InvoiceAudit> getInvoiceAuditForSupplierByInvoiceId(String doId) {
		return invoiceAuditDao.getInvoiceAuditByInvoiceIdForSupplier(doId);
	}

}
