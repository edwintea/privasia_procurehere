package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GoodsReceiptNoteItemDao;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.GoodsReceiptNoteItemService;

@Service
@Transactional(readOnly = true)
public class GoodsReceiptNoteItemServiceImpl implements GoodsReceiptNoteItemService {

	private static final Logger LOG = LogManager.getLogger(Global.GRN_LOG);

	@Autowired
	GoodsReceiptNoteItemDao goodsReceiptNoteItemDao;

	@Override
	public List<GoodsReceiptNoteItem> findAllGrnItemsByGrnId(String grnId) {
		List<GoodsReceiptNoteItem> items = goodsReceiptNoteItemDao.getAllGrnItemByGrnId(grnId);
		if (CollectionUtil.isNotEmpty(items)) {
			for (GoodsReceiptNoteItem item : items) {
				if (item.getParent() != null) {
					LOG.info("Po : " + item.getPo().getId() + " PO ITEM : " + item.getPoItem().getId());
					BigDecimal previousReceivedQuantity = goodsReceiptNoteItemDao.findReceivedQuantitiesByPoAndPoItemId(item.getPo().getId(), item.getPoItem().getId());
					LOG.info("previousReceivedQuantity : " + previousReceivedQuantity);
					item.setPreviousReceivedQuantity(previousReceivedQuantity != null ? previousReceivedQuantity : BigDecimal.ZERO);
				}
			}
		}
		return items;
	}

	@Override
	public GoodsReceiptNoteItem findById(String id) {
		GoodsReceiptNoteItem item = goodsReceiptNoteItemDao.findById(id);
		if (item != null) {
			if (item.getUnit() != null) {
				item.getUnit().getUom();
			}
			if (item.getGoodsReceiptNote() != null) {
				item.getGoodsReceiptNote().getGrnTitle();
				item.getGoodsReceiptNote().getDecimal();
				if (item.getGoodsReceiptNote().getBuyer() != null) {
					item.getGoodsReceiptNote().getBuyer().getCompanyName();
				}
			}
			if (item.getParent() != null) {
				item.getParent().getItemName();
				BigDecimal previousReceivedQuantity = goodsReceiptNoteItemDao.findReceivedQuantitiesByPoAndPoItemId(item.getPo().getId(), item.getPoItem().getId());
				item.setPreviousReceivedQuantity(previousReceivedQuantity != null ? previousReceivedQuantity : BigDecimal.ZERO);

			}
			if (item.getPo() != null) {
				item.getPo().getPoNumber();
			}
			if (item.getPoItem() != null) {
				item.getPoItem().getItemName();
			}

		}
		return item;
	}

	@Override
	public BigDecimal findReceivedQuantitiesByPoAndPoItemId(String poId, String poItemId) {
		return goodsReceiptNoteItemDao.findReceivedQuantitiesByPoAndPoItemId(poId, poItemId);
	}

}