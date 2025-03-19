package com.privasia.procurehere.service.supplier;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.dao.SupplierFormItemDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmissionDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmitionAuditDao;
import com.privasia.procurehere.core.dao.SupplierPlanDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PoShare;
import com.privasia.procurehere.core.enums.RequestAssociateBuyerStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierFormApprovalStatus;
import com.privasia.procurehere.core.enums.SupplierFormSubAuditVisibilityType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionAuditType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierAssociatedBuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FavoutireSupplierAuditService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.UserService;

@Service
@Transactional(readOnly = true)
public class BuyerSupplierRegistrationServiceImpl implements BuyerSupplierRegistrationService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	SupplierDao supplierDao;

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
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Value("${app.url}")
	String APP_URL;

	@Value("${app.root.url}")
	String APP_ROOT_URL;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	SupplierPlanDao supplierPlanDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	IndustryCategoryService industryCategoryService;

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

	@Autowired
	SupplierAssociatedBuyerDao supplierAssociatedBuyerDao;
	
	@Autowired 
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public Supplier registerSupplier(Supplier supplier, String tenantId) throws ApplicationException {

		List<IndustryCategory> categories = supplier.getIndustryCategories();
		Buyer buyer = buyerDao.findPlainBuyerById(tenantId);

		String textPassword = supplier.getPassword();
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		String password = enc.encode(supplier.getPassword());
		supplier.setPassword(password);
		supplier.setStatus(SupplierStatus.APPROVED);
		supplier.setRegistrationDate(new Date());
		supplier.setApprovedDate(new Date());
		supplier.setCommunicationEmail(supplier.getLoginEmail());
		supplier.setCreatedDate(new Date());
		User createdBy = null;

		createdBy = userService.getAdminUserForBuyer(buyer.getId());
		LOG.info("Buyer Admin account " + createdBy.getLoginId());

		supplier.setCreatedBy(createdBy);
		supplier.setNotes(new ArrayList<Notes>());
		Notes note = new Notes();
		note.setSupplier(supplier);
		note.setIncidentType("Supplier Register");
		note.setCreatedDate(new Date());
		note.setDescription("Supplier Register for : " + buyer.getCompanyName());
		supplier.setCreatedDate(new Date());
		supplier.getNotes().add(note);

		supplier.setBuyerAccount(Boolean.TRUE);
		supplier = supplierDao.save(supplier);

		SupplierSubscription subscription = new SupplierSubscription();
		subscription.setCreatedDate(new Date());
		subscription.setStartDate(new Date());
		subscription.setActivatedDate(new Date());
		subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

		SupplierPlan singlePlan = supplierPlanDao.getPlanByName("Single buyer package");
		
		if (singlePlan != null) {
			subscription.setBuyerLimit(singlePlan.getBuyerLimit());
			Calendar endDate = Calendar.getInstance();
			if (PeriodUnitType.YEAR == singlePlan.getPeriodUnit()) {
				endDate.add(Calendar.YEAR, singlePlan.getPeriod());
			} else {
				endDate.add(Calendar.MONTH, singlePlan.getPeriod());
			}
			subscription.setEndDate(endDate.getTime());

			subscription.setPromoCodeDiscount(new BigDecimal(singlePlan.getPrice()));

			subscription.setSupplierPlan(singlePlan);
			subscription.setPriceAmount(new BigDecimal(singlePlan.getPrice()));
			subscription.setPromoCodeDiscount(new BigDecimal(singlePlan.getPrice()));
			subscription.setTotalPriceAmount(BigDecimal.ZERO);
			subscription.setCurrencyCode(singlePlan.getCurrency().getCurrencyCode());
		}else {
			throw new ApplicationException("There is an issue with subscription plan. Please contact Administrator");
		}

		try {
			subscription.setSupplier(supplier);
			supplierSubscriptionDao.save(subscription);

			SupplierPackage sp = new SupplierPackage(subscription);
			supplier.setSupplierSubscription(subscription);
			supplier.setSupplierPackage(sp);
			supplier = supplierDao.saveOrUpdate(supplier);

			createDefaultSettings(supplier, createdBy, new SupplierSettings());

			RequestedAssociatedBuyer requestBuyerObj = new RequestedAssociatedBuyer();
			requestBuyerObj.setBuyer(buyer);
			requestBuyerObj.setRequestedDate(new Date());
			requestBuyerObj.setStatus(RequestAssociateBuyerStatus.PENDING);

			if (categories != null && categories.size() > 0) {
				List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
				for (IndustryCategory ic : categories) {
					IndustryCategory icObj = industryCategoryService.getIndustryCategorCodeAndNameById(ic.getId());
					icList.add(icObj);
				}
				requestBuyerObj.setIndustryCategory(icList);
			}
			requestBuyerObj.setRequestedBy(createdBy);
			requestBuyerObj.setSupplier(supplier);
			RequestedAssociatedBuyer reqBuyerObj = supplierAssociatedBuyerDao.saveOrUpdate(requestBuyerObj);

			// added into favorite supplier
			FavouriteSupplier favouriteSupplier = new FavouriteSupplier();
			favouriteSupplier.setSupplier(reqBuyerObj.getSupplier());
			favouriteSupplier.setBuyer(reqBuyerObj.getBuyer());
			favouriteSupplier.setCommunicationEmail(reqBuyerObj.getSupplier().getCommunicationEmail());
			favouriteSupplier.setCompanyContactNumber(reqBuyerObj.getSupplier().getCompanyContactNumber());
			favouriteSupplier.setFullName(reqBuyerObj.getSupplier().getFullName());
			favouriteSupplier.setDesignation(reqBuyerObj.getSupplier().getDesignation());
			favouriteSupplier.setCreatedBy(createdBy);
			favouriteSupplier.setCreatedDate(new Date());
			favouriteSupplier.setStatus(FavouriteSupplierStatus.PENDING);
			favouriteSupplier.setVendorCode(reqBuyerObj.getSupplier().getVendorCode());
			favouriteSupplier = favoriteSupplierService.saveRequestedFavoriteSupplier(favouriteSupplier);

			// added audit trail of favorite supplier
			FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
			try {
				audit.setActionBy(createdBy);
				audit.setActionDate(new Date());
				audit.setDescription("Requested");
				audit.setTenantId(reqBuyerObj.getBuyer().getId());
				audit.setFavSupp(reqBuyerObj.getSupplier());
				audit.setRemark(messageSource.getMessage("supplier.audit.supplierequested", new Object[] { reqBuyerObj.getSupplierRemark() }, Global.LOCALE));
				favSuppAuditService.saveFavouriteSupplierAudit(audit);
			} catch (Exception e) {
				LOG.error("Error while updating audit trail of fav supplier:" + e.getMessage(), e);
			}

			try {

				LOG.info("Supplier is added in FavouriteSupplier List :" + supplier.getCompanyName() + " \n Supplier Id :" + supplier.getId());
				if (supplier.getAssociatedBuyers() == null) {
					supplier.setAssociatedBuyers(new ArrayList<Buyer>());
				}
				supplier.getAssociatedBuyers().add(new Buyer(tenantId));
				if (CollectionUtil.isNotEmpty(supplier.getAssociatedBuyers())) {
					LOG.info("Associated buyer supplier list size :" + supplier.getAssociatedBuyers().size());
				}

				if (buyer != null && Boolean.TRUE == buyer.getIsEnablePrequalificationForm()) {
					SupplierForm supplierForm = buyer.getSupplierForm();
					LOG.info("Supplier Form : " + supplierForm.getName());
					SupplierFormSubmition supplierFormSubmition = new SupplierFormSubmition();
					supplierFormSubmition.setRequestedBy(createdBy);
					supplierFormSubmition.setRequestedDate(new Date());
					supplierFormSubmition.setBuyer(buyer);
					supplierFormSubmition.setSupplier(supplier);
					supplierFormSubmition.setSupplierForm(supplierForm);
					supplierFormSubmition.setIsOnboardingForm(Boolean.TRUE);
					supplierFormSubmition.setName(supplierForm.getName());
					supplierFormSubmition.setDescription(supplierForm.getDescription());
					supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.PENDING);
					supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
					supplierFormSubmition.setFavouriteSupplier(favouriteSupplier);
					List<SupplierFormItem> formItems = supplierFormItemDao.getAllFormitemsbyFormId(supplierForm.getId());
					List<SupplierFormSubmissionItem> listFormSubItems = new ArrayList<SupplierFormSubmissionItem>();
					if (CollectionUtil.isNotEmpty(formItems)) {
						for (SupplierFormItem item : formItems) {
							LOG.info("Item Name : " + item.getItemName());
							SupplierFormSubmissionItem supplierCq = new SupplierFormSubmissionItem();
							supplierCq.setSupplierFormItem(item);
							supplierCq.setSupplierFormSubmition(supplierFormSubmition);
							listFormSubItems.add(supplierCq);
						}
					}
					supplierFormSubmition.setFormSubmitionItems(listFormSubItems);
					buildApprovalList(supplierFormSubmition, supplierForm);
					supplierFormSubmition = supplierFormSubmissDao.save(supplierFormSubmition);
					LOG.info("Name : " + supplierFormSubmition.getName());
					if (supplierFormSubmition != null) {
						supplierForm.setPendingCount(supplierForm.getPendingCount() != null && supplierForm.getPendingCount() != 0 ? supplierForm.getPendingCount() + 1 : 1);
						supplierFormDao.update(supplierForm);

						SupplierFormSubmitionAudit formAudit = new SupplierFormSubmitionAudit();
						formAudit.setActionBy(createdBy);
						formAudit.setActionDate(new Date());
						formAudit.setDescription(messageSource.getMessage("supplier.form.audit.assigned", new Object[] {}, Global.LOCALE));
						formAudit.setBuyer(supplierFormSubmition.getBuyer());
						formAudit.setSupplier(supplierFormSubmition.getSupplier());
						formAudit.setSupplierFormSubmition(supplierFormSubmition);
						formAudit.setVisibilityType(SupplierFormSubAuditVisibilityType.BOTH);
						formAudit.setAction(SupplierFormSubmitionAuditType.ASSIGNED);
						supplierFormSubmitionAuditDao.save(formAudit);
						
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ASSIGNED, "Supplier Form Assigned", createdBy.getTenantId(), createdBy, new Date(), ModuleType.SupplierForm);
						buyerAuditTrailDao.save(buyerAuditTrail);

					}
					supplier.setIsOnboardingForm(Boolean.TRUE);
				}

				confirmSupplier(supplier, textPassword);
				supplierDao.saveOrUpdate(supplier);

			} catch (Exception e) {
				LOG.error("Error creating FavouriteSupplier instance : " + e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Error storing supplier subscription : " + e.getMessage(), e);
		}

		return supplier;
	}

	private void buildApprovalList(SupplierFormSubmition supplierFormSubmition, SupplierForm supplierForm) {
		List<SupplierFormApproval> approvalList = supplierFormSubmissionService.getAllApprovalListByFormId(supplierForm.getId());
		if (CollectionUtil.isNotEmpty(approvalList)) {
			List<SupplierFormSubmitionApproval> approvalSubList = new ArrayList<SupplierFormSubmitionApproval>();
			for (SupplierFormApproval supplierFormApproval : approvalList) {
				SupplierFormSubmitionApproval formSubmitionApprovalObj = new SupplierFormSubmitionApproval();
				if (supplierFormApproval != null) {
					formSubmitionApprovalObj.setApprovalType(supplierFormApproval.getApprovalType());
					formSubmitionApprovalObj.setBatchNo(0);
					formSubmitionApprovalObj.setLevel(supplierFormApproval.getLevel());
					formSubmitionApprovalObj.setSupplierFormSubmition(supplierFormSubmition);
					formSubmitionApprovalObj.setActive(false);
					formSubmitionApprovalObj.setDone(false);
					if (CollectionUtil.isNotEmpty(supplierFormApproval.getApprovalUsers())) {
						List<SupplierFormSubmitionApprovalUser> approvalSubUserList = new ArrayList<SupplierFormSubmitionApprovalUser>();
						for (SupplierFormApprovalUser formAppUser : supplierFormApproval.getApprovalUsers()) {
							SupplierFormSubmitionApprovalUser appSubUser = new SupplierFormSubmitionApprovalUser();
							appSubUser.setApproval(formSubmitionApprovalObj);
							appSubUser.setApprovalStatus(ApprovalStatus.PENDING);
							appSubUser.setUser(formAppUser.getUser());
							approvalSubUserList.add(appSubUser);
						}
						formSubmitionApprovalObj.setApprovalUsers(approvalSubUserList);
					}
					approvalSubList.add(formSubmitionApprovalObj);
				}
			}
			supplierFormSubmition.setApprovals(approvalSubList);
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
		} else {
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.APPROVED);
		}
	}

	private void createDefaultSettings(Supplier newSupplier, User createdBy, SupplierSettings supplierSettings) {
		if (newSupplier != null) {
			supplierSettings.setSupplier(newSupplier);
			supplierSettings.setPoShare(PoShare.NONE);
			LOG.info("Country Id " + newSupplier.getRegistrationOfCountry().getId());
			com.privasia.procurehere.core.entity.TimeZone timeZone = timeZoneDao.findByProperties(new String[] { "timeZoneDescription", "status" }, new Object[] { "Asia/Kuala_Lumpur", Status.ACTIVE });
			if (timeZone != null) {
				supplierSettings.setTimeZone(timeZone);
			}
			supplierSettings.setModifiedDate(new Date());
			supplierSettings.setModifiedBy(createdBy);
			supplierSettingsService.saveSettings(supplierSettings);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public SupplierFormSubmition findSupplierFormBySupplierId(String supplierId) {
		return supplierFormSubmissDao.findOnBoardingFormForSupplier(supplierId);
	}

	public Supplier confirmSupplier(Supplier supplier, String password) throws ApplicationException {
		LOG.info("++++++++++++++++++supplier.getFullName()+++++++++++++" + supplier.getFullName());
		try {
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
				if (createdBy == null) {
					createdBy = userService.getUserByLoginId("admin@procurehere.com");
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

				createSupplierDefaultRoles(supplier.getId(), createdBy);

				/**
				 * Send email notification start
				 */
				try {
					sendEmailToSupplier(supplier, password);
				} catch (Exception e) {
					LOG.error("Error sending email to supplier about his account : " + e.getMessage(), e);
				}

				LOG.info("SUPPLIER CONFIRMATION EMAIL SENT FOR SUPPLIER [ " + supplier.getCompanyName() + "] ");

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

	private void sendEmailToSupplier(Supplier supplier, String password) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", supplier.getCompanyName());
			map.put("loginId", supplier.getLoginEmail().toUpperCase());
			map.put("appUrl", APP_URL + "/login");
			map.put("blogUrl", APP_ROOT_URL + "/blog");
			map.put("logoUrl", APP_URL + "/resources/images/public/procuhereLogo.png");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			LOG.info("" + supplier.getCommunicationEmail());
			User user = userService.getDetailsOfLoggedinUser(supplier.getLoginEmail());
			if(user.getEmailNotifications())
			notificationService.sendEmail(supplier.getCommunicationEmail(), "Your Procurehere account is created", map, Global.BUYER_SUPPLIER_CREATION);

		} catch (Exception e) {
			LOG.error("Error sending email to supplier about his account : " + e.getMessage(), e);
		}
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

}
