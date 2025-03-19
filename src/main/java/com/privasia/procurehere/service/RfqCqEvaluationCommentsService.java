/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfqCqEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfqCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfqCqEvaluationComments comments);

	/**
	 * 
	 * @param comments
	 */
	public void deleteComment(RfqCqEvaluationComments comments);

	/**
	 * 
	 * @param commentId
	 * @return
	 */
	RfqCqEvaluationComments findComment(String commentId);

	List<RfqCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId,User user, User logedInUser);

}
