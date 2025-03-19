package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;

/**
 * @author pooja
 */

public interface GoodsReceiptNoteAuditService {
	/**
	 * @param audit
	 */
	void save(GoodsReceiptNoteAudit audit);

	/**
	 * @param id
	 * @return
	 */
	List<GoodsReceiptNoteAudit> getGrnAuditForBuyerByGrnId(String id);

	/**
	 * @param grnId
	 * @return
	 */
	List<GoodsReceiptNoteAudit> getGrnAuditForSupplierByGrnId(String grnId);

}
