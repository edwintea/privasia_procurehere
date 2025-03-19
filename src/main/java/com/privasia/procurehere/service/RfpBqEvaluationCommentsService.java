/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfpBqEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfpBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfpBqEvaluationComments comments);

	/**
	 * 
	 * @param comments
	 */
	public void deleteComment(RfpBqEvaluationComments comments);

	/**
	 * 
	 * @param commentId
	 * @return
	 */
	RfpBqEvaluationComments findComment(String commentId);

	List<RfpBqEvaluationComments> getCommentsForSupplierNew(Supplier findSuppById, String eventId, String cqItemId,User user, User logInUser);

}
