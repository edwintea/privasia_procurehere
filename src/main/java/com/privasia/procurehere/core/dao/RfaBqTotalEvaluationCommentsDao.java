package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfaBqTotalEvaluationCommentsDao extends GenericDao<RfaBqTotalEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param logedInUser TODO
	 * @param cqItemId
	 * @return
	 */
	List<RfaBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser);
}
