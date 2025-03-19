package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfpBqTotalEvaluationCommentsDao extends GenericDao<RfpBqTotalEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param logedInUser TODO
	 * @param cqItemId
	 * @return
	 */
	List<RfpBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);
}
