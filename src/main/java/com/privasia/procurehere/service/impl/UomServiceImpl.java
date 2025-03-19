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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class UomServiceImpl implements UomService {

	private static final Logger LOG = LogManager.getLogger(UomServiceImpl.class);

	@Autowired(required = true)
	UomDao uomDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Override
	@Transactional(readOnly = false)
	public String createUom(Uom uom) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + uom.getUom() + "' UOM settings created", uom.getCreatedBy().getTenantId(), uom.getCreatedBy(), new Date(), ModuleType.Uom);
		buyerAuditTrailDao.save(ownerAuditTrail);
		uom = uomDao.saveOrUpdate(uom);
		return (uom != null ? uom.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public Uom updateUom(Uom uom) {
		BuyerAuditTrail ownerAuditTrail = null;
		LOG.info(uom == null);
		if (uom != null && uom.getModifiedBy() != null) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + uom.getUom() + "' UOM settings updated", uom.getModifiedBy().getTenantId(), uom.getModifiedBy(), new Date(), ModuleType.Uom);
			buyerAuditTrailDao.save(ownerAuditTrail);
		}
		return uomDao.update(uom);

	}

	@Override
	@Transactional(readOnly = false)
	public void loadDefaultUomIntoBuyerAccount(Buyer buyer, Owner owner) {
		List<Uom> uomList = uomDao.getAllActiveUomForTenant(owner.getId(), TenantType.OWNER);
		if (CollectionUtil.isNotEmpty(uomList)) {
			List<Uom> loadList = new ArrayList<Uom>();
			for (Uom uom : uomList) {
				Uom loadUom = uom.createShallowCopy();
				loadUom.setId(null);
				loadUom.setBuyer(buyer);
				loadUom.setOwner(null);
				loadList.add(loadUom);
			}
			uomDao.batchInsert(loadList);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteUom(Uom uom) {
		BuyerAuditTrail ownerAuditTrail = null;
		if (uom.getModifiedBy() != null) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + uom.getUom() + "' UOM settings deleted", uom.getModifiedBy().getTenantId(), uom.getModifiedBy(), new Date(), ModuleType.Uom);
		}
		uomDao.delete(uom);
		LOG.info(uom);
		buyerAuditTrailDao.save(ownerAuditTrail);

	}

	@Override
	public Uom getUomById(String id) {
		LOG.info(id);
		return uomDao.findById(id);
	}

	@Override
	public boolean isExists(Uom uom, TenantType tenantType) {
		return uomDao.isExists(uom, tenantType);
	}

	@Override
	public List<UomPojo> getAllUomPojo(String buyerId) {
		List<UomPojo> returnList = new ArrayList<UomPojo>();
		List<Uom> list = uomDao.getAllActiveUomForTenant(buyerId, TenantType.BUYER);
		if (CollectionUtil.isNotEmpty(list)) {
			for (Uom uom : list) {
				if (uom.getCreatedBy() != null)
					uom.getCreatedBy().getLoginId();
				if (uom.getModifiedBy() != null)
					uom.getModifiedBy().getLoginId();

				UomPojo up = new UomPojo(uom);
				returnList.add(up);
			}
		}
		return returnList;
	}

	@Override
	public Uom getUombyCode(String uom, String tenantId) {
		return uomDao.getUombyCode(uom, tenantId);
	}

	@Override
	public List<Uom> findUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType) {
		return uomDao.findUomForTenant(tenantId, tableParams, tenantType);
	}

	@Override
	public long findTotalFilteredUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType) {
		return uomDao.findTotalFilteredUomForTenant(tenantId, tableParams, tenantType);
	}

	@Override
	public long findTotalActiveUomForTenant(String tenantId, TenantType tenantType) {
		return uomDao.findTotalActiveUomForTenant(tenantId, tenantType);
	}

	@Override
	public List<Uom> getAllActiveUomForTenant(String tenantId) {
		return uomDao.getAllActiveUomForTenant(tenantId, TenantType.BUYER);
	}

	@Override
	public Uom getUomByUomAndTenantId(String uom, String tenantId) {
		return uomDao.getUomByUomAndTenantId(uom, tenantId);
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.UOM_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public void uomDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) {
		// TODO Auto-generated method stub

		LOG.info("downloadProdCategoryListExcel method called");
		try {

			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "UOM.xlsx";
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
		XSSFSheet sheet = workbook.createSheet("UOM List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<Uom> uomList = uomDao.getAllUomForTenant(loggedInUserTenantId, TenantType.OWNER);

		int r = 1;
		if (CollectionUtil.isNotEmpty(uomList)) {
			for (Uom uom : uomList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(uom.getId());
				row.createCell(cellNum++).setCellValue(uom.getUom());
				row.createCell(cellNum++).setCellValue(uom.getUomDescription());
				row.createCell(cellNum++).setCellValue(uom.getStatus() != null ? uom.getStatus().toString() : "");
			}
		}
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}

		return workbook;
	}

	@Override
	public void uomExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String zipFileNames) {
		LOG.info("downloadProdCategoryListExcel method called");
		try {
			String fileName = "UOM.xlsx";
			XSSFWorkbook workbook = getExcelWorkBook(loggedInUserTenantId);
			FileUtil.writeXssfExcelToZip(zos, workbook, zipFileNames, fileName);
			LOG.info("Successfully written in Excel");

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	@Override
	public boolean isExists(String uom, String buyerId) {
		return uomDao.isExists(uom, buyerId);
	}

	@Override
	@Transactional(readOnly = false)
	public String createUom(Uom uom, Boolean actionByErp) {
		String auditRemarks = "'" + uom.getUom() + "' Uom created";

		if (Boolean.TRUE == actionByErp) {
			auditRemarks += " via ERP";
		}

		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, auditRemarks, uom.getCreatedBy().getTenantId(), uom.getCreatedBy(), new Date(), ModuleType.Uom);
		buyerAuditTrailDao.save(ownerAuditTrail);
		uom = uomDao.saveOrUpdate(uom);
		return (uom != null ? uom.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public Uom updateUom(Uom uom, Boolean actionByErp) {
		BuyerAuditTrail ownerAuditTrail = null;
		String auditRemarks = "'" + uom.getUom() + "' Uom updated";

		if (Boolean.TRUE == actionByErp) {
			if (Status.INACTIVE == uom.getStatus()) {
				auditRemarks += " to INACTIVE ";
			}
			auditRemarks += " via ERP";
		}
		LOG.info(uom == null);
		if (uom != null && uom.getModifiedBy() != null) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, auditRemarks, uom.getModifiedBy().getTenantId(), uom.getModifiedBy(), new Date(), ModuleType.Uom);
			buyerAuditTrailDao.save(ownerAuditTrail);
		}
		return uomDao.update(uom);
	}

	@Override
	public List<UomPojo> fetchAllActiveUomForTenant(String tenantId, String search) {
		List<UomPojo> list = uomDao.fetchAllActiveUomForTenant(tenantId, search);
		long count = uomDao.countConstructQueryToFetchUom(tenantId);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				UomPojo more = new UomPojo();
				more.setUom("+" + (count - list.size()) + " more. Continue typing to find match...");
				more.setUomDescription("");
				list.add(more);
			}
		}
		return list;
	}

	@Override
	public void downloadCsvFileForUom(HttpServletResponse response, File file, UomPojo uomPojo, String loggedInUserTenantId, TenantType tenantType) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.UOM_REPORT_CSV_COLUMNS;

			String[] columns = { "id", "uom", "uomDescription", "status" };

			long count = uomDao.findTotalUomCountForCsv(loggedInUserTenantId, tenantType);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<Uom> list = uomDao.getAllUomForCsv(loggedInUserTenantId, tenantType, PAGE_SIZE, pageNo);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (Uom pojo : list) {
					beanWriter.write(pojo, columns, processors);
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
				new Optional(),

		};
		return processors;
	}

}
