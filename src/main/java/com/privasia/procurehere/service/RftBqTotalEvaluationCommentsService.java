/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RftBqTotalEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RftBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RftBqTotalEvaluationComments comments);

	/**
	 * @param commentId
	 */
	public void deleteComment(String comments);

	/**
	 * @param commentId
	 * @return
	 */
	RftBqTotalEvaluationComments findComment(String commentId);

	List<RftBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser);

}
