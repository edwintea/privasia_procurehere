package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEvaluatorUserDao;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */

@Repository
public class RftEvaluatorUserDaoImpl extends GenericDaoImpl<RftEvaluatorUser, String> implements RftEvaluatorUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public RftEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		final Query query = getEntityManager().createQuery("from RftEvaluatorUser eu where eu.envelope.id =:envelopeId and eu.user.id=:userId");
		query.setParameter("envelopeId", envelopId);
		query.setParameter("userId", userId);
		List<RftEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean findClosedStatusForLeadEvaluator(String envelopId) {
		final Query query = getEntityManager().createQuery("from RftEvaluatorUser eu where eu.envelope.id =:envelopeId and eu.evaluationStatus <> :evaluationStatus");
		query.setParameter("envelopeId", envelopId);
		query.setParameter("evaluationStatus", EvaluationStatus.COMPLETE);
		List<RftEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isEmpty(list) ? true : false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		final Query query = getEntityManager().createQuery("select new User(eu.user.id, eu.user.name, eu.user.communicationEmail, eu.user.emailNotifications, eu.user.tenantId) from RftEnvelop e left outer join e.evaluators eu where e.rfxEvent.id=:eventId");
		query.setParameter("eventId", eventId);
		List<User> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser) {
		StringBuffer hsql = new StringBuffer("from RftEvaluatorUser eu where eu.envelope.id =:envelopeId ");
		if (loginUser != null) {
			hsql.append(" and eu.user.id=:userId");
		}

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("envelopeId", evelopId);
		if (loginUser != null) {
			query.setParameter("userId", loginUser.getId());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvaluatorUser getEvaluationDocument(String id) {
		final Query query = getEntityManager().createQuery("from RftEvaluatorUser eu where eu.id =:id");
		query.setParameter("id", id);
		List<RftEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

}
