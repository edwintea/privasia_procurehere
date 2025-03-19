package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEventApprovalDao;
import com.privasia.procurehere.core.entity.RfiEventApproval;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author ravi
 */
@Repository
public class RfiEventApprovalDaoImpl extends GenericEventApprovalDaoImpl<RfiEventApproval, String> implements RfiEventApprovalDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventApproval> findRfiApprovelEscalationsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from RfiEventApproval usr left outer join usr.approvalUsers au  left outer join usr.event e where au.approvalStatus = :approvalStatus and au.reminderCount = 0  and usr.active = :active and usr.escalated = false and e.notifyEventOwner = :notifyEventOwner");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("active", true);
		query.setParameter("notifyEventOwner", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public void updateRfiApproval(RfiEventApproval level) {
		getEntityManager().persist(level);
	}

}
