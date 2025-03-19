package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfqBqEvaluationCommentsDao extends GenericDao<RfqBqEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param cqItemId
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfqBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param bqId
	 * @param eventId
	 * @return
	 */
	List<RfqBqEvaluationComments> getCommentsByBqIdAndEventId(String bqId, String eventId, User logedUser);
}
