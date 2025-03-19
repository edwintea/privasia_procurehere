package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftCqItemDao;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.enums.CqType;

/**
 * @author Ravi
 */
@Repository
public class RftCqItemDaoImpl extends GenericCqItemDaoImpl<RftCqItem, String> implements RftCqItemDao {

	@Override
	public int CountCqItemsbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RftCqItem r where r.rfxEvent.id =:eventId and r.optional = true");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftCq> rftMandatoryCqNamesbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select new RftCq(cq.name, count(r)) from RftCqItem r inner join r.cq cq where r.rfxEvent.id =:eventId and r.cqType <> :linkType and r.order <> 0 and r.optional = true group by cq.name");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<RftCq> rftCqItemList = query.getResultList();
		return rftCqItemList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findAllSupplierAttachRequiredId(String eventId) {
		final Query query = getEntityManager().createQuery("select a.id from RftCqItem a where a.rfxEvent.id = :eventId and a.cqType <> :linkType and a.order <> 0 and a.isSupplierAttachRequired = true");
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<String> list = query.getResultList();
		return list;
	}

}
