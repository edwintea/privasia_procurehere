package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.BusinessUnitPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BusinessUnitService;

@Service
@Transactional(readOnly = true)
public class BusinessUnitServiceImpl implements BusinessUnitService {

	static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BusinessUnitDao businessUnitDao;

	@Autowired
	ServletContext context;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public void save(BusinessUnit businessUnit) {
		BusinessUnit bu = businessUnitDao.saveOrUpdate(businessUnit);
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, " Business unit '" + bu.getUnitName() + "' is created", bu.getCreatedBy().getTenantId(), bu.getCreatedBy(), new Date(), ModuleType.BusinessUnit);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.error("ERROR while Buyer Setting storing .. " + e.getMessage(), e);
		}
	}

	@Override
	public BusinessUnit getBusinessUnitById(String id) {
		return businessUnitDao.findById(id);
	}

	@Override
	public BusinessUnit getPlainBusinessUnitById(String id) {
		return businessUnitDao.getPlainBusinessUnitById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public BusinessUnit update(BusinessUnit businessUnit) {
		BusinessUnit bu = businessUnitDao.saveOrUpdate(businessUnit);
		LOG.info("Parent : " + bu.getParent() + " Budget Check : " + bu.getBudgetCheck() + "recursive : " + businessUnit.getRecursive());
		if (bu.getParent() == null && businessUnit.getRecursive().equals(Boolean.TRUE)) {
			LOG.info("INSIDE ::");
			businessUnitDao.updateBudgetCheckForChildRecords(businessUnit);
		}
		if (bu.getParent() == null && businessUnit.getSpmIntegrationRecursive().equals(Boolean.TRUE)) {
			LOG.info("INSIDE ::");
			businessUnitDao.updateSpmIntegrationForChildRecords(businessUnit);
		}
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, " Business unit '" + bu.getUnitName() + "' is updated ", bu.getModifiedBy().getTenantId(), bu.getModifiedBy(), new Date(), ModuleType.BusinessUnit);
			ownerAuditTrail.setModuleType(ModuleType.BusinessUnit);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.error("ERROR while updating business unit storing .. " + e.getMessage(), e);
		}
		return bu;
	}

	@Override
	public List<BusinessUnit> findBusinessUnitsForTenant(String tenantId, TableDataInput input) {
		return businessUnitDao.findBusinessUnitsForTenant(tenantId, input);
	}

	@Override
	public long findTotalFilteredBusinessUnitsForTenant(String tenantId, TableDataInput input) {
		return businessUnitDao.findTotalFilteredBusinessUnitsForTenant(tenantId, input);
	}

	@Override
	public long findTotalBusinessUnitsForTenant(String tenantId) {
		return businessUnitDao.findTotalBusinessUnitsForTenant(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(BusinessUnit businessUnit) {

		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, " Business unit '" + businessUnit.getUnitName() + "' is deleted", businessUnit.getCreatedBy().getTenantId(), businessUnit.getCreatedBy(), new Date(), ModuleType.BusinessUnit);
		buyerAuditTrailDao.save(ownerAuditTrail);

		businessUnitDao.delete(businessUnit);
	}

	@Override
	public boolean isExists(BusinessUnit businessUnit, String tenantId) {
		return businessUnitDao.isExists(businessUnit, tenantId);
	}

	@Override
	public List<BusinessUnit> getPlainActiveBusinessUnitForTenant(String tenantId) {
		return businessUnitDao.getPlainActiveBusinessUnitForTenant(tenantId);
	}

	@Override
	public List<BusinessUnit> getAllActiveBusinessUnitForMoblie(String tenantId) {
		return businessUnitDao.getPlainActiveBusinessUnitForTenant(tenantId);
	}

	@Override
	public boolean isExistsUnitCode(String unitCode, String tenantId, String id) {
		return businessUnitDao.isExistsUnitCode(unitCode, tenantId, id);
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.BUSINESS_UNIT_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public void businessUnitDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) {
		// TODO Auto-generated method stub
		LOG.info("downloadBusinessUnitListExcel method called");
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "BusinessUnit.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			workbook = getExcelWorkBook(loggedInUserTenantId);
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
			LOG.error("Error :- " + e.getMessage());
		}
	}

	public XSSFWorkbook getExcelWorkBook(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Business Unit List");
		// Creating Headings
		buildHeader(workbook, sheet);
		List<BusinessUnit> businessUnitList = businessUnitDao.getBusinessUnitForTenant(loggedInUserTenantId);
		int r = 1;
		if (CollectionUtil.isNotEmpty(businessUnitList)) {
			for (BusinessUnit businessUnit : businessUnitList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(businessUnit.getId());
				row.createCell(cellNum++).setCellValue(businessUnit.getUnitName());
				row.createCell(cellNum++).setCellValue(businessUnit.getUnitCode());
				row.createCell(cellNum++).setCellValue(businessUnit.getDisplayName());
				row.createCell(cellNum++).setCellValue(businessUnit.getParent() != null ? businessUnit.getParent().getUnitName() : "");
				row.createCell(cellNum++).setCellValue(businessUnit.getParent() != null ? businessUnit.getParent().getUnitCode() : "");
				row.createCell(cellNum++).setCellValue(businessUnit.getLine1());
				row.createCell(cellNum++).setCellValue(businessUnit.getLine2());
				row.createCell(cellNum++).setCellValue(businessUnit.getLine3());
				row.createCell(cellNum++).setCellValue(businessUnit.getLine4());
				row.createCell(cellNum++).setCellValue(businessUnit.getLine5());
				row.createCell(cellNum++).setCellValue(businessUnit.getLine6());
				row.createCell(cellNum++).setCellValue(businessUnit.getLine7());
				row.createCell(cellNum++).setCellValue(businessUnit.getStatus() != null ? businessUnit.getStatus().toString() : "");
				row.createCell(cellNum++).setCellValue(Boolean.TRUE == businessUnit.getBudgetCheck() ? "Yes" : "No");
				row.createCell(cellNum++).setCellValue(Boolean.TRUE == businessUnit.getSpmIntegration() ? "Yes" : "No");

			}
		}
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	@Override
	public void businessUnitExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder) {
		LOG.info("downloadBusinessUnitListExcel method called");
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String fileName = "BusinessUnit.xlsx";
			workbook = getExcelWorkBook(loggedInUserTenantId);
			FileUtil.writeXssfExcelToZip(zos, workbook, parentFolder, fileName);
			LOG.info("Successfully written in Excel");
		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	@Override
	public BusinessUnit findBusinessUnitForTenantByUnitCode(String tenantId, String unitName) {
		return businessUnitDao.findByUnitCode(tenantId, unitName);
	}

	@Override
	public List<BusinessUnit> getPlainActiveBusinessUnitParentsForTenant(String tenantId) {
		return businessUnitDao.getPlainActiveBusinessUnitParentsForTenant(tenantId);
	}

	@Override
	public List<BusinessUnitPojo> fetchBusinessUnitByTenantId(String tenantId, String search) {
		List<BusinessUnitPojo> list = businessUnitDao.fetchAllActiveBusinessUnitForTenant(tenantId, search);
		long count = businessUnitDao.countConstructQueryToFetchBusinessUnit(tenantId);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				BusinessUnitPojo more = new BusinessUnitPojo();
				more.setDisplayName("+" + (count - list.size()) + " more. Continue typing to find match...");
				more.setUnitName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}

	@Override
	public List<BusinessUnitPojo> getBusinessUnitIdByTenantId(String tenantId) {
		return businessUnitDao.getBusinessUnitIdByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBusinessUnit(BusinessUnit persistObj) {
		businessUnitDao.update(persistObj);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeAssignCostCenter(String businessUnitId, String costCenterId) {
		businessUnitDao.removeAssignCostCenter(businessUnitId, costCenterId);
	}

	@Override
	public void downloadCsvFileForBusiness(HttpServletResponse response, File file, String loggedInUserTenantId) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.BUSINESS_UNIT_REPORT_CSV_COLUMNS;

			String[] columns = { "id", "unitName", "unitCode", "displayName", "parentBusinessUnit", "parentUnitCode", "line1", "line2", "line3", "line4", "line5", "line6", "line7", "status", "budgetCheck", "spmIntegration" };

			long count = businessUnitDao.countConstructQueryToFetchBusinessUnit(loggedInUserTenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<BusinessUnitPojo> list = findBusinessUnitForCsv(loggedInUserTenantId, PAGE_SIZE, pageNo);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (BusinessUnitPojo businessUnitPojo : list) {
					beanWriter.write(businessUnitPojo, columns, processors);
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

				new Optional(), // Temp name
				new Optional(), // Event id
				new Optional(), // rEf no
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(new FmtBool("Yes", "No")), // Budget Check
				new Optional(new FmtBool("Yes", "No")) // SPM Integration

		};
		return processors;
	}

	private List<BusinessUnitPojo> findBusinessUnitForCsv(String tenantId, int PAGE_SIZE, int pageNo) {

		List<BusinessUnit> businessUnitPojoList = businessUnitDao.getBusinessUnitForCsv(tenantId, PAGE_SIZE, pageNo);

		List<BusinessUnitPojo> businessUnitPojoLists = new ArrayList<>();
		int r = 1;
		for (BusinessUnit businessUnit : businessUnitPojoList) {
			BusinessUnitPojo businessUnitPojo = new BusinessUnitPojo();
			businessUnitPojo.setSrNo(r++);
			businessUnitPojo.setId(businessUnit.getId());
			businessUnitPojo.setUnitName(businessUnit.getUnitName());
			businessUnitPojo.setUnitCode(businessUnit.getUnitCode());
			businessUnitPojo.setDisplayName(businessUnit.getDisplayName());
			businessUnitPojo.setCreatedDate(businessUnit.getCreatedDate());
			businessUnitPojo.setModifiedDate(businessUnit.getModifiedDate());
			businessUnitPojo.setStatus(businessUnit.getStatus());
			businessUnitPojo.setParentBusinessUnit(businessUnit.getParent() != null ? businessUnit.getParent().getUnitName() : "");
			businessUnitPojo.setParentUnitCode(businessUnit.getParent() != null ? businessUnit.getParent().getUnitCode() : "");
			businessUnitPojo.setLine1(businessUnit.getLine1());
			businessUnitPojo.setLine2(businessUnit.getLine2());
			businessUnitPojo.setLine3(businessUnit.getLine3());
			businessUnitPojo.setLine4(businessUnit.getLine4());
			businessUnitPojo.setLine5(businessUnit.getLine5());
			businessUnitPojo.setLine6(businessUnit.getLine6());
			businessUnitPojo.setLine7(businessUnit.getLine7());
			businessUnitPojo.setBudgetCheck(businessUnit.getBudgetCheck());
			businessUnitPojo.setSpmIntegration(businessUnit.getSpmIntegration());
			businessUnitPojoLists.add(businessUnitPojo);
		}

		return businessUnitPojoLists;

	}

	@Override
	public List<CostCenterPojo> getCostCentersByBusinessUnitId(String id, Status status) {
		return businessUnitDao.getCostCentersByBusinessUnitId(id, status);
	}

	@Override
	public long getCountCostCentersByBusinessUnitId(String id, Status status) {
		return businessUnitDao.getCountCostCentersByBusinessUnitId(id, status);
	}

	@Override
	public List<BusinessUnit> getBusinessForContractFromAwardDetails(String tenantId, BusinessUnit bu) {
		return businessUnitDao.getBusinessForContractFromAwardDetails(tenantId, bu);
	}

	@Override
	public List<BusinessUnit> getBusinessUnitForContractFromAward(BusinessUnit businessUnit) {
		List<BusinessUnit> businessUnits = businessUnitDao.getBusinessForContractFromAwardDetails(businessUnit);
		return businessUnits;
	}

	@Override
	public List<String> getBusinessUnitIdByUserId(String userId) {
		return businessUnitDao.getBusinessUnitIdByUserId(userId);
	}

}