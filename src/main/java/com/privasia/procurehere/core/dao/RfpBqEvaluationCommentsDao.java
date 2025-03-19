package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfpBqEvaluationCommentsDao extends GenericDao<RfpBqEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param cqItemId
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfpBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

	/**
	 * @param bqId
	 * @param eventId
	 * @return
	 */
	List<RfpBqEvaluationComments> getCommentsByBqIdAndEventId(String bqId, String eventId, User logedUser);
}
