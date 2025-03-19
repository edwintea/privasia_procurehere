package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierComment;

/**
 * @author Vipul
 * @author Ravi
 */
public interface RfpSupplierCommentDao extends GenericDao<RfpSupplierComment, String> {

	List<RfpSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId);

	void deleteSupplierComments(String eventId);

	//RfpSupplierComment findByRemarkId(String id, String supplierId);

}
