package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */

public interface RftEvaluatorUserDao extends GenericDao<RftEvaluatorUser, String> {

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RftEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param envelopId
	 * @return
	 */
	boolean findClosedStatusForLeadEvaluator(String envelopId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	List<RftEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser);

	RftEvaluatorUser getEvaluationDocument(String envelopId);

}
