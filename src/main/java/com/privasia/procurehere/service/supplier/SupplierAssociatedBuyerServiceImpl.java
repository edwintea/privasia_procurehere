package com.privasia.procurehere.service.supplier;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.SupplierFormApprovalDao;
import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmissionDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmitionAuditDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApprovalUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RequestAssociateBuyerStatus;
import com.privasia.procurehere.core.enums.SupplierFormApprovalStatus;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierAssociatedBuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FavoutireSupplierAuditService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;

/**
 * @author pooja
 */
@Service
@Transactional(readOnly = true)
public class SupplierAssociatedBuyerServiceImpl implements SupplierAssociatedBuyerService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	SupplierAssociatedBuyerDao supplierAssociatedBuyerDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	FavoutireSupplierAuditService favSuppAuditService;

	@Autowired
	SupplierFormSubmissionDao supplierFormSubmissDao;

	@Autowired
	SupplierFormSubmitionAuditDao supplierFormSubmitionAuditDao;

	@Autowired
	SupplierFormDao supplierFormDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	SupplierFormApprovalDao supplierFormApprovalDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	NotificationService notificationService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Transactional(readOnly = false)
	@Override
	public void saveOrUpdateAssociateRequest(RequestedAssociatedBuyer requestBuyerObj, User loggedInUser) {

		RequestedAssociatedBuyer reqBuyerObj = supplierAssociatedBuyerDao.saveOrUpdate(requestBuyerObj);

		// added into favorite supplier
		FavouriteSupplier favouriteSupplier = new FavouriteSupplier();
		favouriteSupplier.setSupplier(reqBuyerObj.getSupplier());
		favouriteSupplier.setBuyer(reqBuyerObj.getBuyer());
		favouriteSupplier.setCommunicationEmail(reqBuyerObj.getSupplier().getCommunicationEmail());
		favouriteSupplier.setCompanyContactNumber(reqBuyerObj.getSupplier().getCompanyContactNumber());
		favouriteSupplier.setFullName(reqBuyerObj.getSupplier().getFullName());
		favouriteSupplier.setDesignation(reqBuyerObj.getSupplier().getDesignation());
		favouriteSupplier.setCreatedBy(loggedInUser);
		favouriteSupplier.setCreatedDate(new Date());
		favouriteSupplier.setStatus(FavouriteSupplierStatus.PENDING);
		favouriteSupplier.setVendorCode(reqBuyerObj.getSupplier().getVendorCode());
		favouriteSupplier = favoriteSupplierService.saveRequestedFavoriteSupplier(favouriteSupplier);

		// added audit trail of favorite supplier
		FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
		try {
			audit.setActionBy(loggedInUser);
			audit.setActionDate(new Date());
			audit.setDescription("Requested");
			audit.setTenantId(reqBuyerObj.getBuyer().getId());
			audit.setFavSupp(reqBuyerObj.getSupplier());
			audit.setRemark(messageSource.getMessage("supplier.audit.supplierequested", new Object[] { reqBuyerObj.getSupplierRemark() }, Global.LOCALE));
			favSuppAuditService.saveFavouriteSupplierAudit(audit);
		} catch (Exception e) {
			LOG.error("Error while updating audit trail of fav supplier:" + e.getMessage(), e);
		}

		SupplierFormSubmition supplierFormSubmition = supplierFormSubmissDao.findOnBoardingFormAvailable(reqBuyerObj.getBuyer().getId(), reqBuyerObj.getSupplier().getId());
		if (supplierFormSubmition != null) {
			boolean secondSubmitForm = false;
			if (supplierFormSubmition.getFavouriteSupplier() != null && supplierFormSubmition.getIsOnboardingForm() == Boolean.TRUE) {
				secondSubmitForm = true;
			}
			supplierFormSubmition.setFavouriteSupplier(favouriteSupplier);
			buildApprovalList(supplierFormSubmition);
			supplierFormSubmissDao.update(supplierFormSubmition);
			if (supplierFormSubmition != null && Boolean.TRUE == supplierFormSubmition.getIsOnboardingForm()) {
				if (!secondSubmitForm) {
					SupplierForm supplierFormObj = supplierFormSubmition.getSupplierForm();
					supplierFormObj.setSubmittedCount(supplierFormObj.getSubmittedCount() != null && supplierFormObj.getSubmittedCount() != 0 ? supplierFormObj.getSubmittedCount() + 1 : 1);
					supplierFormDao.update(supplierFormObj);
				}
			}
			try {
				sendEmailNotificationToSupplierFormOwner(supplierFormSubmition, loggedInUser);

			} catch (Exception e) {
				LOG.error("Error while sending form submitted mail to form owner " + supplierFormSubmition.getRequestedBy().getName() + " :  " + e.getMessage(), e);
			}

		}
	}

	private void sendEmailNotificationToSupplierFormOwner(SupplierFormSubmition supplierFormSubmition, User loggedInUser) {
		LOG.info("Sending Email form submitted to form owner:" + supplierFormSubmition.getRequestedBy().getName());
		try {
			String buyerTimeZone = "GMT+8:00";
			buyerTimeZone = getTimeZoneByBuyerSettings(supplierFormSubmition.getRequestedBy().getTenantId(), buyerTimeZone);
			TimeZone timeZone = TimeZone.getDefault();
			if (StringUtils.checkString(buyerTimeZone).length() > 0) {
				timeZone = TimeZone.getTimeZone(buyerTimeZone);
			}
			String subject = "Supplier Form Submitted";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("buyerName", supplierFormSubmition.getRequestedBy().getName());
			map.put("supplierName", supplierFormSubmition.getSupplier().getCompanyName());
			map.put("formName", supplierFormSubmition.getName());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(timeZone);
			map.put("date", df.format(new Date()));
			map.put("requestedDate", df.format(supplierFormSubmition.getRequestedDate()));
			map.put("submittedDate", df.format(supplierFormSubmition.getSubmitedDate()));
			map.put("actionBy", loggedInUser.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", APP_URL + "/login");

			if (StringUtils.checkString(supplierFormSubmition.getRequestedBy().getCommunicationEmail()).length() > 0 && supplierFormSubmition.getRequestedBy().getEmailNotifications()) {
				sendEmail(supplierFormSubmition.getRequestedBy().getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_SUBMITTED_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + supplierFormSubmition.getRequestedBy().getName() + "... Not going to send email notification");
			}
		} catch (Exception e) {
			LOG.error("Error while Sending revised form email to supplier :" + e.getMessage(), e);
		}

	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo);
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.error("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void buildApprovalList(SupplierFormSubmition supplierFormSubmition) {
		if (CollectionUtil.isNotEmpty(supplierFormSubmition.getApprovals())) {
			String buyerTimeZone = "GMT+8:00";
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
			for (SupplierFormSubmitionApproval formSubmitionApprovalObj : supplierFormSubmition.getApprovals()) {
				if (formSubmitionApprovalObj.getLevel() == 1) {
					formSubmitionApprovalObj.setActive(true);
				} else {
					formSubmitionApprovalObj.setActive(false);
				}
				formSubmitionApprovalObj.setDone(false);
				if (CollectionUtil.isNotEmpty(formSubmitionApprovalObj.getApprovalUsers())) {
					for (SupplierFormSubmitionApprovalUser formAppUser : formSubmitionApprovalObj.getApprovalUsers()) {
						formAppUser.setApprovalStatus(ApprovalStatus.PENDING);
						formAppUser.setActionDate(null);
						formAppUser.setRemarks(null);
						if (formSubmitionApprovalObj.getLevel() == 1) {
							buyerTimeZone = getTimeZoneByBuyerSettings(formAppUser.getUser().getTenantId(), buyerTimeZone);
							approvalService.sendEmailToSupplierFormApprovers(supplierFormSubmition, formAppUser, buyerTimeZone);

						}
					}
				}
			}
		} else {
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.APPROVED);
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
	public void sendEmailToAssociatedBuyer(Supplier supplier, String supplierRemark, Buyer buyer, TimeZone timeZone) {
		approvalService.sendEmailToAssociatedBuyer(supplier, supplierRemark, buyer, timeZone);
	}

	@Override
	public RequestedAssociatedBuyerPojo getRequestedAssociatedBuyerById(String buyerId, String supplierId) {
		return supplierAssociatedBuyerDao.getRequestedAssociatedBuyerById(buyerId, supplierId);
	}

	@Override
	public RequestedAssociatedBuyerPojo getPublishedBuyerDetailsById(String buyerId) {
		return buyerDao.getPublishedBuyerDetailsById(buyerId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoryForTenant(String buyerId) {
		return industryCategoryDao.getIndustryCategoryForTenant(buyerId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoriesById(String requestId) {
		return supplierAssociatedBuyerDao.getIndustryCategoriesById(requestId);
	}

	@Override
	public void sendEmailToAssociatedSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone, boolean approveRejectFlag, RequestedAssociatedBuyer associatedBuyer, String buyerRemark) {
		approvalService.sendEmailToAssociatedSupplier(supplier, buyer, timeZone, approveRejectFlag, associatedBuyer, buyerRemark);

	}

	@Override
	@Transactional(readOnly = false)
	public RequestedAssociatedBuyer rejectSupplierRequest(RequestedAssociatedBuyer associatedBuyer, RequestedAssociatedBuyerPojo data, User loggedInUser) {
		associatedBuyer = supplierAssociatedBuyerDao.findById(associatedBuyer.getId());
		// Update the request sent by supplier
		associatedBuyer.setStatus(RequestAssociateBuyerStatus.REJECTED);
		associatedBuyer.setRejectedDate(new Date());
		associatedBuyer.setBuyerRemark(data.getBuyerRemark());
		// Update industry categories
		String[] IndCatIds = data.getIndCat();
		List<String> IndCatIdList = Arrays.asList(IndCatIds);
		if (IndCatIdList.size() > 0) {
			List<IndustryCategory> finalIndustryList = industryCategoryService.getAllIndustryCategoryOnlyByIds(IndCatIdList);
			associatedBuyer.setIndustryCategory(finalIndustryList);
		}
		associatedBuyer = supplierAssociatedBuyerDao.saveOrUpdate(associatedBuyer);

		// Update to fav supplier list
		FavouriteSupplier dbSupplier = favoriteSupplierService.findFavSupplierBySuppId(associatedBuyer.getSupplier().getId(), associatedBuyer.getBuyer().getId());
		if (dbSupplier != null) {
			dbSupplier.setSupplier(associatedBuyer.getSupplier());
			dbSupplier.setBuyer(associatedBuyer.getBuyer());
			dbSupplier.setCommunicationEmail(associatedBuyer.getSupplier().getCommunicationEmail());
			dbSupplier.setCompanyContactNumber(associatedBuyer.getSupplier().getCompanyContactNumber());
			dbSupplier.setFullName(associatedBuyer.getSupplier().getFullName());
			dbSupplier.setDesignation(associatedBuyer.getSupplier().getDesignation());
			dbSupplier.setCreatedBy(loggedInUser);
			dbSupplier.setStatus(FavouriteSupplierStatus.REJECTED);
			dbSupplier.setVendorCode(associatedBuyer.getSupplier().getVendorCode());
			favoriteSupplierService.saveRequestedFavoriteSupplier(dbSupplier);
		}

		try {
			// Update fav supplier audit trail
			FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
			audit.setActionBy(loggedInUser);
			audit.setActionDate(new Date());
			audit.setDescription("Rejected");
			audit.setRemark(messageSource.getMessage("supplier.audit.rejectedrequest", new Object[] { data.getBuyerRemark() }, Global.LOCALE));
			audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			audit.setFavSupp(associatedBuyer.getSupplier());
			favSuppAuditService.saveFavouriteSupplierAudit(audit);
			
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED, "Supplier request from '" +associatedBuyer.getSupplier().getCompanyName()+ "' is Rejected .", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierList);
			buyerAuditTrailDao.save(buyerAuditTrail);
			
		} catch (Exception e) {
			LOG.error("Error in updating audit while rejecting request : " + e.getMessage(), e);
		}

		return associatedBuyer;
	}

	@Override
	@Transactional(readOnly = false)
	public RequestedAssociatedBuyer acceptSupplierRequest(RequestedAssociatedBuyer associatedBuyer, RequestedAssociatedBuyerPojo data, User loggedInUser) {
		// Update the request sent by supplier
		associatedBuyer = supplierAssociatedBuyerDao.findById(associatedBuyer.getId());
		associatedBuyer.setStatus(RequestAssociateBuyerStatus.APPROVED);
		associatedBuyer.setBuyerRemark(data.getBuyerRemark());
		associatedBuyer.setAssociatedDate(new Date());
		// Update industry categories
		String[] IndCatIds = data.getIndCat();
		List<String> IndCatIdList = Arrays.asList(IndCatIds);
		if (IndCatIdList.size() > 0) {
			List<IndustryCategory> finalIndustryList = industryCategoryService.getAllIndustryCategoryOnlyByIds(IndCatIdList);
			associatedBuyer.setIndustryCategory(finalIndustryList);
		}
		associatedBuyer = supplierAssociatedBuyerDao.saveOrUpdate(associatedBuyer);

		// Update to fav supplier list
		FavouriteSupplier dbSupplier = favoriteSupplierService.findFavSupplierBySuppId(associatedBuyer.getSupplier().getId(), associatedBuyer.getBuyer().getId());
		if (dbSupplier != null) {
			dbSupplier.setSupplier(associatedBuyer.getSupplier());
			dbSupplier.setBuyer(associatedBuyer.getBuyer());
			dbSupplier.setCommunicationEmail(associatedBuyer.getSupplier().getCommunicationEmail());
			dbSupplier.setCompanyContactNumber(associatedBuyer.getSupplier().getCompanyContactNumber());
			dbSupplier.setFullName(associatedBuyer.getSupplier().getFullName());
			dbSupplier.setDesignation(associatedBuyer.getSupplier().getDesignation());
			dbSupplier.setCreatedBy(SecurityLibrary.getLoggedInUser());
			dbSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
			dbSupplier.setAssociatedDate(new Date());
			dbSupplier.setVendorCode(associatedBuyer.getSupplier().getVendorCode());
			if (IndCatIdList.size() > 0) {
				List<IndustryCategory> newCategories = industryCategoryService.getAllIndustryCategoryOnlyByIds(IndCatIdList);
				dbSupplier.setIndustryCategory(newCategories);
			}
			favoriteSupplierService.saveRequestedFavoriteSupplier(dbSupplier);
		}
		try {
			// Update fav supplier audit trail
			FavouriteSupplierStatusAudit audit = new FavouriteSupplierStatusAudit();
			audit.setActionBy(loggedInUser);
			audit.setActionDate(new Date());
			audit.setDescription("Approved");
			audit.setRemark(messageSource.getMessage("supplier.audit.approvedrequest", new Object[] { data.getBuyerRemark() }, Global.LOCALE));
			audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			audit.setFavSupp(associatedBuyer.getSupplier());
			favSuppAuditService.saveFavouriteSupplierAudit(audit);
			
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Supplier  '" +associatedBuyer.getSupplier().getCompanyName()+ "' added to Active List .", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierList);
			buyerAuditTrailDao.save(buyerAuditTrail);
			
		} catch (Exception e) {
			LOG.error("Error in updating audit while accepting request : " + e.getMessage(), e);
		}
		return associatedBuyer;
	}
}
