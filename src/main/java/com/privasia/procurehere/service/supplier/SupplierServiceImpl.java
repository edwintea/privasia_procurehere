package com.privasia.procurehere.service.supplier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.NotesDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PoItemDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.PrDocumentDao;
import com.privasia.procurehere.core.dao.PrItemDao;
import com.privasia.procurehere.core.dao.PromotionalCodeDao;
import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.dao.SupplierAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.dao.SupplierFormItemDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmissionDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmitionAuditDao;
import com.privasia.procurehere.core.dao.SupplierPlanDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrContact;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.entity.PrTemplateField;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierOtherCredentials;
import com.privasia.procurehere.core.entity.SupplierOtherDocuments;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.Coverage.CoverageType;
import com.privasia.procurehere.core.pojo.ExtentionValidity;
import com.privasia.procurehere.core.pojo.NotesPojo;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierDetailsCountPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierReportPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierAssociatedBuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierBoardOfDirectorsDao;
import com.privasia.procurehere.core.supplier.dao.SupplierCompanyProfileDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.supplier.dao.SupplierFinancialDocUploadDao;
import com.privasia.procurehere.core.supplier.dao.SupplierFinancialDocumentsDao;
import com.privasia.procurehere.core.supplier.dao.SupplierOtherCredentialUploadDao;
import com.privasia.procurehere.core.supplier.dao.SupplierOtherDocumentUploadDao;
import com.privasia.procurehere.core.supplier.dao.SupplierProfileUploadDao;
import com.privasia.procurehere.core.supplier.dao.SupplierProjectsDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.EmailSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FavoutireSupplierAuditService;
import com.privasia.procurehere.service.NaicsCodesService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.controller.PoItemsSummaryPojo;
import com.privasia.procurehere.web.controller.PoSummaryPojo;
import com.privasia.procurehere.web.controller.PrItemsSummaryPojo;
import com.privasia.procurehere.web.controller.PrSummaryPojo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class SupplierServiceImpl implements SupplierService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	SupplierProjectsDao supplierProjectsDao;

	@Autowired
	SupplierProfileUploadDao supplierProfileUploadDao;

	@Autowired
	SupplierFinancialDocUploadDao supplierFinancialDocDao;

	@Autowired
	SupplierBoardOfDirectorsDao supplierBoardOfDirectorsDao;

	@Autowired
	SupplierOtherCredentialUploadDao supplierOtherCredentialUploadDao;

	@Autowired
	SupplierOtherDocumentUploadDao supplierOtherDocumentUploadDao;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	UserRoleService userRoleService;

	@Autowired
	UserService userService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	VelocityEngine velocityEngine;

	@Autowired
	EmailSettingsService emailSettingsService;

	@Autowired
	SupplierCompanyProfileDao supplierCompanyProfileDao;

	@Autowired
	SupplierFinancialDocumentsDao financialDocuments;

	@Autowired
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	NotesDao notesDao;

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	UserDao userDao;

	@Autowired
	RfxViewDao rfxViewDao;

	@Autowired
	PrDao prDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	PrItemDao prItemDao;

	@Autowired
	PrDocumentDao prDocumentDao;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	NaicsCodesService industryCategoryService;

	@Autowired
	SupplierAssociatedBuyerDao associatedBuyerDao;

	@Autowired
	PoDao poDao;

	@Autowired
	PoItemDao poItemDao;

	@Autowired
	PromotionalCodeDao promotionalCodeDao;

	@Autowired
	private Environment env;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	SupplierPlanDao supplierPlanDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	FavoutireSupplierAuditService favSuppAuditService;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	SupplierFormItemDao supplierFormItemDao;

	@Autowired
	SupplierFormSubmissionDao supplierFormSubmissDao;

	@Autowired
	SupplierFormDao supplierFormDao;

	@Autowired
	SupplierFormSubmitionAuditDao supplierFormSubmitionAuditDao;

	@Autowired
	SupplierFormSubmissionService supplierFormSubmissionService;

	@Autowired
	TimeZoneDao timeZoneDao;

	@Override
	@Transactional(readOnly = false)
	public Supplier saveSupplier(Supplier supplier, boolean sendNotification) throws Exception {

		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		String password = enc.encode(supplier.getPassword());
		supplier.setPassword(password);
		supplier.setRegistrationDate(new Date());
		supplier.setCreatedDate(new Date());
		supplier = supplierDao.save(supplier);

		if (!sendNotification) {
			return supplier;
		}

		LOG.info("sendNotification: " + sendNotification);

		/**
		 * Sent notification to admin
		 */
		try {
			StringBuffer content = new StringBuffer();
			try {
				String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);

				Map<String, Object> model = new HashMap<String, Object>();
				model.put("contactPerson", supplier.getFullName());
				model.put("companyName", supplier.getCompanyName());
				model.put("comunicationEmail", supplier.getCommunicationEmail());
				model.put("mobileNumber", supplier.getMobileNumber());
				SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
				model.put("date", format.format(new Date()));
				model.put("app_url", (StringUtils.checkString(appPath) + "/login"));
				content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/templates/Supplier_request.vm", "UTF-8", model));

				/**
				 * @author Nitin Otageri Bad way of getting owner settings. Need to improve later
				 */
				String ownerEmailForNotification = "nitin@dhriti-solution.com";
				List<OwnerSettings> settingsList = ownerSettingsDao.findAll(OwnerSettings.class);
				if (CollectionUtil.isNotEmpty(settingsList)) {
					OwnerSettings settings = settingsList.get(0);
					if (settings != null && StringUtils.checkString(settings.getSupplierSignupNotificationEmailAccount()).length() > 0) {
						ownerEmailForNotification = settings.getSupplierSignupNotificationEmailAccount();
					}
				}

				notificationService.sendEmail(ownerEmailForNotification, "Supplier Registration Request", content.toString());

				content = new StringBuffer();
				content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/templates/Supplier_submit_application.vm", "UTF-8", model));

					notificationService.sendEmail(supplier.getCommunicationEmail(), "Procurehere: Sign Up Details Received", content.toString());

				LOG.info("Email sent to: " + supplier.getCommunicationEmail());
			} catch (Exception e) {
				LOG.error("Exception occured while processing template:" + e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Error while sending email notification " + e.getMessage(), e);
		}

		return supplier;
	}

	@Override
	public Supplier findSupplierSubscriptionDetailsBySupplierId(String id) {
		return supplierDao.findSupplierSubscriptionDetailsBySupplierId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Supplier findSupplierSubscriptionDetailsBySupplierIdExcludedExpiredBuyers(String id) {
		Supplier supplier = supplierDao.findSupplierSubscriptionDetailsBySupplierIdExcludedExpiredBuyers(id);
		if (supplier != null) {
			if (supplier.getSupplierSubscription() != null) {
				supplier.getSupplierSubscription().getBuyerLimit();
				if (supplier.getSupplierSubscription().getSupplierPlan() != null) {
					supplier.getSupplierSubscription().getSupplierPlan().getDescription();
				}
			}
		}
		return supplier;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExists(Supplier supplier) {
		return supplierDao.isExists(supplier);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExistsLoginEmail(String loginEmail) {
		return supplierDao.isExistsLoginEmail(loginEmail);

	}

	@Override
	@Transactional(readOnly = false)
	public Supplier updateSupplier(Supplier supplier) {
		return supplierDao.saveOrUpdate(supplier);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplier(Supplier supplier) {
		supplierDao.delete(supplier);
	}

	@Override
	public List<Supplier> findAllactiveSuppliers() {
		return supplierDao.findAllactiveSuppliers();
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSupplierCommunicationEmail(String supplierId, String oldCommunicationEmail, String newCommunicationEmail) {
		supplierDao.updateSupplierCommunicationEmail(supplierId, oldCommunicationEmail, newCommunicationEmail);
	}

	@Override
	public Supplier findSupplierById(String supplierId) {
		Supplier supplier = supplierDao.findById(supplierId);
		if (CollectionUtil.isNotEmpty(supplier.getSupplierProjects())) {
			for (SupplierProjects projects : supplier.getSupplierProjects()) {
				projects.getProjectName();
				if (CollectionUtil.isNotEmpty(projects.getProjectIndustries())) {
					for (NaicsCodes category : projects.getProjectIndustries()) {
						category.getCategoryName();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getSupplierCompanyProfile())) {
			for (SupplierCompanyProfile profile : supplier.getSupplierCompanyProfile()) {
				profile.getFileName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getAssociatedBuyers())) {
			for (Buyer buyer : supplier.getAssociatedBuyers()) {
				buyer.getFullName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getNaicsCodes())) {
			for (NaicsCodes code : supplier.getNaicsCodes()) {
				code.getCategoryName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getSupplierOtherCredentials())) {
			for (SupplierOtherCredentials other : supplier.getSupplierOtherCredentials()) {
				other.getFileName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getCountries())) {
			for (Country country : supplier.getCountries()) {
				country.getCountryCode();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getStates())) {
			for (State state : supplier.getStates()) {
				state.getStateCode();
				if (state.getCountry() != null) {
					state.getCountry().getCountryCode();
				}
			}
		}

		List<Coverage> coverages = new ArrayList<Coverage>();

		List<Country> list = supplier.getCountries();
		List<State> stateList = supplier.getStates();

		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				Coverage coverage = new Coverage();
				coverage.setId(country.getId());
				coverage.setCode(country.getCountryCode());
				coverage.setName(country.getCountryName());
				coverage.setType(CoverageType.COUNTRY);
				coverages.add(coverage);
			}
			if (CollectionUtil.isNotEmpty(stateList)) {
				for (State state : stateList) {
					Coverage child = new Coverage();
					child.setId(state.getId());
					child.setCode(state.getStateCode());
					child.setName(state.getStateName());
					child.setType(CoverageType.STATE);
					coverages.add(child);
				}
			}
		}
		if (CollectionUtil.isNotEmpty(coverages))
			supplier.setCoverages(coverages);

		if (supplier.getSupplierPackage() != null) {
			supplier.getSupplierPackage().getActivatedDate();
		}

		if (supplier.getSupplierSubscription() != null) {
			supplier.getSupplierSubscription().getBuyerLimit();
		}

		if (CollectionUtil.isNotEmpty(supplier.getSupplierFinancialDocuments())) {
			for (SupplierFinanicalDocuments docs : supplier.getSupplierFinancialDocuments()) {
				docs.getDescription();
				docs.getFileData();
				docs.getFileName();
				docs.getFinancialDocContentType();
				docs.getId();
				docs.getUploadDate();
			}

		}

		if (CollectionUtil.isNotEmpty(supplier.getSupplierBoardOfDirectors())) {
			for (SupplierBoardOfDirectors dirs : supplier.getSupplierBoardOfDirectors()) {
				dirs.getDirContact();
				dirs.getDirEmail();
				dirs.getDirType();
				dirs.getDirectorName();
				dirs.getId();
				dirs.getIdNumber();
				dirs.getIdType();
				dirs.getSupplier();
				dirs.getSupplier().getId();
			}

		}

		if (supplier.getCurrency() != null) {
			Currency currency = supplier.getCurrency();
			if (currency != null)
				currency.getCurrencyName();
		}

		if (supplier.getSupplierSubscription() != null) {
			SupplierSubscription currentSubscription = supplier.getSupplierSubscription();
			while (currentSubscription.getNextSubscription() != null) {
				currentSubscription = currentSubscription.getNextSubscription();
			}
		}

		if (supplier.getCreatedBy() != null) {
			supplier.getCreatedBy().getName();
		}

		if (supplier.getActionBy() != null) {
			supplier.getActionBy().getName();
		}

		return supplier;
	}

	@Override
	public Supplier findSupplierForAdminProfileById(String supplierId) {
		Supplier supplier = supplierDao.findById(supplierId);
		if (CollectionUtil.isNotEmpty(supplier.getSupplierProjects())) {
			for (SupplierProjects projects : supplier.getSupplierProjects()) {
				projects.getProjectName();
				if (CollectionUtil.isNotEmpty(projects.getProjectIndustries())) {
					for (NaicsCodes category : projects.getProjectIndustries()) {
						category.getCategoryName();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getSupplierCompanyProfile())) {
			for (SupplierCompanyProfile profile : supplier.getSupplierCompanyProfile()) {
				profile.getFileName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getAssociatedBuyers())) {
			for (Buyer buyer : supplier.getAssociatedBuyers()) {
				buyer.getFullName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getNaicsCodes())) {
			for (NaicsCodes code : supplier.getNaicsCodes()) {
				code.getCategoryName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getSupplierOtherCredentials())) {
			for (SupplierOtherCredentials other : supplier.getSupplierOtherCredentials()) {
				other.getFileName();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getCountries())) {
			for (Country country : supplier.getCountries()) {
				country.getCountryCode();
			}
		}
		if (CollectionUtil.isNotEmpty(supplier.getStates())) {
			for (State state : supplier.getStates()) {
				state.getStateCode();
				if (state.getCountry() != null) {
					state.getCountry().getCountryCode();
				}
			}
		}

		List<Coverage> coverages = new ArrayList<Coverage>();

		List<Country> list = supplier.getCountries();
		List<State> stateList = supplier.getStates();

		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				Coverage coverage = new Coverage();
				coverage.setId(country.getId());
				coverage.setCode(country.getCountryCode());
				coverage.setName(country.getCountryName());
				coverage.setType(CoverageType.COUNTRY);
				coverages.add(coverage);
			}
			if (CollectionUtil.isNotEmpty(stateList)) {
				for (State state : stateList) {
					Coverage child = new Coverage();
					child.setId(state.getId());
					child.setCode(state.getStateCode());
					child.setName(state.getStateName());
					child.setType(CoverageType.STATE);
					coverages.add(child);
				}
			}
		}
		if (CollectionUtil.isNotEmpty(coverages))
			supplier.setCoverages(coverages);

		if (supplier.getSupplierPackage() != null) {
			supplier.getSupplierPackage().getActivatedDate();
			if (supplier.getSupplierPackage().getSupplierPlan() != null) {
				supplier.getSupplierPackage().getSupplierPlan().getPlanName();
			}
		}

		if (supplier.getSupplierSubscription() != null) {
			supplier.getSupplierSubscription().getBuyerLimit();
			if (supplier.getSupplierSubscription().getSupplierPlan() != null) {
				supplier.getSupplierSubscription().getSupplierPlan().getPlanName();
			}
			supplier.getSupplierSubscription().getPromoCode();
			LOG.info("%%%%%%%%%%" + supplier.getSupplierSubscription().getPromoCode());
		}

		if (supplier.getSupplierPackage() != null) {
			supplier.getSupplierPackage().getEndDate();
		}

		if (supplier.getCurrency() != null) {
			supplier.getCurrency().getCurrencyCode();
		}
		return supplier;
	}

	@Override
	public List<SupplierPojo> findPendingSuppliers() {
		return supplierDao.searchSupplierForPagination(null, "Newest", null, "0");
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = ApplicationException.class)
	public Supplier confirmSupplier(Supplier supplier, boolean sendNotification) throws ApplicationException {
		LOG.info("++++++++++++++++++supplier.getFullName()+++++++++++++" + supplier.getFullName());
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
			supplier.setApprovedDate(new Date());
			supplier = supplierDao.saveOrUpdate(supplier);
			if (SupplierStatus.APPROVED == supplier.getStatus()) {
				/**
				 * Create User Role
				 */
				UserRole userRole = new UserRole();
				userRole = new UserRole();
				userRole.setRoleName("Administrator".toUpperCase());
				userRole.setRoleDescription("Application Administrator");
				userRole.setCreatedDate(new Date());
				userRole.setTenantId(supplier.getId());
				User createdBy = null;
				try {
					createdBy = SecurityLibrary.getLoggedInUser();
				} catch (SecurityRuntimeException e) {
					// e.printStackTrace();
				}
				if (createdBy == null) {
					if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
						createdBy = userService.getUserByLoginId("admin@procurehere.com");
					} else {
						createdBy = userService.getUserByLoginId("admin@smebank.com");
					}
				}
				userRole.setCreatedBy(createdBy);

				userRoleService.saveUserRole(userRole, accessRightsDao.getAccessControlListForSupplier(false));
				// LOG.info("SUPPLIER CONFIRMATION USER ROLE CREATED FOR SUPPLIER [ " + supplier.getCompanyName() + "]
				// ");
				/**
				 * Create user and send email notification to supplier
				 */
				User user = new User();
				user.setLoginId(supplier.getLoginEmail().toUpperCase());
				user.setCommunicationEmail(supplier.getCommunicationEmail());
				user.setTenantId(supplier.getId());
				user.setTenantType(TenantType.SUPPLIER);
				user.setName(supplier.getCompanyName());
				user.setPassword(supplier.getPassword());
				user.setSupplier(supplier);
				user.setCreatedDate(new Date());
				if (sendNotification) {
					user.setLastPasswordChangedDate(new Date());
				}

				SupplierSubscription supplierSubscription = supplierSubscriptionDao.getCurrentSubscriptionForSupplier(supplier.getId());
				if (supplierSubscription != null) {
					if (supplierSubscription.getSupplierPlan() != null) {
						user.setShowWizardTutorial(Boolean.FALSE);
					} else {
						user.setShowWizardTutorial(Boolean.TRUE);
					}
				}

				user.setCreatedBy(createdBy);
				user.setUserRole(userRole);
				userService.saveUser(user);

				LOG.info("SUPPLIER CONFIRMATION USER CREATED FOR SUPPLIER [ " + supplier.getCompanyName() + "] LOGIN ID : [" + supplier.getLoginEmail() + " ]");

				if (sendNotification) {
					/**
					 * Send email notification start
					 */
					String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);
					StringBuffer content = new StringBuffer();
					try {
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("fullName", supplier.getFullName());
						// model.put("clickHere", "<a class=\"email_bttn\" href=\"" + StringUtils.checkString(appPath) +
						// "/login\">CLICK HERE TO GET STARTED</a>");
						model.put("appLink", StringUtils.checkString(appPath) + "/login");
						model.put("date", format.format(new Date()));
						content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/templates/Supplier_approv.vm", "UTF-8", model));
					} catch (Exception e) {
						System.out.println("Exception occured while processing template:" + e.getMessage());
					}
					/*User user1 = userDao.getDetailsOfLoggedinUser(supplier.getLoginEmail());
					if(user1.getEmailNotifications()) {*/
						notificationService.sendEmail(supplier.getCommunicationEmail(), "Supplier Registration Request is Approved", content.toString());
					//}
					LOG.info("SUPPLIER CONFIRMATION EMAIL SENT FOR SUPPLIER [ " + supplier.getCompanyName() + "] ");
				}

				createSupplierDefaultRoles(supplier.getId(), createdBy);
			} else {

				String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);
				StringBuffer content = new StringBuffer();
				try {
					Map<String, Object> model = new HashMap<String, Object>();
					// model.put("companyName", supplier.getCompanyName());
					model.put("fullName", supplier.getFullName());
					model.put("clickHere", "<a class=\"email_bttn\" href=\"" + StringUtils.checkString(appPath) + "/login\"> CLICK HERE TO EMAIL PROCUREHERE </a>");
					model.put("date", format.format(new Date()));
					content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/templates/Supplier_reject.vm", "UTF-8", model));
				} catch (Exception e) {
					System.out.println("Exception occured while processing template:" + e.getMessage());
				}
//				User user = userDao.getDetailsOfLoggedinUser(supplier.getLoginEmail());
//				if(user.getEmailNotifications()) {
					notificationService.sendEmail(supplier.getCommunicationEmail(), "Supplier Registration Request is Rejected", content.toString());
				//}
			}
		} catch (DataIntegrityViolationException de) {
			LOG.error("Error while approving supplier registration , " + de.getMessage(), de);
			throw new ApplicationException(de.getCause().getMessage());
		} catch (Exception e) {
			LOG.error("Error while approving supplier registration , " + e.getMessage(), e);
			throw new ApplicationException("Error while approving supplier registration , " + e.getMessage());
		}
		return supplier;
	}

	@Override
	public List<Supplier> searchSuppliers(String status, String order, String globalSearch) {
		return supplierDao.searchSupplier(status, order, globalSearch);
	}

	public SupplierCompanyProfile findCompanyProfileById(String id) {
		return supplierProfileUploadDao.findCompanyProfileById(id);
	}

	@Override
	public List<SupplierCompanyProfile> findCompanyProfileAll() {
		return supplierProfileUploadDao.findCompanyProfileAll();
	}

	@Override
	public List<SupplierCompanyProfile> findAllCompanyProfileBySupplierId(String supplierId) {
		return supplierProfileUploadDao.findAllCompanyProfileBySupplierId(supplierId);
	}

	@Override
	public SupplierOtherCredentials findOtherCredentialById(String id) {
		return supplierOtherCredentialUploadDao.findOtherCredentialById(id);
	}

	@Override
	public List<SupplierOtherCredentials> findOtherCredentialAll() {

		return supplierOtherCredentialUploadDao.findOtherCredentialAll();
	}

	@Override
	public List<SupplierOtherCredentials> findAllOtherCredentialBySupplierId(String supplierId) {

		return supplierOtherCredentialUploadDao.findAllOtherCredentialBySupplierId(supplierId);

	}

	@Override
	public SupplierProjects findSupplierProjectById(String projectId) {
		SupplierProjects supplierProjects = supplierProjectsDao.findById(projectId);
		if (CollectionUtil.isNotEmpty(supplierProjects.getProjectIndustries())) {
			for (NaicsCodes category : supplierProjects.getProjectIndustries()) {
				category.getCategoryName();
			}
		}
		return supplierProjects;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SupplierProjects saveSupplierProject(SupplierProjects supplierProjects) {

		List<Coverage> coverages = supplierProjects.getTracRecordCoverages();
		List<Country> countries = new ArrayList<Country>();
		List<State> states = new ArrayList<State>();
		if (CollectionUtil.isNotEmpty(coverages)) {
			for (Coverage coverage : coverages) {
				if (coverage.getType() == CoverageType.COUNTRY) {
					Country country = new Country();
					country.setCountryCode(coverage.getCode());
					country.setId(coverage.getId());
					country.setCountryName(coverage.getName());
					countries.add(country);
				} else {
					State state = new State();
					state.setStateCode(coverage.getCode());
					state.setId(coverage.getId());
					state.setStateName(coverage.getName());
					states.add(state);
				}
			}
			supplierProjects.setAssignedCountries(countries);
			supplierProjects.setAssignedStates(states);
		}

		return supplierProjectsDao.saveOrUpdate(supplierProjects);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateSupplierProject(SupplierProjects supplierProjects) {
		List<Coverage> coverages = supplierProjects.getTracRecordCoverages();
		List<Country> countries = new ArrayList<Country>();
		List<State> states = new ArrayList<State>();
		if (CollectionUtil.isNotEmpty(coverages)) {
			for (Coverage coverage : coverages) {
				if (coverage.getType() == CoverageType.COUNTRY) {
					Country country = new Country();
					country.setCountryCode(coverage.getCode());
					country.setId(coverage.getId());
					country.setCountryName(coverage.getName());
					countries.add(country);
				} else {
					State state = new State();
					state.setStateCode(coverage.getCode());
					state.setId(coverage.getId());
					state.setStateName(coverage.getName());
					states.add(state);
				}
			}
			supplierProjects.setAssignedCountries(countries);
			supplierProjects.setAssignedStates(states);
		}
		supplierProjectsDao.update(supplierProjects);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplierProject(SupplierProjects supplieProjects) {
		supplierProjectsDao.delete(supplieProjects);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SupplierCompanyProfile saveSupplierProfile(SupplierCompanyProfile supplierProjects) {
		return supplierCompanyProfileDao.saveOrUpdate(supplierProjects);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierProfile(SupplierCompanyProfile supplierProjects) {
		supplierCompanyProfileDao.delete(supplierProjects);
	}

	@Override
	public List<SupplierProjects> findProjectsForSupplierId(String supplierId) {
		return supplierProjectsDao.findProjectsForSupplierId(supplierId);
	}

	@Override
	public SupplierProjects findBySupplierId(String projectId) {

		SupplierProjects project = supplierProjectsDao.findById(projectId);
		if (project.getCurrency() != null) {
			Currency currency = project.getCurrency();
			if (currency != null)
				currency.getCurrencyName();
		}
		if (project.getSupplier() != null) {
			Supplier supplier = project.getSupplier();
			if (supplier != null)
				supplier.getId();
			supplier.getFullName();
		}
		List<Coverage> coverages = new ArrayList<Coverage>();

		List<Country> list = project.getAssignedCountries();
		List<State> stateList = project.getAssignedStates();

		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				Coverage coverage = new Coverage();
				coverage.setId(country.getId());
				coverage.setCode(country.getCountryCode());
				coverage.setName(country.getCountryName());
				coverage.setType(CoverageType.COUNTRY);
				coverages.add(coverage);
			}
		}
		if (CollectionUtil.isNotEmpty(stateList)) {
			for (State state : stateList) {
				Coverage child = new Coverage();
				child.setId(state.getId());
				child.setCode(state.getStateCode());
				child.setName(state.getStateName());
				child.setType(CoverageType.STATE);
				coverages.add(child);
			}
		}

		if (CollectionUtil.isNotEmpty(coverages))
			project.setTracRecordCoverages(coverages);
		return project;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeCompanyProfile(String profileId) {
		supplierCompanyProfileDao.deleteById(profileId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SupplierOtherCredentials saveSupplierOtherCredentials(SupplierOtherCredentials otherCredentials) {
		return supplierOtherCredentialUploadDao.saveOrUpdate(otherCredentials);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeOtherCredentials(String credentialId) {
		supplierOtherCredentialUploadDao.deleteById(credentialId);
	}

	@Override
	public List<Country> assignedCountriesForSupplierId(String supplierId) {
		Supplier supplier = supplierDao.findSupplierByIdForAssignedCountries(supplierId);
		if (supplier != null) {
			return supplier.getCountries();
		}
		return null;
	}

	@Override
	public Supplier findSupplierForProjectTrackById(String id) {
		return supplierDao.findSupplierForProjectTrackById(id);
	}

	@Override
	public List<Country> assignedCountriesForProjectTrackId(String projectId) {
		SupplierProjects supplierProject = supplierProjectsDao.findById(projectId);
		if (supplierProject != null) {
			return supplierProject.getAssignedCountries();
		}
		return null;
	}

	@Override
	public List<State> assignedStatesForSupplierId(String supplierId) {
		Supplier supplier = supplierDao.findSupplierByIdForAssignedStates(supplierId);
		if (supplier != null && CollectionUtil.isNotEmpty(supplier.getStates())) {
			return supplier.getStates();
		}
		return new ArrayList<State>();
	}

	@Override
	public List<Coverage> doSearchCoverage(String activeTab, String supplierId, String projectId, String[] selected, String search) {
		LOG.info("TAB : " + activeTab + "  Supplier Id : " + supplierId + " Project Id : " + projectId);

		List<Coverage> list = new ArrayList<Coverage>();
		List<Country> assignedCountires = null;
		List<State> assignedStates = null;

		/*
		 * Fetch assigned (saved) list of countries and states...
		 */
		if (Global.SUPPLIER_COVERAGE.equals(StringUtils.checkString(activeTab)) && StringUtils.checkString(supplierId).length() > 0) {
			LOG.info("Supplier assigned coverage details");
			assignedCountires = assignedCountriesForSupplierId(supplierId);
			assignedStates = assignedStatesForSupplierId(supplierId);
		} else {
			LOG.info("Project assigned coverage details");
			if (StringUtils.checkString(projectId).length() > 0) {
				SupplierProjects project = supplierProjectsDao.findById(projectId);
				if (project != null) {
					assignedCountires = project.getAssignedCountries();
					assignedStates = project.getAssignedStates();
				}
			}
		}

		if (assignedCountires == null) {
			assignedCountires = new ArrayList<Country>();
		}
		if (assignedStates == null) {
			assignedStates = new ArrayList<State>();
		}

		/*
		 * If the user has selected some Objects, then merge them into the assigned list for re-display
		 */
		if (selected != null && selected.length > 0) {
			for (String str : selected) {
				Country selectedCountry = countryService.getCountryById(str);
				if (selectedCountry != null) {
					// If its not already assigned, add the selected country to the assigned list
					if (!assignedCountires.contains(selectedCountry)) {
						assignedCountires.add(selectedCountry);
					}
				} else {
					State selectedState = stateService.getState(str);
					if (selectedState != null) {
						// If its not already assigned, add the selected state to the assigned list
						if (!assignedStates.contains(selectedState)) {
							assignedStates.add(selectedState);
						}
					}
				}
			}
		}

		// Convert assigned countries and states into Coverage
		for (Country country : assignedCountires) {
			Coverage coverageCountry = new Coverage(country);
			coverageCountry.setChecked(true);
			if (!list.contains(coverageCountry)) {
				list.add(coverageCountry);
			}
			// If this country has Master States, add it as coverage children
			if (CollectionUtil.isNotEmpty(country.getStates())) {
				List<Coverage> stateList = new ArrayList<Coverage>();
				for (State state : country.getStates()) {
					Coverage coverageState = new Coverage(state);
					// If this state is already assigned (Saved), check it
					if (assignedStates.contains(state)) {
						coverageState.setChecked(true);
					}
					if (!stateList.contains(coverageState)) {
						stateList.add(coverageState);
					}
				}
				coverageCountry.setChildren(stateList);
			}
		}

		/*
		 * Perform search
		 */
		if (StringUtils.checkString(search).length() > 0) {
			List<Country> searchCountries = countryService.searchCountiesByNameOrCode(search);
			List<State> searchStates = stateService.searchStatesByNameOrCode(search);

			// Construct for Matched Countries
			if (CollectionUtil.isNotEmpty(searchCountries)) {
				for (Country country : searchCountries) {
					constructCoverageForCountry(list, country, null);
				}
			}

			// Construct for Matched States
			if (CollectionUtil.isNotEmpty(searchStates)) {
				for (State state : searchStates) {
					Country country = state.getCountry();
					constructCoverageForCountry(list, country, state);
				}
			}

		} else {
			List<Country> countries = countryService.findAllActiveCountries();
			// Construct for Remaining Countries
			if (CollectionUtil.isNotEmpty(countries)) {
				for (Country country : countries) {
					Coverage coverageCountry = new Coverage(country);
					if (!list.contains(coverageCountry)) {
						list.add(coverageCountry);
					}
				}
			}
		}
		return list;
	}

	/**
	 * @param list
	 * @param country
	 * @param matchedState
	 */
	private void constructCoverageForCountry(List<Coverage> list, Country country, State matchedState) {
		Coverage coverageCountry = new Coverage(country);
		// If this country is already selected/saved by the user, then skip further processing as it is already
		// available in the coverage list along with all its states
		if (list.contains(coverageCountry)) {
			return;
		}

		// If user is in search mode and we found a state, then we just need to add that single state to the country (if
		// the country is not already in the Coverage)
		if (!list.contains(coverageCountry)) {
			list.add(0, coverageCountry);
		}
		if (matchedState != null) {
			List<Coverage> stateList = new ArrayList<Coverage>();
			Coverage coverageState = new Coverage(matchedState);
			stateList.add(coverageState);
			coverageCountry.setChildren(stateList);
		} else {
			// If this country has Master States, add it as coverage children
			if (CollectionUtil.isNotEmpty(country.getStates())) {
				List<Coverage> stateList = new ArrayList<Coverage>();
				for (State state : country.getStates()) {
					Coverage coverageState = new Coverage(state);
					if (!stateList.contains(coverageState)) {
						stateList.add(coverageState);
					}
				}
				coverageCountry.setChildren(stateList);
			}
		}
	}

	@Override
	public boolean isExistsRegistrationNumber(Supplier supplier) {
		return supplierDao.isExistsRegistrationNumber(supplier);
	}

	@Override
	public boolean isExistsCompanyName(Supplier supplier) {
		return supplierDao.isExistsCompanyName(supplier);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveNotes(Notes notes) {
		notesDao.saveOrUpdate(notes);

	}

	@Override
	public Supplier findSuppById(String supplierId) {
		Supplier supplier = supplierDao.findSuppById(supplierId);
		if (supplier != null) {
			if (supplier.getRegistrationOfCountry() != null) {
				supplier.getRegistrationOfCountry().getCountryName();
				supplier.getRegistrationOfCountry().getCountryCode();
			}
		}
		return supplier;
	}

	@Override
	public List<NotesPojo> getAllNotesPojo() {
		List<NotesPojo> returnList = new ArrayList<NotesPojo>();

		List<Notes> list = notesDao.findAll();

		if (CollectionUtil.isNotEmpty(list)) {
			for (Notes notes : list) {
				if (notes.getCreatedBy() != null)
					notes.getCreatedBy().getLoginId();
				NotesPojo np = new NotesPojo(notes);

				returnList.add(np);
			}
		}

		return returnList;
	}

	@Override
	public List<NotesPojo> notesForSupplier(String id, String loggedInTenantId) {
		List<NotesPojo> returnList = new ArrayList<NotesPojo>();

		List<Notes> list = notesDao.notesForSupplier(id, loggedInTenantId);

		if (CollectionUtil.isNotEmpty(list)) {
			for (Notes notes : list) {
				if (notes.getCreatedBy() != null)
					notes.getCreatedBy().getLoginId();
				NotesPojo np = new NotesPojo(notes);
				returnList.add(np);
			}
		}

		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTrackProject(String projectId) {
		supplierProjectsDao.deleteById(projectId);

	}

	public Supplier findSupplierOnDashbordById(String supplierId) {
		Supplier s = supplierDao.findById(supplierId);
		if (s.getSupplierPackage() != null) {
			s.getSupplierPackage().getId();
		}
		return s;
	}

	@Override
	public List<Supplier> findSuppliersOfNaicsCode(String ncid) {
		return supplierDao.findSuppliersOfNaicsCode(ncid);
	}

	@Override
	public long countTotalInvitedEventOfSupplier(String supplierId) {
		return rfxViewDao.totalEventInvitedSupplier(supplierId, null);
	}

	@Override
	public long countTotalParticipatedEventOfSupplier(String supplierId) {
		return rfxViewDao.totalEventParticipatedSupplier(supplierId, null);
	}

	// @Override
	// public long countTotalInvitedEventOfSupplier(String supplierId) {
	// return supplierDao.countTotalInvitedEventOfSupplier(supplierId);
	// }

	// @Override
	// public long countTotalParticipatedEventOfSupplier(String supplierId) {
	// return supplierDao.countTotalParticipatedEventOfSupplier(supplierId);
	// }

	@Override
	public long totalEventAwardedSupplier(String suppId) {
		return rfxViewDao.totalEventAwardedSupplier(suppId);
	}

	@Override
	public List<Supplier> getAllSupplierFromIds(List<String> supplierIds) {
		return supplierDao.getAllSupplierFromIds(supplierIds);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierOtherDocuments saveSupplierOtherDocuments(SupplierOtherDocuments otherDocuments) {
		return supplierOtherDocumentUploadDao.saveOrUpdate(otherDocuments);
	}

	@Override
	public List<SupplierOtherDocuments> findAllOtherDocumentBySupplierId(String supplierId) {
		return supplierOtherDocumentUploadDao.findAllOtherDocumentBySupplierId(supplierId);

	}

	@Override
	@Transactional(readOnly = false)
	public void removeOtherDocuments(String documentId) {
		supplierOtherDocumentUploadDao.deleteById(documentId);
	}

	@Override
	public SupplierOtherDocuments findOtherDocumentById(String documentId) {
		return supplierOtherDocumentUploadDao.findOtherDocumentById(documentId);
	}

	private void createSupplierDefaultRoles(String tenantId, User createdBy) {
		UserRole userRole = new UserRole();
		userRole.setRoleName("Supplier User".toUpperCase());
		userRole.setRoleDescription("Supplier User");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.SUPPLIER_DEFAULT_USER_ACL_VALUES));
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());

		userRoleService.saveRole(userRole, createdBy);
	}

	@Override
	public Supplier findSupplierAndAssocitedBuyersById(String suppId) {
		Supplier persistObj = supplierDao.findSupplierAndAssocitedBuyersById(suppId);
		if (persistObj.getNaicsCodes() != null) {
			LOG.info("persistObj : " + persistObj.getNaicsCodes().size());
		}
		return persistObj;
	}

	@Override
	public Supplier getSupplierWithAssoBuyersAndSubPackageById(String suppId) {
		Supplier persistObj = supplierDao.findSupplierAndAssocitedBuyersById(suppId);
		if (persistObj != null) {
			if (persistObj.getNaicsCodes() != null) {
				LOG.info("persistObj : " + persistObj.getNaicsCodes().size());
			}
			if (persistObj.getSupplierPackage() != null) {
				persistObj.getSupplierPackage().getActivatedDate();
			}
			if (persistObj.getSupplierSubscription() != null) {
				persistObj.getSupplierSubscription().getActivatedDate();
				persistObj.getSupplierSubscription().getPromoCode();
			}

			if (persistObj.getCreatedBy() != null) {
				persistObj.getCreatedBy().getName();
			}

			if (persistObj.getActionBy() != null) {
				persistObj.getActionBy().getName();
			}

		}
		return persistObj;
	}

	@Override
	public List<Supplier> findSuppliersForSubscriptionExpireOrExtend() {
		return supplierDao.findSuppliersForSubscriptionExpireOrExtend();
	}

	@Override
	public List<Supplier> findSuppliersForExpiryNotificationReminder(Date remindDate) {
		return supplierDao.findSuppliersForExpiryNotificationReminder(remindDate);
	}

	@Override
	public List<PoSupplierPojo> findAllSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status) {
		return supplierDao.findAllSearchFilterPoForSupplier(tenantId, input, startDate, endDate,status);
	}

	@Override
	public long findTotalSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status) {
		return supplierDao.findTotalSearchFilterPoForSupplier(tenantId, input, startDate, endDate,status);
	}

	@Override
	public long findTotalPoForSupplier(String tenantId) {
		return supplierDao.findTotalPoForSupplier(tenantId);
	}

	@Override
	public Pr getPrByIdForSupplierView(String prId) {
		Pr pr = prDao.findByPrId(prId);
		if (pr.getCorrespondenceAddress() != null) {
			pr.getCorrespondenceAddress().getState().getCountry().getCountryName();
		}

		if (pr.getTemplate() != null) {
			pr.getTemplate().getTemplateName();
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getFields())) {
				for (PrTemplateField field : pr.getTemplate().getFields()) {
					field.getFieldName();
				}
			}
		}
		if (pr.getBuyer() != null) {
			pr.getBuyer().getCompanyName();
			pr.getBuyer().getCompanyContactNumber();
			pr.getBuyer().getFaxNumber();
			pr.getBuyer().getLine1();
			pr.getBuyer().getLine2();
			pr.getBuyer().getCity();
			if (pr.getBuyer().getState() != null)
				pr.getBuyer().getState().getStateName();
			if (pr.getBuyer().getRegistrationOfCountry() != null)
				pr.getBuyer().getRegistrationOfCountry().getCountryName();
		}

		if (CollectionUtil.isNotEmpty(pr.getPrContacts())) {
			for (PrContact contact : pr.getPrContacts()) {
				contact.getContactName();
				contact.getContactNumber();
				contact.getComunicationEmail();
			}
		}
		if (pr.getBusinessUnit() != null) {
			pr.getBusinessUnit().getDisplayName();
		}
		return pr;
	}

	@Override
	public JasperPrint getSupplierPOSummaryPdf(Po po, HttpSession session) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (po.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (po.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (po.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (po.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		}

		try {

			Resource resource = applicationContext.getResource("classpath:reports/SupplierPoReport.jasper");

			File jasperfile = resource.getFile();

			PrSummaryPojo summary = new PrSummaryPojo();
			String createDate = po.getCreatedDate() != null ? sdf.format(po.getCreatedDate()).toUpperCase() : "";
			String deliveryDate = po.getDeliveryDate() != null ? sdf.format(po.getDeliveryDate()).toUpperCase() : "";
			String poRevisionDate = po.getPoRevisedDate() != null ? sdf.format(po.getPoRevisedDate()).toUpperCase() : "-";

			summary.setPrName(po.getName());
			summary.setRemarks(po.getRemarks());
			summary.setPaymentTerm(po.getPaymentTerm());
			summary.setTermsAndConditions(po.getTermsAndConditions() != null ? po.getTermsAndConditions().replaceAll("(?m)^[ \t]*\r?\n", "") : "");
			summary.setRequester(po.getRequester());
			summary.setPoNumber(po.getPoNumber());
			summary.setCreatedDate(createDate);
			summary.setPoRevisionDate(poRevisionDate);

			BusinessUnit bUnit = po.getBusinessUnit();
			Buyer buyer = po.getBuyer();
			// Buyer Address
			String buyerAddress = "";

			if (bUnit != null) {
				ImageIcon n;
				if (bUnit.getFileAttatchment() != null) {
					n = new ImageIcon(bUnit.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(bUnit.getDisplayName());
				}
				summary.setLogo(n.getImage());

				getSummaryOfAddressAndPritems(po, df, summary, deliveryDate);

				if (StringUtils.checkString(bUnit.getLine1()).length() > 0) {
					buyerAddress = bUnit.getLine1() + "\r\n";
				} else {
					buyerAddress = " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine2()).length() > 0) {
					buyerAddress += bUnit.getLine2() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine3()).length() > 0) {
					buyerAddress += bUnit.getLine3() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine4()).length() > 0) {
					buyerAddress += bUnit.getLine4() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine5()).length() > 0) {
					buyerAddress += bUnit.getLine5() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine6()).length() > 0) {
					buyerAddress += bUnit.getLine6() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine7()).length() > 0) {
					buyerAddress += bUnit.getLine7() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}

				summary.setDisplayName(bUnit.getDisplayName());
			} else {
				buyerAddress += "\r\n" + buyer.getCity();
				if (buyer.getState() != null) {
					buyerAddress += ", " + buyer.getState().getStateName();
					buyerAddress += "\r\n" + buyer.getState().getCountry().getCountryName();
				}
				buyerAddress += "\r\n";
				buyerAddress += "TEL : " + buyer.getCompanyContactNumber();
				buyerAddress += " FAX : ";
				if (buyer.getFaxNumber() != null) {
					buyerAddress += buyer.getFaxNumber();
				}
				summary.setComanyName(buyer.getCompanyName());
				summary.setDisplayName(buyer.getCompanyName());
			}
			LOG.info("buyerAddress : " + buyerAddress);
			summary.setBuyerAddress(buyerAddress);

			List<PrSummaryPojo> prSummary = Arrays.asList(summary);

			parameters.put("PR_SUMMARY", prSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(prSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report For Supplier. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	@Override
	public JasperPrint getSupplierPOSummaryPdfForDownload(Po po, HttpSession session) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (po.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (po.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (po.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (po.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		}

		try {

			Resource resource = applicationContext.getResource("classpath:reports/SupplierPoReport.jasper");

			File jasperfile = resource.getFile();

			PoSummaryPojo summary = new PoSummaryPojo();
			String createDate = po.getCreatedDate() != null ? sdf.format(po.getCreatedDate()).toUpperCase() : "";
			String deliveryDate = po.getDeliveryDate() != null ? sdf.format(po.getDeliveryDate()).toUpperCase() : "";

			summary.setPrName(po.getName());
			summary.setRemarks(po.getRemarks());
			summary.setPaymentTerm(po.getPaymentTerm());
			summary.setTermsAndConditions(po.getTermsAndConditions() != null ? po.getTermsAndConditions().replaceAll("(?m)^[ \t]*\r?\n", "") : "");
			summary.setRequester(po.getRequester());
			summary.setPoNumber(po.getPoNumber());
			summary.setCreatedDate(createDate);

			BusinessUnit bUnit = po.getBusinessUnit();
			Buyer buyer = po.getBuyer();
			// Buyer Address
			String buyerAddress = "";

			if (bUnit != null) {
				ImageIcon n;
				if (bUnit.getFileAttatchment() != null) {
					n = new ImageIcon(bUnit.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(bUnit.getDisplayName());
				}
				summary.setLogo(n.getImage());

				getSummaryOfAddressAndPoitems(po, df, summary, deliveryDate);

				if (StringUtils.checkString(bUnit.getLine1()).length() > 0) {
					buyerAddress = bUnit.getLine1() + "\r\n";
				} else {
					buyerAddress = " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine2()).length() > 0) {
					buyerAddress += bUnit.getLine2() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine3()).length() > 0) {
					buyerAddress += bUnit.getLine3() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine4()).length() > 0) {
					buyerAddress += bUnit.getLine4() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine5()).length() > 0) {
					buyerAddress += bUnit.getLine5() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine6()).length() > 0) {
					buyerAddress += bUnit.getLine6() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine7()).length() > 0) {
					buyerAddress += bUnit.getLine7() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}

				summary.setDisplayName(bUnit.getDisplayName());
			} else {
				buyerAddress += "\r\n" + buyer.getCity();
				if (buyer.getState() != null) {
					buyerAddress += ", " + buyer.getState().getStateName();
					buyerAddress += "\r\n" + buyer.getState().getCountry().getCountryName();
				}
				buyerAddress += "\r\n";
				buyerAddress += "TEL : " + buyer.getCompanyContactNumber();
				buyerAddress += " FAX : ";
				if (buyer.getFaxNumber() != null) {
					buyerAddress += buyer.getFaxNumber();
				}
				summary.setComanyName(buyer.getCompanyName());
				summary.setDisplayName(buyer.getCompanyName());
			}
			LOG.info("buyerAddress : " + buyerAddress);
			summary.setBuyerAddress(buyerAddress);

			// Supplier Address
			String supplierAddress = "";

			if (po.getSupplier() != null) {
				FavouriteSupplier supplier = po.getSupplier();
				supplierAddress += supplier.getSupplier().getCompanyName() + "\r\n";
				supplierAddress += supplier.getSupplier().getLine1();
				if (StringUtils.checkString(po.getSupplier().getSupplier().getLine2()).length() > 0) {
					supplierAddress += "\r\n" + supplier.getSupplier().getLine2();
				}
				supplierAddress += "\r\n" + supplier.getSupplier().getCity() + ", ";
				if (supplier.getSupplier().getState() != null) {
					supplierAddress += supplier.getSupplier().getState().getStateName() + "\r\n\n";
				}
				supplierAddress += "TEL : ";

				if (supplier.getSupplier().getCompanyContactNumber() != null) {
					supplierAddress += supplier.getSupplier().getCompanyContactNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (supplier.getSupplier().getFaxNumber() != null) {
					supplierAddress += supplier.getSupplier().getFaxNumber() + "\n\n";
				}
				supplierAddress += "Attention: " + supplier.getFullName() + "\nEmail: " + supplier.getCommunicationEmail() + "\n";
			} else {
				supplierAddress += po.getSupplierName() + "\r\n";
				supplierAddress += po.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (po.getSupplierTelNumber() != null) {
					supplierAddress += po.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (po.getSupplierFaxNumber() != null) {
					supplierAddress += po.getSupplierFaxNumber();
				}
			}
			if (po.getSupplier() != null) {
				summary.setSupplierName(po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(po.getSupplierName());
			}
			summary.setSupplierAddress(supplierAddress);
			summary.setTaxnumber(po.getSupplierTaxNumber() != null ? po.getSupplierTaxNumber() : "");

			List<PoSummaryPojo> poSummary = Arrays.asList(summary);

			parameters.put("PR_SUMMARY", poSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(poSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report For Supplier. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	/**
	 * @param po
	 * @param df
	 * @param summary
	 * @param deliveryDate
	 */
	private void getSummaryOfAddressAndPritems(Po po, DecimalFormat df, PrSummaryPojo summary, String deliveryDate) {
		try {
			// Delivery Address
			String deliveryAddress = "";
			deliveryAddress += po.getDeliveryAddress().getTitle() + "\n";
			deliveryAddress += po.getDeliveryAddress().getLine1();
			if (po.getDeliveryAddress().getLine2() != null) {
				deliveryAddress += "\n" + po.getDeliveryAddress().getLine2();
			}
			deliveryAddress += "\n" + po.getDeliveryAddress().getZip() + ", " + po.getDeliveryAddress().getCity() + "\n";
			deliveryAddress += po.getDeliveryAddress().getState().getStateName() + ", " + po.getDeliveryAddress().getState().getCountry().getCountryName();

			summary.setDeliveryAddress(deliveryAddress);
			summary.setDeliveryReceiver(po.getDeliveryReceiver());
			summary.setDeliveryDate(deliveryDate);

			// Correspondence Address
			if (po.getCorrespondenceAddress() != null) {

				String correspondAddress = "";
				correspondAddress += po.getCorrespondenceAddress().getTitle();
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getLine1();
				if (po.getCorrespondenceAddress().getLine2() != null) {
					correspondAddress += ", " + po.getCorrespondenceAddress().getLine2();
				}
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getZip() + ", " + po.getCorrespondenceAddress().getCity();
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getState().getStateName() + ", " + po.getCorrespondenceAddress().getState().getCountry().getCountryName();
				summary.setCorrespondAddress(correspondAddress);
			}
			// Pr items List
			List<PrItemsSummaryPojo> prItemList = new ArrayList<PrItemsSummaryPojo>();
			List<PoItem> poList = findAllPoItemByPoId(po.getId());
			if (CollectionUtil.isNotEmpty(poList)) {
				for (PoItem item : poList) {
					PrItemsSummaryPojo pos = new PrItemsSummaryPojo();
					pos.setSlno(item.getLevel() + "." + item.getOrder());
					pos.setItemName(item.getItemName());
					pos.setCurrency(item.getPo().getCurrency().getCurrencyCode());
					pos.setItemDescription(item.getItemDescription());
					pos.setAdditionalTax(df.format(item.getPo().getAdditionalTax()));
					pos.setGrandTotal(df.format(item.getPo().getGrandTotal()));
					pos.setSumAmount(df.format(po.getTotal()));
					pos.setTaxDescription(item.getPo().getTaxDescription());
					pos.setDecimal(po.getDecimal());
					prItemList.add(pos);
					if (item.getChildren() != null) {
						for (PoItem childItem : item.getChildren()) {
							PrItemsSummaryPojo childPo = new PrItemsSummaryPojo();
							childPo.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childPo.setItemName(childItem.getItemName());
							childPo.setItemDescription(childItem.getItemDescription());
							childPo.setQuantity(df.format(childItem.getQuantity()));
							childPo.setUnitPrice(df.format(childItem.getUnitPrice()));
							childPo.setTaxAmount(df.format(childItem.getTaxAmount()));
							childPo.setTotalAmount(df.format(childItem.getTotalAmount()));
							childPo.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childPo.setUom(childItem.getProduct() != null ? (childItem.getProduct().getUom() != null ? childItem.getProduct().getUom().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childPo.setCurrency(childItem.getPo().getCurrency().getCurrencyCode());
							childPo.setAdditionalTax(df.format(childItem.getPo().getAdditionalTax()));
							childPo.setGrandTotal(df.format(childItem.getPo().getGrandTotal()));
							childPo.setSumAmount(df.format(po.getTotal()));
							childPo.setTaxDescription(childItem.getPo().getTaxDescription());
							childPo.setSumTaxAmount(childItem.getTaxAmount());
							childPo.setSumTotalAmt(childItem.getTotalAmount());
							childPo.setDecimal(po.getDecimal());
							prItemList.add(childPo);
						}
					}

				}
			}

			summary.setPrItems(prItemList);
		} catch (Exception e) {
			LOG.error("Could not Get PR Items and Address For Supplier " + e.getMessage(), e);
		}
	}

	private void getSummaryOfAddressAndPoitems(Po po, DecimalFormat df, PoSummaryPojo summary, String deliveryDate) {
		try {
			// Delivery Address
			String deliveryAddress = "";
			deliveryAddress += po.getDeliveryAddress().getTitle() + "\n";
			deliveryAddress += po.getDeliveryAddress().getLine1();
			if (po.getDeliveryAddress().getLine2() != null) {
				deliveryAddress += "\n" + po.getDeliveryAddress().getLine2();
			}
			deliveryAddress += "\n" + po.getDeliveryAddress().getZip() + ", " + po.getDeliveryAddress().getCity() + "\n";
			deliveryAddress += po.getDeliveryAddress().getState().getStateName() + ", " + po.getDeliveryAddress().getState().getCountry().getCountryName();

			summary.setDeliveryAddress(deliveryAddress);
			summary.setDeliveryReceiver(po.getDeliveryReceiver());
			summary.setDeliveryDate(deliveryDate);

			// Correspondence Address
			if (po.getCorrespondenceAddress() != null) {

				String correspondAddress = "";
				correspondAddress += po.getCorrespondenceAddress().getTitle();
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getLine1();
				if (po.getCorrespondenceAddress().getLine2() != null) {
					correspondAddress += ", " + po.getCorrespondenceAddress().getLine2();
				}
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getZip() + ", " + po.getCorrespondenceAddress().getCity();
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getState().getStateName() + ", " + po.getCorrespondenceAddress().getState().getCountry().getCountryName();
				summary.setCorrespondAddress(correspondAddress);
			}
			// Pr items List
			List<PoItemsSummaryPojo> poItemList = new ArrayList<PoItemsSummaryPojo>();
			List<PoItem> poList = findAllPoItemByPoId(po.getId());
			if (CollectionUtil.isNotEmpty(poList)) {
				for (PoItem item : poList) {
					PoItemsSummaryPojo pos = new PoItemsSummaryPojo();
					pos.setSlno(item.getLevel() + "." + item.getOrder());
					pos.setItemName(item.getItemName());
					pos.setCurrency(item.getPo().getCurrency().getCurrencyCode());
					pos.setItemDescription(item.getItemDescription());
					pos.setAdditionalTax(df.format(item.getPo().getAdditionalTax()));
					pos.setGrandTotal(df.format(item.getPo().getGrandTotal()));
					pos.setSumAmount(df.format(po.getTotal()));
					pos.setTaxDescription(item.getPo().getTaxDescription());
					pos.setDecimal(po.getDecimal());
					poItemList.add(pos);
					if (item.getChildren() != null) {
						for (PoItem childItem : item.getChildren()) {
							PoItemsSummaryPojo childPo = new PoItemsSummaryPojo();
							childPo.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childPo.setItemName(childItem.getItemName());
							childPo.setItemDescription(childItem.getItemDescription());
							childPo.setQuantity(df.format(childItem.getQuantity()));
							childPo.setUnitPrice(df.format(childItem.getUnitPrice()));
							childPo.setTaxAmount(df.format(childItem.getTaxAmount()));
							childPo.setTotalAmount(df.format(childItem.getTotalAmount()));
							childPo.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childPo.setUom(childItem.getProduct() != null ? (childItem.getProduct().getUom() != null ? childItem.getProduct().getUom().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childPo.setCurrency(childItem.getPo().getCurrency().getCurrencyCode());
							childPo.setAdditionalTax(df.format(childItem.getPo().getAdditionalTax()));
							childPo.setGrandTotal(df.format(childItem.getPo().getGrandTotal()));
							childPo.setSumAmount(df.format(po.getTotal()));
							childPo.setTaxDescription(childItem.getPo().getTaxDescription());
							childPo.setSumTaxAmount(childItem.getTaxAmount());
							childPo.setSumTotalAmt(childItem.getTotalAmount());
							childPo.setDecimal(po.getDecimal());
							poItemList.add(childPo);
						}
					}

				}
			}
			summary.setPrItems(poItemList);
		} catch (Exception e) {
			LOG.error("Could not Get PR Items and Address For Supplier " + e.getMessage(), e);
		}
	}

	@Override
	public List<PrItem> findAllPrItemByPrId(String prId) {
		List<PrItem> returnList = new ArrayList<PrItem>();
		List<PrItem> list = prItemDao.getAllPrItemByPrId(prId);
		LOG.info("List :" + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (PrItem item : list) {
				PrItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PrItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<PrItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	public long findCountOfAllPOForSupplier(String tenantId, String userId) {
		return supplierDao.findCountOfAllPOForSupplier(tenantId, userId);
	}

	@Override
	public boolean isExistsRegistrationNumberWithId(Supplier supplier) {
		return supplierDao.isExistsRegistrationNumberWithId(supplier);
	}

	@Override
	public boolean isExistsCompanyNameWithId(Supplier supplier) {
		return supplierDao.isExistsCompanyNameWithId(supplier);
	}

	@Override
	@Transactional
	public void saveAuitTrail(String message, User loggedInUser) {
		try {
			LOG.info("****save audit trail");
			SupplierAuditTrail ownerAuditTrail = new SupplierAuditTrail(AuditTypes.UPDATE, message, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.Supplier);
			supplierAuditTrailDao.save(ownerAuditTrail);

		} catch (Exception e) {
			LOG.error("Error while saving audit trail " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSupplierCommunicationEmailForSupplierOnly(String supplierId, String oldCommunicationEmail, String newCommunicationEmail) {
		supplierDao.updateSupplierCommunicationEmailForSupplierOnly(supplierId, oldCommunicationEmail, newCommunicationEmail);

	}

	@Override
	public String generateAllPoZip(ZipOutputStream zos, HttpServletResponse response, String tenantId, HttpSession session) {
		String parentFolder = "parentDemo" + Global.PATH_SEPARATOR;
		String zipFileName = "demo" + Global.ZIP_FILE_EXTENTION;
		String poFolder = parentFolder + "demofile" + Global.PATH_SEPARATOR;
		List<Po> pList = poDao.findSupplierAllPo(tenantId);
		for (Po pr : pList) {
			try {
				// pr name may be duplicate so we use po number with name
				FileUtil.writePdfToZip(zos, getSupplierPOSummaryPdf(pr, session), poFolder, pr.getName() + "(" + pr.getPoNumber() + ")");

			} catch (IOException | JRException e) {
				e.printStackTrace();
			}
		}
		return zipFileName;
	}

	@Override
	public List<FinancePo> findFinanceSuppliers(String id) {

		return supplierDao.findFinanceSuppliers(id);
	}

	@Override
	public List<FinancePo> searchFinanceSuppliers(String status, String order, String globalSreach, String id) {

		return supplierDao.serchFinanceSuppliers(status, order, globalSreach, id);
	}

	@Override
	public List<Pr> findAllSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		return supplierDao.findAllSearchFilterPoForFinance(tenantId, input, startDate, endDate, status, selectedSupplier);
	}

	@Override
	public long findTotalSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		return supplierDao.findTotalSearchFilterPoForFinance(tenantId, input, startDate, endDate, status, selectedSupplier);
	}

	@Override
	public long findTotalPoForFinance(String tenantId) {
		return supplierDao.findTotalPoForFinance(tenantId);
	}

	@Override
	public Pr getPrByIdForFinanceView(String prId) {
		Pr pr = prDao.findByPrIdForFinance(prId);
		if (pr.getCorrespondenceAddress() != null) {
			pr.getCorrespondenceAddress().getState().getCountry().getCountryName();
		}

		if (pr.getTemplate() != null) {
			pr.getTemplate().getTemplateName();
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getFields())) {
				for (PrTemplateField field : pr.getTemplate().getFields()) {
					field.getFieldName();
				}
			}
		}
		if (pr.getBuyer() != null) {
			pr.getBuyer().getCompanyName();
			pr.getBuyer().getCompanyContactNumber();
			pr.getBuyer().getFaxNumber();
			pr.getBuyer().getLine1();
			pr.getBuyer().getLine2();
			pr.getBuyer().getCity();
			if (pr.getBuyer().getState() != null)
				pr.getBuyer().getState().getStateName();
			if (pr.getBuyer().getRegistrationOfCountry() != null)
				pr.getBuyer().getRegistrationOfCountry().getCountryName();
		}
		if (CollectionUtil.isNotEmpty(pr.getPrContacts())) {
			for (PrContact contact : pr.getPrContacts()) {
				contact.getContactName();
				contact.getContactNumber();
				contact.getComunicationEmail();
			}
		}
		if (pr.getBusinessUnit() != null) {
			pr.getBusinessUnit().getDisplayName();
		}
		return pr;
	}

	@Override
	public JasperPrint getFinancePOSummaryPdf(Po po, HttpSession session) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (po.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (po.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (po.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (po.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		}

		try {

			Resource resource = applicationContext.getResource("classpath:reports/FinancePoReport.jasper");

			File jasperfile = resource.getFile();

			PrSummaryPojo summary = new PrSummaryPojo();
			String createDate = po.getCreatedDate() != null ? sdf.format(po.getCreatedDate()).toUpperCase() : "";
			String deliveryDate = po.getDeliveryDate() != null ? sdf.format(po.getDeliveryDate()).toUpperCase() : "";

			summary.setPrName(po.getName());
			summary.setRemarks(po.getRemarks());
			summary.setPaymentTerm(po.getPaymentTerm());
			summary.setTermsAndConditions(po.getTermsAndConditions() != null ? po.getTermsAndConditions().replaceAll("(?m)^[ \t]*\r?\n", "") : "");
			summary.setRequester(po.getRequester());
			summary.setPoNumber(po.getPoNumber());
			summary.setCreatedDate(createDate);

			BusinessUnit bUnit = po.getBusinessUnit();
			Buyer buyer = po.getBuyer();
			// Buyer Address
			String buyerAddress = "";

			if (bUnit != null) {
				ImageIcon n;
				if (bUnit.getFileAttatchment() != null) {
					n = new ImageIcon(bUnit.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(bUnit.getDisplayName());
				}
				summary.setLogo(n.getImage());

				getSummaryOfAddressAndPritems(po, df, summary, deliveryDate);

				if (StringUtils.checkString(bUnit.getLine1()).length() > 0) {
					buyerAddress = bUnit.getLine1() + "\r\n";
				} else {
					buyerAddress = " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine2()).length() > 0) {
					buyerAddress += bUnit.getLine2() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine3()).length() > 0) {
					buyerAddress += bUnit.getLine3() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine4()).length() > 0) {
					buyerAddress += bUnit.getLine4() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine5()).length() > 0) {
					buyerAddress += bUnit.getLine5() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine6()).length() > 0) {
					buyerAddress += bUnit.getLine6() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine7()).length() > 0) {
					buyerAddress += bUnit.getLine7() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}

				summary.setDisplayName(bUnit.getDisplayName());
			} else {
				buyerAddress += "\r\n" + buyer.getCity();
				if (buyer.getState() != null) {
					buyerAddress += ", " + buyer.getState().getStateName();
					buyerAddress += "\r\n" + buyer.getState().getCountry().getCountryName();
				}
				buyerAddress += "\r\n";
				buyerAddress += "TEL : " + buyer.getCompanyContactNumber();
				buyerAddress += " FAX : ";
				if (buyer.getFaxNumber() != null) {
					buyerAddress += buyer.getFaxNumber();
				}
				summary.setComanyName(buyer.getCompanyName());
				summary.setDisplayName(buyer.getCompanyName());
			}
			LOG.info("buyerAddress : " + buyerAddress);
			summary.setBuyerAddress(buyerAddress);

			List<PrSummaryPojo> prSummary = Arrays.asList(summary);

			parameters.put("PR_SUMMARY", prSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(prSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report For Finance. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	@Override
	public long findCountOfAllPOForFinance(String tenantId, String userId) {
		return supplierDao.findCountOfAllPOForFinance(tenantId, userId);
	}

	@Override
	public List<Pr> findAllSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		return supplierDao.findAllSearchFilterPoForOwner(input, startDate, endDate, status, selectedSupplier);
	}

	@Override
	public long findTotalSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		return supplierDao.findTotalSearchFilterPoForOwner(input, startDate, endDate, status, selectedSupplier);
	}

	@Override
	public long findTotalPoForOwner() {
		return supplierDao.findTotalPoForOwner();
	}

	@Override
	public List<Coverage> doSearchCoverageForSupplierRegistration(String activeTab, String supplierId, String projectId, String[] selected, String search) {
		LOG.info("TAB : " + activeTab + "  Supplier Id : " + supplierId + " Project Id : " + projectId);

		List<Coverage> list = new ArrayList<Coverage>();
		List<Country> assignedCountires = null;
		List<State> assignedStates = null;

		/*
		 * Fetch assigned (saved) list of countries and states...
		 */
		if (Global.SUPPLIER_COVERAGE.equals(StringUtils.checkString(activeTab)) && StringUtils.checkString(supplierId).length() > 0) {
			LOG.info("Supplier assigned coverage details");
			assignedCountires = assignedCountriesForSupplierId(supplierId);
			assignedStates = assignedStatesForSupplierId(supplierId);
		} else {
			LOG.info("Project assigned coverage details");
			if (StringUtils.checkString(projectId).length() > 0) {
				SupplierProjects project = supplierProjectsDao.findById(projectId);
				if (project != null) {
					assignedCountires = project.getAssignedCountries();
					assignedStates = project.getAssignedStates();
				}
			}
		}

		if (assignedCountires == null) {
			assignedCountires = new ArrayList<Country>();
		}
		if (assignedStates == null) {
			assignedStates = new ArrayList<State>();
		}

		/*
		 * If the user has selected some Objects, then merge them into the assigned list for re-display
		 */
		if (selected != null && selected.length > 0) {
			for (String str : selected) {
				Country selectedCountry = countryService.getCountryById(str);
				if (selectedCountry != null) {
					// If its not already assigned, add the selected country to the assigned list
					if (!assignedCountires.contains(selectedCountry)) {
						assignedCountires.add(selectedCountry);
					}
				} else {
					State selectedState = stateService.getState(str);
					if (selectedState != null) {
						// If its not already assigned, add the selected state to the assigned list
						if (!assignedStates.contains(selectedState)) {
							assignedStates.add(selectedState);
						}
					}
				}
			}
		}

		// Convert assigned countries and states into Coverage
		for (Country country : assignedCountires) {
			Coverage coverageCountry = new Coverage(country);
			coverageCountry.setChecked(true);
			if (!list.contains(coverageCountry)) {
				list.add(coverageCountry);
			}
			// If this country has Master States, add it as coverage children
			if (CollectionUtil.isNotEmpty(country.getStates())) {
				List<Coverage> stateList = new ArrayList<Coverage>();
				for (State state : country.getStates()) {
					Coverage coverageState = new Coverage(state);
					// If this state is already assigned (Saved), check it
					if (assignedStates.contains(state)) {
						coverageState.setChecked(true);
					}
					if (!stateList.contains(coverageState)) {
						stateList.add(coverageState);
					}
				}
				coverageCountry.setChildren(stateList);
			}
		}

		/*
		 * Perform search
		 */
		if (StringUtils.checkString(search).length() > 0) {
			List<Country> searchCountries = countryService.searchCountiesByNameOrCode(search);
			List<State> searchStates = stateService.searchStatesByNameOrCode(search);

			// Construct for Matched Countries
			if (CollectionUtil.isNotEmpty(searchCountries)) {
				for (Country country : searchCountries) {
					constructCoverageForCountry(list, country, null);
				}
			}

			// Construct for Matched States
			if (CollectionUtil.isNotEmpty(searchStates)) {
				for (State state : searchStates) {
					Country country = state.getCountry();
					constructCoverageForCountry(list, country, state);
				}
			}

		} else {
			List<Country> countries = countryService.findAllActiveCountries();
			// Construct for Remaining Countries
			if (CollectionUtil.isNotEmpty(countries)) {
				for (Country country : countries) {
					Coverage coverageCountry = new Coverage(country);
					if (!list.contains(coverageCountry)) {
						list.add(coverageCountry);
					}
				}
			}
		}

		removeCheckedAlredy(list, selected);
		return list;
	}

	private void removeCheckedAlredy(List<Coverage> list, String[] selectedCategories) {
		if ((selectedCategories != null && selectedCategories.length > 0) && CollectionUtil.isNotEmpty(list)) {
			for (Coverage coverage : list) {
				if (Arrays.stream(selectedCategories).anyMatch(coverage.getId()::equals)) {
					coverage.setChecked(true);
				} else {
					coverage.setChecked(false);
				}
				if (CollectionUtil.isNotEmpty(coverage.getChildren())) {
					for (Coverage child : coverage.getChildren()) {
						if (Arrays.stream(selectedCategories).anyMatch(child.getId()::equals)) {
							child.setChecked(true);
						} else {
							child.setChecked(false);
						}
					}
				}
			}
		}
	}

	@Override
	public List<SupplierPojo> searchSuppliersForPagination(String status, String order, String globalSearch, String pageNo) {
		return supplierDao.searchSupplierForPagination(status, order, globalSearch, pageNo);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SupplierFinanicalDocuments saveSupplierFinancialDocuments(SupplierFinanicalDocuments supplierFinancialDocuments) {
		return financialDocuments.saveOrUpdate(supplierFinancialDocuments);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSupplierFinancialDocuments(String id) {
		financialDocuments.deleteById(id);
	}

	@Override
	public SupplierFinanicalDocuments findFinancialDocumentId(String id) {
		return supplierFinancialDocDao.findFinancialDocumentById(id);
	}

	@Override
	public List<SupplierFinanicalDocuments> findAllFinancialDocumentsBySupplierID(String id) {
		return financialDocuments.findAllDocumentsBySupplierId(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SupplierBoardOfDirectors saveSupplierBoardOfDirector(SupplierBoardOfDirectors directors) {
		return supplierBoardOfDirectorsDao.saveOrUpdate(directors);
	}

	@Override
	public List<SupplierBoardOfDirectors> findAllDirectorsBySupplierID(String id) {
		return supplierBoardOfDirectorsDao.findAllBySupplierId(id);
	}

	@Override
	public void removeBoardOfDirector(String id) {
		supplierBoardOfDirectorsDao.deleteById(id);

	}

	@Override
	public SupplierBoardOfDirectors findDirectorById(String id) {
		return supplierBoardOfDirectorsDao.findById(id);
	}

	@Override
	public List<SupplierBoardOfDirectors> findDuplicateDirector(String idNumber) {
		List<SupplierBoardOfDirectors> list = supplierBoardOfDirectorsDao.findIfRecordExistsWithDuplicateIdnumber(idNumber);
		if (CollectionUtil.isNotEmpty(list)) {
			for (SupplierBoardOfDirectors dirs : list) {
				dirs.getDirContact();
				dirs.getDirEmail();
				dirs.getDirType();
				dirs.getDirectorName();
				dirs.getId();
				dirs.getIdNumber();
				dirs.getIdType();
				dirs.getSupplier();
				dirs.getSupplier().getId();
			}
		}
		return list;
	}

	@Override
	public void checkIfProfileIsComplete(Model model, Supplier supplier) {
		if (supplier.getTaxRegistrationNumber() == null) {
			model.addAttribute("incompleteTaxReg", true);
		}

		// PH-1098 check if the supplier has selected more than 25 sub categories.
		List<NaicsCodes> codes = industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null);
		int selectedCategories = 0;
		if (CollectionUtil.isNotEmpty(codes)) {
			for (NaicsCodes level1 : codes) {
				if (CollectionUtil.isNotEmpty(level1.getChildren())) {
					for (NaicsCodes level2 : level1.getChildren()) {
						if (level2.isChecked() == true) {
							if (CollectionUtil.isNotEmpty(level2.getChildren())) {
								for (NaicsCodes level3 : level2.getChildren()) {
									if (level3.isChecked() == true) {
										if (CollectionUtil.isNotEmpty(level3.getChildren())) {
											for (NaicsCodes level4 : level3.getChildren()) {
												if (level4.isChecked() == true) {
													if (CollectionUtil.isNotEmpty(level4.getChildren())) {
														for (NaicsCodes level5 : level4.getChildren()) {
															if (level5.isChecked() == true) {
																selectedCategories++;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (selectedCategories > 25) {
			model.addAttribute("incompleteIndustryCategorySection", true);
		}

		if (supplier.getSupplierFinancialDocuments().size() == 0 || supplier.getPaidUpCapital() == null || supplier.getCurrency() == null) {
			model.addAttribute("incompleteFinanceSection", true);

		}
		if (supplier.getSupplierBoardOfDirectors().size() == 0) {
			model.addAttribute("incompleteOrgSection", true);
		}
	}

	@Override
	public long findTotalAssocitedBuyersById(String teanantId) {
		return supplierDao.findTotalAssocitedBuyersById(teanantId);
	}

	@Override
	public long getTotalSupplierCount() {
		return supplierDao.getTotalSupplierCount();
	}

	@Override
	public RequestedAssociatedBuyer findSupplierRequestBySupplierAndBuyerId(String id, String buyerId) {
		RequestedAssociatedBuyer buyer = supplierDao.findSupplierRequestByIds(id, buyerId);
		if (buyer != null) {
			if (CollectionUtil.isNotEmpty(buyer.getIndustryCategory()))
				for (IndustryCategory category : buyer.getIndustryCategory()) {
					category.getName();
					category.getCode();
					category.getId();
				}
		}
		return buyer;
	}

	@Override
	public RequestedAssociatedBuyer findAssocoaitedRequestById(String id) {
		RequestedAssociatedBuyer associatedBuyer = associatedBuyerDao.findById(id);
		if (associatedBuyer != null) {
			if (CollectionUtil.isNotEmpty(associatedBuyer.getIndustryCategory()))
				for (IndustryCategory category : associatedBuyer.getIndustryCategory()) {
					category.getName();
					category.getCode();
				}

			if (CollectionUtil.isNotEmpty(associatedBuyer.getSupplier().getCountries()))
				for (Country country : associatedBuyer.getSupplier().getCountries()) {
					country.getCountryName();
					country.getCountryCode();
				}

			if (associatedBuyer.getSupplier() != null) {
				associatedBuyer.getSupplier().getId();
				associatedBuyer.getSupplier().getFullName();
				associatedBuyer.getSupplier().getCommunicationEmail();
				associatedBuyer.getSupplier().getRegistrationOfCountry().getCountryName();
			}

			if (associatedBuyer.getBuyer() != null) {
				associatedBuyer.getBuyer().getId();
				associatedBuyer.getBuyer().getFullName();
				associatedBuyer.getBuyer().getCommunicationEmail();
				associatedBuyer.getBuyer().getRegistrationOfCountry().getCountryName();
			}

		}
		return associatedBuyer;
	}

	@Transactional(readOnly = false)
	@Override
	public RequestedAssociatedBuyer updateSupplierRequest(RequestedAssociatedBuyer updatedRequest) {
		return associatedBuyerDao.saveOrUpdate(updatedRequest);
	}

	@Override
	public long findCountOfPoForSupplierBasedOnStatus(String tenantId, PoStatus status) {
		return poDao.findCountOfPoForSupplierBasedOnStatus(tenantId, status);
	}

	@Override
	public List<PoSupplierPojo> findAllSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status) {
		return poDao.findAllSearchFilterPoForSupplierByStatus(tenantId, input, status);
	}

	@Override
	public long findTotalSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status) {
		return poDao.findTotalSearchFilterPoForSupplierByStatus(tenantId, input, status);
	}

	@Override
	public Po getPoByIdForSupplierView(String poId) {
		Po po = poDao.findByPoId(poId);
		if (po.getCorrespondenceAddress() != null) {
			po.getCorrespondenceAddress().getState().getCountry().getCountryName();
		}

		if (po.getBuyer() != null) {
			po.getBuyer().getCompanyName();
			po.getBuyer().getCompanyContactNumber();
			po.getBuyer().getFaxNumber();
			po.getBuyer().getLine1();
			po.getBuyer().getLine2();
			po.getBuyer().getCity();
			if (po.getBuyer().getState() != null)
				po.getBuyer().getState().getStateName();
			if (po.getBuyer().getRegistrationOfCountry() != null)
				po.getBuyer().getRegistrationOfCountry().getCountryName();
		}
		if (po.getBusinessUnit() != null) {
			po.getBusinessUnit().getDisplayName();
		}
		if (po.getPr() != null) {
			po.getPr().getName();
		}
		if (po.getSupplier() != null) {
			FavouriteSupplier supplier = po.getSupplier();
			supplier.getFullName();
			supplier.getCommunicationEmail();
			if (supplier.getSupplier() != null) {
				supplier.getSupplier().getCompanyName();
				supplier.getSupplier().getLine1();
				supplier.getSupplier().getLine2();
				supplier.getSupplier().getCity();
				supplier.getSupplier().getState();
				supplier.getSupplier().getCompanyContactNumber();
				supplier.getSupplier().getState().getStateName();
				supplier.getSupplier().getCompanyContactNumber();
				supplier.getSupplier().getFaxNumber();
			}
		}
		return po;
	}

	@Override
	public Supplier findPlainSupplierById(String supplierId) {
		return supplierDao.findPlainSupplierById(supplierId);
	}

	@Override
	public List<PoItem> findAllPoItemByPoId(String poId) {
		List<PoItem> returnList = new ArrayList<PoItem>();
		List<PoItem> list = poItemDao.getAllPoItemByPoId(poId);
		LOG.info("List :" + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (PoItem item : list) {
				PoItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PoItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<PoItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void extendValidity(Supplier supplier, ExtentionValidity extentionValidity) throws ApplicationException {
		try {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			SupplierSubscription currentSubscription = supplier.getSupplierSubscription();

			if (currentSubscription == null) {
				LOG.error("Error in  updating validity supplier has no subscription.");
				throw new ApplicationException("No subscription found.");
			}

			while (currentSubscription.getNextSubscription() != null) {
				currentSubscription = currentSubscription.getNextSubscription();
			}
			LOG.info("Finished computing last subscription.....");
			if (currentSubscription != null) {
				String auditMessage = "Subscription validity extended for supplier " + supplier.getCompanyName();
				if (currentSubscription.getEndDate() != null) {
					auditMessage += " from " + sd.format(currentSubscription.getEndDate());
				}
				auditMessage += " to ";
				currentSubscription.setEndDate(setNightDate(sd.parse(extentionValidity.getExtensionDate())));
				auditMessage += sd.format(currentSubscription.getEndDate());
				LOG.info("Audit message is: " + auditMessage);
				supplierSubscriptionDao.saveOrUpdate(currentSubscription);
				SupplierPackage currentPackage = supplier.getSupplierPackage();
				currentPackage.setEndDate(currentSubscription.getEndDate());

				if (SubscriptionStatus.EXPIRED.equals(currentSubscription.getSubscriptionStatus()) && currentSubscription.getEndDate().after(new Date())) {
					currentPackage.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
					currentSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
				}

				supplier.setSupplierPackage(currentPackage);
				updateSupplier(supplier);
				saveAuitTrail(auditMessage, SecurityLibrary.getLoggedInUser());
				Notes note = new Notes();
				note.setSupplier(supplier);
				note.setDescription(auditMessage);
				note.setIncidentType("VALIDITY EXTENDED");
				note.setCreatedBy(SecurityLibrary.getLoggedInUser());
				note.setCreatedDate(new Date());
				saveNotes(note);
			}

		} catch (Exception e) {
			LOG.error("Error in  updating validity: " + e.getMessage(), e);
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	public Date setNightDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.AM_PM, Calendar.PM);
		cal.set(Calendar.HOUR, 11);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 59);
		return cal.getTime();
	}

	@Override
	public List<Supplier> findSuppliersForFutureSubscriptionActivation() {
		return supplierDao.findSuppliersForFutureSubscriptionActivation();
	}

	@Override
	public List<SupplierSubscription> getSupplierFutureSubscriptionByCreatedDate(String id) {
		return supplierSubscriptionDao.getSupplierFutureSubscriptionByCreatedDate(id);
	}

	@Override
	public List<SupplierReportPojo> findAllSearchFilterSupplierReportList(TableDataInput input, Date startDate, Date endDate) {
		return supplierDao.findAllSearchFilterSupplierReportList(input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterSupplierReportCount(TableDataInput input, Date startDate, Date endDate) {
		return supplierDao.findTotalSearchFilterSupplierReportCount(input, startDate, endDate);
	}

	@Override
	public long findTotalSuppliersCount(Date startDate, Date endDate) {
		return supplierDao.findTotalSuppliersCount(startDate, endDate);
	}

	@Override
	public void downloadCsvFileForSupplierList(HttpServletResponse response, File file, SupplierSearchFilterPojo supplierSearchFilterPojo, boolean select_all, String[] supplierArr, DateFormat formatter, Date startDate, Date endDate) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.ALL_SUPPLIER_REPORT_EXCEL_COLUMNS;

			final String[] columns = new String[] { "companyName", "companyRegistrationNumber", "companyType", "yearOfEstablished", "taxRegistrationNumber", "companyAddress", "country", "state", "companyContactNumber", "faxNumber", "companyWebsite", "loginEmail", "communicationEmail", "registrationDate", "approveDate", "regCompleteDate", "fullName", "designation", "mobileNumber", "status", "subscriptionStatus", "currentSubPlan", "promoCode", "subscribeEndDate", "associatedBuyers", "industrySector", "geographicalCoverage", "companyDocument", "financialInformation", "organazationalDetails", "trackRecordUpdated", "otherDocument", "totalEventsInvited", "totalEventParticipated", "totalEventAwarded" };

			long count = supplierDao.findTotalSuppliersCount(startDate, endDate);

			int PAGE_SIZE = 10000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			LOG.info("Started writing");
			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<SupplierReportPojo> list = supplierDao.getAlSupplierListForCsvReport(supplierArr, supplierSearchFilterPojo, select_all, PAGE_SIZE, pageNo, startDate, endDate);
				for (SupplierReportPojo pojo : list) {

					if (pojo.getCompanyRegDate() != null) {
						pojo.setRegistrationDate(formatter.format(pojo.getCompanyRegDate()));
					}
					if (pojo.getRegistrationCompleteDate() != null) {
						pojo.setRegCompleteDate(formatter.format(pojo.getRegistrationCompleteDate()));
					}
					if (pojo.getSubscriptionEndDate() != null) {
						pojo.setSubscribeEndDate(df.format(pojo.getSubscriptionEndDate()));
					}
					if (pojo.getApprovedDate() != null) {
						pojo.setApproveDate(formatter.format(pojo.getApprovedDate()));
					}
					try {
						String assBuyes = supplierDao.findAssociateBuyersForSupplierId(pojo.getId());
						if (StringUtils.checkString(assBuyes).length() > 0) {
							pojo.setAssociatedBuyers(assBuyes);
						}
					} catch (SQLException e1) {
					}
					try {
						String coverage = supplierDao.findGeoCoverageForSupplierId(pojo.getId());
						if (StringUtils.checkString(coverage).length() > 0) {
							pojo.setGeographicalCoverage(coverage);
						}
					} catch (SQLException e1) {
					}

					try {
						String inSec = supplierDao.findNaicCodesForSupplierId(pojo.getId());
						if (StringUtils.checkString(inSec).length() > 0) {
							pojo.setIndustrySector(StringUtils.checkString(inSec));
						}
					} catch (SQLException e) {
					}

					SupplierDetailsCountPojo counts = supplierDao.findSupplierDetailsCounts(pojo.getId());
					if (counts != null) {

						pojo.setCompanyDocument(counts.getCompanyProfile() > 0 ? "Yes" : "No");
						pojo.setFinancialInformation(counts.getFincDocs() > 0 ? "Yes" : "No");
						pojo.setTrackRecordUpdated(counts.getTrackRecord());
						pojo.setOtherDocument(counts.getOtherDocs() > 0 ? "Yes" : "No");
						pojo.setOrganazationalDetails(counts.getBordDir() > 0 ? "Yes" : "No");
					}

					SupplierPojo details = supplierDao.findInvitedAndSubmitedCountsForSupplier(pojo.getId());
					if (details != null) {
						pojo.setTotalEventsInvited(details.getInvited());
						pojo.setTotalEventParticipated(details.getSubmited());
					}

					pojo.setTotalEventAwarded(supplierDao.findAwardCountForSupplier(pojo.getId()));
					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}
			LOG.info("end writing");

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.error("Error ..." + e.getMessage(), e);
		}
	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), // Company Name
				new Optional(), // Registration Number
				new Optional(), // Company Type
				new Optional(), // yearOfEstablished
				new Optional(), // taxRegistrationNumber
				new Optional(), // companyAddress
				new Optional(), // country
				new Optional(), // state
				new Optional(), // companyContactNumber
				new Optional(), // faxNumber
				new Optional(), // companyWebsite
				new Optional(), // loginEmail
				new Optional(), // communicationEmail
				new Optional(), // registrationDate
				new Optional(), // approvedDate
				new Optional(), // registrationCompleteDate
				new Optional(), // fullname
				new Optional(), // designation
				new Optional(), // mobileNumber
				new Optional(), // supplierStatus
				new Optional(), // subscriptionStatus
				new Optional(), // currentSubPlan
				new Optional(), // promocode
				new Optional(), // subvalidity
				new Optional(), // associatedBuyers
				new Optional(), // Industry Sector
				new Optional(), // geographicalcoverage
				new Optional(), // companyDocument
				new Optional(), // financialInfo
				new Optional(), // Organizational
				new Optional(), // Track Record
				new Optional(), // Other Documents (Yes (If any) / No)
				new Optional(), // totalEventsInvited
				new Optional(), // totalEventParticipated
				new Optional(), // totalEventAwarded
		};
		return processors;
	}

	@Override
	@Transactional(readOnly = false)
	public Supplier updateSupplierCompanyName(String companyName, String supplierId, String tenantId, HttpHeaders headers) throws NoSuchMessageException, ApplicationException {

		String msg = "";
		Supplier supplier = supplierDao.findById(supplierId);

		if (!isExistsByCompanyNameOrRegNo(supplier, companyName, null)) {
			msg = "Supplier Company Name changed from '" + supplier.getCompanyName() + "' to '" + companyName + "'";
			String oldCompName = supplier.getCompanyName();

			if (!StringUtils.checkString(oldCompName).equals(companyName)) {
				supplier.setFormerCompanyName(supplier.getCompanyName());
				supplier.setCompanyName(companyName);
				supplierDao.saveOrUpdate(supplier);
				LOG.info("Company Name updated successfully : ");

				try {
					OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, msg, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Supplier);
					ownerAuditTrailDao.save(ownerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while audit trail :" + e.getMessage(), e);
				}

				// Notification to supplier Admins
				sendEmailNotificationToSupplier(supplier, oldCompName);

				// Proc Admin
				sendCompanyNameChangedEmailNotificationToProcurehereAdmin(supplier, tenantId, oldCompName);

				// Buyer Admins
				sendCompanyNameChangedEmailNotificationToBuyerAdmin(supplier, tenantId, oldCompName);
			}
			return supplier;
		} else {

			LOG.info("Duplicate Supplier : ");
			throw new ApplicationException(messageSource.getMessage("supplier.error.duplicate", new Object[] { companyName, supplier.getCompanyRegistrationNumber(), supplier.getRegistrationOfCountry() }, Global.LOCALE));
			// return null;
		}

	}

	private boolean isExistsByCompanyNameOrRegNo(Supplier supplier, String companyName, String regNumber) {
		return supplierDao.isExistsByCompanyNameOrRegNo(supplier, companyName, regNumber);
	}

	private void sendCompanyNameChangedEmailNotificationToBuyerAdmin(Supplier supplier, String tenantId, String oldCompanyName) {
		try {
			LOG.info("Sending Company Name Changed email to  supplier admin ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			String subject = "Supplier company name changed from " + oldCompanyName + " to " + supplier.getCompanyName();
			String message = "The following supplier has changed their company details as below ";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("newComapnyName", supplier.getCompanyName());
			map.put("oldComapnyName", oldCompanyName);
			map.put("message", message);
			map.put("loginUrl", APP_URL + "/login");

			List<Buyer> associatedBuyerList = favoriteSupplierDao.findFavSupplierBySuppId(supplier.getId());
			if (CollectionUtil.isNotEmpty(associatedBuyerList)) {
				for (Buyer buyer : associatedBuyerList) {
					List<User> buyerAdminList = userDao.getAllAdminUsersForBuyer(buyer.getId());
					if (CollectionUtil.isNotEmpty(buyerAdminList)) {
						for (User buyerAdmin : buyerAdminList) {
							LOG.info("Sending Company Name Changed email to  " + buyerAdmin.getCommunicationEmail() + " .. " + buyerAdmin.getName());
							timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
							df.setTimeZone(TimeZone.getTimeZone(timeZone));
							map.put("date", df.format(new Date()));
							map.put("supplierName", buyerAdmin.getName());
							if (StringUtils.checkString(buyerAdmin.getCommunicationEmail()).length() > 0 && buyerAdmin.getEmailNotifications()) {
								sendEmail(buyerAdmin.getCommunicationEmail(), subject, map, Global.COMPANY_NAME_CHANGED_TEMPLATE);
							} else {
								LOG.warn("No communication email configured for user : " + buyerAdmin.getName() + "... Not going to send email notification");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.info("Error while sending mail Buyer Admin " + e.getMessage(), e);
		}
	}

	private void sendCompanyNameChangedEmailNotificationToProcurehereAdmin(Supplier supplier, String tenantId, String oldCompanyName) {
		try {
			LOG.info("Sending Company Name Changed email to  supplier admin ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			String subject = "Supplier company name changed from " + oldCompanyName + " to " + supplier.getCompanyName();
			String message = "Supplier company details have been changed as below ";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("newComapnyName", supplier.getCompanyName());
			map.put("oldComapnyName", oldCompanyName);
			map.put("message", message);
			map.put("loginUrl", APP_URL + "/login");

			List<User> adminList = userDao.getAllAdminPlainUsersForSupplier(tenantId);
			if (CollectionUtil.isNotEmpty(adminList)) {
				for (User procAdmin : adminList) {
					map.put("supplierName", procAdmin.getName());
					timeZone = getTimeZoneByBuyerSettings(procAdmin.getId(), timeZone);
					df.setTimeZone(TimeZone.getTimeZone(timeZone));
					map.put("date", df.format(new Date()));
					LOG.info("Sending Company Name Changed email to  " + procAdmin.getCommunicationEmail() + " .. " + procAdmin.getName());
					if (StringUtils.checkString(procAdmin.getCommunicationEmail()).length() > 0 && procAdmin.getEmailNotifications()) {
						sendEmail(procAdmin.getCommunicationEmail(), subject, map, Global.COMPANY_NAME_CHANGED_TEMPLATE);
					} else {
						LOG.warn("No communication email configured for user : " + procAdmin.getName() + "... Not going to send email notification");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.info("Error while sending mail Buyer Admin " + e.getMessage(), e);
		}
	}

	private void sendEmailNotificationToSupplier(Supplier supplier, String oldCompanyName) {
		try {
			String couminactionEmail = "";
			couminactionEmail = supplier.getCommunicationEmail();
			LOG.info("Sending Company Name Changed email to (" + supplier.getCompanyName() + ") : " + couminactionEmail);
			String subject = "Company Name changed from " + oldCompanyName + " to " + supplier.getCompanyName();
			String message = "Your company name has been changed successfully by Procurehere Administrator.";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("supplierName", supplier.getCompanyName());
			map.put("newComapnyName", supplier.getCompanyName());
			map.put("oldComapnyName", oldCompanyName);
			map.put("message", message);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(supplier.getId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");

			List<User> adminSuppList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
			if (CollectionUtil.isNotEmpty(adminSuppList)) {
				for (User suppAdmin : adminSuppList) {
					map.put("supplierName", suppAdmin.getName());
					LOG.info("Sending Company Name Changed email to  " + suppAdmin.getCommunicationEmail() + " .. " + suppAdmin.getName());
					if (StringUtils.checkString(suppAdmin.getCommunicationEmail()).length() > 0 && suppAdmin.getEmailNotifications()) {
						sendEmail(suppAdmin.getCommunicationEmail(), subject, map, Global.COMPANY_NAME_CHANGED_TEMPLATE);
					} else {
						LOG.warn("No communication email configured for user : " + suppAdmin.getName() + "... Not going to send email notification");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.info("Error while sending email to supplier admin " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Supplier updateSupplierRegistrationNumber(String regNumber, String supplierId, String tenantId, HttpHeaders headers) throws NoSuchMessageException, ApplicationException {

		String msg = "";
		Supplier supplier = supplierDao.findById(supplierId);

		if (!isExistsByCompanyNameOrRegNo(supplier, null, regNumber)) {
			msg = "Supplier Company Registration Number changed from '" + supplier.getCompanyRegistrationNumber() + "' to '" + regNumber + "'";
			String oldRegNo = supplier.getCompanyRegistrationNumber();
			if (!StringUtils.checkString(oldRegNo).equals(regNumber)) {
				supplier.setFormerRegistrationNumber(supplier.getCompanyRegistrationNumber());
				supplier.setCompanyRegistrationNumber(regNumber);
				supplierDao.saveOrUpdate(supplier);
				LOG.info("Registration Number updated successfully : ");

				try {
					OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, msg, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Supplier);
					ownerAuditTrailDao.save(ownerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while audit trail :" + e.getMessage(), e);
				}

				// Supplier Admin
				sendRegNoChnagedEmailNotificationToSupplier(supplier, tenantId, oldRegNo);

				// Proc Admin
				sendRegNoChangedEmailNotificationToProcurehereAdmin(supplier, tenantId, oldRegNo);

				// Buyer Admin
				sendRegNoChangedEmailNotificationToBuyerAdmin(supplier, tenantId, oldRegNo);
			}
		} else {
			LOG.info("Duplicate Supplier : ");
			throw new ApplicationException(messageSource.getMessage("supplier.error.duplicate", new Object[] { supplier.getCompanyName(), regNumber, supplier.getRegistrationOfCountry() }, Global.LOCALE));
		}

		return supplier;
	}

	private void sendRegNoChangedEmailNotificationToBuyerAdmin(Supplier supplier, String tenantId, String oldRegNum) {

		try {
			LOG.info("Sending Reg No Changed email to ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";

			String subject = "Supplier " + supplier.getCompanyName() + " registration number changed from " + oldRegNum + " to " + supplier.getCompanyRegistrationNumber();
			String message = "Supplier " + supplier.getCompanyName() + " has changed their company details as below";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("newRegistrationNum", supplier.getCompanyRegistrationNumber());
			map.put("oldRegNum", oldRegNum);
			map.put("message", message);
			map.put("loginUrl", APP_URL + "/login");

			List<Buyer> associatedBuyerList = favoriteSupplierDao.findFavSupplierBySuppId(supplier.getId());
			if (CollectionUtil.isNotEmpty(associatedBuyerList)) {
				for (Buyer buyer : associatedBuyerList) {
					List<User> adminBuyerList = userDao.getAllAdminUsersForBuyer(buyer.getId());
					if (CollectionUtil.isNotEmpty(adminBuyerList)) {
						for (User buyerAdmin : adminBuyerList) {
							LOG.info(">>>>>>>> Sending Company Name Changed email to  " + buyerAdmin.getCommunicationEmail() + " .. " + buyerAdmin.getLoginId());
							timeZone = getTimeZoneByBuyerSettings(tenantId, timeZone);
							df.setTimeZone(TimeZone.getTimeZone(timeZone));
							map.put("date", df.format(new Date()));
							map.put("supplierName", buyerAdmin.getName());
							if (StringUtils.checkString(buyerAdmin.getCommunicationEmail()).length() > 0 && buyerAdmin.getEmailNotifications()) {
								sendEmail(buyerAdmin.getCommunicationEmail(), subject, map, Global.REGISTRATION_NUMBER_CHANGED_TEMPLATE);
							} else {
								LOG.warn("No communication email configured for user : " + buyerAdmin.getName() + "... Not going to send email notification");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending email notification " + e.getMessage(), e);
		}
	}

	private void sendRegNoChangedEmailNotificationToProcurehereAdmin(Supplier supplier, String tenantId, String oldRegNumber) {
		try {
			LOG.info("Sending Reg No Changed email to  supplier admin ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			String subject = "Supplier " + supplier.getCompanyName() + " registration number changed from " + oldRegNumber + " to " + supplier.getCompanyRegistrationNumber();
			String message = "Supplier company details for " + supplier.getCompanyName() + " have been changed as below ";

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("newRegistrationNum", supplier.getCompanyRegistrationNumber());
			map.put("oldRegNum", oldRegNumber);
			map.put("message", message);
			map.put("loginUrl", APP_URL + "/login");

			List<User> adminList = userDao.getAllAdminPlainUsersForSupplier(tenantId);
			if (CollectionUtil.isNotEmpty(adminList)) {
				for (User procAdmin : adminList) {
					map.put("supplierName", procAdmin.getName());
					timeZone = getTimeZoneByBuyerSettings(tenantId, timeZone);
					df.setTimeZone(TimeZone.getTimeZone(timeZone));
					map.put("date", df.format(new Date()));
					LOG.info("Sending Reg No Changed email to  " + procAdmin.getCommunicationEmail() + " .. " + procAdmin.getName());
					if (StringUtils.checkString(procAdmin.getCommunicationEmail()).length() > 0 && procAdmin.getEmailNotifications()) {
						sendEmail(procAdmin.getCommunicationEmail(), subject, map, Global.REGISTRATION_NUMBER_CHANGED_TEMPLATE);
					} else {
						LOG.warn("No communication email configured for user : " + procAdmin.getName() + "... Not going to send email notification");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Error while sending mail Buyer Admin " + e.getMessage(), e);
		}
	}

	private void sendRegNoChnagedEmailNotificationToSupplier(Supplier supplier, String tenantId, String oldRegNumber) {
		try {
			String couminactionEmail = "";
			couminactionEmail = supplier.getCommunicationEmail();
			LOG.info("Sending Approval email to (" + supplier.getCompanyName() + ") : " + couminactionEmail);
			String subject = "Company Registration Number changed from " + oldRegNumber + " to " + supplier.getCompanyRegistrationNumber();
			String message = "Your company registration number has been changed by Procurehere Administrator.";

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("supplierName", supplier.getCompanyName());
			map.put("newRegistrationNum", supplier.getCompanyRegistrationNumber());
			map.put("oldRegNum", oldRegNumber);
			map.put("message", message);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(tenantId, timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");

			List<User> adminSuppList = userDao.getAllAdminPlainUsersForSupplier(supplier.getId());
			if (CollectionUtil.isNotEmpty(adminSuppList)) {
				for (User suppAd : adminSuppList) {
					timeZone = getTimeZoneByBuyerSettings(suppAd.getId(), timeZone);
					df.setTimeZone(TimeZone.getTimeZone(timeZone));
					map.put("date", df.format(new Date()));
					map.put("supplierName", suppAd.getName());
					LOG.info("Sending Reg No Changed email to " + suppAd.getCommunicationEmail() + ".." + suppAd.getName());
					if (StringUtils.checkString(suppAd.getCommunicationEmail()).length() > 0 && suppAd.getEmailNotifications()) {
						sendEmail(suppAd.getCommunicationEmail(), subject, map, Global.REGISTRATION_NUMBER_CHANGED_TEMPLATE);
					} else {
						LOG.warn("No communication email configured for user : " + suppAd.getName() + "... Not going to send email notification");
					}
				}
			}
		} catch (Exception e) {
			LOG.error("ERROR while Sending mail :" + e.getMessage(), e);
		}

	}

	private void 
	sendEmail(String mailTo, String subject, HashMap<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	@Override
	@Transactional(readOnly = true)
	public SupplierFormSubmition findSupplierFormBySupplierId(String supplierId) {
		return supplierFormSubmissDao.findOnBoardingFormForSupplier(supplierId);
	}

	@Override
	@Transactional(readOnly = true)
	public Supplier findPlainSupplierUsingConstructorById(String id) {
		return supplierDao.findPlainSupplierUsingConstructorById(id);
	}

	@Override
	public List<Buyer> getBuyerListForSupplierId(String tenantId) {
		List<Buyer> buyerList = new ArrayList<Buyer>();
		List<String> buyerIdList = supplierDao.getBuyerListForSupplierId(tenantId);

		for (String id : buyerIdList) {
			Buyer buyer = buyerDao.findBuyerById(id);
			buyerList.add(buyer);
		}

		return buyerList;
	}



}
