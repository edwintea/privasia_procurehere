package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.dao.RfpEvaluatorUserDao;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author RT-Kapil
 */

@Repository
public class RfpEvaluatorUserDaoImpl extends GenericDaoImpl<RfpEvaluatorUser, String> implements RfpEvaluatorUserDao {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		final Query query = getEntityManager().createQuery("from RfpEvaluatorUser eu where eu.envelope.id =:envelopeId and eu.user.id=:userId");
		query.setParameter("envelopeId", envelopId);
		query.setParameter("userId", userId);
		List<RfpEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		final Query query = getEntityManager().createQuery("select new User(eu.user.id, eu.user.name, eu.user.communicationEmail, eu.user.emailNotifications, eu.user.tenantId) from RfpEnvelop e left outer join e.evaluators eu where e.rfxEvent.id=:eventId");
		query.setParameter("eventId", eventId);
		List<User> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser) {

		StringBuffer hsql = new StringBuffer("from RfpEvaluatorUser eu where eu.envelope.id =:envelopeId ");
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
	public RfpEvaluatorUser getEvaluationDocument(String id) {
		final Query query = getEntityManager().createQuery("from RfpEvaluatorUser eu where eu.id=:id");
		query.setParameter("id", id);
		List<RfpEvaluatorUser> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

}
