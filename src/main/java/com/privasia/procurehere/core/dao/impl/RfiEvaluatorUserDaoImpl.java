package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEvaluatorUserDao;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */

@Repository
public class RfiEvaluatorUserDaoImpl extends GenericDaoImpl<RfiEvaluatorUser, String> implements RfiEvaluatorUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public RfiEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		final Query query = getEntityManager().createQuery("from RfiEvaluatorUser eu where eu.envelope.id =:envelopeId and eu.user.id=:userId");
		query.setParameter("envelopeId", envelopId);
		query.setParameter("userId", userId);
		List<RfiEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		final Query query = getEntityManager().createQuery("select new User(eu.user.id, eu.user.name, eu.user.communicationEmail, eu.user.emailNotifications, eu.user.tenantId) from RfiEnvelop e left outer join e.evaluators eu where e.rfxEvent.id=:eventId");
		query.setParameter("eventId", eventId);
		List<User> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser) {
		StringBuffer hsql = new StringBuffer("from RfiEvaluatorUser eu where eu.envelope.id =:envelopeId ");
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
	public RfiEvaluatorUser getEvaluationDocument(String id) {
		final Query query = getEntityManager().createQuery("from RfiEvaluatorUser eu where eu.id =:id");
		query.setParameter("id", id);
		List<RfiEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}
	

}
