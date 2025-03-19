package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ContractApprovalUserDao;
import com.privasia.procurehere.core.entity.ContractApproval;
import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author anshul
 */
@Repository
public class ContractApprovalUserDaoImpl extends GenericDaoImpl<ContractApprovalUser, String> implements ContractApprovalUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractApprovalUser> findContractApprovelUserForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from ContractApprovalUser usr where usr.approvalStatus= :approvalStatus and usr.reminderCount > 0 and usr.nextReminderTime < :now and usr.approval.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractApproval> findContractApprovelLevelsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from ContractApproval usr left outer join usr.approvalUsers au where au.approvalStatus = :approvalStatus and au.reminderCount > 0 and au.nextReminderTime < :now and usr.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);
		return query.getResultList();
	}

	@Override
	public void updateContractApproval(ContractApproval level) {
		getEntityManager().persist(level);
	}

}
