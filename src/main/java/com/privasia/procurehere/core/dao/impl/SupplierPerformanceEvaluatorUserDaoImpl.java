/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;


import java.util.Date;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceEvaluatorUserDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApproval;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApprovalUser;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Jayshree
 *
 */
@Repository
public class SupplierPerformanceEvaluatorUserDaoImpl extends GenericDaoImpl<SupplierPerformanceEvaluatorUser, String> implements SupplierPerformanceEvaluatorUserDao {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceEvaluatorUserDaoImpl.class);
	
	@Autowired
	UserDao userDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceEvaluatorUser getFormEvaluatorUserByIdForApproval(String evalId, String userId) {
		//final Query query = getEntityManager().createQuery("select r from SupplierPerformanceEvaluatorUser r left outer join fetch r.evaluationApprovals app left outer join app.approvalUsers appUser left outer join fetch r.scoreRating sr where r.form.id = :formId and (r.evaluator.id = :userId or appUser.user.id = :userId) ");
		final Query query = getEntityManager().createQuery("select r from SupplierPerformanceEvaluatorUser r left outer join fetch r.evaluator ev join fetch r.form f join fetch f.businessUnit b join fetch f.procurementCategory pg join fetch f.awardedSupplier sup left outer join fetch r.criteria c join fetch f.formOwner fo left outer join fetch r.scoreRating sr join r.evaluationApprovals app join app.approvalUsers appUser where r.id = :evalId and appUser.user.id = :userId and appUser.approvalStatus = :approvalStatus ");
		query.setParameter("evalId", evalId);
		query.setParameter("userId", userId);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		List<SupplierPerformanceEvaluatorUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceEvaluatorUser getEvaluatorUserById(String id) {
		final Query query = getEntityManager().createQuery("select r from SupplierPerformanceEvaluatorUser r left outer join fetch r.evaluator ev left outer join fetch r.form f left outer join fetch f.formOwner left outer join fetch f.awardedSupplier left outer join fetch f.procurementCategory left outer join fetch f.businessUnit left outer join fetch r.criteria c left outer join fetch r.scoreRating sr where r.id =:id ");
		query.setParameter("id", id);
		List<SupplierPerformanceEvaluatorUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public EventPermissions getUserPemissionsForForm(String userId, String evaluatorUserId) {
		LOG.info("userId :" + userId + " evaluatorUserId: " + evaluatorUserId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		SupplierPerformanceEvaluatorUser evaluatorUser = getEvaluatorUserById(evaluatorUserId);

		if (evaluatorUser.getForm().getFormOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} 

		// Approver
		List<PerformanceEvaluationApproval> approvals = evaluatorUser.getEvaluationApprovals(); 
		for (PerformanceEvaluationApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<PerformanceEvaluationApprovalUser> users = approval.getApprovalUsers();
				for (PerformanceEvaluationApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}
		
		return permissions;
	}
	
	@Override
	public void updateRatingForEvaluatorUser(String evalUserId, ScoreRating scoreRating) {
		final Query query = getEntityManager().createQuery("update SupplierPerformanceEvaluatorUser r set r.scoreRating = :scoreRating where r.id =:id ");
		query.setParameter("id", evalUserId);
		query.setParameter("scoreRating", scoreRating);
		query.executeUpdate();
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceEvaluatorUser getEvaluationUserById(String id) {
		final Query query = getEntityManager().createQuery("select r from SupplierPerformanceEvaluatorUser r left outer join fetch r.evaluator ev left outer join fetch r.form f left outer join fetch f.formOwner left outer join fetch f.awardedSupplier left outer join fetch f.procurementCategory left outer join fetch f.businessUnit left outer join fetch r.evaluationApprovals app left outer join fetch r.scoreRating sr where r.id =:id ");
		query.setParameter("id", id);
		List<SupplierPerformanceEvaluatorUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void updateEvaluatorPreviewedDate(String evalUserId) {
		final Query query = getEntityManager().createQuery("update SupplierPerformanceEvaluatorUser r set r.previewDate = :previewDate where r.id =:id and r.previewDate is null");
		query.setParameter("id", evalUserId);
		query.setParameter("previewDate", new Date());
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluatorUser> getEvaluatorUserByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser(v.id, e.name, v.overallScore) from SupplierPerformanceEvaluatorUser v left outer join v.evaluator e where v.form.id = :formId and v.evaluationStatus = :evaluationStatus ");
		query.setParameter("formId", formId);
		query.setParameter("evaluationStatus", SupperPerformanceEvaluatorStatus.APPROVED);
		List<SupplierPerformanceEvaluatorUser> list = query.getResultList();
		return list;
	}

}
