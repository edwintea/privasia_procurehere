/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfpCqEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfpCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfpCqEvaluationComments comments);

	/**
	 * 
	 * @param comments
	 */
	public void deleteComment(RfpCqEvaluationComments comments);

	/**
	 * 
	 * @param commentId
	 * @return
	 */
	RfpCqEvaluationComments findComment(String commentId);
	
	List<RfpCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId,User user, User logedInUser);


}
