package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author RT-Kapil
 */

@Repository
public class RfpEnvelopDaoImpl extends GenericEnvelopDaoImpl<RfpEnvelop, String> implements RfpEnvelopDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventBq> getNotAssignedBqIdsByEventId(String eventId) {

		Query query = getEntityManager().createQuery("select b from RfpEvent e left outer join e.eventBqs b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpEventBq> bqList = query.getResultList();
		LOG.info("Total BQs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfpEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpEventBq> assignedBqList = query.getResultList();

		LOG.info("Assigned BQs : " + assignedBqList.size());

		List<RfpEventBq> returnList = new ArrayList<RfpEventBq>();

		for (RfpEventBq bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpCq> getNotAssignedRfpCqIdsByEventId(String eventId) {

		Query query = getEntityManager().createQuery("select cq from RfpEvent e left outer join e.cqs cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpCq> cqList = query.getResultList();
		LOG.info("Total CQs : " + cqList.size());

		query = getEntityManager().createQuery("select distinct cq from RfpEnvelop ev inner join ev.rfxEvent e left outer join ev.cqList cq where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpCq> assignedCqList = query.getResultList();

		List<RfpCq> returnList = new ArrayList<RfpCq>();

		for (RfpCq cq : cqList) {
			if (!assignedCqList.contains(cq)) {
				returnList.add(cq);
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEnvelop getEmptyEnvelopByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select distinct ev from RfpEvent e inner join e.rfxEnvelop ev where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpEnvelop> envelope = query.getResultList();

		Integer count = 1;
		RfpEnvelop envelop = new RfpEnvelop();
		envelop.setEnvelopEmpty(count);
		if (CollectionUtil.isNotEmpty(envelope)) {
			for (RfpEnvelop ev : envelope) {
				query = getEntityManager().createQuery("select (count(bq)+ count(cq)+ count(sor)) from RfpEnvelop ev inner join ev.rfxEvent e left outer join ev.bqList as bq left outer join ev.cqList as cq left outer join ev.sorList as sor where e.id =:id and ev.id = :envId");
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
	public List<RfpEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		StringBuilder hsql = new StringBuilder("select e from RfpEnvelop re inner join re.evaluators e where re.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvaluatorUser getRfpEvaluatorUserByUserIdAndEnvelopeId(String envelope, String userId) {
		final Query query = getEntityManager().createQuery("from RfpEvaluatorUser r where r.envelope.id =:envelop and r.user.id= :userId");
		query.setParameter("envelop", envelope);
		query.setParameter("userId", userId);
		List<RfpEvaluatorUser> uList = query.getResultList();
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
		final Query query = getEntityManager().createQuery("from RfpEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfpEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfpEnvelop envelop : uList) {
				envelop.setBqList(null);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSorsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfpEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfpEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfpEnvelop envelop : uList) {
				envelop.setBqList(null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeCqsFromEnvelops(String eventId) {
		final Query query = getEntityManager().createQuery("from RfpEnvelop r where r.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfpEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			for (RfpEnvelop envelop : uList) {
				envelop.setCqList(null);
			}
		}
	}

	@Override
	public int findCountPendingEnvelopse(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfpEnvelop r where r.rfxEvent.id =:eventId and r.evaluationStatus = 'PENDING'");
		query.setParameter("eventId", eventId);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfpEnvelop r where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		Number count = (Number) query.getSingleResult();
		return (count != null) ? count.intValue() : 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
//		final Query query = getEntityManager().createQuery("select distinct new RfpEnvelop(r.id, r.envelopTitle, r.envelopType, op.id, op.name, op.communicationEmail, op.tenantId ) from RfpEnvelop r left outer join r.opener op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType and op is not null");
		final Query query = getEntityManager().createQuery("select distinct new RfpEnvelop(r.id, r.envelopTitle, r.envelopType, op.user.id, op.user.name, op.user.communicationEmail, op.user.emailNotifications, op.user.tenantId ) from RfpEnvelop r left outer join r.openerUsers op where r.rfxEvent.id =:eventId and r.envelopType = :envelopType");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopType", EnvelopType.CLOSED);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfpEvent getEventbyEnvelopeId(String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct e from RfpEnvelop ev inner join  ev.rfxEvent e where ev.id =:id");
		query.setParameter("id", envelopeId);
		List<RfpEvent> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdlistByEnvelopId(String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct cq.id from RfpEnvelop a inner join a.cqList cq  where a.id  =:id");
		query.setParameter("id", envelopeId);
		return query.getResultList();
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfpEvaluatorUser r inner join r.envelope env inner join env.rfxEvent e where r.user.id =:leadUserId and env.id =:envelopId and e.id =:eventId ");
		query.setParameter("leadUserId", leadUserId);
		query.setParameter("envelopId", envelopId);
		query.setParameter("eventId", eventId);
		return ((long) query.getSingleResult()) > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventTeamMember(String eventId) {
		String sql = "SELECT u.USER_NAME  FROM PROC_USER u RIGHT OUTER JOIN PROC_RFP_TEAM  team ON  team.USER_ID = u.ID WHERE team.MEMBER_TYPE =:memberType AND team.EVENT_ID =:eventId";
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", TeamMemberType.Associate_Owner);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public RfpEnvelop getRfpEnvelopBySeq(int seq, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ev from RfpEnvelop ev where ev.rfxEvent.id =:eventId and ev.envelopSequence =:envelopSequence");
		query.setParameter("envelopSequence", seq);
		query.setParameter("eventId", eventId);

		return (RfpEnvelop) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEnvelop> getEnvelopListByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpEnvelop(ev.id,ev.envelopTitle,ev.evaluationCompletedPrematurely ) from RfpEnvelop ev  where ev.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		List<RfpEnvelop> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventBq> getBqsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RfpEnvelop a inner join a.bqList bq where a.id  =:id order by bq.bqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpCq> getCqsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq from RfpEnvelop a inner join a.cqList cq where a.id  =:id order by cq.cqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEnvelop getEvaluationDocument(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfpEnvelop eu where eu.id=:envelopId");
		query.setParameter("envelopId", envelopId);
		List<RfpEnvelop> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list)) ? list.get(0) : null;
	}

	@Override
	public List<RfpEventSor> getNotAssignedSorIdsByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select b from RfpEvent e left outer join e.eventSors b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpEventSor> bqList = query.getResultList();
		LOG.info("Total SORs : " + bqList.size());

		query = getEntityManager().createQuery("select distinct b from RfpEnvelop ev inner join ev.rfxEvent e left outer join ev.sorList b where e.id =:id");
		query.setParameter("id", eventId);
		List<RfpEventSor> assignedBqList = query.getResultList();

		LOG.info("Assigned SORs : " + assignedBqList.size());

		List<RfpEventSor> returnList = new ArrayList<RfpEventSor>();

		for (RfpEventSor bq : bqList) {
			if (!assignedBqList.contains(bq)) {
				returnList.add(bq);
			}
		}
		for (RfpEventSor sor : returnList) {
			LOG.info("Not Assigned SOR : " + sor.toLogString());
		}
		return returnList;
	}

	@Override
	public List<RfpEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq from RfpEnvelop a inner join a.sorList bq where a.id  =:id order by bq.sorOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}
}
