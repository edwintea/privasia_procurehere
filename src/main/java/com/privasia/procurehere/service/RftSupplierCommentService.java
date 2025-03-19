package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RftSupplierComment;

/**
 * @author Vipul
 */

public interface RftSupplierCommentService {

	List<RftSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId);

	List<RftSupplierComment> getSupplierBqCommentByBqId(String bqItemId, String supplierId);

	// RftSupplierComment getSupplierBqCommentByRemarkId(String id , String supplierId);

	List<RftSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierComments(String eventId);

}
