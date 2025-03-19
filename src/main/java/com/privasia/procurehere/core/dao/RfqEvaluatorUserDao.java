package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.User;

/**
 * @author RT-Kapil
 * @author Ravi
 */

public interface RfqEvaluatorUserDao extends GenericDao<RfqEvaluatorUser, String> {

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfqEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	List<RfqEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser);

	RfqEvaluatorUser getEvaluationDocument(String id);

}
