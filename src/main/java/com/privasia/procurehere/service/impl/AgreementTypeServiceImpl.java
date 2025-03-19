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
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.AgreementTypeDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.AgreementTypeService;

@Service
@Transactional(readOnly = true)
public class AgreementTypeServiceImpl implements AgreementTypeService {

	private static final Logger LOG = LogManager.getLogger(AgreementTypeServiceImpl.class);

	@Autowired(required = true)
	AgreementTypeDao agreementTypeDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Override
	@Transactional(readOnly = false)
	public String createAgreementType(AgreementType agreementType) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + agreementType.getAgreementType() + "' Agreement Type settings created", agreementType.getCreatedBy().getTenantId(), agreementType.getCreatedBy(), new Date(), ModuleType.AgreementType);
		buyerAuditTrailDao.save(ownerAuditTrail);
		agreementType = agreementTypeDao.saveOrUpdate(agreementType);
		return (agreementType != null ? agreementType.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public AgreementType updateAgreementType(AgreementType agreementType) {
		BuyerAuditTrail ownerAuditTrail = null;
		LOG.info(agreementType == null);
		if (agreementType != null && agreementType.getModifiedBy() != null) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + agreementType.getAgreementType() + "' Agreement Type settings updated", agreementType.getModifiedBy().getTenantId(), agreementType.getModifiedBy(), new Date(), ModuleType.AgreementType);
			buyerAuditTrailDao.save(ownerAuditTrail);
		}
		return agreementTypeDao.update(agreementType);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAgreementType(AgreementType agreementType) {
		BuyerAuditTrail ownerAuditTrail = null;
		if (agreementType.getModifiedBy() != null) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + agreementType.getAgreementType() + "' Agreement Type settings deleted", agreementType.getModifiedBy().getTenantId(), agreementType.getModifiedBy(), new Date(), ModuleType.AgreementType);
		}
		agreementTypeDao.delete(agreementType);
		LOG.info(agreementType);
		buyerAuditTrailDao.save(ownerAuditTrail);

	}

	@Override
	public AgreementType getAgreementTypeById(String id) {
		LOG.info(id);
		return agreementTypeDao.findById(id);
	}

	@Override
	public boolean isExists(AgreementType agreementType) {
		return agreementTypeDao.isExists(agreementType);
	}

	@Override
	public List<AgreementType> getAllAgreementType(String tenantId) {
		List<AgreementType> returnList = new ArrayList<AgreementType>();
		List<AgreementType> list = agreementTypeDao.getAllActiveAgreementTypeForTenant(tenantId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (AgreementType at : list) {
				if (at.getCreatedBy() != null)
					at.getCreatedBy().getLoginId();
				if (at.getModifiedBy() != null)
					at.getModifiedBy().getLoginId();

				AgreementType agt = new AgreementType(at);
				returnList.add(agt);
			}
		}
		return returnList;
	}

	@Override
	public AgreementType getAgreementTypebyCode(String agreementType, String tenantId) {
		return agreementTypeDao.getAgreementTypebyCode(agreementType, tenantId);
	}

	@Override
	public List<AgreementType> findAgreementTypeForTenant(String tenantId, TableDataInput tableParams) {
		return agreementTypeDao.findAgreementTypeForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput tableParams) {
		return agreementTypeDao.findTotalFilteredAgreementTypeForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalActiveAgreementTypeForTenant(String tenantId) {
		return agreementTypeDao.findTotalActiveAgreementTypeForTenant(tenantId);
	}

	@Override
	public List<AgreementType> getAllActiveAgreementTypeForTenant(String tenantId) {
		return agreementTypeDao.getAllActiveAgreementTypeForTenant(tenantId);
	}

	@Override
	public AgreementType getAgreementTypeByAgreementTypeAndTenantId(String agreementType, String tenantId) {
		return agreementTypeDao.getAgreementTypeByAgreementTypeAndTenantId(agreementType, tenantId);
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.AGREEMENT_TYPE_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public void agreementTypeDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) {
		LOG.info("downloadProdCategoryListExcel method called");
		try {

			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "AGREEMENT_TYPE.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);
			XSSFWorkbook workbook = getExcelWorkBook(loggedInUserTenantId);
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
		XSSFSheet sheet = workbook.createSheet("AgreementType List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<AgreementType> atList = agreementTypeDao.getAllAgreementTypeForTenant(loggedInUserTenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(atList)) {
			for (AgreementType at : atList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(at.getId());
				row.createCell(cellNum++).setCellValue(at.getAgreementType());
				row.createCell(cellNum++).setCellValue(at.getDescription());
				row.createCell(cellNum++).setCellValue(at.getStatus() != null ? at.getStatus().toString() : "");
			}
		}
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}

		return workbook;
	}

	@Override
	public void agreementTypeExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String zipFileNames) {
		LOG.info("downloadProdCategoryListExcel method called");
		try {
			String fileName = "AGREEMENT_TYPE.xlsx";
			XSSFWorkbook workbook = getExcelWorkBook(loggedInUserTenantId);
			FileUtil.writeXssfExcelToZip(zos, workbook, zipFileNames, fileName);
			LOG.info("Successfully written in Excel");

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	@Override
	public boolean isExists(String agreementType) {
		return agreementTypeDao.isExists(agreementType);
	}

	@Override
	public List<AgreementType> fetchAllActiveAgreementTypeForTenant(String tenantId, String search) {
		List<AgreementType> list = agreementTypeDao.fetchAllActiveAgreementTypeForTenant(tenantId, search);
		long count = agreementTypeDao.countConstructQueryToFetchAgreementType(tenantId);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				AgreementType more = new AgreementType();
				more.setAgreementType("+" + (count - list.size()) + " more. Continue typing to find match...");
				more.setDescription("");
				list.add(more);
			}
		}
		return list;
	}

	@Override
	public List<AgreementType> fetchAllAgreementTypeForTenant(String tenantId, String searchVal) {
		List<AgreementType> list = agreementTypeDao.fetchAllAgreementTypeForTenant(tenantId, searchVal);
		long count = agreementTypeDao.findTotalAgreementTypeForTenant(tenantId);
		if (list != null && count > list.size()) {
			AgreementType more = new AgreementType();
			more.setId("-1");
			more.setAgreementType("Total " + (count) + " Agreement Type. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public List<AgreementType> getAllAgreementTypeIdByBusinessUnitId(String buId) {
		return agreementTypeDao.getAllAgreementTypeIdByBusinessUnitId(buId);
	}

	@Override
	public long getCountOfInactiveAgreementType(String buId) {
		return agreementTypeDao.getCountOfInactiveAgreementType(buId);
	}

	@Override
	public List<AgreementType> fetchAllAgreementTypeForTenantForUnit(String tenantId, String searchVal, String buId) {
		List<AgreementType> list = agreementTypeDao.fetchAllAgreementTypeForTenantForUnit(tenantId, searchVal, buId);
		long count = agreementTypeDao.fetchFilterCountAllAgreementTypeForTenantForUnit(tenantId, buId);
		if (list != null && count > list.size()) {
			AgreementType more = new AgreementType();
			more.setId("-1");
			more.setAgreementType("Total " + (count) + " Agreement Type. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public List<AgreementType> findAgreementTypeListByTenantId(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds) {
		return agreementTypeDao.findAgreementTypeListByTenantId(tenantId, input, id, agreementTypeIds, removeIds);
	}

	@Override
	public long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds) {
		return agreementTypeDao.findTotalFilteredAgreementTypeForTenant(tenantId, input, id, agreementTypeIds, removeIds);
	}

	@Override
	public void downloadCsvFileForAgreementType(HttpServletResponse response, File file, AgreementType agreementType, String loggedInUserTenantId) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.AGREEMENT_TYPE_REPORT_CSV_COLUMNS;

			String[] columns = { "agreementType", "description", "status" };

			long count = agreementTypeDao.findTotalAgreementTypeCountForCsv(loggedInUserTenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<AgreementType> list = agreementTypeDao.getAllAgreementTypeForCsv(loggedInUserTenantId, PAGE_SIZE, pageNo);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (AgreementType at : list) {
					beanWriter.write(at, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (Exception e) {
			LOG.info("Error ..." + e, e);
		}
	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), // Temp name
				new Optional(), // Event id
				new Optional(), // rEf no

		};
		return processors;
	}

	@Override
	public List<AgreementType> getAllActiveAgreementType(String tenantId) {
		List<AgreementType> list = agreementTypeDao.getAllActiveAgreementType(tenantId);
		long count = agreementTypeDao.findTotalAgreementTypeForTenant(tenantId);
		if (list != null && count > list.size()) {
			AgreementType more = new AgreementType();
			more.setId(null);
			more.setAgreementType("Total " + (count) + " Agreement Type. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

}
