package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GoodsReceiptNoteAuditDao;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;
import com.privasia.procurehere.service.GoodsReceiptNoteAuditService;

/**
 * @author pooja
 */
@Service
@Transactional(readOnly = true)
public class GoodsReceiptNoteAuditServiceImpl implements GoodsReceiptNoteAuditService {

	public static final Logger LOG = LogManager.getLogger(GoodsReceiptNoteAuditServiceImpl.class);

	@Autowired
	GoodsReceiptNoteAuditDao grnAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(GoodsReceiptNoteAudit audit) {
		LOG.info("Grn Audit :" + audit.toLogString());
		grnAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<GoodsReceiptNoteAudit> getGrnAuditForBuyerByGrnId(String grnId) {
		return grnAuditDao.getGrnAuditByGrnIdForBuyer(grnId);
	}

	@Override
	public List<GoodsReceiptNoteAudit> getGrnAuditForSupplierByGrnId(String grnId) {
		return grnAuditDao.getGrnAuditByGrnIdForSupplier(grnId);
	}

}
