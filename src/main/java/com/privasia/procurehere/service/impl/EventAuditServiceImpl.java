/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.dao.RfiEventAuditDao;
import com.privasia.procurehere.core.dao.RfpEventAuditDao;
import com.privasia.procurehere.core.dao.RfqEventAuditDao;
import com.privasia.procurehere.core.dao.RftEventAuditDao;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.service.EventAuditService;

/**
 * @author Teja
 */
@Service
@Transactional(readOnly = true)
public class EventAuditServiceImpl implements EventAuditService {

	public static final Logger LOG = LogManager.getLogger(EventAuditServiceImpl.class);

	@Autowired
	RftEventAuditDao rftEventAuditDao;

	@Autowired
	RfpEventAuditDao rfpEventAuditDao;

	@Autowired
	RfqEventAuditDao rfqEventAuditDao;

	@Autowired
	RfiEventAuditDao rfiEventAuditDao;

	@Autowired
	RfaEventAuditDao rfaEventAuditDao;

	@Transactional(readOnly = false)
	@Override
	public void save(RftEventAudit audit) {
		try {
			rftEventAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error saving RFT Event Audit : " + e.getMessage(), e);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public void save(RfpEventAudit audit) {
		try {
			rfpEventAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error saving RFT Event Audit : " + e.getMessage(), e);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public void save(RfqEventAudit audit) {
		try {
			rfqEventAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error saving RFT Event Audit : " + e.getMessage(), e);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public void save(RfiEventAudit audit) {
		try {
			rfiEventAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error saving RFT Event Audit : " + e.getMessage(), e);
		}

	}

	@Transactional(readOnly = false)
	@Override
	public void save(RfaEventAudit audit) {
		try {
			rfaEventAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error saving RFT Event Audit : " + e.getMessage(), e);
		}
	}

	@Override
	public List<RftEventAudit> getRftEventAudit(String eventId) {
		return rftEventAuditDao.getRftEventAudit(eventId);
	}
	
	@Override
	public List<RfpEventAudit> getRfpEventAudit(String eventId) {
		return rfpEventAuditDao.getRfpEventAudit(eventId);
	}

	@Override
	public List<RfqEventAudit> getRfqEventAudit(String eventId) {
		return rfqEventAuditDao.getRfqEventAudit(eventId);
	}

	@Override
	public List<RfiEventAudit> getRfiEventAudit(String eventId) {
		return rfiEventAuditDao.getRfiEventAudit(eventId);
	}

	@Override
	public List<RfaEventAudit> getRfaEventAudit(String eventId) {
		return rfaEventAuditDao.getRfaEventAudit(eventId);
	}


	@Override
	public List<RftEventAudit> getRftEventAuditForSupplier(String eventId, String supplierId) {
		return rftEventAuditDao.getRftEventAuditForSupplier(eventId, supplierId);
	}
	
	@Override
	public List<RfaEventAudit> getRfaEventAuditForSupplier(String eventId, String supplierId) {
		return rfaEventAuditDao.getRfaEventAuditForSupplier(eventId, supplierId);
	}
	
	@Override
	public List<RfiEventAudit> getRfiEventAuditForSupplier(String eventId, String supplierId) {
		return rfiEventAuditDao.getRfiEventAuditForSupplier(eventId, supplierId);
	}
	
	@Override
	public List<RfpEventAudit> getRfpEventAuditForSupplier(String eventId, String supplierId) {
		return rfpEventAuditDao.getRfpEventAuditForSupplier(eventId, supplierId);
	}
	
	@Override
	public List<RfqEventAudit> getRfqEventAuditForSupplier(String eventId, String supplierId) {
		return rfqEventAuditDao.getRfqEventAuditForSupplier(eventId, supplierId);
	}
	
	@Override
	public RftEventAudit getRftEventAuditById(String id) {
		return rftEventAuditDao.findById(id);
	}

	@Override
	public RfpEventAudit getRfpEventAuditById(String id) {
		return rfpEventAuditDao.findById(id);
	}

	@Override
	public RfqEventAudit getRfqEventAuditById(String id) {
		return rfqEventAuditDao.findById(id);
	}

	@Override
	public RfiEventAudit getRfiEventAuditById(String id) {
		return rfiEventAuditDao.findById(id);
	}

	@Override
	public RfaEventAudit getRfaEventAuditById(String id) {
		return rfaEventAuditDao.findById(id);
	}

}
