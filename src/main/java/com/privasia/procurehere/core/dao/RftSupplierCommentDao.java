package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierComment;

/**
 * @author Vipul
 */
public interface RftSupplierCommentDao extends GenericDao<RftSupplierComment, String> {

	List<RftSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId);

	/**
	 * 
	 * @param eventId
	 */
	void deleteSupplierComments(String eventId);


	
}
