package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierComment;

/**
 * @author Vipul
 * @author Ravi
 */
public interface RfqSupplierCommentDao extends GenericDao<RfqSupplierComment, String> {

	List<RfqSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId);

	void deleteSupplierComments(String eventId);

	//RfqSupplierComment findByRemarkId(String id, String supplierId);

}
