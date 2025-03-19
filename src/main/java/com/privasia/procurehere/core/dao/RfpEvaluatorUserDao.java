package com.privasia.procurehere.core.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.User;

/**
 * @author RT-Kapil
 */

public interface RfpEvaluatorUserDao extends GenericDao<RfpEvaluatorUser, String> {

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfpEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	List<RfpEvaluatorUser> getEvaluationSummaryRemarks(String evelopId, User loginUser);

	RfpEvaluatorUser getEvaluationDocument(String id);

}
