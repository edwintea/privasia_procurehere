package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormApprovalUserRequestDao;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author ravi
 */
@Repository
public class SourcingFormApprovalUserRequestDaoImpl extends GenericDaoImpl<SourcingFormApprovalUserRequest, String> implements SourcingFormApprovalUserRequestDao {

	@Override
	public List<SourcingFormApprovalUserRequest> findRfsApprovelUserForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from SourcingFormApprovalUserRequest usr where usr.approvalStatus= :approvalStatus and usr.reminderCount > 0 and usr.nextReminderTime < :now and usr.approvalRequest.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormApprovalRequest> findRfsApprovelLevelsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from SourcingFormApprovalRequest usr left outer join usr.approvalUsersRequest au where au.approvalStatus= :approvalStatus and au.reminderCount > 0 and au.nextReminderTime < :now and usr.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormApprovalRequest> findRfsApprovelEscalationsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from SourcingFormApprovalRequest usr left outer join usr.approvalUsersRequest au left outer join usr.sourcingFormRequest s where au.approvalStatus = :approvalStatus and au.reminderCount = 0  and usr.active = :active and usr.escalated = false and s.notifyEventOwner = :notifyEventOwner");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("active", true);
		query.setParameter("notifyEventOwner", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public void updateSourcingFormApproval(SourcingFormApprovalRequest level) {
		getEntityManager().persist(level);
	}

}
