package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NoRollBackException;
import com.privasia.procurehere.core.exceptions.WarningException;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.*;
import com.privasia.procurehere.service.supplier.SupplierAssociatedBuyerService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.NaicsCodesEditor;
import com.privasia.procurehere.web.editors.ProductItemEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;

/**
 * @author Javed Ahmed
 */
@Controller
@RequestMapping(value = "/buyer")
public class BuyerFavSupplierController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	ServletContext context;

	@Autowired
	SupplierService supplierService;

	@Autowired
	CountryService countryService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	NaicsCodesService naicsCodesService;

	@Autowired
	NaicsCodesEditor industryCategoryEditor;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	ProductItemEditor productItemEditor;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired

	SuppNotesDocUploadService suppNotesDocUploadService;

	@Autowired
	FavoriteSupplierService favouriteSupplierService;

	@Autowired

	FavoutireSupplierAuditService favSuppAuditService;

	@Autowired
	SupplierTagsService supplierTagsService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfxViewDao rfxViewDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	SupplierAssociatedBuyerService supplierAssociatedBuyerService;

	@Autowired
	SupplierFormService supplierFormService;

	@Autowired
	SupplierFormSubmissionService supplierFormSubmissionService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SupplierPerformanceEvaluationService supplierPerformanceEvaluationService;

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	ScoreRatingService scoreRatingService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(NaicsCodes.class, "industryCategoryVal", industryCategoryEditor);
		binder.registerCustomEditor(NaicsCodes.class, "industryCategory", industryCategoryEditor);
		binder.registerCustomEditor(NaicsCodes.class, industryCategoryEditor);
		binder.registerCustomEditor(List.class, "industryCategories", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					NaicsCodes group = naicsCodesService.getNaicsCodesById(id);
					return group;
				}
				return null;
			}
		});

		binder.registerCustomEditor(List.class, "projectIndustries", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					NaicsCodes group = naicsCodesService.getNaicsCodesById(id);
					return group;
				}
				return null;
			}
		});

		binder.registerCustomEditor(List.class, "productItem", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					ProductItem group = productListMaintenanceService.getProductCategoryById(id);
					return group;
				}
				return null;
			}
		});
	}

	@ModelAttribute("supplierFormStatusList")
	public List<SupplierFormSubmitionStatus> supplierFormStatusList() {
		return Arrays.asList(SupplierFormSubmitionStatus.PENDING, SupplierFormSubmitionStatus.ACCEPTED, SupplierFormSubmitionStatus.SUBMITTED);
	}

	@ModelAttribute("supplierFormSubStatusList")
	public List<SupplierFormSubmitionStatus> supplierFormSubStatusList() {
		return Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED);
	}

	@RequestMapping(value = "/importSupplier", method = RequestMethod.GET)
	public String importSupplier(Model model, Supplier supplier, SupplierSearchPojo supplierSearchPojo, FavouriteSupplier favouriteSupplier, HttpSession session, RedirectAttributes redir) throws ParseException {
		supplierSearchPojo = (SupplierSearchPojo) session.getAttribute("supplierSearchPojo");
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());

		if (supplierSearchPojo == null) {
			supplierSearchPojo = new SupplierSearchPojo();
		}

		if (StringUtils.checkString(supplierSearchPojo.getIndustryCategories()).length() > 0) {
			IndustryCategory ic = industryCategoryService.getIndustryCategorCodeAndNameById(supplierSearchPojo.getIndustryCategories());
			List<IndustryCategory> icList = new ArrayList<>();
			if (ic != null) {
				icList.add(ic);
			}
			model.addAttribute("industryCategories", icList);
		}

		if (StringUtils.checkString(supplierSearchPojo.getNaicsCode()).length() > 0) {
			NaicsCodes nc = naicsCodesService.getIndustryCategoryCodeAndNameById(supplierSearchPojo.getNaicsCode());
			List<NaicsCodes> ncList = new ArrayList<>();
			if (nc != null) {
				ncList.add(nc);
			}
			model.addAttribute("naicsCategories", ncList);
		}

		model.addAttribute("supplierSearchPojo", supplierSearchPojo);
		try {
			model.addAttribute("buyer", buyerService.findBuyerGeneralDetailsById(SecurityLibrary.getLoggedInUserTenantId()));
		} catch (Exception e) {
		}

		model.addAttribute("supplierFormList", supplierFormService.getSupplierFormListByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("favSupplierList", favoriteSupplierService.getAllSearchFavouriteSupplierByBuyerId(SecurityLibrary.getLoggedInUserTenantId(), null));
		model.addAttribute("supplierAssignFormPojo", new SupplierAssignFormPojo());

		/**
		 * TODO:: Check if below is realy required ??????? ----------------------------------------------------------
		 */
		List<NaicsCodesPojo> projectCategories = null; // industryCategoryService.getAllNaicsCodesPojo();
		model.addAttribute("projectCategories", projectCategories);
		model.addAttribute("formSupplier", new Supplier());
		model.addAttribute("favoriteSupplierObj", new FavouriteSupplier());
		/**
         * TODO: ---------------------------------------------------------------
		 */

		SupplierSearchPojo countPojo = favoriteSupplierService.getTotalAndPendingSupplierCountForBuyer(SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("pendingSupplierCount", countPojo.getPendingSupplierCount());
		model.addAttribute("totalSupplierCount", countPojo.getTotalSupplierCount());

		long pendingSuppCount = favoriteSupplierService.getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus.PENDING, SecurityLibrary.getLoggedInUserTenantId());
		long rejectedSuppCount = favoriteSupplierService.getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus.REJECTED, SecurityLibrary.getLoggedInUserTenantId());
		long suspendedSuppCount = favoriteSupplierService.getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus.SUSPENDED, SecurityLibrary.getLoggedInUserTenantId());
		long blacklistedSuppCount = favoriteSupplierService.getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus.BLACKLISTED, SecurityLibrary.getLoggedInUserTenantId());
		long activeSuppCount = favoriteSupplierService.getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus.ACTIVE, SecurityLibrary.getLoggedInUserTenantId());
		long totalGlobalSuppCount = favouriteSupplierService.getTotalSuppliersCountForBuyerGlobalList(SecurityLibrary.getLoggedInUserTenantId());
		long pendingSuppFormCount = supplierFormSubmissionService.findTotalFormByBuyerIdAnStatus(SecurityLibrary.getLoggedInUserTenantId(), Collections.singletonList(SupplierFormSubmitionStatus.PENDING));
		long submittedSuppFormCount = supplierFormSubmissionService.findTotalFormByBuyerIdAnStatus(SecurityLibrary.getLoggedInUserTenantId(), Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
		model.addAttribute("requestedPendingSuppCount", pendingSuppCount);
		model.addAttribute("requesteddRejectedSuppCount", rejectedSuppCount);
		model.addAttribute("suspendedSuppCount", suspendedSuppCount);
		model.addAttribute("blacklistedSuppCount", blacklistedSuppCount);
		model.addAttribute("activeSuppCount", activeSuppCount);
		model.addAttribute("totalGlobalSuppCount", totalGlobalSuppCount);
		model.addAttribute("pendingSuppFormCount", pendingSuppFormCount);
		model.addAttribute("submittedSuppFormCount", submittedSuppFormCount);

		return "importSupplier";
	}

	@ModelAttribute("statusList")
	public List<FavouriteSupplierStatus> getStatusList() {
		return Arrays.asList(FavouriteSupplierStatus.values());
	}

	@ModelAttribute("favSupplierstatusList")
	public List<String> getStatusListForFavSupplier() {
		List<String> favSupplierStatusList = new ArrayList<String>();
		favSupplierStatusList.add("ACTIVE");
		favSupplierStatusList.add("INACTIVE");
		return favSupplierStatusList;
	}

	@RequestMapping(path = "/addToList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ImportSupplierPojo> addToFavouriteList(@RequestBody ImportSupplierPojo importSupplierPojo, FavouriteSupplier favouriteSupplier) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (importSupplierPojo.getSaveOrUpdate().equals("Add to My List")) {
				Supplier supplier = supplierService.findSuppById(importSupplierPojo.getId());

				if (supplier != null && supplier.getStatus() != SupplierStatus.APPROVED) {
					headers.add("error", "Supplier cannot be added to your list since the supplier is " + supplier.getStatus());
					return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				LOG.info("inside create" + supplier.getId());

				favouriteSupplier.setSupplier(supplier);
				favouriteSupplier.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());

				favouriteSupplier.setCommunicationEmail(importSupplierPojo.getCommunicaionEmail());

				favouriteSupplier.setCompanyContactNumber(importSupplierPojo.getContactNumber());

				favouriteSupplier.setFavouriteSupplierTaxNumber(importSupplierPojo.getFavouriteSupplierTaxNumber());

				favouriteSupplier.setFullName(importSupplierPojo.getFullName());

				favouriteSupplier.setDesignation(importSupplierPojo.getDesignation());

				favouriteSupplier.setCreatedBy(SecurityLibrary.getLoggedInUser());

				favouriteSupplier.setCreatedDate(new Date());

				favouriteSupplier.setStatus(importSupplierPojo.getStatus());

				favouriteSupplier.setVendorCode(importSupplierPojo.getVendorCode());

				String[] IndCatIds = importSupplierPojo.getIndCat();
				List<String> IndCatIdList = Arrays.asList(IndCatIds);
				List<String> productCategory = Arrays.asList(importSupplierPojo.getProductCategory());
				if (IndCatIdList.size() > 0) {
					List<IndustryCategory> finalIndustryList = industryCategoryService.getAllIndustryCategoryOnlyByIds(IndCatIdList);
					favouriteSupplier.setIndustryCategory(finalIndustryList);
				} else {
					headers.add("error", "Please fill Industry Category of the supplier");
					return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (productCategory.size() > 0) {
					List<ProductCategory> finalIndustryList = industryCategoryService.getAllProductCategoryByIds(productCategory);
					favouriteSupplier.setProductCategory(finalIndustryList);
				} else {
					favouriteSupplier.setProductCategory(null);
				}
				List<SupplierTags> finalSupplierTags = new ArrayList<SupplierTags>();
				if (importSupplierPojo.getSupplierTags() != null) {
                    importSupplierPojo.getSupplierTags();
                    for (String tagIds : importSupplierPojo.getSupplierTags()) {
                        finalSupplierTags.add(new SupplierTags(tagIds));
                    }
                }
				favouriteSupplier.setSupplierTags(finalSupplierTags);

				// LOG.info("*****************doValidate(favouriteSupplier)******" + doValidate(favouriteSupplier));
				if (doValidate(favouriteSupplier)) {
					favouriteSupplier = favoriteSupplierService.saveFavoriteSupplier(favouriteSupplier, SecurityLibrary.getLoggedInUser());

					Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
					// Send email to supplier
					TimeZone timeZone = TimeZone.getDefault();
					String strTimerZone = buyerSettingsService.getBuyerTimeZoneByTenantId(buyer.getId());
					if (StringUtils.checkString(strTimerZone).length() > 0) {
						timeZone = TimeZone.getTimeZone(strTimerZone);
					}

					try {
						favoriteSupplierService.sendEmailToFavSupplier(supplier, buyer, timeZone);
					} catch (Exception e) {
						LOG.error("Error in sending email : " + e.getMessage(), e);
					}

					FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
					try {
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Added to My List");
						audit.setRemark(messageSource.getMessage("supplier.added.favlist", new Object[] {}, Global.LOCALE));
						audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						audit.setFavSupp(supplier);
						favSuppAuditService.saveFavouriteSupplierAudit(audit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, "Supplier '" + favouriteSupplier.getSupplier().getCompanyName() + "' added to Active List", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					headers.add("success", "Supplier added to your favorite list successfully");

					return new ResponseEntity<ImportSupplierPojo>(importSupplierPojo, headers, HttpStatus.OK);
				} else {
					LOG.info("*****************doValidate false");
				}
				headers.add("success", "Supplier already exists");
				return new ResponseEntity<ImportSupplierPojo>(importSupplierPojo, headers, HttpStatus.OK);

			} else {
				boolean isStatusChanged = false;

				LOG.info("inside update : " + importSupplierPojo.getId() + " Fav Supplier Id = " + importSupplierPojo.getId() + " : buyerId : " + SecurityLibrary.getLoggedInUserTenantId());
				String[] IndCatIds = importSupplierPojo.getIndCat();
				List<String> IndCatIdList = Arrays.asList(IndCatIds);
				List<String> productCategory = Arrays.asList(importSupplierPojo.getProductCategory());

				FavouriteSupplier favSupp = favoriteSupplierService.findFavSupplierByFavSuppId(importSupplierPojo.getId(), SecurityLibrary.getLoggedInUserTenantId());

				Supplier supplier = supplierService.findSuppById(favSupp.getSupplier().getId());

				if (supplier != null && supplier.getStatus() != SupplierStatus.APPROVED) {
					headers.add("error", "Cannot make changes to this supplier since the supplier is " + supplier.getStatus());
					return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				favSupp.setSupplier(supplier);
				favSupp.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				favSupp.setCommunicationEmail(importSupplierPojo.getCommunicaionEmail());
				favSupp.setCompanyContactNumber(importSupplierPojo.getContactNumber());
				favSupp.setFavouriteSupplierTaxNumber(importSupplierPojo.getFavouriteSupplierTaxNumber());
				favSupp.setFullName(importSupplierPojo.getFullName());
				favSupp.setDesignation(importSupplierPojo.getDesignation());

				// Spend analysis
				favSupp.setSubsidiary(importSupplierPojo.getSubsidiary());

				if (importSupplierPojo.getStatus() != null && favSupp.getStatus() != importSupplierPojo.getStatus()) {
					isStatusChanged = true;

					if (importSupplierPojo.getStatus() == FavouriteSupplierStatus.ACTIVE && supplier.getStatus() != SupplierStatus.APPROVED) {
						headers.add("error", "Cannot change this supplier status to ACTIVE since this supplier is " + supplier.getStatus());
						return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}

				}
				if (importSupplierPojo.getStatus() != null) {
					favSupp.setStatus(importSupplierPojo.getStatus());
				} else {
					favSupp.setStatus(favSupp.getStatus());
				}

				favSupp.setModifiedBy(SecurityLibrary.getLoggedInUser());
				favSupp.setModifiedDate(new Date());
				favSupp.setRatings(importSupplierPojo.getRatings());
				favSupp.setVendorCode(importSupplierPojo.getVendorCode());
				boolean auditTrailFlagStatus = favouriteSupplier.getStatus() == importSupplierPojo.getStatus();

                if (IndCatIdList.size() > 0) {
					List<IndustryCategory> finalIndustryList = industryCategoryService.getAllIndustryCategoryOnlyByIds(IndCatIdList);
					favSupp.setIndustryCategory(finalIndustryList);
				} else {
					headers.add("error", "Please fill all the details of the supplier");
					return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				if (productCategory.size() > 0) {
					List<ProductCategory> finalIndustryList = industryCategoryService.getAllProductCategoryByIds(productCategory);
					LOG.info("finalIndustryList length ::" + finalIndustryList.size());
					favSupp.setProductCategory(finalIndustryList);
				} else {
					favSupp.setProductCategory(null);
				}

				List<SupplierTags> finalSupplierTags = new ArrayList<SupplierTags>();
				if (importSupplierPojo.getSupplierTags() != null) {
                    importSupplierPojo.getSupplierTags();
                    for (String tagIds : importSupplierPojo.getSupplierTags()) {
                        finalSupplierTags.add(new SupplierTags(tagIds));
                    }
                }
				favSupp.setSupplierTags(finalSupplierTags);

				favSupp = favoriteSupplierService.updateFavoriteSupplier(favSupp, SecurityLibrary.getLoggedInUser(), auditTrailFlagStatus);

				if (isStatusChanged) {
					try {
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setFavSupp(supplier);
						if (favSupp.getStatus() == FavouriteSupplierStatus.ACTIVE) {
							audit.setDescription("Activated");
							audit.setRemark(messageSource.getMessage("supplier.auditstatus.active", new Object[] {}, Global.LOCALE));

						} else {
							audit.setDescription("Inactivated");
							audit.setRemark(messageSource.getMessage("supplier.auditstatus.inactive", new Object[] {}, Global.LOCALE));
						}
						audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}
				if (isStatusChanged) {
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(importSupplierPojo.getStatus() == FavouriteSupplierStatus.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, " Supplier'" + supplier.getCompanyName() + "' is '" + importSupplierPojo.getStatus() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}

				headers.add("success", "The supplier is updated successfully");
				return new ResponseEntity<ImportSupplierPojo>(importSupplierPojo, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error during Fav Supplier update : " + e.getMessage(), e);
			headers.add("error", "Please fill all the details of the supplier");
			return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	private boolean doValidate(FavouriteSupplier favouriteSupplier) {
		boolean validate = !favoriteSupplierService.isExists(favouriteSupplier);
        return validate;
	}

	@RequestMapping(path = "/searchSupplier", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> searchSupplierz(TableDataInput input, @RequestParam("searchSupplierId") String id, @RequestParam("searchCompanyName") String companyName, @RequestParam("searchCompanyRegistrationNumber") String companyRegistrationNumber, @RequestParam("searchOrder") String order, @RequestParam("globalSearch") Boolean globalSearch, @RequestParam("searchStatus") FavouriteSupplierStatus status, @RequestParam("searchIndustryCategories") String industryCategories, @RequestParam("searchNaicsCode") String naicsCode, @RequestParam("searchProjectName") String projectName, @RequestParam("registered") Boolean registered, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		try {
			SupplierSearchPojo supplierSearchPojo = new SupplierSearchPojo();
			boolean scheduleSupplierCount = true;
			long supplierCount = 0;
			supplierSearchPojo.setId(id);
			supplierSearchPojo.setCompanyName(companyName);
			supplierSearchPojo.setCompanyRegistrationNumber(companyRegistrationNumber);
			supplierSearchPojo.setGlobalSearch(globalSearch);
			supplierSearchPojo.setIndustryCategories(industryCategories);
			supplierSearchPojo.setNaicsCode(naicsCode);
			supplierSearchPojo.setOrder(order);
			supplierSearchPojo.setProjectName(projectName);
			supplierSearchPojo.setStatus(status);
			supplierSearchPojo.setRegistered(registered);
			LOG.info("Search : " + supplierSearchPojo);
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("status")) {
						if (cp.getSearch().getValue().equals("SCHEDULED")) {
							supplierCount = favoriteSupplierService.getScheduledSupplier(SecurityLibrary.getLoggedInUserTenantId());
							scheduleSupplierCount = false;
						}
					}
				}
			}
			List<SupplierPojo> searchList = favoriteSupplierService.searchSuppliers(input, supplierSearchPojo, SecurityLibrary.getLoggedInUserTenantId());
			List<IndustryCategoryPojo> pojoList = favoriteSupplierService.getTopTwentyFiveCategory(SecurityLibrary.getLoggedInUserTenantId());
			session.setAttribute("industryCategories", pojoList);
			// List<Supplier> list = new ArrayList<Supplier>();
			// if (CollectionUtil.isNotEmpty(searchList)) {
			// for (SupplierPojo supplier : searchList) {
			// supplier.setFavSupplierStatus(status.name());
			// FavouriteSupplier favSupplier =
			// favoriteSupplierService.getFavouriteSupplierBySupplierId(supplier.getId(),
			// SecurityLibrary.getLoggedInUserTenantId());
			// if (favSupplier != null) {
			// supplier.setActiveInactive(favSupplier.getStatus());
			// }
			// list.add(supplier.createShallowCopyWithCountry());
			// }
			// }

			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			if (scheduleSupplierCount) {
				supplierCount = favoriteSupplierService.searchSuppliersCount(input, supplierSearchPojo, SecurityLibrary.getLoggedInUserTenantId());
			}
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			// set search parameter in seesion
			session.setAttribute("supplierSearchPojo", supplierSearchPojo);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Global Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching Global Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/addAll", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> AddAll(@RequestBody String[] idSupplier, FavouriteSupplier favouriteSupplier) {
		HttpHeaders headers = new HttpHeaders();
		try {
			for (String element : idSupplier) {

				Supplier supplier = supplierService.findSuppById(element);
				favouriteSupplier.setSupplier(supplier);
				favouriteSupplier.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				favouriteSupplier.setCommunicationEmail(supplier.getCommunicationEmail());
				favouriteSupplier.setCompanyContactNumber(supplier.getCompanyContactNumber());
				favouriteSupplier.setFullName(supplier.getFullName());
				favouriteSupplier.setDesignation(supplier.getDesignation());

				if (doValidate(favouriteSupplier)) {
					favoriteSupplierService.saveFavoriteSupplier(favouriteSupplier, SecurityLibrary.getLoggedInUser());

				} else {
					// Add update functionality here
					LOG.info("supplier already exists");
				}

			}
			return new ResponseEntity<String>("success", headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("error", headers, HttpStatus.EXPECTATION_FAILED);
		}

	}

	@RequestMapping(value = "searchIndustryCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchIndustryCategory(@RequestParam("search") String search) {
		List<IndustryCategory> icList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, SecurityLibrary.getLoggedInUserTenantId());
		return new ResponseEntity<List<IndustryCategory>>(icList, HttpStatus.OK);
	}

	// working with dialog
	@RequestMapping(value = "searchIndustryCategories", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchIndustryCategories(@RequestParam("search") String search) {
		LOG.info("inside search ic dialog:" + search);
		List<IndustryCategory> icList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, SecurityLibrary.getLoggedInUserTenantId());
		return new ResponseEntity<List<IndustryCategory>>(icList, HttpStatus.OK);
	}

	// working with dialog
	@RequestMapping(value = "searchProductCategories", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProductCategory>> searchProductCategories(@RequestParam("q") String search) {
		LOG.info("inside search ic dialog:" + search);
		List<ProductCategory> icList = productCategoryMaintenanceService.serchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), search);
		for (ProductCategory productCategory : icList) {
			productCategory.setCreatedBy(null);
			productCategory.setBuyer(null);
			productCategory.setModifiedBy(null);
		}
		return new ResponseEntity<List<ProductCategory>>(icList, HttpStatus.OK);
	}

	@ModelAttribute("searchSupplierTags")
	public List<SupplierTags> getsearchSupplierTags() {
		List<SupplierTags> icList = supplierTagsService.searchAllActiveSupplierTagsForTenant(SecurityLibrary.getLoggedInUserTenantId());
		return icList;
	}

	@RequestMapping(value = "searchNaicsCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<NaicsCodes>> searchNaicsCategory(@RequestParam("search") String search) {
		List<NaicsCodes> naicsList = naicsCodesService.searchForCategories(search);
		return new ResponseEntity<List<NaicsCodes>>(naicsList, HttpStatus.OK);
	}

	@RequestMapping(value = "/supplierDetailsOfGlobalSupplier/{suppId}", method = RequestMethod.GET)
	public String supplierDetailsOfGlobalSupplier(@PathVariable(name = "suppId") String supplierId, Model model) throws JsonProcessingException {

		Supplier supplier = supplierService.findSupplierById(supplierId);
		boolean isFavSupplier = false;
		if (SecurityLibrary.getLoggedInUser().getTenantType() == TenantType.BUYER) {
			FavouriteSupplier favSupp = favoriteSupplierService.getFavouriteSupplierBySupplierId(supplierId, SecurityLibrary.getLoggedInUser().getBuyer().getId());
			if (favSupp != null) {
				if (favSupp.getStatus() == FavouriteSupplierStatus.ACTIVE) {
					isFavSupplier = true;
				}
				supplier.setCommunicationEmail(favSupp.getCommunicationEmail());
				supplier.setFullName(favSupp.getFullName());
				supplier.setCompanyContactNumber(favSupp.getCompanyContactNumber());
			}
			RequestedAssociatedBuyer associatedBuyer = supplierService.findSupplierRequestBySupplierAndBuyerId(supplierId, SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("supplierRequest", associatedBuyer);
			if (associatedBuyer != null && favSupp != null) {
				SupplierFormSubmition supplierFormSubmition = supplierFormSubmissionService.findOnboardingFormSubmitionByFavSuppIdAndBuyerId(favSupp.getId(), associatedBuyer.getBuyer().getId());
				model.addAttribute("supplierFormSubmition", supplierFormSubmition);
			}
			List<String> categories = new ArrayList<>();
			if (associatedBuyer != null) {
				if (CollectionUtil.isNotEmpty(associatedBuyer.getIndustryCategory())) {
					for (IndustryCategory catrgory : associatedBuyer.getIndustryCategory()) {
						categories.add(catrgory.getId() + '-' + catrgory.getCode() + '-' + catrgory.getName() + '*');
					}
				}
			}
			model.addAttribute("supplierRequestIndCat", categories);
		}
		if (supplier != null && CollectionUtil.isNotEmpty(supplier.getCountries())) {
			List<Coverage> coverages = new ArrayList<Coverage>();
			for (Country country : supplier.getCountries()) {
				Coverage coverage = new Coverage();
				coverage.setCode(country.getCountryCode());
				coverage.setName(country.getCountryName());
				if (CollectionUtil.isNotEmpty(supplier.getStates())) {
					List<Coverage> childs = new ArrayList<Coverage>();
					for (State state : supplier.getStates()) {
						if (coverage.getCode().equals(state.getCountry().getCountryCode())) {
							Coverage sta = new Coverage();
							sta.setCode(state.getStateCode());
							sta.setName(state.getStateName());
							childs.add(sta);
						}
					}
					coverage.setChildren(childs);
				}
				coverages.add(coverage);
			}
			supplier.setCoverages(coverages);
		}

		long totalEventInvited = favoriteSupplierService.totalEventInvitedSupplier(supplierId, null);
		long totalEventParticipated = favoriteSupplierService.totalEventParticipatedSupplier(supplierId, null);
		long totalEventAwarded = rfxViewDao.totalEventAwardedSupplier(supplierId);

		LOG.info("totalEventInvited :" + totalEventInvited + ",  totalEventParticipated :" + totalEventParticipated + ",  totalEventAwarded :" + totalEventAwarded);
		long totalMyEventInvited = favoriteSupplierService.totalEventInvitedSupplier(supplierId, SecurityLibrary.getLoggedInUserTenantId());
		long totalMyEventParticipated = favoriteSupplierService.totalEventParticipatedSupplier(supplierId, SecurityLibrary.getLoggedInUserTenantId());
		long totalMyEventAwarded = rfxViewDao.totalEventAwardedSupplierAndBuyer(supplierId, SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("totalEventInvited", totalEventInvited);
		model.addAttribute("totalEventParticipated", totalEventParticipated);
		model.addAttribute("totalEventAwarded", totalEventAwarded);

		model.addAttribute("totalMyEventInvited", totalMyEventInvited);
		model.addAttribute("totalMyEventParticipated", totalMyEventParticipated);
		model.addAttribute("totalMyEventAwarded", totalMyEventAwarded);

		model.addAttribute("notesObject", new Notes());
		model.addAttribute("financialDocuments", supplier.getSupplierFinancialDocuments());
		model.addAttribute("organisationalDetails", supplier.getSupplierBoardOfDirectors());
		model.addAttribute("supplier", supplier);
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		model.addAttribute("supplierFormList", supplierFormService.getSupplierFormListByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("isFavSupplier", isFavSupplier);

		SupplierPerformanceForm spf = supplierPerformanceEvaluationService.findScoreRatingForSupplier(supplierId);
		LOG.info(" Supplier Performance " + spf);
		model.addAttribute("spf", spf);
		// model.addAttribute("supplierOtherCredentials", new
		// SupplierOtherCredentials());
		/*
		 * if(SecurityLibrary.getLoggedInUser().getTenantType() == TenantType.OWNER) { } else
		 * if(SecurityLibrary.getLoggedInUser().getTenantType() == TenantType.BUYER) { }
		 */
		List<NotesPojo> noteList = supplierService.notesForSupplier(supplier.getId(), SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("noteList", mapper.writeValueAsString(noteList));
		// model.addAttribute("noteList", noteList);
		model.addAttribute("eventAudit", favSuppAuditService.getAuditByTenantId(SecurityLibrary.getLoggedInUserTenantId(), supplierId));

		SupplierPerformanceEvaluationPojo overallScoreByBuyer;
		try {
			overallScoreByBuyer = supplierPerformanceEvaluationService.getSPFDetailsForBuyerByTenantId(SecurityLibrary.getLoggedInUserTenantId(), supplierId, null, null);
			model.addAttribute("overallScoreByBuyer", overallScoreByBuyer);
		} catch (ApplicationException e) {
			LOG.error("Error while getting overallScoreByBuyer " + e.getMessage(), e);
		}
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));

		List<SupplierPerformanceEvaluationPojo> sumOfOverallScoreByBU = supplierPerformanceEvaluationService.getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(SecurityLibrary.getLoggedInUserTenantId(), supplierId, null, null);
		model.addAttribute("sumOfOverallScoreByBU", sumOfOverallScoreByBU);

		List<SupplierPerformanceEvaluationPojo> overallScoreBySpForm = supplierPerformanceEvaluationService.getOverallScoreBySpFormAndBUnit(null, null, null, supplierId, SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("overallScoreBySpForm", overallScoreBySpForm);

		List<SupplierPerformanceEvaluationPojo> overallScoreByCriteria = supplierPerformanceEvaluationService.getOverallScoreByCriteriaAndFormId(null, null, null, supplierId, SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("overallScoreByCriteria", overallScoreByCriteria);

		BigDecimal totalOfOverallScore = BigDecimal.ZERO;
		for (SupplierPerformanceEvaluationPojo form : overallScoreByCriteria) {
			if (form.getOverallScore() != null) {
				totalOfOverallScore = totalOfOverallScore.add(form.getOverallScore());
			}
		}
		BigDecimal total = BigDecimal.valueOf(overallScoreByCriteria.size());
		if ((total.compareTo(BigDecimal.ZERO) > 0)) {
			totalOfOverallScore = totalOfOverallScore.divide(total, 0, RoundingMode.HALF_UP);
		}

		ScoreRating scoreRating1 = scoreRatingService.getScoreRatingForScoreAndTenant(SecurityLibrary.getLoggedInUserTenantId(), totalOfOverallScore);
		model.addAttribute("formIdTotalOfOverallScore", totalOfOverallScore);
		if (scoreRating1 != null) {
			model.addAttribute("formIdRating", scoreRating1.getRating());
			model.addAttribute("formIdRatDesc", scoreRating1.getDescription());
		}
		List<SupplierPerformanceEvaluationPojo> formIdList = supplierPerformanceFormService.getSpFormIdListForSupplierIdAndTenantId(null, null, supplierId, SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("formIdList", formIdList);

		return "supplierDetails";
	}

	@RequestMapping(path = "/removeFavouriteSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ImportSupplierPojo> RemoveFavouriteSupplier(@RequestBody ImportSupplierPojo importSupplierPojo, FavouriteSupplier favouriteSupplier) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();

		LOG.info("fav supp to be deleated ::: " + importSupplierPojo.getId() + " Buyer  : " + SecurityLibrary.getLoggedInUserTenantId());
		FavouriteSupplier favSupp = favoriteSupplierService.findFavSupplierBySuppId(importSupplierPojo.getId(), SecurityLibrary.getLoggedInUserTenantId());
		Supplier supplier = favoriteSupplierService.getSupplierByFavSupplierId(favSupp.getId());
		FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
		audit.setFavSupp(supplier);
		try {
			favoriteSupplierService.deleteFavoriteSupplier(favSupp, SecurityLibrary.getLoggedInUser());
			try {
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription(favSupp.getFullName() + " is removed from favourite supplier list");
				audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				favSuppAuditService.saveFavouriteSupplierAudit(audit);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			headers.add("success", "Supplier deleted from your favorite list successfully");
			return new ResponseEntity<ImportSupplierPojo>(importSupplierPojo, headers, HttpStatus.OK);
		} catch (ApplicationException ae) {
			headers.add("error", ae.getMessage());
			return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			headers.add("error", "Cannot delete this supplier as this supplier is linked to other places");
			return new ResponseEntity<ImportSupplierPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "changeListOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Supplier>> changeListOrder(@RequestBody SearchVo searchVo) {

		List<Supplier> orderedList = favoriteSupplierService.searchSupplierInFavByOder(searchVo);
		if (CollectionUtil.isNotEmpty(orderedList)) {
			for (Supplier supp : orderedList) {
				supp.getRegistrationOfCountry().setCreatedBy(null);
			}
		}
		return new ResponseEntity<List<Supplier>>(orderedList, HttpStatus.OK);
	}

	@RequestMapping(path = "/editFavSupp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<FavouriteSupplier> editFavSupp(@RequestBody ImportSupplierPojo importSupplierPojo, FavouriteSupplier favouriteSupplier, HttpSession session) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		FavouriteSupplier favSupp = null;
		LOG.info("Supplier id  editFavSupp :::" + importSupplierPojo.getId());
		try {
			favSupp = favoriteSupplierService.findFavSupplierBySuppId(importSupplierPojo.getId(), SecurityLibrary.getLoggedInUserTenantId());
			// Remove the Created and Modified By associations to avoid lazy
			// init exception by JSON serialization
			if (CollectionUtil.isNotEmpty(favSupp.getIndustryCategory())) {
				for (IndustryCategory ic : favSupp.getIndustryCategory()) {
					ic.setCreatedBy(null);
					ic.setModifiedBy(null);
				}
			}

			if (CollectionUtil.isNotEmpty(favSupp.getProductCategory())) {
				for (ProductCategory ic : favSupp.getProductCategory()) {
					ic.setCreatedBy(null);
					ic.setModifiedBy(null);
				}
			}

			if (CollectionUtil.isNotEmpty(favSupp.getSupplierTags())) {
				for (SupplierTags ic : favSupp.getSupplierTags()) {
					ic.setCreatedBy(null);
					ic.setModifiedBy(null);
				}
			}

			favSupp.getSupplier().setPassword(null);
		} catch (Exception e) {
			LOG.error("Cannot fetch Favourite supplier details : " + e.getMessage(), e);
			headers.add("error", "Cannot fetch Favourite supplier details : " + e.getMessage());
			return new ResponseEntity<FavouriteSupplier>(favSupp, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
        favSupp.setIsFutureSuspended(favSupp.getIsFutureSuspended() && favSupp.getSuspendStartDate() != null && favSupp.getStatus() == FavouriteSupplierStatus.ACTIVE);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		String startDate = null;
		String endDate = null;
		if (favSupp.getSuspendStartDate() != null) {
			startDate = sdf.format(favSupp.getSuspendStartDate());
		}
		if (favSupp.getSuspendEndDate() != null) {
			endDate = sdf.format(favSupp.getSuspendEndDate());
		}

		String suspendDuration = startDate + " - " + endDate;
		favSupp.setSuspensionDuration(suspendDuration);
		return new ResponseEntity<FavouriteSupplier>(favSupp, headers, HttpStatus.OK);
	}

	// working with dialog
	@RequestMapping(value = "searchProductItems", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProductItem>> searchProductItems(@RequestParam("search") String search) {
		LOG.info("inside search pi dialog:" + search);
		List<ProductItem> productList = productListMaintenanceService.findProductsByNameAndTenantAndFavSupplier(search, SecurityLibrary.getLoggedInUserTenantId(), null);
		for (ProductItem productItem : productList) {
			productItem.setUom(null);
			productItem.setCreatedBy(null);
			productItem.setModifiedBy(null);
		}
		return new ResponseEntity<List<ProductItem>>(productList, HttpStatus.OK);
	}

	@RequestMapping(value = "/uploadSupplierList", method = RequestMethod.POST)
	public ResponseEntity<Void> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("isUploadNewSupplier") boolean isUploadNewSupplier) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {

				validateUploadSupplierList(file);
				try {
					supplierListUpload(file, SecurityLibrary.getLoggedInUserTenantId(), isUploadNewSupplier);
				} catch (ApplicationException e) {
					LOG.error("Error creating and Uploading Supplier instance : " + e.getMessage(), e);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<Void>(headers, HttpStatus.BAD_REQUEST);
				} catch (NoRollBackException e) {
					LOG.error("Error creating and Uploading Supplier instance : " + e.getMessage(), e);
					headers.add("error", "supplier already exists");
					return new ResponseEntity<Void>(headers, HttpStatus.BAD_REQUEST);
				} catch (Exception e) {
					LOG.error("Error creating and Uploading Supplier instance : " + e.getMessage(), e);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<Void>(headers, HttpStatus.BAD_REQUEST);
				}

			}
		} catch (MultipartException e) {
			LOG.error("Error MultipartException while creating Supplier instance : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Void>(headers, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:createEventBQ/" + eventId + "/" + bqId;
		headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "Supplier List" }, Global.LOCALE));
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	private void validateUploadSupplierList(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
	}

	protected void supplierListUpload(MultipartFile file, String tenantId, boolean isUploadNewSupplier) throws IOException, ExcelParseException, ApplicationException, NoRollBackException, WarningException {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);
		favoriteSupplierService.supplierListUpload(tenantId, convFile, isUploadNewSupplier);
		try {
			if (convFile != null) {
				convFile.delete();
			}
		} catch (Exception e) {
			LOG.error("Error creating Supplier instance : " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/SupplierTemplate", method = RequestMethod.GET)
	public void downloader(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "SupplierTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			eventDownloader(workbook);

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
			LOG.error("Error while downloading SupplierTemplate :: " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/ExportSupplier", method = RequestMethod.POST)
	public void downloadSupplierList(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String poIds, boolean select_all, @ModelAttribute("searchFilterSupplierPojo") SearchFilterSupplierPojo searchFilterSupplierPojo) throws IOException {
		LOG.info("downloadSupplierList is called here " + searchFilterSupplierPojo.toString());
		try {
			String[] favArr = null;
			if (StringUtils.checkString(poIds).length() > 0) {
				favArr = poIds.split(",");
			}
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ExportSupplier.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() == 0 && StringUtils.checkString(searchFilterSupplierPojo.getCusStatus()).length() > 0) {
				searchFilterSupplierPojo.setStatus(searchFilterSupplierPojo.getCusStatus());
			}
			supplierDownloader(workbook, favArr, select_all, searchFilterSupplierPojo);

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
			LOG.error("Error while downloading Supplier List :: " + e.getMessage(), e);
		}
	}

	protected void eventDownloader(XSSFWorkbook workbook) throws IOException {
		XSSFSheet sheet = workbook.createSheet("Supplier List");
		// int r = 1;
		buildHeader(workbook, sheet);

		XSSFSheet lookupSheet3 = workbook.createSheet("PCLOOKUP");
		List<ProductCategory> productcategory = productCategoryMaintenanceService.getAllProductCategoryByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		int index3 = 0;
		for (ProductCategory pc : productcategory) {
			String id = pc.getId();
			String code = pc.getProductCode() + "-" + pc.getProductName();
			String name = pc.getProductName();
			XSSFRow row = lookupSheet3.createRow(index3++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(id);
			XSSFCell cell2 = row.createCell(1);
			cell2.setCellValue(code);
			XSSFCell cell3 = row.createCell(2);
			cell3.setCellValue(name);
		}
		XSSFSheet lookupSheet4 = workbook.createSheet("STATUS");
		for (int i = 0; i <= 1; i++) {
			XSSFRow row = lookupSheet4.createRow(i++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue("ACTIVE");
			cell1.setCellValue("INACTIVE");
		}
		XSSFSheet lookupSheet1 = workbook.createSheet("ICLOOKUP");

		List<IndustryCategory> ic = industryCategoryService.findIndustryCategoryByNameAndTenantId("", SecurityLibrary.getLoggedInUserTenantId());
		int index1 = 0;
		for (IndustryCategory u : ic) {
			String id = u.getId();
			String code = u.getCode() + "-" + u.getName();
			String name = u.getName();
			XSSFRow row = lookupSheet1.createRow(index1++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(id);
			XSSFCell cell2 = row.createCell(1);
			cell2.setCellValue(code);
			XSSFCell cell3 = row.createCell(2);
			cell3.setCellValue(name);
		}
		XSSFSheet lookupSheet2 = workbook.createSheet("COUNTRYLOOKUP");
		int index2 = 0;
		List<Country> country = countryService.getAllCountriesOrderByCountryCode();
		for (Country contry : country) {
			String id = contry.getId();
			String code = contry.getCountryCode();
			String name = contry.getCountryName();
			XSSFRow row = lookupSheet2.createRow(index2++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(id);
			XSSFCell cell2 = row.createCell(1);
			cell2.setCellValue(code);
			XSSFCell cell3 = row.createCell(2);
			cell3.setCellValue(name);
		}
		XSSFSheet lookupSheet5 = workbook.createSheet("STLOOKUP");
		List<SupplierTags> supplierTags = supplierTagsService.searchAllActiveSupplierTagsForTenant(SecurityLibrary.getLoggedInUserTenantId());
		int index4 = 0;
		for (SupplierTags st : supplierTags) {
			String id = st.getId();
			String code = st.getSupplierTags();
			XSSFRow row = lookupSheet5.createRow(index4++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(id);
			XSSFCell cell2 = row.createCell(1);
			cell2.setCellValue(code);

		}
		XSSFDataFormat fmt = workbook.createDataFormat();
		CellStyle textStyle = workbook.createCellStyle();
		textStyle.setDataFormat(fmt.getFormat("@"));
		sheet.setDefaultColumnStyle(5, textStyle);
		sheet.setDefaultColumnStyle(6, textStyle);
		sheet.setDefaultColumnStyle(8, textStyle);
		sheet.setDefaultColumnStyle(9, textStyle);

		// Comment box for Login Email
		CreationHelper factory = workbook.getCreationHelper();
		XSSFRow row = sheet.getRow(0);
		XSSFCell cell = row.getCell(7);
		// cell.setCellValue("H1");

		XSSFDrawing drawing = sheet.createDrawingPatriarch();

		// When the comment box is visible, have it show in a 1x3 space
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setCol1(cell.getColumnIndex());
		anchor.setCol2(cell.getColumnIndex() + 2);
		anchor.setRow1(row.getRowNum());
		anchor.setRow2(row.getRowNum() + 3);

		// Create the comment and set the text+author
		XSSFComment comment = drawing.createCellComment(anchor);
		RichTextString str = factory.createRichTextString("Login Email will be treated as Communication Email.");
		comment.setString(str);

		// Assign the comment to the cell
		cell.setCellComment(comment);

		// Product Category
		XSSFDataValidationHelper productCategoryvalidationHelper = new XSSFDataValidationHelper(lookupSheet3);
		XSSFDataValidationConstraint pcconstraint = (XSSFDataValidationConstraint) productCategoryvalidationHelper.createFormulaListConstraint("'PCLOOKUP'!$B$1:$B$" + (productcategory.size() + 1));
		// DVConstraint pcconstraint =
		// DVConstraint.createFormulaListConstraint("'PCLOOKUP'!$B$1:$B$" +
		// (ic.size() + 1));
		CellRangeAddressList pcaddressList = new CellRangeAddressList(1, 1000, 10, 10);

		XSSFDataValidation pcvalidation = (XSSFDataValidation) productCategoryvalidationHelper.createValidation(pcconstraint, pcaddressList);
		pcvalidation.setSuppressDropDownArrow(true);
		pcvalidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		pcvalidation.createErrorBox("Invalid Product Category code Selected", "Please select code from the list");
		pcvalidation.createPromptBox("Product Category List", "Select Product Category code from the list provided. It has been exported from your master data.");
		pcvalidation.setShowPromptBox(true);
		pcvalidation.setShowErrorBox(true);
		sheet.addValidationData(pcvalidation);

		// supplier tags
		XSSFDataValidationHelper supplierTgasvalidationHelper = new XSSFDataValidationHelper(lookupSheet5);
		XSSFDataValidationConstraint stconstraint = (XSSFDataValidationConstraint) supplierTgasvalidationHelper.createFormulaListConstraint("'STLOOKUP'!$B$1:$B$" + (supplierTags.size() + 1));
		// DVConstraint pcconstraint =
		// DVConstraint.createFormulaListConstraint("'PCLOOKUP'!$B$1:$B$" +
		// (ic.size() + 1));
		CellRangeAddressList staddressList = new CellRangeAddressList(1, 1000, 15, 15);

		XSSFDataValidation stvalidation = (XSSFDataValidation) supplierTgasvalidationHelper.createValidation(stconstraint, staddressList);
		stvalidation.setSuppressDropDownArrow(true);
		stvalidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		stvalidation.createErrorBox("Invalid Supplier Tgas Selected", "Please select Supplier Tgas from the list");
		stvalidation.createPromptBox("Supplier Tgas List", "Select Supplier Tgas from the list provided. It has been exported from your master data.");
		stvalidation.setShowPromptBox(true);
		stvalidation.setShowErrorBox(true);
		sheet.addValidationData(stvalidation);

		// Status

		XSSFDataValidationHelper validationHelperStatus = new XSSFDataValidationHelper(lookupSheet4);
		XSSFDataValidationConstraint constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createFormulaListConstraint("'STATUS'!$B$1:$B$" + (3));
		constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createExplicitListConstraint(new String[] { "ACTIVE", "INACTIVE" });
		CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1000, 12, 12);
		XSSFDataValidation validationStatus = (XSSFDataValidation) validationHelperStatus.createValidation(constraintStatus, cellRangeAddressList);
		validationStatus.setSuppressDropDownArrow(true);
		validationStatus.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validationStatus.createErrorBox("Invalid Status Selected", "Please select Status from the list");
		validationStatus.createPromptBox("Status List", "Select Status from the list provided. It has been exported from your master data.");
		validationStatus.setShowPromptBox(true);
		validationStatus.setShowErrorBox(true);

		sheet.addValidationData(validationStatus);

		// Industry Category
		XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(lookupSheet1);
		XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'ICLOOKUP'!$B$1:$B$" + (ic.size() + 1));

		// DVConstraint constraint =
		// DVConstraint.createFormulaListConstraint("'ICLOOKUP'!$B$1:$B$" +
		// (ic.size() + 1));
		CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 9, 9);

		XSSFDataValidation validation = (XSSFDataValidation) validationHelper.createValidation(constraint, addressList);
		validation.setSuppressDropDownArrow(true);

		sheet.addValidationData(validation);

		// Country Code
		XSSFDataValidationHelper countryValidationHelper = new XSSFDataValidationHelper(lookupSheet2);
		// DVConstraint priceConstraint =
		// DVConstraint.createFormulaListConstraint("'COUNTRYLOOKUP'!$B$1:$B$" +
		// (country.size() + 1));
		XSSFDataValidationConstraint countryConstraint = (XSSFDataValidationConstraint) countryValidationHelper.createFormulaListConstraint("'COUNTRYLOOKUP'!$B$1:$B$" + (country.size() + 1));
		CellRangeAddressList priceaddressList = new CellRangeAddressList(1, 1000, 1, 1);
		XSSFDataValidation countryValidation = (XSSFDataValidation) countryValidationHelper.createValidation(countryConstraint, priceaddressList);
		// HSSFDataValidation pricevalidation = new
		// HSSFDataValidation(priceaddressList, priceConstraint);
		countryValidation.setSuppressDropDownArrow(true);
		countryValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		countryValidation.createErrorBox("Invalid Country Code Selected", "Please select Country Code from the list");
		countryValidation.createPromptBox("Country Code List", "Select Country Code from the list provided. It has been exported from your master data.");
		countryValidation.setShowPromptBox(true);
		countryValidation.setShowErrorBox(true);
		sheet.addValidationData(countryValidation);
		workbook.setSheetHidden(1, true);
		workbook.setSheetHidden(2, true);
		workbook.setSheetHidden(3, true);
		for (int i = 0; i < 16; i++) {
			sheet.autoSizeColumn(i, true);
		}
	}

	protected void supplierDownloader(XSSFWorkbook workbook, String[] favArr, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo) throws Exception {

		XSSFSheet sheet = workbook.createSheet("Supplier List");
		int r = 1;
		buildHeaderDownloadSupplier(workbook, sheet);
		List<FavouriteSupplier> favSuppList = favoriteSupplierService.getAllFavouriteSupplierById(favArr, SecurityLibrary.getLoggedInUserTenantId(), select_all, searchFilterSupplierPojo);
		if (CollectionUtil.isNotEmpty(favSuppList)) {
			for (FavouriteSupplier favSupp : favSuppList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getCompanyName() != null ? favSupp.getSupplier().getCompanyName() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getRegistrationOfCountry() != null ? favSupp.getSupplier().getRegistrationOfCountry().getCountryCode() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getCompanyRegistrationNumber() != null ? favSupp.getSupplier().getCompanyRegistrationNumber() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getFullName() != null ? favSupp.getFullName() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getDesignation() != null ? favSupp.getDesignation() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getMobileNumber() != null ? favSupp.getSupplier().getMobileNumber() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getCompanyContactNumber() != null ? favSupp.getCompanyContactNumber() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getLoginEmail() != null ? favSupp.getSupplier().getLoginEmail() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getFaxNumber() != null ? favSupp.getSupplier().getFaxNumber() : "");

				Cell cell = row.createCell(cellNum++);
				Cell cell1 = row.createCell(cellNum++);

				row.createCell(cellNum++).setCellValue(favSupp.getVendorCode() != null ? favSupp.getVendorCode() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getStatus() != null ? favSupp.getStatus().toString() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getRatings() != null ? favSupp.getRatings().toString() : "");
				List<IndustryCategory> industryCategory = favSupp.getIndustryCategory();
				List<String> inCat = new ArrayList<>();
				String seprator = " , ";
				if (CollectionUtil.isNotEmpty(industryCategory)) {
					for (IndustryCategory ic : industryCategory) {
						inCat.add(ic.getCode() + "-" + ic.getName());
					}
				}
				cell.setCellValue(String.join(seprator, inCat));

				List<ProductCategory> productCategory = favSupp.getProductCategory();
				List<String> pcat = new ArrayList<>();
				String seprator1 = " , ";
				if (CollectionUtil.isNotEmpty(productCategory)) {
					for (ProductCategory productcategory : productCategory) {
						pcat.add(productcategory.getProductCode() + "-" + productcategory.getProductName());
					}
				}
				cell1.setCellValue(String.join(seprator1, pcat));

				row.createCell(cellNum++).setCellValue(favSupp.getCommunicationEmail() != null ? favSupp.getCommunicationEmail() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getYearOfEstablished() != null ? favSupp.getSupplier().getYearOfEstablished().toString() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getRegistrationCompleteDate() != null ? new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(favSupp.getSupplier().getRegistrationCompleteDate()) : "");
				Cell cell2 = row.createCell(cellNum++);
				List<SupplierTags> supplierTags = favSupp.getSupplierTags();
				List<String> stcat = new ArrayList<>();
				String seprator3 = " , ";
				if (CollectionUtil.isNotEmpty(supplierTags)) {
					for (SupplierTags suppTags : supplierTags) {
						stcat.add(suppTags.getSupplierTags());
					}
				}

				cell2.setCellValue(String.join(seprator3, stcat));
			}
		}
		for (int k = 0; k < 18; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildHeaderDownloadSupplier(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.DOWNLOAD_SUPPLIER_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.SUPPLIER_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@RequestMapping(path = "/suppBlackUploadDocument", method = RequestMethod.POST)
	public String suppBlackUploadDocument(@RequestParam(name = "docs", required = false) MultipartFile[] file, @RequestParam(name = "docDescription", required = false) String[] desc, @RequestParam("suppId") String supplierId, @RequestParam("blackListRemark") String blackListRemark, Model model, RedirectAttributes redir) {
		FavouriteSupplier favSupplier = favoriteSupplierService.findFavSupplierByFavSuppId(supplierId, SecurityLibrary.getLoggedInUser().getTenantId());
		if (favSupplier == null) {
			redir.addFlashAttribute("error", "Add supplier to Favourite List first.");
			return "redirect:/buyer/importSupplier";
		}

		Supplier supplier = favoriteSupplierService.getSupplierByFavSupplierId(favSupplier.getId());
		for (int i = 0; i < file.length; i++) {
			for (int j = 0; j < desc.length; j++) {
				if (i == j) {
					if (file[i] != null) {
						if (!file[i].isEmpty()) {
							try {
								String fileName = file[i].getOriginalFilename();
								byte[] bytes = file[i].getBytes();
								SupplierNoteDocument notesDoc = new SupplierNoteDocument();
								notesDoc.setCredContentType(file[i].getContentType());
								notesDoc.setDescription(desc[i]);
								notesDoc.setFileName(fileName);
								notesDoc.setFileData(bytes);
								notesDoc.setUploadDate(new Date());
								notesDoc.setUploadTenantId(SecurityLibrary.getLoggedInUserTenantId());
								notesDoc.setCreatedBy(SecurityLibrary.getLoggedInUser());
								if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getBuyer() != null) {
									notesDoc.setTenantType(TenantType.BUYER);
								} else if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getOwner() != null) {
									notesDoc.setTenantType(TenantType.OWNER);
								}
								notesDoc.setSupplier(supplierService.findSuppById(favSupplier.getSupplier().getId()));
								notesDoc.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
								SupplierNoteDocument docs = suppNotesDocUploadService.saveOrUpdate(notesDoc);
								LOG.info(docs.getDescription());
							} catch (Exception e) {
								redir.addFlashAttribute("error", "Error while uploading documents");
								LOG.error("Error in suppBlackUploadDocument" + e.getMessage(), e);
							}
						}
					}
				}
			}
		}

		try {
			if (favSupplier.getStatus() == FavouriteSupplierStatus.BLACKLISTED) {
				favSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, "Supplier '" + supplier.getCompanyName() + "' activated from blacklisted status", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} else {
				favSupplier.setStatus(FavouriteSupplierStatus.BLACKLISTED);
			}
			favSupplier.setBlackListRemark(blackListRemark);
			favSupplier.setSuspendStartDate(null);
			favSupplier.setSuspendEndDate(null);
			favoriteSupplierService.updateFavoriteSupplier(favSupplier, SecurityLibrary.getLoggedInUser(), true);
		} catch (Exception e) {
			redir.addFlashAttribute("error", "Error while changing supplier status");
			LOG.error("Error in update supplier staust as Black list " + e.getMessage(), e);
		}
		try {
			FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setFavSupp(supplier);
			if (favSupplier.getStatus() == FavouriteSupplierStatus.BLACKLISTED) {
				audit.setDescription("BlackListed");
				audit.setRemark(messageSource.getMessage("supplier.added.blacklisted", new Object[] { blackListRemark }, Global.LOCALE));
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.BLACKLIST, "Supplier '" + supplier.getCompanyName() + "' is Blacklisted", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} else {
				audit.setDescription("Unblacklisted");
				audit.setRemark(messageSource.getMessage("supplier.auditstatus.unblacklisted", new Object[] { blackListRemark }, Global.LOCALE));
			}
			audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
		} catch (Exception e) {
			LOG.error("Error in save supplier staust audit:" + e.getMessage(), e);
		}

		redir.addFlashAttribute("success", favSupplier.getSupplier().getCompanyName() + " " + favSupplier.getStatus() + " successfully");
		return "redirect:/buyer/importSupplier";
	}

	@RequestMapping(path = "/suppSuspendUploadDocument", method = RequestMethod.POST)
	public String suppSuspendUploadDocument(@RequestParam(name = "docs", required = false) MultipartFile[] file, @RequestParam(name = "docDescription", required = false) String[] desc, @RequestParam("suppId") String supplierId, @RequestParam("suspendRemark") String suspendRemark, Model model, RedirectAttributes redir, String suspendDuration, HttpSession session, @RequestParam(name = "activeSuspend", required = false) String reschedule) {
		FavouriteSupplier favSupplier = favoriteSupplierService.findFavSupplierByFavSuppId(supplierId, SecurityLibrary.getLoggedInUser().getTenantId());
		boolean reSchudele = false;
		String futureSuspensionMsg = "";
		if (StringUtils.checkString(reschedule).length() > 0) {
			reSchudele = true;
		}

		if (StringUtils.checkString(suspendDuration).length() > 0) {
			String[] visibilityDates = suspendDuration.split("-");
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			try {
				Date startDate = formatter.parse(visibilityDates[0]);
				Date endDate = formatter.parse(visibilityDates[1]);
			} catch (ParseException e) {

				suspendDuration = null;
				e.fillInStackTrace();
			}

		}

		String[] suspensionVal = new String[2];
		if (favSupplier == null) {
			redir.addFlashAttribute("error", "Add supplier to Favourite List first.");
			return "redirect:/buyer/importSupplier";
		}
		Supplier supplier = favoriteSupplierService.getSupplierByFavSupplierId(favSupplier.getId());
		Date startDate = null;
		Date endDate = null;
		if (StringUtils.checkString(suspendDuration).length() > 0) {
			String timeZoneVal = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (timeZone != null) {
				timeZone = TimeZone.getTimeZone(timeZoneVal);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			String[] visibilityDates = suspendDuration.split("-");
			formatter.setTimeZone(timeZone);
			try {
				startDate = formatter.parse(visibilityDates[0]);
				endDate = formatter.parse(visibilityDates[1]);
				suspensionVal[0] = visibilityDates[0];
				suspensionVal[1] = visibilityDates[1];
				favSupplier.setSuspendStartDate(startDate);
				favSupplier.setSuspendEndDate(endDate);
				if (startDate != null && startDate.before(new Date()) && favSupplier.getStatus() != FavouriteSupplierStatus.SUSPENDED) {
					redir.addFlashAttribute("error", "Suspension start date must not be past date");
					return "redirect:/buyer/importSupplier";
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		for (int i = 0; i < file.length; i++) {
			for (int j = 0; j < desc.length; j++) {
				if (i == j) {
					if (file[i] != null) {
						if (!file[i].isEmpty()) {
							try {
								String fileName = file[i].getOriginalFilename();
								byte[] bytes = file[i].getBytes();
								SupplierNoteDocument notesDoc = new SupplierNoteDocument();
								notesDoc.setCredContentType(file[i].getContentType());
								notesDoc.setDescription(desc[i]);
								notesDoc.setFileName(fileName);
								notesDoc.setFileData(bytes);
								notesDoc.setUploadDate(new Date());
								notesDoc.setUploadTenantId(SecurityLibrary.getLoggedInUserTenantId());
								notesDoc.setCreatedBy(SecurityLibrary.getLoggedInUser());
								if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getBuyer() != null) {
									notesDoc.setTenantType(TenantType.BUYER);
								} else if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getOwner() != null) {
									notesDoc.setTenantType(TenantType.OWNER);
								}
								notesDoc.setSupplier(supplierService.findSuppById(favSupplier.getSupplier().getId()));
								notesDoc.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
								suppNotesDocUploadService.saveOrUpdate(notesDoc);
							} catch (Exception e) {
								redir.addFlashAttribute("error", "Error while uploading documents");
								LOG.error("Error in suppBlackUploadDocument" + e.getMessage(), e);
							}
						}
					}
				}
			}
		}

		try {

			FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
			if (startDate != null && favSupplier.getStatus() != FavouriteSupplierStatus.SUSPENDED && !favSupplier.getIsFutureSuspended()) {
				try {
					favSupplier.setSuspendRemark(suspendRemark);
					favSupplier.setIsFutureSuspended(true);
					favSupplier = favoriteSupplierService.updateFavoriteSupplier(favSupplier, SecurityLibrary.getLoggedInUser(), true);
				} catch (Exception e) {
					redir.addFlashAttribute("error", "Error while changing supplier status");
					return "redirect:/buyer/importSupplier";
				}
				try {
					LOG.info("Log1");
					audit.setDescription("Suspended");
					futureSuspensionMsg = favSupplier.getSupplier().getCompanyName() + " will be suspended from " + suspensionVal[0] + " to " + suspensionVal[1];
					audit.setActionDate(new Date());
					audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setFavSupp(supplier);
					audit.setRemark("Supplier will be suspended from " + suspensionVal[0] + " to " + suspensionVal[1] + ". Remarks: " + suspendRemark);
					audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Supplier '" + favSupplier.getSupplier().getCompanyName() + "' is suspended from'" + suspensionVal[0] + "' to '" + suspensionVal[1] + "'", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}

			else if (startDate == null && favSupplier.getStatus() != FavouriteSupplierStatus.SUSPENDED && !favSupplier.getIsFutureSuspended()) {
				try {
					favSupplier.setStatus(FavouriteSupplierStatus.SUSPENDED);
					favSupplier.setSuspendRemark(suspendRemark);
					favSupplier.setIsFutureSuspended(false);
					favoriteSupplierService.updateFavoriteSupplier(favSupplier, SecurityLibrary.getLoggedInUser(), true);
				} catch (Exception e) {
					redir.addFlashAttribute("error", "Error while changing supplier status");
					LOG.error(" " + e.getMessage(), e);
				}
				try {
					LOG.info("Log2");
					audit.setDescription("Suspended");
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					audit.setFavSupp(supplier);
					audit.setRemark("Supplier suspended from " + suspensionVal[0] + " to " + suspensionVal[1] + ". Remarks: " + suspendRemark);
					audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Supplier '" + favSupplier.getSupplier().getCompanyName() + "' is suspended from'" + suspensionVal[0] + "' to '" + suspensionVal[1] + "'", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}

			else if (favSupplier.getStatus() == FavouriteSupplierStatus.ACTIVE && favSupplier.getIsFutureSuspended() && !reSchudele) {
				try {
					favSupplier.setSuspendRemark(suspendRemark);
					favSupplier.setSuspendStartDate(null);
					favSupplier.setSuspendEndDate(null);
					favSupplier.setIsFutureSuspended(false);
					favoriteSupplierService.updateFavoriteSupplier(favSupplier, SecurityLibrary.getLoggedInUser(), true);
				} catch (Exception e) {
					redir.addFlashAttribute("error", "Error while changing supplier status");
					return "redirect:/buyer/importSupplier";
				}
				try {
					audit.setDescription("Unsuspended");
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					audit.setFavSupp(supplier);
					audit.setRemark(messageSource.getMessage("supplier.auditstatus.unsuspended", new Object[] { suspendRemark }, Global.LOCALE));
					audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, "Supplier '" + favSupplier.getFullName() + "' activated from suspended status", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}

			else if (reSchudele && favSupplier.getStatus() == FavouriteSupplierStatus.ACTIVE && favSupplier.getIsFutureSuspended()) {
				try {
					favSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
					favSupplier.setSuspendRemark(suspendRemark);
					favSupplier.setSuspendStartDate(startDate);
					favSupplier.setSuspendEndDate(endDate);
					favSupplier.setIsFutureSuspended(true);
					favoriteSupplierService.updateFavoriteSupplier(favSupplier, SecurityLibrary.getLoggedInUser(), true);
					LOG.info("---------- " + favSupplier.getIsFutureSuspended());
					futureSuspensionMsg = favSupplier.getSupplier().getCompanyName() + " will be suspended from " + suspensionVal[0] + " to " + suspensionVal[1];
				} catch (Exception e) {
					redir.addFlashAttribute("error", "Error while changing supplier status");
					return "redirect:/buyer/importSupplier";
				}
				try {
					LOG.info("Log4");
					audit.setDescription("Rescheduled");
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					audit.setFavSupp(supplier);
					audit.setRemark("Supplier suspension rescheduled from " + suspensionVal[0] + " to " + suspensionVal[1] + ". Remarks: " + suspendRemark);
					audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Supplier '" + favSupplier.getSupplier().getCompanyName() + "' is suspended from'" + suspensionVal[0] + "' to '" + suspensionVal[1] + "'", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			} else if (favSupplier.getStatus() == FavouriteSupplierStatus.SUSPENDED) {
				try {
					favSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
					favSupplier.setSuspendRemark(suspendRemark);
					favSupplier.setSuspendStartDate(null);
					favSupplier.setSuspendEndDate(null);
					favSupplier.setIsFutureSuspended(false);
					favoriteSupplierService.updateFavoriteSupplier(favSupplier, SecurityLibrary.getLoggedInUser(), true);
					LOG.info("---------- " + favSupplier.getIsFutureSuspended());
				} catch (Exception e) {
					redir.addFlashAttribute("error", "Error while changing supplier status");
					return "redirect:/buyer/importSupplier";
				}
				try {
					LOG.info("Log5");
					audit.setDescription("Activated");
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					audit.setFavSupp(supplier);
					audit.setRemark(messageSource.getMessage("supplier.auditstatus.unsuspended", new Object[] { suspendRemark }, Global.LOCALE));
					audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, "Supplier '" + favSupplier.getFullName() + "' activated from suspended status ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage());
				}

			}

		} catch (Exception e) {
			redir.addFlashAttribute("error", "Error while changing supplier status");
			LOG.error(" " + e.getMessage(), e);
		}
		if (StringUtils.checkString(futureSuspensionMsg).length() > 0) {
			redir.addFlashAttribute("success", futureSuspensionMsg);
		} else {
			redir.addFlashAttribute("success", favSupplier.getSupplier().getCompanyName() + " " + favSupplier.getStatus() + " successfully");
		}
		return "redirect:/buyer/importSupplier";
	}

	@ModelAttribute("industryCategories")
	public List<IndustryCategoryPojo> getAllIndCat() {
		try {
			return favoriteSupplierService.getTopTwentyFiveCategory(SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ArrayList<IndustryCategoryPojo>();
		}
	}

	@RequestMapping(path = "/pendingSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> pendingSupplierList(TableDataInput input, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		try {

			List<SupplierPojo> searchList = favoriteSupplierService.getFavSupplierListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.PENDING);
			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			long supplierCount = favoriteSupplierService.getFavSuppliersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.PENDING);
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching pending Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching pending Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/rejectedSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> rejectedSupplierList(TableDataInput input, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		try {

			List<SupplierPojo> searchList = favoriteSupplierService.getFavSupplierListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.REJECTED);
			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			long supplierCount = favoriteSupplierService.getFavSuppliersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.REJECTED);
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching rejected Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching rejected Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/blacklistedSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> blacklistedSupplierList(TableDataInput input, HttpSession session) {
		LOG.info("Getting blacklisted supplier list");
		HttpHeaders headers = new HttpHeaders();
		try {
			List<SupplierPojo> searchList = favoriteSupplierService.getFavSupplierListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.BLACKLISTED);
			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			long supplierCount = favoriteSupplierService.getFavSuppliersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.BLACKLISTED);
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching blacklisted Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching blacklisted Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/suspendedSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> suspendedSupplierList(TableDataInput input, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		try {

			List<SupplierPojo> searchList = favoriteSupplierService.getFavSupplierListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.SUSPENDED);
			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			long supplierCount = favoriteSupplierService.getFavSuppliersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.SUSPENDED);
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching suspended Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching suspended Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/activeSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> activeSupplierList(TableDataInput input, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		try {

			List<SupplierPojo> searchList = favoriteSupplierService.getFavSupplierListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.ACTIVE);
			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			long supplierCount = favoriteSupplierService.getFavSuppliersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.ACTIVE);
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching active Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching active Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/totalSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPojo>> totalSupplierList(TableDataInput input, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		try {

			List<SupplierPojo> searchList = favoriteSupplierService.getTotalSuppliersFromGlobalList(input, SecurityLibrary.getLoggedInUserTenantId());
			TableData<SupplierPojo> data = new TableData<SupplierPojo>(searchList);
			data.setDraw(input.getDraw());
			long supplierCount = favoriteSupplierService.getTotalSuppliersCountFromGlobalList(input, SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(supplierCount);
			data.setRecordsFiltered(supplierCount);

			return new ResponseEntity<TableData<SupplierPojo>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching total Supplier list : " + e.getMessage(), e);
			headers.add("error", "Error fetching total Supplier list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/acceptSupplierRequest/{requestId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestedAssociatedBuyerPojo> acceptSupplierRequest(@PathVariable(name = "requestId") String requestId, @RequestBody RequestedAssociatedBuyerPojo data) {
		try {
			LOG.info("Supplier request accepted by : " + SecurityLibrary.getLoggedInUserTenantId());
			RequestedAssociatedBuyer associatedBuyer = supplierService.findAssocoaitedRequestById(requestId);
			if (associatedBuyer != null) {
				RequestedAssociatedBuyer updatedAssociatedBuyer = supplierAssociatedBuyerService.acceptSupplierRequest(associatedBuyer, data, SecurityLibrary.getLoggedInUser());
				// Send email to supplier
				if (updatedAssociatedBuyer.getStatus() == RequestAssociateBuyerStatus.APPROVED) {
					TimeZone timeZone = TimeZone.getDefault();
					String strTimerZone = buyerSettingsService.getBuyerTimeZoneByTenantId(associatedBuyer.getBuyer().getId());
					if (StringUtils.checkString(strTimerZone).length() > 0) {
						timeZone = TimeZone.getTimeZone(strTimerZone);
					}
					try {
						supplierAssociatedBuyerService.sendEmailToAssociatedSupplier(associatedBuyer.getSupplier(), associatedBuyer.getBuyer(), timeZone, true, updatedAssociatedBuyer, StringUtils.checkString(data.getBuyerRemark()).length() > 0 ? data.getBuyerRemark() : null);
					} catch (Exception e) {
						LOG.error("Error in sending email : " + e.getMessage(), e);
					}
				}
				data = new RequestedAssociatedBuyerPojo();
				data.setStatus(updatedAssociatedBuyer.getStatus());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", "Supplier added to Active List successfully.");
				return new ResponseEntity<RequestedAssociatedBuyerPojo>(data, headers, HttpStatus.OK);
			} else {
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error while adding supplier to My List.");
				return new ResponseEntity<RequestedAssociatedBuyerPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error while accepting request : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while adding supplier to My List.");
			return new ResponseEntity<RequestedAssociatedBuyerPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/rejectSupplierRequest/{requestId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestedAssociatedBuyerPojo> rejectSupplierRequest(@PathVariable(name = "requestId") String requestId, @RequestBody RequestedAssociatedBuyerPojo data) {
		try {
			LOG.info("Supplier request rejected by : " + SecurityLibrary.getLoggedInUserTenantId());
			RequestedAssociatedBuyer associatedBuyer = supplierService.findAssocoaitedRequestById(requestId);
			if (associatedBuyer != null) {
				RequestedAssociatedBuyer updatedAssociatedBuyer = supplierAssociatedBuyerService.rejectSupplierRequest(associatedBuyer, data, SecurityLibrary.getLoggedInUser());
				// Send email to supplier
				if (updatedAssociatedBuyer.getStatus() == RequestAssociateBuyerStatus.REJECTED) {
					TimeZone timeZone = TimeZone.getDefault();
					String strTimerZone = buyerSettingsService.getBuyerTimeZoneByTenantId(associatedBuyer.getBuyer().getId());
					if (StringUtils.checkString(strTimerZone).length() > 0) {
						timeZone = TimeZone.getTimeZone(strTimerZone);
					}
					try {
						supplierAssociatedBuyerService.sendEmailToAssociatedSupplier(associatedBuyer.getSupplier(), associatedBuyer.getBuyer(), timeZone, false, updatedAssociatedBuyer, data.getBuyerRemark());
					} catch (Exception e) {
						LOG.error("Error in sending email : " + e.getMessage(), e);
					}
				}
				data = new RequestedAssociatedBuyerPojo();
				data.setStatus(updatedAssociatedBuyer.getStatus());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", "Supplier request Rejected.");
				return new ResponseEntity<RequestedAssociatedBuyerPojo>(data, headers, HttpStatus.OK);
			} else {
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error in rejecting supplier request.");
				return new ResponseEntity<RequestedAssociatedBuyerPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error while rejecting request : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error in rejecting supplier request.");
			return new ResponseEntity<RequestedAssociatedBuyerPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// PH-1179 adding this request
	@RequestMapping(value = "supplierDetailsOfGlobalSupplier/searchIndustryCategories", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchIndustryCategoriesForSupplierEngagemnt(@RequestParam("search") String search) {
		LOG.info("inside search ic dialog:" + search);
		List<IndustryCategory> icList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, SecurityLibrary.getLoggedInUserTenantId());
		return new ResponseEntity<List<IndustryCategory>>(icList, HttpStatus.OK);
	}

	@RequestMapping(path = "/ExportSupplierByStatus", method = RequestMethod.POST)
	public void downloadSupplierListByStatus(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String poIds, boolean select_all_supp, @RequestParam(required = false) String status, @ModelAttribute("searchFilterSupplierPojo") SearchFilterSupplierPojo searchFilterSupplierPojo) throws IOException {
		LOG.info("downloadSupplierList is called here");
		try {
			String[] favArr = null;
			if (StringUtils.checkString(poIds).length() > 0) {
				favArr = poIds.split(",");
			}
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ExportSupplier.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			FavouriteSupplierStatus favStatus = null;
			if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.ACTIVE.toString())) {
				favStatus = FavouriteSupplierStatus.ACTIVE;
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.PENDING.toString())) {
				favStatus = FavouriteSupplierStatus.PENDING;
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.REJECTED.toString())) {
				favStatus = FavouriteSupplierStatus.REJECTED;
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.SUSPENDED.toString())) {
				favStatus = FavouriteSupplierStatus.SUSPENDED;
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.BLACKLISTED.toString())) {
				favStatus = FavouriteSupplierStatus.BLACKLISTED;
			}
			supplierDownloaderByStatus(workbook, favArr, select_all_supp, searchFilterSupplierPojo, favStatus);

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
			LOG.error("Error while downloading Supplier List :: " + e.getMessage(), e);
		}
	}

	protected void supplierDownloaderByStatus(XSSFWorkbook workbook, String[] favArr, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo, FavouriteSupplierStatus status) throws Exception {

		XSSFSheet sheet = workbook.createSheet("Supplier List");
		int r = 1;
		buildHeaderDownloadSupplier(workbook, sheet);
		List<FavouriteSupplier> favSuppList = favoriteSupplierService.getAllFavouriteSupplierByIdAndStatus(favArr, SecurityLibrary.getLoggedInUserTenantId(), select_all, searchFilterSupplierPojo, status);
		if (CollectionUtil.isNotEmpty(favSuppList)) {
			for (FavouriteSupplier favSupp : favSuppList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getCompanyName() != null ? favSupp.getSupplier().getCompanyName() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getRegistrationOfCountry() != null ? favSupp.getSupplier().getRegistrationOfCountry().getCountryCode() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getCompanyRegistrationNumber() != null ? favSupp.getSupplier().getCompanyRegistrationNumber() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getFullName() != null ? favSupp.getFullName() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getDesignation() != null ? favSupp.getDesignation() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getMobileNumber() != null ? favSupp.getSupplier().getMobileNumber() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getCompanyContactNumber() != null ? favSupp.getCompanyContactNumber() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getLoginEmail() != null ? favSupp.getSupplier().getLoginEmail() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getFaxNumber() != null ? favSupp.getSupplier().getFaxNumber() : "");

				Cell cell = row.createCell(cellNum++);
				Cell cell1 = row.createCell(cellNum++);

				row.createCell(cellNum++).setCellValue(favSupp.getVendorCode() != null ? favSupp.getVendorCode() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getStatus() != null ? favSupp.getStatus().toString() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getRatings() != null ? favSupp.getRatings().toString() : "");
				List<IndustryCategory> industryCategory = favSupp.getIndustryCategory();
				List<String> inCat = new ArrayList<>();
				String seprator = " , ";
				if (CollectionUtil.isNotEmpty(industryCategory)) {
					for (IndustryCategory ic : industryCategory) {
						inCat.add(ic.getCode() + "-" + ic.getName());
					}
				}
				cell.setCellValue(String.join(seprator, inCat));

				List<ProductCategory> productCategory = favSupp.getProductCategory();
				List<String> pcat = new ArrayList<>();
				String seprator1 = " , ";
				if (CollectionUtil.isNotEmpty(productCategory)) {
					for (ProductCategory productcategory : productCategory) {
						pcat.add(productcategory.getProductCode() + "-" + productcategory.getProductName());
					}
				}
				cell1.setCellValue(String.join(seprator1, pcat));

				row.createCell(cellNum++).setCellValue(favSupp.getCommunicationEmail() != null ? favSupp.getCommunicationEmail() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getYearOfEstablished() != null ? favSupp.getSupplier().getYearOfEstablished().toString() : "");
				row.createCell(cellNum++).setCellValue(favSupp.getSupplier().getRegistrationCompleteDate() != null ? new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(favSupp.getSupplier().getRegistrationCompleteDate()) : "");
				Cell cell2 = row.createCell(cellNum++);
				List<SupplierTags> supplierTags = favSupp.getSupplierTags();
				List<String> stcat = new ArrayList<>();
				String seprator3 = " , ";
				if (CollectionUtil.isNotEmpty(supplierTags)) {
					for (SupplierTags suppTags : supplierTags) {
						stcat.add(suppTags.getSupplierTags());
					}
				}

				cell2.setCellValue(String.join(seprator3, stcat));
			}
		}
		for (int k = 0; k < 18; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	@RequestMapping(path = "/deleteSuppFormSub", method = RequestMethod.GET)
	public String deleteSuppFormSub(@RequestParam String id, Model model, RedirectAttributes redir) {
		LOG.info("Deleting Supplier Form  submission successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
		SupplierFormSubmition supplierFormObj = supplierFormSubmissionService.getSupplierformById(id);
		try {
			if (supplierFormObj != null) {
				supplierFormSubmissionService.deleteSuppFormSubmission(supplierFormObj);
				redir.addFlashAttribute("success", messageSource.getMessage("supplier.form.success.delete", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
				LOG.info("Deleted Supplier Form  submission successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting used supplier form , " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("supplierForm.error.delete.dataIntegrity", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting supplier form submission :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("supplierForm.error.delete", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
		}
		return "redirect:/buyer/importSupplier";

	}

	@RequestMapping(value = "/assignFormToSupplier", method = RequestMethod.POST)
	public String assignFormToSupplier(@ModelAttribute("supplierAssignFormPojo") SupplierAssignFormPojo supplierAssignFormPojo, Model model, RedirectAttributes redir) {
		LOG.info("Sending form By user:" + SecurityLibrary.getLoggedInUserLoginId());
		try {
			if (supplierAssignFormPojo.getSupplierFormIds() != null) {
				long notAssignedSuppCount = supplierFormSubmissionService.assignFormsToFavSupplier(SecurityLibrary.getLoggedInUser(), supplierAssignFormPojo);
				String successMessage = messageSource.getMessage("supplier.assign.form.success", new Object[] {}, Global.LOCALE);
				LOG.info("notAssignedSuppCount:" + notAssignedSuppCount);
				if (notAssignedSuppCount > 1) {
					successMessage += " " + messageSource.getMessage("supplierForm.resend.success", new Object[] { notAssignedSuppCount }, Global.LOCALE);
				}
				redir.addFlashAttribute("success", successMessage);
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("supplier.assign.form.empty", new Object[] {}, Global.LOCALE));
			}
		} catch (ApplicationException e) {
			LOG.error("Error while assigning supplier form , " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			LOG.error("Error while sending form to supplier:" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.assign.form.error", new Object[] {}, Global.LOCALE));
		}
		return "redirect:/buyer/importSupplier";

	}

	@RequestMapping(value = "searchSupplierName", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPojo>> searchUser(@RequestParam("search") String searchValue) {
		List<SupplierPojo> supplierPojo = favoriteSupplierService.getAllSearchFavouriteSupplierByBuyerId(SecurityLibrary.getLoggedInUserTenantId(), searchValue);
		return new ResponseEntity<List<SupplierPojo>>(supplierPojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/exportSupplierCsv", method = RequestMethod.POST)
	public void downloadSupplierCsvFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @RequestParam(required = false) String poIds, boolean select_all, @ModelAttribute("searchFilterSupplierPojo") SearchFilterSupplierPojo searchFilterSupplierPojo) throws IOException {
		LOG.info("downloadSupplierList is called " + searchFilterSupplierPojo.toString());
		try {
			File file = File.createTempFile("ExportSupplier", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String[] favArr = null;
			if (StringUtils.checkString(poIds).length() > 0) {
				favArr = poIds.split(",");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() == 0 && StringUtils.checkString(searchFilterSupplierPojo.getCusStatus()).length() > 0) {
				searchFilterSupplierPojo.setStatus(searchFilterSupplierPojo.getCusStatus());
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Supplier List is downloaded", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			favoriteSupplierService.downloadCsvFileForSupplierList(response, file, favArr, searchFilterSupplierPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(), session);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=ExportSupplier.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}

	@RequestMapping(path = "/ExportSupplierCsvByStatus", method = RequestMethod.POST)
	public void downloadSupplierCsvByStatus(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @RequestParam(required = false) String poIds, boolean select_all_supp, @RequestParam(required = false) String status, @ModelAttribute("searchFilterSupplierPojo") SearchFilterSupplierPojo searchFilterSupplierPojo) throws IOException {
		LOG.info("download Supplier Csv is called here");
		try {
			File file = File.createTempFile("ExportSupplier", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String[] favArr = null;
			if (StringUtils.checkString(poIds).length() > 0) {
				favArr = poIds.split(",");
			}

			FavouriteSupplierStatus favStatus = null;
			if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.ACTIVE.toString())) {
				favStatus = FavouriteSupplierStatus.ACTIVE;
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Supplier list Report for Active status is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.PENDING.toString())) {
				favStatus = FavouriteSupplierStatus.PENDING;
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Supplier list Report for Pending status is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.REJECTED.toString())) {
				favStatus = FavouriteSupplierStatus.REJECTED;
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Supplier list Report for Rejected status is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.SUSPENDED.toString())) {
				favStatus = FavouriteSupplierStatus.SUSPENDED;
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Supplier list Report for Suspended status is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
			} else if (StringUtils.checkString(status).equals(FavouriteSupplierStatus.BLACKLISTED.toString())) {
				favStatus = FavouriteSupplierStatus.BLACKLISTED;
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Supplier list Report for Blacklisted status is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
			}

			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() == 0 && StringUtils.checkString(searchFilterSupplierPojo.getCusStatus()).length() > 0) {
				searchFilterSupplierPojo.setStatus(searchFilterSupplierPojo.getCusStatus());
			}

			favoriteSupplierService.downloadCsvFileForSupplierList(response, file, favArr, searchFilterSupplierPojo, select_all_supp, favStatus, SecurityLibrary.getLoggedInUserTenantId(), session);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=ExportSupplier.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

	@RequestMapping(path = "/getOverallScoreSpFormAndBUnit", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getOverallScoreSpFormAndBUnit(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("unitId") String unitId, @RequestParam("supplierId") String supplierId, HttpSession session) {
		LOG.info("Overall Score SpForm And Business Unit for start date : " + startDate + " and end date : " + endDate);
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = formatter.parse(startDate);
				end = formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getOverallScoreBySpFormAndBUnit(start, end, unitId, supplierId, SecurityLibrary.getLoggedInUserTenantId());

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score SpForm And BUnit : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreByFormIdForCriteria", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SupplierPerformanceEvaluationPojo> getOverallScoreByFormIdForCriteria(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("formId") String formId, @RequestParam("supplierId") String supplierId, HttpSession session) {
		LOG.info("Overall score by Form for start date : " + startDate + " and end date : " + endDate);
		try {
			SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
			List<SupplierPerformanceEvaluationPojo> list = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = formatter.parse(startDate);
				end = formatter.parse(endDate);
			}

			list = supplierPerformanceEvaluationService.getOverallScoreByCriteriaAndFormId(start, end, formId, supplierId, SecurityLibrary.getLoggedInUserTenantId());

			if (StringUtils.checkString(formId).length() > 0) {
				SupplierPerformanceForm form = supplierPerformanceEvaluationService.getSupplierPerformanceFormByFormId(formId);
				data.setOverallScoreBySpForm(list);
				data.setTotalOverallScore(form.getOverallScore());
				if (form.getScoreRating() != null) {
					data.setRating(form.getScoreRating().getRating().toString());
					data.setRatingDescription(form.getScoreRating().getDescription());
				}
			}

			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score By FormId For Criteria : " + e.getMessage(), e);
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreByBuyerAndSupplierID", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SupplierPerformanceEvaluationPojo> getOverallScoreByBuyer(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("supplierId") String supplierId, HttpSession session) {
		LOG.info("Overall score by Buyer for start date : " + startDate + " and end date : " + endDate);
		try {
			SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = formatter.parse(startDate);
				end = formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getSPFDetailsForBuyerByTenantId(SecurityLibrary.getLoggedInUserTenantId(), supplierId, start, end);

			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(data, HttpStatus.OK);
		} catch (ApplicationException e) {
			LOG.error("Error in fetching Overall Score By Buyer : " + e.getMessage(), e);
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score By Buyer : " + e.getMessage(), e);
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreBySupplierIDForBU", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getOverallScoreOfSupplierByBuyerIdAndSupplierIdForBU(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("supplierId") String supplierId, HttpSession session) {
		LOG.info("Overall Score By SupplierID For Business Unit for start date : " + startDate + " and end date : " + endDate);
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = formatter.parse(startDate);
				end = formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(SecurityLibrary.getLoggedInUserTenantId(), supplierId, start, end);

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score SpForm And BUnit : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getFormIdByDate", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getFormIdByDate(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("supplierId") String supplierId, HttpSession session) {
		LOG.info("Form id start date : " + startDate + " and end date : " + endDate);
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = formatter.parse(startDate);
				end = formatter.parse(endDate);
			}

			data = supplierPerformanceFormService.getSpFormIdListForSupplierIdAndTenantId(start, end, supplierId, SecurityLibrary.getLoggedInUserTenantId());

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score SpForm And BUnit : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
