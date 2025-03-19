package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierComment;

/**
 * @author Vipul
 */
public interface RfaSupplierCommentDao extends GenericDao<RfaSupplierComment, String> {

	List<RfaSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId);

	// RftSupplierComment findByRemarkId(String id, String supplierId);

}
