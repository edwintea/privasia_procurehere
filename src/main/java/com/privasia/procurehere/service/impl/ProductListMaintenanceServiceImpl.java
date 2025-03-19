package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;
import org.apache.logging.log4j.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
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

import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.parsers.ProductItemFileParser;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.ProductListMaintenanceService;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class ProductListMaintenanceServiceImpl implements ProductListMaintenanceService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	ProductListMaintenanceDao productListMaintenanceDao;

	@Autowired
	UomService uomService;

	@Autowired
	PrItemDao prItemDao;

	@Autowired
	UomDao uomDao;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	ProductCategoryMaintenanceDao productCategoryMaintenanceDao;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ServletContext context;

	@Override
	@Transactional(readOnly = false)
	public String createProductItem(ProductItem productListMaintenance, Boolean actionByErp) {

		String auditRemarks = "'" + productListMaintenance.getProductName() + "' Product Item created";

		if (Boolean.TRUE == actionByErp) {
			auditRemarks += " via ERP";
		}

		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, auditRemarks, productListMaintenance.getCreatedBy().getTenantId(), productListMaintenance.getCreatedBy(), new Date(), ModuleType.ProductItem);
		buyerAuditTrailDao.save(ownerAuditTrail);
		ProductItem productList = productListMaintenanceDao.saveOrUpdate(productListMaintenance);
		return productList.getId() != null ? productList.getId() : null;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateProductItem(ProductItem productListMaintenance, Boolean actionByErp) {

		String auditRemarks = "'" + productListMaintenance.getProductName() + "' Product Item updated";

		if (Boolean.TRUE == actionByErp) {
			if (Status.INACTIVE == productListMaintenance.getStatus()) {
				auditRemarks += " to INACTIVE ";
			}
			auditRemarks += " via ERP";
		}

		productListMaintenance.getModifiedBy().getName();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, auditRemarks, productListMaintenance.getModifiedBy().getTenantId(), productListMaintenance.getModifiedBy(), new Date(), ModuleType.ProductItem);
		buyerAuditTrailDao.save(ownerAuditTrail);
		productListMaintenanceDao.update(productListMaintenance);

	}

	@Override
	public ProductItem getProductCategoryById(String id) {
		LOG.info(id + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + id);
		ProductItem plm = productListMaintenanceDao.findById(id);
		if (plm != null && plm.getUom() != null)
			plm.getUom().getUom();

		if (plm != null && plm.getProductCategory() != null)
			plm.getProductCategory().getProductName();

		if (plm != null && plm.getFavoriteSupplier() != null)
			plm.getFavoriteSupplier().getSupplier().getCompanyName();

		return plm;
	}

	public ProductItem getProductItemByPrItemId(String id) {
		LOG.info(id + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + id);
		PrItem item = prItemDao.getPrItembyPrItemId(id);
		ProductItem plm = null;
		if(item.getProduct() != null) {
			 plm = productListMaintenanceDao.findById(item.getProduct().getId());
		}
		if (plm != null && plm.getUom() != null)
			plm.getUom().getUom();

		if (plm != null && plm.getProductCategory() != null)
			plm.getProductCategory().getProductName();

		if (plm != null && plm.getFavoriteSupplier() != null)
			plm.getFavoriteSupplier().getSupplier().getCompanyName();

		return plm;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProductCategory(ProductItem productListMaintenance) {
		BuyerAuditTrail ownerAuditTrail = null;
		if (productListMaintenance.getModifiedBy() != null) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + productListMaintenance.getProductName() + "' Product Item settings deleted ", productListMaintenance.getModifiedBy().getTenantId(), productListMaintenance.getModifiedBy(), new Date(), ModuleType.ProductItem);
		}
		buyerAuditTrailDao.save(ownerAuditTrail);
		LOG.info("productListMaintenance:" + productListMaintenance);
		productListMaintenanceDao.delete(productListMaintenance);
	}

	@Override
	public boolean isExists(String productCode, String buyerId, String productId) {
		return productListMaintenanceDao.isExists(productCode, buyerId, productId);
	}

	@Override
	public List<ProductItemPojo> findProductListForTenant(String tenantId, TableDataInput tableParams) {
		List<ProductItemPojo> list = productListMaintenanceDao.findProductListForTenant(tenantId, tableParams);
		// if (CollectionUtil.isNotEmpty(list)) {
		// returnList = new ArrayList<ProductItem>();
		// for (ProductItem item : list) {
		// returnList.add(item.createShallowCopy());
		// LOG.info("Cateogry : " + item.getProductCategory().getProductName());
		// }
		// }
		return list;
	}

	@Override
	public long findTotalFilteredProductListForTenant(String tenantId, TableDataInput tableParams) {
		return productListMaintenanceDao.findTotalFilteredProductListForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalProductListForTenant(String tenantId) {
		return productListMaintenanceDao.findTotalProductListForTenant(tenantId);
	}

	@Override
	public List<ProductItem> findProductListForByProductCategoryId(String productCategoryId) {
		List<ProductItem> list = productListMaintenanceDao.findProductListForByProductCategoryId(productCategoryId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (ProductItem item : list) {
				item.createShallowCopy();
				LOG.info("UOM : " + item.getUom().getCreatedBy());
			}
		}
		return list;
	}

	@Override
	public ProductItem getProductCategoryForPrById(String id) {
		ProductItem plm = productListMaintenanceDao.findProductCategoryById(id);
		return plm != null ? plm.createShallowCopy() : null;
	}

	@Override
	public List<ProductItem> findProductsByNameAndTenantAndFavSupplier(String productItem, String tenantId, String supplierId) {
		return productListMaintenanceDao.findProductsByNameAndTenantAndFavSupplier(productItem, tenantId, supplierId);
	}

	@Override
	public List<ProductItem> findProductsByTenantWithOutFavSupplier(String tenantId) {
		return productListMaintenanceDao.findProductsByTenantWithOutFavSupplier(tenantId);
	}

	@Override
	public ProductItem findProductItemByCode(String productCode, String tenantId, ProductItemType productItemType) {
		return productListMaintenanceDao.findProductItemByCode(productCode, tenantId, productItemType);
	}

	@Override
	public ProductItem getProductCategoryForDownloadAttachmentById(String id) {
		ProductItem plm = productListMaintenanceDao.findProductCategoryById(id);
		return plm != null ? plm.createShallowCopyForAttachment() : null;
	}

	public List<ProductItemPojo> getAllProductItemsByTenantIdForDownloadExcel(String tenantId) {
		return productListMaintenanceDao.getAllProductItemsByTenantIdForDownloadExcel(tenantId);
	}

	@Override
	public List<ProductItemPojo> getAllProductItemsByTenantIdAndIdsForDownloadExcel(String tenantId, List<String> ids) {
		return productListMaintenanceDao.getAllProductItemsByTenantIdAndIdsForDownloadExcel(tenantId, ids);
	}

	@Override
	public List<ProductItem> getAllProductItemsByTenantId(String tenantId) {
		return productListMaintenanceDao.getAllProductItemsByTenantId(tenantId);
	}

	@Override
	public void productListDownload(HttpServletResponse response, String tenantId, Integer type) throws FileNotFoundException, IOException {
		LOG.info("downloadProductList method called");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String downloadFolder = context.getRealPath("/WEB-INF/");
		String fileName = "ProductList.xlsx";
		Path file = Paths.get(downloadFolder, fileName);
		LOG.info("File Path ::" + file);

		XSSFSheet sheet = workbook.createSheet("Product List");
		// Creating Headings
		buildHeaderForProductList(workbook, sheet, type);
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue("1.0");
		row.createCell(1).setCellValue("Section Name");
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 6));

		List<ProductItemPojo> productItemList = getAllProductItemsByTenantIdForDownloadExcel(tenantId);
		int r = 2;
		if (CollectionUtil.isNotEmpty(productItemList)) {
			for (ProductItemPojo productItem : productItemList) {
				row = sheet.createRow(r);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue("1." + (r - 1));
				row.createCell(cellNum++).setCellValue(productItem.getItemName() != null ? productItem.getItemName() : "");
				row.createCell(cellNum++).setCellValue(productItem.getItemDescription() != null ? productItem.getItemDescription() : "");
				row.createCell(cellNum++).setCellValue(productItem.getUom() != null ? productItem.getUom() : "");
				row.createCell(cellNum++);
				row.createCell(cellNum++);
				row.createCell(cellNum++).setCellValue(PricingTypes.NORMAL_PRICE != null ? PricingTypes.NORMAL_PRICE.getValue() : "");
				row.createCell(cellNum++);
				row.createCell(cellNum++).setCellValue(productItem.getItemCode() != null ? productItem.getItemCode() : "");
				row.createCell(cellNum++).setCellValue(productItem.getItemCategory() != null ? productItem.getItemCategory() : "");
				row.createCell(cellNum++).setCellValue(productItem.getItemType() != null ? productItem.getItemType().toString() : "");
				r++;
			}
		}
		buildDropDownsForProductList(tenantId, workbook, sheet);

		for (int k = 0; k < 8; k++) {
			sheet.autoSizeColumn(k, true);
		}

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
	}

	@Override
	public HttpServletResponse productListDownload(HttpServletResponse response, String tenantId, List<String> ids, Integer type) throws FileNotFoundException, IOException {
		LOG.info("downloadProductList method called");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String downloadFolder = context.getRealPath("/WEB-INF/");
		String fileName = "ProductList.xlsx";
		Path file = Paths.get(downloadFolder, fileName);
		LOG.info("File Path ::" + file);

		XSSFSheet sheet = workbook.createSheet("Product List");
		// Creating Headings
		buildHeaderForProductList(workbook, sheet, type);
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue("1.0");
		row.createCell(1).setCellValue("Section Name");
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 6));

		List<ProductItemPojo> productItemList = getAllProductItemsByTenantIdAndIdsForDownloadExcel(tenantId, ids);
		int r = 2;
		if (CollectionUtil.isNotEmpty(productItemList)) {
			LOG.info("productItemList : " + productItemList.size());
			for (ProductItemPojo productItem : productItemList) {
				row = sheet.createRow(r);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue("1." + (r - 1));
				row.createCell(cellNum++).setCellValue(productItem.getItemName() != null ? productItem.getItemName() : "");
				row.createCell(cellNum++).setCellValue(productItem.getItemDescription() != null ? productItem.getItemDescription() : "");
				row.createCell(cellNum++).setCellValue(productItem.getUom() != null ? productItem.getUom() : "");
				row.createCell(cellNum++);
				row.createCell(cellNum++);
				row.createCell(cellNum++).setCellValue(PricingTypes.NORMAL_PRICE != null ? PricingTypes.NORMAL_PRICE.getValue() : "");
				if (type != 1) {
					row.createCell(cellNum++);
					row.createCell(cellNum++).setCellValue(productItem.getItemCode() != null ? productItem.getItemCode() : "");
					row.createCell(cellNum++).setCellValue(productItem.getItemCategory() != null ? productItem.getItemCategory() : "");
					row.createCell(cellNum++).setCellValue(productItem.getItemType() != null ? productItem.getItemType().toString() : "");
				}
				r++;
			}
		}
		buildDropDownsForProductList(tenantId, workbook, sheet);

		for (int k = 0; k < 8; k++) {
			sheet.autoSizeColumn(k, true);
		}

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
		return response;
	}

	public void productDownloadTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException {
		LOG.info("downloadproductTemplate method called");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String downloadFolder = context.getRealPath("/WEB-INF/");
		String fileName = "ProductTemplate.xlsx";
		Path file = Paths.get(downloadFolder, fileName);
		LOG.info("File Path ::" + file);

		XSSFSheet sheet = workbook.createSheet("Product List");
		// Creating Headings
		buildHeader(workbook, sheet);

		/* String tenantId = SecurityLibrary.getLoggedInUserTenantId(); */
//		List<ProductItem> productItemList = getAllProductItemsByTenantId(tenantId);
		List<ProductItemPojo> piList = productListMaintenanceDao.getAllProductItemsForDownloadByTenantId(tenantId);
		int r = 1;
		if (CollectionUtil.isNotEmpty(piList)) {
			for (ProductItemPojo productItem : piList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(productItem.getInterfaceCode());
				// row.createCell(cellNum++).setCellValue(productItem.getProductCode());
				row.createCell(cellNum++).setCellValue(productItem.getItemName());
				row.createCell(cellNum++).setCellValue(productItem.getUom());
				row.createCell(cellNum++).setCellValue(productItem.getItemCategory());

//				if (productItem.getProductCategory() != null && StringUtils.checkString(productItem.getProductCategory().getProductCode()).length() > 0) {
//					row.createCell(cellNum++).setCellValue(productItem.getProductCode());
//				}
//				if (productItem.getFavoriteSupplier() != null && productItem.getFavoriteSupplier().getSupplier() != null && StringUtils.checkString(productItem.getFavoriteSupplier().getSupplier().getCompanyName()).length() > 0) {
//					row.createCell(cellNum++).setCellValue(productItem.getFavoriteSupplier().getSupplier().getCompanyName());
//				} else {
//					row.createCell(cellNum++).setCellValue("");
//				}
				row.createCell(cellNum++).setCellValue(productItem.getFavoriteSupplier());

				row.createCell(cellNum++).setCellValue(productItem.getUnitPrice() != null ? productItem.getUnitPrice().toString() : "");
				row.createCell(cellNum++).setCellValue(productItem.getTax() != null ? productItem.getTax().toString() : "");
//				row.createCell(cellNum++).setCellValue(productItem.getProductItemType() != null ? productItem.getProductItemType().toString() : "");
				row.createCell(cellNum++).setCellValue(productItem.getItemType() != null ? productItem.getItemType().toString() : "");
				row.createCell(cellNum++).setCellValue(productItem.getGlCode());
				row.createCell(cellNum++).setCellValue(productItem.getUnspscCode());
				row.createCell(cellNum++).setCellValue(productItem.getStatus() != null ? productItem.getStatus().toString() : "");
				row.createCell(cellNum++).setCellValue(productItem.getRemarks());
				row.createCell(cellNum++).setCellValue(productItem.getHistoricPricingRefNo() != null ? productItem.getHistoricPricingRefNo() : "");
				row.createCell(cellNum++).setCellValue(productItem.getPurchaseGroupCode() != null ? productItem.getPurchaseGroupCode() : "");
				row.createCell(cellNum++).setCellValue(productItem.getBrand() != null ? productItem.getBrand() : "");

				row.createCell(cellNum++).setCellValue(productItem.getStartDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(productItem.getStartDate()) : "");
				row.createCell(cellNum++).setCellValue(productItem.getValidityDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(productItem.getValidityDate()) : "");
				row.createCell(cellNum++).setCellValue(productItem.getContractReferenceNumber() != null ? productItem.getContractReferenceNumber() : "");

			}
		}
		buildDropDowns(tenantId, workbook, sheet);

		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}

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
	}

	public void buildDropDownsForProductList(String tenantId, XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFSheet lookupSheet1 = workbook.createSheet("LOOKUP1");
		List<UomPojo> uom = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		int index1 = 0;
		if (CollectionUtil.isNotEmpty(uom)) {
			for (UomPojo u : uom) {
				String uomId = u.getId();
				String uomName = u.getUom();
				// String uomDescription = u.getUomDescription();
				LOG.info("UOM NAME :: " + uomName);
				XSSFRow row = lookupSheet1.createRow(index1++);
				XSSFCell cell1 = row.createCell(0);
				cell1.setCellValue(uomId);
				XSSFCell cell2 = row.createCell(1);
				cell2.setCellValue(uomName);
			}
		}
		// UOM
		XSSFDataValidationHelper validationHelper1 = new XSSFDataValidationHelper(lookupSheet1);
		XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) validationHelper1.createFormulaListConstraint("'LOOKUP1'!$B$1:$B$" + (uom.size()));
		CellRangeAddressList addressList1 = new CellRangeAddressList(1, 1000, 3, 3);

		XSSFDataValidation validation1 = (XSSFDataValidation) validationHelper1.createValidation(constraint1, addressList1);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation1.setSuppressDropDownArrow(true);
		validation1.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation1.createErrorBox("Invalid UOM Selected", "Please select UOM from the list");
		validation1.createPromptBox("UOM List", "Select UOM from the list provided.");
		validation1.setShowPromptBox(true);
		validation1.setShowErrorBox(true);
		sheet.addValidationData(validation1);
		workbook.setSheetHidden(1, true);

		// Price_Type
		XSSFSheet lookupSheet2 = workbook.createSheet("LOOKUP2");
		PricingTypes[] pta = PricingTypes.values();
		int index3 = 0;
		for (PricingTypes pt : pta) {
			XSSFRow row = lookupSheet2.createRow(index3++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(pt.getValue());
		}
		// Price_Type
		XSSFDataValidationHelper validationHelper2 = new XSSFDataValidationHelper(lookupSheet2);
		XSSFDataValidationConstraint constraint2 = (XSSFDataValidationConstraint) validationHelper2.createFormulaListConstraint("'LOOKUP2'!$A$1:$A$" + (pta.length));
		CellRangeAddressList addressList2 = new CellRangeAddressList(1, 1000, 6, 6);

		XSSFDataValidation validation2 = (XSSFDataValidation) validationHelper2.createValidation(constraint2, addressList2);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation2.setSuppressDropDownArrow(true);
		validation2.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation2.createErrorBox("Invalid PRICE TYPE Selected", "Please select PRICE TYPE from the list");
		validation2.createPromptBox("PRICE TYPE List", "Select PRICE TYPE from the list provided.");
		validation2.setShowPromptBox(true);
		validation2.setShowErrorBox(true);
		sheet.addValidationData(validation2);
		workbook.setSheetHidden(2, true);

	}

	/**
	 * @param tenantId
	 * @param workbook
	 * @param sheet
	 */
	public void buildDropDowns(String tenantId, XSSFWorkbook workbook, XSSFSheet sheet) {
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
		CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 10, 10);
		XSSFDataValidation validation = (XSSFDataValidation) validationHelper.createValidation(constraint, addressList);
		validation.setSuppressDropDownArrow(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Invalid  STATUS Selected", "Please select STATUS from the list");
		validation.createPromptBox("STATUS List", "Select STATUS from the list provided. It has been exported from your master data.");
		validation.setShowPromptBox(true);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
		workbook.setSheetHidden(1, true);

		//ProductItemType
		XSSFSheet lookupSheet5 = workbook.createSheet("LOOKUP2");
		int index5 = 0;
		ProductItemType[] typeArr = ProductItemType.values();
		for (ProductItemType status : typeArr) {
			XSSFRow firstRow = lookupSheet5.createRow(index5++);
			XSSFCell cell1 = firstRow.createCell(0);
			cell1.setCellValue(status.toString());
		}

		XSSFDataValidationHelper validationHelper5 = new XSSFDataValidationHelper(lookupSheet5);
		XSSFDataValidationConstraint constraint5 = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP2'!$A$1:$A$" + (statsArr.length + 1));
		CellRangeAddressList addressList5 = new CellRangeAddressList(1, 1000, 7, 7);
		XSSFDataValidation validation5 = (XSSFDataValidation) validationHelper5.createValidation(constraint5, addressList5);
		validation.setSuppressDropDownArrow(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Invalid  Type Selected", "Please select Type from the list");
		validation.createPromptBox("Item Type List", "Select Item Type from the list provided. It has been exported from your master data.");
		validation.setShowPromptBox(true);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation5);
		workbook.setSheetHidden(2, true);

		/**
		// UOM
		XSSFSheet lookupSheet2 = workbook.createSheet("LOOKUP2");
		List<UomPojo> uom = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		int index1 = 0;
		if (CollectionUtil.isNotEmpty(uom)) {
			for (UomPojo u : uom) {
				String uomId = u.getId();
				String uomName = u.getUom();
				// String uomDescription = u.getUomDescription();
				LOG.info("UOM NAME :: " + uomName);
				XSSFRow row = lookupSheet2.createRow(index1++);
				XSSFCell cell1 = row.createCell(0);
				cell1.setCellValue(uomId);
				XSSFCell cell2 = row.createCell(1);
				cell2.setCellValue(uomName);
			}
		}
		XSSFDataValidationHelper validationHelper1 = new XSSFDataValidationHelper(lookupSheet2);
		XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) validationHelper1.createFormulaListConstraint("'LOOKUP2'!$B$1:$B$" + (uom.size() + 1));
		CellRangeAddressList addressList1 = new CellRangeAddressList(1, 1000, 2, 2);

		XSSFDataValidation validation1 = (XSSFDataValidation) validationHelper1.createValidation(constraint1, addressList1);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation1.setSuppressDropDownArrow(true);
		validation1.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation1.createErrorBox("Invalid UOM Selected", "Please select UOM from the list");
		validation1.createPromptBox("UOM List", "Select UOM from the list provided. It has been exported from your master data.");
		validation1.setShowPromptBox(true);
		validation1.setShowErrorBox(true);
		sheet.addValidationData(validation1);
		workbook.setSheetHidden(2, true);

		// Product Category
		XSSFSheet lookupSheet3 = workbook.createSheet("LOOKUP3");
		List<ProductCategory> productCategory = productCategoryMaintenanceService.findAllActiveProductCategoryForTenant(tenantId);
		int index3 = 0;
		if (CollectionUtil.isNotEmpty(productCategory)) {
			for (ProductCategory pc : productCategory) {
				String pcId = pc.getId();
				// String pcName = pc.getProductName();

				// LOG.info("Product NAME :: " + pcName);
				XSSFRow row = lookupSheet3.createRow(index3++);
				XSSFCell cell1 = row.createCell(0);
				cell1.setCellValue(pcId);
				XSSFCell cell2 = row.createCell(1);
				cell2.setCellValue(pc.getProductCode());
			}
		}
		
		XSSFDataValidationHelper validationHelper2 = new XSSFDataValidationHelper(lookupSheet3);
		XSSFDataValidationConstraint constraint2 = (XSSFDataValidationConstraint) validationHelper2.createFormulaListConstraint("'LOOKUP3'!$B$1:$B$" + (productCategory.size() + 1));
		CellRangeAddressList addressList2 = new CellRangeAddressList(1, 1000, 3, 3);

		XSSFDataValidation validation2 = (XSSFDataValidation) validationHelper2.createValidation(constraint2, addressList2);
		validation2.setSuppressDropDownArrow(true);
		validation2.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation2.createErrorBox("Invalid Product Category Selected", "Please select Product Category from the list");
		validation2.createPromptBox("Product Category List", "Select Product Category from the list provided. It has been exported from your master data.");
		validation2.setShowPromptBox(true);
		validation2.setShowErrorBox(true);
		sheet.addValidationData(validation2);
		workbook.setSheetHidden(3, true);

		// Favourite Supplier
		XSSFSheet lookupSheet4 = workbook.createSheet("LOOKUP4");
		List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.getAllActiveFavouriteSupplierByTenantId(tenantId);
		int index4 = 0;
		if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
			LOG.info("......." + favouriteSupplier.size());
			String fsName = "";

			for (FavouriteSupplier fs : favouriteSupplier) {
				if (fs.getSupplier() != null) {
					XSSFRow row = lookupSheet4.createRow(index4++);
					XSSFCell cell1 = row.createCell(0);
					XSSFCell cell2 = row.createCell(1);
					String fsId = fs.getId();
					fsName = fs.getSupplier().getCompanyName();

					LOG.info("Product NAME :: " + fsName);
					cell1 = row.createCell(0);
					cell1.setCellValue(fsId);
					cell2 = row.createCell(1);
					cell2.setCellValue(fsName);

				}
			}
		}
		XSSFDataValidationHelper validationHelper3 = new XSSFDataValidationHelper(lookupSheet4);
		XSSFDataValidationConstraint constraint3 = (XSSFDataValidationConstraint) validationHelper3.createFormulaListConstraint("'LOOKUP4'!$B$1:$B$" + (favouriteSupplier.size() + 2));
		CellRangeAddressList addressList3 = new CellRangeAddressList(1, 1000, 4, 4);

		XSSFDataValidation validation3 = (XSSFDataValidation) validationHelper3.createValidation(constraint3, addressList3);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation3.setSuppressDropDownArrow(true);
		validation3.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation3.createErrorBox("Invalid Supplier Selected", "Please select Supplier from the list");
		validation3.createPromptBox("Supplier List", "Select Supplier from the list provided. It has been exported from your master data.");
		validation3.setShowPromptBox(true);
		validation3.setShowErrorBox(true);
		sheet.addValidationData(validation3);
		workbook.setSheetHidden(4, true);
		
		*/

	}

	private void buildHeaderForProductList(XSSFWorkbook workbook, XSSFSheet sheet, Integer type) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : ((type == null || (type != null && type == 1)) ? Global.BQ_EXCEL_COLUMNS_TYPE_1 : Global.BQ_EXCEL_COLUMNS_TYPE_2)) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PRODUCT_ITEM_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void productItemUploadFile(MultipartFile file, String tenantId, User loggedInUser) throws Exception {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);

		ProductItemFileParser<ProductItem> ccParser = new ProductItemFileParser<ProductItem>(ProductItem.class);
		List<ProductItem> productItemList = ccParser.parse(convFile);

		if (CollectionUtil.isNotEmpty(productItemList)) {
			int row = 2;
			List<String> productNameList = new ArrayList<>();
			for (ProductItem productItem : productItemList) {
				// For validation
				if (StringUtils.checkString(productItem.getPurchaseGroupCode()).length() > 0) {
					productItem.setProductCode(productItem.getInterfaceCode() + productItem.getPurchaseGroupCode());
				} else {
					productItem.setProductCode(productItem.getInterfaceCode());
				}
				List<String> errorList = validateProductItem(productItem, ProductItem.ProductItemInt.class);
				if (CollectionUtil.isNotEmpty(errorList)) {
					throw new ApplicationException(errorList.toString() + " at row number \"" + row + "\"");
				}
				if (productNameList.contains(productItem.getProductCode().toUpperCase())) {
					throw new ApplicationException("Duplicate Product code \'" + productItem.getProductCode() + "\' in excel at row number \"" + row + "\"");
				}
				ProductItem dbProduct = productListMaintenanceDao.getByProductCode(productItem.getProductCode(), tenantId);
				if (dbProduct != null) {
					LOG.info("old Product code :" + productItem.toLogString());
					dbProduct.setProductName(productItem.getProductName());
					dbProduct.setStatus(productItem.getStatus());
					dbProduct.setTax(productItem.getTax());
					dbProduct.setUnitPrice(productItem.getUnitPrice());
					dbProduct.setRemarks(productItem.getRemarks());
					dbProduct.setGlCode(productItem.getGlCode());
					dbProduct.setUnspscCode(productItem.getUnspscCode());
					if (productItem.getUom() != null && StringUtils.checkString(productItem.getUom().getUom()).length() > 0) {
						// LOG.info("dbProduct.getUom().getUom() " +dbProduct.getUom().getUom()+ "===
						// productItem.getUom().getUom() "+ productItem.getUom().getUom());
						if (dbProduct.getUom() != null && !dbProduct.getUom().getUom().equalsIgnoreCase(productItem.getUom().getUom())) {
							/* Uom uom = uomService.getUombyCode(productItem.getUom().getUom()); */
							Uom uom = uomService.getUomByUomAndTenantId(productItem.getUom().getUom(), tenantId);
							// LOG.info("uom :" + uom.getUom());
							if (uom == null) {
								throw new ApplicationException("Please select uom from drop down list at row number \"" + row + "\"");
							}
							dbProduct.setUom(uom);
						}
					}
					if (productItem.getProductCategory() != null && StringUtils.checkString(productItem.getProductCategory().getProductCode()).length() > 0) {
						// LOG.info("dbProduct.getProductCategory().getProductCode() :"+
						// dbProduct.getProductCategory().getProductCode()+"=====productItem.getProductCategory().getProductCode()
						// :" + productItem.getProductCategory().getProductCode());
						if (dbProduct.getProductCategory() != null && !dbProduct.getProductCategory().getProductCode().equalsIgnoreCase(productItem.getProductCategory().getProductCode())) {
							ProductCategory productCategory = productCategoryMaintenanceDao.getByProductCategoryByCode(productItem.getProductCategory().getProductCode(), tenantId);
							// LOG.info("product name :" + productCategory.getProductName());
							if (productCategory == null) {
								throw new ApplicationException("Please select product category from drop down list at row number \"" + row + "\"");
							}
							dbProduct.setProductCategory(productCategory);
						}
					}
					if (productItem.getFavoriteSupplier() != null && StringUtils.checkString(productItem.getFavoriteSupplier().getSupplier().getCompanyName()).length() > 0) {
						// LOG.info("fav supp company name :" +
						// productItem.getFavoriteSupplier().getSupplier().getCompanyName());
						FavouriteSupplier favoriteSupplier = favoriteSupplierDao.getFavouriteSupplierByCompanyName(tenantId, productItem.getFavoriteSupplier().getSupplier().getCompanyName());
						// LOG.info("favoriteSupplier company name :" + favoriteSupplier.getFullName());
						if (favoriteSupplier == null) {
							throw new ApplicationException("Please select supplier from drop down list at row number \"" + row + "\"");
						}
						dbProduct.setFavoriteSupplier(favoriteSupplier);
					} else {
						dbProduct.setFavoriteSupplier(null);
					}

					dbProduct.setHistoricPricingRefNo(productItem.getHistoricPricingRefNo());
					dbProduct.setPurchaseGroupCode(productItem.getPurchaseGroupCode());
					dbProduct.setBrand(productItem.getBrand());
					dbProduct.setModifiedBy(loggedInUser);
					dbProduct.setModifiedDate(new Date());
					// PH-331
					if (productItem.getStartDate() == null || productItem.getValidityDate() == null) {
						if (!(productItem.getStartDate() == null && productItem.getValidityDate() == null)) {
							throw new ApplicationException("Please select Start and End date for contract validity");
						}
					}
					dbProduct.setStartDate(productItem.getStartDate());
					dbProduct.setValidityDate(productItem.getValidityDate());
					dbProduct.setContractReferenceNumber(productItem.getContractReferenceNumber());
					dbProduct.setProductItemType(productItem.getProductItemType());
					productListMaintenanceDao.saveOrUpdate(dbProduct);
				} else {
					LOG.info("new Product code :" + productItem.toLogString());
					productItem.setBuyer(loggedInUser.getBuyer());
					productItem.setCreatedBy(loggedInUser);
					productItem.setCreatedDate(new Date());

					if (productItem.getStartDate() == null || productItem.getValidityDate() == null) {
						if (!(productItem.getStartDate() == null && productItem.getValidityDate() == null)) {
							throw new ApplicationException("Please select Start and End date for contract validity");
						}
					}

					if (productItem.getUom() != null && StringUtils.checkString(productItem.getUom().getUom()).length() > 0) {
						// Uom uom = uomService.getUombyCode(productItem.getUom().getUom());
						Uom uom = uomService.getUomByUomAndTenantId(productItem.getUom().getUom(), tenantId);
						// LOG.info("uom" + productItem.getUom().getUom());
						if (uom == null) {
							throw new ApplicationException("Please select uom from drop down list at row number \"" + row + "\"");
						}
						productItem.setUom(uom);
					}
					if (productItem.getProductCategory() != null && StringUtils.checkString(productItem.getProductCategory().getProductCode()).length() > 0) {
						LOG.info("========>>>>>" + productItem.getProductCategory().getProductName());
						ProductCategory productCategory = productCategoryMaintenanceDao.getByProductCategoryByCode(productItem.getProductCategory().getProductCode(), tenantId);
						// LOG.info("productCategory :" + productItem.getProductCategory().getProductName());
						if (productCategory == null) {
							throw new ApplicationException("Please select product category from drop down list at row number \"" + row + "\"");
						}
						productItem.setProductCategory(productCategory);
					}
					if (productItem.getFavoriteSupplier() != null && StringUtils.checkString(productItem.getFavoriteSupplier().getSupplier().getCompanyName()).length() > 0) {
						// LOG.info("fav supp company name :" +
						// productItem.getFavoriteSupplier().getSupplier().getCompanyName());
						FavouriteSupplier favoriteSupplier = favoriteSupplierDao.getFavouriteSupplierByCompanyName(tenantId, productItem.getFavoriteSupplier().getSupplier().getCompanyName());
						// LOG.info("favoriteSupplier company name :" + favoriteSupplier.getFullName());
						if (favoriteSupplier == null) {
							throw new ApplicationException("Please select supplier from drop down list at row number \"" + row + "\"");
						}
						productItem.setFavoriteSupplier(favoriteSupplier);
					}

					productListMaintenanceDao.save(productItem);
				}
				row++;
				productNameList.add(productItem.getProductCode().toUpperCase());
			}
		}
	}

	/**
	 * @param pi
	 */
	public List<String> validateProductItem(ProductItem pi, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<ProductItem>> constraintViolations = validator.validate(pi, validations);
		for (ConstraintViolation<ProductItem> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@Override
	public List<ProductItem> findProductsByTenantId(String loggedInUserTenantId) {
		return productListMaintenanceDao.findProductsByTenantId(loggedInUserTenantId);
	}

	@Override
	public ProductItem findProductItembyId(String itemId) {
		ProductItem item = productListMaintenanceDao.findById(itemId);
		if (item.getFavoriteSupplier() != null) {
			item.getFavoriteSupplier().getFullName();
			item.getFavoriteSupplier().getSupplier().getCompanyName();
		}
		if (item.getProductCategory() != null)
			item.getProductCategory().getProductCode();
		if (item.getUom() != null)
			item.getUom().getUom();

		return item;

	}

	@Override
	public boolean isExistsproductCode(String productCode, String buyerId) {
		return productListMaintenanceDao.isExistsproductCode(productCode, buyerId);
	}
	
	@Override
	public void downloadCsvFileForProductList(HttpServletResponse response, File file, String tenantId) {

		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.PRODUCT_ITEM_CSV_COLUMN_HEADINGS;

			final String[] columns = new String[] { "interfaceCode", "itemName", "uom", "itemCategory", "favoriteSupplier", "unitPrice", "tax", "itemType", "glCode", "unspscCode", "status", "remarks", "historicPricingRefNo", "purchaseGroupCode", "brand", "startDate", "validityDate", "contractReferenceNumber" };

			long count = productListMaintenanceDao.findTotalProductItems(tenantId);

			int PAGE_SIZE = 25000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<ProductItemPojo> list = productListMaintenanceDao.findAllActiveProductItemForTenantId(tenantId, Status.ACTIVE, PAGE_SIZE, pageNo);
				LOG.info("size ........" + list.size() + ".... count " + count);
				for (ProductItemPojo pojo : list) {
					beanWriter.write(pojo, columns, processors);
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
				new Optional(), new NotNull(), new Optional(), new NotNull(), // Product Category
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // Gl code
				new Optional(), new NotNull(), new Optional(), new Optional(), new Optional(), // Purchase GroupCode
				new Optional(), new Optional(), new Optional(), new Optional(),

				// new NotNull(), // Product Code
		};
		return processors;
	}

	@Override
	public void downloadProductItemTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException {

		LOG.info("download productTemplate method called");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String downloadFolder = context.getRealPath("/WEB-INF/");
		String fileName = "ProductTemplate.xlsx";
		Path file = Paths.get(downloadFolder, fileName);
		LOG.info("File Path ::" + file);

		XSSFSheet sheet = workbook.createSheet("Product List");
		// Creating Headings
		buildHeader(workbook, sheet);

		buildDropDownsForTemplate(tenantId, workbook, sheet);

		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}

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
	
		
	}

	private void buildDropDownsForTemplate(String tenantId, XSSFWorkbook workbook, XSSFSheet sheet) {
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
		CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 10, 10);
		XSSFDataValidation validation = (XSSFDataValidation) validationHelper.createValidation(constraint, addressList);
		validation.setSuppressDropDownArrow(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Invalid  STATUS Selected", "Please select STATUS from the list");
		validation.createPromptBox("STATUS List", "Select STATUS from the list provided. It has been exported from your master data.");
		validation.setShowPromptBox(true);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
		workbook.setSheetHidden(1, true);

		//ProductItemType
		XSSFSheet lookupSheet5 = workbook.createSheet("LOOKUP2");
		int index5 = 0;
		ProductItemType[] typeArr = ProductItemType.values();
		for (ProductItemType product : typeArr) {
			XSSFRow firstRow = lookupSheet5.createRow(index5++);
			XSSFCell cell1 = firstRow.createCell(0);
			cell1.setCellValue(product.toString());
		}

		XSSFDataValidationHelper validationHelper5 = new XSSFDataValidationHelper(lookupSheet5);
		XSSFDataValidationConstraint constraint5 = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP2'!$A$1:$A$" + (typeArr.length + 1));
		CellRangeAddressList addressList5 = new CellRangeAddressList(1, 1000, 7, 7);
		XSSFDataValidation validation5 = (XSSFDataValidation) validationHelper5.createValidation(constraint5, addressList5);
		validation.setSuppressDropDownArrow(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Invalid  Type Selected", "Please select Type from the list");
		validation.createPromptBox("Item Type List", "Select Item Type from the list provided. It has been exported from your master data.");
		validation.setShowPromptBox(true);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation5);
		workbook.setSheetHidden(2, true);

		
		// UOM
		XSSFSheet lookupSheet2 = workbook.createSheet("LOOKUP3");
		List<UomPojo> uom = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		int index1 = 0;
		if (CollectionUtil.isNotEmpty(uom)) {
			for (UomPojo u : uom) {
				String uomId = u.getId();
				String uomName = u.getUom();
				// String uomDescription = u.getUomDescription();
				LOG.info("UOM NAME :: " + uomName);
				XSSFRow row = lookupSheet2.createRow(index1++);
				XSSFCell cell1 = row.createCell(0);
				cell1.setCellValue(uomId);
				XSSFCell cell2 = row.createCell(1);
				cell2.setCellValue(uomName);
			}
		}
		XSSFDataValidationHelper validationHelper1 = new XSSFDataValidationHelper(lookupSheet2);
		XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) validationHelper1.createFormulaListConstraint("'LOOKUP3'!$B$1:$B$" + (uom.size() + 1));
		CellRangeAddressList addressList1 = new CellRangeAddressList(1, 1000, 2, 2);

		XSSFDataValidation validation1 = (XSSFDataValidation) validationHelper1.createValidation(constraint1, addressList1);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation1.setSuppressDropDownArrow(true);
		validation1.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation1.createErrorBox("Invalid UOM Selected", "Please select UOM from the list");
		validation1.createPromptBox("UOM List", "Select UOM from the list provided. It has been exported from your master data.");
		validation1.setShowPromptBox(true);
		validation1.setShowErrorBox(true);
		sheet.addValidationData(validation1);
		workbook.setSheetHidden(2, true);

		// Product Category
		XSSFSheet lookupSheet3 = workbook.createSheet("LOOKUP4");
		List<ProductCategory> productCategory = productCategoryMaintenanceService.findAllActiveProductCategoryForTenant(tenantId);
		int index3 = 0;
		if (CollectionUtil.isNotEmpty(productCategory)) {
			for (ProductCategory pc : productCategory) {
				String pcId = pc.getId();
				// String pcName = pc.getProductName();

				// LOG.info("Product NAME :: " + pcName);
				XSSFRow row = lookupSheet3.createRow(index3++);
				XSSFCell cell1 = row.createCell(0);
				cell1.setCellValue(pcId);
				XSSFCell cell2 = row.createCell(1);
				cell2.setCellValue(pc.getProductCode());
			}
		}
		
		XSSFDataValidationHelper validationHelper2 = new XSSFDataValidationHelper(lookupSheet3);
		XSSFDataValidationConstraint constraint2 = (XSSFDataValidationConstraint) validationHelper2.createFormulaListConstraint("'LOOKUP4'!$B$1:$B$" + (productCategory.size() + 1));
		CellRangeAddressList addressList2 = new CellRangeAddressList(1, 1000, 3, 3);

		XSSFDataValidation validation2 = (XSSFDataValidation) validationHelper2.createValidation(constraint2, addressList2);
		validation2.setSuppressDropDownArrow(true);
		validation2.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation2.createErrorBox("Invalid Product Category Selected", "Please select Product Category from the list");
		validation2.createPromptBox("Product Category List", "Select Product Category from the list provided. It has been exported from your master data.");
		validation2.setShowPromptBox(true);
		validation2.setShowErrorBox(true);
		sheet.addValidationData(validation2);
		workbook.setSheetHidden(3, true);

		// Favourite Supplier
		XSSFSheet lookupSheet4 = workbook.createSheet("LOOKUP5");
		List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.getAllActiveFavouriteSupplierByTenantId(tenantId);
		int index4 = 0;
		if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
			LOG.info("......." + favouriteSupplier.size());
			String fsName = "";

			for (FavouriteSupplier fs : favouriteSupplier) {
				if (fs.getSupplier() != null) {
					XSSFRow row = lookupSheet4.createRow(index4++);
					XSSFCell cell1 = row.createCell(0);
					XSSFCell cell2 = row.createCell(1);
					String fsId = fs.getId();
					fsName = fs.getSupplier().getCompanyName();

					LOG.info("Product NAME :: " + fsName);
					cell1 = row.createCell(0);
					cell1.setCellValue(fsId);
					cell2 = row.createCell(1);
					cell2.setCellValue(fsName);

				}
			}
		}
		XSSFDataValidationHelper validationHelper3 = new XSSFDataValidationHelper(lookupSheet4);
		XSSFDataValidationConstraint constraint3 = (XSSFDataValidationConstraint) validationHelper3.createFormulaListConstraint("'LOOKUP5'!$B$1:$B$" + (favouriteSupplier.size() + 2));
		CellRangeAddressList addressList3 = new CellRangeAddressList(1, 1000, 4, 4);

		XSSFDataValidation validation3 = (XSSFDataValidation) validationHelper3.createValidation(constraint3, addressList3);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation3.setSuppressDropDownArrow(true);
		validation3.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation3.createErrorBox("Invalid Supplier Selected", "Please select Supplier from the list");
		validation3.createPromptBox("Supplier List", "Select Supplier from the list provided. It has been exported from your master data.");
		validation3.setShowPromptBox(true);
		validation3.setShowErrorBox(true);
		sheet.addValidationData(validation3);
		workbook.setSheetHidden(4, true);

	}

	@Override
	public List<ProductItemPojo> fetchAllProductItemForTenant(String tenantId, String search) {
		List<ProductItemPojo> list = productListMaintenanceDao.fetchAllActiveProductItemNameAndCodeForTenant(tenantId, search);
		long count = productListMaintenanceDao.countConstructQueryToFetchProductItem(tenantId);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				ProductItemPojo more = new ProductItemPojo();
				more.setItemName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}
	
	@Override
	public List<ProductItemPojo> fetchAllActiveProductItemNameAndCodeForTenant(String tenantId, String search) {
		return productListMaintenanceDao.fetchAllActiveProductItemNameAndCodeForTenant(tenantId, search);
	}

}