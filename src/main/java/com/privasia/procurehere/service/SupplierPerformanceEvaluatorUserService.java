/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;

/**
 * @author Jayshree
 */
public interface SupplierPerformanceEvaluatorUserService {

	/**
	 * @param evaluatorUser
	 * @return
	 */
	SupplierPerformanceEvaluatorUser updateEvaluatorUser(SupplierPerformanceEvaluatorUser evaluatorUser);

	/**
	 * @param evalId
	 * @param userId
	 * @return
	 */
	SupplierPerformanceEvaluatorUser getFormEvaluatorUserByIdFromApproval(String evalId, String userId);

	/**
	 * @param evaluatorUser
	 * @param isDraft TODO
	 * @return
	 * @throws ApplicationException
	 */
	SupplierPerformanceEvaluatorUser saveEvaluatorUser(SupplierPerformanceEvaluatorUser evaluatorUser, Boolean isDraft) throws ApplicationException;

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
	SupplierPerformanceEvaluatorUser getEvaluatorUserById(String id);

	/**
	 * @param evalUserId
	 * @param userId
	 * @return
	 */
	SupplierPerformanceEvaluatorUser getFormEvaluatorUserByUserIdAndEvalUserId(String evalUserId, String userId);

	/**
	 * @param id
	 * @return
	 */
	SupplierPerformanceEvaluatorUser getEvaluationUserById(String id);

	/**
	 * @param evalUserId
	 */
	void updateEvaluatorPreviewedDate(String evalUserId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceEvaluatorUser> getEvaluatorUserByFormId(String formId);

}
