package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.dao.SupplierPlanDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PoShare;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NoRollBackException;
import com.privasia.procurehere.core.exceptions.WarningException;
import com.privasia.procurehere.core.parsers.SupplierParser;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.pojo.SearchFilterSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.SupplierActivationIntegrationPojo;
import com.privasia.procurehere.core.pojo.SupplierCountPojo;
import com.privasia.procurehere.core.pojo.SupplierIntigrationPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.SupplierSuspendIntegrationPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FavoutireSupplierAuditService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.SupplierTagsService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Javed Ahmed
 */
@Service
@Transactional(readOnly = true)
public class FavoriteSupplierServiceImpl implements FavoriteSupplierService {

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfxViewDao rfxViewDao;

	@Autowired
	CountryService countryService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	UserService userService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	SupplierPlanDao supplierPlanDao;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	SupplierTagsService supplierTagsService;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	CurrencyDao currency;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	FavoutireSupplierAuditService favSuppAuditService;

	@Autowired
	PrService prService;

	@Autowired
	PoService poService;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	StateDao stateDao;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Override
	@Transactional(readOnly = false)
	public FavouriteSupplier saveFavoriteSupplier(FavouriteSupplier favouriteSupplier, User user) {

		FavouriteSupplier favouriteSupplier2 = favoriteSupplierDao.saveOrUpdate(favouriteSupplier);
		if (user != null) {
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + favouriteSupplier.getSupplier().getCompanyName() + "' Favourite Supplier created", user.getTenantId(), user, new Date(), ModuleType.SupplierList);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error("error while saving audit for Favourite Supplier" + e.getMessage(), e);
			}
		}
		if (favouriteSupplier2.getSupplier() != null) {
			favouriteSupplier2.getSupplier().getCompanyName();
		}
		return favouriteSupplier2;

	}

	@Override
	@Transactional(readOnly = false)
	public FavouriteSupplier updateFavoriteSupplier(FavouriteSupplier favouriteSupplier, User user, boolean auditTrailFlagStatus) {
		favouriteSupplier = favoriteSupplierDao.saveOrUpdate(favouriteSupplier);
		LOG.info("Status : " + favouriteSupplier.getStatus());
		if (auditTrailFlagStatus) {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "' " + favouriteSupplier.getSupplier().getCompanyName() + "' Favourite Supplier status Updated", user.getTenantId(), user, new Date(), ModuleType.SupplierList);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} else {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "' " + favouriteSupplier.getSupplier().getCompanyName() + "' Favourite Supplier Updated", user.getTenantId(), user, new Date(), ModuleType.SupplierList);
			buyerAuditTrailDao.save(buyerAuditTrail);
		}

		return favouriteSupplier;

	}

	@Override
	public List<SupplierPojo> searchSuppliers(TableDataInput input, SupplierSearchPojo searchParams, String tenantId) {
		return favoriteSupplierDao.searchSuppliers(input, searchParams, tenantId);
	}

	@Override
	public Integer searchSuppliersCount(TableDataInput input, SupplierSearchPojo searchParams, String tenantId) {
		return favoriteSupplierDao.searchSuppliersCount(input, searchParams, tenantId);
	}

	@Override
	public List<Supplier> searchSupplierz(SupplierSearchPojo searchParams) {
		return favoriteSupplierDao.searchSupplierz(searchParams);
	}

	@Override
	public List<FavouriteSupplier> favoriteSuppliersOfBuyer(String buyerId, List<Supplier> invitedList, BigDecimal minGrade, BigDecimal maxGrade) {
		List<FavouriteSupplier> favSuppList = favoriteSupplierDao.favoriteSuppliersOfBuyer(buyerId, invitedList, minGrade, maxGrade);
		for (FavouriteSupplier favSupp : favSuppList) {
			favSupp.getSupplier().getCompanyName();
			favSupp.setIndustryCategory(null);
			favSupp.setProductCategory(null);
			favSupp.setCreatedBy(null);
			favSupp.setModifiedBy(null);
		}
		return favSuppList;
	}

	@Override
	public Integer favoriteSuppliersOfBuyerCount(String buyerId, List<Supplier> invitedList) {
		return favoriteSupplierDao.favoriteSuppliersOfBuyerCount(buyerId, invitedList);
	}

	@Override
	public boolean isExists(FavouriteSupplier favouriteSupplier) {
		return favoriteSupplierDao.isExists(favouriteSupplier);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteFavoriteSupplier(FavouriteSupplier favouriteSupplier, User user) throws ApplicationException {
		if (rftEventSupplierService.findSupplierBySupplierId(favouriteSupplier.getSupplier().getId()) == null && //
				rfpEventSupplierService.findSupplierBySupplierId(favouriteSupplier.getId()) == null && //
				rfqEventSupplierService.findSupplierBySupplierId(favouriteSupplier.getId()) == null && //
				rfiEventSupplierService.findSupplierBySupplierId(favouriteSupplier.getId()) == null && //
				rfaEventSupplierService.findSupplierBySupplierId(favouriteSupplier.getId()) == null && //
				prService.findSupplierByFavSupplierId(favouriteSupplier.getId()) == null && //
				poService.findSupplierByFavSupplierId(favouriteSupplier.getId()) == null) {

			// Update the associated buyer list.
			Supplier supplier = supplierDao.findById(favouriteSupplier.getSupplier().getId());
			if (supplier != null) {
				List<Buyer> associated = new ArrayList<Buyer>();
				if (supplier.getAssociatedBuyers() == null) {
					for (Buyer buyer : supplier.getAssociatedBuyers()) {
						if (!buyer.getId().equals(favouriteSupplier.getBuyer().getId())) {
							associated.add(buyer);
						}
					}
				}
				supplier.setAssociatedBuyers(associated);
				supplierDao.update(supplier);
			}

			String fullName = favouriteSupplier.getSupplier().getCompanyName();

			favoriteSupplierDao.deleteIndustryCatAndProductCat(favouriteSupplier.getId());
			favoriteSupplierDao.delete(favouriteSupplier);
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + fullName + "' Favourite Supplier deleted ", user.getTenantId(), user, new Date(), ModuleType.FavouriteSupplier);
			buyerAuditTrailDao.save(buyerAuditTrail);

		} else {
			throw new ApplicationException("Supplier already assigned to events.");
		}
	}

	@Override
	public List<FavouriteSupplier> searchFavSuppliers(SupplierSearchPojo searchParams) {
		return favoriteSupplierDao.searchFavSuppliers(searchParams);
	}

	@Override
	public List<FavouriteSupplier> searchFavSuppliersOfIndCat(String icId) {
		return favoriteSupplierDao.searchFavSuppliersOfIndCat(icId);
	}

	@Override
	public FavouriteSupplier findFavSupplierBySuppId(String sId, String buyerId) {
		FavouriteSupplier s = favoriteSupplierDao.findFavSupplierBySuppId(sId, buyerId);
		if (s != null) {
			if (CollectionUtil.isNotEmpty(s.getProductCategory())) {
				s.getProductCategory().get(0).getProductName();
			}

		}

		if (s != null) {
			if (CollectionUtil.isNotEmpty(s.getSupplierTags())) {
				s.getSupplierTags().get(0).getSupplierTags();
			}

		}

		return s;
	}

	@Override
	public FavouriteSupplier findFavSupplierByFavSuppId(String sId, String buyerId) {
		return favoriteSupplierDao.findFavSupplierByFavSuppId(sId, buyerId);
	}

	@Override
	public List<Supplier> searchSupplierInFavByOder(SearchVo searchSupplierInFavByOder) {
		return favoriteSupplierDao.searchSupplierInFavByOder(searchSupplierInFavByOder);
	}

	@Override
	public FavouriteSupplier getFavouriteSupplierBySupplierId(String supId, String buyerId) {
		return favoriteSupplierDao.getFavouriteSupplierBySupplierId(supId, buyerId);
	}

	@Override
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId, List<String> invitedList) {
		return favoriteSupplierDao.searchFavouriteSupplierByCompanyNameOrRegistrationNo(searchParam, buyerId, invitedList);
	}

	@Override
	public List<FavouriteSupplier> getFavouriteSupplierBySupplierId(List<Supplier> suppliers) {
		return favoriteSupplierDao.findAllFavouriteSuppliersForSuppliers(suppliers);
	}

	@Override
	public List<FavouriteSupplier> favSuppliersByNameAndTenant(String search, String tenantId) {
		List<FavouriteSupplier> favSuppList = favoriteSupplierDao.favSuppliersByNameAndTenant(search, tenantId);
		for (FavouriteSupplier favSupp : favSuppList) {
			favSupp.getSupplier().getCompanyName();
			favSupp.setIndustryCategory(null);
			favSupp.setIndustryCategory(null);
			favSupp.setCreatedBy(null);
			favSupp.setModifiedBy(null);
		}
		return favSuppList;
	}

	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierByTenantId(String tenantId) {
		List<FavouriteSupplier> favSuppList = favoriteSupplierDao.getAllFavouriteSupplierByTenantId(tenantId);
		// for (FavouriteSupplier favSupp : favSuppList) {
		// favSupp.getSupplier().getCompanyName();
		// //favSupp.setIndustryCategory(null);
		// //favSupp.setCreatedBy(null);
		// //favSupp.setModifiedBy(null);
		// }
		return favSuppList;
	}

	@Override
	public long totalEventInvitedSupplier(String suppId, String buyerId) {
		return rfxViewDao.totalEventInvitedSupplier(suppId, buyerId);
	}

	@Override
	public long totalEventParticipatedSupplier(String suppId, String buyerId) {
		return rfxViewDao.totalEventParticipatedSupplier(suppId, buyerId);
	}

	@Override
	public long totalEventAwardedSupplier(String suppId) {
		return rfxViewDao.totalEventAwardedSupplier(suppId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { ExcelParseException.class, Exception.class }, noRollbackFor = { NoRollBackException.class, WarningException.class })
	public void supplierListUpload(String tenantId, File convFile, boolean isUploadNewSupplier) throws ExcelParseException, ApplicationException, NoRollBackException, WarningException {
		SupplierParser<Supplier> supplierParser = new SupplierParser<Supplier>(Supplier.class);
		List<Supplier> supplierList = supplierParser.parse(convFile);
		if (CollectionUtil.isEmpty(supplierList)) {
			throw new ApplicationException("Please enter Supplier Data");
		}
		String errorMsg = validateAndUploadSupplier(tenantId, isUploadNewSupplier, supplierList, true, false);

		if (StringUtils.checkString(errorMsg).length() > 0) {
			throw new ApplicationException("Please enter Supplier Data");
		}

	}

	private String validateAndUploadSupplier(String tenantId, boolean isUploadNewSupplier, List<Supplier> supplierList, boolean isApi, boolean isErpUpload) throws ApplicationException, WarningException {
		int j = 1;
		String errorMsg = "";
		List<Supplier> supplierListForCreate = new ArrayList<>();
		List<String> uniqSupplier = new ArrayList<String>();
		List<String> uniqLoginEmail = new ArrayList<String>();
		// List<FavouriteSupplier> favSupplierList = getAllFavouriteSupplier(tenantId);
		// List<String> uploadedFavSuppplierIds = new ArrayList<String>();

		for (Supplier supp : supplierList) {
			Supplier supplier = validateUploadSupplierList(j++, supp, isUploadNewSupplier, tenantId, uniqSupplier, uniqLoginEmail, isApi);
			/*
			 * if (supplier != null) { FavouriteSupplier favSupp =
			 * favoriteSupplierDao.getFavouriteSupplierBySupplierId(supplier.getId()); if (favSupp != null) {
			 * uploadedFavSuppplierIds.add(favSupp.getId()); } }
			 */
			if(supplier==null) {
				supplierListForCreate.add(supp);
			}
		}

		errorMsg = createSupplier(tenantId, errorMsg, supplierListForCreate, isErpUpload);
		return errorMsg;
	}

	private String createSupplier(String tenantId, String errorMsg, List<Supplier> supplierListForCreate, boolean isErpUpload) throws ApplicationException, WarningException {
		for (Supplier supp : supplierListForCreate) {
			Supplier supplier = getExistSupplier(supp);
			if (supplier == null) {
				errorMsg = saveUploadedSupplier(tenantId, errorMsg, supp, isErpUpload);
			} else {
				LOG.info("found in global. Now check in local");
				// found in global. Now check in local for buyer
				if (!isSupplierInFavouriteList(supplier, tenantId)) {
					LOG.info("supplier not found in local list");
					addSupplierToFavouriteList(supp, supplier, tenantId, isErpUpload);
				} else {
					LOG.info("supplier found in local list");
					// check if supplier has this buyer in associated list
					SupplierSubscription ss = supplier.getSupplierSubscription();
					if (ss != null && ss.getSupplierPlan() != null) {
						if ((ss.getSupplierPlan().getBuyerLimit() == 1 && CollectionUtil.isEmpty(supplier.getAssociatedBuyers())) || ss.getSupplierPlan().getBuyerLimit() > 1) {
							if (supplier.getAssociatedBuyers() == null) {
								supplier.setAssociatedBuyers(new ArrayList<Buyer>());
							}
							if (!isAssociatedBuyer(supplier, tenantId)) {
								supplier.getAssociatedBuyers().add(new Buyer(tenantId));
								supplierService.updateSupplier(supplier);
							}
						}
					}
				}
			}
		}
		return errorMsg;
	}

	private Supplier getExistSupplier(Supplier supp) {
		String registrationNo = supp.getCompanyRegistrationNumber();
		String companyName = supp.getCompanyName();

		Country country = null;
		if (supp.getRegistrationOfCountry() != null) {
			String cc = supp.getRegistrationOfCountry().getCountryCode();
			country = countryService.getCountryByCode(cc);
		}
		Supplier supplier = isSupplierInRegNoandCompany(country, registrationNo, companyName);
		return supplier;
	}

	private String saveUploadedSupplier(String tenantId, String errorMsg, Supplier supp, boolean isErpUpload) {
		Supplier newSupplier = new Supplier();
		addUploadSupplierList(supp, newSupplier);
		UUID uuid = UUID.randomUUID();
		String password = uuid.toString().replaceAll("-", "").toUpperCase();
		password = password.substring(0, 9);
		newSupplier.setPassword(password);
		if(supp.getState() != null){
			String sc = supp.getState().getStateCode();
			List<State> state = stateDao.searchStatesByNameOrCode(sc);
			newSupplier.setState(state.get(0));
		}
		if (supp.getRegistrationOfCountry() != null) {
			String cc = supp.getRegistrationOfCountry().getCountryCode();
			Country country = countryService.getCountryByCode(cc);
			newSupplier.setRegistrationOfCountry(country);
		}
		newSupplier.setStatus(SupplierStatus.APPROVED);
		User createdBy = null;
		try {
			createdBy = SecurityLibrary.getLoggedInUser();
		} catch (Exception e) {
			createdBy = userService.getAdminUserForBuyer(tenantId);
		}
		newSupplier.setCreatedBy(createdBy);
		newSupplier.setNotes(new ArrayList<Notes>());
		Notes note = new Notes();
		note.setSupplier(newSupplier);
		note.setIncidentType("Auto Created");
		note.setCreatedDate(new Date());
		note.setDescription("Supplier auto created due to excel upload by Buyer : " + createdBy.getBuyer().getCompanyName());
		newSupplier.setCreatedDate(new Date());
		newSupplier.setApprovedDate(new Date());
		newSupplier.getNotes().add(note);
		try {
			if (newSupplier.getAssociatedBuyers() == null) {
				newSupplier.setAssociatedBuyers(new ArrayList<Buyer>());
			}
			newSupplier.getAssociatedBuyers().add(new Buyer(tenantId));

			newSupplier = supplierService.saveSupplier(newSupplier, false);

			List<com.privasia.procurehere.core.entity.TimeZone> timeZones = timeZoneService.findAllActiveTimeZone();
			SupplierSettings supplierSettings = new SupplierSettings();

			createDefaultSettings(newSupplier, createdBy, timeZones, supplierSettings);

			ActiveSingleBuyerSubcription(newSupplier);
			try {
				newSupplier = supplierService.confirmSupplier(newSupplier, false);
			} catch (ApplicationException e) {
				LOG.error("Error :" + e.getMessage(), e);
				if (errorMsg == null) {
					errorMsg = "";
				}
				errorMsg = errorMsg + " '" + newSupplier.getLoginEmail() + "', ";
			}
			LOG.info("Supplier is added in Global List :" + newSupplier.getCompanyName() + " \n Supplier Id :" + supp.getId());

			addSupplierToFavouriteList(supp, newSupplier, tenantId, isErpUpload);
			sendEmailToSupplier(tenantId, newSupplier, password);

		} catch (Exception e) {
			LOG.error("Error creating Supplier instance : " + e.getMessage(), e);
		}
		return errorMsg;
	}

	private void createDefaultSettings(Supplier newSupplier, User createdBy, List<com.privasia.procurehere.core.entity.TimeZone> timeZones, SupplierSettings supplierSettings) {
		if (newSupplier != null) {
			supplierSettings.setSupplier(newSupplier);
			supplierSettings.setPoShare(PoShare.NONE);
			for (com.privasia.procurehere.core.entity.TimeZone timeZone : timeZones) {
				if (timeZone.getTimeZone().equalsIgnoreCase("GMT+8:00")) {
					supplierSettings.setTimeZone(timeZone);
					break;
				}
			}
			supplierSettings.setModifiedDate(new Date());
			supplierSettings.setModifiedBy(createdBy);
			supplierSettingsService.saveSettings(supplierSettings);
		}
	}

	private void sendEmailToSupplier(String tenantId, Supplier newSupplier, String password) {
		try {
			User user = userService.getAdminUserForSupplier(newSupplier);
			Buyer buyer = buyerService.findBuyerGeneralDetailsById(tenantId);
			if (user != null) {
				LOG.info("sending email:" + user.getCommunicationEmail());
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("userName", user.getName());
				map.put("loginId", user.getLoginId());
				map.put("password", password);
				map.put("buyerName", buyer.getCompanyName());
				map.put("appUrl", APP_URL + "/login");
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
				String timeZone = "GMT+8:00";
				df.setTimeZone(TimeZone.getTimeZone(timeZone));
				map.put("date", df.format(new Date()));
				if(user.getEmailNotifications())
				notificationService.sendEmail(user.getCommunicationEmail(), "Your Procurehere account is created", map, Global.SUPPLIER_AUTO_CREATION_TEMPLATE);
			}

		} catch (Exception e) {
			LOG.error("Error sending email to supplier about his new account : " + e.getMessage(), e);
		}
	}

	private void addUploadSupplierList(Supplier supp, Supplier newSupplier) {
		newSupplier.setCompanyName(supp.getCompanyName());
		newSupplier.setCompanyRegistrationNumber(supp.getCompanyRegistrationNumber());
		newSupplier.setFullName(supp.getFullName());
		newSupplier.setDesignation(supp.getDesignation());
		newSupplier.setMobileNumber(supp.getMobileNumber());
		newSupplier.setCompanyContactNumber(supp.getCompanyContactNumber());
		newSupplier.setCommunicationEmail(supp.getCommunicationEmail());
		newSupplier.setLoginEmail(supp.getLoginEmail());
		newSupplier.setFaxNumber(supp.getFaxNumber());
	}

	private Supplier validateUploadSupplierList(int i, Supplier supp, boolean isUploadNewSupplier, String buyerId, List<String> uniqSupplier, List<String> uniqLoginEmail, boolean isApi) throws ApplicationException, WarningException {
		String registrationNo = null;
		String companyName = null;
		String countryCode = null;
		/*if(supp.getState() != null){
		if (StringUtils.checkString(supp.getState().getStateCode()).length() == 0) {
			throw new ApplicationException("Please provide State Code");
		  }
		}*/
		if (StringUtils.checkString(supp.getLoginEmail()).length() == 0) {
			throw new ApplicationException("Please provide Login Email");
		}
		if (StringUtils.checkString(supp.getLoginEmail()).length() > 0) {
			if (uniqLoginEmail.contains(supp.getLoginEmail())) {
				throw new ApplicationException("Duplicate Login Email" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
			uniqLoginEmail.add(supp.getLoginEmail());
			String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
			Pattern pat = Pattern.compile(emailRegex);
			if (!pat.matcher(supp.getLoginEmail()).matches()) {
				throw new ApplicationException("Please enter valid Login email" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		}

		if (StringUtils.checkString(supp.getCompanyName()).length() > 0) {
			String companyNameRegex = "[a-zA-Z0-9\\s .,-_&()']+$"; // "^[a-zA-Z0-9\\s.,()_-\\\\/&]+$";
			companyName = supp.getCompanyName();
			Pattern pat = Pattern.compile(companyNameRegex);
			if (!(supp.getCompanyName().length() >= 4 && supp.getCompanyName().length() <= 124)) {
				throw new ApplicationException("Company Name length must be between 4 and 124" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
			// if (!supp.getCompanyName().matches(companyNameRegex)) {
			if (!pat.matcher(companyName).matches()) {
				throw new ApplicationException("Company name can only contain alphanumeric characters and &/\\(),._-' and spaces");
			}
		}
		if (StringUtils.checkString(supp.getCompanyName()).length() == 0) {
			throw new ApplicationException("Please provide company name" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
		}
		if (StringUtils.checkString(supp.getCompanyRegistrationNumber()).length() > 0) {
			registrationNo = supp.getCompanyRegistrationNumber();
			String regex =  "[a-zA-Z0-9\\s .,-_&()]+$"; //"^[a-zA-Z0-9\\s.,/()&]+$";
			Pattern pat = Pattern.compile(regex);
			if (!(supp.getCompanyRegistrationNumber().length() >= 2 && supp.getCompanyRegistrationNumber().length() <= 124)) {
				throw new ApplicationException("Registration Number length must be between 2 and 124" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
			if (!pat.matcher(registrationNo).matches()) {
//			if (!registrationNo.matches(regex)) {
				throw new ApplicationException("Company Registration Number can only contain alphanumeric characters and &/(),._- and spaces");
			}
		}
		if (StringUtils.checkString(supp.getCompanyRegistrationNumber()).length() == 0) {
			throw new ApplicationException("Please provide company registration number" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName())));
		}
		if (supp.getFullName() == null) {
			throw new ApplicationException("Contact full name is required" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
		}

		if (StringUtils.checkString(supp.getDesignation()).length() > 0) {

			if (!(supp.getDesignation().length() >= 2 && supp.getDesignation().length() <= 128)) {
				throw new ApplicationException("Designation length must be between 2 and 128" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		}
		if (StringUtils.checkString(supp.getMobileNumber()).length() == 0) {
			throw new ApplicationException("Please Enter Mobile Number" + (isApi ? (" at row no:" + i) : ""));
		}
		if (StringUtils.checkString(supp.getMobileNumber()).length() > 0) {

			if (!(supp.getMobileNumber().length() >= 6 && supp.getMobileNumber().length() <= 14)) {
				throw new ApplicationException("Mobile Number length must be between 6 and 14" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		}

		if (StringUtils.checkString(supp.getCompanyContactNumber()).length() == 0) {
			throw new ApplicationException("Please Enter Company Contact Number" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
		}

		if (!(supp.getCompanyContactNumber().length() >= 6 && supp.getCompanyContactNumber().length() <= 14)) {
			throw new ApplicationException("Company Contact Number value must be between 6 and 14" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
		}

		if (StringUtils.checkString(supp.getCommunicationEmail()).length() > 0) {
			LOG.info(supp.getCommunicationEmail());
			String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
			Pattern pat = Pattern.compile(emailRegex);
			if (!pat.matcher(supp.getCommunicationEmail()).matches()) {
				throw new ApplicationException("Please enter valid Communication Email" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		} else {
			supp.setCommunicationEmail(supp.getLoginEmail());
		}
		if (StringUtils.checkString(supp.getFaxNumber()).length() > 0) {

			String faxno = "^[$@\\-_#!%?&]+$";
			Pattern p = Pattern.compile(faxno);
			if (p.matcher(supp.getFaxNumber()).matches()) {
				throw new ApplicationException("Only special characters as Fax No not Allowed" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		}
		if (StringUtils.checkString(supp.getFaxNumber()).length() > 0) {
			if (StringUtils.checkString(supp.getFaxNumber()).length() > 14) {
				throw new ApplicationException("Fax Number length must be less than 14" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		}

		if (StringUtils.checkString(supp.getVendorCode()).length() > 100) {
			throw new ApplicationException("Vendor code length must be less than 100" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
		}
		if (supp.getRegistrationOfCountry() != null) {
			countryCode = supp.getRegistrationOfCountry().getCountryCode();
		}

		String uniqSupplierKey = companyName + registrationNo + countryCode;
		LOG.info("Unique Company Name , Country code , Registration No " + uniqSupplierKey);
		if (uniqSupplier.contains(uniqSupplierKey)) {
			throw new ApplicationException("Duplicate record found" + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
		}
		uniqSupplier.add(companyName + registrationNo + countryCode);

		Supplier supplier = getExistSupplier(supp);
		if (supplier != null) {
			if (isUploadNewSupplier) {
				throw new ApplicationException("Combination of Registration No \"" + supplier.getCompanyRegistrationNumber() + "\", Country \"" + (supplier.getRegistrationOfCountry() != null ? supplier.getRegistrationOfCountry().getCountryCode() : "") + "\" and Company Name \"" + supplier.getCompanyName() + "\" is already registered in the application " + (isApi ? (" at row no:" + i) : ""));
			} else {
				FavouriteSupplier favSupplier = getFavouriteSupplierBySupplierId(supplier.getId(), buyerId);
				if (favSupplier != null) {
					try {
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						User user = userService.getAdminUserForBuyer(buyerId);
						audit.setActionBy(user);
						if (supp.getFavSupplierStatus().equals("ACTIVE") && FavouriteSupplierStatus.INACTIVE == favSupplier.getStatus()) {
							audit.setDescription("Activated");
							audit.setRemark("ERP: " + supplier.getCompanyName() + " has been Activated from Inactive status");
						}
						if (supp.getFavSupplierStatus().equals("INACTIVE") && FavouriteSupplierStatus.ACTIVE == favSupplier.getStatus()) {
							audit.setDescription("Inactive");
							audit.setRemark("ERP: " + supplier.getCompanyName() + "  has been Inactivated");
						}

						audit.setActionDate(new Date());
						audit.setTenantId(buyerId);
						audit.setFavSupp(supplier);
						if (StringUtils.checkString(audit.getRemark()).length() > 0) {
							audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
						}
					} catch (Exception e) {
						LOG.error("Error while saving favorite supplier audit : " + e.getMessage(), e);
					}

					favSupplier = bindDataToFavSupplier(supp, favSupplier, buyerId);
					User createdBy = null;
					try {
						createdBy = SecurityLibrary.getLoggedInUser();
					} catch (Exception e) {
						createdBy = userService.getAdminUserForBuyer(buyerId);
					}
					if(supplier != null){
						//updated
						favoriteSupplierService.updateFavoriteSupplier(favSupplier, createdBy, true);
					}else{
						favoriteSupplierDao.update(favSupplier);
					}
					return supplier;
				}
			}
		} else {
			// If supplier not found then need to check Login Id for create new supplier.
			if (userService.isExistsLoginEmailGlobal(supp.getLoginEmail())) {
				throw new ApplicationException("Error : User already exists by login email \"" + supp.getLoginEmail() + "\"." + (isApi ? (" at row no:" + i) : ("->" + supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")")));
			}
		}

		return null;// if return null new supplier will be create here

	}

	private boolean isAssociatedBuyer(Supplier supplier, String loggedInUserTenantId) {
		boolean exists = false;
		if (supplier.getAssociatedBuyers() != null) {
			for (Buyer buyer : supplier.getAssociatedBuyers()) {
				LOG.info("buyer Id :" + buyer.getId() + ", supplier Id :" + supplier.getId());
				if (buyer.getId().equals(loggedInUserTenantId)) {
					exists = true;
					break;
				}
			}
		} else {
			LOG.info("supplier.getAssociatedBuyers() is null");
		}
		return exists;
	}

	private void addSupplierToFavouriteList(Supplier supp, Supplier supplier, String tenantId, boolean isErpUpload) throws ApplicationException, WarningException {
		// Add to local as well
		FavouriteSupplier favouriteSupplier = new FavouriteSupplier();
		favouriteSupplier.setSupplier(supplier);
		bindDataToFavSupplier(supp, favouriteSupplier, tenantId);

		User createdBy = null;
		try {
			createdBy = SecurityLibrary.getLoggedInUser();
		} catch (Exception e) {
			createdBy = userService.getAdminUserForBuyer(tenantId);
		}

		favouriteSupplier.setCreatedBy(createdBy);

		favouriteSupplier.setCreatedDate(new Date());
		//
		// favouriteSupplier.setStatus(Status.ACTIVE);

		try {
			saveFavoriteSupplier(favouriteSupplier, createdBy);
			try {
				FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
				LOG.info("Saving uploaded fav supplier audit ");
				if (isErpUpload) {
					audit.setDescription("Added to My List");
					User actionBy = userService.getAdminUserForBuyer(tenantId);
					audit.setActionBy(actionBy);
					audit.setRemark(messageSource.getMessage("supplier.erpAudit.activated", new Object[] { supplier.getCompanyName() }, Global.LOCALE));
				} else {
					audit.setDescription("Uploaded");
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setRemark(messageSource.getMessage("supplier.added.favlist", new Object[] {}, Global.LOCALE));

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, "Supplier '" + favouriteSupplier.getSupplier().getCompanyName() + " ' added to Active List", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				}
				audit.setActionDate(new Date());
				audit.setTenantId(tenantId);
				audit.setFavSupp(supplier);
				audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
			} catch (Exception e) {
				LOG.error("Error while saving favorite supplier audit : " + e.getMessage(), e);
			}
			LOG.info("Supplier is added in FavouriteSupplier List :" + supplier.getCompanyName() + " \n Supplier Id :" + supplier.getId());
			SupplierSubscription ss = supplier.getSupplierSubscription();
			if (ss != null && ss.getSupplierPlan() != null) {
				if ((ss.getSupplierPlan().getBuyerLimit() == 1 && CollectionUtil.isEmpty(supplier.getAssociatedBuyers())) || ss.getSupplierPlan().getBuyerLimit() > 1) {
					if (supplier.getAssociatedBuyers() == null) {
						supplier.setAssociatedBuyers(new ArrayList<Buyer>());
					}
					if (!isAssociatedBuyer(supplier, tenantId)) {
						supplier.getAssociatedBuyers().add(new Buyer(tenantId));
						supplierService.updateSupplier(supplier);
					}
				}
			}

		} catch (Exception e) {
			LOG.error("Error creating FavouriteSupplier instance : " + e.getMessage(), e);
		}
	}

	private FavouriteSupplier bindDataToFavSupplier(Supplier supp, FavouriteSupplier favouriteSupplier, String tenantId) throws WarningException {
		Buyer buyer = new Buyer();
		buyer.setId(tenantId);
		favouriteSupplier.setBuyer(buyer);
		favouriteSupplier.setFavouriteSupplierTaxNumber(supp.getFaxNumber());
		if (supp.getCommunicationEmail() != null) {
			favouriteSupplier.setCommunicationEmail(supp.getCommunicationEmail());
		}

		favouriteSupplier.setVendorCode(supp.getVendorCode());
		if (StringUtils.checkString(supp.getFavSupplierStatus()).length() == 0) {
			if (FavouriteSupplierStatus.BLACKLISTED != favouriteSupplier.getStatus() && FavouriteSupplierStatus.SUSPENDED != favouriteSupplier.getStatus()) {
				favouriteSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
			}
		} else {
			if (supp.getFavSupplierStatus().equals("ACTIVE")) {
				if (FavouriteSupplierStatus.BLACKLISTED != favouriteSupplier.getStatus() && FavouriteSupplierStatus.SUSPENDED != favouriteSupplier.getStatus()) {
					favouriteSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
				}
			} else {
				if (FavouriteSupplierStatus.BLACKLISTED != favouriteSupplier.getStatus() && FavouriteSupplierStatus.SUSPENDED != favouriteSupplier.getStatus()) {
					favouriteSupplier.setStatus(FavouriteSupplierStatus.INACTIVE);
				}
			}
		}
		favouriteSupplier.setCompanyContactNumber(supp.getMobileNumber());
		favouriteSupplier.setFavouriteSupplierTaxNumber(supp.getFaxNumber()); // Added fax number

		favouriteSupplier.setFullName(supp.getFullName());
		favouriteSupplier.setRatings(supp.getFavSupplierRatings());

		if (supp.getIndustryCategory() != null && StringUtils.checkString(supp.getIndustryCategory().getCode()).length() > 0) {
			Set<IndustryCategory> indusList = new HashSet<IndustryCategory>();
			String ic = supp.getIndustryCategory().getCode();
			LOG.info("Industry category " + ic + " For vendor " + supp.getCompanyName());
			String arr[] = ic.split(",");
			StringBuffer icName = new StringBuffer();
			for (String string : arr) {
				if (StringUtils.checkString(string).length() > 0) {
					IndustryCategory indcat = null;
					if (string.contains("-")) {
						String singleItmArr[] = string.split("-");
						if (StringUtils.checkString(singleItmArr[0]).length() > 0) {
							String categoryCode = StringUtils.checkString(singleItmArr[0]);
							icName.append(categoryCode + ",");
							indcat = industryCategoryService.getIndustryCategoryByCode(categoryCode.replace(" ", ""), tenantId);
						}
					} else {
						icName.append(string.replace(" ", "") + ",");
						indcat = industryCategoryService.getIndustryCategoryByCode(string.replace(" ", ""), tenantId);
					}
					if (indcat != null) {
						indusList.add(indcat);
					}
				}
			}
			/// indusList
			if (CollectionUtil.isEmpty(indusList)) {
				throw new WarningException("Industry Categorys not found in PH for " + icName.substring(0, icName.length() - 1).toString() + " for vendor " + supp.getCompanyName());
			}
			favouriteSupplier.setIndustryCategory(new ArrayList<IndustryCategory>(indusList));
		}

		List<ProductCategory> prodList = new ArrayList<ProductCategory>();
		if (supp.getProductCategory() != null && StringUtils.checkString(supp.getProductCategory().getProductCode()).length() > 0) {
			String productcategory = supp.getProductCategory().getProductCode();
			String arr[] = productcategory.split("-");
			ProductCategory pc = productCategoryMaintenanceService.getProductCategoryByCodeAndTenantId(arr[0], tenantId);
			prodList.add(pc);
			favouriteSupplier.setProductCategory(prodList);
		}else{
			favouriteSupplier.setProductCategory(prodList);
		}

		if (supp.getSupplierTags() != null && StringUtils.checkString(supp.getSupplierTags().getSupplierTags()).length() > 0) {
			String supplierTgas = supp.getSupplierTags().getSupplierTags();
			List<SupplierTags> supptagList = new ArrayList<SupplierTags>();
			SupplierTags st = supplierTagsService.getSuppliertagsAndTenantId(supplierTgas, tenantId);
			if (st != null) {
				supptagList.add(st);
			}else{
				//PH-3405
				SupplierTags stbDes = supplierTagsService.getSuppliertagsDescriptionAndTenantId(supplierTgas, tenantId);
				if (stbDes != null)
					supptagList.add(stbDes);

			}
			favouriteSupplier.setSupplierTags(supptagList);
		}

		if (supp.getSupplierTags() == null && CollectionUtil.isNotEmpty(supp.getSupplierTagsList())) {
			favouriteSupplier.setSupplierTags(supp.getSupplierTagsList());
		}

		//when both supplier tag and bussiness coverage is empty
		if(supp.getSupplierTagsList() != null) {
			if (supp.getSupplierTags() == null && supp.getSupplierTagsList().size() == 0)
				favouriteSupplier.setSupplierTags(null);
		}
		if(supp.getState() != null) {
			if (supp.getState().getStateCode() != null) {
				String sc = supp.getState().getStateCode();
				List<State> state = stateDao.searchStatesByNameOrCode(sc);
				favouriteSupplier.getSupplier().setState(state.get(0));
			}
		}
		favouriteSupplier.setDesignation(supp.getDesignation());
		return favouriteSupplier;
	}

	@Override
	public Supplier isSupplierInGlobalList(Country country, String registrationNo, String companyName) {
		return favoriteSupplierDao.isSupplierInGlobalList(country, registrationNo, companyName);
	}

	@Override
	public Supplier isSupplierInRegNoandCompany(Country country, String registrationNo, String companyName) {
		return favoriteSupplierDao.isSupplierInGlobalList(country, registrationNo, companyName);
	}

	@Override
	public boolean isSupplierInFavouriteList(Supplier supplier, String buyerId) {
		return favoriteSupplierDao.isSupplierInFavouriteList(supplier, buyerId);
	}

	@Override
	public long countForFavSupplier(String tenantId) {
		return favoriteSupplierDao.countForFavSupplier(tenantId);
	}

	@Override
	public List<SupplierCountPojo> getCurrentSupplierCountForTopFiveCategories(String tenantId) {
		return favoriteSupplierDao.getCurrentSupplierCountForTopFiveCategories(tenantId);
	}

	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantId(String tenantId) {
		return favoriteSupplierDao.getAllActiveFavouriteSupplierByTenantId(tenantId);
	}

	@Override
	public List<FavouriteSupplier> favoriteSuppliersOfBuyerByIndusCategory(String buyerId, List<Supplier> invitedList, List<IndustryCategory> eventCategoryList, BigDecimal minGrade, BigDecimal maxGrade) {
		return favoriteSupplierDao.favoriteSuppliersOfBuyerByIndusCategory(buyerId, invitedList, eventCategoryList, minGrade, maxGrade);
	}

	@Override
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId, List<String> invitedList, List<IndustryCategory> eventCategoryList) {
		return favoriteSupplierDao.searchFavouriteSupplierByCompanyNameOrRegistrationNoByIndusCategory(searchParam, buyerId, invitedList, eventCategoryList);
	}

	@Override
	public FavouriteSupplier findFavSupplierByFavSupplierIdForDefault(String supplierId) {
		FavouriteSupplier favSupp = favoriteSupplierDao.findById(supplierId);
		if (favSupp != null) {
			if (favSupp.getSupplier() != null) {
				favSupp.getSupplier().getCompanyName();
			}
		}
		return favSupp;
	}

	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierById(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo) {
		List<FavouriteSupplier> list = favoriteSupplierDao.getAllFavouriteSupplierById(favArr, loggedInUserTenantId, select_all, searchFilterSupplierPojo);
		for (FavouriteSupplier favouriteSupplier : list) {
			if (CollectionUtil.isNotEmpty(favouriteSupplier.getProductCategory())) {
				for (ProductCategory categoryList : favouriteSupplier.getProductCategory()) {
					categoryList.getProductName();
				}
			}
			if (CollectionUtil.isNotEmpty(favouriteSupplier.getSupplierTags())) {
				for (SupplierTags categoryList : favouriteSupplier.getSupplierTags()) {
					categoryList.getSupplierTags();
				}
			}
		}
		return list;
	}

	@Override
	public JasperPrint getSupplierProfilePdf(String tenantId, String supplierId, HttpSession session) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/supplierProfile.jasper");
			File jasperfile = resource.getFile();

			FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierIdForReport(supplierId, tenantId);
			Supplier supplier = favouriteSupplier.getSupplier();
			// parameters.put("FAVOURITE_SUPPLIER",
			// favoriteSupplierDao.getFavouriteSupplierBySupplierId(supplierId,
			// tenantId));

			List<SupplierPojo> list = new ArrayList<>();
			SupplierPojo supplierPojo = new SupplierPojo();
			supplierPojo.setId(supplier.getId());
			supplierPojo.setCompanyName(supplier.getCompanyName());
			supplierPojo.setCompanystatus(supplier.getCompanyStatus().getCompanystatus());
			supplierPojo.setYearOfEstablished(supplier.getYearOfEstablished());
			supplierPojo.setLine1(supplier.getLine1());
			supplierPojo.setLine2(supplier.getLine2());
			supplierPojo.setCity(supplier.getCity());
			supplierPojo.setState(supplier.getState() != null ? supplier.getState().getStateName() : "N/A");
			supplierPojo.setCountryName(supplier.getRegistrationOfCountry().getCountryName());
			supplierPojo.setCompanyContactNumber(supplier.getCompanyContactNumber());
			supplierPojo.setFaxNumber(supplier.getFaxNumber());
			supplierPojo.setCompanyWebsite(supplier.getCompanyWebsite());
			supplierPojo.setLoginEmail(supplier.getLoginEmail());
			supplierPojo.setCommunicationEmail(supplier.getCommunicationEmail());
			supplierPojo.setRegistrationCompleteDate(supplier.getRegistrationCompleteDate());
			supplierPojo.setRemarks(supplier.getRemarks());
			supplierPojo.setMobileNumber(supplier.getMobileNumber());
			if (CollectionUtil.isNotEmpty(supplier.getNaicsCodes())) {
				for (NaicsCodes naicsCodes : supplier.getNaicsCodes()) {
					LOG.info("CodeName : " + naicsCodes.getCategoryName());

				}
			} else {
				LOG.info("EMPTY");
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
				supplierPojo.setCoverages(coverages);
			}

			long totalEventInvited = rfxViewDao.totalEventInvitedSupplier(supplierId, tenantId);
			long totalEventParticipated = rfxViewDao.totalEventParticipatedSupplier(supplierId, tenantId);
			long totalEventAwarded = rfxViewDao.totalEventAwardedSupplier(supplierId);

			long totalMyEventInvited = rfxViewDao.totalEventInvitedSupplier(supplierId, tenantId);
			long totalMyEventParticipated = rfxViewDao.totalEventParticipatedSupplier(supplierId, tenantId);
			long totalMyEventAwarded = rfxViewDao.totalEventAwardedSupplierAndBuyer(supplierId, tenantId);

			LOG.info("totalMyEventInvited :" + totalMyEventInvited + ",  totalMyEventParticipated :" + totalMyEventParticipated + ",  totalMyEventAwarded :" + totalMyEventAwarded);

			parameters.put("totalEventInvited", totalEventInvited);
			parameters.put("totalEventParticipated", totalEventParticipated);
			parameters.put("totalEventAwarded", totalEventAwarded);

			parameters.put("totalMyEventInvited", totalMyEventInvited);
			parameters.put("totalMyEventParticipated", totalMyEventParticipated);
			parameters.put("totalMyEventAwarded", totalMyEventAwarded);

			supplierPojo.setNaicsCodeslist(supplier.getNaicsCodes());

			if (CollectionUtil.isNotEmpty(supplier.getSupplierProjects())) {
				for (SupplierProjects sp : supplier.getSupplierProjects()) {
					String projectIndustries = "";
					for (NaicsCodes code : sp.getProjectIndustries()) {
						projectIndustries += code.getCategoryName();
					}
					sp.setProjectIndustrie(projectIndustries);
				}
				supplierPojo.setSupplierProjects(supplier.getSupplierProjects());
			} else {
				LOG.error("empty project list");
				supplierPojo.setSupplierProjects(new ArrayList<>());
			}

			list.add(supplierPojo);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Supplier profile PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public JasperPrint getSupplierProfilePdfForAll(String supplierId, HttpSession session, String tenantId, String teanantType) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
			LOG.info(timeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/supplierProfile.jasper");
			File jasperfile = resource.getFile();

			// FavouriteSupplier favouriteSupplier =
			// favoriteSupplierDao.getFavouriteSupplierBySupplierIdReport(supplierId);
			// LOG.info(favouriteSupplier.getClass().getName());
			// Supplier supplier = favouriteSupplier.getSupplier();
			Supplier supplier = supplierDao.findById(supplierId);
			// parameters.put("FAVOURITE_SUPPLIER",
			// favoriteSupplierDao.getFavouriteSupplierBySupplierId(supplierId,
			// tenantId));

			List<SupplierPojo> list = new ArrayList<>();
			SupplierPojo supplierPojo = new SupplierPojo();
			supplierPojo.setTeanantType(teanantType);
			supplierPojo.setId(supplier.getId());
			supplierPojo.setDesignation(supplier.getDesignation());
			supplierPojo.setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
			supplierPojo.setCompanyName(supplier.getCompanyName());
			supplierPojo.setCompanystatus(supplier.getCompanyStatus() != null ? supplier.getCompanyStatus().getCompanystatus() : "N/A");
			supplierPojo.setYearOfEstablished(supplier.getYearOfEstablished());
			supplierPojo.setLine1(supplier.getLine1() != null ? supplier.getLine1() : "N/A");
			supplierPojo.setLine2(supplier.getLine2() != null ? supplier.getLine2() : "N/A");
			supplierPojo.setCity(supplier.getCity() != null ? supplier.getCity() : "N/A");
			supplierPojo.setState(supplier.getState() != null ? supplier.getState().getStateName() : "N/A");
			supplierPojo.setCountryName(supplier.getRegistrationOfCountry().getCountryName());
			supplierPojo.setCompanyContactNumber(supplier.getCompanyContactNumber());
			supplierPojo.setFullName(StringUtils.checkString(supplier.getFullName()).length() > 0 ? supplier.getFullName() : "N/A");
			supplierPojo.setFaxNumber(supplier.getFaxNumber());
			supplierPojo.setCompanyWebsite(supplier.getCompanyWebsite() != null ? supplier.getCompanyWebsite() : "N/A");
			supplierPojo.setLoginEmail(supplier.getLoginEmail());
			supplierPojo.setCommunicationEmail(supplier.getCommunicationEmail());
			supplierPojo.setTaxRegistrationNumber(supplier.getTaxRegistrationNumber());
			supplierPojo.setPaidUpCapital(supplier.getPaidUpCapital());
			if (supplier.getCurrency() != null) {
				supplierPojo.setCurrencyCode(supplier.getCurrency().getCurrencyCode());
			} else {
				supplierPojo.setCurrencyCode(null);
			}
			if (CollectionUtil.isEmpty(supplier.getSupplierFinancialDocuments())) {
				com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments doc = new com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments();
				List<com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments> docList = new ArrayList<com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments>();
				docList.add(doc);
				supplierPojo.setSupplierFinancialDocuments(docList);
			} else {
				List<com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments> docList = new ArrayList<com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments>();
				for (SupplierFinanicalDocuments document : supplier.getSupplierFinancialDocuments()) {
					com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments doc = new com.privasia.procurehere.core.pojo.SupplierFinanicalDocuments();
					doc.setDescription(document.getDescription());
					doc.setFileName(document.getFileName());
					String date = sdf.format(document.getUploadDate());
					doc.setUploadDate(date);
					docList.add(doc);
				}
				supplierPojo.setSupplierFinancialDocuments(docList);
			}
			if (CollectionUtil.isEmpty(supplier.getSupplierBoardOfDirectors())) {
				SupplierBoardOfDirectors dir = new SupplierBoardOfDirectors();
				List<SupplierBoardOfDirectors> directors = new ArrayList<SupplierBoardOfDirectors>();
				directors.add(dir);
				supplierPojo.setSupplierBoardOfDirectors(directors);
			} else {
				supplierPojo.setSupplierBoardOfDirectors(supplier.getSupplierBoardOfDirectors());
			}
			if (supplier.getRegistrationCompleteDate() != null) {
				String date = sdf.format(supplier.getRegistrationCompleteDate());
				Date date6 = sdf.parse(date);
				LOG.info("Date " + date);
				LOG.info(date6);
				supplierPojo.setRegistrationCompleteDateS(StringUtils.checkString(date).length() > 0 ? date : "N/A");
				supplierPojo.setRegistrationCompleteDate(date6);
			}
			supplierPojo.setRemarks(supplier.getRemarks());
			supplierPojo.setMobileNumber(supplier.getMobileNumber());
			if (CollectionUtil.isNotEmpty(supplier.getNaicsCodes())) {
				for (NaicsCodes naicsCodes : supplier.getNaicsCodes()) {
					LOG.info("CodeName : " + naicsCodes.getCategoryName());

				}
			} else {
				LOG.info("EMPTY");
			}

			LOG.info("countries " + supplier.getCountries().size());
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
				supplierPojo.setCoverages(coverages);
			}

			long totalEventInvited = rfxViewDao.totalEventInvitedSupplier(supplierId, null);
			long totalEventParticipated = rfxViewDao.totalEventParticipatedSupplier(supplierId, null);
			long totalEventAwarded = rfxViewDao.totalEventAwardedSupplier(supplierId);

			long totalMyEventInvited = rfxViewDao.totalEventInvitedSupplier(supplierId, tenantId);
			long totalMyEventParticipated = rfxViewDao.totalEventParticipatedSupplier(supplierId, tenantId);
			long totalMyEventAwarded = rfxViewDao.totalEventAwardedSupplierAndBuyer(supplierId, tenantId);

			LOG.info("totalMyEventInvited :" + totalMyEventInvited + ",  totalMyEventParticipated :" + totalMyEventParticipated + ",  totalMyEventAwarded :" + totalMyEventAwarded);

			parameters.put("totalEventInvited", totalEventInvited);
			parameters.put("totalEventParticipated", totalEventParticipated);
			parameters.put("totalEventAwarded", totalEventAwarded);
			parameters.put("totalMyEventInvited", totalMyEventInvited);
			parameters.put("totalMyEventParticipated", totalMyEventParticipated);
			parameters.put("totalMyEventAwarded", totalMyEventAwarded);

			supplierPojo.setNaicsCodeslist(supplier.getNaicsCodes());

			if (CollectionUtil.isNotEmpty(supplier.getSupplierProjects())) {
				for (SupplierProjects sp : supplier.getSupplierProjects()) {
					String projectIndustries = "";
					for (NaicsCodes code : sp.getProjectIndustries()) {
						projectIndustries += code.getCategoryName();
					}
					sp.setProjectIndustrie(projectIndustries);
				}
				supplierPojo.setSupplierProjects(supplier.getSupplierProjects());
			} else {
				LOG.error("empty project list");
				supplierPojo.setSupplierProjects(new ArrayList<>());
			}
			LOG.info("Tax registration number " + supplierPojo.getTaxRegistrationNumber());
			LOG.info("Compony registration number " + supplierPojo.getCompanyRegistrationNumber());
			LOG.info("Compony registration number  " + supplier.getCompanyRegistrationNumber());
			LOG.info("Designation " + supplierPojo.getDesignation());
			LOG.info("Designation " + supplier.getDesignation());
			list.add(supplierPojo);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Supplier profile PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	/**
	 * @param supplier
	 * @return
	 */
	private Supplier ActiveSingleBuyerSubcription(Supplier supplier) {
		SupplierSubscription subscription = new SupplierSubscription();
		// PH 211 & PH 224 issue fixed
		subscription.setCreatedDate(new Date());
		subscription.setStartDate(new Date());
		subscription.setActivatedDate(new Date());
		subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
		SupplierPlan singlePlan = supplierPlanDao.getPlanByName("Single buyer package");
		if (singlePlan != null) {
			subscription.setBuyerLimit(singlePlan.getBuyerLimit());
			Calendar endDate = Calendar.getInstance();
			if (singlePlan.getPeriodUnit() == PeriodUnitType.MONTH) {
				endDate.add(Calendar.MONTH, singlePlan.getPeriod());
			} else if (singlePlan.getPeriodUnit() == PeriodUnitType.YEAR) {
				endDate.add(Calendar.YEAR, singlePlan.getPeriod());
			} else {
				endDate.add(Calendar.MONTH, singlePlan.getPeriod());
			}
			subscription.setEndDate(endDate.getTime());
			subscription.setSupplierPlan(singlePlan);
			subscription.setPriceAmount(new BigDecimal(singlePlan.getPrice()));
			subscription.setPromoCodeDiscount(BigDecimal.ZERO);
			subscription.setTotalPriceAmount(new BigDecimal(singlePlan.getPrice()));
			subscription.setCurrencyCode(singlePlan.getCurrency().getCurrencyCode());
		} else {
			subscription.setBuyerLimit(1);
			subscription.setSupplierPlan(null);
			subscription.setPriceAmount(BigDecimal.ZERO);
			subscription.setPromoCodeDiscount(BigDecimal.ZERO);
			subscription.setTotalPriceAmount(BigDecimal.ZERO);
			Calendar endDate = Calendar.getInstance();
			endDate.add(Calendar.YEAR, 99);
			subscription.setEndDate(endDate.getTime());
		}

		try {

			subscription.setSupplier(supplier);
			supplierSubscriptionDao.save(subscription);

			SupplierPackage sp = new SupplierPackage(subscription);
			supplier.setSupplierSubscription(subscription);
			supplier.setSupplierPackage(sp);
			supplier = supplierService.updateSupplier(supplier);
		} catch (Exception e) {
			LOG.error("Error storing supplier subscription : " + e.getMessage(), e);
		}
		return supplier;
	}

	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplier(String buyerId) {
		return favoriteSupplierDao.getAllFavouriteSupplier(buyerId);

	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = WarningException.class)
	public List<String> addSupplierTo(List<SupplierIntigrationPojo> supplier, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		for (SupplierIntigrationPojo supplierIntigrationPojo : supplier) {
			LOG.info("Storing supplier" + supplierIntigrationPojo.toLogString());
			String errorMsg = uploadSupplierFromApi(supplierIntigrationPojo, tenantId);
			if (StringUtils.checkString(errorMsg).length() > 0) {
				errorList.add(errorMsg);
				LOG.error("Error storing supplier " + errorMsg);
			}
		}
		return errorList;
	}

	private String uploadSupplierFromApi(SupplierIntigrationPojo supp, String tenantId) {
		List<Supplier> supplierList = new ArrayList<Supplier>();
		try {
			Supplier item = new Supplier();

			item.setCompanyName(supp.getCompanyName());
			State state = new State();
			state.setStateCode(StringUtils.checkString(supp.getStateCode()));
			item.setState(state);
			Country country = new Country();
			country.setCountryCode(StringUtils.checkString(supp.getCountryCode()));
			item.setRegistrationOfCountry(country);

			if (StringUtils.checkString(supp.getCompanyRegistrationNumber()).length() > 0) {
				item.setCompanyRegistrationNumber(supp.getCompanyRegistrationNumber());
			}

			item.setFullName(supp.getFullName());

			if (StringUtils.checkString(supp.getDesignation()).length() > 0) {
				item.setDesignation(supp.getDesignation());
			}
			item.setMobileNumber(supp.getMobileNumber());
			item.setCompanyContactNumber(StringUtils.checkString(supp.getCompanyContactNumber()));
			item.setLoginEmail(StringUtils.checkString(supp.getLoginEmail()));
			item.setFaxNumber(StringUtils.checkString(supp.getFaxNumber()));

			if (supp.getIndustryCategory() != null) {
				IndustryCategory ic = new IndustryCategory();
				ic.setCode(StringUtils.checkString(String.join(",", supp.getIndustryCategory())));
				item.setIndustryCategory(ic);
			}
			ProductCategory productcategory = new ProductCategory();
			productcategory.setProductCode(supp.getProductCategory() != null ? supp.getProductCategory().isEmpty() ? null: StringUtils.checkString(supp.getProductCategory()) : null);
			item.setProductCategory(productcategory);
			item.setVendorCode(StringUtils.checkString(supp.getVendorCode()));
			item.setFavSupplierStatus(supp.getStatus() != null ? supp.getStatus().name() : "ACTIVE");
			item.setCommunicationEmail((StringUtils.checkString(supp.getCommunicationEmail())));

			List<SupplierTags> supptagList = new ArrayList<SupplierTags>();
			if (supp.getSupplierTags() != null && !supp.getSupplierTags().isEmpty()) {
				for (String tag : supp.getSupplierTags()) {
					SupplierTags st = supplierTagsService.getSuppliertagsAndTenantId(tag, tenantId);
					if (st != null) {
						supptagList.add(st);
					}else{
						//PH-3405
						SupplierTags stbDes = supplierTagsService.getSuppliertagsDescriptionAndTenantId(tag, tenantId);
						if (stbDes != null) supptagList.add(stbDes);

					}
				}
			}
			if (supp.getBusinessCoverage() != null && !supp.getBusinessCoverage().isEmpty()) {
				for (String statetag : supp.getBusinessCoverage()) {
					SupplierTags stb = supplierTagsService.getSuppliertagsAndTenantId(statetag, tenantId);
					if (stb != null) {
						supptagList.add(stb);
					}
				}
			}
			item.setSupplierTagsList(supptagList);

			if (StringUtils.checkString(supp.getRating()).length() > 0) {
				try {
					item.setFavSupplierRatings(new BigDecimal(StringUtils.checkString(supp.getRating())));
				} catch (Exception e) {
					throw new ApplicationException("Supplier rating not valid for:" + (supp.getCompanyName() + "(" + supp.getCompanyRegistrationNumber() + ")"));
				}
			}
			supplierList.add(item);
			return validateAndUploadSupplier(tenantId, false, supplierList, false, true);

		} catch (WarningException e) {
			LOG.error("Error storing supplier" + e.getMessage(), e);
			return StringUtils.checkString(e.getMessage()).length() > 0 ? e.getMessage() : "some server side error";
		} catch (Exception e) {
			LOG.error("Error storing supplier" + e.getMessage(), e);
			return StringUtils.checkString(e.getMessage()).length() > 0 ? e.getMessage() : "some server side error";
		}
	}

	@Override
	public List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategory(String loggedInUserTenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search) {
		boolean isMinMaxPresent = false;
		switch (eventType) {
		case RFT:
			isMinMaxPresent = rftEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFA:
			isMinMaxPresent = rfaEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFP:
			isMinMaxPresent = rfpEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFQ:
			isMinMaxPresent = rfqEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFI:
			isMinMaxPresent = rfiEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		}
		LOG.info("isMinMaxPresent:" + isMinMaxPresent);
		List<EventSupplierPojo> list = favoriteSupplierDao.favoriteEventSupplierPojosOfBuyerByIndusCategory(loggedInUserTenantId, includeIndustryCategories, eventId, eventType, search, isMinMaxPresent);
		long count = favoriteSupplierDao.countFavoriteEventSupplierPojosOfBuyerByIndusCategory(loggedInUserTenantId, includeIndustryCategories, eventId, eventType, search, isMinMaxPresent);
		// get count
		if (count > 30) {
			EventSupplierPojo more = new EventSupplierPojo();
			more.setId("-1");
			more.setCompanyName("+" + (count - 30) + " more. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public FavouriteSupplier findFavSupplierById(String id) {
		return favoriteSupplierDao.findFavSupplierById(id);
	}

	@Override
	public List<FavouriteSupplier> findAllFavouriteSupplierForSuspension() {
		return favoriteSupplierDao.findAllFavouriteSupplierForSuspension();
	}

	@Override
	public Supplier getSupplierByFavSupplierId(String favSupplierId) {
		return favoriteSupplierDao.getSupplierByFavSupplierId(favSupplierId);
	}

	@Override
	public SupplierSearchPojo getTotalAndPendingSupplierCountForBuyer(String buyerId) {
		return favoriteSupplierDao.getTotalAndPendingSupplierCountForBuyer(buyerId);
	}

	@Override
	@Transactional(readOnly = true)
	public FavouriteSupplier saveFavoriteSuppliers(FavouriteSupplier favouriteSupplier) {
		FavouriteSupplier favSupp = favoriteSupplierDao.saveOrUpdate(favouriteSupplier);
		if (favSupp != null) {
			favSupp.getSupplier().getCompanyName();
			favSupp.setIndustryCategory(null);
			favSupp.setProductCategory(null);
			favSupp.setCreatedBy(null);
			favSupp.setModifiedBy(null);
		}
		return favSupp;
	}

	@Override
	public List<FavouriteSupplier> findInvitedSupplierByIndCat(String sId, Boolean select_all, SupplierSearchPojo supplierSearchPojo, String buyerId, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive) {
		return favoriteSupplierDao.findInvitedSupplierByIndCat(sId, select_all, supplierSearchPojo, buyerId, industryCategories, exclusive, inclusive);
	}

	@Override
	public List<FavouriteSupplier> findInvitedSupplierBySuppId(String sId, Boolean select_all, SupplierSearchPojo supplierSearchPojo, String buyerId, Boolean exclusive, Boolean inclusive) {
		return favoriteSupplierDao.findInvitedSupplierBySuppId(sId, select_all, supplierSearchPojo, buyerId, exclusive, inclusive);
	}

	@Override
	public boolean isSelfInviteSupplierInFavouriteList(String supplierId, String buyerId) {
		return favoriteSupplierDao.isSelfInviteSupplierInFavouriteList(supplierId, buyerId);
	}

	@Override
	public boolean existsEventCategoriesInSupplier(String eventId, String supplierId, RfxTypes eventType) {
		return favoriteSupplierDao.existsEventCategoriesInSupplier(eventId, supplierId, eventType);
	}

	@Override
	public boolean isSupplierRatingMatchToEventRating(String supplierId, BigDecimal minRating, BigDecimal maxRating, String buyerId) {
		return favoriteSupplierDao.isSupplierRatingMatchToEventRating(supplierId, minRating, maxRating, buyerId);
	}

	@Override
	public List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(String loggedInUserTenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search) {
		boolean isMinMaxPresent = false;
		switch (eventType) {
		case RFT:
			isMinMaxPresent = rftEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFA:
			isMinMaxPresent = rfaEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFP:
			isMinMaxPresent = rfpEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFQ:
			isMinMaxPresent = rfqEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFI:
			isMinMaxPresent = rfiEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		}
		LOG.info("isMinMaxPresent:" + isMinMaxPresent);
		return favoriteSupplierDao.favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(loggedInUserTenantId, includeIndustryCategories, eventId, eventType, search, isMinMaxPresent);
	}

	@Override
	public List<IndustryCategoryPojo> getTopTwentyFiveCategory(String tanentId) {
		List<IndustryCategoryPojo> indCatPojoList = new ArrayList<IndustryCategoryPojo>();
		List<IndustryCategoryPojo> pojoList = favoriteSupplierDao.getTopTwentyFiveCategory(tanentId);
		LOG.info(pojoList.size() + " Teanant ID " + tanentId);
		for (IndustryCategoryPojo industryCategoryPojo : pojoList) {
			IndustryCategoryPojo pojo = new IndustryCategoryPojo();
			pojo.setName(industryCategoryPojo.getName());
			pojo.setCode(industryCategoryPojo.getCode());
			pojo.setId(industryCategoryPojo.getId());
			indCatPojoList.add(pojo);
		}
		return indCatPojoList;
	}

	@Override
	public long getTotalAwardedSupplirForBuyer(String suppId, String tenantId) {
		return rfxViewDao.getTotalAwardedSupplirForBuyer(suppId, tenantId);
	}

	@Override
	public long getScheduledSupplier(String loggedInUserTenantId) {
		return favoriteSupplierDao.getScheduledSupplier(loggedInUserTenantId);
	}

	@Override
	public List<EventSupplierPojo> searchPrSuppliers(String tenantId, boolean filterByIndustryCategory, String prId, String search) {

		List<EventSupplierPojo> list = favoriteSupplierDao.favoritePrSupplierPojosOfBuyerByIndusCategory(tenantId, filterByIndustryCategory, prId, search);
		long count = favoriteSupplierDao.countConstructQueryToFetchSuppliersForPrSelection(tenantId, false, null, null);
		LOG.info("count : " + count + " List Size : " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				EventSupplierPojo more = new EventSupplierPojo();
				more.setCompanyName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}

	/**
	 * @param tenantId
	 * @param includeIndustryCategories
	 * @param prId
	 * @param search
	 * @return
	 */
	public long countConstructQueryToFetchSuppliersForPrSelection(String tenantId, boolean includeIndustryCategories, String prId, String search) {
		return favoriteSupplierDao.countConstructQueryToFetchSuppliersForPrSelection(tenantId, includeIndustryCategories, prId, search);
	}

	@Override
	public FavouriteSupplier getFavouriteSupplierByVendorCode(String vendorCode, String buyerId) {
		return favoriteSupplierDao.getFavouriteSupplierByVendorCode(vendorCode, buyerId);
	}

	@Transactional(readOnly = false)
	@Override
	public FavouriteSupplier updateFavoriteSupplier(FavouriteSupplier favouriteSupplier) {
		return favoriteSupplierDao.update(favouriteSupplier);
	}

	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantIdForAnnouncement(String tenantId) {
		return favoriteSupplierDao.getAllActiveFavouriteSupplierByTenantIdForAnnouncement(tenantId);
	}

	@Transactional(readOnly = false)
	@Override
	public FavouriteSupplier saveRequestedFavoriteSupplier(FavouriteSupplier favouriteSupplier) {
		return favoriteSupplierDao.saveOrUpdate(favouriteSupplier);
	}

	@Override
	public long getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus status, String loggedInUserTenantId) {
		return favoriteSupplierDao.getSupplierCountBasedOnStatusForBuyer(status, loggedInUserTenantId);
	}

	@Override
	public List<SupplierPojo> getFavSupplierListBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		return favoriteSupplierDao.getFavSupplierListBasedOnStatus(input, tenantId, status);
	}

	@Override
	public long getFavSuppliersCountBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		return favoriteSupplierDao.getFavSuppliersCountBasedOnStatus(input, tenantId, status);
	}

	@Override
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		return favoriteSupplierDao.getAssociatedBuyerListBasedOnStatus(input, tenantId, status);

	}

	@Override
	public long getAssociatedBuyersCountBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		return favoriteSupplierDao.getAssociatedBuyersCountBasedOnStatus(input, tenantId, status);
	}

	@Override
	public long getAssociatedBuyersCountForSupplierBasedOnStatus(String tenantId, FavouriteSupplierStatus status) {
		return favoriteSupplierDao.getAssociatedBuyersCountForSupplierBasedOnStatus(tenantId, status);
	}

	@Override
	public List<RequestedAssociatedBuyerPojo> searchBuyers(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String tenantId) {
		List<RequestedAssociatedBuyerPojo> searchBuyersList = favoriteSupplierDao.searchBuyers(input, searchBuyerPojo, tenantId);
		if (StringUtils.checkString(searchBuyerPojo.getSearchCompanyName()).length() > 0 || StringUtils.checkString(searchBuyerPojo.getSearchCountryName()).length() > 0) {
			if (CollectionUtil.isNotEmpty(searchBuyersList)) {
				for (RequestedAssociatedBuyerPojo buyer : searchBuyersList) {
					FavouriteSupplier favSupplier = favoriteSupplierDao.getFavSupplierDetailsByBuyerAndSupplierId(tenantId, buyer.getBuyerId());
					if (favSupplier != null) {
						LOG.info("not null");
						buyer.setFavStatus(favSupplier.getStatus());
						buyer.setRequestedDate(favSupplier.getCreatedDate());
						if (favSupplier.getAssociatedDate() != null) {
							buyer.setAssociatedDate(favSupplier.getAssociatedDate());
						}
						buyer.setFavSupp(true);
					} else {
						buyer.setFavSupp(false);
					}
				}
			}
		}
		return searchBuyersList;
	}

	@Override
	public List<RequestedAssociatedBuyerPojo> getAvailableBuyerList(TableDataInput input, String loggedInUserTenantId) {
		return favoriteSupplierDao.getAvailableBuyerList(input, loggedInUserTenantId);
	}

	@Override
	public long getAvailableBuyerListCount(TableDataInput input, String loggedInUserTenantId) {
		return favoriteSupplierDao.getAvailableBuyerListCount(input, loggedInUserTenantId);
	}

	@Override
	public long getTotalPublishedAvailableBuyerList(String loggedInUserTenantId) {
		return favoriteSupplierDao.getTotalPublishedAvailableBuyerList(loggedInUserTenantId);
	}

	@Override
	public long isSupplierInBuyerFavList(String buyerId, String loggedInUserLoginId) {
		return favoriteSupplierDao.isSupplierInBuyerFavList(buyerId, loggedInUserLoginId);
	}

	@Override
	public List<SupplierPojo> getTotalSuppliersFromGlobalList(TableDataInput input, String loggedInUserTenantId) {
		return favoriteSupplierDao.getTotalSuppliersFromGlobalList(input, loggedInUserTenantId);
	}

	@Override
	public long getTotalSuppliersCountFromGlobalList(TableDataInput input, String loggedInUserTenantId) {
		return favoriteSupplierDao.getTotalSuppliersCountFromGlobalList(input, loggedInUserTenantId);
	}

	@Override
	public long getTotalSuppliersCountForBuyerGlobalList(String loggedInUserTenantId) {
		return favoriteSupplierDao.getTotalSuppliersCountForBuyerGlobalList(loggedInUserTenantId);
	}

	@Override
	public long searchBuyersCount(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String loggedInUserTenantId) {
		return favoriteSupplierDao.searchBuyersCount(input, searchBuyerPojo, loggedInUserTenantId);
	}

	@Override
	public void sendEmailToFavSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone) {
		approvalService.sendEmailToFavSupplier(supplier, buyer, timeZone);
	}

	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierByIdAndStatus(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo, FavouriteSupplierStatus status) {
		List<FavouriteSupplier> list = favoriteSupplierDao.getAllFavouriteSupplierByIdAndStatus(favArr, loggedInUserTenantId, select_all, searchFilterSupplierPojo, status);
		for (FavouriteSupplier favouriteSupplier : list) {
			if (CollectionUtil.isNotEmpty(favouriteSupplier.getProductCategory())) {
				for (ProductCategory categoryList : favouriteSupplier.getProductCategory()) {
					categoryList.getProductName();
				}
			}
			if (CollectionUtil.isNotEmpty(favouriteSupplier.getSupplierTags())) {
				for (SupplierTags categoryList : favouriteSupplier.getSupplierTags()) {
					categoryList.getSupplierTags();
				}
			}
		}
		return list;
	}

	@Override
	public long getAssociatedBuyersCountForSupplier(String tenantId) {
		return favoriteSupplierDao.getAssociatedBuyersCountForSupplier(tenantId);
	}

	@Override
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListForSupplier(TableDataInput input, String tenantId) {
		return favoriteSupplierDao.getAssociatedBuyerListForSupplier(input, tenantId);
	}

	@Override
	public long getAssociatedBuyerCountForSupplier(TableDataInput input, String tenantId) {
		return favoriteSupplierDao.getAssociatedBuyerCountForSupplier(input, tenantId);
	}

	@Override
	public FavouriteSupplier getFavouritSupplier(SupplierSearchPojo searchParams, String buyerId) {
		return favoriteSupplierDao.getFavouritSupplier(searchParams, buyerId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> suspendSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (CollectionUtil.isNotEmpty(suppliers)) {
			Date start = null;
			Date end = null;

			for (SupplierSuspendIntegrationPojo supp : suppliers) {
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setCompanyName(supp.getCompanyName());
				pojo.setCompanyRegistrationNumber(supp.getCompanyRegistrationNumber());
				FavouriteSupplier favSupplier = favoriteSupplierDao.getFavouritSupplier(pojo, tenantId);
				if (favSupplier != null && FavouriteSupplierStatus.ACTIVE == favSupplier.getStatus()) {
					LOG.info("Suspending : " + favSupplier.getSupplier().getCompanyName() + " Start Date : " + supp.getStartDateStr());
					User user = userService.getAdminUserForBuyer(tenantId);
					if (StringUtils.checkString(supp.getStartDateStr()).length() > 0) {
						try {
							start = simpleDateFormat.parse(supp.getStartDateStr());
						} catch (ParseException e) {
						}
					}
					if (StringUtils.checkString(supp.getEndDateStr()).length() > 0) {
						try {
							end = simpleDateFormat.parse(supp.getEndDateStr());
						} catch (ParseException e) {
						}
					}

					favSupplier.setSuspendStartDate(start);
					favSupplier.setSuspendEndDate(end);
					favSupplier.setStatus(FavouriteSupplierStatus.SUSPENDED);
					favSupplier.setSuspendRemark(supp.getRemarks());
					favSupplier.setIsFutureSuspended(true);
					updateFavoriteSupplier(favSupplier, user, true);

					try {
						Supplier supplier = favoriteSupplierDao.getSupplierByFavSupplierId(favSupplier.getId());
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionDate(new Date());
						audit.setTenantId(tenantId);
						audit.setActionBy(user);
						audit.setFavSupp(supplier);
						audit.setDescription("Suspended");
						String remarks = StringUtils.checkString(supp.getRemarks());
						if (remarks.length() > 500) {
							remarks = remarks.substring(0, 499);
						}
						if (start != null && end != null) {
							audit.setRemark("ERP: " + supplier.getCompanyName() + " suspended from " + start + " to " + end + ". Remarks: " + remarks);
						} else if (start != null && end == null) {
							audit.setRemark("ERP: " + supplier.getCompanyName() + " suspended from " + start + ". Remarks: " + remarks);
						} else {
							audit.setRemark("ERP: " + supplier.getCompanyName() + " suspended. Remarks: " + remarks);
						}
						audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				} else {
					if (favSupplier == null) {
						LOG.warn("Supplier " + supp.getCompanyName() + " details not found");
						errorList.add("Supplier " + supp.getCompanyName() + " details not found");
					} else {
						LOG.warn("Supplier " + supp.getCompanyName() + " is not in active status");
					}
				}
			}
		}
		return errorList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> unSuspendSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (CollectionUtil.isNotEmpty(suppliers)) {

			Date start = null;
			Date end = null;
			for (SupplierSuspendIntegrationPojo supp : suppliers) {
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setCompanyName(supp.getCompanyName());
				pojo.setCompanyRegistrationNumber(supp.getCompanyRegistrationNumber());
				FavouriteSupplier favSupplier = getFavouritSupplier(pojo, tenantId);
				if (favSupplier != null && FavouriteSupplierStatus.SUSPENDED == favSupplier.getStatus()) {
					LOG.info("Un Suspending : " + favSupplier.getSupplier().getCompanyName() + " End Date : " + supp.getEndDateStr());
					User user = userService.getAdminUserForBuyer(tenantId);
					if (StringUtils.checkString(supp.getStartDateStr()).length() > 0) {
						try {
							start = simpleDateFormat.parse(supp.getStartDateStr());
						} catch (ParseException e) {
						}
					}
					if (StringUtils.checkString(supp.getEndDateStr()).length() > 0) {
						try {
							end = simpleDateFormat.parse(supp.getEndDateStr());
						} catch (ParseException e) {
						}
					}

					favSupplier.setSuspendStartDate(start);
					favSupplier.setSuspendEndDate(end);
					favSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
					favSupplier.setSuspendRemark(supp.getRemarks());
					favSupplier.setIsFutureSuspended(true);
					updateFavoriteSupplier(favSupplier, user, true);

					try {
						Supplier supplier = favoriteSupplierDao.getSupplierByFavSupplierId(favSupplier.getId());
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionDate(new Date());
						audit.setTenantId(tenantId);
						audit.setActionBy(user);
						audit.setFavSupp(supplier);
						String remarks = StringUtils.checkString(supp.getRemarks());
						if (remarks.length() > 500) {
							remarks = remarks.substring(0, 499);
						}
						audit.setDescription("Unsuspended");
						audit.setRemark(messageSource.getMessage("supplier.erpaudit.unsuspended", new Object[] { supplier.getCompanyName(), remarks }, Global.LOCALE));
						audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				} else {
					if (favSupplier == null) {
						LOG.warn("Supplier " + supp.getCompanyName() + " details not found");
						errorList.add("Supplier " + supp.getCompanyName() + " details not found");
					} else {
						LOG.warn("Supplier " + supp.getCompanyName() + " is not in suspend status");
					}
				}
			}
		}
		return errorList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> blockListSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(suppliers)) {

			for (SupplierSuspendIntegrationPojo supp : suppliers) {
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setCompanyName(supp.getCompanyName());
				pojo.setCompanyRegistrationNumber(supp.getCompanyRegistrationNumber());
				FavouriteSupplier favSupplier = getFavouritSupplier(pojo, tenantId);
				if (favSupplier != null && FavouriteSupplierStatus.ACTIVE == favSupplier.getStatus()) {
					LOG.info("Blocklist : " + favSupplier.getSupplier().getCompanyName());
					User user = userService.getAdminUserForBuyer(tenantId);
					favSupplier.setSuspendStartDate(null);
					favSupplier.setSuspendEndDate(null);
					favSupplier.setStatus(FavouriteSupplierStatus.BLACKLISTED);
					favSupplier.setBlackListRemark(supp.getRemarks());
					updateFavoriteSupplier(favSupplier, user, false);

					try {
						Supplier supplier = favoriteSupplierDao.getSupplierByFavSupplierId(favSupplier.getId());
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionDate(new Date());
						audit.setTenantId(tenantId);
						audit.setActionBy(user);
						audit.setFavSupp(supplier);
						audit.setDescription("Blacklisted");

						String remarks = StringUtils.checkString(supp.getRemarks());
						if (remarks.length() > 500) {
							remarks = remarks.substring(0, 499);
						}
						audit.setRemark(messageSource.getMessage("supplier.erpAudit.blacklisted", new Object[] { supplier.getCompanyName(), remarks }, Global.LOCALE));

						audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				} else {
					if (favSupplier == null) {
						LOG.warn("Supplier " + supp.getCompanyName() + " details not found");
						errorList.add("Supplier " + supp.getCompanyName() + " details not found");
					} else {
						LOG.warn("Supplier " + supp.getCompanyName() + " is not in active status");
						errorList.add("Supplier " + supp.getCompanyName() + " is not in active status");
					}
				}
			}
		}
		return errorList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> releaseSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(suppliers)) {

			for (SupplierSuspendIntegrationPojo supp : suppliers) {
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setCompanyName(supp.getCompanyName());
				pojo.setCompanyRegistrationNumber(supp.getCompanyRegistrationNumber());
				FavouriteSupplier favSupplier = getFavouritSupplier(pojo, tenantId);
				if (favSupplier != null && FavouriteSupplierStatus.BLACKLISTED == favSupplier.getStatus()) {
					LOG.info("Release  : " + favSupplier.getSupplier().getCompanyName());
					User user = userService.getAdminUserForBuyer(tenantId);
					favSupplier.setSuspendStartDate(null);
					favSupplier.setSuspendEndDate(null);
					favSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
					favSupplier.setBlackListRemark(supp.getRemarks());
					updateFavoriteSupplier(favSupplier, user, false);

					try {
						Supplier supplier = favoriteSupplierDao.getSupplierByFavSupplierId(favSupplier.getId());
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionDate(new Date());
						audit.setTenantId(tenantId);
						audit.setActionBy(user);
						audit.setFavSupp(supplier);
						audit.setDescription("Unblacklisted");
						String remarks = StringUtils.checkString(supp.getRemarks());
						if (remarks.length() > 500) {
							remarks = remarks.substring(0, 499);
						}
						audit.setRemark(messageSource.getMessage("supplier.erpAudit.unblacklisted", new Object[] { supplier.getCompanyName(), remarks }, Global.LOCALE));
						favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				} else {
					if (favSupplier == null) {
						LOG.warn("Supplier " + supp.getCompanyName() + " details not found");
						errorList.add("Supplier " + supp.getCompanyName() + " details not found");
					} else {
						LOG.warn("Supplier " + supp.getCompanyName() + " is not in blacklist status");
						errorList.add("Supplier " + supp.getCompanyName() + " is not in blacklist status");
					}
				}
			}
		}
		return errorList;
	}

	@Override
	public List<Supplier> searchBuyerSuppliers(String tenantId, String search, String id) {
		List<Supplier> list = favoriteSupplierDao.searchBuyerSuppliers(tenantId, search, id);
		long count = favoriteSupplierDao.countConstructQueryToFetchFavouriteSupplier(tenantId, id);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				Supplier more = new Supplier();
				more.setCompanyName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}

	@Override
	public List<SupplierPojo> searchFavouriteSupplier(String tenantId, String search, String id) {
		List<SupplierPojo> list = favoriteSupplierDao.searchFavouriteSupplier(tenantId, search, id);
		long count = favoriteSupplierDao.countConstructQueryToFetchFavouriteSupplier(tenantId, id);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list.size() < count) {
				SupplierPojo more = new SupplierPojo();
				more.setCompanyName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}

	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByBuyerId(String loggedInUserTenantId) {
		return favoriteSupplierDao.getAllActiveFavouriteSupplierByBuyerId(loggedInUserTenantId);
	}

	@Override
	public List<SupplierPojo> getAllSearchFavouriteSupplierByBuyerId(String loggedInUserTenantId, String searchValue) {
		List<SupplierPojo> list = favoriteSupplierDao.searchFavouriteSupplier(loggedInUserTenantId, searchValue, null);
		long count = favoriteSupplierDao.countConstructQueryToFetchFavouriteSupplier(loggedInUserTenantId, null);
		LOG.info("Count: " + count + " List size: " + list.size());
		if (list != null && count > list.size()) {
			SupplierPojo more = new SupplierPojo();
			more.setId("-1");
			more.setCompanyName("Total " + (count) + " suppliers. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> inActivateSuppliers(List<SupplierActivationIntegrationPojo> suppliers, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(suppliers)) {

			for (SupplierActivationIntegrationPojo supp : suppliers) {
				LOG.info("Supplier : " + supp.toLogString());
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setCompanyName(StringUtils.checkString(supp.getCompanyName()));
				pojo.setCompanyRegistrationNumber(StringUtils.checkString(supp.getCompanyRegistrationNumber()));
				FavouriteSupplier favSupplier = getFavouritSupplier(pojo, tenantId);
				if (favSupplier != null && FavouriteSupplierStatus.ACTIVE == favSupplier.getStatus()) {
					LOG.info("Inactivate : " + favSupplier.getSupplier().getCompanyName());
					User user = userService.getAdminUserForBuyer(tenantId);
					favSupplier.setStatus(FavouriteSupplierStatus.INACTIVE);
					favSupplier.setBlackListRemark(supp.getRemarks());
					updateFavoriteSupplier(favSupplier, user, false);

					try {
						Supplier supplier = favoriteSupplierDao.getSupplierByFavSupplierId(favSupplier.getId());
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionDate(new Date());
						audit.setTenantId(tenantId);
						audit.setActionBy(user);
						audit.setFavSupp(supplier);
						audit.setDescription("Inactive");

						String remarks = StringUtils.checkString(supp.getRemarks());
						if (remarks.length() > 500) {
							remarks = remarks.substring(0, 499);
						}
						String message = messageSource.getMessage("supplier.erpAudit.inactivated", new Object[] { supplier.getCompanyName() }, Global.LOCALE);
						if (StringUtils.checkString(remarks).length() > 0) {
							message = message.concat(" Remarks:  " + remarks);
						}
						audit.setRemark(message);

						audit = favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				} else {
					if (favSupplier == null) {
						LOG.warn("Supplier " + supp.getCompanyName() + " details not found");
						errorList.add("Supplier " + supp.getCompanyName() + " details not found");
					} else {
						LOG.warn("Supplier " + supp.getCompanyName() + " is not in active status");
						errorList.add("Supplier " + supp.getCompanyName() + " is not in active status");
					}
				}
			}
		}
		return errorList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> activateSuppliers(List<SupplierActivationIntegrationPojo> suppliers, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(suppliers)) {

			for (SupplierActivationIntegrationPojo supp : suppliers) {
				LOG.info("Supplier : " + supp.toLogString());
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setCompanyName(StringUtils.checkString(supp.getCompanyName()));
				pojo.setCompanyRegistrationNumber(StringUtils.checkString(supp.getCompanyRegistrationNumber()));
				FavouriteSupplier favSupplier = getFavouritSupplier(pojo, tenantId);
				if (favSupplier != null && FavouriteSupplierStatus.INACTIVE == favSupplier.getStatus()) {
					LOG.info("Activate  : " + favSupplier.getSupplier().getCompanyName());
					User user = userService.getAdminUserForBuyer(tenantId);
					favSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
					favSupplier.setBlackListRemark(supp.getRemarks());
					updateFavoriteSupplier(favSupplier, user, false);

					try {
						Supplier supplier = favoriteSupplierDao.getSupplierByFavSupplierId(favSupplier.getId());
						FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
						audit.setActionDate(new Date());
						audit.setTenantId(tenantId);
						audit.setActionBy(user);
						audit.setFavSupp(supplier);
						audit.setDescription("Activated");
						String remarks = StringUtils.checkString(supp.getRemarks());
						if (remarks.length() > 500) {
							remarks = remarks.substring(0, 499);
						}
						String message = messageSource.getMessage("supplier.erpAudit.activated", new Object[] { supplier.getCompanyName() }, Global.LOCALE);
						if (StringUtils.checkString(remarks).length() > 0) {
							message = message.concat(" Remarks:  " + remarks);
						}
						audit.setRemark(message);
						favSuppAuditService.saveFavouriteSupplierAudit(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				} else {
					if (favSupplier == null) {
						LOG.warn("Supplier " + supp.getCompanyName() + " details not found");
						errorList.add("Supplier " + supp.getCompanyName() + " details not found");
					} else {
						LOG.warn("Supplier " + supp.getCompanyName() + " is not in inactive status");
						errorList.add("Supplier " + supp.getCompanyName() + " is not in inactive status");
					}
				}
			}
		}
		return errorList;
	}

	@Override
	public void downloadCsvFileForSupplierList(HttpServletResponse response, File file, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.DOWNLOAD_SUPPLIER_CSV_COLUMNS;
			String[] columns = { "companyName", "countryCode", "companyRegistrationNumber", "fullName", "designation", "mobileNumber", "companyContactNumber", "loginEmail", "faxNumber", "industryCategory", "productCategory", "vendorCode", "status", "ratings", "communicationEmail", "yearOfEstablished", "registrationCompleteDateStr", "supplierTags" };

			long count = favoriteSupplierDao.getTotalSupplierCountForList(tenantId, searchFilterSupplierPojo.getStatus());
			LOG.info("................ count " + count);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<SupplierPojo> list = findAllActiveSupplierForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, favArr, searchFilterSupplierPojo, select_all);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (SupplierPojo pojo : list) {
					if (pojo.getRegistrationCompleteDate() != null) {
						LOG.info("*********************" + pojo.getRegistrationCompleteDate());
						pojo.setRegistrationCompleteDateStr(sdf.format(pojo.getRegistrationCompleteDate()));
					}

					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.error("Error ..." + e.getMessage(), e);
		}

	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] { new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // companyContactNumber
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // communicationEmail
				new Optional(), //
				new Optional(), //
				new Optional(), //
		};
		return processors;
	}

	private List<SupplierPojo> findAllActiveSupplierForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all) {
		List<SupplierPojo> pojoList = new ArrayList<SupplierPojo>();
		SupplierPojo pojo = null;
		String seprator1 = " , ";

		List<FavouriteSupplier> list = favoriteSupplierDao.findAllActiveSupplierForTenantIdForCsv(tenantId, pageSize, pageNo, favArr, searchFilterSupplierPojo, select_all);

		if (CollectionUtil.isNotEmpty(list)) {
			for (FavouriteSupplier data : list) {
				List<ProductCategory> productCat = data.getProductCategory();
				List<String> pcat = new ArrayList<>();
				pojo = new SupplierPojo();
				String pcCat = "";
				if (CollectionUtil.isNotEmpty(productCat)) {
					for (ProductCategory productcategory : productCat) {
						pcat.add(productcategory.getProductCode() + "-" + productcategory.getProductName());
					}
					pcCat = String.join(seprator1, pcat);
				}
				pojo.setProductCategory(StringUtils.checkString(pcCat).length() > 0 ? pcCat : "");

				List<IndustryCategory> icList = data.getIndustryCategory();
				List<String> indCat = new ArrayList<>();
				if (CollectionUtil.isNotEmpty(icList)) {
					for (IndustryCategory inCategory : icList) {
						indCat.add(inCategory.getCode() + "-" + inCategory.getName());
					}
				}
				pojo.setIndustryCategory(String.join(seprator1, indCat));

				List<SupplierTags> tagsList = data.getSupplierTags();
				List<String> suppTag = new ArrayList<>();
				if (CollectionUtil.isNotEmpty(tagsList)) {
					for (SupplierTags tag : tagsList) {
						suppTag.add(tag.getSupplierTags());
					}
				}
				pojo.setSupplierTags(String.join(seprator1, suppTag));

				pojo.setId(data.getId());
				pojo.setCompanyName(data.getSupplier().getCompanyName());
				pojo.setCountryCode(data.getSupplier().getRegistrationOfCountry().getCountryCode());
				pojo.setCompanyRegistrationNumber(data.getSupplier().getCompanyRegistrationNumber());
				pojo.setFullName(data.getFullName());
				pojo.setDesignation(data.getDesignation());
				pojo.setMobileNumber(data.getSupplier().getMobileNumber());
				pojo.setCompanyContactNumber(data.getCompanyContactNumber());
				pojo.setLoginEmail(data.getSupplier().getLoginEmail());
				pojo.setFaxNumber(data.getSupplier().getFaxNumber());
				pojo.setVendorCode(data.getVendorCode());
				pojo.setStatus(data.getStatus().toString());
				pojo.setRatings(data.getRatings());
				pojo.setCommunicationEmail(data.getCommunicationEmail());
				pojo.setYearOfEstablished(data.getSupplier().getYearOfEstablished());
				pojo.setRegistrationCompleteDate(data.getSupplier().getRegistrationCompleteDate());

				pojoList.add(pojo);
			}
		}
		return pojoList;
	}

	@Override
	public void downloadCsvFileForSupplierList(HttpServletResponse response, File file, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, FavouriteSupplierStatus favStatus, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.DOWNLOAD_SUPPLIER_CSV_COLUMNS;
			String[] columns = { "companyName", "countryCode", "companyRegistrationNumber", "fullName", "designation", "mobileNumber", "companyContactNumber", "loginEmail", "faxNumber", "industryCategory", "productCategory", "vendorCode", "status", "ratings", "communicationEmail", "yearOfEstablished", "registrationCompleteDateStr", "supplierTags" };

			long count = favoriteSupplierDao.getTotalSupplierCountByIdAndStatusForList(tenantId, favStatus);
			LOG.info("................ count " + count);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<SupplierPojo> list = findAllActiveSupplierByTenantIdAndStatusForCsv(tenantId, PAGE_SIZE, pageNo, favArr, searchFilterSupplierPojo, select_all, favStatus);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (SupplierPojo pojo : list) {
					if (pojo.getRegistrationCompleteDate() != null) {
						LOG.info("*********************" + pojo.getRegistrationCompleteDate());
						pojo.setRegistrationCompleteDateStr(sdf.format(pojo.getRegistrationCompleteDate()));
					}
					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.error("Error ..." + e.getMessage(), e);
		}
	}

	private List<SupplierPojo> findAllActiveSupplierByTenantIdAndStatusForCsv(String tenantId, int pageSize, int pageNo, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, FavouriteSupplierStatus favStatus) {
		List<SupplierPojo> pojoList = new ArrayList<SupplierPojo>();
		SupplierPojo pojo = null;
		String seprator1 = " , ";

		List<FavouriteSupplier> list = favoriteSupplierDao.findAllActiveSupplierByTenantIdAndStatusForCsv(pageSize, pageNo, favArr, searchFilterSupplierPojo, select_all, favStatus, tenantId);

		if (CollectionUtil.isNotEmpty(list)) {
			for (FavouriteSupplier data : list) {
				List<ProductCategory> productCat = data.getProductCategory();
				List<String> pcat = new ArrayList<>();
				pojo = new SupplierPojo();
				String pcCat = "";
				if (CollectionUtil.isNotEmpty(productCat)) {
					for (ProductCategory productcategory : productCat) {
						pcat.add(productcategory.getProductCode() + "-" + productcategory.getProductName());
					}
					pcCat = String.join(seprator1, pcat);
				}
				pojo.setProductCategory(StringUtils.checkString(pcCat).length() > 0 ? pcCat : "");

				List<IndustryCategory> icList = data.getIndustryCategory();
				List<String> indCat = new ArrayList<>();
				if (CollectionUtil.isNotEmpty(icList)) {
					for (IndustryCategory inCategory : icList) {
						indCat.add(inCategory.getCode() + "-" + inCategory.getName());
					}
				}
				pojo.setIndustryCategory(String.join(seprator1, indCat));

				List<SupplierTags> tagsList = data.getSupplierTags();
				List<String> suppTag = new ArrayList<>();
				if (CollectionUtil.isNotEmpty(tagsList)) {
					for (SupplierTags tag : tagsList) {
						suppTag.add(tag.getSupplierTags());
					}
				}
				pojo.setSupplierTags(String.join(seprator1, suppTag));

				pojo.setId(data.getId());
				pojo.setCompanyName(data.getSupplier().getCompanyName());
				pojo.setCountryCode(data.getSupplier().getRegistrationOfCountry().getCountryCode());
				pojo.setCompanyRegistrationNumber(data.getSupplier().getCompanyRegistrationNumber());
				pojo.setFullName(data.getFullName());
				pojo.setDesignation(data.getDesignation());
				pojo.setMobileNumber(data.getSupplier().getMobileNumber());
				pojo.setCompanyContactNumber(data.getCompanyContactNumber());
				pojo.setLoginEmail(data.getSupplier().getLoginEmail());
				pojo.setFaxNumber(data.getSupplier().getFaxNumber());
				pojo.setVendorCode(data.getVendorCode());
				pojo.setStatus(data.getStatus().toString());
				pojo.setRatings(data.getRatings());
				pojo.setCommunicationEmail(data.getCommunicationEmail());
				pojo.setYearOfEstablished(data.getSupplier().getYearOfEstablished());
				pojo.setRegistrationCompleteDate(data.getSupplier().getRegistrationCompleteDate());

				pojoList.add(pojo);
			}
		}
		return pojoList;
	}

	@Override
	@Transactional(readOnly = true)
	public String getSupplierNameByVendorCode(String vendorCode, String buyerId) {
		return favoriteSupplierDao.getSupplierNameByVendorCode(vendorCode, buyerId);
	}

	public static void main(String[] args) {
		String companyNameRegex = "^[a-zA-Z0-9\\s .,-_&()]+$";
		Pattern pat = Pattern.compile(companyNameRegex);
		if (!pat.matcher("RUBIC SDN. BHD.,/\\ -_&()").matches()) {
			System.out.println("Company name can only contain alphanumeric characters and &/\\(),._- ");
		}
	}
}