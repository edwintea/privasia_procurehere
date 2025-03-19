package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfaBqItemDaoImpl extends GenericBqItemDaoImpl<RfaBqItem, String> implements RfaBqItemDao {

	private static final Logger LOG = LogManager.getLogger(RfaBqItemDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBqItem getBqItemByBqIItemId(String itemId, String supplierId) {
		try {
			LOG.info("getBqItem ::: " + itemId);
			final Query query = getEntityManager().createQuery("from RfaSupplierBqItem rb inner join fetch rb.supplier as s where rb.id =:itemId and s.id= :supplierId");
			query.setParameter("itemId", itemId);
			query.setParameter("supplierId", supplierId);
			List<RfaSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				LOG.info("getBqItemByBqIItemId ::: " + uList);
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting SupplierBQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public Integer getCountOfAllRfaBqItemByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(bq) from RfaBqItem bq where bq.rfxEvent.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public boolean checkIfBqItemExists(String eventId) {
		final Query query = getEntityManager().createQuery("select count(bq) from RfaBqItem bq where bq.rfxEvent.id =:id and bq.parent is  not null");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBqSectionIdByBqLevelAndOrder(Integer level, Integer order, String eventId) {
		final Query query = getEntityManager().createQuery("select bq.id from RfaBqItem bq where bq.level =:level and bq.order =:order and bq.rfxEvent.id =:eventId");
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
