/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.FinanceSettingsDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.dao.PasswordHistoryDao;
import com.privasia.procurehere.core.dao.PrTemplateDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.dao.SourcingTemplateDao;
import com.privasia.procurehere.core.dao.SupplierAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpEventDocument;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftEventDocument;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.SecurityToken.TokenValidity;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.SupplierActivationIntegrationPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.pojo.UserRevocationIntegrationPojo;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.FinanceCompanyService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.PurgeDataService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RfqDocumentService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	private static Logger LOG = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;
	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	UserDao userDao;

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	PasswordHistoryDao passwordHistoryDao;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Resource
	MessageSource messageSource;

	@Autowired
	VelocityEngine velocityEngine;

	@Autowired
	RfxTemplateDao rfxTemplateDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	PrTemplateDao prTemplateDao;

	@Autowired
	SupplierDao supplierDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	ServletContext context;

	@Autowired
	UomService uomService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	PrService prService;

	@Autowired
	PurgeDataService purgeDataService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	@Autowired
	FinanceSettingsDao financeSettingsDao;

	@Autowired
	FinanceCompanyService financeCompanyService;

	@Autowired
	PasswordSettingService passwordSettingsService;
	
	@Autowired
	SourcingTemplateDao sourcingTemplateDao;

	@Autowired
	SupplierPerformanceTemplateDao supplierPerformanceTemplateDao;

	@Autowired
	BusinessUnitDao businessUnitDao;

	/*
	 * NEW
	 * @AUTHOR KAPIL
	 */
	@Transactional(readOnly = false)
	@Override
	public String saveUsers(User user, String generatedPassword) throws ApplicationException {
		try {
			user = userDao.saveOrUpdate(user);

			// Send user creation email
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", user.getName());
			map.put("loginId", user.getLoginId());
			map.put("password", generatedPassword);
			map.put("appUrl", APP_URL + "/login");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			try {

				switch (user.getTenantType()) {
				case BUYER: {
					BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
					if (settings != null && settings.getTimeZone() != null && (settings.getTimeZone().getTimeZone()).length() > 0) {
						timeZone = settings.getTimeZone().getTimeZone();
					}
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + user.getName() + "' User created", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
					buyerAuditTrailDao.save(buyerAuditTrail);
					break;
				}
				case OWNER: {
					OwnerSettings settings = ownerSettingsDao.getOwnersettings();
					if (settings != null && settings.getTimeZone() != null && (settings.getTimeZone().getTimeZone()).length() > 0) {
						timeZone = settings.getTimeZone().getTimeZone();
					}
					OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + user.getName() + "' User created", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
					ownerAuditTrailDao.save(OwnerAuditTrail);
					break;
				}
				case SUPPLIER: {
					SupplierSettings settings = supplierSettingsDao.getSupplierSettingsByTenantId(user.getTenantId());
					if (settings != null && settings.getTimeZone() != null && (settings.getTimeZone().getTimeZone()).length() > 0) {
						timeZone = settings.getTimeZone().getTimeZone();
					}
					SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.CREATE, "'" + user.getName() + "' User created", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
					supplierAuditTrailDao.save(supplierAuditTrail);
					break;
				}

				case FINANCE_COMPANY: {
					FinanceCompanySettings settings = financeSettingsDao.getFinanceSettingsByTenantId(user.getTenantId());
					if (settings != null && settings.getTimeZone() != null && (settings.getTimeZone().getTimeZone()).length() > 0) {
						timeZone = settings.getTimeZone().getTimeZone();
					}
					break;
				}

				default:
					break;
				}

			} catch (Exception e) {
				LOG.error("Error fetching timezone for tennant : " + e.getMessage());
			}
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), "Your Procurehere account is created", map, Global.USER_CREATION_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("ERROR while User storing .. " + e.getMessage(), e);
			throw new ApplicationException(e);
		}
		return (user != null ? user.getId() : null);
	}

	@Transactional(readOnly = false)
	@Override
	public User updateUserPlain(User user) {
		return userDao.update(user);
	}

	@Transactional(readOnly = false)
	@Override
	public void updateUsers(User user) throws ApplicationException {
		try {
			if (user.getBuyer() != null && user.getBuyer().getLoginEmail().equalsIgnoreCase(user.getLoginId())) {
				Buyer buyer = user.getBuyer();
				buyer.setCommunicationEmail(user.getCommunicationEmail().toUpperCase());
				buyerDao.update(buyer);
			}
			if (user.getSupplier() != null && user.getSupplier().getLoginEmail().equalsIgnoreCase(user.getLoginId())) {
				LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				Supplier supplier = user.getSupplier();
				supplier.setCommunicationEmail(user.getCommunicationEmail().toUpperCase());
				supplierDao.update(supplier);
			}

			if (user.getFinanceCompany() != null && user.getFinanceCompany().getLoginEmail().equalsIgnoreCase(user.getLoginId())) {
				LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				FinanceCompany fc = user.getFinanceCompany();
				fc.setCommunicationEmail(user.getCommunicationEmail().toUpperCase());
				financeCompanyService.updateFinanceCompany(fc);
			}
		} catch (Exception e) {
			LOG.error("ERROR while updating communication email at Buyer/Supplier Profile... " + e.getMessage(), e);
		}

		userDao.update(user);

		try {
			switch (user.getTenantType()) {
			case BUYER:
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' User updated", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
				buyerAuditTrailDao.save(buyerAuditTrail);
				break;

			case OWNER:
				OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' User updated", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
				ownerAuditTrailDao.save(OwnerAuditTrail);
				break;

			case SUPPLIER:
				SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' User updated", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
				supplierAuditTrailDao.save(supplierAuditTrail);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("ERROR while updating user audit.. " + e.getMessage(), e);
		}

		// Update active user Count
		// if (user.getTenantType() == TenantType.BUYER) {
		// activeUserCount =
		// userDao.getActiveUserCountForTenant(user.getTenantId());
		// Buyer buyer = user.getBuyer();
		// buyer.setNoOfUsers(activeUserCount);
		// buyerDao.update(buyer);
		// }

	}

	@Transactional(readOnly = false)
	@Override
	public void deleteUsers(User user) throws ApplicationException {
		// int activeUserCount =
		// userDao.getActiveUserCountForTenant(user.getTenantId());
		// LOG.info("Users " + user.getLoginId());
		// if (user.getTenantType() == TenantType.BUYER) {
		// Buyer buyer = user.getBuyer();
		//
		// if (buyer.getCurrentSubscription() != null &&
		// buyer.getCurrentSubscription().getUserLimit() != null &&
		// activeUserCount == buyer.getCurrentSubscription().getUserLimit()) {
		// throw new ApplicationException("Active User Accounts limit of " +
		// activeUserCount + " Users has reached.");
		// }
		// buyer.setNoOfUsers(activeUserCount);
		// buyerDao.update(buyer);
		// }
		// Update active user Count
		// if (user.getTenantType() == TenantType.BUYER) {
		// activeUserCount =
		// userDao.getActiveUserCountForTenant(user.getTenantId());
		// Buyer buyer = user.getBuyer();
		// buyer.setNoOfUsers(activeUserCount);
		// buyerDao.update(buyer);
		// }
		String userName = user.getName();
		user.setDeleted(Boolean.TRUE);
		userDao.delete(user);
		try {
			switch (user.getTenantType()) {
			case BUYER:
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + userName + "' User deleted", user.getTenantId(), user, new Date(), ModuleType.User);
				buyerAuditTrailDao.save(buyerAuditTrail);
				break;

			case OWNER:
				OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'" + userName + "' User deleted", user.getTenantId(), user, new Date(), ModuleType.User);
				ownerAuditTrailDao.save(OwnerAuditTrail);
				break;

			case SUPPLIER:
				SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.DELETE, "'" + userName + "' User deleted", user.getTenantId(), user, new Date(), ModuleType.User);
				supplierAuditTrailDao.save(supplierAuditTrail);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("ERROR while deleting user audit.. " + e.getMessage(), e);
		}

	}

	@Override
	public boolean isExists(User user) {
		return userDao.isExist(user);
	}

	@Override
	public List<UserPojo> getAllUserPojo() {
		List<UserPojo> returnList = new ArrayList<UserPojo>();

		List<User> list = userDao.findAllActiveUsers(SecurityLibrary.getLoggedInUserTenantId());

		if (CollectionUtil.isNotEmpty(list)) {
			for (User user : list) {
				if (user.getCreatedBy() != null)
					user.getCreatedBy().getLoginId();
				if (user.getModifiedBy() != null)
					user.getModifiedBy().getLoginId();
				if (user.getUserRole() != null)
					user.getUserRole().getRoleName();

				UserPojo cp = new UserPojo(user);
				returnList.add(cp);
			}
		}

		return returnList;

	}

	@Override
	public User getUsersById(String id) {
		LOG.info("assignedSourcingTemplates 13");
		User user = userDao.findById(id);
		if (user != null) {
			if (user.getUserRole() != null)
				user.getUserRole().getRoleName();
			if (user.getAssignedTemplates() != null)
				user.getAssignedTemplates();
			
			if(user.getAssignedSourcingTemplates()!= null)
				user.getAssignedSourcingTemplates();
			if(user.getAssignedSupplierPerformanceTemplates() != null)
				user.getAssignedSupplierPerformanceTemplates();
		}
		return user;

	}

	/*
	 * END neW
	 */

	@Override
	public User getUserByLoginIdNoTouch(String loginEmail) {
		User user = userDao.getUserByLoginIdNoTouch(loginEmail);
		if (user != null) {
			switch (user.getTenantType()) {
			case BUYER:
				user.getBuyer().getLoginEmail();
				break;
			case FINANCE_COMPANY:
				user.getFinanceCompany().getLoginEmail();
				break;
			case OWNER:
				user.getOwner().getLoginEmail();
				break;
			case SUPPLIER:
				user.getSupplier().getLoginEmail();
				break;
			default:
				break;
			}
		}
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		user.setDeleted(true);
		userDao.update(user);
		/*
		 * EventAudit auditTrail = new EventAudit(SecurityLibrary.getLoggedInUser(), new Date(),
		 * UserActivity.DELETE.toString(), "User " + user.getLoginId() + " Deleted by : " +
		 * SecurityLibrary.getLoggedInUserLoginId()); auditTrailService.save(auditTrail);
		 */

	}

	@Override
	public User findUserById(String userId) {
		return userDao.findById(userId);
	}

	@Override
	public List<User> getUsers() {
		List<User> userList = userDao.findAllActiveUsers(SecurityLibrary.getLoggedInUserTenantId());
		if (userList != null && userList.size() > 0) {
			for (User user : userList) {
				UserRole role = user.getUserRole();
				if (role != null) {
					role.getRoleName();
					User createdBy = user.getCreatedBy();
					if (createdBy != null)
						createdBy.getName();
					User modifiedBy = user.getModifiedBy();
					if (modifiedBy != null)
						modifiedBy.getName();
				}
			}
		}
		return userList;
	}

	@Transactional(readOnly = false)
	public User saveUser(User user) {
		user = userDao.save(user);
		// AuditTrail auditTrail = new
		// AuditTrail(SecurityLibrary.getLoggedInUser(), new Date(),
		// UserActivity.ADD.toString(), "User " + user.getLoginId() + " Created
		// by : " +
		// SecurityLibrary.getLoggedInUserLoginId());
		// auditTrailService.save(auditTrail);
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public User updateUser(User user) {
		try {
			if (user.getBuyer() != null && user.getBuyer().getLoginEmail().equalsIgnoreCase(user.getLoginId())) {
				LOG.info("Buyer communication updating.....");
				Buyer buyer = user.getBuyer();
				buyer.setCommunicationEmail(user.getCommunicationEmail().toUpperCase());
				buyerDao.update(buyer);
				LOG.info("Buyer communication finished updating.....");
				// LOG.info("Buyer comunication email : " +
				// user.getBuyer().getCommunicationEmail());
			}

			if (user.getSupplier() != null && user.getSupplier().getLoginEmail().equalsIgnoreCase(user.getLoginId())) {
				LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				Supplier supplier = user.getSupplier();
				supplier.setCommunicationEmail(user.getCommunicationEmail().toUpperCase());
				supplierDao.update(supplier);
			}
		} catch (Exception e) {
			LOG.error("Stupid logic implemented here... need to change this : " + e.getMessage(), e);
		}
		return userDao.update(user);
	}

	@Transactional(readOnly = false)
	@Override
	public void toggleAdminAccountStatus(User adminUser, String buyerId) {
		Buyer buyer = buyerDao.findById(buyerId);
		User user = getAdminUserForBuyer(buyer);
		if (user.getId().equalsIgnoreCase(adminUser.getId())) {
			LOG.info("Changing Locked Account Status for Admin account for Buyer : " + buyerId + " from : " + user.isLocked() + " to " + (!user.isLocked()) + " - Done By User : " + SecurityLibrary.getLoggedInUserLoginId());
			user.setLocked(!user.isLocked());
			if (!user.isLocked()) {
				user.setActive(true);
			}
			user.setFailedAttempts(0);
			userDao.update(user);
		} else {
			// error - someone hacked...
		}
	}

	@Transactional(readOnly = false)
	@Override
	public void toggleFinanceAdminAccountStatus(User adminUser, String financeid) {
		FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeid);
		User user = getUserByLoginId(financeCompany.getLoginEmail());
		if (user.getId().equalsIgnoreCase(adminUser.getId())) {
			LOG.info("Changing Locked Account Status for Admin account for Finance : " + financeid + " from : " + user.isLocked() + " to " + (!user.isLocked()) + " - Done By User : " + SecurityLibrary.getLoggedInUserLoginId());
			user.setLocked(!user.isLocked());
			user.setFailedAttempts(0);
			userDao.update(user);
		} else {
			LOG.info("Locked Account Status not match");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public User getCurrentLoggedInUser() {
		String id = SecurityLibrary.getLoggedInUser().getId();
		User user = userDao.findById(id);
		if (user != null) {
			UserRole userRole = user.getUserRole();
			if (userRole != null) {
				userRole.getAccessControlList();
			}
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByLoginName(String loginName) {
		return userDao.findByUser(loginName);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByLoginId(String loginName) {
		User user = userDao.findByUser(loginName);
		if (user != null) {
			if (user.getUserRole() != null) {
				user.getUserRole().getId();
			}
		}
		return user;
	}

	@Override
	public void sendPasswordResetEmail(User user) {

		LOG.info("Sending reset email password for User : " + user.getLoginId() + ", Comm Email " + user.getCommunicationEmail());
		SecurityToken securityToken = securityTokenDao.generateTokenWithValidityForUser(TokenValidity.THIRTY_MINUTES, user);
		/**
		 * Sent notification to admin
		 */
		StringBuffer content = new StringBuffer();
		try {
			String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);

			Map<String, String> data = new HashMap<String, String>();
			data.put("token", securityToken.getToken());
			data.put("email", user.getLoginId());

			String[] urlData = EncryptionUtils.encryptObject(data);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("contactPerson", user.getName());
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			format.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			map.put("date", format.format(new Date()));
			map.put("appLink", StringUtils.checkString(appPath) + "/admin/resetPassword?d=" + urlData[0] + "&v=" + urlData[1]);
			// content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
			// "/templates/reset_password.vm", "UTF-8", map));
			// notificationService.sendEmail(user.getCommunicationEmail(), "Reset Password Request for PROCUREHERE",
			// content.toString());
				notificationService.sendEmail(user.getCommunicationEmail(), " Reset Password Request for PROCUREHERE", map, "reset_password.ftl");
		} catch (Exception e) {
			LOG.error("Exception occured while processing template:" + e.getMessage(), e);
		}

	}

	@Override
	public User getDetailsOfLoggedinUser(String loginId) {
		User user = userDao.getDetailsOfLoggedinUser(loginId);
		if (user != null) {
			if (CollectionUtil.isNotEmpty(user.getAssignedPrTemplates())) {
				for (PrTemplate pr : user.getAssignedPrTemplates()) {
					pr.getTemplateName();
				}
			}
		}
		return user;
	}

	@Override
	public List<RfxTemplate> getAllTemplatesOfBuyer(String tenantId) {
		return rfxTemplateDao.findAllActiveTemplatesForTenant(tenantId);
	}

	@Override
	public User getDetailsOfLoggedinBuyerWithTemplates(String tenantId, String id) {
		User user = userDao.getDetailsOfLoggedinBuyerWithTemplates(tenantId, id);
		if (user.getUserRole() != null)
			user.getUserRole().getRoleName();
		if (user != null) {
			if (CollectionUtil.isNotEmpty(user.getAssignedPrTemplates())) {
				for (PrTemplate prTemplate : user.getAssignedPrTemplates()) {
					prTemplate.getTemplateName();
				}
			}
			
			if (CollectionUtil.isNotEmpty(user.getAssignedSourcingTemplates())) {
				LOG.info("assignedSourcingTemplates 15");
				for (SourcingFormTemplate template : user.getAssignedSourcingTemplates()) {
					template.getFormName();
					template.getDescription();
					template.getId();
				}
			}
			
			if (CollectionUtil.isNotEmpty(user.getAssignedSupplierPerformanceTemplates())) {
				for (SupplierPerformanceTemplate template : user.getAssignedSupplierPerformanceTemplates()) {
					template.getId();
					template.getTemplateName();
					template.getTemplateDescription();
				}
			}
		}
		return user;
	}

	@Override
	public List<UserPojo> findUserForTenant(String tenantId, TableDataInput tableParams) {
		// List<User> returnList = new ArrayList<User>();
		//
		// List<User> list = userDao.findUserForTenant(tenantId, tableParams);
		// if (CollectionUtil.isNotEmpty(list)) {
		// for (User user : list) {
		// if (user.getCreatedBy() != null)
		// user.getCreatedBy().getLoginId();
		// if (user.getModifiedBy() != null)
		// user.getModifiedBy().getLoginId();
		// if (user.getUserRole() != null)
		// user.getUserRole().getRoleName();
		//
		// returnList.add(user);
		// }
		// }
		return userDao.findUserForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalFilteredUserForTenant(String tenantId, TableDataInput tableParams) {
		return userDao.findTotalFilteredUserForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalUserForTenant(String tenantId) {
		return userDao.findTotalUserForTenant(tenantId);
	}

	@Override
	public List<User> fetchAllActiveUsersForTenant(String tenantId) {
		return userDao.fetchAllActiveUsersForTenant(tenantId);
	}

	@Override
	public User getAdminUserForBuyer(Buyer buyer) {
		return userDao.getAdminUserForBuyer(buyer);
	}

	@Override
	public User getAdminUserForSupplier(Supplier supplier) {
		User user = userDao.getAdminUserForSupplier(supplier);
		if (user != null && user.getSupplier() != null)
			user.getSupplier().getLoginEmail();
		return user;
	}

	@Override
	public List<PrTemplate> getAllPrTemplatesOfBuyer(String tenantId) {
		return prTemplateDao.findAllActiveTemplatesForTenant(tenantId);
	}

	@Override
	public List<User> fetchAllActiveUsersForEnvelopForTenant(String tenantId) {
		return userDao.fetchAllActiveUsersForEnvelopForTenant(tenantId);
	}

	@Override
	public List<User> getAllAdminUsersForBuyer(String tenantId) {
		return userDao.getAllAdminUsersForBuyer(tenantId);
	}

	@Override
	public List<User> getAllAdminUsersForSupplier(String tenantId) {
		return userDao.getAllAdminUsersForSupplier(tenantId);
	}

	@Override
	public List<User> getAllAdminPlainUsersForSupplier(String tenantId) {
		return userDao.getAllAdminPlainUsersForSupplier(tenantId);
	}

	@Override
	public User findTeamUserById(String userId) {
		return userDao.findUserById(userId);
	}

	@Override
	public List<User> fetchAllUsersForTenant(String tenantId) {
		return userDao.fetchAllUsersForTenant(tenantId);
	}

	@Override
	public long findTotalRegisteredOrActiveUserForTenant(String tenantId, boolean isActiveUser) {
		return userDao.findTotalRegisteredOrActiveUserForTenant(tenantId, isActiveUser);
	}

	@Override
	public User getPlainUserByLoginId(String loginEmail) {
		return userDao.getPlainUserByLoginId(loginEmail);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateUserBqPageLength(Integer pageLength, String userId) {
		userDao.updateUserBqPageLength(pageLength, userId);
	}

	@Override
	public List<User> fetchAllActiveUsersForTenantAndUserType(String tenantId, UserType userType) {
		return userDao.fetchAllActiveUsersForTenantAndUserType(tenantId, userType);
	}

	@Override
	public User findById(String id) {
		return userDao.findById(id);
	}

	@Override
	public List<User> fetchAllActiveNormalUsersForTenant(String tenantId) {
		return userDao.fetchAllActiveNormalUsersForTenant(tenantId);
	}

	@Transactional(readOnly = false)
	@Override
	public void toggleErpStatus(User adminUser, String buyerId) {
		Buyer buyer = buyerDao.findById(buyerId);
		User user = getAdminUserForBuyer(buyer);
		ErpSetup erpConfig = erpSetupDao.getErpConfigBytenantId(buyerId);
		if (buyer != null) {
			if (buyer.getErpEnable() != null) {
				LOG.info("Changing ERP Status for Buyer : " + buyerId + " from : " + buyer.getErpEnable() + " to " + (!buyer.getErpEnable()) + " - Done By User : " + SecurityLibrary.getLoggedInUserLoginId());
				if (buyer.getErpEnable() == Boolean.TRUE) {
					buyer.setErpEnable(Boolean.FALSE);
					if (erpConfig != null) {
						user.setIsBuyerErpEnable(Boolean.FALSE);
						if (erpConfig.getIsErpEnable() == Boolean.TRUE) {
							erpConfig.setIsErpEnable(Boolean.FALSE);
						}
					}
				} else {
					buyer.setErpEnable(Boolean.TRUE);
					if (erpConfig != null) {
						user.setIsBuyerErpEnable(Boolean.FALSE);
					}

				}

			} else {
				buyer.setErpEnable(Boolean.TRUE);
				user.setIsBuyerErpEnable(Boolean.FALSE);
			}

			if (erpConfig != null) {
				LOG.info("Updating ERP .......");
				erpSetupDao.update(erpConfig);
			}
			userDao.update(user);
			buyerDao.update(buyer);
		}

	}

	@Override
	public List<User> fetchAllActiveNormalUsersForEnvelopForTenant(String tenantId) {
		return userDao.fetchAllActiveNormalUsersForEnvelopForTenant(tenantId);
	}

	@Override
	public void userDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "User.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = getExcelWorkBook(loggedInUserTenantId);
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
			LOG.error("Error :- " + e.getMessage(), e);
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
		for (String column : Global.USER_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public String generateUserZip(ZipOutputStream zos, String strTimeZone, User user) {
		String zipFileName = "MasterData".replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
		purgeDataService.deleteBuyerData(user.getTenantId());
		return zipFileName;
	}

	public void writeOnDisk(String strTimeZone, User user) {

		String homeDir = System.getProperty("user.home");

		String masterData = homeDir + Global.PATH_SEPARATOR + "Data" + Global.PATH_SEPARATOR + "MasterData" + Global.PATH_SEPARATOR;

		userExportExcelForEachSupplier(user.getTenantId(), masterData);

	}

	private void userExportExcelForEachSupplier(String tenantId, String masterData) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String fileName = "User.xlsx";
			File file = new File(masterData + "" + fileName);
			file.getParentFile().mkdirs();
			file.createNewFile();

			workbook = getExcelWorkBook(tenantId);
			FileOutputStream out = new FileOutputStream(file, false);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage(), e);
		}

	}

	private void prDataToZip(ZipOutputStream zos, String parentFolder, User user, String strTimeZone, boolean isPo) {
		List<Pr> pList = prService.findBuyerAllPo(user.getTenantId(), isPo);
		for (Pr pr : pList) {
			LOG.info("--id=" + pr.getId() + "----Prid-------" + pr.getPrId());
		}

		LOG.info("------found-------" + pList.size());
		for (Pr pr : pList) {
			try {
				LOG.info("-next-id=" + pr.getId() + "----Prid-------" + pr.getPrId());

				String poFolder = parentFolder + pr.getPrId().replaceAll("/", "_") + Global.PATH_SEPARATOR + (isPo ? "PO" : "PR") + Global.PATH_SEPARATOR;
				String summaryFolder = parentFolder + pr.getPrId().replaceAll("/", "_") + Global.PATH_SEPARATOR + "Summary" + Global.PATH_SEPARATOR;
				FileUtil.writePdfToZip(zos, prService.getPrSummaryPdf(pr, strTimeZone), summaryFolder, pr.getPrId() + ".pdf");
				String attachment = parentFolder + pr.getPrId().replaceAll("/", "_") + Global.PATH_SEPARATOR + "Attachment" + Global.PATH_SEPARATOR;
				List<PrDocument> prDocsList = prService.findAllPrDocsbyPrId(pr.getId());
				for (PrDocument prDocument : prDocsList) {
					FileUtil.writeFileToZip(zos, prDocument.getFileData(), attachment, prDocument.getFileName());
				}

			} catch (IOException | JRException e) {
				LOG.info("error in exporting po data to zip " + e.getMessage(), e);
			}
		}

	}

	@SuppressWarnings("unused")
	private void eventDataToZip(ZipOutputStream zos, String parentFolder, String strTimeZone, User user, EventStatus status) {
		LOG.info("event Data To Zip for tanent id....." + user.getTenantId());
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		try {
			List<DraftEventPojo> eventPojo = rftEventDao.getAllEventsForBuyer(user.getTenantId(), status);
			LOG.info("Found total event :" + eventPojo.size());
			String eventParent = parentFolder + "Event" + Global.PATH_SEPARATOR;

			for (DraftEventPojo event : eventPojo) {
				if (event.getStatus() != EventStatus.DRAFT && event.getStatus() != EventStatus.CANCELED) {
					LOG.info("Zip....." + event.getSysEventId());
					event.setStatus(status);
					eventToZip(event, zos, eventParent, strTimeZone, user, virtualizer);
				} else {
					LOG.info("not ziping ....");
				}

			}
		} catch (Exception e) {
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}

	}

	private void eventToZip(DraftEventPojo event, ZipOutputStream zos, String parentFolder, String strTimeZone, User user, JRSwapFileVirtualizer virtualizer) {
		LOG.info("event to Zip....." + event.getStatus());
		String eventName = event.getEventName();

		String folder = parentFolder + event.getType() + Global.PATH_SEPARATOR + event.getStatus() + Global.PATH_SEPARATOR + event.getSysEventId().replaceAll("/", "_") + Global.PATH_SEPARATOR;
		switch (event.getType()) {
		case RFA:
			LOG.info("RFA to  Zip....." + event.getId());
			exportRfaToZip(zos, event, folder, strTimeZone, eventName, user, virtualizer);
			break;
		case RFI:
			exportRfiToZip(zos, event, folder, strTimeZone, eventName, user, virtualizer);
			break;
		case RFP:
			exportRfpToZip(zos, event, folder, strTimeZone, eventName, user, virtualizer);
			break;
		case RFQ:
			exportRfqToZip(zos, event, folder, strTimeZone, eventName, user, virtualizer);
			break;
		case RFT:
			exportRftToZip(zos, event, folder, strTimeZone, eventName, user, virtualizer);
			break;
		default:
			break;

		}

	}

	private void exportRftToZip(ZipOutputStream zos, DraftEventPojo event, String folder, String strTimeZone, String eventName, User user, JRSwapFileVirtualizer virtualizer) {
		try {
			LOG.info("expotrting event summary pdf............");
			RftEvent rftEvent = new RftEvent();
			rftEvent.setId(event.getId());
			FileUtil.writePdfToZip(zos, rftEventService.getEvaluationSummaryPdf(rftEvent, user, strTimeZone, virtualizer), folder + "Summary" + Global.PATH_SEPARATOR, eventName + ".pdf");
			List<RftEventBq> bqEventList = rftBqService.getAllBqListByEventId(event.getId());
			for (RftEventBq rftEventBq : bqEventList) {
				LOG.info("Exporting BQ to ZIP ..................");
				XSSFWorkbook workbook = rftBqService.eventDownloader(rftEventBq.getId(), RfxTypes.RFT);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "BQ" + Global.PATH_SEPARATOR, rftEventBq.getName() + ".xlsx");
			}
			List<RftCq> cqList = rftCqService.findRftCqForEvent(event.getId());
			for (RftCq rftCq : cqList) {
				LOG.info("Exporting CQ to ZIP ..................");
				XSSFWorkbook workbook = rftCqService.eventcqDownloader(rftCq.getId(), RfxTypes.RFT);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "CQ" + Global.PATH_SEPARATOR, rftCq.getName() + ".xlsx");
			}
			List<RftEventDocument> rftDocsList = rftDocumentService.findAllRfadocsForZipbyEventId(event.getId());
			for (RftEventDocument rftEventDocument : rftDocsList) {

				LOG.info("Exporting Attachment to ZIP ..................");
				FileUtil.writeFileToZip(zos, rftEventDocument.getFileData(), folder + "Attachment" + Global.PATH_SEPARATOR, rftEventDocument.getFileName());
			}

		} catch (Exception e) {
			LOG.error("error in export Rfp to zip" + e.getMessage(), e);
		}

	}

	private void exportRfqToZip(ZipOutputStream zos, DraftEventPojo event, String folder, String strTimeZone, String eventName, User user, JRSwapFileVirtualizer virtualizer) {
		try {
			LOG.info("expotrting event summary pdf............");
			RfqEvent rfqEvent = new RfqEvent();
			rfqEvent.setId(event.getId());
			FileUtil.writePdfToZip(zos, rfqEventService.getEvaluationSummaryPdf(rfqEvent, user, strTimeZone, virtualizer), folder + "Summary" + Global.PATH_SEPARATOR, eventName + ".pdf");
			List<RfqEventBq> bqEventList = rfqBqService.getAllBqListByEventId(event.getId());
			for (RfqEventBq rfqEventBq : bqEventList) {
				LOG.info("Exporting BQ to ZIP ..................");
				XSSFWorkbook workbook = rftBqService.eventDownloader(rfqEventBq.getId(), RfxTypes.RFQ);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "BQ" + Global.PATH_SEPARATOR, rfqEventBq.getName() + ".xlsx");
			}
			List<RfqCq> cqList = rfqCqService.findCqForEvent(event.getId());
			for (RfqCq rfqCq : cqList) {
				LOG.info("Exporting CQ to ZIP ..................");
				XSSFWorkbook workbook = rftCqService.eventcqDownloader(rfqCq.getId(), RfxTypes.RFQ);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "CQ" + Global.PATH_SEPARATOR, rfqCq.getName() + ".xlsx");
			}
			List<RfqEventDocument> rfqDocsList = rfqDocumentService.findAllRfadocsForZipbyEventId(event.getId());
			for (RfqEventDocument rfqEventDocument : rfqDocsList) {

				LOG.info("Exporting Attachment to ZIP ..................");
				FileUtil.writeFileToZip(zos, rfqEventDocument.getFileData(), folder + "Attachment" + Global.PATH_SEPARATOR, rfqEventDocument.getFileName());
			}

		} catch (Exception e) {
			LOG.error("error in export Rfp to zip" + e.getMessage(), e);
		}

	}

	private void exportRfpToZip(ZipOutputStream zos, DraftEventPojo event, String folder, String strTimeZone, String eventName, User user, JRSwapFileVirtualizer virtualizer) {
		try {
			LOG.info("expotrting event summary pdf............");
			LOG.info("expotrting event summary pdf............");
			RfpEvent rfpEvent = new RfpEvent();
			rfpEvent.setId(event.getId());
			FileUtil.writePdfToZip(zos, rfpEventService.getEvaluationSummaryPdf(rfpEvent, user, strTimeZone, virtualizer), folder + "Summary" + Global.PATH_SEPARATOR, eventName + ".pdf");
			List<RfpEventBq> bqEventList = rfpBqService.getAllBqListByEventId(event.getId());
			for (RfpEventBq rfpEventBq : bqEventList) {
				LOG.info("Exporting BQ to ZIP ..................");
				XSSFWorkbook workbook = rftBqService.eventDownloader(rfpEventBq.getId(), RfxTypes.RFP);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "BQ" + Global.PATH_SEPARATOR, rfpEventBq.getName() + ".xlsx");
			}
			List<RfpCq> cqList = rfpCqService.findCqForEvent(event.getId());
			for (RfpCq rfPCq : cqList) {
				LOG.info("Exporting CQ to ZIP ..................");
				XSSFWorkbook workbook = rftCqService.eventcqDownloader(rfPCq.getId(), RfxTypes.RFP);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "CQ" + Global.PATH_SEPARATOR, rfPCq.getName() + ".xlsx");
			}
			List<RfpEventDocument> rfpDocsList = rfpDocumentService.findAllRfadocsForZipbyEventId(event.getId());
			for (RfpEventDocument rfPEventDocument : rfpDocsList) {

				LOG.info("Exporting Attachment to ZIP ..................");
				FileUtil.writeFileToZip(zos, rfPEventDocument.getFileData(), folder + "Attachment" + Global.PATH_SEPARATOR, rfPEventDocument.getFileName());
			}

		} catch (Exception e) {
			LOG.error("error in export Rfp to zip" + e.getMessage(), e);
		}

	}

	private void exportRfiToZip(ZipOutputStream zos, DraftEventPojo event, String folder, String strTimeZone, String eventName, User user, JRSwapFileVirtualizer virtualizer) {

		try {
			LOG.info("expotrting event summary pdf............");
			RfiEvent rfiEvent = new RfiEvent();
			rfiEvent.setId(event.getId());
			FileUtil.writePdfToZip(zos, rfiEventService.getEvaluationSummaryPdf(rfiEvent, user, strTimeZone, virtualizer), folder + "Summary" + Global.PATH_SEPARATOR, eventName + ".pdf");

			List<RfiCq> cqList = rfiCqService.findRfiCqForEvent(event.getId());
			for (RfiCq rfiCq : cqList) {
				LOG.info("Exporting CQ to ZIP ..................");
				XSSFWorkbook workbook = rftCqService.eventcqDownloader(rfiCq.getId(), RfxTypes.RFI);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "CQ" + Global.PATH_SEPARATOR, rfiCq.getName() + ".xlsx");
			}
			List<RfiEventDocument> rfiDocsList = rfiDocumentService.findAllRfidocsForZipbyEventId(event.getId());
			for (RfiEventDocument rfiEventDocument : rfiDocsList) {

				LOG.info("Exporting Attachment to ZIP ..................");
				FileUtil.writeFileToZip(zos, rfiEventDocument.getFileData(), folder + "Attachment" + Global.PATH_SEPARATOR, rfiEventDocument.getFileName());
			}

		} catch (Exception e) {
			LOG.error("error in export Rfi to zip" + e.getMessage(), e);
		}

	}

	private void exportRfaToZip(ZipOutputStream zos, DraftEventPojo event, String folder, String strTimeZone, String eventName, User user, JRSwapFileVirtualizer virtualizer) {
		try {

			FileUtil.writePdfToZip(zos, rfaEventService.getBuyerAuctionReport(event.getId(), strTimeZone, virtualizer), folder + "Report" + Global.PATH_SEPARATOR, eventName + ".pdf");

			LOG.info("expotrting event summary pdf............");
			try {
				RfaEvent rfaEvent = new RfaEvent();
				rfaEvent.setId(event.getId());
				FileUtil.writePdfToZip(zos, rfaEventService.getEvaluationSummaryPdf(rfaEvent, user, strTimeZone, virtualizer), folder + "Summary" + Global.PATH_SEPARATOR, eventName + ".pdf");
			} catch (Exception e) {
			}
			List<RfaEventBq> bqEventList = rfaBqService.getAllBqListByEventId(event.getId());
			for (RfaEventBq rfaEventBq : bqEventList) {
				LOG.info("Exporting BQ to ZIP ..................");
				XSSFWorkbook workbook = rftBqService.eventDownloader(rfaEventBq.getId(), RfxTypes.RFA);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "BQ" + Global.PATH_SEPARATOR, rfaEventBq.getName() + ".xlsx");
			}
			List<RfaCq> cqList = rfaCqService.findRfaCqForEvent(event.getId());
			for (RfaCq rfaCq : cqList) {
				LOG.info("Exporting CQ to ZIP ..................");
				XSSFWorkbook workbook = rftCqService.eventcqDownloader(rfaCq.getId(), RfxTypes.RFA);
				FileUtil.writeXssfExcelToZip(zos, workbook, folder + "CQ" + Global.PATH_SEPARATOR, rfaCq.getName() + ".xlsx");
			}
			List<RfaEventDocument> rftDocsList = rfaDocumentService.findAllRfadocsForZipbyEventId(event.getId());
			for (RfaEventDocument rfaEventDocument : rftDocsList) {

				LOG.info("Exporting Attachment to ZIP ..................");
				FileUtil.writeFileToZip(zos, rfaEventDocument.getFileData(), folder + "Attachment" + Global.PATH_SEPARATOR, rfaEventDocument.getFileName());
			}

		} catch (IOException | JRException e) {
			LOG.error("error in export Rfa to zip" + e.getMessage(), e);
		}

	}

	private XSSFWorkbook getExcelWorkBook(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("User List");
		// Creating Headings
		buildHeader(workbook, sheet);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		List<User> userList = userDao.getAllUserByTenantId(loggedInUserTenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(userList)) {
			for (User user : userList) {
				String rfxTemplateString = "";
				String prTemplateString = "";
				List<RfxTemplate> rfxTemplate = userDao.getAllAssignedRfxTemplateByUserId(user.getId());
				if (CollectionUtil.isNotEmpty(rfxTemplate)) {
					int count = 1;
					for (RfxTemplate rfxTemplate2 : rfxTemplate) {
						rfxTemplateString = rfxTemplateString + (count > 1 ? ", " : "") + rfxTemplate2.getTemplateName();
						count++;
					}
				} else {
					rfxTemplateString = "";
				}

				List<PrTemplate> prTemplate = userDao.getAllAssignedPrTemplateByUserId(user.getId());
				if (CollectionUtil.isNotEmpty(prTemplate)) {
					int count = 1;
					for (PrTemplate prTemplate2 : prTemplate) {
						prTemplateString = prTemplateString + (count > 1 ? ", " : "") + prTemplate2.getTemplateName();
						count++;
					}
				} else {
					prTemplateString = "";
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(user.getLoginId());
				row.createCell(cellNum++).setCellValue(user.getName());
				row.createCell(cellNum++).setCellValue(user.getCommunicationEmail());
				row.createCell(cellNum++).setCellValue(user.getDesignation());
				row.createCell(cellNum++).setCellValue(user.getPhoneNumber());
				row.createCell(cellNum++).setCellValue(user.getUserType().toString());
				row.createCell(cellNum++).setCellValue(user.getUserRole().getRoleName());
				row.createCell(cellNum++).setCellValue(user.isActive() ? "ACTIVE" : "INACTIVE");
				row.createCell(cellNum++).setCellValue(rfxTemplateString);
				row.createCell(cellNum++).setCellValue(prTemplateString);
				row.createCell(cellNum++).setCellValue(user.isLocked() ? "LOCKED" : "UNLOCKED");
				row.createCell(cellNum++).setCellValue(user.getLastLoginTime() != null ? format.format(user.getLastLoginTime()) : "N/A");
				row.createCell(cellNum++).setCellValue(user.getLastFailedLoginTime() != null ? format.format(user.getLastFailedLoginTime()) : "N/A");

			}
		}
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	@Override
	public User getAdminUser() {
		return userDao.getAdminUser();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExistsLoginEmailGlobal(String loginEmail) {
		return userDao.isExistsLoginEmailGlobal(loginEmail);

	}

	@Override
	public User getAdminUserForFinance(FinanceCompany financeCompany) {
		return userDao.getAdminUserForFinance(financeCompany);
	}

	@Override
	public List<User> getPrCreatorUser(String loggedInUserTenantId) {
		return userDao.getPrCreatorUser(loggedInUserTenantId);
	}

	@Override
	public User findUserWithRoleById(String userId) {
		User user = userDao.findById(userId);
		if (user.getUserRole() != null) {
			user.getUserRole().getRoleName();
		}
		switch (user.getTenantType()) {
		case BUYER:
			user.getBuyer().getLoginEmail();
			break;
		case FINANCE_COMPANY:
			user.getFinanceCompany().getLoginEmail();
			break;
		case OWNER:
			user.getOwner().getLoginEmail();
			break;
		case SUPPLIER:
			user.getSupplier().getLoginEmail();
			break;
		default:
			break;
		}
		return user;
	}

	@Override
	public boolean findAvalableAdminUser(String userid, String tenantId) {
		return userDao.getfindAvalableAdminUser(userid, tenantId);
	}

	@Override
	public List<User> fetchAllActiveUserForTenantId(String tenantId) {
		return userDao.fetchAllActiveUserForTenantId(tenantId);
	}

	@Override
	public List<User> getAllAdminPlainUsersForSupplierNotification(String tenantId) {

		return userDao.getAllAdminPlainUsersForSupplierNotification(tenantId);
	}

	@Override
	public User getAdminUserForBuyer(String tenantId) {
		return userDao.getAdminUserForBuyer(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLangCodeForUser(String id, String code) {
		userDao.updateLangCodeForUser(id, code);
	}

	@Override
	@Transactional(readOnly = true)
	public User findUserByLoginId(String loginName) {
		return userDao.findByUser(loginName);
	}

	/**
	 * Only 30 users will fetch in this list last user is not user this is used for search users add approver users
	 */
	@Override
	public List<UserPojo> fetchAllUsersForTenant(String loggedInUserTenantId, String searchValue, UserType userType) {
		List<UserPojo> list = userDao.fetchAllUsersForTenant(loggedInUserTenantId, searchValue, userType);
		long count = userDao.fetchFilterCountAllUsersForTenant(loggedInUserTenantId, "", userType);
		// get count
		if (list != null && count > list.size()) {
			UserPojo more = new UserPojo();
			more.setId("-1");
			more.setName("Total " + (count) + " users. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	/**
	 * Only 30 users will fetch in this list last user is not user this is used for search users add approver users
	 */
	@Override
	public List<UserPojo> fetchAllUsersForPoApproval(String loggedUserId,String loggedInUserTenantId, String searchValue, UserType userType) {
		List<UserPojo> list = userDao.fetchAllUsersForPoApproval(loggedUserId,loggedInUserTenantId, searchValue, userType);
		long count = userDao.fetchFilterCountAllUsersForTenant(loggedUserId, "", userType);
		// get count
		if (list != null && count > list.size()) {
			UserPojo more = new UserPojo();
			more.setId("-1");
			more.setName("Total " + (count) + " users. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	/**
	 * Only for RFX Templates Only 30 users will fetch in this list last user is not user this is used for search users
	 * add approver users
	 */
	@Override
	public List<UserPojo> fetchAllUsersForTenantForRfxTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId) {
		List<UserPojo> list = userDao.fetchUnAssignedUsersForRfxTemplate(loggedInUserTenantId, searchValue, userType, templateId);
		long count = userDao.fetchFilterCountUnAssignedUsersForRfxTemplate(loggedInUserTenantId, "", userType, templateId);
		// get count
		if (list != null && count > list.size()) {
			UserPojo more = new UserPojo();
			more.setId("-1");
			more.setName("Total " + (count) + " users. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	/**
	 * Only for PR Templates Only 30 users will fetch in this list last user is not user this is used for search users
	 * add approver users
	 */
	@Override
	public List<UserPojo> fetchAllUsersForTenantForPrTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId) {
		List<UserPojo> list = userDao.fetchUnAssignedUsersForPrTemplate(loggedInUserTenantId, searchValue, userType, templateId);
		long count = userDao.fetchFilterCountUnAssignedUsersForPrTemplate(loggedInUserTenantId, "", userType, templateId);
		// get count
		if (list != null && count > list.size()) {
			UserPojo more = new UserPojo();
			more.setId("-1");
			more.setName("Total " + (count) + " users. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	/**
	 * Only for PR Templates Only 30 users will fetch in this list last user is not user this is used for search users
	 * add approver users
	 */






	@Override
	public User getUsersForRfxById(String usersId) {
		return userDao.getUsersForRfxById(usersId);
	}

	@Override
	public User getUsersForPrById(String usersId) {
		return userDao.getUsersForPrById(usersId);
	}





	@Override
	public User getUsersNameAndId(String usersId) {
		return userDao.getUsersNameAndId(usersId);
	}

	@Override
	public List<User> getUsersNameAndIdForTemplate(List usersIds) {
		return userDao.getUsersNameAndIdForTemplate(usersIds);
	}

	@Override
	public void downloadUserCsvFile(HttpServletResponse response, File file, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.USER_CSV_COLUMNS;
			String[] columns = new String[] { "loginId", "name", "communicationEmail", "designation", "phoneNumber", "userType", "role", "status", "rfxTemplate", "prTemplate", "isLocked", "lastLoginTimeStr", "lastFailedLoginTimeStr" };

			long count = userDao.findTotalUserForTenant(tenantId);
			LOG.info("Count  ..........................." + count);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processor = getProcessor();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<UserPojo> list = findUserListForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo);
				LOG.info("List ..................... " + list.size());

				for (UserPojo user : list) {

					if (user.getLastLoginTime() != null) {
						user.setLastLoginTimeStr(sdf.format(user.getLastLoginTime()));
					}
					if (user.getLastFailedLoginTime() != null) {
						user.setLastFailedLoginTimeStr(sdf.format(user.getLastFailedLoginTime()));
					}

					beanWriter.write(user, columns, processor);
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

	private List<UserPojo> findUserListForTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		List<UserPojo> pojoList = new ArrayList<>();

		List<User> list = userDao.findUserListForTenantIdForCsv(tenantId, pageSize, pageNo);
		for (User user : list) {
			UserPojo pojo = new UserPojo();
			String rfxTemplateString = "";
			String prTemplateString = "";

			pojo.setLoginId(user.getLoginId());
			pojo.setName(user.getName());
			pojo.setCommunicationEmail(user.getCommunicationEmail());
			pojo.setDesignation(user.getDesignation());
			pojo.setPhoneNumber(user.getPhoneNumber());
			pojo.setUserType(user.getUserType().toString());
			pojo.setRole(user.getUserRole().getRoleName());
			pojo.setStatus(user.isActive() ? "ACTIVE" : "INACTIVE");
			pojo.setIsLocked(user.isLocked() ? "LOCKED" : "UNLOCKED");
			pojo.setLastLoginTime(user.getLastLoginTime());
			pojo.setLastFailedLoginTime(user.getLastFailedLoginTime());

			List<RfxTemplate> rfxTemplate = userDao.getAllAssignedRfxTemplateByUserId(user.getId());
			if (CollectionUtil.isNotEmpty(rfxTemplate)) {
				int count = 1;
				for (RfxTemplate rfxTemplate2 : rfxTemplate) {
					rfxTemplateString = rfxTemplateString + (count > 1 ? ", " : "") + rfxTemplate2.getTemplateName();
					count++;
				}
			} else {
				rfxTemplateString = "";
			}

			List<PrTemplate> prTemplate = userDao.getAllAssignedPrTemplateByUserId(user.getId());
			if (CollectionUtil.isNotEmpty(prTemplate)) {
				int count = 1;
				for (PrTemplate prTemplate2 : prTemplate) {
					prTemplateString = prTemplateString + (count > 1 ? ", " : "") + prTemplate2.getTemplateName();
					count++;
				}
			} else {
				prTemplateString = "";
			}

			pojo.setPrTemplate(prTemplateString);
			pojo.setRfxTemplate(rfxTemplateString);

			pojoList.add(pojo);
		}
		return pojoList;
	}

	private CellProcessor[] getProcessor() {
		CellProcessor[] processor = new CellProcessor[] {

				new NotNull(), new Optional(), new Optional(), new Optional(), new Optional(), new NotNull(), // USER
																												// TYPE
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional() };
		return processor;
	}

	@Override
	public List<UserPojo> fetchAllUsersForTenantForSourcingTemplate(String tenantId, String searchValue, UserType userType, String templateId) {
		List<UserPojo> list = userDao.fetchUnAssignedUsersForSourcingTemplate(tenantId, searchValue, userType, templateId);
		long count = userDao.fetchFilterCountUnAssignedUsersForSourcingTemplate(tenantId, "", userType, templateId);
		// get count
		if (list != null && count > list.size()) {
			UserPojo more = new UserPojo();
			more.setId("-1");
			more.setName("Total " + (count) + " users. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public User getUsersForSourcingFormById(String userID) {
		return userDao.getUsersForSourcingFormById(userID);
	}

	@Override
	public List<SourcingFormTemplate> getAllSourcingTemplatesOfBuyer(String tenantId) {
		return sourcingTemplateDao.getAllSourcingTemplatesOfBuyer(tenantId);
	}

	@Override
	public List<UserPojo> fetchAllUsersForTenantForSPTemplate(String tenantId, String searchValue, UserType userType, String templateId) {
		List<UserPojo> list = userDao.fetchUnAssignedUsersForSPTemplate(tenantId, searchValue, userType, templateId);
		long count = userDao.fetchFilterCountUnAssignedUsersForSPTemplate(tenantId, "", userType, templateId);
		// get count
		if (list != null && count > list.size()) {
			UserPojo more = new UserPojo();
			more.setId("-1");
			more.setName("Total " + (count) + " users. Continue typing to find match...");
			list.add(more);
		}
		return list;
	}

	@Override
	public User getUsersForSupplierPerformanceTemplateById(String userId) {
		return userDao.getUsersForSupplierPerformanceTemplateById(userId);
	}

	@Override
	public List<SupplierPerformanceTemplate> getAllSpTemplatesOfBuyer(String tenantId) {
		return supplierPerformanceTemplateDao.getAllSpTemplatesOfBuyer(tenantId);
	}
	

	@Override
	public List<User> fetchUserByCommunicationEmail(String emailId, String tenantId) {
		return userDao.fetchUserByCommunicationEmail(emailId, tenantId);
	}
	
	@Override
	@Transactional(readOnly = false)
	public List<String> userUpdate(List<User> users, String emailId, String tenantId) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isNotBlank(emailId)) {
//			List<User> users = fetchUserByCommunicationEmail(emailId, tenantId);
			if (CollectionUtil.isNotEmpty(users)) {
				try {
				for (User user : users) {
					LOG.info("User : " + user.toLogString());
					System.out.println("User : " + user.toLogString());
				}
				userDao.revokeUserAccount(emailId, tenantId);
				}catch(Exception e){
					errorList.add("Unable To Update User Record");
					e.printStackTrace();
				}
			}
			else {
				LOG.warn("User Email Id : " + emailId + " is not exist");
				errorList.add("Record Not Found");
			}
		}
		return errorList;
	}

	@Override
	@Transactional
	public List<BusinessUnit> getAllBizUnitOfBuyer(String tenantId) {
		return businessUnitDao.getPlainActiveBusinessUnitForTenant(tenantId);
	}

	@Override
	public User getDetailsOfLoggedinBuyerWithBizUnits(String tenantId, String id) {
		User user = userDao.getDetailsOfLoggedinBuyerWithBizUnits(tenantId, id);
		if (user.getUserRole() != null)
			user.getUserRole().getRoleName();
		if (user != null) {

			if (CollectionUtil.isNotEmpty(user.getAssignedBusinessUnits())) {
				LOG.info("assignedBusinessUnits 15");
				for (BusinessUnit bizUnit : user.getAssignedBusinessUnits()) {
					bizUnit.getUnitName();
					bizUnit.getId();
				}
			}
/*			if (CollectionUtil.isNotEmpty(user.getAssignedPrTemplates())) {
				for (PrTemplate prTemplate : user.getAssignedPrTemplates()) {
					prTemplate.getTemplateName();
				}
			}

			if (CollectionUtil.isNotEmpty(user.getAssignedSourcingTemplates())) {
				LOG.info("assignedSourcingTemplates 15.1");
				for (SourcingFormTemplate template : user.getAssignedSourcingTemplates()) {
					template.getFormName();
					template.getDescription();
					template.getId();
				}
			}

			if (CollectionUtil.isNotEmpty(user.getAssignedSupplierPerformanceTemplates())) {
				for (SupplierPerformanceTemplate template : user.getAssignedSupplierPerformanceTemplates()) {
					template.getId();
					template.getTemplateName();
					template.getTemplateDescription();
				}
			}*/
		}
		return user;
	}
}
