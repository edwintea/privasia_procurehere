/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface RfaBqTotalEvaluationCommentsService {

	/**
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfaBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);

	/**
	 * @param comments
	 */
	public void SaveComment(RfaBqTotalEvaluationComments comments);

	/**
	 * @param commentId
	 */
	public void deleteComment(String commentId);

	/**
	 * @param commentId
	 * @return
	 */
	RfaBqTotalEvaluationComments findComment(String commentId);

	List<RfaBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser);
}
