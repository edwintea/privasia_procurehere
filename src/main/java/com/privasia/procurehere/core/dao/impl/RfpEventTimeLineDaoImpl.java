package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventTimeLineDao;
import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfpEventTimeLine;

/**
 * @author Teja
 */
@Repository
public class RfpEventTimeLineDaoImpl extends GenericDaoImpl<RfpEventTimeLine, String> implements RfpEventTimeLineDao {

	@Override
	public void deleteTimeline(String id) {
		final Query query = getEntityManager().createQuery("delete from RfpEventTimeLine t where t.event.id = :eventId");
		query.setParameter("eventId", id);
		query.executeUpdate();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventTimeLine> getRfpEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("from RfpEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTimeline> getPlainRfpEventTimeline(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventTimeline(t.activityDate, t.activity, t.description) from RfpEventTimeLine t where t.event.id = :eventId order by t.activityDate");
		query.setParameter("eventId", id);
		return query.getResultList();
	}

}
