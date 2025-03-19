package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.GoodsReceiptNoteItemDao;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;
import com.privasia.procurehere.core.enums.GrnStatus;

@Repository("goodsReceiptNoteItemDao")
public class GoodsReceiptNoteItemDaoImpl extends GenericDaoImpl<GoodsReceiptNoteItem, String> implements GoodsReceiptNoteItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNoteItem> getAllGrnItemByGrnId(String grnId) {
		StringBuilder hsql = new StringBuilder("select distinct di from GoodsReceiptNoteItem di left outer join fetch di.unit u left outer join fetch di.goodsReceiptNote d where d.id = :grnId order by di.level, di.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("grnId", grnId);
		List<GoodsReceiptNoteItem> doItemList = query.getResultList();
		return doItemList;
	}

	@Override
	public BigDecimal findReceivedQuantitiesByPoAndPoItemId(String poId, String poItemId) {
		StringBuilder hsql = new StringBuilder("select sum (di.receivedQuantity) from GoodsReceiptNoteItem di left outer join di.goodsReceiptNote g where di.po.id = :poId and di.poItem.id = :poItemId and di.parent is not null and g.status = :status");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		query.setParameter("poItemId", poItemId);
		query.setParameter("status", GrnStatus.RECEIVED);
		return (BigDecimal) query.getSingleResult();
	}

	@Override
	public void deleteItems(List<String> ids, String grnId) {
		final Query query = getEntityManager().createQuery("delete from GoodsReceiptNoteItem e where e.id not in (:ids) and  e.goodsReceiptNote.id = :grnId ");
		query.setParameter("ids", ids);
		query.setParameter("grnId", grnId);
		query.executeUpdate();
	}
}
