/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.GroupCodeDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.parsers.GroupCodeFileParser;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.GroupCodeService;

/**
 * @author jayshree
 *
 */
@Service
@Transactional(readOnly = true)
public class GroupCodeServiceImpl implements GroupCodeService {
	
	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	
	@Autowired
	GroupCodeDao groupCodeDao;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;
	
	@Override
	public boolean isExists(GroupCode groupCode, String tenantId) {
		return groupCodeDao.isExists(groupCode, tenantId);
	}

	@Override
	public List<GroupCode> findGroupCodesForTenant(String tenantId, TableDataInput input) {
		return groupCodeDao.findGroupCodesForTenant(input, tenantId);
	}

	@Override
	public long findTotalFilteredGroupCodesForTenant(String tenantId, TableDataInput input) {
		return groupCodeDao.findTotalFilteredGroupCodesForTenant(input, tenantId);
	}

	@Override
	public long findTotalGroupCodesForTenant(String tenantId) {
		return groupCodeDao.findTotalGroupCodesForTenant(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveGroupCode(GroupCode groupCode) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + groupCode.getGroupCode()+ "' Group Code settings created ", groupCode.getCreatedBy().getTenantId(), groupCode.getCreatedBy(), new Date(), ModuleType.GroupCode);
		buyerAuditTrailDao.save(ownerAuditTrail);
		groupCodeDao.saveOrUpdate(groupCode);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateGroupCode(GroupCode groupCode) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + groupCode.getGroupCode() + "' Group Code settings updated ", groupCode.getModifiedBy().getTenantId(), groupCode.getModifiedBy(), new Date(), ModuleType.GroupCode);
		buyerAuditTrailDao.save(ownerAuditTrail);
		groupCodeDao.update(groupCode);
	}

	@Override
	public GroupCode getGroupCodeById(String id) {
		GroupCode grC = groupCodeDao.findById(id);
		return grC;
	}

	@Override
	public void downloadGroupCodeCsvFile(HttpServletResponse response, File file, String tenantId) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.GROUP_CODE_CSV_COLUMNS;
			String[] columns = new String[] { "groupCode", "description", "status" };

			long count = groupCodeDao.findCountOfGroupCodesForTenant(tenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processor = getProcessor();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<GroupCode> list = groupCodeDao.findGroupCodeListForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo);
				LOG.info("List ............. " + list.size() + "Count  ..........." + count);

				for (GroupCode user : list) {
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
		CellProcessor[] processor = new CellProcessor[] {
				 new Optional(), new Optional(), new NotNull()
		};
		return processor;
	}
	
	public XSSFWorkbook templateDownloader(XSSFWorkbook workbook) {

		XSSFSheet sheet = workbook.createSheet("Group Code List");
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
		XSSFDataValidationConstraint constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createFormulaListConstraint("'STATUS'!$B$1:$B$" + (3));
		constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createExplicitListConstraint(new String[] { "ACTIVE", "INACTIVE" });
		CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1000, 2, 2);
		XSSFDataValidation validationStatus = (XSSFDataValidation) validationHelperStatus.createValidation(constraintStatus, cellRangeAddressList);
		validationStatus.setSuppressDropDownArrow(true);
		validationStatus.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validationStatus.createErrorBox("Invalid Status Selected", "Please select Status from the list");
		validationStatus.createPromptBox("STATUS List", "Select STATUS from the list provided. It has been exported from your master data.");
		validationStatus.setShowPromptBox(true);
		validationStatus.setShowErrorBox(true);
		sheet.addValidationData(validationStatus);
		workbook.setSheetHidden(1, true);
		
		for (int i = 0; i < 4; i++) {
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
		Cell cell = null;
		for (String column : Global.GROUP_CODE_CSV_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void groupCodeUploadFile(MultipartFile file, String tenantId, User user) throws Exception {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);

		GroupCodeFileParser<GroupCode> gcParser = new GroupCodeFileParser<GroupCode>(GroupCode.class);
		List<GroupCode> groupCodeList = gcParser.parse(convFile);

		if (CollectionUtil.isNotEmpty(groupCodeList)) {
			int row = 2;
			List<String> costCenterNameList = new ArrayList<>();
			for (GroupCode groupCode : groupCodeList) {
				// For validation
				List<String> errorList = validateGroupCode(groupCode, GroupCode.GroupCodeInt.class);
				if (CollectionUtil.isNotEmpty(errorList)) {
					throw new ApplicationException(errorList.toString() + " at row number \"" + row + "\"");
				}
				if (costCenterNameList.contains(groupCode.getGroupCode().toUpperCase())) {
					throw new ApplicationException("Duplicate cost center \'" + groupCode.getGroupCode() + "\' in excel at row number \"" + row + "\"");
				}

				GroupCode dbGroupCode = groupCodeDao.getByGroupCode(groupCode.getGroupCode(), tenantId);
				if (dbGroupCode != null) {
					LOG.info("old group Code :" + groupCode.toString());
					dbGroupCode.setDescription(groupCode.getDescription());
					dbGroupCode.setStatus(groupCode.getStatus());
					dbGroupCode.setModifiedBy(user);
					dbGroupCode.setModifiedDate(new Date());
					groupCodeDao.saveOrUpdate(dbGroupCode);
				} else {
					LOG.info("new group Code :" + groupCode.toString());
					groupCode.setBuyer(user.getBuyer());
					groupCode.setCreatedBy(user);
					groupCode.setCreatedDate(new Date());
					groupCodeDao.save(groupCode);
				}
				row++;
				costCenterNameList.add(groupCode.getGroupCode().toUpperCase());
			}
		}
	}
	
	public List<String> validateGroupCode(GroupCode cc, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<GroupCode>> constraintViolations = validator.validate(cc, validations);
		for (ConstraintViolation<GroupCode> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteGroupCode(GroupCode groupCode) {
		String grC = groupCode.getGroupCode();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + grC + "' Group Code settings deleted ", groupCode.getModifiedBy().getTenantId(), groupCode.getModifiedBy(), new Date(), ModuleType.GroupCode);
		buyerAuditTrailDao.save(ownerAuditTrail);
		groupCodeDao.delete(groupCode);
	}

	@Override
	public List<GroupCode> fetchAllGroupCodesForTenant(String tenantId, String searchVal) {
		List<GroupCode> list = groupCodeDao.fetchAllGroupCodesForTenant(tenantId, searchVal);
		long count = groupCodeDao.findTotalGroupCodesForTenant(tenantId);
		if (list != null && count > list.size()) {
			GroupCode more = new GroupCode();
			more.setId("-1");
			more.setGroupCode("Total " + (count) + " Group Code. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public List<GroupCode> getGroupCodeIdByBusinessId(String buId) {
		return groupCodeDao.getGroupCodeIdByBusinessUnitId(buId);
	}

	@Override
	public List<GroupCode> fetchAllGroupCodeForTenantForUnit(String tenantId, String searchVal, String buId) {
		List<GroupCode> list = groupCodeDao.fetchAllCostCenterForTenantForUnit(tenantId, searchVal, buId);
		long count = groupCodeDao.fetchFilterCountAllCostForTenantForUnit(tenantId, buId);
		if (list != null && count > list.size()) {
			GroupCode more = new GroupCode();
			more.setId("-1");
			more.setGroupCode("Total " + (count) + " Group Code. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public long getCountOfInactiveGroupCode(String buId) {
		return groupCodeDao.getCountOfInactiveGroupCode(buId);
	}

	@Override
	public List<GroupCode> findGroupCodeListByTenantId(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds) {
		return groupCodeDao.findGroupCodeListByTenantId(tenantId, input, id, groupCodeIds, removeIds);
	}

	@Override
	public long findTotalFilteredGroupCodeForTenant(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds) {
		return groupCodeDao.findTotalFilteredGroupCodeForTenant(tenantId, input, id, groupCodeIds, removeIds);
	}

	@Override
	public List<GroupCode> fetchAllActiveGroupCodeForTenantID(String tenantId) {
		return groupCodeDao.fetchAllActiveGroupCodeForTenantID(tenantId);
	}
	
	@Override
	public List<String> getGroupCodeByBusinessId(String buId) {
		return groupCodeDao.getGroupCodeByBusinessId(buId);
	}

	@Override
	public GroupCode getGroupCodeByGCId(String gcId) {
		return groupCodeDao.getGroupCodeById(gcId);
	}

	@Override
	public List<GroupCode> getGroupCodedByIds(List<String> gcIds) {
		return groupCodeDao.getGroupCodedByIds(gcIds);
	}

	@Override
	public List<GroupCode> getAllGroupCodeIdByBusinessUnitId(String buId) {
		return groupCodeDao.getAllGroupCodeIdByBusinessUnitId(buId);
	}

}
