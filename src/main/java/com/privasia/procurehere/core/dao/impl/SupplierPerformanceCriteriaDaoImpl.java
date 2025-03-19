/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceCriteriaDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;

/**
 * @author Jayshree
 *
 */
@Repository
public class SupplierPerformanceCriteriaDaoImpl extends GenericDaoImpl<SupplierPerformanceCriteria, String> implements SupplierPerformanceCriteriaDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceCriteria> getSPCriteriaByFormIdAndUserId(String formId, String evalUserId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierPerformanceCriteria a join a.evaluationUser eval where a.form.id =:formId and eval.id =:userId order by a.level, a.order");
		query.setParameter("formId", formId);
		query.setParameter("userId", evalUserId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceCriteria> getSPCriteriaForAppByFormIdAndUserId(String formId, String userId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierPerformanceCriteria a left outer join a.evaluationUser eu left outer join eu.evaluationApprovals apps left outer join apps.approvalUsers au where a.form.id = :formId and (eu.evaluator.id = :userId or au.user.id = :userId) order by a.level, a.order");
		query.setParameter("formId", formId);
		query.setParameter("userId", userId);
		return query.getResultList();
	}

}
