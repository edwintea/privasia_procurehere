/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.TatReportDao;
import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.TatReportService;

/**
 * @author jayshree
 */
@Service
@Transactional(readOnly = true)
public class TatReportServiceImpl implements TatReportService {

	private static final Logger LOG = LogManager.getLogger(Global.TATREPORT_LOG);

	@Autowired
	TatReportDao tatReportDao;

	@Override
	@Transactional(readOnly = false)
	public TatReport saveTatReport(TatReport tatReport) {
		return tatReportDao.save(tatReport);
	}

	@Override
	@Transactional(readOnly = false)
	public TatReport updateTatReport(TatReport tatReport) {
		return tatReportDao.saveOrUpdate(tatReport);
	}

	@Override
	public List<TatReport> getRfsForTatReportListByRfsFormIdAndTenantId(String formId, String tenantId) {
		return tatReportDao.getRfsForTatReportListByRfsFormIdAndTenantId(formId, tenantId);
	}

	@Override
	public List<TatReport> getEventsDataForTatReportListByEventIdAndTenantId(String eventId, String tenantId) {
		return tatReportDao.getEventsDataForTatReportListByEventIdAndTenantId(eventId, tenantId);
	}

	@Override
	public TatReport getEventsDataForTatReportListByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId) {
		return tatReportDao.getEventsDataForTatReportListByEventIdAndFormIdAndTenantId(requestGeneratedId, eventId, tenantId);
	}

	@Override
	public TatReport getRfsForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		return tatReportDao.getRfsForTatReportListByRfsFormIdAndIdAndTenantId(requestGeneratedId, tenantId);
	}

	@Override
	public TatReport getEventsDataForTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		return tatReportDao.getEventsDataForTatReportByEventIdAndTenantId(eventGeneratedId, tenantId);
	}

	@Override
	public List<TatReport> getTatReportDataForListByTenantId(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return tatReportDao.getTatReportDataForListByTenantId(tenantId, input, startDate, endDate);
	}

	@Override
	public long getTatReportsCountForListByTenantId(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return tatReportDao.getTatReportsCountForListByTenantId(tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalTatReportsForListByTenantId(String tenantId) {
		return tatReportDao.findTotalTatReportsForListByTenantId(tenantId);
	}

	@Override
	public List<TatReport> getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		return tatReportDao.getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(requestGeneratedId, tenantId);
	}

	@Override
	public void downloadCsvFileForTatReport(File file, TatReportPojo tatReport, SimpleDateFormat sdf) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.TAT_REPORT_CSV_COLUMNS;

			final String[] columns = new String[] { "businessUnit", "costCenter", "groupCode", "requestOwner", "sourcingFormName", "formDescription", "formId", "requestStatus", "procurementMethod", "procurementCategories", "sapPrId", "strReqCreatedDate", "strReqFinishDate", "availableBudget", "estimatedBudget", "strReqRejectedDate", "strFirstApprovedDate", "strLastApprovedDate", "reqApprovalDaysCount", "eventType", "eventId", "status", "eventName", "strEventCreatedDate", "eventOwner", "referanceNumber", "invitedSupplierCount", "acceptedSupplierCount", "submitedSupplierCount", "strEventFinishDate", "strEventFirstApprovedDate", "strEventLastApprovedDate", "strEventRejectedDate", "eventApprovalDaysCount", "strEventPublishDate", "strEventStart", "strEventEnd", "strEventFirstMeetingDate", "eventOpenDuration", "strFirstEnvelopOpenDate", "strEvaluationCompletedDate", "strUnmskingDate", "strEventConcludeDate", "strEventAwardDate", "awardedSupplier", "sapPoId", "strSapPocreatedDate", "awardValue", "paperApprovalDaysCount", "overallTat" };

			long count = tatReportDao.findTotalTatReportsForListByTenantId(tatReport.getTenantId());

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<TatReport> list = tatReportDao.findAllTatReportForTenantIdForCsv(PAGE_SIZE, pageNo, tatReport);
				LOG.info("size ........" + list.size() + ".... count " + count);
				for (TatReport pojo : list) {

					if (pojo.getCreatedDate() != null) {
						pojo.setStrReqCreatedDate(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getFinishDate() != null) {
						pojo.setStrReqFinishDate(sdf.format(pojo.getFinishDate()));
					}
					if (pojo.getReqRejectedDate() != null) {
						pojo.setStrReqRejectedDate(sdf.format(pojo.getReqRejectedDate()));
					}
					if (pojo.getFirstApprovedDate() != null) {
						pojo.setStrFirstApprovedDate(sdf.format(pojo.getFirstApprovedDate()));
					}
					if (pojo.getLastApprovedDate() != null) {
						pojo.setStrLastApprovedDate(sdf.format(pojo.getLastApprovedDate()));
					}
					if (pojo.getEventCreatedDate() != null) {
						pojo.setStrEventCreatedDate(sdf.format(pojo.getEventCreatedDate()));
					}
					if (pojo.getEventFinishDate() != null) {
						pojo.setStrEventFinishDate(sdf.format(pojo.getEventFinishDate()));
					}
					if (pojo.getEventFirstApprovedDate() != null) {
						pojo.setStrEventFirstApprovedDate(sdf.format(pojo.getEventFirstApprovedDate()));
					}
					if (pojo.getEventLastApprovedDate() != null) {
						pojo.setStrEventLastApprovedDate(sdf.format(pojo.getEventLastApprovedDate()));
					}
					if (pojo.getEventRejectedDate() != null) {
						pojo.setStrEventRejectedDate(sdf.format(pojo.getEventRejectedDate()));
					}
					if (pojo.getEventPublishDate() != null) {
						pojo.setStrEventPublishDate(sdf.format(pojo.getEventPublishDate()));
					}
					if (pojo.getEventStart() != null) {
						pojo.setStrEventStart(sdf.format(pojo.getEventStart()));
					}
					if (pojo.getEventEnd() != null) {
						pojo.setStrEventEnd(sdf.format(pojo.getEventEnd()));
					}
					if (pojo.getEventFirstMeetingDate() != null) {
						pojo.setStrEventFirstMeetingDate(sdf.format(pojo.getEventFirstMeetingDate()));
					}
					if (pojo.getFirstEnvelopOpenDate() != null) {
						pojo.setStrFirstEnvelopOpenDate(sdf.format(pojo.getFirstEnvelopOpenDate()));
					}

					if (pojo.getEvaluationCompletedDate() != null) {
						pojo.setStrEvaluationCompletedDate(sdf.format(pojo.getEvaluationCompletedDate()));
					}
					if (pojo.getUnmskingDate() != null) {
						pojo.setStrUnmskingDate(sdf.format(pojo.getUnmskingDate()));
					}
					if (pojo.getEventConcludeDate() != null) {
						pojo.setStrEventConcludeDate(sdf.format(pojo.getEventConcludeDate()));
					}
					if (pojo.getEventAwardDate() != null) {
						pojo.setStrEventAwardDate(sdf.format(pojo.getEventAwardDate()));
					}

					if (pojo.getSapPocreatedDate() != null) {
						pojo.setStrSapPocreatedDate(sdf.format(pojo.getSapPocreatedDate()));
					}

					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.info("Error ..." + e, e);
		}
	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new NotNull(), // SOURCING
																																// id
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // ESTIMATED
																																				// BUDGET
				new Optional(), new Optional(), new Optional(), new Optional(),

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // Event
																												// OWNER
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // Event
																																// LAST
																																// APPROVER
																																// DATE
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // EVALUATION
																																								// COMPLETED
																																								// DATE
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()

		};
		return processors;
	}

	@Override
	public TatReport getRfsForTatReportByRfsIDAndFormIdAndTenantId(String formId, String tenantId, String requestGeneratedId) {
		return tatReportDao.getRfsForTatReportByRfsIDAndFormIdAndTenantId(formId, tenantId, requestGeneratedId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventsStartDate(String eventId, EventStatus status) {
		tatReportDao.updateTatReportEventsStartDate(eventId, status);
	}

	@Override
	public TatReport getTatReportDataForTatReportByEventIdAndTenantId(String eventId, String tenantId) {
		return tatReportDao.getTatReportDataForTatReportByEventIdAndTenantId(eventId, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportOnSapResponse(String eventId, EventStatus status, String tenantId) {
		tatReportDao.updateTatReportOnSapResponse(eventId, status, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportSapPrId(String requestGeneratedId, String sapPrId, String tenantId) {
		tatReportDao.updateTatReportSapPrId(requestGeneratedId, sapPrId, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportSapoDetails(String id, String sapPoId, Date sapPocreatedDate, BigDecimal overallTat) {
		tatReportDao.updateTatReportSapoDetails(id, sapPoId, sapPocreatedDate, overallTat);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportApproved(String id, Date eventFirstApprovedDate, Date eventLastApprovedDate, BigDecimal eventApprovalDaysCount, EventStatus status) {
		tatReportDao.updateTatReportApproved(id, eventFirstApprovedDate, eventLastApprovedDate, eventApprovalDaysCount, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventFirstApprovedDate(String previousRequestId, String eventId, String tenantId, Date eventFirstApprovedDate) {
		tatReportDao.updateTatReportEventFirstApprovedDate(previousRequestId, eventId, tenantId, eventFirstApprovedDate);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventRejection(String requestGeneratedId, String eventId, String tenantId, Date eventRejectedDate, EventStatus status) {
		tatReportDao.updateTatReportEventRejection(requestGeneratedId, eventId, tenantId, eventRejectedDate, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(String eventGeneratedId, String tenantId, Date eventLastApprovedDate, BigDecimal eventApprovalDaysCount, EventStatus status) {
		tatReportDao.updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(eventGeneratedId, tenantId, eventLastApprovedDate, eventApprovalDaysCount, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportReqApprovedAndApprovalDaysCountAndLastApprovedDate(String id, Date lastApprovedDate, BigDecimal reqApprovalDaysCount, SourcingFormStatus requestStatus) {
		tatReportDao.updateTatReportReqApprovedAndApprovalDaysCountAndLastApprovedDate(id, lastApprovedDate, reqApprovalDaysCount, requestStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportReqRejection(String id, Date reqRejectedDate, SourcingFormStatus requestStatus) {
		tatReportDao.updateTatReportReqRejection(id, reqRejectedDate, requestStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportReqFirstApprovedDate(String requestGeneratedId, String tenantId, Date firstApprovedDate) {
		tatReportDao.updateTatReportReqFirstApprovedDate(requestGeneratedId, tenantId, firstApprovedDate);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportAwardDetails(String id, String awardedSupplier, String awardValue, Date eventAwardDate, EventStatus status, Date eventConcludeDate, Date eventFinishedDate, BigDecimal PaperApprovalDaysCount) {
		tatReportDao.updateTatReportAwardDetails(id, awardedSupplier, awardValue, eventAwardDate, status, eventConcludeDate, eventFinishedDate, PaperApprovalDaysCount);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEvaluationCompleted(String eventGeneratedId, String tenantId, Date evaluationCompleted, EventStatus status) {
		tatReportDao.updateTatReportEvaluationCompleted(eventGeneratedId, tenantId, evaluationCompleted, status);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(String eventId, EventStatus status) {
		tatReportDao.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(eventId, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventFirstMeetingDate(String id, Date eventFirstMeetingDate) {
		tatReportDao.updateTatReportEventFirstMeetingDate(id, eventFirstMeetingDate);
	}

	@Override
	@Transactional(readOnly = false)
	public long updateTatReportReqStatus(String formId, String tenantId, String requestGeneratedId, SourcingFormStatus requestStatus) {
		return tatReportDao.updateTatReportReqStatus(formId, tenantId, requestGeneratedId, requestStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventStatus(String eventId, String requestGeneratedId, String tenantId, EventStatus status) {
		tatReportDao.updateTatReportEventStatus(eventId, requestGeneratedId, tenantId, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventDetails(String id, String eventGeneratedId, Date eventStart, Date eventEnd, Date eventPublishDate, String eventName, String referanceNumber) {
		tatReportDao.updateTatReportEventDetails(id, eventGeneratedId, eventStart, eventEnd, eventPublishDate, eventName, referanceNumber);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTatReportFirstEnvelopOpenDate(String eventGeneratedId, String tenantId, EventStatus status) {
		tatReportDao.updateTatReportFirstEnvelopOpenDate(eventGeneratedId, tenantId, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportUnmskingDate(String id, Date unmskingDate) {
		tatReportDao.updateTatReportUnmskingDate(id, unmskingDate);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportFinishDateAndDecimalAndStatus(String id, Date eventFinishedDate, String eventDecimal, EventStatus status) {
		tatReportDao.updateTatReportFinishDateAndDecimalAndStatus(id, eventFinishedDate, eventDecimal, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportReqFinishDateAndStatus(String id, Date finishDate, SourcingFormStatus requestStatus) {
		tatReportDao.updateTatReportReqFinishDateAndStatus(id, finishDate, requestStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportReqDetails(String id, String requestGeneratedId, String formId, String sourcingFormName, String formDescription, String businessUnit, String costCenter, String requestOwner, String groupCode, BigDecimal availableBudget, BigDecimal estimatedBudget, Date createdDate, String procurementMethod, String procurementCategories, String baseCurrency, SourcingFormStatus requestStatus, String reqDecimal, String tenantId) {
		tatReportDao.updateTatReportReqDetails(id, requestGeneratedId, formId, sourcingFormName, formDescription, businessUnit, costCenter, requestOwner, groupCode, availableBudget, estimatedBudget, createdDate, procurementMethod, procurementCategories, baseCurrency, requestStatus, reqDecimal, tenantId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public TatReportPojo geTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		return tatReportDao.geTatReportByEventIdAndTenantId(eventGeneratedId, tenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public Date getEventFinishDateByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		return tatReportDao.getEventFinishDateByEventIdAndTenantId(eventGeneratedId, tenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public long getRfsCountForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		return tatReportDao.getRfsCountForTatReportListByRfsFormIdAndIdAndTenantId(requestGeneratedId, tenantId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public TatReportPojo getTatReportListByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId) {
		return tatReportDao.getTatReportListByEventIdAndFormIdAndTenantId(requestGeneratedId, eventId, tenantId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public TatReportPojo geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		return tatReportDao.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventGeneratedId, tenantId);
	}

	@Override
	public long getEventCountByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		return tatReportDao.getEventCountByEventIdAndTenantId(eventGeneratedId, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventStatus(String eventId, String tenantId, EventStatus status) {
		tatReportDao.updateTatReportEventStatus(eventId, tenantId, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportEventStatusById(String eventGeneratedId, String tenantId, EventStatus status) {
		tatReportDao.updateTatReportEventStatusById(eventGeneratedId, tenantId, status);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public TatReportPojo getTatReportByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId) {
		return tatReportDao.getTatReportByEventIdAndFormIdAndTenantId(requestGeneratedId, eventId, tenantId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public TatReportPojo geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		return tatReportDao.geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(eventGeneratedId, tenantId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public TatReportPojo getRfsFinishDatetByRfsIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		return tatReportDao.getRfsFinishDatetByRfsIdAndIdAndTenantId(requestGeneratedId, tenantId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public long getRfsCountForTatReportByRfsIDAndFormIdAndTenantId(String formId, String tenantId, String requestGeneratedId) {
		return tatReportDao.getRfsCountForTatReportByRfsIDAndFormIdAndTenantId(formId, tenantId, requestGeneratedId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTatReportReqApprovedAndApprovalDaysCountAndFirstAndLastApprovedDate(String id, Date lastApprovedDate, BigDecimal reqApprovalDaysCount, Date firstApprovedDate) {
		tatReportDao.updateTatReportReqApprovedAndApprovalDaysCountAndFirstAndLastApprovedDate(id, lastApprovedDate, reqApprovalDaysCount, firstApprovedDate);
	}
}
