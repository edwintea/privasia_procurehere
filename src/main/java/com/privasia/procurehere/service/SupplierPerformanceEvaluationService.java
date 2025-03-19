/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SearchFilterPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author priyanka
 */
public interface SupplierPerformanceEvaluationService {

	// Dashboard Tasks listing methods
	List<SupplierPerformanceEvaluationPojo> findPendingSPEvaluation(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalPendingSPEvaluation(String loggedInUserTenantId, String id, TableDataInput input);

	List<SupplierPerformanceEvaluationPojo> findSPEvaluationPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	long findCountOfSPEvaluationPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	// supplier performance menu listing methods
	List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId);
	List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds);

	long findTotalFilteredSPEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId);
	long findTotalFilteredSPEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds);

	long findTotalActiveSPEvaluation(String loggedInUserTenantId);
	long findTotalActiveSPEvaluationBizUnit(String loggedInUserTenantId,List<String> businessUnitIds);

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm getSupplierPerformanceFormDetailsByFormId(String formId);

	/**
	 * @param performanceForm
	 * @param user
	 * @param strTimeZone
	 * @param virtualizer
	 * @return
	 */
	JasperPrint getPerformancEvaluationSummaryPdf(SupplierPerformanceForm performanceForm, User user, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm getSupplierPerformanceFormByFormId(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceReminder> getAllSpFormRemindersByFormId(String formId);

	EventPermissions getUserPemissionsForForm(String id, String formId);

	void suspendForm(String form);

	XSSFWorkbook generateEvaluationReport(String formId) throws FileNotFoundException, IOException;

	/**
	 * @param evalUser
	 * @param loggedInUser
	 * @param strTimeZone
	 * @param virtualizer
	 * @return
	 */
	JasperPrint getPerformancEvaluationApprovalSummaryPdf(SupplierPerformanceEvaluatorUser evalUser, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	XSSFWorkbook downloadCsvFileForPerformanceEvaluation(String id, Date startDate, Date endDate, boolean select_all, String[] formIdArr, SearchFilterPerformanceEvaluationPojo searchFilterPerformanceEvaluationPojo, HttpSession session, String tenantId);

	SupplierPerformanceForm findScoreRatingForSupplier(String supplierId);

	/**
	 * @param tenantId
	 * @param supplierId
	 * @param startDate 
	 * @param endDate 
	 * @return
	 * @throws ApplicationException 
	 */
	SupplierPerformanceEvaluationPojo getSPFDetailsForBuyerByTenantId(String tenantId, String supplierId, Date startDate, Date endDate) throws ApplicationException;

	/**
	 * @param start
	 * @param end
	 * @param unitId
	 * @param supplierId
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreBySpFormAndBUnit(Date start, Date end, String unitId, String supplierId, String tenantId);

	/**
	 * @param tenantId
	 * @param supplierId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(String tenantId, String supplierId, Date startDate, Date endDate);

	/**
	 * @param startDate
	 * @param endDate
	 * @param formId
	 * @param supplierId
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndFormId(Date startDate, Date endDate, String formId, String supplierId, String tenantId);

	/**
	 * @param start
	 * @param end
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	SupplierPerformanceEvaluationPojo getOverallScoreOfSupplierByBuyerId(Date start, Date end, String buyerId, String supplierId);

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
	 * @param start
	 * @param end
	 * @return
	 */
	List<SupplierPerformanceForm> getEventIdListForSupplierId(String supplierId, String buyerId, Date start, Date end);

	/**
	 * @param start
	 * @param end
	 * @param eventId
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndEventId(Date start, Date end, String eventId, String buyerId, String supplierId);

	/**
	 * @param performanceForm
	 * @param user
	 * @param strTimeZone
	 * @param virtualizer
	 * @param tenantId
	 * @return
	 */
	JasperPrint getScoreCardPdf(SupplierPerformanceForm performanceForm, User user, String strTimeZone, JRSwapFileVirtualizer virtualizer, String tenantId);

	/**
	 * @param performanceForm
	 * @param user
	 * @param strTimeZone
	 * @param tenantId
	 * @return
	 */
	Map<String, Object> getScoreCard(SupplierPerformanceForm performanceForm, User user, String strTimeZone, String tenantId);

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

}
