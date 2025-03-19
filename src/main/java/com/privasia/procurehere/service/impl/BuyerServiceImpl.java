package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.BuyerAddressDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.BuyerSubscriptionDao;
import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.dao.ProductCategoryMaintenanceDao;
import com.privasia.procurehere.core.dao.ProductListMaintenanceDao;
import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.SecurityToken.TokenValidity;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.IdSettingType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.pojo.BuyerReportPojo;
import com.privasia.procurehere.core.pojo.BuyerSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.BuyerNotesDao;
import com.privasia.procurehere.core.supplier.dao.OwnerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.EmailSettingsService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.UserService;

@Service
@Transactional(readOnly = true)
public class BuyerServiceImpl implements BuyerService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerDao buyerDao;

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
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	BuyerNotesDao buyerNotesDao;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	TimeZoneDao timeZoneDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@Autowired
	OwnerDao ownerDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	BuyerSubscriptionDao buyerSubscriptionDao;

	@Autowired
	UomService uomService;

	@Autowired
	ProductListMaintenanceDao productListMaintenanceDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	BusinessUnitDao businessUnitDao;

	@Autowired
	BuyerAddressDao buyerAddressDao;

	@Autowired
	ProductCategoryMaintenanceDao productCategoryMaintenanceDao;

	@Autowired
	private Environment env;

	@Override
	@Transactional(readOnly = false)
	public User saveBuyer(Buyer buyer) throws ApplicationException {
		User user = null;
		try {
			buyer = buyerDao.save(buyer);
			Date createdDate = new Date();
			LOG.info("User saved................");
			/**
			 * Create User Role
			 */
			UserRole userRole = new UserRole();
			userRole = new UserRole();
			userRole.setRoleName("Administrator".toUpperCase());
			userRole.setRoleDescription("Application Administrator");
			userRole.setCreatedDate(createdDate);
			userRole.setTenantId(buyer.getId());

			User createdBy = null;
			try {
				createdBy = SecurityLibrary.getLoggedInUser();
			} catch (SecurityRuntimeException e) {
				// e.printStackTrace();
			}
			if (createdBy == null) {
				createdBy = userService.getUserByLoginId("admin@procurehere.com");
			}
			userRole.setCreatedBy(createdBy);
			boolean isEventBasedSubscription = false;
			if (buyer.getCurrentSubscription() != null && buyer.getCurrentSubscription().getPlanType() == PlanType.PER_EVENT) {
				isEventBasedSubscription = true;
			}
			LOG.info("Is Event Based Subscription :" + isEventBasedSubscription);
			LOG.info("userRole :" + userRole.getRoleName());
			LOG.info(">>>> access role list :" + accessRightsDao.getAccessControlListForBuyer(false, isEventBasedSubscription));
			userRoleService.saveUserRole(userRole, accessRightsDao.getAccessControlListForBuyer(false, isEventBasedSubscription));
			LOG.info("BUYER ADMIN USER ROLE CREATED FOR BUYER [ " + buyer.getCompanyName() + "] ");

			/**
			 * Create user and send email notification to supplier
			 */
			user = new User();
			user.setLoginId(buyer.getLoginEmail().toUpperCase());
			user.setCommunicationEmail(buyer.getCommunicationEmail());
			user.setTenantId(buyer.getId());
			user.setTenantType(TenantType.BUYER);
			user.setName(buyer.getFullName());
			user.setBuyer(buyer);
			user.setPassword("dummy_password_that_will_not_work"); // Password
																	// will be
																	// set by
																	// the Buyer
																	// upon
																	// clicking
																	// on
																	// the email
																	// link.
			user.setCreatedDate(createdDate);
			user.setLastPasswordChangedDate(createdDate);
			user.setCreatedBy(createdBy);

			user.setUserRole(userRole);
			user = userService.saveUser(user);

			user.getBuyer().getCompanyName();

			LOG.info("ADMIN USER CREATED FOR BUYER [ " + buyer.getCompanyName() + "] LOGIN ID : [" + buyer.getLoginEmail() + " ]");

			/*
			 * Create Default Buyer Settings
			 */
			try {
				buyerSettingsDao.createDefaultBuyerSettings(buyer, createdDate, createdBy);
			} catch (Exception e) {
				LOG.error("Error creating default buyer settings : " + e.getMessage(), e);
				throw new ApplicationException("Error creating default buyer settings : " + e.getMessage(), e);
			}

			LOG.info("Default buyer settings created...");
			/*
			 * Create default Event ID Settings
			 */
			createDefaultEventIdSettingsForBuyer(buyer);
			LOG.info("Default event ID settings created...");

			try {
				createBuyerDefaultRoles(buyer.getId(), createdBy, isEventBasedSubscription);
			} catch (Exception e) {
				LOG.error("Error while creating default roles for [ " + buyer.getCompanyName() + " ] " + e.getMessage(), e);
				throw new ApplicationException("Error while creating default roles for [ " + buyer.getCompanyName() + " ]  " + e.getMessage(), e);
			}
			LOG.info("Finished saving user.....");
		} catch (Exception e) {
			LOG.error("Error creating Admin Account for Buyer and sending email notification : " + e.getMessage(), e);
			throw new ApplicationException("Error creating Admin Account for Buyer and sending email notification :  " + e.getMessage(), e);
		}
		return user;
	}

	/**
	 * @param buyer
	 */
	private void createDefaultEventIdSettingsForBuyer(Buyer buyer) {
		try {
			IdSettings settings = new IdSettings();
			settings = new IdSettings();
			settings.setIdDatePattern("MMdd");
			settings.setIdPerfix("RFI");
			settings.setIdType("RFI");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			settings = new IdSettings();
			settings.setIdDatePattern("MMdd");
			settings.setIdPerfix("RFP");
			settings.setIdType("RFP");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			settings = new IdSettings();
			settings.setIdDatePattern("MMdd");
			settings.setIdPerfix("RFQ");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdType("RFQ");
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			settings = new IdSettings();
			settings.setIdDatePattern("MMdd");
			settings.setIdPerfix("RFT");
			settings.setIdType("RFT");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			settings = new IdSettings();
			settings.setIdDatePattern("MMdd");
			settings.setIdPerfix("RFA");
			settings.setIdType("RFA");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			// PR
			settings = new IdSettings();
			settings.setIdPerfix("PR");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdType("PR");
			settings.setIdDatePattern("MMYYYY");
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			// PO
			settings = new IdSettings();
			settings.setIdPerfix("PO");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdType("PO");
			settings.setIdDatePattern("YYMMdd");
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			// BUDGET
			settings = new IdSettings();
			settings.setIdPerfix("BG");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdType("BG");
			settings.setIdDatePattern("MMYYYY");
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			// GRN
			settings = new IdSettings();
			settings.setIdPerfix("GRN");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdType("GRN");
 			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

			// SR
			settings = new IdSettings();
			settings.setIdPerfix("SR");
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setIdType("SR");
			settings.setIdSequence(1);
			settings.setTenantId(buyer.getId());
			eventIdSettingsDao.save(settings);

		} catch (Exception e) {
			LOG.error("Error creating default Event ID settings for buyer : " + e.getMessage(), e);
		}
	}

	@Override
	public Owner getDefaultOwner() {
		List<Owner> ownerList = ownerDao.findAll(Owner.class);
		if (CollectionUtil.isNotEmpty(ownerList)) {
			return ownerList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @param buyer
	 * @param user
	 * @throws ApplicationException
	 */
	@Override
	@Transactional(readOnly = false)
	public void sentBuyerCreationMail(Buyer buyer, User user) throws ApplicationException {
		/*
		 * Send email notification start
		 */
		try {
			LOG.info("User : " + user.getId());
			LOG.info("Generating security token.....");
			SecurityToken securityToken = securityTokenDao.generateTokenWithValidityForNewUser(TokenValidity.TWELVE_HOUR, user);
			LOG.info("Generated security token.....");
			StringBuffer content = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

			String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);

			Map<String, String> data = new HashMap<String, String>();
			data.put("token", securityToken.getToken());
			data.put("email", user.getLoginId());

			String[] urlData = EncryptionUtils.encryptObject(data);

			Map<String, Object> model = new HashMap<String, Object>();
			boolean subscribedPlan = false;
			if (buyer.getCurrentSubscription() != null) {
				LOG.info("Buyer current subscription is not null.....");
				subscribedPlan = true;
				model.put("plan", buyer.getCurrentSubscription().getPlan() != null ? buyer.getCurrentSubscription().getPlan().getPlanName() : "Not Available");
				model.put("startDate", buyer.getCurrentSubscription().getStartDate() != null ? format.format(buyer.getCurrentSubscription().getStartDate()) : "");
				model.put("endDate", buyer.getCurrentSubscription().getEndDate() != null ? format.format(buyer.getCurrentSubscription().getEndDate()) + " (UTC)" : "Not Available");
				DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
				model.put("currencyCode", buyer.getCurrentSubscription() != null ? buyer.getCurrentSubscription().getCurrencyCode() : "Not Available");
				model.put("amount", buyer.getCurrentSubscription() != null ? df.format(buyer.getCurrentSubscription().getTotalPriceAmount()) : "");
			}

			model.put("subscribedPlan", subscribedPlan);
			model.put("fullName", buyer.getFullName());
			model.put("date", format.format(new Date()));
			model.put("logoUrl", StringUtils.checkString(appPath) + "/resources/images/public/procuhereLogo.png");
			model.put("appLink", StringUtils.checkString(appPath) + "/buyer/buyerProfileSetup?d=" + urlData[0] + "&v=" + urlData[1]);
			content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/templates/buyer_creation.vm", "UTF-8", model));
			LOG.info("Sending email.....");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(buyer.getCommunicationEmail(), "Buyer registration request for PROCUREHERE", content.toString());
				LOG.info("BUYER ACCOUNT CREATION EMAIL SENT FOR BUYER [ " + buyer.getCompanyName() + "] ");
			}
			} catch (Exception e) {
			LOG.error("Error occured while sending email notification on buyer creation : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExists(Buyer buyer) {
		return buyerDao.isExists(buyer);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExistsLoginEmail(String loginEmail) {
		return buyerDao.isExistsLoginEmail(loginEmail);

	}

	@Override
	@Transactional(readOnly = false)
	public Buyer updateBuyer(Buyer buyer) {
		User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
		if (adminUser != null) {
			if (!StringUtils.checkString(adminUser.getCommunicationEmail()).equalsIgnoreCase(StringUtils.checkString(buyer.getCommunicationEmail()))) {
				adminUser.setCommunicationEmail(buyer.getCommunicationEmail().toUpperCase());
				userService.updateUser(adminUser);
			}
		}
		return buyerDao.update(buyer);
	}

	@Override
	@Transactional(readOnly = false)
	public Buyer updateBuyerOnly(Buyer buyer) {
		return buyerDao.update(buyer);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBuyer(Buyer buyer) {
		buyerDao.delete(buyer);
	}

	@Override
	public List<Buyer> findAllBuyers() {
		return buyerDao.findAllBuyers();
	}

	@Override
	public List<Buyer> findAllActiveBuyers() {
		return buyerDao.findAllActiveBuyers();
	}

	@Override
	public List<Buyer> findAllActiveBuyersWithActiveSubscription() {
		return buyerDao.findAllActiveBuyersWithActiveSubscription();
	}

	@Override
	public Buyer findBuyerById(String buyerId) {
		Buyer buyer = buyerDao.findById(buyerId);
		if (buyer != null) {
			if (buyer.getSupplierForm() != null) {
				buyer.getSupplierForm().getName();
			}
		}
		return buyer;

	}

	@Override
	public Buyer findPlainBuyerById(String id) {
		return buyerDao.findPlainBuyerById(id);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = ApplicationException.class)
	public void confirmBuyer(Buyer supplier) throws ApplicationException {
		try {

		} catch (Exception e) {
			LOG.error("Error while approving supplier registration , " + e.getMessage(), e);
			throw new ApplicationException("Error while approving supplier registration , " + e.getMessage());
		}
	}

	@Override
	public List<Buyer> searchBuyers(String status, String order, String globalSearch) {
		return buyerDao.searchBuyer(status, order, globalSearch);
	}

	@Override
	public boolean isExistsRegistrationNumber(Buyer buyer) {
		return buyerDao.isExistsRegistrationNumber(buyer);
	}

	@Override
	public boolean isExistsCompanyName(Buyer buyer) {
		return buyerDao.isExistsCompanyName(buyer);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Notes saveByerNotes(Notes notes) {
		return buyerNotesDao.saveOrUpdate(notes);
	}

	@Override
	public List<Notes> findAllNotesById(String id) {
		return buyerNotesDao.findAllNotesById(id);
	}

	@Override
	public List<Buyer> findAllBuyers1() {
		return buyerDao.findAll1();
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public User saveManualBuyer(Buyer buyerObj, String createdByLoginId) throws Exception {

		BuyerSubscription subscription = buyerObj.getCurrentSubscription();
		subscription.setCreatedDate(new Date());
		subscription.setActivatedDate(new Date());
		if (subscription.getPlan() != null) {

			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(subscription.getPlan().getId());
			// default lowest period duration for manual buyer
			if (plan != null) {
				if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
					subscription.setPlanPeriod(plan.getPlanPeriodList().get(0));
				}
				subscription.setPlanType(plan.getPlanType());

				// default highest range price for manual buyer
				if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
					subscription.setRange(plan.getRangeList().get(0));
				}
			}
			subscription.setCurrencyCode(plan.getCurrency().getCurrencyCode());
		}
		subscription.setPriceAmount(new BigDecimal(0));
		subscription.setTotalPriceAmount(new BigDecimal(0));
		subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
		subscription.setActivatedDate(new Date());

		Calendar validityDate = Calendar.getInstance();
		validityDate.set(Calendar.HOUR_OF_DAY, 00);
		validityDate.set(Calendar.MINUTE, 00);
		validityDate.set(Calendar.SECOND, 00);
		subscription.setStartDate(validityDate.getTime());

		validityDate = Calendar.getInstance();
		validityDate.setTime(subscription.getEndDate());
		validityDate.set(Calendar.HOUR_OF_DAY, 23);
		validityDate.set(Calendar.MINUTE, 59);
		validityDate.set(Calendar.SECOND, 59);
		subscription.setEndDate(validityDate.getTime());
		// Construct the Buyer Package that will hold the total sum of all
		// active subscriptions
		BuyerPackage buyerPackage = new BuyerPackage(subscription);
		buyerPackage.setBuyer(buyerObj);

		// We will save subscription later after saving the Buyer.
		buyerObj.setCurrentSubscription(null);

		subscription = buyerSubscriptionService.saveBuyerSubscription(subscription);
		buyerObj.setCurrentSubscription(subscription);

		buyerObj.setBuyerPackage(buyerPackage);
		buyerObj.setStatus(BuyerStatus.PENDING);
		buyerObj.setCommunicationEmail(buyerObj.getLoginEmail());
		buyerObj.setCreatedDate(new Date());
		buyerObj.setSubscriptionDate(new Date());
		User user = saveBuyer(buyerObj);

		LOG.info("Created Buyer User : " + user.toLogString());
		LOG.info("User Buyer Created: " + user.getBuyer().toLogString());

		buyerObj = user.getBuyer();

		subscription.setBuyer(buyerObj);
		subscription = buyerSubscriptionService.updateSubscription(subscription);

		// Save the Subscription now
		// subscription.setBuyer(buyerObj);
		// subscription = subscriptionService.saveSubscription(subscription);
		// LOG.info("Buyer's current Subscription : " +
		// subscription.toLogString());

		// Attach the subscription object.
		// buyerObj.setCurrentSubscription(subscription);
		// System.out.println("Updating buyer....");
		// updateBuyer(buyerObj);
		// System.out.println("updated buyer.....");
		// sentBuyerCreationMail(buyerObj, user);

		return user;

	}

	@Override
	public Buyer findBuyerGeneralDetailsById(String tenantId) {
		return buyerDao.findBuyerGeneralDetailsById(tenantId);
	}

	private void createBuyerDefaultRoles(String tenantId, User createdBy, boolean isEventBasedSubscription) {

		UserRole userRole = new UserRole();
		userRole.setRoleName("System Setting Admin".toUpperCase());
		userRole.setRoleDescription("System Setting Admin");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_SYSTEM_ADMIN_SETTING_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		LOG.info("ACL LIST SIZE 111 :" + userRole.getAccessControlList().size());
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("User Admin".toUpperCase());
		userRole.setRoleDescription("User Admin");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_USER_ADMIN_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Account Admin".toUpperCase());
		userRole.setRoleDescription("Account Admin");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_ACCOUNT_ADMIN_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Buyer User".toUpperCase());
		userRole.setRoleDescription("Buyer User");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_USER_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Event & PR Creator".toUpperCase());
		userRole.setRoleDescription("Event Creator");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_EVENT_PR_CREATOR_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Event Creator".toUpperCase());
		userRole.setRoleDescription("Event Creator");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_EVENT_CREATOR_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("PR Creator".toUpperCase());
		userRole.setRoleDescription("PR Creator");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_PR_CREATE_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Auction Creator".toUpperCase());
		userRole.setRoleDescription("Auction Creator");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_AUCTION_CREATE_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("RFx Creator".toUpperCase());
		userRole.setRoleDescription("RFx Creator");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_RFX_CREATE_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Admin Viewer".toUpperCase());
		userRole.setRoleDescription("Admin Viewer");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_READONLY_ADMIN_ACL_VALUES));
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Buyer Approver User".toUpperCase());
		userRole.setRoleDescription("Buyer Approver User Role only for approver user type can't be modify or delete");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_APPROVER_USER_ACL_VALUES));
		userRole.setReadOnly(Boolean.TRUE);
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole, createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Buyer Requester User".toUpperCase());
		userRole.setRoleDescription("Buyer Requester User Role only for Requester user type can't be modify or delete");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_REQUESTER_USER_ACL_VALUES));
		userRole.setReadOnly(Boolean.TRUE);
		removeAclPrPoForEventBasedSubscription(isEventBasedSubscription, userRole);
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		LOG.info("User role saved.....");
		userRoleService.saveRole(userRole, createdBy);

	}

	private void removeAclPrPoForEventBasedSubscription(boolean isEventBasedSubscription, UserRole userRole) {
		if (isEventBasedSubscription && CollectionUtil.isNotEmpty(userRole.getAccessControlList())) {
			List<AccessRights> aclList = new ArrayList<AccessRights>();
			for (AccessRights accessRights : userRole.getAccessControlList()) {
				if (!(accessRights.getAclValue().equalsIgnoreCase("ROLE_PR_PO") || accessRights.getAclValue().equalsIgnoreCase("ROLE_PR_CREATE") || accessRights.getAclValue().equalsIgnoreCase("ROLE_PR_TEMPLATE_LIST") || accessRights.getAclValue().equalsIgnoreCase("ROLE_PR_TEMPLATE_EDIT") || accessRights.getAclValue().equalsIgnoreCase("ROLE_PR_TEMPLATE_VIEW_ONLY") || accessRights.getAclValue().equalsIgnoreCase("ROLE_VIEW_PO_LIST") || accessRights.getAclValue().equalsIgnoreCase("ROLE_VIEW_PR_DRAFT"))) {
					aclList.add(accessRights);
					LOG.info("accessRights.getAclValueUserRoleServiceImpl() :" + accessRights.getAclValue());
				}

			}
			userRole.setAccessControlList(aclList);
			LOG.info("ACL LIST SIZE :" + aclList.size());
		}
	}

	@Override
	public List<Buyer> findAllBuyersWithActiveSubscription() {
		return buyerDao.findAllBuyersWithActiveSubscription();
	}

	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore30Days() {
		return buyerDao.findBuyersForExpiryNotificationReminderBefore30Days();
	}

	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore15Days() {
		return buyerDao.findBuyersForExpiryNotificationReminderBefore15Days();
	}

	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore7Days() {
		return buyerDao.findBuyersForExpiryNotificationReminderBefore7Days();
	}

	// @Override
	// @Transactional(readOnly = false, rollbackFor = Exception.class)
	// public Buyer updateManualSubscription(String buyerId, String startDate,
	// String endDate, Integer userLimit, Integer eventLimit) throws Exception {
	// SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	// Buyer buyerObj = buyerDao.findById(buyerId);
	//
	// BuyerPackage buyerPackage = buyerObj.getBuyerPackage();
	// Date oldStartDate = buyerPackage.getStartDate();
	// Date oldEndDate = buyerPackage.getEndDate();
	// Integer oldEventLimit = buyerPackage.getEventLimit();
	// Integer oldUserLimit = buyerPackage.getUserLimit();
	//
	// buyerPackage.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
	// buyerPackage.setStartDate(sd.parse(startDate));
	// buyerPackage.setEndDate(sd.parse(endDate));
	// buyerPackage.setUserLimit(userLimit);
	// buyerPackage.setEventLimit(eventLimit);
	//
	// // updating last user subscription
	// BuyerSubscription buyerSubscription =
	// buyerSubscriptionDao.getLastBuyerSubscriptionForBuyer(buyerId);
	// if (SubscriptionStatus.EXPIRED ==
	// buyerSubscription.getSubscriptionStatus()) {
	// buyerSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
	// }
	// buyerSubscription.setEndDate(buyerPackage.getEndDate());
	//
	// // updating current user subscription
	// if (SubscriptionStatus.ACTIVE ==
	// buyerSubscription.getSubscriptionStatus()) {
	// buyerSubscription.setUserQuantity(userLimit);
	// buyerSubscription.setEventQuantity(eventLimit);
	// } else {
	// BuyerSubscription currentSubscription =
	// buyerSubscriptionDao.getCurrentBuyerSubscriptionForBuyer(buyerId);
	// currentSubscription.setUserQuantity(userLimit);
	// currentSubscription.setEventQuantity(eventLimit);
	// buyerSubscriptionService.updateSubscription(currentSubscription);
	// }
	// buyerSubscriptionService.updateSubscription(buyerSubscription);
	//
	// buyerObj.setBuyerPackage(buyerPackage);
	// buyerObj = updateBuyer(buyerObj);
	// OwnerSettings ownerSettings =
	// ownerSettingsDao.getOwnersettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
	// String timeZone = "GMT+8:00";
	// if (ownerSettings != null && ownerSettings.getTimeZone() != null) {
	// timeZone = ownerSettings.getTimeZone().getTimeZone();
	// }
	// sd.setTimeZone(TimeZone.getTimeZone(timeZone));
	// try {
	// String msg = "";
	// msg += sd.format(oldStartDate).equals(sd.format(sd.parse(startDate))) ?
	// "" : "Start Date changed from '" + sd.format(oldStartDate) + "' to '" +
	// sd.format(sd.parse(startDate)) + "' and ";
	// msg += sd.format(oldEndDate).equals(sd.format(sd.parse(endDate))) ? "" :
	// "End Date changed from '" + sd.format(oldEndDate) + "' to '" +
	// sd.format(sd.parse(endDate)) + "' and ";
	// msg += oldUserLimit.intValue() == userLimit.intValue() ? "" : "User Limit
	// changed from '" + oldUserLimit + "' to '" + userLimit + "' and ";
	// msg += oldEventLimit.intValue() == eventLimit.intValue() ? "" : "Event
	// Limit changed from '" + oldEventLimit + "' to '" + eventLimit + "'";
	// msg += StringUtils.checkString(msg).length() > 0 ? " for buyer '" +
	// buyerObj.getLoginEmail() + "'" : "";
	// LOG.info("subscription Changed : " + msg);
	// if (StringUtils.checkString(msg).length() > 0) {
	// msg = "Subscription " + msg;
	// OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE,
	// msg, SecurityLibrary.getLoggedInUserTenantId(),
	// SecurityLibrary.getLoggedInUser(), new Date());
	// ownerAuditTrailDao.save(ownerAuditTrail);
	// }
	// } catch (Exception e) {
	// LOG.error("Error while audit trail :" + e.getMessage(), e);
	// }
	// return buyerObj;
	// }

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Buyer updateManualSubscription(String buyerId, String startDate, String endDate, Integer userLimit, Integer eventLimit) throws Exception {
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		Buyer buyerObj = buyerDao.findById(buyerId);
		BuyerSubscription currSubscription = buyerObj.getCurrentSubscription();
		if (currSubscription != null) {
			Date oldStartDate = currSubscription.getStartDate();
			Date oldEndDate = currSubscription.getEndDate();
			Integer oldEventLimit = currSubscription.getEventQuantity();
			Integer oldUserLimit = currSubscription.getUserQuantity();

			currSubscription.setEndDate(setNightDate(sd.parse(endDate)));
			currSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
			currSubscription.setUserQuantity(userLimit);
			currSubscription.setEventQuantity(eventLimit);
			buyerSubscriptionService.updateSubscription(currSubscription);

			Date subsEndDate = currSubscription.getEndDate();
			BuyerSubscription futureSubs = currSubscription.getNextSubscription();

			while (futureSubs != null) {
				Integer duration = 30;
				if (futureSubs.getPlanPeriod() != null) {
					duration = futureSubs.getPlanPeriod().getPlanDuration();
				}
				Date subsStartDate = getnextDate(subsEndDate);
				futureSubs.setStartDate(subsStartDate);

				subsEndDate = getEndDate(duration, subsStartDate);
				futureSubs.setEndDate(subsEndDate);
				futureSubs = futureSubs.getNextSubscription();
			}

			BuyerPackage buyerPackage = buyerObj.getBuyerPackage();
			buyerPackage.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
			buyerPackage.setEndDate(subsEndDate);
			buyerPackage.setUserLimit(userLimit);
			buyerPackage.setEventLimit(eventLimit);

			buyerObj.setBuyerPackage(buyerPackage);
			buyerObj = updateBuyer(buyerObj);
			OwnerSettings ownerSettings = ownerSettingsDao.getOwnersettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			String timeZone = "GMT+8:00";
			if (ownerSettings != null && ownerSettings.getTimeZone() != null) {
				timeZone = ownerSettings.getTimeZone().getTimeZone();
			}
			// sd.setTimeZone(TimeZone.getTimeZone(timeZone));
			try {
				String msg = "";
				msg += sd.format(oldStartDate).equals(sd.format(sd.parse(startDate))) ? "" : "Start Date changed from '" + sd.format(oldStartDate) + "' to '" + sd.format(sd.parse(startDate)) + "' and ";
				msg += sd.format(oldEndDate).equals(sd.format(sd.parse(endDate))) ? "" : "End Date changed from '" + sd.format(oldEndDate) + "' to '" + sd.format(sd.parse(endDate)) + "' " + ((oldUserLimit.intValue() != userLimit.intValue() || oldEventLimit.intValue() != eventLimit.intValue()) ? " and " : "");
				msg += oldUserLimit.intValue() == userLimit.intValue() ? "" : "User Limit changed from '" + oldUserLimit + "' to '" + userLimit + "'" + ((oldEventLimit.intValue() != eventLimit.intValue()) ? " and " : "");
				msg += oldEventLimit.intValue() == eventLimit.intValue() ? "" : "Event Limit changed from '" + oldEventLimit + "' to '" + eventLimit + "'";
				msg += StringUtils.checkString(msg).length() > 0 ? " for buyer '" + buyerObj.getCompanyName() + "'" : "";
				LOG.info("subscription Changed : " + msg);
				if (StringUtils.checkString(msg).length() > 0) {
					msg = "Subscription " + msg;
					OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, msg, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Buyer);
					ownerAuditTrailDao.save(ownerAuditTrail);
				}
			} catch (Exception e) {
				LOG.error("Error while audit trail :" + e.getMessage(), e);
			}
		} else if (buyerObj.getBuyerPackage() != null) {

			BuyerPackage buyerPackage = buyerObj.getBuyerPackage();
			Date oldStartDate = buyerPackage.getStartDate();
			Date oldEndDate = buyerPackage.getEndDate();
			Integer oldEventLimit = buyerPackage.getEventLimit();
			Integer oldUserLimit = buyerPackage.getUserLimit();

			buyerPackage.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
			buyerPackage.setEndDate(sd.parse(endDate));
			buyerPackage.setUserLimit(userLimit);
			buyerPackage.setEventLimit(eventLimit);

			buyerObj.setBuyerPackage(buyerPackage);
			buyerObj = updateBuyer(buyerObj);
			OwnerSettings ownerSettings = ownerSettingsDao.getOwnersettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			String timeZone = "GMT+8:00";
			if (ownerSettings != null && ownerSettings.getTimeZone() != null) {
				timeZone = ownerSettings.getTimeZone().getTimeZone();
			}
			// sd.setTimeZone(TimeZone.getTimeZone(timeZone));
			try {
				String msg = "";
				msg += sd.format(oldStartDate).equals(sd.format(sd.parse(startDate))) ? "" : "Start Date changed from '" + sd.format(oldStartDate) + "' to '" + sd.format(sd.parse(startDate)) + "' and ";
				msg += sd.format(oldEndDate).equals(sd.format(sd.parse(endDate))) ? "" : "End Date changed from '" + sd.format(oldEndDate) + "' to '" + sd.format(sd.parse(endDate)) + "' " + ((oldUserLimit.intValue() != userLimit.intValue() || oldEventLimit.intValue() != eventLimit.intValue()) ? " and " : "");
				msg += oldUserLimit.intValue() == userLimit.intValue() ? "" : "User Limit changed from '" + oldUserLimit + "' to '" + userLimit + "' and ";
				msg += oldEventLimit.intValue() == eventLimit.intValue() ? "" : "Event Limit changed from '" + oldEventLimit + "' to '" + eventLimit + "'";
				msg += StringUtils.checkString(msg).length() > 0 ? " for buyer '" + buyerObj.getCompanyName() + "'" : "";
				LOG.info("subscription Changed : " + msg);
				if (StringUtils.checkString(msg).length() > 0) {
					msg = "Subscription " + msg;
					OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, msg, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.OwnerSettings);
					ownerAuditTrailDao.save(ownerAuditTrail);
				}
			} catch (Exception e) {
				LOG.error("Error while audit trail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("buyer package or subscription is null ");
		}

		return buyerObj;
	}

	/**
	 * @param duration
	 * @param subsStartDate
	 * @return
	 */
	public Date getEndDate(Integer duration, Date subsStartDate) {
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(subsStartDate);
		endCal.set(Calendar.AM_PM, Calendar.PM);
		endCal.set(Calendar.HOUR, 11);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		endCal.set(Calendar.MILLISECOND, 59);
		endCal.add(Calendar.DATE, -1);
		endCal.add(Calendar.MONTH, duration);
		return endCal.getTime();
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

	/**
	 * @param subsEndDate
	 * @return
	 */
	private Date getnextDate(Date subsEndDate) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(subsEndDate);
		startCal.set(Calendar.AM_PM, Calendar.AM);
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		startCal.add(Calendar.DATE, 1);
		return startCal.getTime();
	}

	@Override
	public Buyer findBuyerByIdWithBuyerPackage(String buyerId) {
		return buyerDao.findBuyerByIdWithBuyerPackage(buyerId);
	}

	@Override
	public List<Buyer> findAllBuyersFor2DaysBeforeSubscriptionExpire() {
		return buyerDao.findAllBuyersFor2DaysBeforeSubscriptionExpire();
	}

	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore6Months() {
		return buyerDao.findBuyersForExpiryNotificationReminderBefore6Months();
	}

	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore3Months() {
		return buyerDao.findBuyersForExpiryNotificationReminderBefore3Months();
	}

	@Override
	public List<Buyer> findAllBuyersFor7DaysBeforeSubscriptionExpire() {
		return buyerDao.findAllBuyersFor7DaysBeforeSubscriptionExpire();
	}

	@Override
	public List<Buyer> findAllBuyersFor15DaysBeforeSubscriptionExpire() {
		return buyerDao.findAllBuyersFor15DaysBeforeSubscriptionExpire();
	}

	@Override
	public List<Buyer> findAllBuyersFor30DaysBeforeSubscriptionExpire() {
		return buyerDao.findAllBuyersFor30DaysBeforeSubscriptionExpire();
	}

	@Override
	@Transactional(readOnly = false)
	public void sendTrialBuyerCreationMail(Buyer buyer, User user) throws ApplicationException {
		try {
			LOG.info("User : " + user.getId());
			SecurityToken securityToken = securityTokenDao.generateTokenWithValidityForUser(TokenValidity.ONE_DAY, user);
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

			String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);

			Map<String, String> data = new HashMap<String, String>();
			data.put("token", securityToken.getToken());
			data.put("email", user.getLoginId());

			String[] urlData = EncryptionUtils.encryptObject(data);

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("userName", buyer.getFullName());
			model.put("date", format.format(new Date()));

			model.put("logoUrl", StringUtils.checkString(appPath) + "/resources/images/public/procuhereLogo.png");
			model.put("appUrl", StringUtils.checkString(appPath) + "/buyer/buyerProfileSetup?d=" + urlData[0] + "&v=" + urlData[1]);
			model.put("loginUrl", StringUtils.checkString(appPath) + "/login");
			// content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
			// "/templates/buyer_creation.vm", "UTF-8", model));
			// notificationService.sendEmail(buyer.getCommunicationEmail(),
			// "Buyer registration request for PROCUREHERE",
			// content.toString());
			User user1 = userService.getDetailsOfLoggedinUser(buyer.getLoginEmail());
			if (StringUtils.checkString(buyer.getCommunicationEmail()).length() > 0 && user1.getEmailNotifications() ) {
				sendEmail(buyer.getCommunicationEmail(), "Buyer registration request for PROCUREHERE", model, Global.TRIAL_BUYER_CREATION_TEMPLATE);
			} else {
				LOG.warn("No communication email configured Not going to send email notification");
			}

			LOG.info("BUYER ACCOUNT CREATION EMAIL SENT FOR BUYER [ " + buyer.getCompanyName() + "] ");
		} catch (Exception e) {
			LOG.error("Error occured while sending email notification on buyer creation : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
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

	@Override
	@Transactional(readOnly = false)
	public void createBuyerDefaultData(Buyer buyer, Date createdDate, User createdBy) {

		// Create default Address for buyer.
		BuyerAddress address = new BuyerAddress();
		address.setCity(buyer.getCity());
		address.setBuyer(buyer);
		address.setCountry(buyer.getRegistrationOfCountry());
		address.setCreatedDate(createdDate);
		address.setCreatedBy(createdBy);
		address.setLine1(buyer.getLine1());
		address.setLine2(buyer.getLine2());
		address.setState(buyer.getState());
		LOG.info("Post code in Buyer................." + buyer.getPostcode());
		address.setZip(buyer.getPostcode());
		LOG.info("Post code in address................." + address.getZip());
		address.setStatus(Status.ACTIVE);
		address.setTitle("Office Address");
		buyerAddressDao.save(address);

		IndustryCategory generalIc = new IndustryCategory();
		generalIc.setBuyer(buyer);
		generalIc.setCode("GENERAL - 01");
		generalIc.setCreatedBy(createdBy);
		generalIc.setCreatedDate(new Date());
		generalIc.setName("General Purchases");
		generalIc.setStatus(Status.ACTIVE);
		industryCategoryDao.save(generalIc);

		CostCenter costCenter = new CostCenter();
		costCenter.setBuyer(buyer);
		costCenter.setCreatedBy(createdBy);
		costCenter.setCreatedDate(createdDate);
		costCenter.setStatus(Status.ACTIVE);
		costCenter.setCostCenter("GENERAL COSTS");
		costCenter.setDescription("General Costs");
		costCenterDao.save(costCenter);

		BusinessUnit bu = new BusinessUnit();
		bu.setBuyer(buyer);
		bu.setCreatedBy(createdBy);
		bu.setCreatedDate(createdDate);
		bu.setUnitName("GENERAL");
		bu.setDisplayName("GENERAL");
		bu.setStatus(Status.ACTIVE);
		businessUnitDao.save(bu);

		ProductCategory pc = new ProductCategory();
		pc.setBuyer(buyer);
		pc.setCreatedBy(createdBy);
		pc.setCreatedDate(createdDate);
		pc.setProductCode("GENERAL");
		pc.setProductName("General");
		pc.setStatus(Status.ACTIVE);
		pc = productCategoryMaintenanceDao.save(pc);

		List<Uom> uomList = uomService.getAllActiveUomForTenant(buyer.getId());
		if (CollectionUtil.isNotEmpty(uomList)) {
			ProductItem pi = new ProductItem();
			pi.setBuyer(buyer);
			pi.setCreatedBy(createdBy);
			pi.setCreatedDate(createdDate);
			pi.setProductCategory(pc);
			pi.setStatus(Status.ACTIVE);
			pi.setProductCode("GENERAL-01");
			pi.setProductName("General Items");
			pi.setUom(uomList.get(0));
			productListMaintenanceDao.save(pi);
		}

	}

	@Override
	public List<Buyer> searchBuyersForPagination(String status, String order, String globalSearch, String pageNo) {
		return buyerDao.searchBuyerForPagination(status, order, globalSearch, pageNo);
	}

	@Override
	public List<Buyer> findAllActiveMailBoxBuyers() {
		return buyerDao.findAllActiveMailBoxBuyers();
	}

	@Override
	public Integer isExistPublicContextPathForBuyer(String publicContextPath, String buyerId) {
		return buyerDao.isExistPublicContextPathForBuyer(publicContextPath, buyerId);
	}

	@Override
	public String getTenantIdByPublicContextPath(String buyerId) {
		return buyerDao.getTenantIdByPublicContextPath(buyerId);
	}

	@Override
	public String getTenantId(String buyerId) {
		return buyerDao.getTenantId(buyerId);
	}

	@Override
	public String getContextPathByBuyerId(String buyerId) {
		return buyerDao.getContextPathByBuyerId(buyerId);
	}

	@Override
	public List<BuyerReportPojo> findAllSearchFilterBuyerReportList(TableDataInput input, Date startDate, Date endDate) {
		return buyerDao.findAllSearchFilterBuyerReportList(input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterBuyerReportCount(TableDataInput input, Date startDate, Date endDate) {
		return buyerDao.findTotalSearchFilterBuyerReportCount(input, startDate, endDate);
	}

	@Override
	public long findTotalBuyerReportCount(Date startDate, Date endDate) {
		return buyerDao.findTotalBuyerReportCount(startDate, endDate);
	}

	@Override
	public void downloadCsvFileForBuyerList(HttpServletResponse response, File file, BuyerSearchFilterPojo buyerSearchFilterPojo, boolean select_all, String[] buyerIds, DateFormat formatter, Date startDate, Date endDate) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.ALL_BUYER_REPORT_EXCEL_COLUMNS;

			final String[] columns = new String[] { "companyName", "companyRegistrationNumber", "companyType", "yearOfEstablished", "createDate", "status", "companyAddress", "country", "state", "companyContactNumber", "faxNumber", "companyWebsite", "loginEmail", "communicationEmail", "fullName", "mobileNumber", "publicContextPath", "planType", "subscribeEndDate", "userDetails", "eventDetails" };

			long count = buyerDao.findTotalBuyerReportCount(startDate, endDate);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);
			LOG.info("Total Pages : " + totalPages + " count : " + count);
			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<BuyerReportPojo> list = buyerDao.getAllBuyerListForCsvReport(buyerIds, buyerSearchFilterPojo, select_all, PAGE_SIZE, pageNo, startDate, endDate);
				LOG.info("Buyer List : " + list.size());
				for (BuyerReportPojo pojo : list) {
					if (pojo.getRegistrationCompleteDate() != null) {
						pojo.setRegistrationDate(formatter.format(pojo.getRegistrationCompleteDate()));
					}
					if (pojo.getSubscriptionEndDate() != null) {
						pojo.setSubscribeEndDate(formatter.format(pojo.getSubscriptionEndDate()));
					}
					if (pojo.getCreatedDate() != null) {
						pojo.setCreateDate(formatter.format(pojo.getCreatedDate()));
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
		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // Company Name
				new NotNull(), // Registration Number
				new Optional(), // Company Type
				new Optional(), // yearOfEstablished
				new Optional(), // registrationCompleteDate
				new NotNull(), // status
				new Optional(), // companyAddress
				new Optional(), // country
				new Optional(), // state
				new Optional(), // companyContactNumber
				new Optional(), // faxNumber
				new Optional(), // companyWebsite
				new NotNull(), // loginEmail
				new Optional(), // communicationEmail
				new Optional(), // communicationEmail
				new Optional(), // mobileNumber
				new Optional(), // publicContextPath
				new Optional(), // currentSubPlan
				new Optional(), // subscriptionEndDate
				new Optional(), // noOfUsers
				new Optional(), // noOfEvents
		};
		return processors;
	}

	@Override
	public Buyer getContextPathForRegistration(String buyerId) {
 		return buyerDao.getContextPathForRegistration(buyerId);
	}
}
