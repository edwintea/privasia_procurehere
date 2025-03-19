/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SearchFilterPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * 
 * @author priyanka
 *
 */
public interface SupplierPerformanceEvaluationDao extends GenericDao<SupplierPerformanceForm, String> {

	List<SupplierPerformanceEvaluationPojo> findPendingSPEvaluation(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalPendingSPEvaluation(String loggedInUserTenantId, String id, TableDataInput input);

	List<SupplierPerformanceEvaluationPojo> findSPEvaluationPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	long findCountOfSPEvaluationPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId);
	List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds);

	long findTotalFilteredSPEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId);
	long findTotalFilteredSPEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds);

	long findTotalActiveSPEvaluation(String loggedInUserTenantId);
	long findTotalActiveSPEvaluationBizUnit(String loggedInUserTenantId,List<String> businessUnitIds);

	SupplierPerformanceForm getSupplierPerformanceFormDetailsByFormId(String formId);

	List<SupplierPerformanceReminder> getAllSpFormRemindersByFormId(String formId);

	long findTotalPerformanceEvaluationCountForCsv(String userId);

	List<SupplierPerformanceForm> getAllPerformanceEvaluationForCsv(String userId, Date startDate, Date endDate, String[] formIdArr, boolean select_all,SearchFilterPerformanceEvaluationPojo searchFilterPerformanceEvaluationPojo, String tenantId);

	List<SupplierPerformanceReminder> getSupplierReminderForNotification();

	EventPermissions getUserPemissionsForForm(String id, String formId);

	void suspendForm(String form);

	SupplierPerformanceForm getFormDetailsBySupplierId(String supplierId);

	/**
	 * @param tenantId
	 * @param supplierId
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	BigDecimal getSumOfOverallScoreOfSupplierByBuyerId(String tenantId, String supplierId, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param supplierId
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	Long getCountOfFormsBySupplierByBuyerId(String tenantId, String supplierId, Date startDate, Date endDate);

	/**
	 * @param buyerId
	 * @param supplierId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(String buyerId, String supplierId, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param supplierId
	 * @param startDate
	 * @param endDate
	 * @param businessUnit
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(String tenantId, String supplierId, Date startDate, Date endDate, String businessUnit);

	/**
	 * @param startDate
	 * @param endDate
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	SupplierPerformanceEvaluationPojo getOverallScoreOfSupplierByBuyerId(Date startDate, Date endDate, String buyerId, String supplierId);

	/**
	 * @param start
	 * @param end
	 * @param unitId
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreOfSupplierByBUnitAndEvent(Date start, Date end, String unitId, String supplierId, String buyerId);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<SupplierPerformanceForm> getEventIdListForSupplierId(String supplierId, String buyerId, Date startDate, Date endDate);

	/**
	 * @param start
	 * @param end
	 * @param eventId
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndEventId(Date start, Date end, String eventId, String buyerId, String supplierId);

}
