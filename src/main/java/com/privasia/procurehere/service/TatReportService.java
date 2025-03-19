/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;

/**
 * @author jayshree
 */
public interface TatReportService {

	/**
	 * @param tatReport
	 * @return
	 */
	TatReport saveTatReport(TatReport tatReport);

	/**
	 * @param tatReport
	 * @return
	 */
	TatReport updateTatReport(TatReport tatReport);

	/**
	 * @param formId
	 * @param tenantId
	 * @return
	 */
	List<TatReport> getRfsForTatReportListByRfsFormIdAndTenantId(String formId, String tenantId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	List<TatReport> getEventsDataForTatReportListByEventIdAndTenantId(String eventId, String tenantId);

	/**
	 * @param formId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	TatReport getEventsDataForTatReportListByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId);

	/**
	 * @param requestGeneratedId
	 * @param tenantId
	 * @return
	 */
	TatReport getRfsForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @return
	 */
	TatReport getEventsDataForTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<TatReport> getTatReportDataForListByTenantId(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long getTatReportsCountForListByTenantId(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalTatReportsForListByTenantId(String tenantId);

	/**
	 * @param requestGeneratedId
	 * @param tenantId
	 * @return
	 */
	List<TatReport> getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId);

	/**
	 * @param file
	 * @param tatReport
	 * @param sdf
	 */
	void downloadCsvFileForTatReport(File file, TatReportPojo tatReport, SimpleDateFormat sdf);

	/**
	 * @param formId
	 * @param tenantId
	 * @param requestGeneratedId
	 * @return
	 */
	TatReport getRfsForTatReportByRfsIDAndFormIdAndTenantId(String formId, String tenantId, String requestGeneratedId);

	/**
	 * @param eventId
	 * @param status
	 */
	void updateTatReportEventsStartDate(String eventId, EventStatus status);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	TatReport getTatReportDataForTatReportByEventIdAndTenantId(String eventId, String tenantId);

	/**
	 * @param eventId
	 * @param status
	 * @param tenantId
	 */
	void updateTatReportOnSapResponse(String eventId, EventStatus status, String tenantId);

	/**
	 * @param requestGeneratedId
	 * @param sapPrId
	 * @param tenantId TODO
	 */
	void updateTatReportSapPrId(String requestGeneratedId, String sapPrId, String tenantId);

	/**
	 * @param id
	 * @param sapPoId
	 * @param sapPocreatedDate
	 * @param overallTat
	 */
	void updateTatReportSapoDetails(String id, String sapPoId, Date sapPocreatedDate, BigDecimal overallTat);

	/**
	 * @param id
	 * @param eventFirstApprovedDate
	 * @param eventLastApprovedDate
	 * @param eventApprovalDaysCount
	 * @param status
	 */
	void updateTatReportApproved(String id, Date eventFirstApprovedDate, Date eventLastApprovedDate, BigDecimal eventApprovalDaysCount, EventStatus status);

	/**
	 * @param previousRequestId
	 * @param eventId TODO
	 * @param tenantId TODO
	 * @param eventFirstApprovedDate
	 */
	void updateTatReportEventFirstApprovedDate(String previousRequestId, String eventId, String tenantId, Date eventFirstApprovedDate);

	/**
	 * @param requestGeneratedId
	 * @param eventId TODO
	 * @param tenantId TODO
	 * @param eventRejectedDate
	 * @param status
	 */
	void updateTatReportEventRejection(String requestGeneratedId, String eventId, String tenantId, Date eventRejectedDate, EventStatus status);

	/**
	 * @param eventGeneratedId
	 * @param tenantId TODO
	 * @param eventLastApprovedDate
	 * @param eventApprovalDaysCount
	 * @param status
	 */
	void updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(String eventGeneratedId, String tenantId, Date eventLastApprovedDate, BigDecimal eventApprovalDaysCount, EventStatus status);

	/**
	 * @param id
	 * @param lastApprovedDate
	 * @param reqApprovalDaysCount
	 * @param requestStatus
	 */
	void updateTatReportReqApprovedAndApprovalDaysCountAndLastApprovedDate(String id, Date lastApprovedDate, BigDecimal reqApprovalDaysCount, SourcingFormStatus requestStatus);

	/**
	 * @param id
	 * @param reqRejectedDate
	 * @param requestStatus
	 */
	void updateTatReportReqRejection(String id, Date reqRejectedDate, SourcingFormStatus requestStatus);

	/**
	 * @param requestGeneratedId
	 * @param tenantId TODO
	 * @param firstApprovedDate
	 */
	void updateTatReportReqFirstApprovedDate(String requestGeneratedId, String tenantId, Date firstApprovedDate);

	/**
	 * @param id
	 * @param awardedSupplier
	 * @param awardValue
	 * @param eventAwardDate
	 * @param status
	 * @param eventConcludeDate
	 * @param eventFinishedDate
	 * @param PaperApprovalDaysCount
	 */
	void updateTatReportAwardDetails(String id, String awardedSupplier, String awardValue, Date eventAwardDate, EventStatus status, Date eventConcludeDate, Date eventFinishedDate, BigDecimal PaperApprovalDaysCount);

	/**
	 * @param eventGeneratedId
	 * @param tenantId TODO
	 * @param evaluationCompleted
	 * @param status
	 */
	void updateTatReportEvaluationCompleted(String eventGeneratedId, String tenantId, Date evaluationCompleted, EventStatus status);

	/**
	 * @param eventId
	 * @param status
	 */
	void updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(String eventId, EventStatus status);

	/**
	 * @param id
	 * @param eventFirstMeetingDate
	 */
	void updateTatReportEventFirstMeetingDate(String id, Date eventFirstMeetingDate);

	/**
	 * @param formId
	 * @param tenantId TODO
	 * @param requestGeneratedId TODO
	 * @param requestStatus
	 * @return TODO
	 */
	long updateTatReportReqStatus(String formId, String tenantId, String requestGeneratedId, SourcingFormStatus requestStatus);

	/**
	 * @param eventId
	 * @param requestGeneratedId TODO
	 * @param tenantId TODO
	 * @param status
	 */
	void updateTatReportEventStatus(String eventId, String requestGeneratedId, String tenantId, EventStatus status);

	/**
	 * @param id
	 * @param eventGeneratedId
	 * @param eventStart
	 * @param eventEnd
	 * @param eventPublishDate
	 * @param eventName
	 * @param referanceNumber
	 */
	void updateTatReportEventDetails(String id, String eventGeneratedId, Date eventStart, Date eventEnd, Date eventPublishDate, String eventName, String referanceNumber);

	/**
	 * @param id
	 * @param unmskingDate
	 */
	void updateTatReportUnmskingDate(String id, Date unmskingDate);

	/**
	 * @param id
	 * @param eventFinishedDate
	 * @param eventDecimal
	 * @param status
	 */
	void updateTatReportFinishDateAndDecimalAndStatus(String id, Date eventFinishedDate, String eventDecimal, EventStatus status);

	/**
	 * @param id
	 * @param finishDate
	 * @param requestStatus
	 */
	void updateTatReportReqFinishDateAndStatus(String id, Date finishDate, SourcingFormStatus requestStatus);

	/**
	 * @param id
	 * @param requestGeneratedId
	 * @param formId
	 * @param sourcingFormName
	 * @param formDescription
	 * @param businessUnit
	 * @param costCenter
	 * @param requestOwner
	 * @param groupCode
	 * @param availableBudget
	 * @param estimatedBudget
	 * @param createdDate
	 * @param procurementMethod
	 * @param procurementCategories
	 * @param baseCurrency
	 * @param requestStatus
	 * @param reqDecimal
	 * @param tenantId
	 */
	void updateTatReportReqDetails(String id, String requestGeneratedId, String formId, String sourcingFormName, String formDescription, String businessUnit, String costCenter, String requestOwner, String groupCode, BigDecimal availableBudget, BigDecimal estimatedBudget, Date createdDate, String procurementMethod, String procurementCategories, String baseCurrency, SourcingFormStatus requestStatus, String reqDecimal, String tenantId);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @return
	 */
	TatReportPojo geTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @return
	 */
	Date getEventFinishDateByEventIdAndTenantId(String eventGeneratedId, String tenantId);

	/**
	 * @param requestGeneratedId
	 * @param tenantId
	 * @return
	 */
	long getRfsCountForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId);

	/**
	 * @param requestGeneratedId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	TatReportPojo getTatReportListByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @return
	 */
	TatReportPojo geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(String eventGeneratedId, String tenantId);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @return
	 */
	long getEventCountByEventIdAndTenantId(String eventGeneratedId, String tenantId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @param status
	 */

	void updateTatReportEventStatus(String eventId, String tenantId, EventStatus status);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @param status
	 */
	void updateTatReportEventStatusById(String eventGeneratedId, String tenantId, EventStatus status);

	/**
	 * @param requestGeneratedId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	TatReportPojo getTatReportByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @return
	 */
	TatReportPojo geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(String eventGeneratedId, String tenantId);

	/**
	 * @param requestGeneratedId
	 * @param tenantId
	 * @return
	 */
	TatReportPojo getRfsFinishDatetByRfsIdAndIdAndTenantId(String requestGeneratedId, String tenantId);

	/**
	 * @param formId
	 * @param tenantId
	 * @param requestGeneratedId
	 * @return
	 */
	long getRfsCountForTatReportByRfsIDAndFormIdAndTenantId(String formId, String tenantId, String requestGeneratedId);

	/**
	 * @param id
	 * @param lastApprovedDate
	 * @param reqApprovalDaysCount
	 * @param firstApprovedDate
	 */
	void updateTatReportReqApprovedAndApprovalDaysCountAndFirstAndLastApprovedDate(String id, Date lastApprovedDate, BigDecimal reqApprovalDaysCount, Date firstApprovedDate);

	/**
	 * @param eventGeneratedId
	 * @param tenantId
	 * @param status
	 */
	void updateTatReportFirstEnvelopOpenDate(String eventGeneratedId, String tenantId, EventStatus status);
}
