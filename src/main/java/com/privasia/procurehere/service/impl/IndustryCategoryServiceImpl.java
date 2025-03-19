package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.IndustryCategoryService;

@Service
@Transactional(readOnly = true)
public class IndustryCategoryServiceImpl implements IndustryCategoryService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Override
	@Transactional(readOnly = false)
	public void save(IndustryCategory industryCategory) {
		industryCategoryDao.saveOrUpdate(industryCategory);
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + industryCategory.getName() + "' Industry Category settings created", industryCategory.getCreatedBy().getTenantId(), industryCategory.getCreatedBy(), new Date(), ModuleType.IndustryCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);

	}

	@Override
	@Transactional(readOnly = false)
	public void update(IndustryCategory industryCategory) {
		industryCategoryDao.update(industryCategory);
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + industryCategory.getName() + "' Industry Category settings updated", industryCategory.getModifiedBy().getTenantId(), industryCategory.getModifiedBy(), new Date(), ModuleType.IndustryCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);

	}

	@Override
	@Transactional(readOnly = false)
	public void delete(IndustryCategory industryCategory) {
		String industryName = industryCategory.getName();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + industryName + "' Industry Category settings deleted ", industryCategory.getModifiedBy().getTenantId(), industryCategory.getModifiedBy(), new Date(), ModuleType.IndustryCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
		industryCategoryDao.delete(industryCategory);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExists(IndustryCategory industryCategory, String tenantId) {
		return industryCategoryDao.isExists(industryCategory, tenantId);
	}

	@Override
	public IndustryCategory getIndustryCategoryById(String id) {
		return industryCategoryDao.findById(id);
	}

	public IndustryCategory getIndustryCategoryNameAndCodeById(String id) {
		return industryCategoryDao.findById(id);
	}

	@Override
	public IndustryCategory getIndustryCategoryByCode(String code, String tenantId) {
		return industryCategoryDao.findIndustryCategoryByCodeAndTenantId(code, tenantId);
	}

	@Override
	public IndustryCategory getIndustryCategoryByCodeExceptStatus(String code, String tenantId) {
		return industryCategoryDao.findIndustryCategoryByCodeAndTenantIdAndExceptStatus(code, tenantId);
	}

	@Override
	public List<IndustryCategory> findIndustryCategoryByNameAndTenantId(String searchValue, String tenantId) {
		List<IndustryCategory> icList = industryCategoryDao.findIndustryCategoryByNameAndTenantId(searchValue, tenantId);
		if (icList != null) {
			for (IndustryCategory bic : icList) {
				bic.setCreatedBy(null);
				bic.setModifiedBy(null);
				// LOG.info("Matching Buyer Industry Category : " +
				// bic.getCode() + " - " + bic.getName());
			}
		}
		return icList;
	}

	@Override
	public List<IndustryCategory> findIndustryCategoryForTenant(String tenantId, TableDataInput tableParams) {
		return industryCategoryDao.findIndustryCategoryForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalIndustryCategoryForTenant(String tenantId) {
		return industryCategoryDao.findTotalIndustryCategoryForTenant(tenantId);
	}

	@Override
	public long findTotalFilteredIndustryCategoryForTenant(String tenantId, TableDataInput tableParams) {
		return industryCategoryDao.findTotalFilteredIndustryCategoryForTenant(tenantId, tableParams);
	}

	@Override
	public void loadNaicsCodesForTenant(String tenantId, String userId) {
		List<NaicsCodes> loadIcList = this.findAllLeafIndustryCategory();
		LOG.info("NAICS Codes to load : " + loadIcList.size());
		industryCategoryDao.loadNaicsCodesForTenant(loadIcList, tenantId, userId);
	}

	@Override
	public List<NaicsCodes> findAllLeafIndustryCategory() {
		return naicsCodesDao.findLeafIndustryCategoryByName(null);
	}

	@Override
	public long countIndustryCategory() {
		return industryCategoryDao.countIndustryCategory();
	}

	@Override
	public List<IndustryCategory> getAllIndustryCategoryByIds(List<String> ids) {
		return industryCategoryDao.getAllIndustryCategoryByIds(ids);
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.INDUSTRY_CATEGORY_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public void industryCategoryExportTemplate(HttpServletResponse response, String loggedInUserTenantId) {
		// TODO Auto-generated method stub
		LOG.info("downloadIndustryCategoryListExcel method called");

		try {
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "IndustryCategory.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);
			XSSFWorkbook workbook = getExcelWorkbook(loggedInUserTenantId);
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

	public XSSFWorkbook getExcelWorkbook(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("IndustryCategory List");
		buildHeader(workbook, sheet);
		List<IndustryCategory> industryCategoryList = industryCategoryDao.getAllIndustryCategoryForTenant(loggedInUserTenantId);
		int r = 1;
		if (CollectionUtil.isNotEmpty(industryCategoryList)) {
			for (IndustryCategory industryCategory : industryCategoryList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(industryCategory.getId());
				row.createCell(cellNum++).setCellValue(industryCategory.getCode());
				row.createCell(cellNum++).setCellValue(industryCategory.getName());
				row.createCell(cellNum++).setCellValue(industryCategory.getStatus() != null ? industryCategory.getStatus().toString() : "");
			}
		}
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	@Override
	public void industryCategoryExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder) {
		LOG.info("downloadIndustryCategoryListExcel method called");
		try {
			String fileName = "IndustryCategory.xlsx";
			XSSFWorkbook workbook = getExcelWorkbook(loggedInUserTenantId);
			FileUtil.writeXssfExcelToZip(zos, workbook, parentFolder, fileName);
		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}
	}

	@Override
	public List<ProductCategory> getAllProductCategoryByIds(List<String> productCategory) {
		return industryCategoryDao.getAllProductCategoryByIds(productCategory);
	}

	@Override
	public IndustryCategory getIndustryCategorCodeAndNameById(String id) {
		return industryCategoryDao.getIndustryCategorCodeAndNameById(id);
	}

	@Override
	public List<IndustryCategory> getAllIndustryCategoryOnlyByIds(List<String> ids) {
		return industryCategoryDao.getAllIndustryCategoryOnlyByIds(ids);
	}

	@Override
	public void downloadCsvFileForIndustryCategory(HttpServletResponse response, File file, IndustryCategoryPojo industryCategoryPojo, String loggedInUserTenantId) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.INDUSTRY_CATEGORY_CSV_COLUMNS;

			String[] columns = { "id", "code", "name", "status" };

			long count = industryCategoryDao.findTotalIndustryCategoryForTenant(loggedInUserTenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<IndustryCategory> list = industryCategoryDao.getAllIndustryCategoryForCsv(loggedInUserTenantId, PAGE_SIZE, pageNo);

				for (IndustryCategory industryCategory : list) {
					beanWriter.write(industryCategory, columns, processors);
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
				new Optional(),

		};
		return processors;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndustryCategory> findActiveIndustryCategoryByTenantId(String tenantId) {
		return industryCategoryDao.findActiveIndustryCategoryByTenantId(tenantId);
	}

}
