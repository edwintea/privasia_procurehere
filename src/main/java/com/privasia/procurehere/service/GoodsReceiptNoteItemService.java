package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;

/**
 * @author pooja
 */
public interface GoodsReceiptNoteItemService {

	/**
	 * @param grnId
	 * @return
	 */
	List<GoodsReceiptNoteItem> findAllGrnItemsByGrnId(String grnId);

	/**
	 * @param id
	 * @return
	 */
	GoodsReceiptNoteItem findById(String id);

	/**
	 * @param poId
	 * @param poItemId
	 * @return
	 */
	BigDecimal findReceivedQuantitiesByPoAndPoItemId(String poId, String poItemId);

}