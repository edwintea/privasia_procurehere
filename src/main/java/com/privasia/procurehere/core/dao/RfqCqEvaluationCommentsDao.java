package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
public interface RfqCqEvaluationCommentsDao extends GenericDao<RfqCqEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param cqItemId
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfqCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);
}
