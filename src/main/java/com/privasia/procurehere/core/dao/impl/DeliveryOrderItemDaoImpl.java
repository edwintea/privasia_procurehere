package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.DeliveryOrderItemDao;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository("deliveryOrderItemDao")
public class DeliveryOrderItemDaoImpl extends GenericDaoImpl<DeliveryOrderItem, String> implements DeliveryOrderItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryOrderItem> getAllDoItemByDoId(String doId) {
		StringBuilder hsql = new StringBuilder("select distinct di from DeliveryOrderItem di left outer join fetch di.unit u left outer join fetch di.deliverOrder d where d.id = :doId order by di.level, di.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("doId", doId);
		List<DeliveryOrderItem> doItemList = query.getResultList();
		return doItemList;
	}

	@Override
	public void deleteItemsByIds(List<String> ids, String deliveryId) {
		final Query query = getEntityManager().createQuery("delete from DeliveryOrderItem it where it.id not in (:ids) and it.deliverOrder.id = :deliveryId");
		query.setParameter("ids", ids);
		query.setParameter("deliveryId", deliveryId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DeliveryOrderItem getDoItemById(String itemId) {
		StringBuilder hsql = new StringBuilder("select distinct di from DeliveryOrderItem di left outer join fetch di.unit u left outer join fetch di.deliverOrder d where di.id = :itemId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("itemId", itemId);
		List<DeliveryOrderItem> doItemList = query.getResultList();
		if (CollectionUtil.isNotEmpty(doItemList)) {
			return doItemList.get(0);
		}
		return null;
	}

}
