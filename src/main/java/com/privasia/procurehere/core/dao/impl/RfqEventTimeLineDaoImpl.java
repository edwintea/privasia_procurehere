package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEventTimeLineDao;
import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfqEventTimeLine;

/**
 * @author Teja
 */
@Repository
public class RfqEventTimeLineDaoImpl extends GenericDaoImpl<RfqEventTimeLine, String> implements RfqEventTimeLineDao {

	@Override
	public void deleteTimeline(String id) {
		final Query query = getEntityManager().createQuery("delete from RfqEventTimeLine t where t.event.id = :eventId");
		query.setParameter("eventId", id);
		query.executeUpdate();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventTimeLine> getRfqEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("from RfqEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventTimeline> getPlainRfqEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventTimeline(t.activityDate, t.activity, t.description) from RfqEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}
}
