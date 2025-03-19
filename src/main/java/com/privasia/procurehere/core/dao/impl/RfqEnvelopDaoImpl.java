package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author RT-Kapil
 */

@Repository
public class RfqEnvelopDaoImpl extends GenericEnvelopDaoImpl<RfqEnvelop, String> implements RfqEnvelopDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventBq> getNotAssignedBqIdsByEventId(String eventId) {

		Query query = getEntityManager().createQuery("select b from RfqEvent e left outer join e.eventBqs b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqEventBq> bqList = query.getResultList();
		LOG.info("Total BQs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfqEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqEventBq> assignedBqList = query.getResultList();

		LOG.info("Assigned BQs : " + assignedBqList.size());

		List<RfqEventBq> returnList = new ArrayList<RfqEventBq>();

		for (RfqEventBq bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		for (RfqEventBq bq : returnList) {
			LOG.info("Not Assigned BQ : " + bq.toLogString());
		}
		return returnList;

	}

	@Override
	public List<RfqEventSor> getNotAssignedSorIdsByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select b from RfqEvent e left outer join e.eventSors b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqEventSor> bqList = query.getResultList();
		LOG.info("Total SORs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfqEnvelop ev inner join ev.rfxEvent e left outer join ev.sorList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqEventSor> assignedBqList = query.getResultList();

		LOG.info("Assigned SORs : " + assignedBqList.size());

		List<RfqEventSor> returnList = new ArrayList<RfqEventSor>();

		for (RfqEventSor bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		for (RfqEventSor sor : returnList) {
			LOG.info("Not Assigned SOR : " + sor.toLogString());
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqCq> getNotAssignedCqIdsByEventId(String eventId) {

		Query query = getEntityManager().createQuery("select cq from RfqEvent e left outer join e.cqs cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqCq> cqList = query.getResultList();

		query = getEntityManager().createQuery("select distinct cq from RfqEnvelop ev inner join ev.rfxEvent e left outer join ev.cqList cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqCq> assignedCqList = query.getResultList();

		List<RfqCq> returnList = new ArrayList<RfqCq>();

		for (RfqCq cq : cqList) {
			if (!assignedCqList.contains(cq)) {
				returnList.add(cq);
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfqEnvelop getEmptyEnvelopByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select distinct ev from RfqEvent e inner join e.rfxEnvelop ev where e.id =:id");
		query.setParameter("id", eventId);
		List<RfqEnvelop> envelope = query.getResultList();

		Integer count = 1;
		RfqEnvelop envelop = new RfqEnvelop();
		envelop.setEnvelopEmpty(count);
		if (CollectionUtil.isNotEmpty(envelope)) {
			for (RfqEnvelop ev : envelope) {
				query = getEntityManager().createQuery("select (count(bq)+ count(cq)+ count(sor)) from RfqEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList as bq left outer join ev.cqList as cq left outer join ev.sorList as sor where e.id =:id and ev.id = :envId");
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
	public List<RfqEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		StringBuilder hsql = new StringBuilder("select e from RfqEnvelop re inner join re.evaluators e where re.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfqEvaluatorUser getRfqEvaluatorUserByUserIdAndEnvelopeId(String envelope, String userId) {
		final Query query = getEntityManager().createQuery("from RfqEvaluatorUser r where r.envelope.id =:envelop and r.user.id= :userId");
		query.setParameter("envelop", envelope);
		query.setParameter("userId", userId);
		List<RfqEvaluatorUser> uList = query.getResultList();
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
		final Query query = getEntityManager().createQuery("from RfqEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfqEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfqEnvelop envelop : uList) {
				envelop.setBqList(null);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSorsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfqEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfqEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfqEnvelop envelop : uList) {
				envelop.setSorList(null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeCqsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfqEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfqEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfqEnvelop envelop : uList) {
				envelop.setCqList(null);
			}
		}
	}

	@Override
	public int findCountPendingEnvelopse(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfqEnvelop r where r.rfxEvent.id =:eventId and r.evaluationStatus = 'PENDING'");
		query.setParameter("eventId", eventId);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfqEnvelop r where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfqEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		// final Query query = getEntityManager().createQuery("select distinct new RfqEnvelop(r.id, r.envelopTitle,
		// r.envelopType, op.id, op.name, op.communicationEmail, op.tenantId ) from RfqEnvelop r left outer join
		// r.opener op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType and op is not null");
		final Query query = getEntityManager().createQuery("select distinct new RfqEnvelop(r.id, r.envelopTitle, r.envelopType, op.user.id, op.user.name, op.user.communicationEmail, op.user.emailNotifications, op.user.tenantId ) from RfqEnvelop r left outer join r.openerUsers op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfqEvent getEventbyEnvelopeId(String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct e from RfqEnvelop ev inner join  ev.rfxEvent e where ev.id =:id");
		query.setParameter("id", envelopeId);
		List<RfqEvent> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq.id from RfqEnvelop a inner join a.bqList bq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq.id from RfqEnvelop a inner join a.cqList cq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfqEvaluatorUser r inner join r.envelope env inner join env.rfxEvent e where r.user.id =:leadUserId and env.id =:envelopId and e.id =:eventId ");
		query.setParameter("leadUserId", leadUserId);
		query.setParameter("envelopId", envelopId);
		query.setParameter("eventId", eventId);
		return ((long) query.getSingleResult()) > 0;
	}

	@Override
	public RfqEnvelop getRfiEnvelopBySeq(int seq, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ev from RfqEnvelop ev where ev.rfxEvent.id =:eventId and ev.envelopSequence =:envelopSequence");
		query.setParameter("envelopSequence", seq);
		query.setParameter("eventId", eventId);

		return (RfqEnvelop) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEnvelop> getEnvelopListByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfqEnvelop(ev.id,ev.envelopTitle,ev.evaluationCompletedPrematurely ) from RfqEnvelop ev  where ev.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		List<RfqEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventBq> getBqsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RfqEnvelop a inner join a.bqList bq where a.id  =:id order by bq.bqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqCq> getCqIdlistByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq from RfqEnvelop a inner join a.cqList cq where a.id  =:id order by cq.cqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public RfqEnvelop getEvaluationDocument(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfqEnvelop eu where eu.id =:envelopeId");
		query.setParameter("envelopeId", envelopId);
		List<RfqEnvelop> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}


	@Override
	public List<RfqEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RfqEnvelop a inner join a.sorList bq where a.id  =:id order by bq.sorOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}
}
