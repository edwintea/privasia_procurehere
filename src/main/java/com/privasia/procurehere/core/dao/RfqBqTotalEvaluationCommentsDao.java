package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfqBqTotalEvaluationCommentsDao extends GenericDao<RfqBqTotalEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param logedInUser TODO
	 * @param cqItemId
	 * @return
	 */
	List<RfqBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);
}
