package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RfpSupplierComment;

/**
 * @author Vipul
 * @author Ravi
 */

public interface RfpSupplierCommentService {

	/**
	 * @param supplierComment
	 * @param sspplierId
	 * @return
	 */
	List<RfpSupplierComment> saveSupplierBqComment(Comments supplierComment, String sspplierId);

	/**
	 * @param bqItemId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierComment> getSupplierCommentsByBqId(String bqItemId, String supplierId);

	/**
	 * @param id
	 * @return
	 */
	//RfpSupplierComment getSupplierBqCommentByRemarkId(String id, String supplierId);

	/**
	 * @param remarkId
	 * @param supplierId 
	 * @return
	 */
	List<RfpSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierComments(String eventId);
}
