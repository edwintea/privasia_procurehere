package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.parsers.GroupCodeFileParser;
import com.privasia.procurehere.core.parsers.ProcurementMehtodFileParser;
import com.privasia.procurehere.core.utils.SecurityLibrary;
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
import com.privasia.procurehere.core.dao.ProcurementMethodDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.ProcurementMethodService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author sana
 */
@Service
@Transactional(readOnly = true)
public class ProcurementMethodServiceImpl implements ProcurementMethodService {

	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Override
	public List<ProcurementMethod> findProcurementMethodsForTenant(String tenantId, TableDataInput input) {
		return procurementMethodDao.findProcurementMethodsForTenant(tenantId, input);
	}

	@Override
	public long findTotalFilteredProcurementMethodsForTenant(String tenantId, TableDataInput input) {
		return procurementMethodDao.findTotalFilteredProcurementMethodsForTenant(tenantId, input);
	}

	@Override
	public long findCountOfProcurementMethodsForTenant(String tenantId) {
		return procurementMethodDao.findCountOfProcurementMethodsForTenant(tenantId);
	}

	@Override
	public boolean isExists(ProcurementMethod procurementMethod, String tenantId) {
		return procurementMethodDao.isExists(procurementMethod, tenantId);
	}

	@Override
	public ProcurementMethod getProcurementMethodById(String id) {
		return procurementMethodDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveProcurementMethod(ProcurementMethod procurementMethod) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + procurementMethod.getProcurementMethod() + "' Procurement Method settings created ", procurementMethod.getCreatedBy().getTenantId(), procurementMethod.getCreatedBy(), new Date(), ModuleType.ProcurementMethod);
		buyerAuditTrailDao.save(ownerAuditTrail);
		procurementMethodDao.saveOrUpdate(procurementMethod);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateProcurementMethod(ProcurementMethod procurementMethod) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + procurementMethod.getProcurementMethod() + "' Procurement Method settings updated ", procurementMethod.getModifiedBy().getTenantId(), procurementMethod.getModifiedBy(), new Date(), ModuleType.ProcurementMethod);
		buyerAuditTrailDao.save(ownerAuditTrail);
		procurementMethodDao.update(procurementMethod);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProcurementMethod(ProcurementMethod procurementMethod) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + procurementMethod.getProcurementMethod() + "' Procurement Method settings deleted ", procurementMethod.getModifiedBy().getTenantId(), procurementMethod.getModifiedBy(), new Date(), ModuleType.ProcurementMethod);
		buyerAuditTrailDao.save(ownerAuditTrail);
		procurementMethodDao.delete(procurementMethod);

	}

	@Override
	public void procurementMethodDownloadTemplate(HttpServletResponse response, String tenantId) {
		try {
			LOG.info("Procurement Method template called.");
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ProcurementMethod.csv";
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
		// Auto-generated method stub
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Procurement Method List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<ProcurementMethod> procurementMethodList = procurementMethodDao.getAllProcurementMethodByTenantId(tenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(procurementMethodList)) {
			for (ProcurementMethod procurementMethod : procurementMethodList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(procurementMethod.getProcurementMethodCode());
				row.createCell(cellNum++).setCellValue(procurementMethod.getProcurementMethod());
				row.createCell(cellNum++).setCellValue(procurementMethod.getDescription());
				row.createCell(cellNum++).setCellValue(procurementMethod.getStatus() != null ? procurementMethod.getStatus().toString() : "");
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

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PROCUREMENT_METHOD_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public List<ProcurementMethod> getAllActiveProcurementMethod(String tenantId) {
		return procurementMethodDao.getAllActiveProcurementMethod(tenantId);
	}

	public XSSFWorkbook templateDownloader(XSSFWorkbook workbook) {

		XSSFSheet sheet = workbook.createSheet("Procurement Method List");
		buildHeader(workbook, sheet);

		XSSFSheet lookupSheet4 = workbook.createSheet("STATUS");
		int index2 = 0;
		Status[] statsArr = Status.values();
		for (Status status : statsArr) {
			XSSFRow firstRow = lookupSheet4.createRow(index2++);
			XSSFCell cell1 = firstRow.createCell(0);
			cell1.setCellValue(status.toString());
		}

		// Status
		XSSFDataValidationHelper validationHelperStatus = new XSSFDataValidationHelper(lookupSheet4);
		XSSFDataValidationConstraint constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createFormulaListConstraint("'STATUS'!$C$1:$C$" + (4));
		constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createExplicitListConstraint(new String[] { "ACTIVE", "INACTIVE" });
		CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1000, 3, 3);
		XSSFDataValidation validationStatus = (XSSFDataValidation) validationHelperStatus.createValidation(constraintStatus, cellRangeAddressList);
		validationStatus.setSuppressDropDownArrow(true);
		validationStatus.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validationStatus.createErrorBox("Invalid Status Selected", "Please select Status from the list");
		validationStatus.createPromptBox("STATUS List", "Select STATUS from the list provided. It has been exported from your master data.");
		validationStatus.setShowPromptBox(true);
		validationStatus.setShowErrorBox(true);
		sheet.addValidationData(validationStatus);
		workbook.setSheetHidden(1, true);

		for (int i = 0; i < 5; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void procurementMethodUploadFile(MultipartFile file, String tenantId, User user) throws Exception {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);

		ProcurementMehtodFileParser<ProcurementMethod> gcParser = new ProcurementMehtodFileParser<ProcurementMethod>(ProcurementMethod.class);
		List<ProcurementMethod> procurementMethodList = gcParser.parse(convFile);

		if (CollectionUtil.isNotEmpty(procurementMethodList)) {
			int row = 2;
			List<String> procurementMethodNameList = new ArrayList<>();
			for (ProcurementMethod procurementMethod : procurementMethodList) {
				// For validation
				List<String> errorList = validateProcurementMethod(procurementMethod, ProcurementMethod.ProcurementMethodInt.class);
				if (CollectionUtil.isNotEmpty(errorList)) {
					StringBuilder errorMessage = new StringBuilder();
					for (String error : errorList) {
						errorMessage.append(error).append(", ");
					}
					// Remove the trailing comma and space
					errorMessage.setLength(errorMessage.length() - 2);

					throw new ApplicationException(errorMessage.toString() + " at row number \"" + row + "\"");
				}
				if (isExists(procurementMethod, SecurityLibrary.getLoggedInUserTenantId())) {
					throw new ApplicationException("Duplicate ProcurementMethod \'" + procurementMethod.getProcurementMethod() + "\' in excel at row number \"" + row + "\"");
				}

				if(procurementMethod.getStatus() == null){
					throw new ApplicationException("Status is empty " + " at row number \"" + row + "\"");
				}

				ProcurementMethod dbProcurementMethod = procurementMethodDao.getByProcurementMethod(procurementMethod.getProcurementMethod(), tenantId);
				if (dbProcurementMethod != null) {
					LOG.info("old ProcurementMethod :" + procurementMethod.toString());
					dbProcurementMethod.setProcurementMethodCode(procurementMethod.getProcurementMethodCode());
					dbProcurementMethod.setDescription(procurementMethod.getDescription());
					dbProcurementMethod.setStatus(procurementMethod.getStatus() );
					dbProcurementMethod.setModifiedBy(user);
					dbProcurementMethod.setModifiedDate(new Date());
					procurementMethodDao.saveOrUpdate(dbProcurementMethod);
				} else {
					LOG.info("new procurement Method :" + procurementMethod.toString());
					procurementMethod.setBuyer(user.getBuyer());
					procurementMethod.setCreatedBy(user);
					procurementMethod.setCreatedDate(new Date());
					procurementMethodDao.save(procurementMethod);
				}
				row++;
				procurementMethodNameList.add(procurementMethod.getProcurementMethod().toUpperCase());
			}
		}
	}

	public List<String> validateProcurementMethod(ProcurementMethod cc, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<ProcurementMethod>> constraintViolations = validator.validate(cc, validations);
		for (ConstraintViolation<ProcurementMethod> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}
}
