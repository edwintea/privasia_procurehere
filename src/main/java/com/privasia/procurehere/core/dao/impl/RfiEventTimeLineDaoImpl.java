package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEventTimeLineDao;
import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfiEventTimeLine;

/**
 * @author Teja
 */
@Repository
public class RfiEventTimeLineDaoImpl extends GenericDaoImpl<RfiEventTimeLine, String> implements RfiEventTimeLineDao {

	@Override
	public void deleteTimeline(String id) {
		final Query query = getEntityManager().createQuery("delete from RfiEventTimeLine t where t.event.id = :eventId");
		query.setParameter("eventId", id);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventTimeLine> getRfiEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("from RfiEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTimeline> getPlainRfiEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventTimeline(t.activityDate, t.activity, t.description) from RfiEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

}
