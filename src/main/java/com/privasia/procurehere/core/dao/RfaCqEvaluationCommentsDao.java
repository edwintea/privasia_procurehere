package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Priyanka Singh
 */
public interface RfaCqEvaluationCommentsDao extends GenericDao<RfaCqEvaluationComments, String> {

	/**
	 * @param supplier
	 * @param eventId
	 * @param cqItemId
	 * @param logedInUser TODO
	 * @return
	 */
	List<RfaCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);
}
