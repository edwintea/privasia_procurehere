package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RftBqTotalEvaluationCommentsDao extends GenericDao<RftBqTotalEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param logedInUser TODO
	 * @param cqItemId
	 * @return
	 */
	List<RftBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);
}
