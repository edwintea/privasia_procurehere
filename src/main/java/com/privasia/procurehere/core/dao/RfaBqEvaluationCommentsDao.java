package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfaBqEvaluationCommentsDao extends GenericDao<RfaBqEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param cqItemId
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfaBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param bqId
	 * @param eventId
	 * @return
	 */
	List<RfaBqEvaluationComments> getCommentsByBqIdAndEventId(String bqId, String eventId, User logedUser);
}
