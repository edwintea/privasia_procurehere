package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceAuditDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;

/*
 * @author anshul
 */
@Repository
public class SupplierPerformanceAuditDaoImpl extends GenericDaoImpl<SupplierPerformanceAudit, String> implements SupplierPerformanceAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormId(String formId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.hasSnapshot) from SupplierPerformanceAudit pa left outer join pa.actionBy u where pa.form.id = :formId order by pa.actionDate");
		query.setParameter("formId", formId);
		List<SupplierPerformanceAudit> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForOwner(String formId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.ownerSnapshot) from SupplierPerformanceAudit pa left outer join pa.actionBy u where pa.form.id = :formId and pa.owner =:owner order by pa.actionDate");
		query.setParameter("formId", formId);
		query.setParameter("owner", Boolean.TRUE);
		List<SupplierPerformanceAudit> list = query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForEvaluater(String formId, String evaluatorUserId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.evaluatorSnapshot) from SupplierPerformanceAudit pa left outer join pa.actionBy u where pa.form.id = :formId and pa.evaluator =:evaluator and  (pa.evaluatorUser.id = :evaluatorUserId  or pa.evaluatorUser is null ) order by pa.actionDate");
		query.setParameter("formId", formId);
		query.setParameter("evaluatorUserId", evaluatorUserId);
		query.setParameter("evaluator", Boolean.TRUE);
		List<SupplierPerformanceAudit> list = query.getResultList();
		return list;
	}

	
	@SuppressWarnings("unchecked")
	@Override	
	public List<SupplierPerformanceAudit> getSupplierPerformanceAuditByFormIdForApprover(String formId, String evaluatorUserId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.approverSnapshot) from SupplierPerformanceAudit pa left outer join pa.actionBy u where pa.form.id = :formId and pa.approver =:approver and  (pa.evaluatorUser.id = :evaluatorUserId  or pa.evaluatorUser is null ) order by pa.actionDate");
		query.setParameter("formId", formId);
		query.setParameter("evaluatorUserId", evaluatorUserId);
		query.setParameter("approver", Boolean.TRUE);
		List<SupplierPerformanceAudit> list = query.getResultList();
		return list;
	}
}
