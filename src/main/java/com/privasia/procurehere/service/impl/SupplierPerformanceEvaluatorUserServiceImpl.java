/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ScoreRatingDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceEvaluatorUserDao;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApproval;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApprovalUser;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.entity.SpFormEvaluationAppComment;
import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.ScoreRatingService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluatorUserService;

/**
 * @author Jayshree
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceEvaluatorUserServiceImpl implements SupplierPerformanceEvaluatorUserService {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceEvaluatorUserServiceImpl.class);

	@Autowired
	SupplierPerformanceEvaluatorUserDao supplierPerformanceEvaluatorUserDao;

	@Autowired
	ScoreRatingDao scoreRatingDao;

	@Autowired
	ScoreRatingService scoreRatingService;

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceEvaluatorUser updateEvaluatorUser(SupplierPerformanceEvaluatorUser evaluatorUser) {
		return supplierPerformanceEvaluatorUserDao.saveOrUpdate(evaluatorUser);
	}

	@Override
	public SupplierPerformanceEvaluatorUser getFormEvaluatorUserByIdFromApproval(String id, String userId) {
		SupplierPerformanceEvaluatorUser user = supplierPerformanceEvaluatorUserDao.getEvaluatorUserById(id);

		if (user != null) {
			if (CollectionUtil.isNotEmpty(user.getEvaluationApprovals())) {
				for (PerformanceEvaluationApproval approval : user.getEvaluationApprovals()) {
					for (PerformanceEvaluationApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(user.getEvalApprovalComment())) {
				for (SpFormEvaluationAppComment comment : user.getEvalApprovalComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(user.getForm().getFormReminder())) {
				for (SupplierPerformanceReminder rem : user.getForm().getFormReminder()) {
					rem.getReminderDate();
				}
			}

		}
		return user;
	}

	@Override
	public SupplierPerformanceEvaluatorUser getFormEvaluatorUserByUserIdAndEvalUserId(String evalUserId, String userId) {
		SupplierPerformanceEvaluatorUser user = supplierPerformanceEvaluatorUserDao.getEvaluatorUserById(evalUserId);

		if (user != null) {
			if (CollectionUtil.isNotEmpty(user.getEvaluationApprovals())) {
				for (PerformanceEvaluationApproval approval : user.getEvaluationApprovals()) {
					for (PerformanceEvaluationApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(user.getEvalApprovalComment())) {
				for (SpFormEvaluationAppComment comment : user.getEvalApprovalComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}
			// user.getForm().get
			if (user.getForm() != null) {
				if (CollectionUtil.isNotEmpty(user.getForm().getFormReminder())) {
					for (SupplierPerformanceReminder r : user.getForm().getFormReminder()) {
						r.getReminderDate();
					}
				}
				if (CollectionUtil.isNotEmpty(user.getForm().getEvaluators())) {
					for (SupplierPerformanceEvaluatorUser eu : user.getForm().getEvaluators()) {
						eu.getEvaluator().getName();
					}
				}
			}

		}
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceEvaluatorUser saveEvaluatorUser(SupplierPerformanceEvaluatorUser evaluatorUser, Boolean isDraft) throws ApplicationException {

		SupplierPerformanceEvaluatorUser persistObj = supplierPerformanceEvaluatorUserDao.getEvaluatorUserById(evaluatorUser.getId());

		if (persistObj != null) {

			// get the tenantId from Evaluator
			String tenantId = persistObj.getEvaluator().getTenantId();

			if (Boolean.TRUE == evaluatorUser.getEnablePerformanceEvaluationApproval()) {
				if (CollectionUtil.isNotEmpty(evaluatorUser.getEvaluationApprovals())) {
					int level = 1;
					for (PerformanceEvaluationApproval app : evaluatorUser.getEvaluationApprovals()) {
						app.setEvalutorUser(evaluatorUser);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (PerformanceEvaluationApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
								LOG.info("APPROVAL USER : " + approvalUser.toLogString());
							}
						}
						LOG.info("APPROVAL : " + app.toLogString());
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}
			} else {
				LOG.info("Clrearing Approvals >>>>>>>>>>>>>>>>>>>> .");
				if (CollectionUtil.isNotEmpty(evaluatorUser.getEvaluationApprovals())) {
					evaluatorUser.getEvaluationApprovals().clear();
				}
			}

			persistObj.setEnablePerformanceEvaluationApproval(evaluatorUser.getEnablePerformanceEvaluationApproval());
			persistObj.setEvaluationApprovals(evaluatorUser.getEvaluationApprovals());
			if (Boolean.FALSE == isDraft) {
				persistObj.setEvaluateDate(new Date());
				persistObj.setEvaluationStatus(SupperPerformanceEvaluatorStatus.DRAFT);
			}
			
			// Update the score from UI to db
			// Compute the total score at item level
			for (SupplierPerformanceCriteria dbC : persistObj.getCriteria()) {
				for (SupplierPerformanceCriteria uiC : evaluatorUser.getCriteria()) {

					if (dbC.getId().equals(uiC.getId()) && dbC.getOrder() > 0) {
						if (uiC.getEvaluatorScore() != null) {
							if (uiC.getEvaluatorScore().floatValue() > dbC.getMaximumScore().floatValue()) {
								throw new ApplicationException("Score should not be greater than maximum score");
							} else {
								// Total Score = (Sub Criteria Score/Maximum Score) x Sub Criteria Weightage
								LOG.info("Setting evluator scores...." + uiC.getLevel() + ":" + uiC.getOrder() + "=" + uiC.getEvaluatorScore());
								dbC.setEvaluatorScore(uiC.getEvaluatorScore());
								dbC.setEvaluatorTotalScore(uiC.getEvaluatorScore().divide(dbC.getMaximumScore(), 2, RoundingMode.HALF_UP).multiply(dbC.getWeightage()));
								if (uiC.getComments() != null) {
									dbC.setComments(uiC.getComments());
								}
							}
							break;
						} else {
							dbC.setEvaluatorScore(null);
							dbC.setEvaluatorTotalScore(null);
						}
					}
				}
			}

			// Compute the total at Section level
			SupplierPerformanceCriteria section = null;
			BigDecimal sectionTotal = BigDecimal.ZERO;
			for (SupplierPerformanceCriteria c : persistObj.getCriteria()) {
				if (c.getOrder() == 0) {

					if (section != null) {
						// Total Score = (Sum of Sub Criteria Score/100) x Criteria Weightage
						LOG.info(" ,, ");
						sectionTotal = sectionTotal.divide(new BigDecimal(100));
						sectionTotal = sectionTotal.multiply(section.getWeightage()); //
						sectionTotal = sectionTotal.setScale(0, RoundingMode.HALF_UP);
						section.setEvaluatorTotalScore(sectionTotal);
					}

					section = c;
					sectionTotal = BigDecimal.ZERO;

				} else {
					if (c.getEvaluatorScore() != null) {
						sectionTotal = sectionTotal.add(c.getEvaluatorTotalScore());
					}
				}
			}
			// Set value for last section
			if (section != null) {
				// Total Score = (Sum of Sub Criteria Score/100) x Criteria Weightage
				sectionTotal = sectionTotal.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
				sectionTotal = sectionTotal.multiply(section.getWeightage()); //
				sectionTotal = sectionTotal.setScale(0, RoundingMode.HALF_UP);
				section.setEvaluatorTotalScore(sectionTotal);
			}

			// Compute Overall Score
			BigDecimal overallScore = BigDecimal.ZERO;
			for (SupplierPerformanceCriteria fc : persistObj.getCriteria()) {
				if (fc.getOrder() == 0) {
					// Overall Score = Total Score C1 + Total Score C2 + Total Score C3 + .....
					overallScore = overallScore.add(fc.getEvaluatorTotalScore());
				}
			}
			persistObj.setOverallScore(overallScore);
			persistObj = supplierPerformanceEvaluatorUserDao.saveOrUpdate(persistObj);

			// Assign Rating
			ScoreRating scoreRating = scoreRatingDao.getScoreRatingForScoreAndTenant(tenantId, overallScore);
			supplierPerformanceEvaluatorUserDao.updateRatingForEvaluatorUser(persistObj.getId(), scoreRating);

			persistObj.setScoreRating(scoreRating);
		}

		return persistObj;
	}

	@Override
	public EventPermissions getUserPemissionsForForm(String userId, String evaluatorUserId) {
		return supplierPerformanceEvaluatorUserDao.getUserPemissionsForForm(userId, evaluatorUserId);
	}

	@Override
	public SupplierPerformanceEvaluatorUser getEvaluatorUserById(String id) {
		return supplierPerformanceEvaluatorUserDao.getEvaluatorUserById(id);
	}

	@Override
	public SupplierPerformanceEvaluatorUser getEvaluationUserById(String id) {
		SupplierPerformanceEvaluatorUser evalUser = supplierPerformanceEvaluatorUserDao.getEvaluationUserById(id);

		if (CollectionUtil.isNotEmpty(evalUser.getEvalApprovalComment())) {
			for (SpFormEvaluationAppComment comment : evalUser.getEvalApprovalComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getEvaluatorUser();
			}
		}
		return evalUser;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEvaluatorPreviewedDate(String evalUserId) {
		supplierPerformanceEvaluatorUserDao.updateEvaluatorPreviewedDate(evalUserId);
	}

	@Override
	public List<SupplierPerformanceEvaluatorUser> getEvaluatorUserByFormId(String formId) {
		return supplierPerformanceEvaluatorUserDao.getEvaluatorUserByFormId(formId);
	}

}
