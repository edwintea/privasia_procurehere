package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.RfiSorDao;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.Sor;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class RfiSorDaoImpl extends GenericSorDaoImpl<RfiEventSor, String> implements RfiSorDao {
    @Override
    @SuppressWarnings("unchecked")
    public List<Sor> findSorbyEventId(String eventId) {
        final Query query = getEntityManager().createQuery("select distinct a from RfiEventSor a inner join fetch a.rfxEvent sp left outer join fetch a.sorItems bi where sp.id = :id order by a.sorOrder, a.createdDate");
        query.setParameter("id", eventId);
        return query.getResultList();
    }

    @Override
    public Integer getCountOfSorByEventId(String eventId) {
        final Query query = getEntityManager().createQuery("select count(bq) from RfiEvent e inner join e.eventSors bq  where e.id =:id");
        query.setParameter("id", eventId);
        Integer count = ((Number) query.getSingleResult()).intValue();
        return count;
    }

    @Override
    public List<String> rfiSorNamesByEventId(String eventId) {
        final Query query = getEntityManager().createQuery("select a.name from RfiEventSor a where a.rfxEvent.id = :eventId ");
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getNotSectionAddedRfiSorIdsByEventId(String eventId) {
        final Query query = getEntityManager().createQuery("select bq1.name from RfiEventSor bq1 where bq1.rfxEvent.id = :eventid and bq1.id not in (select bq.id from RfiEventSor bq inner join bq.sorItems item where bq.rfxEvent.id = :eventid and item.order > 0 group by bq.id having count(item) > 0)");
        query.setParameter("eventid", eventId);
        return query.getResultList();
    }

    @Override
    public List<String> getNotSectionItemAddedRfiSorIdsByEventId(String eventId) {
        final Query query = getEntityManager().createQuery("select bq1.name from RfiSorItem i inner join i.sor bq1 where i.rfxEvent.id = :eventid and i.parent is null and  (select count(child.id) from  RfiSorItem child where child.parent.id = i.id ) = 0");
        query.setParameter("eventid", eventId);
        return query.getResultList();
    }
}
