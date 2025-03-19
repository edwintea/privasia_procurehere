package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.User;

/**
 * @author RT-Kapil
 */

public interface RfiEvaluatorUserDao extends GenericDao<RfiEvaluatorUser, String> {

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfiEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	List<RfiEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser);

	RfiEvaluatorUser getEvaluationDocument(String id);

}
