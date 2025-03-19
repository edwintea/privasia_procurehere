package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEvaluatorDeclarationDao;
import com.privasia.procurehere.core.entity.RfiEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RftEvaluatorDeclaration;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */

@Repository
public class RfiEvaluatorDeclarationDaoImpl extends GenericDaoImpl<RfiEvaluatorDeclaration, String> implements RfiEvaluatorDeclarationDao {

	@SuppressWarnings("unchecked")
	@Override
	public boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId) {
		final Query query = getEntityManager().createQuery("from RfiEvaluatorDeclaration ed where ed.event.id =:eventId and ed.envelope.id =:envelopeId and ed.user.id  =:loggedInUser");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopeId", envelopId);
		query.setParameter("loggedInUser", loggedInUser);
		List<RftEvaluatorDeclaration> list = query.getResultList();
		return (CollectionUtil.isEmpty(list) ? true : false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvaluatorDeclaration> getAllEvaluatorDeclarationByEnvelopAndEventId(String envelopId, String eventId) {
		final Query query = getEntityManager().createQuery("from RfiEvaluatorDeclaration ed inner join fetch ed.user u where ed.event.id =:eventId and ed.envelope.id =:envelopeId order by ed.acceptedDate");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopeId", envelopId);
		return query.getResultList();
	}

}
