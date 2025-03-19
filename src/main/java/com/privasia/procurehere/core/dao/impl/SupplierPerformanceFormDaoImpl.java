/**
 *
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author anshul
 */
@Repository
public class SupplierPerformanceFormDaoImpl extends GenericDaoImpl<SupplierPerformanceForm, String> implements SupplierPerformanceFormDao {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceFormDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSPFormIdListByTemplateId(String templateId) {
		final Query query = getEntityManager().createQuery("select distinct a.id from SupplierPerformanceForm a where a.template.id = :templateId");
		query.setParameter("templateId", templateId);
		List<String> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluatorUser> findEvaluatorsByFormId(String formId) {
		StringBuilder hsql = new StringBuilder("select e from SupplierPerformanceEvaluatorUser e join fetch e.evaluator ev left outer join e.evaluationApprovals ea where e.form.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<SupplierPerformanceEvaluatorUser> list = (List<SupplierPerformanceEvaluatorUser>) query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> findEvaluatorsUserByFormId(String formId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.pojo.UserPojo(eu.id, eu.loginId, eu.name, eu.tenantId) from SupplierPerformanceEvaluatorUser e join e.evaluator eu where e.form.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<UserPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceForm getFormAndCriteriaAndBusinessUnitForErpIntegration(String formId) {
		StringBuilder hsql = new StringBuilder("from SupplierPerformanceForm sp left outer join fetch sp.criteria e left outer join fetch sp.businessUnit bu where sp.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<SupplierPerformanceForm> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceForm getFormAndCriteriaByFormId(String formId) {
		StringBuilder hsql = new StringBuilder("from SupplierPerformanceForm sp left outer join fetch sp.criteria e left outer join fetch sp.businessUnit bu left outer join fetch sp.procurementCategory pc left outer join fetch sp.formOwner fo left outer join fetch sp.awardedSupplier awds where sp.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<SupplierPerformanceForm> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@Override
	public SupplierPerformanceFormStatus getFormStatusByFormId(String formId) {
		try {
			StringBuilder hsql = new StringBuilder("select sp.formStatus from SupplierPerformanceForm sp where sp.id = :id");
			Query query = getEntityManager().createQuery(hsql.toString());
			query.setParameter("id", formId);
			return ((SupplierPerformanceFormStatus) query.getSingleResult());
		} catch (Exception e) {
			// No Result found
			LOG.error("No Result for Form : " + formId, e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventTimerPojo getTimeByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select NEW com.privasia.procurehere.core.pojo.EventTimerPojo(r.id, r.evaluationStartDate, r.evaluationEndDate, r.formStatus) from SupplierPerformanceForm r where r.id =:formId");
		query.setParameter("formId", formId);
		List<EventTimerPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SupplierPerformanceForm> getAllScheduledEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceForm(v.id, v.formId, v.evaluationStartDate, v.formName, v.referenceNumber, v.formOwner, v.oldFormStatus, v.buyer.id) from SupplierPerformanceForm v inner join v.formOwner where v.formStatus = :formStatus");
		query.setParameter("formStatus", SupplierPerformanceFormStatus.SCHEDULED);
		List<SupplierPerformanceForm> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SupplierPerformanceForm> getAllActiveEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceForm(v.id, v.formId , v.evaluationEndDate, v.referenceNumber , v.formOwner, v.buyer.id) from SupplierPerformanceForm v inner join v.formOwner where v.formStatus = :formStatus");
		query.setParameter("formStatus", SupplierPerformanceFormStatus.ACTIVE);
		List<SupplierPerformanceForm> list = query.getResultList();
		return list;
	}

	@Override
	public int updateImmediately(String formId, SupplierPerformanceFormStatus status) {
		String hql = "update SupplierPerformanceForm re set re.formStatus = :status where re.id = :formId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("formId", formId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public int updateImmediately(String reminderId) {
		String hql = "update SupplierPerformanceReminder re set re.reminderSent = :reminderSent where re.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("reminderSent", Boolean.TRUE);
		query.setParameter("id", reminderId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceReminder findReminderByFormId(String reminderId) {
		final Query query = getEntityManager().createQuery("select sp from SupplierPerformanceReminder sp inner join sp.form as e where sp.id = :id");
		query.setParameter("id", reminderId);
		List<SupplierPerformanceReminder> list = (List<SupplierPerformanceReminder>) query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SupplierPerformanceForm> getAllConcludedFormsForRecurrenceJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceForm(v.id, v.formId, v.evaluationStartDate, v.formName, v.referenceNumber, v.recurrenceEvaluation, v.noOfRecurrence, v.recurrenceExecuted) from SupplierPerformanceForm v where v.formStatus = :formStatus and v.isRecurrenceEvaluation = 1");
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		List<SupplierPerformanceForm> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> getSpFormIdListForSupplierIdAndTenantId(Date startDate, Date endDate, String supplierId, String tenantId) {
		String hql = "select NEW com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo(v.id, v.formId, v.formName) from SupplierPerformanceForm v where v.buyer.id = :tenantId and v.awardedSupplier.id = :supplierId and v.formStatus = :formStatus and v.overallScore > 0 ";

		if (startDate != null && endDate != null) {
			hql += " and v.concludeDate between :startDate and :endDate";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		query.setParameter("supplierId", supplierId);
		query.setParameter("tenantId", tenantId);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		List<SupplierPerformanceEvaluationPojo> list = query.getResultList();
		return list;
	}

	@Override
	public SupplierPerformanceForm getFormAndawardedSupplier(String formId) {
		StringBuilder hsql = new StringBuilder("select sp from SupplierPerformanceForm sp left outer join fetch sp.awardedSupplier e where sp.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<SupplierPerformanceForm> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluatorUser> findEvaluatorUsersByFormId(String formId) {
		StringBuilder hsql = new StringBuilder("select e from SupplierPerformanceEvaluatorUser e inner join e.evaluator as eu  where e.form.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<SupplierPerformanceEvaluatorUser> list = (List<SupplierPerformanceEvaluatorUser>) query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnit> getBusinessUnitListForTenant(String tenantId, Date startDate, Date endDate, String supplierId) {
		String hsql = "select distinct new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status) from SupplierPerformanceForm sp left outer join sp.businessUnit bu where sp.formStatus = 'CONCLUDED'";

		if (startDate != null && endDate != null) {
			hsql += " and sp.concludeDate between :startDate and :endDate";
		}
		if (StringUtils.checkString(supplierId).length() > 0) {
			hsql += " and sp.awardedSupplier.id = :supplierId  ";
		}
		hsql += " and bu.buyer.id =:tenantId order by bu.displayName ";

		Query query = getEntityManager().createQuery(hsql.toString());
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		if (StringUtils.checkString(supplierId).length() > 0) {
			query.setParameter("supplierId", supplierId);
		}
		query.setParameter("tenantId", tenantId);
		List<BusinessUnit> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcurementCategories> getProcurementCategoriesListForTenantForDate(String tenantId, Date startDate, Date endDate) {
		String hsql = "select distinct new com.privasia.procurehere.core.entity.ProcurementCategories(p.id, p.procurementCategories, p.description ) from SupplierPerformanceForm sp left outer join sp.procurementCategory p where sp.formStatus = 'CONCLUDED'";

		if (startDate != null && endDate != null) {
			hsql += " and sp.concludeDate between :startDate and :endDate";
		}

		hsql += " and p.buyer.id =:tenantId order by p.procurementCategories ";

		Query query = getEntityManager().createQuery(hsql.toString());
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		List<ProcurementCategories> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@Override
	public void transferOwnership(String fromUserId, String toUserId) {

		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_SUPPLIER_PERFORMANCE_FORM SET CREATED_BY_ID = :toUserId WHERE CREATED_BY_ID =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators supplier performance transferred: {}", recordsUpdated);

		Query query2 = getEntityManager().createNativeQuery("UPDATE PROC_SUPPLIER_PERFORMANCE_FORM SET FORM_OWNER_ID = :toUserId WHERE FORM_OWNER_ID =:fromUserId");
		query2.setParameter("toUserId", toUserId);
		query2.setParameter("fromUserId", fromUserId);
		recordsUpdated = query2.executeUpdate();
		LOG.info(" supplier performance Form Owner transferred: {}", recordsUpdated);

		//transfer ownership of evaluators
		Query query5 = getEntityManager().createQuery(
				"UPDATE SupplierPerformanceEvaluatorUser sfaur " +
						"SET sfaur.evaluator = :targetUser " +
						"WHERE sfaur.evaluator = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info(" SUPPLIER PERFORMANCE Evaluator user transferred: {}", recordsUpdated);
		Query query6 = getEntityManager().createQuery(
				"UPDATE PerformanceEvaluationApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		LOG.info("sourceUser  issue 42      "+sourceUser);
		LOG.info("targetUser  issue 42      "+targetUser);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		query6.executeUpdate();
		recordsUpdated = query6.executeUpdate();
		LOG.info(" SUPPLIER PERFORMANCE APPROVAL Evaluator user transferred: {}", recordsUpdated);


	}

}
