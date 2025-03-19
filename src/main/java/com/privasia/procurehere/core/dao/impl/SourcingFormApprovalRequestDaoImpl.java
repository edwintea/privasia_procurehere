package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormApprovalRequestDao;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Repository
public class SourcingFormApprovalRequestDaoImpl extends GenericDaoImpl<SourcingFormApprovalRequest, String> implements SourcingFormApprovalRequestDao {

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormApprovalRequest getSourcingFormActiveApproverById(String reqId) {
		final Query query = getEntityManager().createQuery("select distinct s from SourcingFormApprovalRequest s left outer join fetch s.approvalUsersRequest sfa  left outer join fetch s.sourcingFormRequest f where f.id = :reqId and s.active = :isActive");
		query.setParameter("reqId", reqId);
		query.setParameter("isActive", Boolean.TRUE);
		List<SourcingFormApprovalRequest> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormApprovalRequest findSourcingFormApprovalById(String id) {
		StringBuilder hql = new StringBuilder("from SourcingFormApprovalRequest sub where sub.id = :id");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		List<SourcingFormApprovalRequest> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

}