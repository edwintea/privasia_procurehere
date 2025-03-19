package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.InvoiceItem;

/**
 * @author pooja
 */
public interface InvoiceItemDao extends GenericDao<InvoiceItem, String> {
	/**
	 * @param invoiceId
	 * @return
	 */
	List<InvoiceItem> getAllInvoiceItemByInvoiceId(String invoiceId);

	/**
	 * @param ids
	 * @param invoiceId TODO
	 */
	void deleteItemsByIds(List<String> ids, String invoiceId);

	/**
	 * @param itemId
	 * @return
	 */
	InvoiceItem getInvoiceItemById(String itemId);

}
