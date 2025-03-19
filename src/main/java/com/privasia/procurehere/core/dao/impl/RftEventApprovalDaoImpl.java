package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEventApprovalDao;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author ravi
 */
@Repository
public class RftEventApprovalDaoImpl extends GenericEventApprovalDaoImpl<RftEventApproval, String> implements RftEventApprovalDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventApproval> findRftApprovalEscalationsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from RftEventApproval usr left outer join usr.approvalUsers au left outer join usr.event e where au.approvalStatus = :approvalStatus and au.reminderCount = 0 and usr.active = :active and usr.escalated = false and e.notifyEventOwner = :notifyEventOwner");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("active", true);
		query.setParameter("notifyEventOwner", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public void updateRftApproval(RftEventApproval level) {
		getEntityManager().persist(level);
	}
}
