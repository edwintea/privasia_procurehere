package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfaEventBq;

@Repository
public class RfaBqDaoImpl extends GenericBqDaoImpl<RfaEventBq, String> implements RfaBqDao {
	@Override
	public Integer getCountOfRfaBqByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(bq) from RfaEvent e inner join e.eventBqs bq  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Bq> findRfaBqbyEventId(String eventId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		//final Query query = getEntityManager().createQuery("select distinct a from RfaEventBq a inner join fetch a.rfxEvent sp left outer join fetch a.bqItems bi where sp.id = :id order by a.createdDate, bi.level, bi.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfaEventBq a inner join fetch a.rfxEvent sp left outer join fetch a.bqItems bi where sp.id = :id order by a.bqOrder, a.createdDate");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getNotSectionAddedRfaBqIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select bq1.name from RfaEventBq bq1 where bq1.rfxEvent.id = :eventid and bq1.id not in (select bq.id from RfaEventBq bq inner join bq.bqItems item where bq.rfxEvent.id = :eventid and item.order > 0 group by bq.id having count(item) > 0)");
		query.setParameter("eventid", eventId);
		return query.getResultList();
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<String> rfaBqNamesByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select a.name from RfaEventBq a where a.rfxEvent.id = :eventId ");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotSectionItemAddedRfaBqIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select bq1.name from RfaBqItem i inner join i.bq bq1 where i.rfxEvent.id = :eventid and i.parent is null and  (select count(child.id) from  RfaBqItem child where child.parent.id = i.id ) = 0");
		query.setParameter("eventid", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Bq> findRfaBqbyEventIdByEnvelopeId(String eventId, String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaEventBq a inner join fetch a.rfxEvent sp  inner join sp.rfaEnvelop re left outer join fetch a.bqItems bi where sp.id = :id  and re.id  =:envelopeId order by a.createdDate, bi.level, bi.order");
		query.setParameter("id", eventId);
		query.setParameter("envelopeId", envelopeId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventBq> findBqbyEventIdAndEnvelopId(String eventId, String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaEventBq a inner join fetch a.rfxEvent sp inner join sp.rfaEnvelop re inner join re.bqList bq where re.id  =:envelopeId and sp.id = :id");
		query.setParameter("id", eventId);
		query.setParameter("envelopeId", envelopeId);
		return query.getResultList();
	}

}
