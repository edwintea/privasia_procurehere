package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventTimeLineDao;
import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfaEventTimeLine;

/**
 * @author Teja
 */
@Repository
public class RfaEventTimeLineDaoImpl extends GenericDaoImpl<RfaEventTimeLine, String> implements RfaEventTimeLineDao {

	@Override
	public void deleteTimeline(String id) {
		final Query query = getEntityManager().createQuery("delete from RfaEventTimeLine t where t.event.id = :eventId");
		query.setParameter("eventId", id);
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventTimeLine> getRfaEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("from RfaEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventTimeline> getPlainRfaEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventTimeline(t.activityDate, t.activity, t.description) from RfaEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

}
