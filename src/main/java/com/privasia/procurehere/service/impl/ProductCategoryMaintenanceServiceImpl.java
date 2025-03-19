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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ProductCategoryMaintenanceDao;
import com.privasia.procurehere.core.dao.ProductListMaintenanceDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.parsers.ProductCategoryFileParser;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;

@Service
@Transactional(readOnly = true)
public class ProductCategoryMaintenanceServiceImpl implements ProductCategoryMaintenanceService {

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	ProductCategoryMaintenanceDao productCategoryMaintenanceDao;

	@Autowired
	ProductCategoryMaintenanceDao productcategorydao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ProductListMaintenanceDao productListMaintenanceDao;

	@Autowired
	ServletContext context;

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Override
	@Transactional(readOnly = false)
	public String createProductCategory(ProductCategory productCategoryMaintenance) {
		BuyerAuditTrail ownerAuditTrail = null;
		if (StringUtils.checkString(productCategoryMaintenance.getId()).length() > 0) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + productCategoryMaintenance.getProductName() + "' Product Category settings updated", productCategoryMaintenance.getModifiedBy().getTenantId(), productCategoryMaintenance.getModifiedBy(), new Date(), ModuleType.ProductCategory);
		} else {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + productCategoryMaintenance.getProductName() + "' Product Category settings created", productCategoryMaintenance.getCreatedBy().getTenantId(), productCategoryMaintenance.getCreatedBy(), new Date(), ModuleType.ProductCategory);
		}
		buyerAuditTrailDao.save(ownerAuditTrail);

		productCategoryMaintenance = productCategoryMaintenanceDao.saveOrUpdate(productCategoryMaintenance);
		return (productCategoryMaintenance != null ? productCategoryMaintenance.getId() : null);
	}

	/**
	 * Shouldnt there be status clause?
	 */

	@SuppressWarnings("unused")
	@Override
	@Deprecated
	public List<ProductCategory> getAllProductCategory() {
		List<ProductCategory> returnList = null;
		List<ProductCategory> allProductCategory = productCategoryMaintenanceDao.findAll(ProductCategory.class);
		return allProductCategory;
	}

	@Override
	public ProductCategory getProductCategoryById(String id) {
		return productCategoryMaintenanceDao.findById(id);
	}

	@Override
	public ProductCategory getProductCategoryByCodeAndTenantId(String code, String tenantId) {
		return productCategoryMaintenanceDao.getByProductCategoryByCode(code, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProductCategory(ProductCategory productCategoryMaintenance, User loggedInUser) {
		String productName = productCategoryMaintenance.getProductName();
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + productName + "' Product Category settings deleted ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.ProductCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
		productCategoryMaintenanceDao.delete(productCategoryMaintenance);

	}

	@Override
	public boolean isExists(ProductCategory productCategoryMaintenance, String buyerId) {
		return productCategoryMaintenanceDao.isExists(productCategoryMaintenance, buyerId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateProductCategory(ProductCategory productCategoryMaintenance) {
		productCategoryMaintenanceDao.update(productCategoryMaintenance);
		LOG.info("productCategoryMaintenance Update ID---" + productCategoryMaintenance.getId().length());
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + productCategoryMaintenance.getProductName() + "' Product Category settings updated", productCategoryMaintenance.getModifiedBy().getTenantId(), productCategoryMaintenance.getModifiedBy(), new Date(), ModuleType.ProductCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
	}

	@Override
	public long findTotalFilteredProductCategoryForTenant(String tenantId, TableDataInput tableParams) {
		return productCategoryMaintenanceDao.findTotalFilteredProductCategoryForTenant(tenantId, tableParams);
	}

	@Override
	public List<ProductCategory> findProductCategoryForTenant(String tenantId, TableDataInput tableParams) {
		return productCategoryMaintenanceDao.findProductCategoryForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalProductCategoryForTenant(String tenantId) {
		return productCategoryMaintenanceDao.findTotalProductCategoryForTenant(tenantId);
	}

	@Override
	public List<ProductCategory> findAllActiveProductCategoryForTenant(String tenantId) {
		return productCategoryMaintenanceDao.findAllActiveProductCategoryForTenant(tenantId);
	}

	@Override
	public List<ProductCategory> getAllProductCategoryByTenantId(String tenantId) {
		List<ProductCategory> pcList = productCategoryMaintenanceDao.getAllProductCategoryByTenantId(tenantId);

		if (pcList != null) {
			for (ProductCategory bic : pcList) {
				bic.setCreatedBy(null);
				bic.setModifiedBy(null);
				// LOG.info("Matching Buyer Product Category : " +
				// bic.getCode() + " - " + bic.getName());
			}
		}

		return pcList;

	}

	@Override
	public void productCategoryDownloadTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException {
		LOG.info("downloadProdCategoryListExcel method called");
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ProductCategoryTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			XSSFSheet sheet = workbook.createSheet("Product Category List");
			// Creating Headings
			buildHeader(workbook, sheet);

			List<ProductCategory> productCategoryList = getAllProductCategoryByTenantId(tenantId);

			int r = 1;
			if (CollectionUtil.isNotEmpty(productCategoryList)) {
				for (ProductCategory productCategory : productCategoryList) {
					Row row = sheet.createRow(r++);
					int cellNum = 0;
					row.createCell(cellNum++).setCellValue(productCategory.getProductCode());
					row.createCell(cellNum++).setCellValue(productCategory.getProductName());
					row.createCell(cellNum++).setCellValue(productCategory.getStatus() != null ? productCategory.getStatus().toString() : "");
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

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PRODUCT_CATEGORY_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void productCategoryUploadFile(MultipartFile file, String tenantId, User loggedInUser) throws Exception {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);

		ProductCategoryFileParser<ProductCategory> pcParser = new ProductCategoryFileParser<ProductCategory>(ProductCategory.class);
		List<ProductCategory> ProductCategoryList = pcParser.parse(convFile);

		if (CollectionUtil.isNotEmpty(ProductCategoryList)) {
			int row = 2;
			List<String> ProductCategoryNameList = new ArrayList<>();
			for (ProductCategory productCategory : ProductCategoryList) {
				// For validation
				List<String> errorList = validateProductCategory(productCategory, ProductCategory.ProductCategoryInt.class);
				if (CollectionUtil.isNotEmpty(errorList)) {
					throw new ApplicationException(errorList.toString() + " at row number \"" + row + "\"");
				}
				if (ProductCategoryNameList.contains(productCategory.getProductCode().toUpperCase())) {
					throw new ApplicationException("Duplicate Product Category \'" + productCategory.getProductCode() + "\' in excel at row number \"" + row + "\"");
				}

				ProductCategory dbProductCategory = productCategoryMaintenanceDao.getByProductCategoryByCode(productCategory.getProductCode(), tenantId);
				if (dbProductCategory != null) {
					LOG.info("old Product Category :" + productCategory.toLogString());
					dbProductCategory.setProductName(productCategory.getProductName());
					dbProductCategory.setStatus(productCategory.getStatus());
					dbProductCategory.setModifiedBy(loggedInUser);
					dbProductCategory.setModifiedDate(new Date());
					productCategoryMaintenanceDao.saveOrUpdate(dbProductCategory);
				} else {
					LOG.info("new Product Category :" + productCategory.toLogString());
					productCategory.setBuyer(loggedInUser.getBuyer());
					productCategory.setCreatedBy(loggedInUser);
					productCategory.setCreatedDate(new Date());
					productCategoryMaintenanceDao.save(productCategory);
				}
				row++;
				ProductCategoryNameList.add(productCategory.getProductCode().toUpperCase());
			}
		}

	}

	/**
	 * @param pc
	 */
	public List<String> validateProductCategory(ProductCategory pc, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<ProductCategory>> constraintViolations = validator.validate(pc, validations);
		for (ConstraintViolation<ProductCategory> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@Override
	public ProductItem checkProductItemExistOrNot(String itemName, String supplierId, String teanantId) {
		return productCategoryMaintenanceDao.checkProductItemExistOrNot(itemName, supplierId, teanantId);
	}

	@Override
	@Transactional(readOnly = false)
	public ProductItem updateProductItemPrice(ProductItem productItem) {

		return productListMaintenanceDao.update(productItem);
	}

	@Override
	@Transactional(readOnly = false)
	public ProductItem saveNewProductItem(ProductItem productItem) {
		return productListMaintenanceDao.save(productItem);
	}

	@Override
	public List<ProductCategory> findProductCategoryByTenantIDSupplierID(String loggedInUserTenantId, String supplierId) {
		List<ProductCategory> pcList = productCategoryMaintenanceDao.findProductCategoryByTenantIDSupplierID(loggedInUserTenantId, supplierId);
		if (pcList != null) {
			for (ProductCategory bpc : pcList) {
				if (bpc != null) {
					bpc.setCreatedBy(null);
					bpc.setModifiedBy(null);
				}
			}
		}
		return pcList;
	}

	@Override
	public List<ProductItem> findProductsBySupplierIdAndCategoryId(String loggedInUserTenantId, String supplierId, String productCategoryId) {
		return productCategoryMaintenanceDao.findProductsBySupplierIdAndCategoryId(loggedInUserTenantId, supplierId, productCategoryId);
	}

	@Override
	public List<ProductCategory> findProductsCategoryByTenantAndItemId(String loggedInUserTenantId, String itemId) {
		return productCategoryMaintenanceDao.findProductsCategoryByTenantAndItemId(loggedInUserTenantId, itemId);
	}

	@Override
	public List<FavouriteSupplier> findFavSupplierByCategoryId(String productCategoryId, String supplierId) {
		return productCategoryMaintenanceDao.findFavSupplierByCategoryId(productCategoryId, supplierId);
	}

	@Override
	public List<ProductCategory> findProductsBytenantAndOpenSupplier(String loggedInUserTenantId) {

		return productCategoryMaintenanceDao.findProductsBytenantAndOpenSupplier(loggedInUserTenantId);
	}

	@Override
	public List<ProductItem> searchProductsBySupplierIdAndCategoryId(String loggedInUserTenantId, String supplierId, String categoryId, String searchParam, String openSupplier) {
		List<ProductItem> list = productCategoryMaintenanceDao.searchProductsBySupplierIdAndCategoryId(loggedInUserTenantId, supplierId, categoryId, searchParam, openSupplier);
		for (ProductItem productItem : list) {
			if (productItem.getFavoriteSupplier() != null) {
				productItem.getFavoriteSupplier().getFullName();
				if (productItem.getFavoriteSupplier().getSupplier() != null) {
					productItem.getFavoriteSupplier().getSupplier().getCompanyName();
				}
			}
			if (productItem.getProductCategory() != null) {
				productItem.getProductCategory().getProductName();
			}
		}

		return list;
	}

	@Override
	public List<ProductCategory> serchAllActiveProductCategoryForTenant(String loggedInUserTenantId, String search) {
		return productCategoryMaintenanceDao.serchAllActiveProductCategoryForTenant(loggedInUserTenantId, search);

	}

	@Override
	public List<FavouriteSupplier> findFavSupplierByCategoryListId(List<String> catList) {
		List<FavouriteSupplier> list = productcategorydao.findFavSupplierByCategoryListId(catList);
		for (FavouriteSupplier favouriteSupplier : list) {
			if (CollectionUtil.isNotEmpty(favouriteSupplier.getProductCategory())) {
				favouriteSupplier.getProductCategory().get(0).getProductName();
			}
		}
		return list;
	}

	@Override
	public List<ProductItem> searchProductsBySupplierIdList(String loggedInUserTenantId, List<String> slist, String searchParam, List<String> catList) {
		List<ProductItem> list = productCategoryMaintenanceDao.searchProductsBySupplierIdList(loggedInUserTenantId, slist, searchParam, catList);
		for (ProductItem productItem : list) {
			if (productItem.getFavoriteSupplier() != null) {
				productItem.getFavoriteSupplier().getFullName();
				if (productItem.getFavoriteSupplier().getSupplier() != null) {
					productItem.getFavoriteSupplier().getSupplier().getCompanyName();
				}
			}
			if (productItem.getProductCategory() != null) {
				productItem.getProductCategory().getProductName();
			}
		}

		return list;
	}

	@Override
	public List<EventSupplierPojo> findFavSupplierByCategoryListForPr(List<String> catList) {
		return productcategorydao.findFavSupplierByCategoryListForPr(catList);
	}

	@Override
	public ProductCategory getProductCategoryAndTenantId(String productCategory, String id) {
		return productCategoryMaintenanceDao.getProductCategoryCodeAndTenantId(productCategory, id);
	}

	@Override
	public boolean isExistsProductCategory(String productCode, String id) {
		return productCategoryMaintenanceDao.isExistsProductCategory(productCode, id);
	}

	@Override
	public ProductCategory findProductCategoryByCode(String productCode, String id) {
		return productCategoryMaintenanceDao.findProductCategoryByCode(productCode, id);
	}

	@Override
	@Transactional(readOnly = false)
	public String createProductCategory(ProductCategory productCatgory, Boolean actionByErp) {
		BuyerAuditTrail ownerAuditTrail = null;
		String auditRemarks = "'" + productCatgory.getProductName() + "' Product Category created";

		if (Boolean.TRUE == actionByErp) {
			auditRemarks += " via ERP";
		}
		if (StringUtils.checkString(productCatgory.getId()).length() > 0) {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + productCatgory.getProductName() + "' Product Category settings updated", productCatgory.getModifiedBy().getTenantId(), productCatgory.getModifiedBy(), new Date(), ModuleType.ProductCategory);
		} else {
			ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, auditRemarks, productCatgory.getCreatedBy().getTenantId(), productCatgory.getCreatedBy(), new Date(), ModuleType.ProductCategory);
		}
		buyerAuditTrailDao.save(ownerAuditTrail);

		productCatgory = productCategoryMaintenanceDao.saveOrUpdate(productCatgory);
		return (productCatgory != null ? productCatgory.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateProductCategory(ProductCategory productCatgory, Boolean actionByErp) {

		String auditRemarks = "'" + productCatgory.getProductName() + "' Product Category updated";

		if (Boolean.TRUE == actionByErp) {
			if (Status.INACTIVE == productCatgory.getStatus()) {
				auditRemarks += " to INACTIVE ";
			}
			auditRemarks += " via ERP";
		}
		productCategoryMaintenanceDao.update(productCatgory);

		LOG.info("productCategoryMaintenance Update ID---" + productCatgory.getId().length());
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, auditRemarks, productCatgory.getModifiedBy().getTenantId(), productCatgory.getModifiedBy(), new Date(), ModuleType.ProductCategory);
		buyerAuditTrailDao.save(ownerAuditTrail);
	}

	public List<ProductContractItems> searchProductContractBySupplierIdList(String loggedInUserTenantId, List<String> slist, String searchParam, List<String> catList, String unitName) {
		List<ProductContractItems> list = productCategoryMaintenanceDao.searchProductContractBySupplierIdList(loggedInUserTenantId, slist, searchParam, catList, unitName);
		for (ProductContractItems productItem : list) {
			if (productItem.getSupplier() != null) {
				productItem.getSupplier().getFullName();
				if (productItem.getSupplier().getSupplier() != null) {
					productItem.getSupplier().getSupplier().getCompanyName();
				}
			}
			if (productItem.getProductCategory() != null) {
				productItem.getProductCategory().getProductName();
			}
			if (productItem.getUom() != null) {
				productItem.getUom().getUom();
			}
			if (productItem.getBusinessUnit() != null) {
				productItem.getBusinessUnit().getUnitName();
			}
		}

		return list;
	}

	@Override
	public List<ProductCategoryPojo> fetchAllActiveProductCategoryForTenant(String tenantId, String search) {
		List<ProductCategoryPojo> list = productCategoryMaintenanceDao.fetchAllActiveProductCategoryForTenant(tenantId, search);
		long count = productCategoryMaintenanceDao.countConstructQueryToFetchProductCategory(tenantId);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list != null && count > list.size()) {
				ProductCategoryPojo more = new ProductCategoryPojo();
				more.setId("-1");
				more.setProductName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}

	@Override
	public void prodCategoryDownloadTemplate(HttpServletResponse response, String tenantId) {

		LOG.info("download ProdCategory Template method called");
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ProductCategoryTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			XSSFSheet sheet = workbook.createSheet("Product Category List");
			// Creating Headings
			buildHeader(workbook, sheet);

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

	@Override
	public void downloadProdCategoryCsvFile(HttpServletResponse response, File file, String tenantId) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.PRODUCT_CATEGORY_EXCEL_COLUMNS;
			String[] columns = new String[] { "productCode", "productName", "status" };

			long count = productCategoryMaintenanceDao.findProductCategoryCountForTenant(tenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);
			CellProcessor[] processor = getProcessor();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<ProductCategory> list = getAllProductCategoryByTenantIdForCsv(tenantId, PAGE_SIZE, pageNo);
				LOG.info("List ............. " + list.size() + "Count  ..........." + count);

				for (ProductCategory product : list) {
					beanWriter.write(product, columns, processor);
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

	private List<ProductCategory> getAllProductCategoryByTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		List<ProductCategory> pcList = productCategoryMaintenanceDao.getAllProductCategoryByTenantIdForCsv(tenantId, pageSize, pageNo);
		if (pcList != null) {
			for (ProductCategory bic : pcList) {
				bic.setCreatedBy(null);
				bic.setModifiedBy(null);
			}
		}
		return pcList;
	}

	private CellProcessor[] getProcessor() {
		CellProcessor[] processor = new CellProcessor[] { new Optional(), new Optional(), new Optional() };
		return processor;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductCategory> getAllActiveProductCategoryForTenant(String tenantId, String searchValue) {

		List<ProductCategory> list = productCategoryMaintenanceDao.getAllActiveProductCategoryForTenant(tenantId, searchValue);
//		long count = productCategoryMaintenanceDao.findActiveProductCategoryForTenant(tenantId, searchValue);
//		// get count
//		if (count > list.size()) {
//			ProductCategory more = new ProductCategory();
//			more.setId("-1");
//			more.setProductName("Total " + (count) + " Categories. Continue typing to find match...");
//			list.add(more);
//		}

		return list;
	}

}
