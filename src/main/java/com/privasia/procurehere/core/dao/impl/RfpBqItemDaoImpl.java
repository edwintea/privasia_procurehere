package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfpBqItemDaoImpl extends GenericBqItemDaoImpl<RfpBqItem, String> implements RfpBqItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfpSupplierBqItem a inner join fetch a.supplier as s inner join fetch a.bq as b left outer join fetch a.children as ch where s.id= :supplierId and b.id= :bqId and a.parent is null order by a.level, a.order");
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@Override
	public Integer getCountOfAllRftBqItemByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(bq) from RfpBqItem bq where bq.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBqSectionIdByBqLevelAndOrder(Integer level, Integer order, String eventId) {
		final Query query = getEntityManager().createQuery("select bq.id from RfpBqItem bq where bq.level =:level and bq.order =:order and bq.rfxEvent.id =:eventId");
		query.setParameter("level", level);
		query.setParameter("order", order);
		query.setParameter("eventId", eventId);
		List<String> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
