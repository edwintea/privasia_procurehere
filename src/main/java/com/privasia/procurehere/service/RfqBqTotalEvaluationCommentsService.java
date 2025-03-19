/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfqBqTotalEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfqBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfqBqTotalEvaluationComments comments);

	/**
	 * @param commentId
	 */
	public void deleteComment(String commentId);

	/**
	 * @param commentId
	 * @return
	 */
	RfqBqTotalEvaluationComments findComment(String commentId);

	List<RfqBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser);
}
