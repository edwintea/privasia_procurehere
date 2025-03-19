/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfpEventAwardAuditDao;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author priyanka
 */
@Component
public class RfpEventAwardAuditDaoImpl extends GenericDaoImpl<RfpEventAwardAudit, String> implements RfpEventAwardAuditDao {

	public static final Logger LOG = LogManager.getLogger(RftEventAwardAuditDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfpEventAwardAudit (r.id, ab, r.actionDate, r.description, r.fileName, r.hasSnapshot, r.hasExcelSnapshot) from RfpEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteDocumentsByRfpAuditId(String id) {
		final Query query = getEntityManager().createQuery("UPDATE RfpEventAward r SET r.fileName = null , r.fileData = null where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfpEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct r from RfpEventAwardAudit r left outer join r.actionBy ab where r.event.id =:eventId and r.buyer.id=:tenantId order by r.actionDate desc");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", loggedInUserTenantId);
		try {
			List<RfpEventAwardAudit> rfpEventAwardAuditList = query.getResultList();
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
