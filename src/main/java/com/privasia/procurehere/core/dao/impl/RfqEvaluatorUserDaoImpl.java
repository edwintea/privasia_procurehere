package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEvaluatorUserDao;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */

@Repository
public class RfqEvaluatorUserDaoImpl extends GenericDaoImpl<RfqEvaluatorUser, String> implements RfqEvaluatorUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public RfqEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		final Query query = getEntityManager().createQuery("from RfqEvaluatorUser eu where eu.envelope.id =:envelopeId and eu.user.id=:userId");
		query.setParameter("envelopeId", envelopId);
		query.setParameter("userId", userId);
		List<RfqEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		final Query query = getEntityManager().createQuery("select new User(eu.user.id, eu.user.name, eu.user.communicationEmail, eu.user.emailNotifications, eu.user.tenantId) from RfqEnvelop e left outer join e.evaluators eu where e.rfxEvent.id=:eventId");
		query.setParameter("eventId", eventId);
		List<User> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser) {
		StringBuffer hsql = new StringBuffer("from RfqEvaluatorUser eu where eu.envelope.id =:envelopeId ");
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
	public RfqEvaluatorUser getEvaluationDocument(String id) {
		final Query query = getEntityManager().createQuery("from RfqEvaluatorUser eu where eu.id =:id");
		query.setParameter("id", id);
		List<RfqEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}
}
