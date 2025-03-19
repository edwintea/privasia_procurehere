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
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.privasia.procurehere.core.dao.BuyerAddressDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerAddressService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class BuyerAddressServiceImpl implements BuyerAddressService {

	private static final Logger LOG = LogManager.getLogger(BuyerAddressService.class);

	@Autowired(required = true)
	BuyerAddressDao buyerAddressDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Autowired
	CountryDao countryDao;

	@Override
	@Transactional(readOnly = false)
	public String createBuyerAddress(BuyerAddress buyeraddress) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + buyeraddress.getTitle() + "' Buyer Address settings created", buyeraddress.getCreatedBy().getTenantId(), buyeraddress.getCreatedBy(), new Date(), ModuleType.BuyerAddress);
		buyerAuditTrailDao.save(ownerAuditTrail);
		buyeraddress = buyerAddressDao.saveOrUpdate(buyeraddress);
		return (buyeraddress != null ? buyeraddress.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBuyerAddress(BuyerAddress buyeraddress) {
		String buyerAdderess = buyeraddress.getTitle();
		LOG.info("BUyer Address Update----"+buyerAdderess);
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + buyeraddress.getTitle() + "' Buyer Address settings updated", buyeraddress.getModifiedBy().getTenantId(), buyeraddress.getModifiedBy(), new Date(), ModuleType.BuyerAddress);
		buyerAuditTrailDao.save(ownerAuditTrail);
		buyerAddressDao.update(buyeraddress);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBuyerAddress(BuyerAddress buyeraddress, User loggedInUser) {
		buyeraddress = buyerAddressDao.findById(buyeraddress.getId());
		String buyerAdderess = buyeraddress.getTitle();
		LOG.info("BUyer Address----"+buyerAdderess  + " --- " +loggedInUser.getTenantId());
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + buyerAdderess + "' Buyer Address setting deleted", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.BuyerAddress);
		buyerAuditTrailDao.save(ownerAuditTrail);
		buyerAddressDao.delete(buyeraddress);
		

	}

	@Override
	public List<BuyerAddressPojo> getAllBuyerAddressPojo(String id) {
		List<BuyerAddressPojo> returnList = new ArrayList<BuyerAddressPojo>();

		List<BuyerAddress> list = buyerAddressDao.findAllAddressesForTenant(id);

		if (CollectionUtil.isNotEmpty(list)) {
			for (BuyerAddress buyerAddress : list) {
				if (buyerAddress.getState() != null)
					buyerAddress.getState().getStateName();
				if (buyerAddress.getState().getCountry() != null)
					buyerAddress.getState().getCountry().getCountryName();
				if (buyerAddress.getCountry() != null) {
					buyerAddress.getCountry().getCountryName();
				}

				BuyerAddressPojo rep = new BuyerAddressPojo(buyerAddress);
				rep.setCountry(buyerAddress.getState().getCountry().getCountryName());
				returnList.add(rep);
			}
		}

		return returnList;

	}

	@Override
	public BuyerAddress getBuyerAddress(String id) {
		BuyerAddress buyerAddress = buyerAddressDao.findById(id);
		if (buyerAddress != null && buyerAddress.getState().getCountry() != null)
			buyerAddress.getState().getCountry().getCountryCode();

		return buyerAddress;
	}

	@Override
	public List<BuyerAddress> findBuyerAddressForTenant(String tenantId, TableDataInput tableParams) {
		return buyerAddressDao.findBuyerAddressForTenant(tenantId, tableParams);
		/*
		 * List<BuyerAddress> buyerAddress = buyerAddressDao.findBuyerAddressForTenant(tenantId, tableParams);
		 * List<BuyerAddress> returnList = null; if (CollectionUtil.isNotEmpty(buyerAddress)) { returnList = new
		 * ArrayList<BuyerAddress>(); for (BuyerAddress addr : buyerAddress) { if (addr.getState() != null) {
		 * addr.getState().getCountry().setCreatedBy(null); } returnList.add(addr); } } return returnList;
		 */
	}

	@Override
	public long findTotalFilteredBuyerAddressForTenant(String tenantId, TableDataInput tableParams) {
		return buyerAddressDao.findTotalFilteredBuyerAddressForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalBuyerAddressForTenant(String tenantId) {
		return buyerAddressDao.findTotalBuyerAddressForTenant(tenantId);
	}

	@Override
	public boolean isExists(BuyerAddress buyerAddress, String tenantId) {
		return buyerAddressDao.isExists(buyerAddress, tenantId);
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.BUYER_ADDRESS_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public void buyerAddressDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "BuyerAddress.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);
			workbook = getExcelWorkbook(loggedInUserTenantId);
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
		XSSFSheet sheet = workbook.createSheet("Buyer Address List");
		// Creating Headings
		buildHeader(workbook, sheet);

		List<BuyerAddress> buyerAddressList = buyerAddressDao.getBuyerAddressForTenant(loggedInUserTenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(buyerAddressList)) {
			for (BuyerAddress buyerAddress : buyerAddressList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(buyerAddress.getId());
				row.createCell(cellNum++).setCellValue(buyerAddress.getTitle());
				row.createCell(cellNum++).setCellValue(buyerAddress.getLine1());
				row.createCell(cellNum++).setCellValue(buyerAddress.getLine2());
				row.createCell(cellNum++).setCellValue(buyerAddress.getCity());
				row.createCell(cellNum++).setCellValue(buyerAddress.getState().getCountry().getCountryCode() + " - " + buyerAddress.getState().getCountry().getCountryName());
				row.createCell(cellNum++).setCellValue(buyerAddress.getState().getStateName());
				row.createCell(cellNum++).setCellValue(buyerAddress.getZip());
				row.createCell(cellNum++).setCellValue(buyerAddress.getStatus() != null ? buyerAddress.getStatus().toString() : "");
			}
		}
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	@Override
	public void buyerAddressExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "BuyerAddress.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = getExcelWorkbook(loggedInUserTenantId);
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

	@Override
	public void downloadBuyerAddressCsvFile(HttpServletResponse response, File file, String tenantId) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.BUYER_ADDRESS_EXCEL_COLUMNS;
			String[] columns = new String[] { "id", "title", "line1", "line2", "city", "country", "stateName", "zip", "status" };

			long count = buyerAddressDao.findBuyerAddressCountForTenant(tenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);
			CellProcessor[] processor = getProcessor();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<BuyerAddressPojo> list = findBuyerAddressListForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo);
				LOG.info("List ............. " + list.size() + "Count  ..........." + count);

				for (BuyerAddressPojo user : list) {
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

	private List<BuyerAddressPojo> findBuyerAddressListForTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		List<BuyerAddress> addList = buyerAddressDao.findBuyerAddressListForTenantIdForCsv(tenantId, pageSize, pageNo); 
		
		List<BuyerAddressPojo> pojoList =new ArrayList<>();
		for(BuyerAddress data: addList) {
			BuyerAddressPojo pojo = new BuyerAddressPojo();
			
			pojo.setId(data.getId());
			pojo.setTitle(data.getTitle());
			pojo.setLine1(data.getLine1());
			pojo.setLine2(data.getLine2());
			pojo.setCity(data.getCity());
			pojo.setCountry(data.getState().getCountry().getCountryCode() + " - " + data.getState().getCountry().getCountryName());
			pojo.setStateName(data.getState().getStateName());
			pojo.setZip(data.getZip());
			pojo.setStatus(data.getStatus().toString());
			pojoList.add(pojo);
		}
		
		return pojoList;
	}

	private CellProcessor[] getProcessor() {
		CellProcessor[] processor = new CellProcessor[] {
				 new NotNull(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()
		};
		return processor;
	}

}
