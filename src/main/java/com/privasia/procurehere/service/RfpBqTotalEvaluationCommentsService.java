/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfpBqTotalEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfpBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfpBqTotalEvaluationComments comments);

	/**
	 * 
	 * @param commentId
	 */
	public void deleteComment(String commentId);

	/**
	 * 
	 * @param commentId
	 * @return
	 */
	RfpBqTotalEvaluationComments findComment(String commentId);

	List<RfpBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser);

}
