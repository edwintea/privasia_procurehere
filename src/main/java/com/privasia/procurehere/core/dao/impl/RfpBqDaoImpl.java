package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfpEventBq;

@Repository
public class RfpBqDaoImpl extends GenericBqDaoImpl<RfpEventBq, String> implements RfpBqDao {

	@Override
	public Integer getCountOfBqByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(bq) from RfpEvent e inner join e.eventBqs bq  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Bq> findBqbyEventId(String eventId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		//final Query query = getEntityManager().createQuery("select distinct a from RfpEventBq a inner join fetch a.rfxEvent sp left outer join fetch a.bqItems bi where sp.id = :id order by a.createdDate, bi.level, bi.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfpEventBq a inner join fetch a.rfxEvent sp left outer join fetch a.bqItems bi where sp.id = :id order by a.bqOrder, a.createdDate");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getNotSectionAddedRfpBqIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select bq1.name from RfpEventBq bq1 where bq1.rfxEvent.id = :eventid and bq1.id not in (select bq.id from RfpEventBq bq inner join bq.bqItems item where bq.rfxEvent.id = :eventid and item.order > 0 group by bq.id having count(item) > 0)");
		query.setParameter("eventid", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> rfpBqNamesByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select a.name from RfpEventBq a where a.rfxEvent.id = :eventId ");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotSectionItemAddedRfpBqIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select bq1.name from RfpBqItem i inner join i.bq bq1 where i.rfxEvent.id = :eventid and i.parent is null and  (select count(child.id) from  RfpBqItem child where child.parent.id = i.id ) = 0");
		query.setParameter("eventid", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Bq> findBqbyEventIdAndEnvelopeId(String eventId, String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfpEventBq a inner join fetch a.rfxEvent sp inner join sp.rfxEnvelop re  left outer join fetch a.bqItems bi where sp.id = :id  and re.id  =:envelopeId order by a.createdDate, bi.level, bi.order");
		query.setParameter("id", eventId);
		query.setParameter("envelopeId", envelopeId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventBq> findBqbyEventIdAndEnvelopId(String eventId, String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfpEventBq a inner join fetch a.rfxEvent sp inner join sp.rfxEnvelop re inner join re.bqList bq where re.id  =:envelopeId and sp.id = :id order by a.bqOrder");
		query.setParameter("id", eventId);
		query.setParameter("envelopeId", envelopeId);
		return query.getResultList();
	}
}
