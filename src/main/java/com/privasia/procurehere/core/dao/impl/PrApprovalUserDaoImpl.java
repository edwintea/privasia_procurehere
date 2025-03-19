package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PrApprovalUserDao;
import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrApprovalUser;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author ravi
 */
@Repository
public class PrApprovalUserDaoImpl extends GenericDaoImpl<PrApprovalUser, String> implements PrApprovalUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<PrApprovalUser> findPrApprovelUserForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from PrApprovalUser usr where usr.approvalStatus= :approvalStatus and usr.reminderCount > 0 and usr.nextReminderTime < :now and usr.approval.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrApproval> findPrApprovelLevelsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from PrApproval usr left outer join usr.approvalUsers au where au.approvalStatus= :approvalStatus and au.reminderCount > 0 and au.nextReminderTime < :now and usr.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrApproval> findPrApprovelEscalationsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from PrApproval usr left outer join usr.approvalUsers au left outer join usr.pr p  where au.approvalStatus = :approvalStatus and au.reminderCount = 0  and usr.active = :active and usr.escalated = false and p.notifyEventOwner = :notifyEventOwner");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("active", true);
		query.setParameter("notifyEventOwner", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public void updatePrApproval(PrApproval level) {
		getEntityManager().persist(level);
	}

}
