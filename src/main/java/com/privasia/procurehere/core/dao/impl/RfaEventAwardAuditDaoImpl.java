/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfaEventAwardAuditDao;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author priyanka
 */
@Component
public class RfaEventAwardAuditDaoImpl extends GenericDaoImpl<RfaEventAwardAudit, String> implements RfaEventAwardAuditDao {

	public static final Logger LOG = LogManager.getLogger(RftEventAwardAuditDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfaEventAwardAudit(r.id,ab, r.actionDate, r.description, r.fileName, r.hasSnapshot, r.hasExcelSnapshot) from RfaEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteDocumentsByRfaAuditId(String id) {
		final Query query = getEntityManager().createQuery("UPDATE RfaEventAward r SET r.fileName = null , r.fileData = null where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfaEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct r from RfaEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		try {
			List<RfaEventAwardAudit> rfaEventAwardAuditList = query.getResultList();
			if (rfaEventAwardAuditList != null) {
				return rfaEventAwardAuditList.get(0);
			}
		}
		catch (Exception ef) {
			LOG.error("Error in Fetching AwardData");
		}
		return null;
	}

}
