/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.pojo.EventPermissions;

/**
 * @author Jayshree
 */
public interface SupplierPerformanceEvaluatorUserDao extends GenericDao<SupplierPerformanceEvaluatorUser, String> {

	/**
	 * @param evalId
	 * @param userId
	 * @return
	 */
	SupplierPerformanceEvaluatorUser getFormEvaluatorUserByIdForApproval(String evalId, String userId);

	/**
	 * @param id
	 * @return
	 */
	SupplierPerformanceEvaluatorUser getEvaluatorUserById(String id);

	/**
	 * @param userId
	 * @param evaluatorUserId
	 * @return
	 */
	EventPermissions getUserPemissionsForForm(String userId, String evaluatorUserId);

	/**
	 * @param id
	 * @return
	 */
	SupplierPerformanceEvaluatorUser getEvaluationUserById(String id);

	/**
	 * @param evalUserId
	 * @param scoreRating
	 */
	void updateRatingForEvaluatorUser(String evalUserId, ScoreRating scoreRating);

	/**
	 * @param evalUserId
	 * @return
	 */
	void updateEvaluatorPreviewedDate(String evalUserId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceEvaluatorUser> getEvaluatorUserByFormId(String formId);

}
