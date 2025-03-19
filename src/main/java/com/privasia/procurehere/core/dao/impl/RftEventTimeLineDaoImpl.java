package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEventTimeLineDao;
import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RftEventTimeLine;

/**
 * @author Teja
 */
@Repository
public class RftEventTimeLineDaoImpl extends GenericDaoImpl<RftEventTimeLine, String> implements RftEventTimeLineDao {

	@Override
	public void deleteTimeline(String id) {
		final Query query = getEntityManager().createQuery("delete from RftEventTimeLine t where t.event.id = :eventId");
		query.setParameter("eventId", id);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventTimeLine> getRftEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("from RftEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventTimeline> getPlainRftEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventTimeline(t.activityDate, t.activity, t.description) from RftEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

}
