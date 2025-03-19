package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
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
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.parsers.CostCenterFileParser;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.CostCenterService;

@Service
@Transactional(readOnly = true)
public class CostCenterServiceImpl implements CostCenterService {

	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Override
	@Transactional(readOnly = false)
	public void saveCostCenter(CostCenter costCenter) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + costCenter.getCostCenter() + "' Cost Center settings created ", costCenter.getCreatedBy().getTenantId(), costCenter.getCreatedBy(), new Date(), ModuleType.CostCenter);
		buyerAuditTrailDao.save(ownerAuditTrail);
		costCenterDao.saveOrUpdate(costCenter);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCostCenter(CostCenter costCenter) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + costCenter.getCostCenter() + "' Cost Center settings updated ", costCenter.getModifiedBy().getTenantId(), costCenter.getModifiedBy(), new Date(), ModuleType.CostCenter);
		buyerAuditTrailDao.save(ownerAuditTrail);
		costCenterDao.update(costCenter);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCostCenter(CostCenter costCenter) {
		String cost = costCenter.getCostCenter();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + cost + "' Cost Center settings deleted ", costCenter.getModifiedBy().getTenantId(), costCenter.getModifiedBy(), new Date(), ModuleType.CostCenter);
		buyerAuditTrailDao.save(ownerAuditTrail);
		costCenterDao.delete(costCenter);
	}

	@Override
	public boolean isExists(CostCenter costCenter, String tenantId) {
		return costCenterDao.isExists(costCenter, tenantId);
	}

	@Override
	public CostCenter getCostCenterById(String id) {
		CostCenter costCenter = costCenterDao.findById(id);
		/*
		 * if (costCenter != null && costCenter.getBuyer() != null) costCenter.getBuyer().getFullName();
		 */
		return costCenter;
	}

	// @Override
	// public List<CostCenterPojo> getAllCostCenterPojo() {
	// List<CostCenterPojo> returnList = new ArrayList<CostCenterPojo>();
	// List<CostCenter> list = costCenterDao.getAllCostCenter();
	// if (CollectionUtil.isNotEmpty(list)) {
	//
	// for (CostCenter costCenter : list) {
	// if (costCenter.getCreatedBy() != null)
	// costCenter.getCreatedBy().getLoginId();
	// if (costCenter.getModifiedBy() != null)
	// costCenter.getModifiedBy().getLoginId();
	//
	// CostCenterPojo cp = new CostCenterPojo(costCenter);
	//
	// returnList.add(cp);
	// }
	// }
	//
	// return returnList;
	// }
	//
	@Override
	public List<CostCenter> getAllActiveCostCentersForTenant(String tenantId) {
		return costCenterDao.getAllActiveCostCentersForTenant(tenantId);
	}

	@Override
	public List<CostCenter> findCostCentersForTenant(String tenantId, TableDataInput tableParams) {
		return costCenterDao.findCostCentersForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalFilteredCostCentersForTenant(String tenantId, TableDataInput tableParams) {
		return costCenterDao.findTotalFilteredCostCentersForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalCostCentersForTenant(String tenantId) {
		return costCenterDao.findTotalCostCentersForTenant(tenantId);
	}

	@Override
	public List<CostCenter> getAllCostCentersByTenantId(String tenantId) {
		return costCenterDao.getAllCostCentersByTenantId(tenantId);
	}

	@Override
	public void costCenterDownloadTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException {
		LOG.info("Cost center template called.");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String downloadFolder = context.getRealPath("/WEB-INF/");
		String fileName = "CostCenterTemplate.xlsx";
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
		for (String column : Global.COST_CENTER_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void costCenterUploadFile(MultipartFile file, String tenantId, User loggedInUser) throws Exception {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);

		CostCenterFileParser<CostCenter> ccParser = new CostCenterFileParser<CostCenter>(CostCenter.class);
		List<CostCenter> costCenterList = ccParser.parse(convFile);

		if (CollectionUtil.isNotEmpty(costCenterList)) {
			int row = 2;
			List<String> costCenterNameList = new ArrayList<>();
			for (CostCenter costCenter : costCenterList) {
				// For validation
				List<String> errorList = validateCostCenter(costCenter, CostCenter.CostCenterInt.class);
				if (CollectionUtil.isNotEmpty(errorList)) {
					throw new ApplicationException(errorList.toString() + " at row number \"" + row + "\"");
				}
				if (costCenterNameList.contains(costCenter.getCostCenter().toUpperCase())) {
					throw new ApplicationException("Duplicate cost center \'" + costCenter.getCostCenter() + "\' in excel at row number \"" + row + "\"");
				}

				CostCenter dbCostCenter = costCenterDao.getByCostCenter(costCenter.getCostCenter(), tenantId);
				if (dbCostCenter != null) {
					LOG.info("old cost center :" + costCenter.toLogString());
					dbCostCenter.setDescription(costCenter.getDescription());
					dbCostCenter.setStatus(costCenter.getStatus());
					dbCostCenter.setModifiedBy(loggedInUser);
					dbCostCenter.setModifiedDate(new Date());
					costCenterDao.saveOrUpdate(dbCostCenter);
				} else {
					LOG.info("new cost center :" + costCenter.toLogString());
					costCenter.setBuyer(loggedInUser.getBuyer());
					costCenter.setCreatedBy(loggedInUser);
					costCenter.setCreatedDate(new Date());
					costCenterDao.save(costCenter);
				}
				row++;
				costCenterNameList.add(costCenter.getCostCenter().toUpperCase());
			}
		}

	}

	/**
	 * @param cc
	 */
	public List<String> validateCostCenter(CostCenter cc, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<CostCenter>> constraintViolations = validator.validate(cc, validations);
		for (ConstraintViolation<CostCenter> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@Override
	public void costCenterExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "CosCenter.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = getExcelWorkBook(loggedInUserTenantId);
			FileUtil.writeXssfExcelToZip(zos, workbook, parentFolder, fileName);
			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	private XSSFWorkbook getExcelWorkBook(String loggedInUserTenantId) {
		// TODO Auto-generated method stub
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Cost Center List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<CostCenter> costCenterList = getAllCostCentersByTenantId(loggedInUserTenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(costCenterList)) {
			for (CostCenter costCenter : costCenterList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(costCenter.getCostCenter());
				row.createCell(cellNum++).setCellValue(costCenter.getDescription());
				row.createCell(cellNum++).setCellValue(costCenter.getStatus() != null ? costCenter.getStatus().toString() : "");
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

	@Override
	public List<CostCenter> getActiveCostCentersForTenant(String tenantId) {
		return costCenterDao.getActiveCostCentersForTenant(tenantId);
	}

	@Override
	public CostCenter getActiveCostCenterForTenantByCostCenterName(String costCenterName, String tenantId) {
		return costCenterDao.getActiveCostCenterForTenantByCostCenterName(costCenterName, tenantId);
	}

	@Override
	public List<CostCenterPojo> fetchAllCostCenterForTenant(String tenantId, String searchValue, String businessUnitId) {
		List<CostCenterPojo> list = costCenterDao.fetchAllCostCenterForTenant(tenantId, searchValue, businessUnitId);
		long count = costCenterDao.fetchFilterCountAllCostForTenant(tenantId, businessUnitId);
		if (list != null && count > list.size()) {
			CostCenterPojo more = new CostCenterPojo();
			more.setId("-1");
			more.setCostCenter("Total " + (count) + " cost center. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public List<String> getCostCenterByBusinessId(String id) {
		return costCenterDao.getCostCenterByBusinessId(id);
	}

	@Override
	public List<CostCenterPojo> fetchAllCostCenterForTenantForUnit(String tenantId, String searchValue, List<String> assignedCostId, String buId) {
		List<CostCenterPojo> list = costCenterDao.fetchAllCostCenterForTenantForUnit(tenantId, searchValue, assignedCostId, buId);
		long count = costCenterDao.fetchFilterCountAllCostForTenantForUnit(tenantId, assignedCostId, buId);
		if (count > list.size()) {
			CostCenterPojo more = new CostCenterPojo();
			more.setId("-1");
			more.setCostCenter("Total " + (count) + " cost center. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public List<CostCenterPojo> findCostCenterListByTenantId(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds) {
		return costCenterDao.findCostCenterListByTenantId(tenantId, input, id, costCenterIds, removeIds);
	}

	@Override
	public long findTotalFilteredCostCenterForTenant(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds) {
		return costCenterDao.findTotalFilteredCostCenterForTenant(tenantId, input, id, costCenterIds, removeIds);
	}

	@Override
	public CostCenterPojo getCostCenterByCostId(String id) {
		return costCenterDao.getCostCenterByCostId(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAssignedCostCenter(String id) {
		costCenterDao.deleteAssignedCostCenter(id);
	}

	@Override
	public CostCenter getCostCenterBycostId(String id) {
		CostCenter cost = costCenterDao.findById(id);
		if (cost != null) {
			if (cost.getCreatedBy() != null) {
				cost.getCreatedBy().getName();
			}
			if (cost.getModifiedBy() != null) {
				cost.getModifiedBy().getName();
			}
		}
		return cost;
	}

	@Override
	public long getCountOfInactiveCostCenter(List<String> assignedCostId) {
		return costCenterDao.getCountOfInactiveCostCenter(assignedCostId);
	}

	@Override
	public List<String> getListOfAssignedCostCenterIdsForBusinessUnit(String buId) {
		return costCenterDao.getCostCenterByBusinessId(buId);
	}

	@Override
	public void downloadCostCenterCsvFile(HttpServletResponse response, File file, String tenantId) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.COST_CENTER_EXCEL_COLUMNS;
			String[] columns = new String[] { "costCenter", "description", "status" };

			long count = costCenterDao.findAllCostCentersForTenant(tenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processor = getProcessor();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<CostCenter> list = costCenterDao.findCostCenterListForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo);
				LOG.info("List ............. " + list.size() + "Count  ..........." + count);

				for (CostCenter user : list) {
					beanWriter.write(user, columns, processor);
				}
				beanWriter.flush();
				LOG.info("Write done.................................");
			}
			beanWriter.close();
			beanWriter = null;

		} catch (Exception e) {
			LOG.info("Error ......... : " + e.getMessage(), e);
		}
	}

	private CellProcessor[] getProcessor() {
		CellProcessor[] processor = new CellProcessor[] { new Optional(), new Optional(), new Optional() };
		return processor;
	}

	@Override
	public List<CostCenter> getCostCentersByBusinessUnitIdForAwardScreen(String id) {
		return costCenterDao.getCostCentersByBusinessUnitIdForAwardScreen(id);
	}

}
