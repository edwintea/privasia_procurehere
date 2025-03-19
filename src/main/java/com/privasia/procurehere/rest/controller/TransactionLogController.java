package com.privasia.procurehere.rest.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BudgetApprovalUser;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.TransactionLogStatus;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TransactionLogPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.TransactionLogService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BudgetApprovalEditor;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author Shubham
 */

@Controller
@RequestMapping("/admin/transactionLog")
public class TransactionLogController implements Serializable {

	private static final long serialVersionUID = -8494803310406827981L;

	private static final Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@Autowired
	UserService userService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	TransactionLogService transactionLogService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	CurrencyService currencyService;

	@Resource
	MessageSource messageSource;

	@Autowired
	BudgetApprovalEditor budgetApprovalEditor;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	ServletContext context;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(BudgetApprovalUser.class, budgetApprovalEditor);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
		timeFormat.setTimeZone(timeZone);

		binder.registerCustomEditor(Date.class, "transactionTimeStamp", new CustomDateEditor(timeFormat, true));
	}

	@ModelAttribute("txStatusList")
	public List<TransactionLogStatus> transactionLogStatus() {
		return Arrays.asList(TransactionLogStatus.values());
	}

	@RequestMapping(path = "/transactionLogData", method = RequestMethod.GET)
	public ResponseEntity<TableData<TransactionLogPojo>> budgetTemplateData(TableDataInput input) {
		TableData<TransactionLogPojo> data = null;
		try {
			data = new TableData<TransactionLogPojo>(transactionLogService.getAlltransactionLogsForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			LOG.info("***************txLog query data" + data.getRecordsTotal());
			long filteredTotalCount = transactionLogService.findfilteredTotalCountTransactionLogForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(filteredTotalCount);
			long totalCount = transactionLogService.findTotalTransactionLogForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsTotal(totalCount);
			LOG.info(" totalCount :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading txLog list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<TransactionLogPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/exportTransactionLogs", method = RequestMethod.GET)
	public void exportTransactionLogs(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws JsonProcessingException {
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			timeFormat.setTimeZone(timeZone);

			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "TransactionLogs.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			transactionLogDownloader(workbook, timeFormat);

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");
			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}
		} catch (Exception e) {
			LOG.error("Error while downloading BQ items :: " + e.getMessage(), e);
		}

	}

	private void transactionLogDownloader(XSSFWorkbook workbook, SimpleDateFormat timeFormat) {
		XSSFSheet sheet = workbook.createSheet("Budget Transaction Logs");
		transactionLogService.buildHeader(workbook, sheet);

		int r = 1;
		List<TransactionLogPojo> transactionLogList = transactionLogService.getAlltransactionLogsForTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(transactionLogList)) {
			DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
			for (TransactionLogPojo transactionLog : transactionLogList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(transactionLog.getReferenceNumber());
				row.createCell(cellNum++).setCellValue(timeFormat.format(transactionLog.getTransactionTimeStamp()));
				row.createCell(cellNum++).setCellValue(transactionLog.getUnitName());
				row.createCell(cellNum++).setCellValue(transactionLog.getCostCenter());
				row.createCell(cellNum++).setCellValue(transactionLog.getNewAmount() != null ? df.format(transactionLog.getNewAmount()) : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getAddAmount() != null ? df.format(transactionLog.getAddAmount()) : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getDeductAmount() != null ? df.format(transactionLog.getDeductAmount()) : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(transactionLog.getFromBusinessUnit()).length() > 0 ? transactionLog.getFromBusinessUnit() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(transactionLog.getToBusinessUnit()).length() > 0 ? transactionLog.getToBusinessUnit() : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getPurchaseOrder() != null ? df.format(transactionLog.getPurchaseOrder()) : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getTxStatus() != null ? transactionLog.getTxStatus().toString() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(transactionLog.getLocked()).length() > 0 ? transactionLog.getLocked() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(transactionLog.getPrBaseCurrency()).length() > 0 ? transactionLog.getPrBaseCurrency() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(transactionLog.getBudgetBaseCurrency()).length() > 0 ? transactionLog.getBudgetBaseCurrency() : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getConversionRateAmount() != null ? df.format(transactionLog.getConversionRateAmount()) : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getAmountAfterConversion() != null ? df.format(transactionLog.getAmountAfterConversion()) : "");
				row.createCell(cellNum++).setCellValue(transactionLog.getRemainingAmount() != null ? df.format(transactionLog.getRemainingAmount()) : "");

			}
		}
		for (int k = 0; k < 18; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}
	
	@RequestMapping(path = "/exportTransactionLogsCsv", method = RequestMethod.GET)
	public void exportTransactionLogsCsv(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws JsonProcessingException {
		try {
			File file = File.createTempFile("TransactionLogs", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			timeFormat.setTimeZone(timeZone);

			transactionLogService.downloadCsvFileForTransactionLogs(response, file, timeFormat, SecurityLibrary.getLoggedInUserTenantId(), session);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Transaction logs is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.BudgetPlanner);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=TransactionLogs.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();
			

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}

	}

}
