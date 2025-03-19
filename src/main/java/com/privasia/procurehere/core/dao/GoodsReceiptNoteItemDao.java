package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;

/**
 * @author pooja
 */
public interface GoodsReceiptNoteItemDao extends GenericDao<GoodsReceiptNoteItem, String> {
	/**
	 * @param grnId
	 * @return
	 */
	List<GoodsReceiptNoteItem> getAllGrnItemByGrnId(String grnId);

	/**
	 * @param poId
	 * @param poItemId
	 * @return
	 */
	BigDecimal findReceivedQuantitiesByPoAndPoItemId(String poId, String poItemId);

	/**
	 * 
	 * @param ids
	 * @param id TODO
	 */
	void deleteItems(List<String> ids, String id);

}
