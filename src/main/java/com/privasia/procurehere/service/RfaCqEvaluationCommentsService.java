/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Priyanka Singh
 */
public interface RfaCqEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfaCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfaCqEvaluationComments comments);

	/**
	 * @param comments
	 */
	public void deleteComment(RfaCqEvaluationComments comments);

	/**
	 * @param commentId
	 * @return
	 */
	RfaCqEvaluationComments findComment(String commentId);

	List<RfaCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logedInUser);

}
