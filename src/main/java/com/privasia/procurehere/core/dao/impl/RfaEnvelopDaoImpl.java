package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import com.privasia.procurehere.core.entity.RfaEventSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author RT-Kapil
 */

@Repository
public class RfaEnvelopDaoImpl extends GenericEnvelopDaoImpl<RfaEnvelop, String> implements RfaEnvelopDao {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@SuppressWarnings("unchecked")
	@Override

	public List<RfaEventBq> getNotAssignedRfaBqIdsByEventId(String eventId) {

		// final Query query = getEntityManager().createQuery("select distinct a.id from RfaEnvelop a inner join
		// a.rfaEvent sp where sp.id =:id");
		Query query = getEntityManager().createQuery("select b from RfaEvent e left outer join e.eventBqs b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaEventBq> bqList = query.getResultList();
		LOG.info("Total BQs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfaEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaEventBq> assignedBqList = query.getResultList();

		LOG.info("Assigned BQs : " + assignedBqList.size());

		List<RfaEventBq> returnList = new ArrayList<RfaEventBq>();

		for (RfaEventBq bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}

		for (RfaEventBq bq : returnList) {
			LOG.info("Not Assigned BQ : " + bq.toLogString());
		}
		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaCq> getNotAssignedRfaCqIdsByEventId(String eventId) {

		// final Query query = getEntityManager().createQuery("select distinct a.id from RftEnvelop a inner join
		// a.rftEvent sp where sp.id =:id");
		Query query = getEntityManager().createQuery("select cq from RfaEvent e left outer join e.cqs cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaCq> cqList = query.getResultList();
		LOG.info("Total CQs : " + cqList.size());

		query = getEntityManager().createQuery("select distinct cq from RfaEnvelop ev inner join ev.rfxEvent e left outer join ev.cqList cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaCq> assignedCqList = query.getResultList();

		LOG.info("Assigned CQs : " + assignedCqList.size());

		List<RfaCq> returnList = new ArrayList<RfaCq>();

		for (RfaCq cq : cqList) {
			if (!assignedCqList.contains(cq)) {
				returnList.add(cq);
			}
		}
		for (RfaCq cq : returnList) {
			LOG.info("Not Assigned CQ : " + cq.toLogString());
		}
		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEnvelop getEmptyEnvelopByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select distinct ev from RfaEvent e inner join e.rfaEnvelop ev where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaEnvelop> envelope = query.getResultList();

		Integer count = 1;
		RfaEnvelop envelop = new RfaEnvelop();
		envelop.setEnvelopEmpty(count);
		if (CollectionUtil.isNotEmpty(envelope)) {
			for (RfaEnvelop ev : envelope) {
				query = getEntityManager().createQuery("select (count(bq)+ count(cq) + count(sor)) from RfaEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList as bq left outer join ev.cqList as cq left outer join ev.sorList as sor where e.id =:id and ev.id = :envId");
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
	public List<RfaEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		StringBuilder hsql = new StringBuilder("select e from RfaEnvelop re inner join re.evaluators e where re.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvaluatorUser getRfaEvaluatorUserByUserIdAndEnvelopeId(String envelope, String userId) {

		final Query query = getEntityManager().createQuery("from RfaEvaluatorUser r where r.envelope.id =:envelop and r.user.id= :userId");
		query.setParameter("envelop", envelope);
		query.setParameter("userId", userId);
		List<RfaEvaluatorUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeBqsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfaEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfaEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfaEnvelop envelop : uList) {
				envelop.setBqList(null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeCqsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfaEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfaEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfaEnvelop envelop : uList) {
				envelop.setCqList(null);
			}
		}
	}

	@Override
	public int findCountPendingEnvelopse(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfaEnvelop r where r.rfxEvent.id =:eventId and r.evaluationStatus = 'PENDING'");
		query.setParameter("eventId", eventId);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfaEnvelop r where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		// final Query query = getEntityManager().createQuery("select distinct new RfaEnvelop(r.id, r.envelopTitle,
		// r.envelopType, op.id, op.name, op.communicationEmail, op.tenantId ) from RfaEnvelop r left outer join
		// r.opener op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType and op is not null");
		final Query query = getEntityManager().createQuery("select distinct new RfaEnvelop(r.id, r.envelopTitle, r.envelopType, op.user.id, op.user.name, op.user.communicationEmail, op.user.emailNotifications, op.user.tenantId ) from RfaEnvelop r left outer join r.openerUsers op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvent getEventbyEnvelopeId(String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct e from RfaEnvelop ev inner join  ev.rfxEvent e where ev.id =:id");
		query.setParameter("id", envelopeId);
		List<RfaEvent> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq.id from RfaEnvelop a inner join a.bqList bq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq.id from RfaEnvelop a inner join a.cqList cq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfaEvaluatorUser r inner join r.envelope env inner join env.rfxEvent e where r.user.id =:leadUserId and env.id =:envelopId and e.id =:eventId ");
		query.setParameter("leadUserId", leadUserId);
		query.setParameter("envelopId", envelopId);
		query.setParameter("eventId", eventId);
		return ((long) query.getSingleResult()) > 0;
	}

	@Override
	public RfaEnvelop getRfaEnvelopBySeq(int seq, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ev from RfaEnvelop ev where ev.rfxEvent.id =:eventId and ev.envelopSequence =:envelopSequence");
		query.setParameter("envelopSequence", seq);
		query.setParameter("eventId", eventId);

		return (RfaEnvelop) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEnvelop> getEnvelopListByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaEnvelop(ev.id,ev.envelopTitle,ev.evaluationCompletedPrematurely ) from RfaEnvelop ev  where ev.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		List<RfaEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEnvelopIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct a.id from RfaEnvelop a inner join a.rfxEvent ev  where ev.id  =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaCq> getCqsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq from RfaEnvelop a inner join a.cqList cq where a.id  =:id order by cq.cqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEnvelop getRfaEnvelopDocument(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfaEnvelop eu where eu.id =:envelopeId");
		query.setParameter("envelopeId", envelopId);
		List<RfaEnvelop> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	public List<RfaEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RfaEnvelop a inner join a.sorList bq where a.id  =:id order by bq.sorOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<RfaEventSor> getNotAssignedSorIdsByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select b from RfaEvent e left outer join e.eventSors b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaEventSor> bqList = query.getResultList();
		LOG.info("Total SORs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfaEnvelop ev inner join ev.rfxEvent e left outer join ev.sorList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfaEventSor> assignedBqList = query.getResultList();

		LOG.info("Assigned SORs : " + assignedBqList.size());

		List<RfaEventSor> returnList = new ArrayList<RfaEventSor>();

		for (RfaEventSor bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		for (RfaEventSor sor : returnList) {
			LOG.info("Not Assigned SOR : " + sor.toLogString());
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSorsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfaEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfaEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfaEnvelop envelop : uList) {
				envelop.setSorList(null);
			}
		}
	}

}
