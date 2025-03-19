package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaCqItemDao;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.enums.CqType;

/**
 * @author RT-Kapil
 */
@Repository
public class RfaCqItemDaoImpl extends GenericCqItemDaoImpl<RfaCqItem, String> implements RfaCqItemDao {

	// select count(ci) as tcount from RfaEvent r left outer join fetch r.cqs c inner join fetch c.cqItems ci where r.id
	// =:eventId and ci.optional is true
	@Override
	public int CountCqItemsbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RfaCqItem r where r.rfxEvent.id =:eventId and r.optional = true");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaCq> rfaMandatoryCqNamesbyEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select new RfaCq(cq.name, count(r)) from RfaCqItem r inner join r.cq cq where r.rfxEvent.id =:eventId and r.order <> 0 and r.cqType <> :linkType and r.optional = true group by cq.name");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<RfaCq> cqItemList = query.getResultList();
		return cqItemList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findAllSupplierAttachRequiredId(String eventId) {
		final Query query = getEntityManager().createQuery("select a.id from RfaCqItem a where a.rfxEvent.id = :eventId and a.cqType <> :linkType and a.order <> 0 and a.isSupplierAttachRequired = true");
		query.setParameter("eventId", eventId);
		query.setParameter("linkType", CqType.DOCUMENT_DOWNLOAD_LINK);
		List<String> list = query.getResultList();
		return list;
	}
}
