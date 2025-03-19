package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RfaSupplierComment;

/**
 * @author Vipul
 */

public interface RfaSupplierCommentService {

	List<RfaSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId);

	List<RfaSupplierComment> getSupplierBqCommentByBqId(String bqItemId, String supplierId);

	// RftSupplierComment getSupplierBqCommentByRemarkId(String id , String supplierId);

	List<RfaSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId);

}
