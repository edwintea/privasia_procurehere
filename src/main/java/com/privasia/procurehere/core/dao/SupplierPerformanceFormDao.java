/**
 *
 */
package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.UserPojo;

/**
 * @author anshul
 */
public interface SupplierPerformanceFormDao extends GenericDao<SupplierPerformanceForm, String> {

	/**
	 * @param templateId
	 * @return
	 */
	List<String> getSPFormIdListByTemplateId(String templateId);

	List<SupplierPerformanceEvaluatorUser> findEvaluatorsByFormId(String formId);

	/**
	 * @param formId
	 * @return
	 */
	EventTimerPojo getTimeByFormId(String formId);

	/**
	 * @return
	 */
	List<SupplierPerformanceForm> getAllScheduledEventsforJob();

	/**
	 * @param formId
	 * @param status
	 * @return
	 */
	int updateImmediately(String formId, SupplierPerformanceFormStatus status);

	/**
	 * @return
	 */
	List<SupplierPerformanceForm> getAllActiveEventsforJob();

	int updateImmediately(String reminderId);

	SupplierPerformanceReminder findReminderByFormId(String reminderId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm getFormAndCriteriaByFormId(String formId);

	/**
	 * @return
	 */
	List<SupplierPerformanceForm> getAllConcludedFormsForRecurrenceJob();

	/**
	 * @param startDate
	 * @param endDate
	 * @param supplierId
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getSpFormIdListForSupplierIdAndTenantId(Date startDate, Date endDate, String supplierId, String tenantId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm getFormAndCriteriaAndBusinessUnitForErpIntegration(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<UserPojo> findEvaluatorsUserByFormId(String formId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm getFormAndawardedSupplier(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceEvaluatorUser> findEvaluatorUsersByFormId(String formId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceFormStatus getFormStatusByFormId(String formId);

	/**
	 * @param tenantId
	 * @param startDate TODO
	 * @param endDate TODO
	 * @param supplierId TODO
	 * @return
	 */
	List<BusinessUnit> getBusinessUnitListForTenant(String tenantId, Date startDate, Date endDate, String supplierId);

	/**
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<ProcurementCategories> getProcurementCategoriesListForTenantForDate(String tenantId, Date startDate, Date endDate);

    void transferOwnership(String fromUser, String toUser);
}
