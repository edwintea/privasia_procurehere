package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEvaluatorUserDao;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */

@Repository
public class RfaEvaluatorUserDaoImpl extends GenericDaoImpl<RfaEvaluatorUser, String> implements RfaEvaluatorUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		final Query query = getEntityManager().createQuery("from RfaEvaluatorUser eu where eu.envelope.id =:envelopeId and eu.user.id=:userId");
		query.setParameter("envelopeId", envelopId);
		query.setParameter("userId", userId);
		List<RfaEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		final Query query = getEntityManager().createQuery("select new User(eu.user.id, eu.user.name, eu.user.communicationEmail, eu.user.emailNotifications, eu.user.tenantId) from RfaEnvelop e left outer join e.evaluators eu where e.rfxEvent.id=:eventId");
		query.setParameter("eventId", eventId);
		List<User> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User logedInUser) {

		StringBuffer hsql = new StringBuffer("from RfaEvaluatorUser eu where eu.envelope.id =:envelopeId ");
		if (logedInUser != null) {
			hsql.append(" and eu.user.id=:userId");
		}

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("envelopeId", evelopId);
		if (logedInUser != null) {
			query.setParameter("userId", logedInUser.getId());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvaluatorUser getEvaluationDocument(String id) {
		final Query query = getEntityManager().createQuery("from RfaEvaluatorUser eu where eu.id =:id");
		query.setParameter("id", id);
		List<RfaEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

}
