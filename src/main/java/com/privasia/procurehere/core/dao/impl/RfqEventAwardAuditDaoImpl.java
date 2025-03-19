/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfqEventAwardAuditDao;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author priyanka
 */
@Component
public class RfqEventAwardAuditDaoImpl extends GenericDaoImpl<RfqEventAwardAudit, String> implements RfqEventAwardAuditDao {

	public static final Logger LOG = LogManager.getLogger(RftEventAwardAuditDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<RfqEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct New com.privasia.procurehere.core.entity.RfqEventAwardAudit(r.id, ab, r.actionDate, r.description, r.fileName, r.hasSnapshot, r.hasExcelSnapshot) from RfqEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteDocumentsByRfqAuditId(String id) {
		final Query query = getEntityManager().createQuery("UPDATE RfqEventAward r SET r.fileName = null , r.fileData = null where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfqEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct r from RfqEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		try {
			List<RfqEventAwardAudit> rfpEventAwardAuditList = query.getResultList();
			if (rfpEventAwardAuditList != null) {
				return rfpEventAwardAuditList.get(0);
			}
		}
		catch (Exception ef) {
			LOG.error("Error in Fetching AwardData");
		}
		return null;
	}

}
