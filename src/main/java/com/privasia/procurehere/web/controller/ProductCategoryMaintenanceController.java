package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;

/**
 * @author Javed Ahmed
 */
@Controller
@RequestMapping(path = "/buyer")
public class ProductCategoryMaintenanceController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	ProductCategoryMaintenanceService productCategoryService;
	
	@Autowired 
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/productCategoryMaintenance", method = RequestMethod.GET)
	public ModelAndView createProductCategory(@ModelAttribute ProductCategory productCategoryMaintenance, Model model) {
		model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("productCategoryMaintenance", "productCategoryMaintenance", new ProductCategory());
	}

	@RequestMapping(path = "/productCategoryMaintenance", method = RequestMethod.POST)
	public ModelAndView saveProductCategory(@ModelAttribute("productCategoryMaintenance") ProductCategory productCategoryMaintenance, BindingResult result, Model model, RedirectAttributes redir) {

		List<String> errMessages = validateProductCategory(productCategoryMaintenance, ProductCategory.ProductCategoryInt.class);

		if (CollectionUtil.isNotEmpty(errMessages)) {
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			return new ModelAndView("productCategoryMaintenance", "productCategoryMaintenance", new ProductCategory());
		}
		try {
			if (doValidate(productCategoryMaintenance)) {

				if (StringUtils.checkString(productCategoryMaintenance.getId()).length() == 0) {
					productCategoryMaintenance.setCreatedBy(SecurityLibrary.getLoggedInUser());
					productCategoryMaintenance.setCreatedDate(new Date());
					productCategoryMaintenance.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					productCategoryService.createProductCategory(productCategoryMaintenance);
					redir.addFlashAttribute("success", messageSource.getMessage("product.cat.create.success", new Object[] { productCategoryMaintenance.getProductName() }, Global.LOCALE));
					return new ModelAndView("redirect:listProductCategory");
				} else {
					ProductCategory persistObj = productCategoryService.getProductCategoryById(productCategoryMaintenance.getId());

					persistObj.setProductCode(productCategoryMaintenance.getProductCode());
					persistObj.setProductName(productCategoryMaintenance.getProductName());
					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());
					persistObj.setStatus(productCategoryMaintenance.getStatus());
					productCategoryService.updateProductCategory(persistObj);
					redir.addFlashAttribute("success", messageSource.getMessage("product.cat.update.success", new Object[] { productCategoryMaintenance.getProductName() }, Global.LOCALE));
					return new ModelAndView("redirect:listProductCategory");

				}

			} else {
				model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("errors", messageSource.getMessage("product.error.duplicate", new Object[] {}, Global.LOCALE));
				return new ModelAndView("productCategoryMaintenance", "productCategoryMaintenance", productCategoryMaintenance);

			}
		} catch (Exception e) {
			productCategoryService.updateProductCategory(productCategoryMaintenance);
			redir.addFlashAttribute("success", messageSource.getMessage("product.cat.update.success", new Object[] { productCategoryMaintenance.getProductName() }, Global.LOCALE));
			return new ModelAndView("redirect:listProductCategory");
		}

	}

	@RequestMapping(path = "/listProductCategory", method = RequestMethod.GET)
	public String listProductCategory(Model model) throws JsonProcessingException {
		return "productCategoryMaintenanceList";

	}

	private boolean doValidate(ProductCategory productCategoryMaintenance) {
		boolean validate = true;
		if (productCategoryService.isExists(productCategoryMaintenance, SecurityLibrary.getLoggedInUserTenantId())) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/productCategoryEdit", method = RequestMethod.GET)
	public ModelAndView editProductCategory(@RequestParam String id, ProductCategory productCategoryMaintenance, ModelMap model) {
		ProductCategory productCategory = productCategoryService.getProductCategoryById(id);
		model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("productCategoryMaintenance", "productCategoryMaintenance", productCategory);

	}

	@RequestMapping(path = "/productCategoryDelete", method = RequestMethod.GET)
	public String deleteProductCategory(@RequestParam String id, ProductCategory productCategoryMaintenance, Model model, RedirectAttributes redir) throws JsonProcessingException {
		LOG.info("productCategory in delete" + id);
		ProductCategory productCategory = productCategoryService.getProductCategoryById(id);
		try {
			if (productCategory != null) {
				LOG.info("prodcode:" + productCategory.getProductCode());
				productCategoryService.deleteProductCategory(productCategory, SecurityLibrary.getLoggedInUser());
				redir.addFlashAttribute("success", messageSource.getMessage("product.cat.delete.success", new Object[] { productCategory.getProductName() }, Global.LOCALE));
				LOG.info("Deletion success");
			}
		} catch (Exception e) {
			LOG.error("Deletion error occured : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.cat.delete.error", new Object[] { productCategory.getProductName() }, Global.LOCALE));
		}
		return "redirect:listProductCategory";
	}

	@RequestMapping(path = "/productCategoryData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductCategory>> costCenterData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<ProductCategory> data = new TableData<ProductCategory>(productCategoryService.findProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));

			data.setDraw(input.getDraw());
			long totalFilterCount = productCategoryService.findTotalFilteredProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productCategoryService.findTotalProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductCategory>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Product Category Maintenance list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Product Category Maintenance list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductCategory>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/productCategoryTemplate", method = RequestMethod.GET)
	public void downloadProdCategoryListExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			productCategoryService.productCategoryDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			LOG.error("Error while downloading Product Category  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/uploadProductCategory", method = RequestMethod.POST)
	public ResponseEntity<String> uploadProductCategoryExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		try {
			validateUploadProductCategory(file);
			LOG.info("uploadProductCategoryExcel method called" + file.getOriginalFilename());
			productCategoryService.productCategoryUploadFile(file, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
			headers.add("success", "Product category list uploaded successfully");
			return new ResponseEntity<String>(headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Uploading ProductCategory  template :: " + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public void validateUploadProductCategory(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
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
			LOG.info("Error Message : " + cv.getMessage() + " ==Property path : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}
	
	@RequestMapping(path = "/productCategoryXlTemplate", method = RequestMethod.GET)
	public void downloadProdCategoryTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			productCategoryService.prodCategoryDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			LOG.error("Error while downloading Product Category  template :: " + e.getMessage(), e);
		}
	}
	
	@RequestMapping(path = "/productCategoryCsv", method = RequestMethod.GET)
	public void downloadProdCategoryListCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			File file = File.createTempFile("ProductCategory", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			productCategoryService.downloadProdCategoryCsvFile(response, file,  SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Product Category is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ProductCategory);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=ProductCategory.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading Product template :: " + e.getMessage(), e);
		}
	}
	
}