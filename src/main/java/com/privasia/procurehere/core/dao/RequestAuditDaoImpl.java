package com.privasia.procurehere.core.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.RequestAudit;

@Repository
public class RequestAuditDaoImpl extends GenericDaoImpl<RequestAudit, String> implements RequestAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestAudit> RequestAuditById(String prId) {
		String hql = "select distinct new com.privasia.procurehere.core.entity.RequestAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description) from RequestAudit pa left outer join pa.actionBy u where pa.req.id = :prId order by pa.actionDate";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("prId", prId);
		return query.getResultList();

	}

}
