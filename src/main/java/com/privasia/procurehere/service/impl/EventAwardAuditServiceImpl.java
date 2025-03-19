/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import com.privasia.procurehere.core.enums.RfxTypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEventAwardAuditDao;
import com.privasia.procurehere.core.dao.RfpEventAwardAuditDao;
import com.privasia.procurehere.core.dao.RfqEventAwardAuditDao;
import com.privasia.procurehere.core.dao.RftEventAwardAuditDao;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.service.EventAwardAuditService;

/**
 * @author Priyanka Ghadage
 */
@Service
@Transactional(readOnly = true)
public class EventAwardAuditServiceImpl implements EventAwardAuditService {

	public static final Logger LOG = LogManager.getLogger(EventAwardAuditServiceImpl.class);

	@Autowired
	RftEventAwardAuditDao rftEventAwardAuditDao;

	@Autowired
	RfqEventAwardAuditDao rfqEventAwardAuditDao;

	@Autowired
	RfpEventAwardAuditDao rfpEventAwardAuditDao;

	@Autowired
	RfaEventAwardAuditDao rfaEventAwardAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void saveRftAwardAudit(RftEventAwardAudit audit) {
		rftEventAwardAuditDao.saveOrUpdate(audit);
	}

	@Override
	public RftEventAwardAudit findByRftAuditId(String id) {
		return rftEventAwardAuditDao.findById(id);
	}

	@Override
	public List<RftEventAwardAudit> findAllAwardAuditForTenantIdAndRftEventId(String loggedInUserTenantId, String eventId) {
		return rftEventAwardAuditDao.findAllAwardAuditForTenantIdAndEventId(loggedInUserTenantId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfqAwardAudit(RfqEventAwardAudit audit) {
		rfqEventAwardAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<RfqEventAwardAudit> findAllAwardAuditForTenantIdAndRfqEventId(String loggedInUserTenantId, String eventId) {
		return rfqEventAwardAuditDao.findAllAwardAuditForTenantIdAndEventId(loggedInUserTenantId, eventId);
	}

	@Override
	public RfqEventAwardAudit findByRfqAuditId(String id) {
		return rfqEventAwardAuditDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfpAwardAudit(RfpEventAwardAudit audit) {
		rfpEventAwardAuditDao.saveOrUpdate(audit);
	}

	@Override
	public List<RfpEventAwardAudit> findAllAwardAuditForTenantIdAndRfpEventId(String loggedInUserTenantId, String eventId) {
		return rfpEventAwardAuditDao.findAllAwardAuditForTenantIdAndEventId(loggedInUserTenantId, eventId);
	}

	@Override
	public RfpEventAwardAudit findByRfpAuditId(String id) {
		return rfpEventAwardAuditDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfaAwardAudit(RfaEventAwardAudit audit) {
		rfaEventAwardAuditDao.saveOrUpdate(audit);

	}

	@Override
	public List<RfaEventAwardAudit> findAllAwardAuditForTenantIdAndRfaEventId(String loggedInUserTenantId, String eventId) {
		return rfaEventAwardAuditDao.findAllAwardAuditForTenantIdAndEventId(loggedInUserTenantId, eventId);
	}

	@Override
	public RfaEventAwardAudit findByRfaAuditId(String id) {
		return rfaEventAwardAuditDao.findById(id);
	}

	@Override
	public void deleteDocumentsByRfxAuditId(String id, RfxTypes rfxTypes) {

		switch (rfxTypes) {
			case RFT:
				rftEventAwardAuditDao.deleteDocumentsByRftAuditId(id);
				break;
			case RFQ:
				rfqEventAwardAuditDao.deleteDocumentsByRfqAuditId(id);
				break;
			case RFA:
				rfaEventAwardAuditDao.deleteDocumentsByRfaAuditId(id);
				break;
			case RFP:
				rfpEventAwardAuditDao.deleteDocumentsByRfpAuditId(id);
				break;
			default:
				break;
		}
	}

}
