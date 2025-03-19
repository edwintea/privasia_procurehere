package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FinancePoAuditDao;
import com.privasia.procurehere.core.entity.FinancePoAudit;

@Repository
public class FinancePoAuditDaoImpl extends GenericDaoImpl<FinancePoAudit, String> implements FinancePoAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<FinancePoAudit> getAuditForFinancePo(String prId) {
		StringBuilder hsql = new StringBuilder("from FinancePoAudit p left outer join fetch p.po po left outer join fetch p.actionBy cb where po.po.id = :id ORDER BY ACTION_DATE DESC");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", prId);
		return query.getResultList();
	}

}
