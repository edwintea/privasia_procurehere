/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RftCqEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RftCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RftCqEvaluationComments comments);

	/**
	 * 
	 * @param comments
	 */
	public void deleteComment(RftCqEvaluationComments comments);

	/**
	 * 
	 * @param commentId
	 * @return
	 */
	RftCqEvaluationComments findComment(String commentId);
	
	List<RftCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId,User user, User logedInUser);
	

}
