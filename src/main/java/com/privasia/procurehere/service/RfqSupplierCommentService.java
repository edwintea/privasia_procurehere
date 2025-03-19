package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RfqSupplierComment;

/**
 * @author Vipul
 * @author Ravi
 */

public interface RfqSupplierCommentService {

	/**
	 * @param supplierComment
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId);

	/**
	 * @param bqItemId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierComment> getSupplierCommentsByBqId(String bqItemId, String supplierId);

	/**
	 * @param id
	 * @return
	 */
	//RfqSupplierComment getSupplierBqCommentByRemarkId(String id, String supplierId);

	/**
	 * @param remarkId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId);

	/**
	 * 
	 * @param eventId
	 */
	void deleteSupplierComments(String eventId);

}
