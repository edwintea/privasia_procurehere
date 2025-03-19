/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.ScoreRatingDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceAuditDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceCriteriaDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceEvaluationDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceEvaluatorUserDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApproval;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApprovalUser;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.entity.SpFormEvaluationAppComment;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;
import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.ConsolidateScorePojo;
import com.privasia.procurehere.core.pojo.EvaluationApprovalsPojo;
import com.privasia.procurehere.core.pojo.EvaluationAprovalUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuditPojo;
import com.privasia.procurehere.core.pojo.EvaluationCommentsPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.PerformanceEvaluationCriteriaPojo;
import com.privasia.procurehere.core.pojo.ScoreCardPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ScoreRatingService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluationService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluatorUserService;
import com.privasia.procurehere.service.SupplierPerformanceFormCriteriaService;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author priyanka
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceEvaluationServiceImpl implements SupplierPerformanceEvaluationService {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceEvaluationServiceImpl.class);

	@Autowired
	SupplierPerformanceEvaluationDao supplierPerformanceEvaluationDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	SupplierPerformanceAuditDao supplierPerformanceAuditDao;

	@Autowired
	SupplierPerformanceFormCriteriaService supplierPerformanceFormCriteriaService;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	SupplierPerformanceEvaluatorUserDao supplierPerformanceEvaluatorUserDao;

	@Autowired
	SupplierPerformanceEvaluatorUserService supplierPerformanceEvaluatorUserService;

	@Autowired
	ScoreRatingDao scoreRatingDao;

	SupplierPerformanceCriteriaDao supplierPerformanceCriteriaDao;

	@Autowired
	ScoreRatingService scoreRatingService;

	@Autowired
	BuyerDao buyerDao;

	@Override
	public List<SupplierPerformanceEvaluationPojo> findPendingSPEvaluation(String loggedInUserTenantId, String id, TableDataInput input) {
		return supplierPerformanceEvaluationDao.findPendingSPEvaluation(loggedInUserTenantId, id, input);
	}

	@Override
	public long findTotalPendingSPEvaluation(String loggedInUserTenantId, String id, TableDataInput input) {
		return supplierPerformanceEvaluationDao.findTotalPendingSPEvaluation(loggedInUserTenantId, id, input);
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> findSPEvaluationPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input) {
		return supplierPerformanceEvaluationDao.findSPEvaluationPendingApprovals(loggedInUserTenantId, id, input);
	}

	@Override
	public long findCountOfSPEvaluationPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input) {
		return supplierPerformanceEvaluationDao.findCountOfSPEvaluationPendingApprovals(loggedInUserTenantId, id, input);
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId) {
		return supplierPerformanceEvaluationDao.findSupplierPerformanceEvaluation(loggedInUserId, input, startDate, endDate, tenantId);
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds) {
		//return supplierPerformanceEvaluationDao.findSupplierPerformanceEvaluation(loggedInUserId, input, startDate, endDate, tenantId);
		return supplierPerformanceEvaluationDao.findSupplierPerformanceEvaluationBizUnit(loggedInUserId, input, startDate, endDate, tenantId,businessUnitIds);
	}

	@Override
	public long findTotalFilteredSPEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId) {
		return supplierPerformanceEvaluationDao.findTotalFilteredSPEvaluation(loggedInUserId, input, startDate, endDate, tenantId);
	}

	@Override
	public long findTotalFilteredSPEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds) {
		return supplierPerformanceEvaluationDao.findTotalFilteredSPEvaluationBizUnit(loggedInUserId, input, startDate, endDate, tenantId,businessUnitIds);
	}

	@Override
	public long findTotalActiveSPEvaluation(String loggedInUserTenantId) {
		return supplierPerformanceEvaluationDao.findTotalActiveSPEvaluation(loggedInUserTenantId);
	}

	@Override
	public long findTotalActiveSPEvaluationBizUnit(String loggedInUserTenantId,List<String> businessUnitIds) {
		//return supplierPerformanceEvaluationDao.findTotalActiveSPEvaluation(loggedInUserTenantId);
		return supplierPerformanceEvaluationDao.findTotalActiveSPEvaluationBizUnit(loggedInUserTenantId,businessUnitIds);
	}

	@Override
	public SupplierPerformanceForm getSupplierPerformanceFormDetailsByFormId(String formId) {
		SupplierPerformanceForm sup = supplierPerformanceEvaluationDao.getSupplierPerformanceFormDetailsByFormId(formId);
		if (sup.getBusinessUnit() != null) {
			sup.getBusinessUnit().getUnitName();
		}

		if (sup.getCreatedBy() != null) {
			sup.getCreatedBy().getName();
		}

		if (sup.getProcurementCategory() != null) {
			sup.getProcurementCategory().getProcurementCategories();
		}

		if (sup.getAwardedSupplier() != null) {
			sup.getAwardedSupplier().getCompanyName();
		}
		return sup;
	}

	@Override
	public SupplierPerformanceForm getSupplierPerformanceFormByFormId(String formId) {
		SupplierPerformanceForm sup = supplierPerformanceEvaluationDao.findById(formId);
		if (sup.getBusinessUnit() != null) {
			sup.getBusinessUnit().getUnitName();
		}
		if (sup.getFormOwner() != null) {
			sup.getFormOwner().getName();
		}

		if (sup.getCreatedBy() != null) {
			sup.getCreatedBy().getName();
		}

		if (sup.getProcurementCategory() != null) {
			sup.getProcurementCategory().getProcurementCategories();
		}

		if (sup.getAwardedSupplier() != null) {
			sup.getAwardedSupplier().getCompanyName();
		}

		if (CollectionUtil.isNotEmpty(sup.getEvaluators())) {
			for (SupplierPerformanceEvaluatorUser evalUser : sup.getEvaluators()) {
				evalUser.getId();
				if (evalUser.getEvaluator() != null) {
					evalUser.getEvaluator().getId();
					evalUser.getEvaluator().getName();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(sup.getFormReminder())) {
			for (SupplierPerformanceReminder reminder : sup.getFormReminder()) {
				reminder.getId();
				reminder.getReminderDate();
			}
		}

		if (sup.getTemplate() != null) {
			sup.getTemplate().getProcurementCategoryVisible();
			sup.getTemplate().getProcurementCategoryDisabled();
			sup.getTemplate().getProcurementCategoryOptional();
		}

		if (sup.getScoreRating() != null) {
			sup.getScoreRating().getId();
			sup.getScoreRating().getRating();
			sup.getScoreRating().getDescription();
		}

		return sup;
	}

	@Override
	public JasperPrint getPerformancEvaluationSummaryPdf(SupplierPerformanceForm performanceForm, User user, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		performanceForm = getSupplierPerformanceFormByFormId(performanceForm.getId());
		JasperPrint jasperPrint = null;
		List<SupplierPerformanceEvaluationPojo> summary = new ArrayList<SupplierPerformanceEvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/PerformanceEvaluationSummary.jasper");
			File jasperfile = resource.getFile();

			SupplierPerformanceEvaluationPojo formDetails = new SupplierPerformanceEvaluationPojo();
			formDetails.setFormId(performanceForm.getFormId());
			formDetails.setFormName(performanceForm.getFormName());
			String owner = "";
			if (performanceForm.getFormOwner() != null) {
				owner += performanceForm.getFormOwner().getName() + "\r\n" + performanceForm.getFormOwner().getCommunicationEmail();
			}
			formDetails.setFormOwner(owner);
			formDetails.setReferenceNumber(performanceForm.getReferenceNumber());
			formDetails.setReferenceName(performanceForm.getReferenceName());
			formDetails.setSupplierName(performanceForm.getAwardedSupplier() != null ? performanceForm.getAwardedSupplier().getCompanyName() : "");
			formDetails.setProcurementCategory(performanceForm.getProcurementCategory() != null ? performanceForm.getProcurementCategory().getProcurementCategories() : "");
			formDetails.setUnitName(performanceForm.getBusinessUnit() != null ? performanceForm.getBusinessUnit().getUnitName() : "");

			String evaluators = "";
			int i = 1;

			if (CollectionUtil.isNotEmpty(performanceForm.getEvaluators())) {
				for (SupplierPerformanceEvaluatorUser eval : performanceForm.getEvaluators()) {
					evaluators += (i++) + ". " + eval.getEvaluator().getName() + "\r\n";
				}
			}
			formDetails.setEvaluator(evaluators);

			formDetails.setEvaluationStartDateStr(performanceForm.getEvaluationStartDate() != null ? sdf.format(performanceForm.getEvaluationStartDate()) : "");
			formDetails.setEvaluationEndDateStr(performanceForm.getEvaluationEndDate() != null ? sdf.format(performanceForm.getEvaluationEndDate()) : "");

			// Evaluation Reminder
			String eventStartRemider = "";
			if (CollectionUtil.isNotEmpty(performanceForm.getFormReminder())) {
				for (SupplierPerformanceReminder item : performanceForm.getFormReminder()) {
					eventStartRemider += "Reminder Date: " + sdf.format(item.getReminderDate()) + "\r\n";
				}
			}
			LOG.info("Reminders : " + eventStartRemider);
			formDetails.setReminderDate(eventStartRemider);

			if (Boolean.TRUE == performanceForm.getIsRecurrenceEvaluation()) {
				formDetails.setEvaluationRecurrence(performanceForm.getRecurrenceEvaluation() != null ? "Allowed - " + performanceForm.getRecurrenceEvaluation().toString() + " Days" : "");
				formDetails.setNoOfRecurrenceStr(performanceForm.getNoOfRecurrence() != null ? performanceForm.getNoOfRecurrence().toString() : "");
			} else {
				formDetails.setEvaluationRecurrence("Not Allowed");
				formDetails.setNoOfRecurrenceStr("0");
			}

			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<SupplierPerformanceAudit> formAudit = supplierPerformanceAuditDao.getSupplierPerformanceAuditByFormIdForOwner(performanceForm.getId());
			if (CollectionUtil.isNotEmpty(formAudit)) {
				for (SupplierPerformanceAudit ra : formAudit) {
					EvaluationAuditPojo audit = new EvaluationAuditPojo();
					audit.setAuctionDate(ra.getActionDate() != null ? sdf.format(ra.getActionDate()) : "");
					audit.setAuctionBy(StringUtils.checkString(ra.getActionBy().getName()).length() > 0 ? ra.getActionBy().getName() : "");
					audit.setAuction(ra.getAction().name());
					audit.setDescription(ra.getDescription());
					auditList.add(audit);
				}
			}
			formDetails.setAuditDetails(auditList);

			List<PerformanceEvaluationCriteriaPojo> criteriaList = new ArrayList<PerformanceEvaluationCriteriaPojo>();
			List<SupplierPerformanceFormCriteria> dbCriteriaList = supplierPerformanceFormCriteriaService.getSPFormCriteriaByFormId(performanceForm.getId());
			if (CollectionUtil.isNotEmpty(dbCriteriaList)) {
				for (SupplierPerformanceFormCriteria dbCriteria : dbCriteriaList) {
					PerformanceEvaluationCriteriaPojo criteria = new PerformanceEvaluationCriteriaPojo();
					criteria.setLevel(dbCriteria.getLevel() + "." + dbCriteria.getOrder());
					criteria.setCriteriaName(dbCriteria.getName());
					criteria.setMaximumScore(dbCriteria.getMaximumScore() != null ? dbCriteria.getMaximumScore().toString() : "");
					criteria.setWeightage(dbCriteria.getWeightage() != null ? dbCriteria.getWeightage().toString() : "");
					criteriaList.add(criteria);
				}
			}
			formDetails.setCriteriaList(criteriaList);

			summary.add(formDetails);

			parameters.put("EVALUATION_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public List<SupplierPerformanceReminder> getAllSpFormRemindersByFormId(String formId) {
		return supplierPerformanceEvaluationDao.getAllSpFormRemindersByFormId(formId);
	}

	@Override
	public EventPermissions getUserPemissionsForForm(String id, String formId) {
		return supplierPerformanceEvaluationDao.getUserPemissionsForForm(id, formId);
	}

	@Override
	@Transactional(readOnly = false)
	public void suspendForm(String form) {
		supplierPerformanceEvaluationDao.suspendForm(form);
	}

	@Override
	public XSSFWorkbook downloadCsvFileForPerformanceEvaluation(String loggedInUserId, Date startDate, Date endDate, boolean select_all, String[] formIdArr, SearchFilterPerformanceEvaluationPojo searchFilterPerformanceEvaluationPojo, HttpSession session, String tenantId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Performance Evaluation Report ");

			List<SupplierPerformanceEvaluationPojo> list = getAllPerformanceEvaluationForCsv(loggedInUserId, startDate, endDate, formIdArr, select_all, searchFilterPerformanceEvaluationPojo, tenantId);
			int r = 1;
			if (CollectionUtil.isNotEmpty(list)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

				int maxCriteriaSize = 0;
				for (SupplierPerformanceEvaluationPojo pojo : list) {
					if (CollectionUtil.isNotEmpty(pojo.getCriteriaList())) {
						maxCriteriaSize = maxCriteriaSize > pojo.getCriteriaList().size() ? maxCriteriaSize : pojo.getCriteriaList().size();
					}
					Row row = sheet.createRow(r++);
					int cellNum = 0;
					row.createCell(cellNum++).setCellValue(pojo.getFormId());
					row.createCell(cellNum++).setCellValue(pojo.getFormName());
					row.createCell(cellNum++).setCellValue(pojo.getReferenceNumber() != null ? pojo.getReferenceNumber() : "");
					row.createCell(cellNum++).setCellValue(pojo.getReferenceName() != null ? pojo.getReferenceName() : "");
					row.createCell(cellNum++).setCellValue(pojo.getFormOwner() != null ? pojo.getFormOwner() : "");
					row.createCell(cellNum++).setCellValue(pojo.getProcurementCategory() != null ? pojo.getProcurementCategory() : "");
					row.createCell(cellNum++).setCellValue(pojo.getUnitName() != null ? pojo.getUnitName() : "");
					row.createCell(cellNum++).setCellValue(pojo.getUnitCode() != null ? pojo.getUnitCode() : "");
					row.createCell(cellNum++).setCellValue(pojo.getSupplierName() != null ? pojo.getSupplierName() : "");
					row.createCell(cellNum++).setCellValue(pojo.getSupplierCode() != null ? pojo.getSupplierCode() : "");
					row.createCell(cellNum++).setCellValue(pojo.getSupplierTag() != null ? pojo.getSupplierTag() : "");
					row.createCell(cellNum++).setCellValue(pojo.getTotalEvaluator() != null ? pojo.getTotalEvaluator() : 0);
					row.createCell(cellNum++).setCellValue(pojo.getTotalEvaluationComplete() != null ? pojo.getTotalEvaluationComplete() : 0);
					row.createCell(cellNum++).setCellValue(pojo.getEvaluationStartDate() != null ? sdf.format(pojo.getEvaluationStartDate()) : "");
					row.createCell(cellNum++).setCellValue(pojo.getEvaluationEndDate() != null ? sdf.format(pojo.getEvaluationEndDate()) : "");
					row.createCell(cellNum++).setCellValue(pojo.getRecurrence());
					row.createCell(cellNum++).setCellValue(pojo.getRecurrenceEvaluation() != null ? pojo.getRecurrenceEvaluation().toString() : "");
					row.createCell(cellNum++).setCellValue(pojo.getNoOfRecurrence() != null ? pojo.getNoOfRecurrence().toString() : "");
					row.createCell(cellNum++).setCellValue(pojo.getFormStatus().name());
					row.createCell(cellNum++).setCellValue(pojo.getConcludeDate() != null ? sdf.format(pojo.getConcludeDate()) : "");
					row.createCell(cellNum++).setCellValue(pojo.getScoreRating() != null ? pojo.getScoreRating().toString() : "");
					row.createCell(cellNum++).setCellValue(pojo.getOverallScore() != null ? pojo.getOverallScore().toString() : "");

					if (maxCriteriaSize > 0 && CollectionUtil.isNotEmpty(pojo.getCriteriaList())) {
						for (PerformanceEvaluationCriteriaPojo crit : pojo.getCriteriaList()) {
							row.createCell(cellNum++).setCellValue(crit.getTotalScore() != null ? crit.getTotalScore() : "");
						}
					}
				}

				buildHeader(workbook, sheet, maxCriteriaSize);
			}

			for (int i = 0; i < 22; i++) {
				sheet.autoSizeColumn(i, true);
			}
			return workbook;
		} catch (Exception e) {
			LOG.info("Error ..." + e, e);
		}
		return null;

	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet, int maxCriteriaSize) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PERFORMANCE_EVALUATION_REPORT_CSV_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
		if (maxCriteriaSize > 0) {
			int k = 1;
			for (int j = i; j < (i + maxCriteriaSize); j++) {
				Cell cell = rowHeading.createCell(j);
				cell.setCellValue("SCORE MAIN CRITERIA " + (k++));
				cell.setCellStyle(styleHeading);
			}
		}
	}

	private List<SupplierPerformanceEvaluationPojo> getAllPerformanceEvaluationForCsv(String loggedInUserId, Date startDate, Date endDate, String[] formIdArr, boolean select_all, SearchFilterPerformanceEvaluationPojo searchFilterPerformanceEvaluationPojo, String tenantId) {

		List<SupplierPerformanceForm> spForm = supplierPerformanceEvaluationDao.getAllPerformanceEvaluationForCsv(loggedInUserId, startDate, endDate, formIdArr, select_all, searchFilterPerformanceEvaluationPojo, tenantId);
		List<SupplierPerformanceEvaluationPojo> pojoList = new ArrayList<SupplierPerformanceEvaluationPojo>();
		for (SupplierPerformanceForm spf : spForm) {
			SupplierPerformanceEvaluationPojo pojo = new SupplierPerformanceEvaluationPojo(spf);
			if (spf.getFormOwner() != null) {
				pojo.setFormOwner(spf.getFormOwner().getName());
			}

			if (spf.getProcurementCategory() != null) {
				pojo.setProcurementCategory(spf.getProcurementCategory().getProcurementCategories());
			}
			if (spf.getBusinessUnit() != null) {
				pojo.setUnitName(spf.getBusinessUnit().getUnitName());
				pojo.setUnitCode(spf.getBusinessUnit().getUnitCode());
			}
			if (spf.getAwardedSupplier() != null) {
				pojo.setSupplierName(spf.getAwardedSupplier().getCompanyName());
			}
			if (spf.getScoreRating() != null) {
				pojo.setScoreRating(spf.getScoreRating().getRating());
			}

			if (CollectionUtil.isNotEmpty(spf.getEvaluators())) {
				pojo.setTotalEvaluator(Long.valueOf(spf.getEvaluators().size()));
				long totalComplete = spf.getEvaluators().stream().filter(evl -> SupperPerformanceEvaluatorStatus.APPROVED.equals(evl.getEvaluationStatus())).count();
				pojo.setTotalEvaluationComplete((int) totalComplete);
			}
			FavouriteSupplier favSupplier = favoriteSupplierDao.getSupplierDetailsBySupplierId(spf.getAwardedSupplier().getId(), tenantId);
			pojo.setSupplierCode(favSupplier.getVendorCode());
			if (CollectionUtil.isNotEmpty(favSupplier.getSupplierTags())) {
				String suptag = "";
				for (SupplierTags tags : favSupplier.getSupplierTags()) {
					if (StringUtils.checkString(tags.getSupplierTags()).length() > 0) {
						suptag += tags.getSupplierTags() + ",";
					}
				}
				pojo.setSupplierTag(suptag.substring(0, suptag.length() - 1));
			}
			if (CollectionUtil.isNotEmpty(spf.getCriteria())) {
				List<SupplierPerformanceFormCriteria> critList = spf.getCriteria().stream().filter(crit -> crit.getOrder() == 0).collect(Collectors.toList());
				if (CollectionUtil.isNotEmpty(critList)) {
					List<PerformanceEvaluationCriteriaPojo> critPojo = new ArrayList<PerformanceEvaluationCriteriaPojo>();
					for (SupplierPerformanceFormCriteria ct : critList) {
						critPojo.add(new PerformanceEvaluationCriteriaPojo(ct));
					}
					pojo.setCriteriaList(critPojo);
				}

			}
			pojoList.add(pojo);
		}
		return pojoList;
	}

	@Override
	public XSSFWorkbook generateEvaluationReport(String formId) throws IOException {
		LOG.info(" Form is ready to build ");
		XSSFWorkbook workbook = new XSSFWorkbook();
		SupplierPerformanceForm sof = getSupplierPerformanceFormByFormId(formId);
		List<SupplierPerformanceFormCriteria> criteria = sof.getCriteria();
		List<SupplierPerformanceEvaluatorUser> eval = supplierPerformanceFormDao.findEvaluatorsByFormId(formId);

		// Get only the submitted Eval Users
		List<SupplierPerformanceEvaluatorUser> list = new ArrayList<>();
		for (SupplierPerformanceEvaluatorUser usr : eval) {
			if (usr.getEvaluationStatus() == SupperPerformanceEvaluatorStatus.APPROVED) {
				list.add(usr);
			}
		}
		eval = list;

		XSSFSheet sheet = workbook.createSheet("Supplier Performance");
		// if (CollectionUtil.isNotEmpty(eval))
		{
			XSSFRow rowHeading = sheet.createRow(0);

			XSSFFont fontBold = workbook.createFont();
			fontBold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

			XSSFFont fontWhiteAndBold = workbook.createFont();
			fontWhiteAndBold.setColor(IndexedColors.WHITE.getIndex());
			fontWhiteAndBold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

			XSSFCellStyle headingAlignLeft = workbook.createCellStyle();
			headingAlignLeft.setFont(fontWhiteAndBold);
			headingAlignLeft.setAlignment(HorizontalAlignment.LEFT);
			headingAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);
			headingAlignLeft.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			headingAlignLeft.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle headingAlignRight = workbook.createCellStyle();
			headingAlignRight.setFont(fontWhiteAndBold);
			headingAlignRight.setAlignment(HorizontalAlignment.RIGHT);
			headingAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
			headingAlignRight.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			headingAlignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle textAlignLeft = workbook.createCellStyle();
			textAlignLeft.setAlignment(HorizontalAlignment.LEFT);
			textAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);

			XSSFCellStyle textAlignRight = workbook.createCellStyle();
			textAlignRight.setAlignment(HorizontalAlignment.RIGHT);
			textAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);

			XSSFCellStyle averageTotalRow = workbook.createCellStyle();
			averageTotalRow.setAlignment(HorizontalAlignment.LEFT);
			averageTotalRow.setVerticalAlignment(VerticalAlignment.CENTER);
			averageTotalRow.setFont(fontBold);
			averageTotalRow.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			averageTotalRow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle textBoldAlignLeft = workbook.createCellStyle();
			textBoldAlignLeft.setAlignment(HorizontalAlignment.LEFT);
			textBoldAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);
			textBoldAlignLeft.setFont(fontBold);
			textBoldAlignLeft.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			textBoldAlignLeft.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle textBoldAlignRight = workbook.createCellStyle();
			textBoldAlignRight.setAlignment(HorizontalAlignment.RIGHT);
			textBoldAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
			textBoldAlignRight.setFont(fontBold);
			textBoldAlignRight.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			textBoldAlignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle styleHeadingb = workbook.createCellStyle();
			styleHeadingb.setFont(fontBold);
			styleHeadingb.setAlignment(HorizontalAlignment.CENTER);
			styleHeadingb.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleHeadingb.setBorderBottom(CellStyle.BORDER_THIN);
			styleHeadingb.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleHeadingb.setBorderLeft(CellStyle.BORDER_THIN);
			styleHeadingb.setLeftBorderColor(IndexedColors.GREEN.getIndex());
			styleHeadingb.setBorderRight(CellStyle.BORDER_THIN);
			styleHeadingb.setRightBorderColor(IndexedColors.BLUE.getIndex());
			styleHeadingb.setBorderTop(CellStyle.BORDER_MEDIUM_DASHED);
			styleHeadingb.setTopBorderColor(IndexedColors.BLACK.getIndex());

			XSSFCellStyle styleHeadingAlign = workbook.createCellStyle();
			styleHeadingAlign.setFont(fontWhiteAndBold);
			styleHeadingAlign.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleHeadingAlign.setVerticalAlignment(VerticalAlignment.CENTER);
			styleHeadingAlign.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			styleHeadingAlign.setAlignment(HorizontalAlignment.RIGHT);

			int r = 1;
			int i = 0;

			i = 2;

			XSSFCell cell = null;
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
			cell = rowHeading.createCell(0);
			cell.setCellValue("Evaluator");
			cell.setCellStyle(styleHeadingb);

			int x = 1;
			for (SupplierPerformanceEvaluatorUser eu : eval) {
				int cellFirstMerge = 0;
				int lastCellForMerge = 0;
				cellFirstMerge = i;
				i = i + 5;
				lastCellForMerge = i - 1;
				sheet.addMergedRegion(new CellRangeAddress(0, 0, cellFirstMerge, lastCellForMerge));
				cell = rowHeading.createCell(cellFirstMerge);
				cell.setCellValue(eu.getEvaluator().getName());
				cell.setCellStyle(styleHeadingb);

				for (int p = cellFirstMerge + 1; p <= lastCellForMerge; p++) {
					cell = rowHeading.createCell(p);
					cell.setCellValue("");
					cell.setCellStyle(styleHeadingb);
				}
				cellFirstMerge = i;
				x = lastCellForMerge;
			}

			// Create dummy cols for empty eval
			if (CollectionUtil.isEmpty(eval)) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 6));
				cell = rowHeading.createCell(2);
				cell.setCellValue("");
				cell.setCellStyle(styleHeadingb);

			}

			cell = rowHeading.createCell(2 + (5 * (eval.size() > 0 ? eval.size() : 1)));
			cell.setCellValue("");
			cell.setCellStyle(styleHeadingb);

			i = 0;

			XSSFRow row = sheet.createRow(r++);
			XSSFCell cells1 = row.createCell(i++);
			cells1.setCellValue("No");
			cells1.setCellStyle(headingAlignLeft);

			cells1 = row.createCell(i++);
			cells1.setCellValue("Performance Criteria");
			cells1.setCellStyle(headingAlignLeft);

			for (int j = 0; j < eval.size(); j++) {

				cells1 = row.createCell(i++);
				cells1.setCellValue("Maximum Score");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Weightage");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Score");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Total Score");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Comments");
				cells1.setCellStyle(headingAlignLeft);
			}

			// If no evaluator scores - create empty cells
			if (CollectionUtil.isEmpty(eval)) {
				cells1 = row.createCell(i++);
				cells1.setCellValue("Maximum Score");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Weightage");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Score");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Total Score");
				cells1.setCellStyle(headingAlignRight);

				cells1 = row.createCell(i++);
				cells1.setCellValue("Comments");
				cells1.setCellStyle(headingAlignLeft);
			}
			cells1 = row.createCell(i++);
			cells1.setCellValue("Average Score");
			cells1.setCellStyle(headingAlignLeft);

			SupplierPerformanceFormCriteria parent = null;
			BigDecimal averageTotal = BigDecimal.ZERO;
			for (int k = 0; k < criteria.size(); k++) {
				int level = criteria.get(k).getLevel();
				int order = criteria.get(k).getOrder();
				boolean section = order == 0;

				i = 0;

				// Draw the Average Total Criteria Score row
				if (criteria.get(k).getOrder() == 0) {
					if (parent == null) {
						parent = criteria.get(k);
					} else {
						averageTotal = averageTotal.setScale(0, RoundingMode.HALF_UP);
						parent.setAverageScore(averageTotal);

						row = sheet.createRow(r++);
						sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 1, (5 * (eval.size() > 0 ? eval.size() : 1)) + 1));
						cells1 = row.createCell(0);
						cells1.setCellValue("");
						cells1.setCellStyle(averageTotalRow);
						cells1 = row.createCell(1);
						cells1.setCellValue("Average Total Criteria Score");
						cells1.setCellStyle(averageTotalRow);
						cells1 = row.createCell(2 + (5 * (eval.size() > 0 ? eval.size() : 1)));
						cells1.setCellValue(averageTotal.toString());
						cells1.setCellStyle(averageTotalRow);

						parent = criteria.get(k);
						averageTotal = BigDecimal.ZERO;
					}
				}

				row = sheet.createRow(r++);
				cells1 = row.createCell(i++);
				cells1.setCellValue(String.valueOf(criteria.get(k).getLevel()) + "." + String.valueOf(criteria.get(k).getOrder()));
				cells1.setCellStyle(section ? textBoldAlignLeft : textAlignLeft);

				cells1 = row.createCell(i++);
				cells1.setCellValue(criteria.get(k).getName());
				cells1.setCellStyle(section ? textBoldAlignLeft : textAlignLeft);

				BigDecimal average = BigDecimal.ZERO;
				for (int j = 0; j < eval.size(); j++) {
					cells1 = row.createCell(i++);
					cells1.setCellValue(criteria.get(k).getMaximumScore() != null ? criteria.get(k).getMaximumScore().toString() : "");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					if (order > 0) { // Display only sub criteria values
						cells1.setCellValue(criteria.get(k).getWeightage() != null ? criteria.get(k).getWeightage().toString() : "");
					} else {
						cells1.setCellValue("");
					}
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					BigDecimal value = null;
					if (order > 0) { // Display only sub criteria values
						value = eval.get(j).getCriteria().stream().filter(c -> c.getLevel() == level && c.getOrder() == order).findFirst().get().getEvaluatorScore();
						LOG.info("Eval Score : " + value + ", level.order : " + level + "." + order + ", Criteria : " + criteria.get(k).getName() + ", Eval : " + eval.get(j).getEvaluator().getName());
					}
					cells1.setCellValue(value != null ? value.toString() : "");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					if (order > 0) { // Display only sub criteria values
						value = eval.get(j).getCriteria().stream().filter(c -> c.getLevel() == level && c.getOrder() == order).findFirst().get().getEvaluatorTotalScore();
						if (value != null) {
							average = average.add(value);
						}
					} else {
						value = null;
					}
					cells1.setCellValue(value != null ? value.toString() : "");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);

					if (order > 0) { // Display only sub criteria values
						cells1.setCellValue(eval.get(j).getCriteria().stream().filter(c -> c.getLevel() == level && c.getOrder() == order).findFirst().get().getComments());
					} else {
						cells1.setCellValue("");
					}
					cells1.setCellStyle(section ? textBoldAlignLeft : textAlignLeft);
				}

				// If no evaluator scores - create empty cells
				if (CollectionUtil.isEmpty(eval)) {
					cells1 = row.createCell(i++);
					cells1.setCellValue("");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					cells1.setCellValue("");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					cells1.setCellValue("");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					cells1.setCellValue("");
					cells1.setCellStyle(section ? textBoldAlignRight : textAlignRight);

					cells1 = row.createCell(i++);
					cells1.setCellValue("");
					cells1.setCellStyle(section ? textBoldAlignLeft : textAlignLeft);

					if (criteria.get(k).getOrder() > 0) {
						// Average Score
						cells1 = row.createCell(i++);
						cells1.setCellValue("");
					} else {
						cells1 = row.createCell(i++);
						cells1.setCellValue("");
						cells1.setCellStyle(section ? textBoldAlignLeft : textAlignLeft);
					}

				} else {
					if (criteria.get(k).getOrder() > 0) {
						// Average Score
						cells1 = row.createCell(i++);
						average = average.divide(new BigDecimal(eval.size()), 2, RoundingMode.HALF_UP);
						averageTotal = averageTotal.add(average);
						cells1.setCellValue(average.toString());
					} else {
						cells1 = row.createCell(i++);
						cells1.setCellValue("");
						cells1.setCellStyle(section ? textBoldAlignLeft : textAlignLeft);
					}
				}

			}
			if (parent != null) {
				row = sheet.createRow(r++);
				sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 1, (5 * (eval.size() > 0 ? eval.size() : 1)) + 1));
				cells1 = row.createCell(0);
				cells1.setCellValue("");
				cells1.setCellStyle(averageTotalRow);
				cells1 = row.createCell(1);
				cells1.setCellValue("Average Total Criteria Score");
				cells1.setCellStyle(averageTotalRow);
				cells1 = row.createCell(2 + (5 * (eval.size() > 0 ? eval.size() : 1)));
				cells1.setCellValue(averageTotal.setScale(0, RoundingMode.HALF_UP).toString());
				cells1.setCellStyle(averageTotalRow);
			}

		}

		// Auto size all the columns
		for (int x = 0; x < sheet.getRow(0).getPhysicalNumberOfCells(); x++) {
			sheet.autoSizeColumn(x);
		}

		return workbook;
	}

	@Override
	public JasperPrint getPerformancEvaluationApprovalSummaryPdf(SupplierPerformanceEvaluatorUser evalUser, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {

		SupplierPerformanceForm performanceForm = getSupplierPerformanceFormByFormId(evalUser.getForm().getId());

		SupplierPerformanceEvaluatorUser evaluationUser = supplierPerformanceEvaluatorUserService.getEvaluationUserById(evalUser.getId());

		JasperPrint jasperPrint = null;
		List<SupplierPerformanceEvaluationPojo> summary = new ArrayList<SupplierPerformanceEvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/PerformanceEvaluationApproverSummary.jasper");
			String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();

			SupplierPerformanceEvaluationPojo formDetails = new SupplierPerformanceEvaluationPojo();
			formDetails.setFormId(performanceForm.getFormId());
			formDetails.setFormName(performanceForm.getFormName());
			String owner = "";
			if (performanceForm.getFormOwner() != null) {
				owner += performanceForm.getFormOwner().getName() + "\r\n" + performanceForm.getFormOwner().getCommunicationEmail();
			}
			formDetails.setFormOwner(owner);
			formDetails.setReferenceNumber(performanceForm.getReferenceNumber());
			formDetails.setReferenceName(performanceForm.getReferenceName());
			formDetails.setSupplierName(performanceForm.getAwardedSupplier() != null ? performanceForm.getAwardedSupplier().getCompanyName() : "");
			formDetails.setProcurementCategory(performanceForm.getProcurementCategory() != null ? performanceForm.getProcurementCategory().getProcurementCategories() : "");
			formDetails.setUnitName(performanceForm.getBusinessUnit() != null ? performanceForm.getBusinessUnit().getUnitName() : "");

			String evaluators = "";

			if (CollectionUtil.isNotEmpty(performanceForm.getEvaluators())) {
				evaluators = evaluationUser.getEvaluator().getName();
			}
			formDetails.setEvaluator(evaluators);

			formDetails.setEvaluationStartDateStr(performanceForm.getEvaluationStartDate() != null ? sdf.format(performanceForm.getEvaluationStartDate()) : "");
			formDetails.setEvaluationEndDateStr(performanceForm.getEvaluationEndDate() != null ? sdf.format(performanceForm.getEvaluationEndDate()) : "");

			// Evaluation Reminder
			String eventStartRemider = "";
			if (CollectionUtil.isNotEmpty(performanceForm.getFormReminder())) {
				for (SupplierPerformanceReminder item : performanceForm.getFormReminder()) {
					eventStartRemider += "Reminder Date: " + item.getReminderDate() + "\r\n";
				}
			}
			LOG.info("Reminders : " + eventStartRemider);
			formDetails.setReminderDate(eventStartRemider);

			formDetails.setEvaluationRecurrence(performanceForm.getRecurrenceEvaluation() != null ? performanceForm.getRecurrenceEvaluation().toString() : "");
			formDetails.setEvaluationDueBy(performanceForm.getEvaluationDuration() != null ? performanceForm.getEvaluationDuration() : 0);

			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<SupplierPerformanceAudit> formAudit = supplierPerformanceAuditDao.getSupplierPerformanceAuditByFormIdForEvaluater(performanceForm.getId(), evalUser.getId());
			if (CollectionUtil.isNotEmpty(formAudit)) {
				for (SupplierPerformanceAudit ra : formAudit) {
					EvaluationAuditPojo audit = new EvaluationAuditPojo();
					audit.setAuctionDate(ra.getActionDate() != null ? sdf.format(ra.getActionDate()) : "");
					audit.setAuctionBy(StringUtils.checkString(ra.getActionBy().getName()).length() > 0 ? ra.getActionBy().getName() : "");
					audit.setAuction(ra.getAction().name());
					audit.setDescription(ra.getDescription());
					auditList.add(audit);
				}
			}
			formDetails.setAuditDetails(auditList);

			List<PerformanceEvaluationCriteriaPojo> criteriaList = new ArrayList<PerformanceEvaluationCriteriaPojo>();
			List<SupplierPerformanceCriteria> dbCriteriaList = evaluationUser.getCriteria();
			if (CollectionUtil.isNotEmpty(dbCriteriaList)) {
				for (SupplierPerformanceCriteria dbCriteria : dbCriteriaList) {
					PerformanceEvaluationCriteriaPojo criteria = new PerformanceEvaluationCriteriaPojo();
					criteria.setLevel(dbCriteria.getLevel() + "." + dbCriteria.getOrder());
					criteria.setCriteriaName(dbCriteria.getName());
					criteria.setMaximumScore(dbCriteria.getMaximumScore() != null ? dbCriteria.getMaximumScore().toString() : "");
					criteria.setWeightage(dbCriteria.getWeightage() != null ? dbCriteria.getWeightage().toString() : "");
					criteria.setScore(dbCriteria.getEvaluatorScore() != null ? dbCriteria.getEvaluatorScore().toString() : "");
					criteria.setTotalScore(dbCriteria.getEvaluatorTotalScore() != null ? dbCriteria.getEvaluatorTotalScore().toString() : "");
					criteria.setComments(StringUtils.checkString(dbCriteria.getComments()).length() > 0 ? dbCriteria.getComments() : "");

					criteriaList.add(criteria);
				}
			}
			formDetails.setCriteriaList(criteriaList);

			List<PerformanceEvaluationCriteriaPojo> consolCriteriaList = new ArrayList<PerformanceEvaluationCriteriaPojo>();
			if (CollectionUtil.isNotEmpty(dbCriteriaList)) {
				for (SupplierPerformanceCriteria dbCriteria : dbCriteriaList) {
					if (dbCriteria.getOrder().equals(0)) {
						PerformanceEvaluationCriteriaPojo criteria = new PerformanceEvaluationCriteriaPojo();
						criteria.setLevel(dbCriteria.getLevel() + "." + dbCriteria.getOrder());
						criteria.setCriteriaName(dbCriteria.getName());
						criteria.setTotalScore(dbCriteria.getEvaluatorTotalScore() != null ? dbCriteria.getEvaluatorTotalScore().toString() : "");
						criteria.setRating(evaluationUser.getScoreRating() != null ? evaluationUser.getScoreRating().getRating().toString() : "");
						criteria.setRatingDescription(evaluationUser.getScoreRating() != null && StringUtils.checkString(evaluationUser.getScoreRating().getDescription()).length() > 0 ? evaluationUser.getScoreRating().getDescription() : "");
						criteria.setOverallScoreStr(evaluationUser.getOverallScore() != null ? evaluationUser.getOverallScore().toString() : "");
						consolCriteriaList.add(criteria);
					}
				}
			}
			LOG.info(">>>>>>>>>>>>>>>>>> LIST SIZE" + consolCriteriaList.size());

			formDetails.setConsolCriteriaList(consolCriteriaList);

			// Event Approvals
			List<PerformanceEvaluationApproval> approvals = evaluationUser.getEvaluationApprovals();
			List<EvaluationApprovalsPojo> approvalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (PerformanceEvaluationApproval item : approvals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (PerformanceEvaluationApprovalUser usr : item.getApprovalUsers()) {
							EvaluationAprovalUsersPojo usrs = new EvaluationAprovalUsersPojo();
							Integer cnt = item.getApprovalUsers().size();

							if (usr.getApprovalStatus() == ApprovalStatus.APPROVED && usr.getApproval().getApprovalType() == ApprovalType.OR) {
								statusOrFlag = true;
							}
							if (usr.getApprovalStatus() == ApprovalStatus.PENDING && usr.getApproval().getApprovalType() == ApprovalType.AND) {
								statusAndFlag = true;
							}

							userName += usr.getUser().getName() + "  ";
							if (cnt > index) {
								userName += usr.getApproval().getApprovalType() != null ? usr.getApproval().getApprovalType().name() : "";
								userName += "  ";
							}
							if (cnt == index) {
								usrs.setName(userName);
								usrs.setType(usr.getApproval().getApprovalType().name());
								if (statusOrFlag) {
									usrs.setStatus("APPROVED");
								} else if (statusAndFlag) {
									usrs.setStatus("PENDING");
								} else {
									usrs.setStatus(usr.getApprovalStatus().name());
								}
								usrs.setImgPath(imgPath);
								approvUserList.add(usrs);
							}
							index++;
						}
					}
					approve.setApprovalUsers(approvUserList);
					approvalList.add(approve);
				}
			}

			// Comments
			List<SpFormEvaluationAppComment> comments = evaluationUser.getEvalApprovalComment();
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (SpFormEvaluationAppComment item : comments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
					// if (item.isApproved() == Boolean.TRUE) {
					ec.setComment(item.getComment());
					ec.setCreatedBy(item.getCreatedBy().getName());
					if (item.getCreatedDate() != null) {
						ec.setCreatedDate(new Date(sdf.format(item.getCreatedDate())));
					}
					ec.setImgPath(imgPath);
					commentDetails.add(ec);
					// }
				}
			}
			formDetails.setComments(commentDetails);
			formDetails.setApprovals(approvalList);

			summary.add(formDetails);

			parameters.put("EVALUATION_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public SupplierPerformanceForm findScoreRatingForSupplier(String supplierId) {
		return supplierPerformanceEvaluationDao.getFormDetailsBySupplierId(supplierId);
	}

	@Override
	public SupplierPerformanceEvaluationPojo getSPFDetailsForBuyerByTenantId(String tenantId, String supplierId, Date startDate, Date endDate) throws ApplicationException {
		SupplierPerformanceEvaluationPojo evaluationPojo = new SupplierPerformanceEvaluationPojo();
		BigDecimal devision = null;
		BigDecimal countOfForms = new BigDecimal(supplierPerformanceEvaluationDao.getCountOfFormsBySupplierByBuyerId(tenantId, supplierId, startDate, endDate));

		BigDecimal sumOfOverallScore = null;
		if (countOfForms != null && countOfForms.intValue() > 0) {
			sumOfOverallScore = supplierPerformanceEvaluationDao.getSumOfOverallScoreOfSupplierByBuyerId(tenantId, supplierId, startDate, endDate);
			if (sumOfOverallScore != null) {
				devision = sumOfOverallScore.divide(countOfForms, 0, RoundingMode.HALF_UP);
			}

			ScoreRating scoreRating = scoreRatingDao.getScoreRatingForScoreAndTenant(tenantId, devision);
			if (scoreRating != null) {
				evaluationPojo.setScoreRating(scoreRating.getRating());
				evaluationPojo.setRatingDescription(scoreRating.getDescription());
			}else {
				throw new ApplicationException("Please configure ScoreRating");
			}
		}

		Buyer buyer = buyerDao.findPlainBuyerById(tenantId);

		evaluationPojo.setOverallScore(devision);
		if (buyer != null) {
			evaluationPojo.setBuyerName(buyer.getCompanyName());
		}

		return evaluationPojo;
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreBySpFormAndBUnit(Date startDate, Date endDate, String unitId, String supplierId, String tenantId) {
		return supplierPerformanceEvaluationDao.getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(tenantId, supplierId, startDate, endDate, unitId);
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(String tenantId, String supplierId, Date startDate, Date endDate) {
		List<SupplierPerformanceEvaluationPojo> sumOfOverallScoreByBU = supplierPerformanceEvaluationDao.getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(tenantId, supplierId, startDate, endDate);
		if (CollectionUtil.isNotEmpty(sumOfOverallScoreByBU)) {
			for (SupplierPerformanceEvaluationPojo eval : sumOfOverallScoreByBU) {
				ScoreRating scoreRating1 = scoreRatingDao.getScoreRatingForScoreAndTenant(tenantId, eval.getOverallScore());
				eval.setScoreRating(scoreRating1.getRating());
				eval.setRatingDescription(scoreRating1.getDescription());
			}
		} else {
			sumOfOverallScoreByBU = new ArrayList<SupplierPerformanceEvaluationPojo>();
		}
		return sumOfOverallScoreByBU;
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndFormId(Date startDate, Date endDate, String formId, String supplierId, String tenantId) {
		List<SupplierPerformanceEvaluationPojo> overallScoreByCriteria = supplierPerformanceFormCriteriaService.getOverallScoreByCriteriaAndFormId(startDate, endDate, formId, supplierId, tenantId);
		return overallScoreByCriteria;
	}

	public SupplierPerformanceEvaluationPojo getOverallScoreOfSupplierByBuyerId(Date startDate, Date endDate, String buyerId, String supplierId) {
		SupplierPerformanceEvaluationPojo overallScoreByBuyer = supplierPerformanceEvaluationDao.getOverallScoreOfSupplierByBuyerId(startDate, endDate, buyerId, supplierId);

		if (overallScoreByBuyer.getOverallScore() != null) {
			ScoreRating scoreRating1 = scoreRatingDao.getScoreRatingForScoreAndTenant(buyerId, overallScoreByBuyer.getOverallScore());
			overallScoreByBuyer.setScoreRating(scoreRating1.getRating());
			overallScoreByBuyer.setRatingDescription(scoreRating1.getDescription());
		}

		return overallScoreByBuyer;
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreOfSupplierByBUnitAndEvent(Date start, Date end, String unitId, String supplierId, String buyerId) {
		List<SupplierPerformanceEvaluationPojo> overallScoreByBU = supplierPerformanceEvaluationDao.getOverallScoreOfSupplierByBUnitAndEvent(start, end, unitId, supplierId, buyerId);
		return overallScoreByBU;
	}

	@Override
	public List<SupplierPerformanceForm> getEventIdListForSupplierId(String supplierId, String buyerId, Date start, Date end) {
		return supplierPerformanceEvaluationDao.getEventIdListForSupplierId(supplierId, buyerId, start, end);
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndEventId(Date start, Date end, String eventId, String buyerId, String supplierId) {
		return supplierPerformanceEvaluationDao.getOverallScoreByCriteriaAndEventId(start, end, eventId, buyerId, supplierId);
	}

	public JasperPrint getScoreCardPdf(SupplierPerformanceForm performanceForm, User user, String strTimeZone, JRSwapFileVirtualizer virtualizer, String tenantId) {

		performanceForm = supplierPerformanceFormDao.getFormAndCriteriaByFormId(performanceForm.getId());
		JasperPrint jasperPrint = null;
		List<SupplierPerformanceEvaluationPojo> summary = new ArrayList<SupplierPerformanceEvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/ScoreCardSummary.jasper");
			File jasperfile = resource.getFile();

			SupplierPerformanceEvaluationPojo formDetails = new SupplierPerformanceEvaluationPojo();

			formDetails.setFormId(performanceForm.getFormId());
			formDetails.setReferenceNumber(performanceForm.getReferenceNumber());
			formDetails.setReferenceName(performanceForm.getReferenceName());
			formDetails.setProcurementCategory(performanceForm.getProcurementCategory() != null ? performanceForm.getProcurementCategory().getProcurementCategories() : "");
			formDetails.setUnitName(performanceForm.getBusinessUnit() != null ? performanceForm.getBusinessUnit().getDisplayName() : "");
			formDetails.setEvaluationStartDateStr(performanceForm.getEvaluationStartDate() != null ? sdf.format(performanceForm.getEvaluationStartDate()) : "");
			formDetails.setEvaluationEndDateStr(performanceForm.getEvaluationEndDate() != null ? sdf.format(performanceForm.getEvaluationEndDate()) : "");
			String evals = "";
			int i = 1;

			if (CollectionUtil.isNotEmpty(performanceForm.getEvaluators())) {
				for (SupplierPerformanceEvaluatorUser eval : performanceForm.getEvaluators()) {
					evals += (i++) + ". " + eval.getEvaluator().getName() + "\r\n";
				}
			}
			formDetails.setEvaluator(evals);
			formDetails.setSupplierName(performanceForm.getAwardedSupplier() != null ? performanceForm.getAwardedSupplier().getCompanyName() : "");

			List<PerformanceEvaluationCriteriaPojo> criteriaList = new ArrayList<PerformanceEvaluationCriteriaPojo>();
			List<PerformanceEvaluationCriteriaPojo> consolCriteriaList = new ArrayList<PerformanceEvaluationCriteriaPojo>();
			List<SupplierPerformanceEvaluatorUser> evalUserList = supplierPerformanceEvaluatorUserService.getEvaluatorUserByFormId(performanceForm.getId());
			if (CollectionUtil.isNotEmpty(evalUserList)) {
				for (SupplierPerformanceEvaluatorUser evalUSer : evalUserList) {
					List<SupplierPerformanceCriteria> evalUSerCriteriaList = supplierPerformanceCriteriaDao.getSPCriteriaByFormIdAndUserId(performanceForm.getId(), evalUSer.getId());
					if (CollectionUtil.isNotEmpty(evalUSerCriteriaList)) {
						for (SupplierPerformanceCriteria dbCriteria : evalUSerCriteriaList) {
							PerformanceEvaluationCriteriaPojo criteria = new PerformanceEvaluationCriteriaPojo();
							criteria.setEvaluatorName(evalUSer.getName());
							criteria.setLevel(dbCriteria.getLevel() + "." + dbCriteria.getOrder());
							criteria.setCriteriaName(dbCriteria.getName());
							criteria.setScore(dbCriteria.getEvaluatorTotalScore() != null ? dbCriteria.getEvaluatorTotalScore().toString() : "");
							criteria.setTotalScore(dbCriteria.getEvaluatorTotalScore() != null ? dbCriteria.getEvaluatorTotalScore().toString() : "");
							criteriaList.add(criteria);
							if (dbCriteria.getOrder() == 0) {
								PerformanceEvaluationCriteriaPojo scp = new PerformanceEvaluationCriteriaPojo();
								criteria.setLevel(dbCriteria.getLevel() + "." + dbCriteria.getOrder());
								scp.setCriteriaName(dbCriteria.getName());
								BigDecimal total = BigDecimal.ZERO;
								total = total.add(dbCriteria.getEvaluatorTotalScore() != null ? dbCriteria.getEvaluatorTotalScore() : BigDecimal.ZERO);
								scp.setTotalScore((total.divide(new BigDecimal(performanceForm.getEvaluators().size()), 0, RoundingMode.HALF_UP)).toString());
								consolCriteriaList.add(scp);
							}
						}
					}
				}
			}

			// List<ConsolidateScorePojo> conScoreList = new ArrayList<ConsolidateScorePojo>();
			// for (PerformanceEvaluationCriteriaPojo c : criteriaList) {
			// if (c.getOrder() == 0) {
			// ConsolidateScorePojo scp = new ConsolidateScorePojo();
			// scp.setOrder(c.getOrder());
			// scp.setLevel(c.getLevel());
			// scp.setCriteria(c.getName());
			// BigDecimal total = BigDecimal.ZERO;
			// for (SupplierPerformanceEvaluatorUser userr : evaluators) {
			// if (SupperPerformanceEvaluatorStatus.APPROVED != userr.getEvaluationStatus()) {
			// continue;
			// }
			//
			// for (SupplierPerformanceCriteria uCriteria : userr.getCriteria()) {
			// if (uCriteria.getLevel() == c.getLevel() && uCriteria.getOrder() == 0 && c.getOrder() == 0 &&
			// uCriteria.getEvaluatorTotalScore() != null) {
			// total = total.add(uCriteria.getEvaluatorTotalScore());
			// break;
			// }
			// }
			// }
			// // If no evaluators have submitted then set the total score as zero.
			// if (evaluatorNames.size() > 0) {
			// scp.setTotalScore(total.divide(new BigDecimal(evaluatorNames.size()), 0, RoundingMode.HALF_UP));
			// } else {
			// scp.setTotalScore(BigDecimal.ZERO);
			// }
			// conScoreList.add(scp);
			// }
			// }
			formDetails.setConsolCriteriaList(consolCriteriaList);
			// BigDecimal overallScore = BigDecimal.ZERO;
			// for (ConsolidateScorePojo c : conScoreList) {
			// if (c.getTotalScore() != null) {
			// overallScore = overallScore.add(c.getTotalScore());
			// }
			// }
			// ScoreRating scoreRating = scoreRatingService.getScoreRatingForScoreAndTenant(tenantId, overallScore);
			// model.addAttribute("scoreRating", scoreRating);
			// model.addAttribute("overallScore", overallScore);
			// model.addAttribute("conScoreList", conScoreList);
			// model.addAttribute("scoreCardList", scoreCardList);

			// List<PerformanceEvaluationCriteriaPojo> conScoreList = new
			// ArrayList<PerformanceEvaluationCriteriaPojo>();
			// for (SupplierPerformanceFormCriteria c : criteriaList) {
			// if (c.getOrder() == 0) {
			// ConsolidateScorePojo scp = new ConsolidateScorePojo();
			// scp.setOrder(c.getOrder());
			// scp.setLevel(c.getLevel());
			// scp.setCriteria(c.getName());
			// BigDecimal total = BigDecimal.ZERO;
			// for (SupplierPerformanceEvaluatorUser user : evaluators) {
			// for (SupplierPerformanceCriteria uCriteria : user.getCriteria()) {
			// if (uCriteria.getLevel() == c.getLevel() && uCriteria.getOrder() == 0 && c.getOrder() == 0 &&
			// uCriteria.getEvaluatorTotalScore() != null) {
			// total = total.add(uCriteria.getEvaluatorTotalScore());
			// break;
			// }
			// }
			// }
			// scp.setTotalScore(total.divide(new BigDecimal(evaluators.size()), 0, RoundingMode.HALF_UP));
			// conScoreList.add(scp);
			// }
			// }
			//
			//
			//
			// if (CollectionUtil.isNotEmpty(dbCriteriaList)) {
			// for (SupplierPerformanceCriteria dbCriteria : dbCriteriaList) {
			// if (dbCriteria.getOrder().equals(0)) {
			// PerformanceEvaluationCriteriaPojo criteria = new PerformanceEvaluationCriteriaPojo();
			// criteria.setLevel(dbCriteria.getLevel() + "." + dbCriteria.getOrder());
			// criteria.setCriteriaName(dbCriteria.getName());
			// criteria.setTotalScore(dbCriteria.getEvaluatorTotalScore() != null ?
			// dbCriteria.getEvaluatorTotalScore().toString() : "");
			// criteria.setRating(evalUser.getScoreRating() != null ? evalUser.getScoreRating().getRating().toString() :
			// "");
			// criteria.setRatingDescription(evalUser.getScoreRating() != null &&
			// StringUtils.checkString(evalUser.getScoreRating().getDescription()).length() > 0 ?
			// evalUser.getScoreRating().getDescription() : "");
			// criteria.setOverallScoreStr(evalUser.getOverallScore() != null ? evalUser.getOverallScore().toString() :
			// "");
			// consolCriteriaList.add(criteria);
			// }
			// }
			// }
			// LOG.info(">>>>>>>>>>>>>>>>>> LIST SIZE" + consolCriteriaList.size());
			//
			// formDetails.setConsolCriteriaList(consolCriteriaList);
			summary.add(formDetails);

			parameters.put("EVALUATION_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public Map<String, Object> getScoreCard(SupplierPerformanceForm performanceForm, User user, String strTimeZone, String tenantId) {

		performanceForm = supplierPerformanceFormDao.getFormAndCriteriaByFormId(performanceForm.getId());
		List<SupplierPerformanceEvaluationPojo> summary = new ArrayList<SupplierPerformanceEvaluationPojo>();
		Map<String, Object> map = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		try {

			map.put("formId", performanceForm.getFormId());
			map.put("referenceNumber", StringUtils.checkString(performanceForm.getReferenceNumber()));
			map.put("referenceName", StringUtils.checkString(performanceForm.getReferenceName()));
			map.put("procurementCategory", performanceForm.getProcurementCategory() != null ? performanceForm.getProcurementCategory().getProcurementCategories() : "");
			map.put("unitName", performanceForm.getBusinessUnit() != null ? performanceForm.getBusinessUnit().getDisplayName() : "");
			map.put("evaluationStartDateStr", performanceForm.getEvaluationStartDate() != null ? sdf.format(performanceForm.getEvaluationStartDate()) : "");
			map.put("evaluationEndDateStr", performanceForm.getEvaluationEndDate() != null ? sdf.format(performanceForm.getEvaluationEndDate()) : "");

			List<String> evals = new ArrayList<String>();

			if (CollectionUtil.isNotEmpty(performanceForm.getEvaluators())) {
				for (SupplierPerformanceEvaluatorUser eval : performanceForm.getEvaluators()) {
					evals.add(eval.getEvaluator().getName());
				}
			}
			map.put("evaluator", evals);
			map.put("remarks", StringUtils.checkString(performanceForm.getRemarks()));
			map.put("supplierName", performanceForm.getAwardedSupplier() != null ? performanceForm.getAwardedSupplier().getCompanyName() : "");

			List<SupplierPerformanceEvaluatorUser> evaluators = performanceForm.getEvaluators();
			List<String> evaluatorNames = new ArrayList<String>();
			for (SupplierPerformanceEvaluatorUser user1 : evaluators) {
				if (SupperPerformanceEvaluatorStatus.APPROVED == user1.getEvaluationStatus()) {
					evaluatorNames.add(user1.getEvaluator().getName());
				}
			}

			map.put("evaluators", evaluatorNames);

			SupplierPerformanceFormCriteria parentCriteria = null;
			ScoreCardPojo parent = null;
			BigDecimal sectionTotalAvg = BigDecimal.ZERO;
			List<ScoreCardPojo> scoreCardList = new ArrayList<ScoreCardPojo>();
			List<SupplierPerformanceFormCriteria> criteriaList = supplierPerformanceFormCriteriaService.getSPFormCriteriaByFormId(performanceForm.getId());
			for (SupplierPerformanceFormCriteria c : criteriaList) {
				ScoreCardPojo sc = new ScoreCardPojo();
				sc.setCriteria(c.getName());
				sc.setOrder(c.getOrder());
				sc.setLevel(c.getLevel());
				sc.setAverage(c.getAverageScore().toString());
				sc.setScore(new ArrayList<String>());
				scoreCardList.add(sc);
				BigDecimal avgScore = BigDecimal.ZERO;
				for (SupplierPerformanceEvaluatorUser user2 : evaluators) {
					if (SupperPerformanceEvaluatorStatus.APPROVED != user2.getEvaluationStatus()) {
						continue;
					}
					for (SupplierPerformanceCriteria uCriteria : user2.getCriteria()) {
						if (uCriteria.getLevel() == c.getLevel() && uCriteria.getOrder() == c.getOrder() && uCriteria.getEvaluatorTotalScore() != null) {
							sc.getScore().add(uCriteria.getEvaluatorTotalScore().toString());
							avgScore = avgScore.add(uCriteria.getEvaluatorTotalScore());
							break;
						}
					}
				}
				// Only compute the average if the form is not concluded yet.
				if (evaluatorNames.size() > 0 && performanceForm.getFormStatus() != SupplierPerformanceFormStatus.CONCLUDED) {
					avgScore = avgScore.divide(new BigDecimal(evaluatorNames.size()), 2, RoundingMode.HALF_UP);
					sc.setAverage(avgScore.toString());
					c.setAverageScore(avgScore);
					if (c.getOrder() > 0) {
						sectionTotalAvg = sectionTotalAvg.add(avgScore);
						LOG.info("Sec Total - Order : " + c.getOrder() + ", AVG Score : " + avgScore + ", Sec Tot Avg : " + sectionTotalAvg);
					}
				} else {
					if (evaluatorNames.size() == 0) {
						sc.setAverage(BigDecimal.ZERO.setScale(2).toString());
					} else {
						if (c.getOrder() == 0) {
							sc.setAverage(c.getAverageScore().toString());
						} else {
							sc.setAverage(c.getAverageScore().setScale(2).toString());
						}
					}
				}

				// Only compute the average if the form is not concluded yet.
				if (performanceForm.getFormStatus() != SupplierPerformanceFormStatus.CONCLUDED) {
					if (parent == null) {
						parent = sc;
						parentCriteria = c;
					} else if (parent.getLevel() != sc.getLevel()) {
						parent.setAverage(sectionTotalAvg.setScale(0, RoundingMode.HALF_UP).toString());
						LOG.info(">>>>> Sec Total - Order : " + parent.getOrder() + ", AVG Score : " + avgScore + ", Sec Tot Avg : " + sectionTotalAvg);
						parentCriteria.setAverageScore(sectionTotalAvg.setScale(0, RoundingMode.HALF_UP));
						sectionTotalAvg = BigDecimal.ZERO;
						parent = sc;
						parentCriteria = c;
					}
				}
			}

			if (parent != null && performanceForm.getFormStatus() != SupplierPerformanceFormStatus.CONCLUDED) {
				parent.setAverage(sectionTotalAvg.setScale(0, RoundingMode.HALF_UP).toString());
				parentCriteria.setAverageScore(sectionTotalAvg.setScale(0, RoundingMode.HALF_UP));
				LOG.info("Sec Total - Order : " + parent.getOrder() + ", Sec Tot Avg : " + sectionTotalAvg);
			}

			for (ScoreCardPojo s : scoreCardList) {
				LOG.info(s);
			}

			List<ConsolidateScorePojo> conScoreList = new ArrayList<ConsolidateScorePojo>();
			for (SupplierPerformanceFormCriteria c : criteriaList) {
				if (c.getOrder() == 0) {
					ConsolidateScorePojo scp = new ConsolidateScorePojo();
					scp.setOrder(c.getOrder());
					scp.setLevel(c.getLevel());
					scp.setCriteria(c.getName());
					scp.setAverageScore(c.getAverageScore());
					scp.setWeightage(c.getWeightage());
					// If the form is already concluded then dont compute the scores
					if (performanceForm.getFormStatus() == SupplierPerformanceFormStatus.CONCLUDED) {
						scp.setTotalScore(c.getTotalScore().setScale(2));
					} else {
						if (scp.getAverageScore() != null) {
							LOG.info("Setting total score at section : " + scp.getAverageScore().multiply(scp.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
							scp.setTotalScore(scp.getAverageScore().multiply(scp.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
						} else {
							scp.setTotalScore(BigDecimal.ZERO.setScale(2));
						}
					}
					conScoreList.add(scp);
				}
			}

			BigDecimal overallScore = BigDecimal.ZERO;
			ScoreRating scoreRating = null;
			// If the form is already concluded then dont compute the scores
			if (performanceForm.getFormStatus() == SupplierPerformanceFormStatus.CONCLUDED) {
				overallScore = performanceForm.getOverallScore();

				if (performanceForm.getScoreRating() != null) {
					scoreRating = performanceForm.getScoreRating();
					performanceForm.getScoreRating().getRating();
				}
			} else {

				for (ConsolidateScorePojo c : conScoreList) {
					if (c.getTotalScore() != null) {
						overallScore = overallScore.add(c.getTotalScore());
					}
				}
				overallScore = overallScore.setScale(0, RoundingMode.HALF_UP);
				scoreRating = scoreRatingService.getScoreRatingForScoreAndTenant(tenantId, overallScore);
			}
			map.put("scoreRating", scoreRating);
			map.put("overallScore", overallScore);
			map.put("conScoreList", conScoreList);
			map.put("scoreCardList", scoreCardList);

		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return map;
	}

	@Override
	public List<BusinessUnit> getBusinessUnitListForTenant(String tenantId, Date startDate, Date endDate, String supplierId) {
		return supplierPerformanceFormDao.getBusinessUnitListForTenant(tenantId, startDate, endDate, supplierId);
	}

	@Override
	public List<ProcurementCategories> getProcurementCategoriesListForTenantForDate(String tenantId, Date startDate, Date endDate) {
		return supplierPerformanceFormDao.getProcurementCategoriesListForTenantForDate(tenantId, startDate, endDate);
	}

}
