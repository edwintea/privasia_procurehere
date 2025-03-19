package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Teja
 */
@Repository
public class RfaEventAuditDaoImpl extends GenericDaoImpl<RfaEventAudit, String> implements RfaEventAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventAudit> getRfaEventAudit(String eventId) {
		// final Query query = getEntityManager().createQuery("from
		// RfaEventAudit e left outer join fetch e.actionBy as a left outer join
		// fetch e.buyer b where e.event.id = :eventId order by e.actionDate");
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaEventAudit(e.id, e.actionBy, e.event, b, e.actionDate, e.action, e.description) from RfaEventAudit e left outer join e.supplier as sup left outer join e.actionBy as a left outer join e.buyer b where e.event.id = :eventId order by e.actionDate");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventAudit> getRfaEventAuditForSupplier(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaEventAudit(e.id, e.actionBy, e.event, e.actionDate, e.action, e.description) from RfaEventAudit e left outer join e.actionBy as a  where e.event.id = :eventId and e.supplier.id = :supplierId order by e.actionDate");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Date findUnMaskigDateForActionByAndAction(String actionBy, AuditActionType action, String eventId) {
		final Query query = getEntityManager().createQuery("select e  from RfaEventAudit e  where e.actionBy.id = :actionBy and e.action = :action and e.event.id= :eventId");
		query.setParameter("actionBy", actionBy);
		query.setParameter("action", action);
		query.setParameter("eventId", eventId);
		List<RfaEventAudit> audit = query.getResultList();
		if (CollectionUtil.isNotEmpty(audit)) {
			return audit.get(0).getActionDate();
		} else {
			return null;
		}
	}
}
