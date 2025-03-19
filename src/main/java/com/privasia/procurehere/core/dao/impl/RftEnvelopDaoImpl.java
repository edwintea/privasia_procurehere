package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import com.privasia.procurehere.core.entity.RftEventSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Kapil
 */

@Repository
public class RftEnvelopDaoImpl extends GenericEnvelopDaoImpl<RftEnvelop, String> implements RftEnvelopDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventBq> getNotAssignedRftBqIdsByEventId(String eventId) {

		Query query = getEntityManager().createQuery("select b from RftEvent e left outer join e.eventBqs b where e.id =:id");
		query.setParameter("id", eventId);
		List<RftEventBq> bqList = query.getResultList();
		LOG.info("Total BQs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RftEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RftEventBq> assignedBqList = query.getResultList();

		LOG.info("Assigned BQs : " + assignedBqList.size());

		List<RftEventBq> returnList = new ArrayList<RftEventBq>();

		for (RftEventBq bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftCq> getNotAssignedRftCqIdsByEventId(String eventId) {

		Query query = getEntityManager().createQuery("select cq from RftEvent e left outer join e.cqs cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RftCq> cqList = query.getResultList();
		LOG.info("Total CQs : " + cqList.size());

		query = getEntityManager().createQuery("select distinct cq from RftEnvelop ev inner join ev.rfxEvent e left outer join ev.cqList cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RftCq> assignedCqList = query.getResultList();

		LOG.info("Assigned CQs : " + assignedCqList.size());

		List<RftCq> returnList = new ArrayList<RftCq>();

		for (RftCq cq : cqList) {
			if (!assignedCqList.contains(cq)) {
				returnList.add(cq);
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEnvelop getEmptyEnvelopByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select distinct ev from RftEvent e inner join e.rftEnvelop ev where e.id =:id");
		query.setParameter("id", eventId);
		List<RftEnvelop> envelope = query.getResultList();

		Integer count = 1;
		RftEnvelop envelop = new RftEnvelop();
		envelop.setEnvelopEmpty(count);
		if (CollectionUtil.isNotEmpty(envelope)) {
			for (RftEnvelop ev : envelope) {
				query = getEntityManager().createQuery("select (count(bq)+ count(cq) + count(sor)) from RftEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList as bq left outer join ev.cqList as cq left outer join ev.sorList as sor where e.id =:id and ev.id = :envId");
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
	public List<RftEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		StringBuilder hsql = new StringBuilder("select e from RftEnvelop re inner join re.evaluators e where re.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvaluatorUser getRftEvaluatorUserByUserIdAndEnvelopeId(String envelope, String userId) {
		final Query query = getEntityManager().createQuery("from RftEvaluatorUser r where r.envelope.id =:envelop and r.user.id= :userId");
		query.setParameter("envelop", envelope);
		query.setParameter("userId", userId);
		List<RftEvaluatorUser> uList = query.getResultList();
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
		final Query query = getEntityManager().createQuery("from RftEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RftEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RftEnvelop envelop : uList) {
				envelop.setBqList(null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeCqsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RftEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RftEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RftEnvelop envelop : uList) {
				envelop.setCqList(null);
			}
		}
	}

	@Override
	public int findCountPendingEnvelopse(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RftEnvelop r where r.rfxEvent.id =:eventId and r.evaluationStatus = 'PENDING'");
		query.setParameter("eventId", eventId);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new RftEnvelop(r.id, r.envelopTitle, r.envelopType, op.user.id, op.user.name, op.user.communicationEmail, op.user.emailNotifications, op.user.tenantId ) from RftEnvelop r left outer join r.openerUsers op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		return query.getResultList();
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RftEnvelop r where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	public RftEvent getEventbyEnvelopeId(String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct e from RftEnvelop ev inner join  ev.rfxEvent e where ev.id =:id");
		query.setParameter("id", envelopeId);
		List<RftEvent> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq.id from RftEnvelop a inner join a.cqList cq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqIdlistByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq.id from RftEnvelop a inner join a.bqList bq  where a.id  =:id");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RftEvaluatorUser r inner join r.envelope env inner join env.rfxEvent e where r.user.id =:leadUserId and env.id =:envelopId and e.id =:eventId ");
		query.setParameter("leadUserId", leadUserId);
		query.setParameter("envelopId", envelopId);
		query.setParameter("eventId", eventId);
		return ((long) query.getSingleResult()) > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getEnvelipeTitleById(String envelopeId, String eventType) {
		final Query query = getEntityManager().createQuery("select ev.envelopTitle from  " + StringUtils.capitalize((eventType.toLowerCase() + "Envelop")) + " ev where ev.id =:id");
		query.setParameter("id", envelopeId);
		return (String) query.getSingleResult();
	}

	@Override
	public RftEnvelop getRftEnvelopBySeqId(int seq, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ev from RftEnvelop ev where ev.rfxEvent.id =:eventId and ev.envelopSequence =:envelopSequence");
		query.setParameter("envelopSequence", seq);
		query.setParameter("eventId", eventId);

		return (RftEnvelop) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEnvelop> getEnvelopListByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftEnvelop(ev.id,ev.envelopTitle,ev.evaluationCompletedPrematurely ) from RftEnvelop ev  where ev.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		List<RftEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventBq> getBqIdlistByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RftEnvelop a inner join a.bqList bq where a.id  =:id order by bq.bqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftCq> getCqIdlistByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq from RftEnvelop a inner join a.cqList cq where a.id  =:id order by cq.cqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEnvelop getEvaluationDocument(String envelopId) {
		final Query query = getEntityManager().createQuery("from RftEnvelop eu where eu.id =:envelopeId");
		query.setParameter("envelopeId", envelopId);
		List<RftEnvelop> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	public List<RftEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RftEnvelop a inner join a.sorList bq where a.id  =:id order by bq.sorOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<RftEventSor> getNotAssignedSorIdsByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select b from RftEvent e left outer join e.eventSors b where e.id =:id");
		query.setParameter("id", eventId);
		List<RftEventSor> bqList = query.getResultList();
		LOG.info("Total SORs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RftEnvelop ev inner join ev.rfxEvent e left outer join ev.sorList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RftEventSor> assignedBqList = query.getResultList();

		LOG.info("Assigned SORs : " + assignedBqList.size());

		List<RftEventSor> returnList = new ArrayList<RftEventSor>();

		for (RftEventSor bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		for (RftEventSor sor : returnList) {
			LOG.info("Not Assigned SOR : " + sor.toLogString());
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSorsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RftEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RftEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RftEnvelop envelop : uList) {
				envelop.setSorList(null);
			}
		}
	}
}
