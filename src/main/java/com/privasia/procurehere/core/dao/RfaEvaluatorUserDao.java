package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */

public interface RfaEvaluatorUserDao extends GenericDao<RfaEvaluatorUser, String> {

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfaEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	/**
	 * @param evelopId
	 * @param logedInUser
	 * @return
	 */
	List<RfaEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User logedInUser);

	RfaEvaluatorUser getEvaluationDocument(String id);

}
