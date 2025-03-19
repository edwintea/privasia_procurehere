package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpCqItemDao;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.enums.CqType;

/**
 * @author Ravi
 */
@Repository
public class RfpCqItemDaoImpl extends GenericCqItemDaoImpl<RfpCqItem, String> implements RfpCqItemDao {

	@Override
	public int CountCqItemsbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RfpCqItem r where r.rfxEvent.id =:eventId and r.optional = true");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpCq> rfpMandatoryCqNamesbyEventId(String eventId) {
		// StringBuilder hsql = new StringBuilder("select new RfpCq(cq.name, sum(r.optional)) from RfpCqItem r inner
		// join r.cq cq where r.rfxEvent.id =:eventId and r.order <> 0 group by cq.name, r.optional");
		StringBuilder hsql = new StringBuilder("select new RfpCq(cq.name, count(r)) from RfpCqItem r inner join r.cq cq where r.rfxEvent.id =:eventId and r.cqType <> :linkType and r.order <> 0 and r.optional = true group by cq.name");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<RfpCq> rfpCqItemList = query.getResultList();
		return rfpCqItemList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findAllSupplierAttachRequiredId(String eventId) {
		final Query query = getEntityManager().createQuery("select a.id from RfpCqItem a where a.rfxEvent.id = :eventId and a.cqType <> :linkType and a.order <> 0 and a.isSupplierAttachRequired = true");
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<String> list = query.getResultList();
		return list;
	}
}
