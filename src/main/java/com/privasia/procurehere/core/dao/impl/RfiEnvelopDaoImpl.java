package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author RT-Kapil
 */

@Repository
public class RfiEnvelopDaoImpl extends GenericEnvelopDaoImpl<RfiEnvelop, String> implements RfiEnvelopDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiCq> getNotAssignedCqIdsByEventId(String eventId) {

		// final Query query = getEntityManager().createQuery("select distinct a.id from RftEnvelop a inner join
		// a.rftEvent sp where sp.id =:id");
		Query query = getEntityManager().createQuery("select cq from RfiEvent e left outer join e.cqs cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfiCq> cqList = query.getResultList();
		LOG.info("Total CQs : " + cqList.size());

		query = getEntityManager().createQuery("select distinct cq from RfiEnvelop ev inner join ev.rfxEvent e left outer join ev.cqList cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfiCq> assignedCqList = query.getResultList();

		LOG.info("Assigned CQs : " + assignedCqList.size());

		List<RfiCq> returnList = new ArrayList<RfiCq>();

		for (RfiCq cq : cqList) {
			if (!assignedCqList.contains(cq)) {
				returnList.add(cq);
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEnvelop getEmptyEnvelopByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select distinct ev from RfiEvent e inner join e.rfiEnvelop ev where e.id =:id");
		query.setParameter("id", eventId);
		List<RfiEnvelop> envelope = query.getResultList();

		Integer count = 1;
		RfiEnvelop envelop = new RfiEnvelop();
		envelop.setEnvelopEmpty(count);
		if (CollectionUtil.isNotEmpty(envelope)) {
			for (RfiEnvelop ev : envelope) {
				query = getEntityManager().createQuery("select (count(cq) + count(sor)) from RfiEnvelop ev inner join ev.rfxEvent e left outer join ev.cqList as cq left outer join ev.sorList as sor where e.id =:id and ev.id = :envId");
				query.setParameter("id", eventId);
				query.setParameter("envId", ev.getId());
				count = ((Number) query.getSingleResult()).intValue();
				ev.setEnvelopEmpty(count);
				if (count == 0) {
					return ev;
				}
			}
		}
		return envelop;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiCq> getAllCqByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfiCq cq inner join fetch cq.rftEnvelop re where re.id =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		StringBuilder hsql = new StringBuilder("select e from RfiEnvelop re inner join re.evaluators e where re.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEvaluatorUser getRfiEvaluatorUserByUserIdAndEnvelopeId(String envelope, String userId) {
		final Query query = getEntityManager().createQuery("from RfiEvaluatorUser r where r.envelope.id =:envelop and r.user.id= :userId");
		query.setParameter("envelop", envelope);
		query.setParameter("userId", userId);
		List<RfiEvaluatorUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeCqsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfiEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfiEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfiEnvelop envelop : uList) {
				envelop.setCqList(null);
			}
		}
	}

	@Override
	public int findCountPendingEnvelopse(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfiEnvelop r where r.rfxEvent.id =:eventId and r.evaluationStatus = 'PENDING'");
		query.setParameter("eventId", eventId);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfiEnvelop r where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		// final Query query = getEntityManager().createQuery("select distinct new RfiEnvelop(r.id, r.envelopTitle,
		// r.envelopType, op.id, op.name, op.communicationEmail, op.tenantId ) from RfiEnvelop r left outer join
		// r.opener op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType and op is not null");
		final Query query = getEntityManager().createQuery("select distinct new RfiEnvelop(r.id, r.envelopTitle, r.envelopType, op.user.id, op.user.name, op.user.communicationEmail, op.user.emailNotifications, op.user.tenantId ) from RfiEnvelop r left outer join r.openerUsers op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEvent getEventbyEnvelopeId(String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct e from RfiEnvelop ev inner join  ev.rfxEvent e where ev.id =:id");
		query.setParameter("id", envelopeId);
		List<RfiEvent> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq.id from RfiEnvelop a inner join a.cqList cq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfiEvaluatorUser r inner join r.envelope env inner join env.rfxEvent e where r.user.id =:leadUserId and env.id =:envelopId and e.id =:eventId ");
		query.setParameter("leadUserId", leadUserId);
		query.setParameter("envelopId", envelopId);
		query.setParameter("eventId", eventId);
		return ((long) query.getSingleResult()) > 0;
	}

	@Override
	public RfiEnvelop getRfiEnvelopBySeq(Integer seq, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ev from RfiEnvelop ev where ev.rfxEvent.id =:eventId and ev.envelopSequence =:envelopSequence");
		query.setParameter("envelopSequence", seq);
		query.setParameter("eventId", eventId);

		return (RfiEnvelop) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEnvelop> getEnvelopListByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfiEnvelop(ev.id,ev.envelopTitle,ev.evaluationCompletedPrematurely ) from RfiEnvelop ev  where ev.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		List<RfiEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiCq> getCqsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq from RfiEnvelop a inner join a.cqList cq where a.id  =:id order by cq.cqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEnvelop getEnvelopEvaluationDocument(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfiEnvelop eu where eu.id =:envelopeId");
		query.setParameter("envelopeId", envelopId);
		List<RfiEnvelop> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	public List<RfiEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RfiEnvelop a inner join a.sorList bq where a.id  =:id order by bq.sorOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<RfiEventSor> getNotAssignedSorIdsByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select b from RfiEvent e left outer join e.eventSors b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfiEventSor> bqList = query.getResultList();
		LOG.info("Total SORs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfiEnvelop ev inner join ev.rfxEvent e left outer join ev.sorList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfiEventSor> assignedBqList = query.getResultList();

		LOG.info("Assigned SORs : " + assignedBqList.size());

		List<RfiEventSor> returnList = new ArrayList<RfiEventSor>();

		for (RfiEventSor bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		for (RfiEventSor sor : returnList) {
			LOG.info("Not Assigned SOR : " + sor.toLogString());
		}
		return returnList;
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSorsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfiEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfiEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfiEnvelop envelop : uList) {
				envelop.setSorList(null);
			}
		}
	}

	@Override
	public String getEnvelipeTitleById(String envelopeId, String eventType) {
		final Query query = getEntityManager().createQuery("select ev.envelopTitle from  " + StringUtils.capitalize((eventType.toLowerCase() + "Envelop")) + " ev where ev.id =:id");
		query.setParameter("id", envelopeId);
		return (String) query.getSingleResult();
	}

}
