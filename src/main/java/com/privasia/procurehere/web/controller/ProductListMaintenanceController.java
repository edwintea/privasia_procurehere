package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.ProductListMaintenanceService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.web.editors.FavouriteSupplierEditor;
import com.privasia.procurehere.web.editors.ProductCategoryMaintenanceEditor;
import com.privasia.procurehere.web.editors.UomEditor;

/**
 * @author Javed Ahmed
 */

@Controller
@RequestMapping(path = "/buyer")
public class ProductListMaintenanceController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UomService uomService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	UomEditor uomEditor;

	@Autowired
	ProductCategoryMaintenanceEditor pcmEditor;

	@Autowired
	FavouriteSupplierEditor fsEditor;

	@Autowired
	ProductListMaintenanceService productListService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Resource
	MessageSource messageSource;

	@Autowired
	PrService prService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(Uom.class, uomEditor);
		binder.registerCustomEditor(ProductCategory.class, pcmEditor);
		binder.registerCustomEditor(FavouriteSupplier.class, fsEditor);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
		timeFormat.setTimeZone(timeZone);

		binder.registerCustomEditor(Date.class, "validityDate", new CustomDateEditor(timeFormat, true));
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}
	
	@ModelAttribute("itemTypeList")
	public List<ProductItemType> getProductItemType() {
		return Arrays.asList(ProductItemType.values());
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@RequestMapping(path = "/productListMaintenance", method = RequestMethod.GET)
	public ModelAndView createProductList(@ModelAttribute ProductItem productListMaintenance, Model model) {
		LOG.info("logged in user ::  " + SecurityLibrary.getLoggedInUser().getBuyer().getId());
		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		String decimal = buyerSettings.getDecimal();
		int z = Integer.parseInt(decimal);
		StringBuffer decimals = new StringBuffer(0);
		for (int i = 0; i < z; i++) {
			decimals.append(0);
		}
		model.addAttribute("favSupp", favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null));
		// model.addAttribute("favSupp",
		// favoriteSupplierService.getAllActiveFavouriteSupplierByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("decimals", decimals);
		model.addAttribute("decimal", decimal);
		model.addAttribute("uoms", uomService.fetchAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), null));
		model.addAttribute("pcms", productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null));
		model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("productListMaintenance", "productListMaintenance", new ProductItem());
	}

	@RequestMapping(path = "/saveProductListMaintenance", method = RequestMethod.POST)
	public ModelAndView saveProductList(@Valid @ModelAttribute("productListMaintenance") ProductItem productListMaintenance, BindingResult result, @RequestParam(value = "uploadDocx", required = false) MultipartFile uploadDocx, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("inside save");
		List<String> errMessages = new ArrayList<String>();
		ProductItem productListObj = null;
		if (StringUtils.checkString(productListMaintenance.getId()).length() > 0) {
			productListObj = productListService.getProductCategoryById(productListMaintenance.getId());
		}
		try {
			if (productListMaintenance.isContractItem() && productListMaintenance.getValidityDate() != null) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				// String[] dateTimeArr = productListMaintenance.getDateTimeRange().split("-");
				// productListMaintenance.setStartDate(formatter.parse(dateTimeArr[0]));
				// productListMaintenance.setValidityDate(formatter.parse(dateTimeArr[1]));
			} else {
				productListMaintenance.setStartDate(null);
				productListMaintenance.setValidityDate(null);
				productListMaintenance.setContractReferenceNumber(null);
			}
		} catch (Exception e) {
			LOG.error("Error while Parsing Date draft list : " + e.getMessage(), e);
		}

		try {
			if (productListMaintenance.getValidityDate() != null && productListMaintenance.getValidityDate().before(new Date())) {
				LOG.info("validity date :" + productListMaintenance.getValidityDate() + " current time :" + new Date());
				model.addAttribute("error", messageSource.getMessage("product.validityDate.error", new Object[] {}, Global.LOCALE));

				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
				String decimal = buyerSettings.getDecimal();
				int z = Integer.parseInt(decimal);
				StringBuffer decimals = new StringBuffer(0);
				for (int i = 0; i < z; i++) {
					decimals.append(0);
				}
				model.addAttribute("decimals", decimals);
				model.addAttribute("decimal", decimal);
				// model.addAttribute("favSupp", favoriteSupplierService.favSuppliersByNameAndTenant(null,
				// SecurityLibrary.getLoggedInUser().getBuyer().getId()));
				List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null);
				List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
				List<UomPojo> uomListData = uomService.fetchAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
				List<UomPojo> uomList = new ArrayList<UomPojo>();
				List<ProductCategoryPojo> productCategoryListData = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
				List<ProductCategoryPojo> productCategoryList = new ArrayList<ProductCategoryPojo>();
				if (StringUtils.checkString(productListMaintenance.getId()).length() == 0) {
					model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				} else {
					model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
					if (productListMaintenance.getFavoriteSupplier() != null) {
						supplierList.add(new SupplierPojo(productListMaintenance.getFavoriteSupplier().getId(), productListObj.getSupplierName()));
					}
					if (productListMaintenance.getUom() != null) {
						uomList.add(new UomPojo(productListMaintenance.getUom().getId(), productListMaintenance.getUom().getUom(), productListMaintenance.getUom().getUomDescription()));
					}
					if (productListMaintenance.getProductCategory() != null) {
						productCategoryList.add(new ProductCategoryPojo(productListMaintenance.getProductCategory().getId(), productListMaintenance.getProductCategory().getProductName()));
					}
				}
				for (SupplierPojo supplier : supplierListData) {

					boolean isSupplierExist = false;
					if (productListMaintenance.getFavoriteSupplier() != null && productListMaintenance.getFavoriteSupplier().getId().equals(supplier.getId())) {
						isSupplierExist = true;
					}
					if (!isSupplierExist) {
						supplierList.add(supplier);
					}
				}
				for (UomPojo uom : uomListData) {
					boolean isUomExist = false;
					if (productListMaintenance.getUom() != null && productListMaintenance.getUom().getId().equals(uom.getId())) {
						isUomExist = true;
					}
					if (!isUomExist) {
						uomList.add(uom);
					}
				}

				for (ProductCategoryPojo productCategory : productCategoryListData) {
					boolean isproductExist = false;
					if (productListMaintenance.getProductCategory() != null && productListMaintenance.getProductCategory().getId().equals(productCategory.getId())) {
						isproductExist = true;
					}
					if (!isproductExist) {
						productCategoryList.add(productCategory);
					}
				}

				model.addAttribute("favSupp", supplierList);
				model.addAttribute("uoms", uomList);
				model.addAttribute("pcms", productCategoryList);
				return new ModelAndView("productListMaintenance", "productListMaintenance", productListMaintenance);
			}
			if (doValidate(productListMaintenance)) {
				if (result.hasErrors()) {
					for (ObjectError err : result.getAllErrors()) {
						errMessages.add(err.getDefaultMessage());
					}
					model.addAttribute("error", errMessages);

					BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
					String decimal = buyerSettings.getDecimal();
					int z = Integer.parseInt(decimal);
					StringBuffer decimals = new StringBuffer(0);
					for (int i = 0; i < z; i++) {
						decimals.append(0);
					}
					model.addAttribute("decimals", decimals);
					model.addAttribute("decimal", decimal);
					List<UomPojo> uomListData = uomService.fetchAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
					List<UomPojo> uomList = new ArrayList<UomPojo>();
					List<ProductCategoryPojo> productCategoryListData = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
					List<ProductCategoryPojo> productCategoryList = new ArrayList<ProductCategoryPojo>();
					if (StringUtils.checkString(productListMaintenance.getId()).length() == 0) {
						model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					} else {
						model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
						if (productListMaintenance.getUom() != null) {
							uomList.add(new UomPojo(productListMaintenance.getUom().getId(), productListMaintenance.getUom().getUom(), productListMaintenance.getUom().getUomDescription()));
						}
						if (productListMaintenance.getProductCategory() != null) {
							productCategoryList.add(new ProductCategoryPojo(productListMaintenance.getProductCategory().getId(), productListMaintenance.getProductCategory().getProductName()));
						}
					}
					for (UomPojo uom : uomListData) {
						boolean isUomExist = false;
						if (productListMaintenance.getUom() != null && productListMaintenance.getUom().getId().equals(uom.getId())) {
							isUomExist = true;
						}
						if (!isUomExist) {
							uomList.add(uom);
						}
					}
					for (ProductCategoryPojo productCategory : productCategoryListData) {
						boolean isproductExist = false;
						if (productListMaintenance.getProductCategory() != null && productListMaintenance.getProductCategory().getId().equals(productCategory.getId())) {
							isproductExist = true;
						}
						if (!isproductExist) {
							productCategoryList.add(productCategory);
						}
					}
					model.addAttribute("uoms", uomList);
					model.addAttribute("pcms", productCategoryList);
					return new ModelAndView("productListMaintenance", "productListMaintenance", productListMaintenance);
				} else if (StringUtils.checkString(productListMaintenance.getId()).length() == 0) {
					LOG.info("inside create");
					String fileName = null;
					if (uploadDocx != null && !uploadDocx.isEmpty()) {
						fileName = uploadDocx.getOriginalFilename();
						byte[] bytes = uploadDocx.getBytes();
						LOG.info("FILE CONTENT   " + uploadDocx.getName());
						productListMaintenance.setContentType(uploadDocx.getContentType());
						productListMaintenance.setFileName(fileName);
						productListMaintenance.setFileAttatchment(bytes);
					}
					productListMaintenance.setCreatedBy(SecurityLibrary.getLoggedInUser());
					productListMaintenance.setCreatedDate(new Date());
					productListMaintenance.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());

					productListService.createProductItem(productListMaintenance, Boolean.FALSE);
					LOG.info("Product List after save:::");
					redir.addFlashAttribute("success", messageSource.getMessage("product.create.success", new Object[] { productListMaintenance.getProductName() }, Global.LOCALE));
					return new ModelAndView("redirect:productList");
				} else if (StringUtils.checkString(productListMaintenance.getId()).length() > 0) {

					LOG.info("Product List In update::");
					try {
						ProductItem persistObj = productListService.getProductCategoryById(productListMaintenance.getId());
						String fileName = null;
						if (uploadDocx != null && !uploadDocx.isEmpty()) {
							try {
								fileName = uploadDocx.getOriginalFilename();
								byte[] bytes = uploadDocx.getBytes();
								LOG.info("FILE CONTENT   " + bytes);
								persistObj.setContentType(uploadDocx.getContentType());
								persistObj.setFileName(fileName);
								persistObj.setFileAttatchment(bytes);
							} catch (Exception e) {
								LOG.info("inside save catch");
								return new ModelAndView("redirect:productListMaintenance");
							}
						} else {
							LOG.info("update empty file");
							persistObj.setContentType(null);
							persistObj.setFileName(null);
							persistObj.setFileAttatchment(null);
						}
						persistObj.setInterfaceCode(productListMaintenance.getInterfaceCode());
						persistObj.setProductCode(productListMaintenance.getProductCode());
						persistObj.setProductName(productListMaintenance.getProductName());
						persistObj.setUom(productListMaintenance.getUom());
						persistObj.setFavoriteSupplier(productListMaintenance.getFavoriteSupplier());
						persistObj.setUnitPrice(productListMaintenance.getUnitPrice());
						persistObj.setRemarks(productListMaintenance.getRemarks());
						persistObj.setValidityDate(productListMaintenance.getValidityDate());
						persistObj.setStartDate(productListMaintenance.getStartDate());
						persistObj.setContractReferenceNumber(productListMaintenance.getContractReferenceNumber());
						persistObj.setGlCode(productListMaintenance.getGlCode());
						persistObj.setUnspscCode(productListMaintenance.getUnspscCode());
						persistObj.setProductCategory(productListMaintenance.getProductCategory());
						persistObj.setStatus(productListMaintenance.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setContractItem(productListMaintenance.isContractItem());
						persistObj.setHistoricPricingRefNo(productListMaintenance.getHistoricPricingRefNo());
						persistObj.setPurchaseGroupCode(productListMaintenance.getPurchaseGroupCode());
						persistObj.setBrand(productListMaintenance.getBrand());
						persistObj.setTax(productListMaintenance.getTax());
						persistObj.setProductItemType(productListMaintenance.getProductItemType());
						productListService.updateProductItem(persistObj, Boolean.FALSE);

						redir.addFlashAttribute("success", messageSource.getMessage("product.update.success", new Object[] { persistObj.getProductName() }, Global.LOCALE));
						return new ModelAndView("redirect:productList");
					} catch (Exception e) {

						LOG.info("inside save catch");
						return new ModelAndView("redirect:productListMaintenance");
					}

				}
			} else {
				// model.addAttribute("favSupp",
				// favoriteSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUser().getBuyer().getId(),
				// null, null, null));
				List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, productListObj != null ? productListObj.getId() : null);
				List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
				List<UomPojo> uomListData = uomService.fetchAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
				List<UomPojo> uomList = new ArrayList<UomPojo>();
				List<ProductCategoryPojo> productCategoryListData = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
				List<ProductCategoryPojo> productCategoryList = new ArrayList<ProductCategoryPojo>();
				if (StringUtils.checkString(productListMaintenance.getId()).length() == 0) {
					model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				} else {
					model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
					if (productListMaintenance.getFavoriteSupplier() != null) {
						supplierList.add(new SupplierPojo(productListMaintenance.getFavoriteSupplier().getId(), productListObj.getSupplierName()));
					}
					if (productListMaintenance.getUom() != null) {
						uomList.add(new UomPojo(productListMaintenance.getUom().getId(), productListMaintenance.getUom().getUom(), productListMaintenance.getUom().getUomDescription()));
					}
					if (productListMaintenance.getProductCategory() != null) {
						productCategoryList.add(new ProductCategoryPojo(productListMaintenance.getProductCategory().getId(), productListMaintenance.getProductCategory().getProductName()));
					}
				}
				for (SupplierPojo supplier : supplierListData) {
					boolean isSupplierExist = false;
					if (productListMaintenance.getFavoriteSupplier() != null && productListMaintenance.getFavoriteSupplier().getId().equals(supplier.getId())) {
						isSupplierExist = true;
					}
					if (!isSupplierExist) {
						supplierList.add(supplier);
					}
				}
				for (UomPojo uom : uomListData) {
					boolean isUomExist = false;
					if (productListMaintenance.getUom() != null && productListMaintenance.getUom().getId().equals(uom.getId())) {
						isUomExist = true;
					}
					if (!isUomExist) {
						uomList.add(uom);
					}
				}
				for (ProductCategoryPojo productCategory : productCategoryListData) {
					boolean isproductExist = false;
					if (productListMaintenance.getProductCategory() != null && productListMaintenance.getProductCategory().getId().equals(productCategory.getId())) {
						isproductExist = true;
					}
					if (!isproductExist) {
						productCategoryList.add(productCategory);
					}
				}
				model.addAttribute("uoms", uomList);
				model.addAttribute("pcms", productCategoryList);
				model.addAttribute("favSupp", supplierList);
				model.addAttribute("error", messageSource.getMessage("product.code.duplicate", new Object[] { productListMaintenance.getProductName() }, Global.LOCALE));
				return new ModelAndView("productListMaintenance", "productListMaintenance", productListMaintenance);
			}
			return new ModelAndView("redirect:productList");

		} catch (Exception e) {
			LOG.error("Error While saving the productList" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("product.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:productListMaintenance");
		}

	}

	@RequestMapping(path = "/productList", method = RequestMethod.GET)
	public String productList(Model model) throws JsonProcessingException {
		return "productListMaintenanceList";
	}

	@RequestMapping(path = "/productListEdit", method = RequestMethod.GET)
	public ModelAndView productListEdit(@RequestParam String id, @ModelAttribute("productListMaintenance") ProductItem productListMaintenance, ModelMap model, HttpSession session) {
		ProductItem productListObj = productListService.getProductCategoryById(id);
		String startDate = "";
		String endDate = "";

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		formatter.setTimeZone(timeZone);

		if (productListObj.getStartDate() != null && productListObj.getValidityDate() != null) {
			startDate = formatter.format(productListObj.getStartDate());
			endDate = formatter.format(productListObj.getValidityDate());
			productListObj.setDateTimeRange(startDate + " - " + endDate);
		}

		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		String decimal = buyerSettings.getDecimal();
		int z = Integer.parseInt(decimal);
		StringBuffer decimals = new StringBuffer(0);
		for (int i = 0; i < z; i++) {
			decimals.append(0);
		}
		model.addAttribute("decimals", decimals);
		model.addAttribute("decimal", decimal);
		// model.addAttribute("favSupp",
		// favoriteSupplierService.getAllActiveFavouriteSupplierByTenantIdForAnnouncement(SecurityLibrary.getLoggedInUser().getBuyer().getId()));
		List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, productListObj.getId());
		List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
		if (productListObj.getFavoriteSupplier() != null) {
			supplierList.add(new SupplierPojo(productListObj.getFavoriteSupplier().getId(), productListObj.getSupplierName()));
		}
		for (SupplierPojo supplier : supplierListData) {
			boolean isSupplierExist = false;
			if (productListObj.getFavoriteSupplier() != null && productListObj.getFavoriteSupplier().getId().equals(supplier.getId())) {
				isSupplierExist = true;
			}
			if (!isSupplierExist) {
				supplierList.add(supplier);
			}
		}
		List<UomPojo> uomListData = uomService.fetchAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		List<UomPojo> uomList = new ArrayList<UomPojo>();
		if (productListObj.getUom() != null) {
			uomList.add(new UomPojo(productListObj.getUom().getId(), productListObj.getUom().getUom(), productListObj.getUom().getUomDescription()));
		}
		for (UomPojo uom : uomListData) {
			boolean isUomExist = false;
			if (productListObj.getUom() != null && productListObj.getUom().getId().equals(uom.getId())) {
				isUomExist = true;
			}
			if (!isUomExist) {
				uomList.add(uom);
			}
		}
		List<ProductCategoryPojo> productCategoryListData = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		List<ProductCategoryPojo> productCategoryList = new ArrayList<ProductCategoryPojo>();
		if (productListObj.getProductCategory() != null) {
			productCategoryList.add(new ProductCategoryPojo(productListObj.getProductCategory().getId(), productListObj.getProductCategory().getProductName()));
		}
		for (ProductCategoryPojo productCategory : productCategoryListData) {
			boolean isproductExist = false;
			if (productListObj.getProductCategory() != null && productListObj.getProductCategory().getId().equals(productCategory.getId())) {
				isproductExist = true;
			}
			if (!isproductExist) {
				productCategoryList.add(productCategory);
			}
		}
		model.addAttribute("favSupp", supplierList);
		model.addAttribute("uoms", uomList);
		model.addAttribute("pcms", productCategoryList);
		model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		LOG.info("productListObj : " + productListObj.getValidityDate());
		return new ModelAndView("productListMaintenance", "productListMaintenance", productListObj);
	}

	@RequestMapping(path = "/productListDelete", method = RequestMethod.GET)
	public String productListEdit(@RequestParam String id, Model model, RedirectAttributes redir) throws JsonProcessingException {
		ProductItem productListObj = null;
		try {
			productListObj = productListService.getProductCategoryById(id);
			productListObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
			productListObj.setModifiedDate(new Date());
			productListService.deleteProductCategory(productListObj);
			// model.addAttribute("success",
			// messageSource.getMessage("product.success.delete", new Object[] {
			// productListObj != null ? productListObj.getProductCode()+"-"+
			// productListObj.getProductName() : "" },
			// Global.LOCALE));
			redir.addFlashAttribute("success", messageSource.getMessage("product.success.delete", new Object[] { productListObj != null ? productListObj.getProductCode() + "-" + productListObj.getProductName() : "" }, Global.LOCALE));
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting Product , " + e.getMessage(), e);
			// model.addAttribute("error",
			// messageSource.getMessage("product.error.delete.child.exist", new Object[] {
			// productListObj != null ?productListObj.getProductCode()+"-"+
			// productListObj.getProductName() : "" },
			// Global.LOCALE));
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.delete.child.exist", new Object[] { productListObj != null ? productListObj.getProductCode() + "-" + productListObj.getProductName() : "" }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Product , " + e.getMessage(), e);
			// model.addAttribute("error", messageSource.getMessage("product.error.delete",
			// new Object[] {
			// productListObj != null ?
			// productListObj.getProductCode()+"-"+productListObj.getProductName() : "" },
			// Global.LOCALE));
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.delete", new Object[] { productListObj != null ? productListObj.getProductCode() + "-" + productListObj.getProductName() : "" }, Global.LOCALE));
		}
		// return "productListMaintenanceList";
		return "redirect:productList";
	}

	@RequestMapping(path = "/productListMaintenanceData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductItemPojo>> rfxTemplateData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<ProductItemPojo> data = new TableData<ProductItemPojo>(productListService.findProductListForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = productListService.findTotalFilteredProductListForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productListService.findTotalProductListForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			List<ProductItemPojo> items = data.getData();

			return new ResponseEntity<TableData<ProductItemPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching productListMaintenanceData list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productListMaintenanceData list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductItemPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/searchFavSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<FavouriteSupplier>> searchProductItem(@RequestParam("search") String search) {
		LOG.info("search fav supp post called search : " + search);
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		List<FavouriteSupplier> suppList = favoriteSupplierService.favSuppliersByNameAndTenant(search, tenantId);
		for (FavouriteSupplier favouriteSupplier : suppList) {
			favouriteSupplier.setIndustryCategory(null);
			favouriteSupplier.setProductCategory(null);
		}
		LOG.info("suppList :" + suppList.size());
		return new ResponseEntity<List<FavouriteSupplier>>(suppList, HttpStatus.OK);
	}

	private boolean doValidate(ProductItem productListMaintenance) {
		boolean validate = true;
		if (productListService.isExists(productListMaintenance.getProductCode(), SecurityLibrary.getLoggedInUserTenantId(), productListMaintenance.getId())) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(value = "/downloadProductDocument/{productId}", method = RequestMethod.GET)
	public void downloadProductDocument(@PathVariable String productId, HttpServletResponse response) {
		try {
			LOG.info("  :: :: " + productId + "::::::");
			ProductItem productItem = productListService.getProductCategoryForDownloadAttachmentById(productId);
			response.setContentType(productItem.getContentType());
			response.setContentLength(productItem.getFileAttatchment().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + productItem.getFileName() + "\"");
			FileCopyUtils.copy(productItem.getFileAttatchment(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while downloading product Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/exportProductList/{type}", method = RequestMethod.GET)
	public void downloadProductItemList(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "type", required = true) Integer type) throws IOException {
		try {
			productListMaintenanceService.productListDownload(response, SecurityLibrary.getLoggedInUserTenantId(), type);
		} catch (Exception e) {
			LOG.error("Error while downloading product Item Excel :" + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/exportSelectedProductList", method = RequestMethod.POST)
	public String exportProductList(@RequestParam String itemIds, @RequestParam Integer downloadtype, HttpServletResponse response, RedirectAttributes redir) throws IOException {
		try {
			if (StringUtils.checkString(itemIds).length() > 0) {
				String[] strList = itemIds.split(",");
				List<String> list = new ArrayList<String>();
				for (String str : strList) {
					list.add(str);
				}
				productListMaintenanceService.productListDownload(response, SecurityLibrary.getLoggedInUserTenantId(), list, downloadtype);
			} else {
				LOG.warn("Please add items to bucket list");
				redir.addFlashAttribute("error", messageSource.getMessage("product.bucket.list.empty.error", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/productList";
			}
		} catch (Exception e) {
			LOG.error("Error while downloading product Item Excel :" + e.getMessage(), e);
		}
		return null;
	}

	@RequestMapping(path = "/productTemplate", method = RequestMethod.GET)
	public void downloadproductTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			productListMaintenanceService.productDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error("Error while downloading product Item Template :" + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/uploadProductItem", method = RequestMethod.POST)
	public ResponseEntity<String> uploadProductItemExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		try {
			validateUploadProductItemExcel(file);
			LOG.info("uploadProductItemExcel method called" + file.getOriginalFilename());
			productListMaintenanceService.productItemUploadFile(file, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
			headers.add("success", "Product item list uploaded successfully");
			return new ResponseEntity<String>(headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Uploading Product item  template :: " + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public void validateUploadProductItemExcel(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/addToBucketList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<ProductItemPojo>> addToBucketList(@RequestBody ProductItemPojo itemPojo, HttpSession session) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(itemPojo.getId()).length() > 0) {
				LOG.info("Adding to Bucket List : " + itemPojo.getId());
				List<ProductItemPojo> sessionList = (List<ProductItemPojo>) session.getAttribute(Global.BUCKET_ITEM_LIST);
				if (sessionList != null) {
					sessionList.add(itemPojo);
					LOG.info("sessionList.getData() : " + sessionList.get(0).getFavoriteSupplier());
					session.setAttribute(Global.BUCKET_ITEM_LIST, sessionList);
				} else {
					sessionList = new ArrayList<ProductItemPojo>();
					sessionList.add(itemPojo);
				}
				session.setAttribute(Global.BUCKET_ITEM_LIST, sessionList);

				ObjectMapper mapper = new ObjectMapper();
				session.removeAttribute(Global.BUCKET_ITEM_JSON);
				session.setAttribute(Global.BUCKET_ITEM_JSON, mapper.writeValueAsString(sessionList));
				headers.add("success", "Item added to bucket list.");
				return new ResponseEntity<List<ProductItemPojo>>(sessionList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding Items to bucket list : " + e.getMessage(), e);
			headers.add("error", "Error while adding product item to bucket list");
			return new ResponseEntity<List<ProductItemPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<ProductItemPojo>>(null, headers, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/removeBucketList", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> removeBucketList(HttpSession session) throws ApplicationException {
		Map<String, String> response = new HashMap<String, String>();
		try {
			List<ProductItemPojo> sessionList = (List<ProductItemPojo>) session.getAttribute(Global.BUCKET_ITEM_LIST);
			if (sessionList != null) {
				session.removeAttribute(Global.BUCKET_ITEM_LIST);
				session.removeAttribute(Global.BUCKET_ITEM_JSON);
				LOG.info("Cleared session..................");
				response.put("success", "Cleared Bucket List successfully");
			} else {
				response.put("error", "No Items in Bucket List");
				return new ResponseEntity<Map<String, String>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error while adding Items to bucket list : " + e.getMessage(), e);
			response.put("error", "Error while clearing the Bucket List");
			return new ResponseEntity<Map<String, String>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/removeBucketListById/{id}", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> removeBucketListById(@PathVariable("id") String id, HttpSession session) throws ApplicationException {
		Map<String, String> response = new HashMap<String, String>();
		try {
			if (StringUtils.checkString(id).length() > 0) {
				List<ProductItemPojo> sessionList = (List<ProductItemPojo>) session.getAttribute(Global.BUCKET_ITEM_LIST);
				if (sessionList != null) {
					List<ProductItemPojo> newList = new ArrayList<ProductItemPojo>();
					for (ProductItemPojo pojo : sessionList) {
						if (!pojo.getId().equals(id)) {
							newList.add(pojo);
						}
					}
					session.removeAttribute(Global.BUCKET_ITEM_LIST);
					session.removeAttribute(Global.BUCKET_ITEM_JSON);

					session.setAttribute(Global.BUCKET_ITEM_LIST, newList);
					ObjectMapper mapper = new ObjectMapper();
					session.setAttribute(Global.BUCKET_ITEM_JSON, mapper.writeValueAsString(newList));
					response.put("success", "Item removed from bucket list.");
				} else {
					LOG.info("Id not present..................");
				}
			}
		} catch (Exception e) {
			LOG.error("Error while adding Items to bucket list : " + e.getMessage(), e);
			response.put("error", "Error while removeing item from Bucket List");
			return new ResponseEntity<Map<String, String>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}

	@PostMapping("/searchMoreFavouritesuppliers")
	public @ResponseBody ResponseEntity<List<SupplierPojo>> searchMoreFavouritesuppliers(@RequestParam("searchSupplier") String searchSupplier, @RequestParam("productId") String productId) {
		List<SupplierPojo> favouriteSupplierList = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), searchSupplier, productId);
		return new ResponseEntity<List<SupplierPojo>>(favouriteSupplierList, HttpStatus.OK);
	}

	@PostMapping("/searchUomFromList")
	public @ResponseBody ResponseEntity<List<UomPojo>> searchUomFromList(@RequestParam("uom") String searchSupplier) {
		List<UomPojo> uomList = uomService.fetchAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), searchSupplier);
		return new ResponseEntity<List<UomPojo>>(uomList, HttpStatus.OK);
	}

	@PostMapping("/searchProductCategoryFromList")
	public @ResponseBody ResponseEntity<List<ProductCategoryPojo>> searchProductCategoryFromList(@RequestParam("productName") String searchSupplier) {
		List<ProductCategoryPojo> productCategoryList = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), searchSupplier);
		return new ResponseEntity<List<ProductCategoryPojo>>(productCategoryList, HttpStatus.OK);
	}

	@RequestMapping(path = "/exportProductItemCsv", method = RequestMethod.GET)
	public String exportCsvFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir) throws IOException {

		try {
			File file = File.createTempFile("product-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			productListMaintenanceService.downloadCsvFileForProductList(response, file, SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Product Item is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ProductItem);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=productList.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();
			
		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productList";
		} 
		return "redirect:productList";
	}
	
	@RequestMapping(path = "/productItemTemplate", method = RequestMethod.GET)
	public void downloadProductItemTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			productListMaintenanceService.downloadProductItemTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error("Error while downloading product Item Template :" + e.getMessage(), e);
		}

	}

}
