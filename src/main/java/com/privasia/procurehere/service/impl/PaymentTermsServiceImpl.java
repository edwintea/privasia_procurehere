/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.PaymentTermsDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.PaymentTermsService;

/**
 * @author jayshree
 */
@Service
@Transactional(readOnly = true)
public class PaymentTermsServiceImpl implements PaymentTermsService {

	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Autowired
	PaymentTermsDao paymentTermesDao;

	@Override
	@Transactional(readOnly = false)
	public void savePaymentTermes(PaymentTermes paymentTermes) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + paymentTermes.getPaymentTermCode() + "' Payment terms created ", paymentTermes.getCreatedBy().getTenantId(), paymentTermes.getCreatedBy(), new Date(), ModuleType.PaymentTerms);
		buyerAuditTrailDao.save(ownerAuditTrail);
		paymentTermesDao.saveOrUpdate(paymentTermes);
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePaymentTermes(PaymentTermes paymentTermes) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + paymentTermes.getPaymentTermCode() + "' Payment terms updated ", paymentTermes.getModifiedBy().getTenantId(), paymentTermes.getModifiedBy(), new Date(), ModuleType.PaymentTerms);
		buyerAuditTrailDao.save(ownerAuditTrail);
		paymentTermesDao.update(paymentTermes);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePaymentTermes(PaymentTermes paymentTermes) {
		String cost = paymentTermes.getPaymentTermCode();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + cost + "' Payment terms  deleted ", paymentTermes.getModifiedBy().getTenantId(), paymentTermes.getModifiedBy(), new Date(), ModuleType.PaymentTerms);
		buyerAuditTrailDao.save(ownerAuditTrail);
		paymentTermesDao.delete(paymentTermes);
	}

	@Override
	public boolean isExists(PaymentTermes paymentTermes, String tenantId) {
		return paymentTermesDao.isExists(paymentTermes, tenantId);
	}

	@Override
	public PaymentTermes getPaymentTermesById(String id) {
		PaymentTermes paymentTermes = paymentTermesDao.findById(id);
		return paymentTermes;
	}

	@Override
	public List<PaymentTermes> getAllActivePaymentTermesForTenant(String tenantId) {
		return paymentTermesDao.getAllActivePaymentTermesForTenant(tenantId);
	}

	@Override
	public List<PaymentTermes> findPaymentTermesForTenant(String tenantId, TableDataInput tableParams) {
		return paymentTermesDao.findPaymentTermesForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalFilteredPaymentTermesForTenant(String tenantId, TableDataInput tableParams) {
		return paymentTermesDao.findTotalFilteredPaymentTermesForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalPaymentTermesForTenant(String tenantId) {
		return paymentTermesDao.findTotalPaymentTermesForTenant(tenantId);
	}

	@Override
	public List<PaymentTermes> getAllPaymentTermesByTenantId(String tenantId) {
		return paymentTermesDao.getAllPaymentTermesByTenantId(tenantId);
	}

	@Override
	public List<PaymentTermes> getActivePaymentTermesByTenantId(String tenantId) {
		return paymentTermesDao.getActivePaymentTermesByTenantId(tenantId);
	}

	@Override
	public void paymentTermesDownloadTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException {
		LOG.info("Payment Terms template called.");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String downloadFolder = context.getRealPath("/WEB-INF/");
		String fileName = "PaymentTermsTemplate.xlsx";
		Path file = Paths.get(downloadFolder, fileName);
		LOG.info("File Path ::" + file);

		// Save Excel File
		workbook = getExcelWorkBook(tenantId);
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
	}

	// Style of Heading Cells
	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PAYMENT_TERMES_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private XSSFWorkbook getExcelWorkBook(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Payment Termes List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<PaymentTermes> paymentTermesList = getAllPaymentTermesByTenantId(loggedInUserTenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(paymentTermesList)) {
			for (PaymentTermes paymentTermes : paymentTermesList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(paymentTermes.getPaymentTermCode());
				row.createCell(cellNum++).setCellValue(paymentTermes.getDescription());
				row.createCell(cellNum++).setCellValue(paymentTermes.getPaymentDays());
				row.createCell(cellNum++).setCellValue(paymentTermes.getStatus() != null ? paymentTermes.getStatus().toString() : "");
			}
		}
		XSSFSheet lookupSheet1 = workbook.createSheet("LOOKUP1");
		int index2 = 0;
		Status[] statsArr = Status.values();
		for (Status status : statsArr) {
			XSSFRow firstRow = lookupSheet1.createRow(index2++);
			XSSFCell cell1 = firstRow.createCell(0);
			cell1.setCellValue(status.toString());
		}

		XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(lookupSheet1);
		XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP1'!$A$1:$A$" + (statsArr.length + 1));
		CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 3, 3);
		XSSFDataValidation validation = (XSSFDataValidation) validationHelper.createValidation(constraint, addressList);
		validation.setSuppressDropDownArrow(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Invalid  STATUS Selected", "Please select STATUS from the list");
		validation.createPromptBox("STATUS List", "Select STATUS from the list provided. It has been exported from your master data.");
		validation.setShowPromptBox(true);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
		workbook.setSheetHidden(1, true);

		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentTermes getByPaymentTermes(String paymentTermes, String tenantId) {
		return paymentTermesDao.getByPaymentTermes(paymentTermes, tenantId);
	}

}
