package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.InvoiceItemDao;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository("invoiceItemDao")
public class InvoiceItemDaoImpl extends GenericDaoImpl<InvoiceItem, String> implements InvoiceItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> getAllInvoiceItemByInvoiceId(String invoiceId) {
		StringBuilder hsql = new StringBuilder("select distinct ii from InvoiceItem ii left outer join fetch ii.unit u left outer join fetch ii.invoice i where i.id = :invoiceId order by ii.level, ii.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("invoiceId", invoiceId);
		List<InvoiceItem> invoiceItemList = query.getResultList();
		return invoiceItemList;
	}

	@Override
	public void deleteItemsByIds(List<String> ids, String invoiceId) {
		final Query query = getEntityManager().createQuery("delete from InvoiceItem it where it.id not in (:ids) and it.invoice.id =:invoiceId");
		query.setParameter("ids", ids);
		query.setParameter("invoiceId", invoiceId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public InvoiceItem getInvoiceItemById(String itemId) {
		StringBuilder hsql = new StringBuilder("select distinct di from InvoiceItem di left outer join fetch di.unit u left outer join fetch di.invoice d where di.id = :itemId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("itemId", itemId);
		List<InvoiceItem> doItemList = query.getResultList();
		if (CollectionUtil.isNotEmpty(doItemList)) {
			return doItemList.get(0);
		}
		return null;
	}

}
