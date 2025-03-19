package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiCqItemDao;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.enums.CqType;

/**
 * @author Ravi
 */
@Repository
public class RfiCqItemDaoImpl extends GenericCqItemDaoImpl<RfiCqItem, String> implements RfiCqItemDao {

	@Override
	public int CountCqItemsbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RfiCqItem r where r.rfxEvent.id =:eventId and r.optional = true");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiCq> rfiMandatoryCqNamesbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select new RfiCq(cq.name, count(r)) from RfiCqItem r inner join r.cq cq where r.rfxEvent.id =:eventId and r.cqType <> :linkType and r.order <> 0 and r.optional = true group by cq.name");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<RfiCq> cqItemList = query.getResultList();
		return cqItemList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findAllSupplierAttachRequiredId(String eventId) {
		final Query query = getEntityManager().createQuery("select a.id from RfiCqItem a where a.rfxEvent.id = :eventId and a.cqType <> :linkType and a.order <> 0 and a.isSupplierAttachRequired = true");
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<String> list = query.getResultList();
		return list;
	}
}
