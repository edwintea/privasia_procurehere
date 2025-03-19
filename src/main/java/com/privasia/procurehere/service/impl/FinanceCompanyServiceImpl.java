package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.FinanceCompanyDao;
import com.privasia.procurehere.core.dao.FinanceSettingsDao;
import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.SecurityToken.TokenValidity;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.FinanceCompanyService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.UserService;

@Service
@Transactional(readOnly = true)
public class FinanceCompanyServiceImpl implements FinanceCompanyService {

	@Autowired
	NotificationService notificationService;

	@Resource
	MessageSource messageSource;

	@Autowired
	private Environment env;

	@Autowired
	FinanceCompanyDao financeCompanyDao;
	@Autowired
	UserService userService;
	@Autowired
	UserRoleService userRoleService;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	FinanceSettingsDao financeSetting;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Autowired
	VelocityEngine velocityEngine;
	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Override
	public FinanceCompany getFinanceCompanyById(String id) {
		return financeCompanyDao.getFinanceCompanyById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public User saveFinanceCompany(FinanceCompany financeCompany, String loginUserId) {
		financeCompany.setStatus(FinanceCompanyStatus.PENDING);
		financeCompany.setCreatedDate(new Date());
		financeCompany = financeCompanyDao.save(financeCompany);

		User user = null;
		Date createdDate = new Date();
		try {
			//Create User Role

			UserRole userRole = new UserRole();

			userRole.setRoleName("Administrator".toUpperCase());
			userRole.setRoleDescription("Application Administrator");
			userRole.setCreatedDate(createdDate);
			userRole.setTenantId(financeCompany.getId());

			User createdBy = null;
			try {
				createdBy = SecurityLibrary.getLoggedInUser();
			} catch (SecurityRuntimeException e) {
				LOG.error("Error creating : " + e.getMessage(), e);
			}
			if (createdBy == null) {
				if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
					createdBy = userService.getUserByLoginId("admin@procurehere.com");
				}else {
					createdBy = userService.getUserByLoginId("admin@smebank.com");
				}
			}
			userRole.setCreatedBy(createdBy);
			userRoleService.saveUserRole(userRole, accessRightsDao.getAccessControlListForFinanceComapany(false));

			user = new User();
			user.setLoginId(financeCompany.getLoginEmail().toUpperCase());
			user.setCommunicationEmail(financeCompany.getCommunicationEmail());
			user.setTenantId(financeCompany.getId());
			user.setTenantType(TenantType.FINANCE_COMPANY);
			user.setName(financeCompany.getFullName());
			user.setFinanceCompany(financeCompany);
			user.setPassword("dummy_password_that_will_not_work"); // Password will be set by the finance upon clicking on
																	// the email link.
			user.setCreatedDate(createdDate);
			user.setLastPasswordChangedDate(createdDate);
			user.setCreatedBy(createdBy);

			user.setUserRole(userRole);
			user = userService.saveUser(user);

			user.getFinanceCompany().getCompanyName();

			try {
				LOG.info(" creating default Finance settings : ...................................");
				financeSetting.createDefaultFinanceSettings(financeCompany, createdDate, createdBy);
			} catch (Exception e) {
				LOG.error("Error creating default Finance settings : " + e.getMessage(), e);
			}

			try {
				LOG.info("creating default roles...................... ");
				createFinanceDefaultRoles(financeCompany.getId(), createdBy);
			} catch (Exception e) {
				LOG.error("Error while creating default roles for [ " + financeCompany.getCompanyName() + " ] " + e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Error creating Admin Account for Finance and sending email notification : " + e.getMessage(), e);
		}
		return user;

	}

	@Override
	@Transactional(readOnly = false)
	public FinanceCompany updateFinanceCompany(FinanceCompany financeCompany) {
		return financeCompanyDao.saveOrUpdate(financeCompany);
	}

	@Override
	public boolean isExists(FinanceCompany financeCompany) {
		return financeCompanyDao.isExists(financeCompany);
	}

	@Override
	public boolean isExistsLoginEmail(String loginEmail) {
		return financeCompanyDao.isExistsLoginEmail(loginEmail);
	}

	@Override
	public boolean isExistsRegistrationNumber(FinanceCompany financeCompany) {
		return financeCompanyDao.isExistsRegistrationNumber(financeCompany);
	}

	@Override
	public boolean isExistsCompanyName(FinanceCompany financeCompany) {
		return financeCompanyDao.isExistsCompanyName(financeCompany);
	}

	@Override
	@Transactional(readOnly = false)
	public void sentFinanaceCompanyCreationMail(FinanceCompany financecompany, User user) throws ApplicationException {

		try {
			LOG.info("User : " + user.getId());
			SecurityToken securityToken = securityTokenDao.generateTokenWithValidityForUser(TokenValidity.TWELVE_HOUR, user);
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

			String appPath = messageSource.getMessage("app.url", null, Global.LOCALE);

			Map<String, String> data = new HashMap<String, String>();
			data.put("token", securityToken.getToken());
			data.put("email", user.getLoginId());

			String[] urlData = EncryptionUtils.encryptObject(data);

			Map<String, Object> model = new HashMap<String, Object>();

			model.put("fullName", financecompany.getFullName());
			model.put("date", format.format(new Date()));
			model.put("appLink", StringUtils.checkString(appPath) + "/finance/financeCompanyTermsOfUse?d=" + urlData[0] + "&v=" + urlData[1]);
			notificationService.sendEmail(financecompany.getCommunicationEmail(), "Finance Company registration request for PROCUREHERE", model, Global.FINANCECOMPANY_INVIATION_TEMPLATE);

			LOG.info("FINANCE ACCOUNT CREATION EMAIL SENT FOR FINANCE [ " + financecompany.getCompanyName() + "] ");

		} catch (Exception e) {
			LOG.error("Error occured while sending email notification on Finance creation : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

	}

	@Override
	public List<FinanceCompany> searchFinanceCompany(String status, String order, String globalSearch) {
		return financeCompanyDao.searchFinanceCompany(status, order, globalSearch);
	}

	private void createFinanceDefaultRoles(String tenantId, User createdBy) {

		UserRole userRole = new UserRole();

		userRole.setRoleName("User Admin".toUpperCase());
		userRole.setRoleDescription("User Admin");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForFinance(Global.FINANCE_DEFAULT_USER_ADMIN_ACL_VALUES));
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole,createdBy);

		userRole = new UserRole();
		userRole.setRoleName("Finance User".toUpperCase());
		userRole.setRoleDescription("Finance User");
		userRole.setAccessControlList(accessRightsDao.findCustomeAccessForFinance(Global.FINANCE_DEFAULT_USER_ACL_VALUES));
		userRole.setTenantId(tenantId);
		userRole.setCreatedBy(createdBy);
		userRole.setCreatedDate(new Date());
		userRoleService.saveRole(userRole,createdBy);

	}

}
