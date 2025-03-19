package com.privasia.procurehere.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

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
import com.privasia.procurehere.core.dao.ProcurementCategoriesDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.ProcurementCategoriesService;

/**
 * @author sana
 */
@Service
@Transactional(readOnly = true)
public class ProcurementCategoriesServiceImpl implements ProcurementCategoriesService {

	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;

	@Override
	public List<ProcurementCategories> findProcurementCategoriesForTenant(String tenantId, TableDataInput input) {
		return procurementCategoriesDao.findProcurementCategoriesForTenant(tenantId, input);
	}

	@Override
	public long findTotalFilteredProcurementCategoriesForTenant(String tenantId, TableDataInput input) {
		return procurementCategoriesDao.findTotalFilteredProcurementCategoriesForTenant(tenantId, input);
	}

	@Override
	public long findCountOfProcurementCategoriesForTenant(String tenantId) {
		return procurementCategoriesDao.findCountOfProcurementCategoriesForTenant(tenantId);
	}

	@Override
	public boolean isExists(ProcurementCategories procurementCategories, String tenantId) {
		return procurementCategoriesDao.isExists(procurementCategories, tenantId);
	}

	@Override
	public ProcurementCategories getProcurementCategoriesById(String id) {
		return procurementCategoriesDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveProcurementCategories(ProcurementCategories procurementCategories) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + procurementCategories.getProcurementCategories() + "' Procurement Categories settings created ", procurementCategories.getCreatedBy().getTenantId(), procurementCategories.getCreatedBy(), new Date(), ModuleType.ProcurementCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
		procurementCategoriesDao.saveOrUpdate(procurementCategories);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateProcurementCategories(ProcurementCategories procurementCategories) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + procurementCategories.getProcurementCategories() + "' Procurement Categories settings updated ", procurementCategories.getModifiedBy().getTenantId(), procurementCategories.getModifiedBy(), new Date(), ModuleType.ProcurementCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
		procurementCategoriesDao.update(procurementCategories);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProcurementCategories(ProcurementCategories procurementCategories) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + procurementCategories.getProcurementCategories() + "' Procurement Categories settings deleted ", procurementCategories.getModifiedBy().getTenantId(), procurementCategories.getModifiedBy(), new Date(), ModuleType.ProcurementCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
		procurementCategoriesDao.delete(procurementCategories);

	}

	@Override
	public void procurementCategoriesDownloadTemplate(HttpServletResponse response, String tenantId) {
		try {
			LOG.info("Procurement Categories template called.");
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ProcurementCategoriesTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);
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

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}
	}

	private XSSFWorkbook getExcelWorkBook(String tenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Procurement Categories List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<ProcurementCategories> procurementCategoriesList = procurementCategoriesDao.getAllProcurementCategoriesByTenantId(tenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(procurementCategoriesList)) {
			for (ProcurementCategories procurementCategories : procurementCategoriesList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(procurementCategories.getProcurementCategories());
				row.createCell(cellNum++).setCellValue(procurementCategories.getDescription());
				row.createCell(cellNum++).setCellValue(procurementCategories.getStatus() != null ? procurementCategories.getStatus().toString() : "");
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
		CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 2, 2);
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

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PROCUREMENT_CATEGORIES_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public List<ProcurementCategories> getAllActiveProcurementCategory(String tenantId) {
		List<ProcurementCategories> list = procurementCategoriesDao.getAllActiveProcurementCategory(tenantId);
		long count = procurementCategoriesDao.findCountOfProcurementCategoriesForTenant(tenantId);
		if (list != null && count > list.size()) {
			ProcurementCategories more = new ProcurementCategories();
			more.setId("-1");
			more.setProcurementCategories("Total " + (count) + " Group Code. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

}
