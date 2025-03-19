/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RftEventAwardAuditDao;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author priyanka
 */
@Component
public class RftEventAwardAuditDaoImpl extends GenericDaoImpl<RftEventAwardAudit, String> implements RftEventAwardAuditDao {

	public static final Logger LOG = LogManager.getLogger(RftEventAwardAuditDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RftEventAwardAudit(r.id, ab, r.actionDate, r.description, r.fileName, r.hasSnapshot, r.hasExcelSnapshot) from RftEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteDocumentsByRftAuditId(String id) {
		final Query query = getEntityManager().createQuery("UPDATE RftEventAward r SET r.fileName = null , r.fileData = null where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RftEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct r from RftEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		try {
			List<RftEventAwardAudit> rftEventAwardAuditList = query.getResultList();
			if (rftEventAwardAuditList != null) {
				return rftEventAwardAuditList.get(0);
			}
		}
		catch (Exception ef) {
			LOG.error("Error in Fetching AwardData");
		}
		return null;
	}

}
