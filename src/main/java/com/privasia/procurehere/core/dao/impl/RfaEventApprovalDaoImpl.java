package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventApprovalDao;
import com.privasia.procurehere.core.entity.RfaEventApproval;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author ravi
 */
@Repository
public class RfaEventApprovalDaoImpl extends GenericEventApprovalDaoImpl<RfaEventApproval, String> implements RfaEventApprovalDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventApproval> findRfaApprovelEscalationsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from RfaEventApproval usr left outer join usr.approvalUsers au left outer join usr.event e where au.approvalStatus = :approvalStatus and au.reminderCount = 0  and usr.active = :active and usr.escalated = false and e.notifyEventOwner = :notifyEventOwner");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("active", true);
		query.setParameter("notifyEventOwner", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public void updateRfaApproval(RfaEventApproval level) {
		getEntityManager().persist(level);
	}
}
