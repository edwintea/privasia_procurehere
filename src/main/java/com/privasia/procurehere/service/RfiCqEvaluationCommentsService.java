/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfiCqEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfiCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfiCqEvaluationComments comments);

	/**
	 * 
	 * @param comments
	 */
	public void deleteComment(RfiCqEvaluationComments comments);

	/**
	 * 
	 * @param commentId
	 * @return
	 */
	RfiCqEvaluationComments findComment(String commentId);
	
	List<RfiCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId,User user, User logedInUser);

}
