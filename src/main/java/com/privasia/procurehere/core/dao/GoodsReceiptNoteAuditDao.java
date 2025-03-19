package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;

/**
 * @author ravi
 */
public interface GoodsReceiptNoteAuditDao extends GenericDao<GoodsReceiptNoteAudit, String> {

	/**
	 * @param grnId
	 * @return
	 */
	List<GoodsReceiptNoteAudit> getGrnAuditByGrnIdForSupplier(String grnId);

	/**
	 * @param grnId
	 * @return
	 */
	List<GoodsReceiptNoteAudit> getGrnAuditByGrnIdForBuyer(String grnId);

}
