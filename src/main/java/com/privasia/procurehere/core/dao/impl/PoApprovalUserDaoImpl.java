/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoApprovalUserDao;
import com.privasia.procurehere.core.entity.PoApproval;
import com.privasia.procurehere.core.entity.PoApprovalUser;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author Jayshree
 *
 */
@Repository
public class PoApprovalUserDaoImpl extends GenericDaoImpl<PoApprovalUser, String> implements PoApprovalUserDao {

	
	@SuppressWarnings("unchecked")
	@Override
	public List<PoApproval> findPoApprovalEscalationsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from PoApproval usr left outer join usr.approvalUsers au left outer join usr.po p where au.approvalStatus = :approvalStatus and au.reminderCount = 0  and usr.active = :active and usr.escalated = false and p.notifyEventOwner = :notifyEventOwner");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("active", true);
		query.setParameter("notifyEventOwner", Boolean.TRUE);
		return query.getResultList();
	}
	
	@Override
	public void updatePoApproval(PoApproval level) {
		getEntityManager().persist(level);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<PoApproval> findPoApprovalLevelsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from PoApproval usr left outer join usr.approvalUsers au where au.approvalStatus= :approvalStatus and au.reminderCount > 0 and au.nextReminderTime < :now and usr.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);
		return query.getResultList();
	}

}
