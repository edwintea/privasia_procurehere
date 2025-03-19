package com.privasia.procurehere.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.PasswordSettingDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.PasswordSettingService;

@Service
@Transactional(readOnly = true)
public class PasswordSettingServiceImpl implements PasswordSettingService {

	@Autowired
	PasswordSettingDao passwordSettingDao;
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public PasswordSettings getPasswordSettingsByTenantId(String tenantId) {
		PasswordSettings passwordSettings = passwordSettingDao.getPasswordSettingsByTenantId(tenantId);
		LOG.info(passwordSettings);
		if (passwordSettings == null) {
			try {
				PasswordSettings defaultSettings = new PasswordSettings();
				defaultSettings.setMessage("Password should contain at least one digit, one small case, one capital case letter and a special character.");
				defaultSettings.setCreatedDate(new Date());
				defaultSettings.setContainOneLowerCaseLetters(Boolean.TRUE);
				defaultSettings.setContainOneUpperCaseLetters(Boolean.TRUE);
				defaultSettings.setContainNonAlphanumeric(Boolean.TRUE);
				defaultSettings.setEnableExpiration(Boolean.TRUE);
				defaultSettings.setContainOneNumbers(Boolean.TRUE);
				defaultSettings.setPasswordExpiryInDays(90);
				defaultSettings.setEnableReusePassword(Boolean.TRUE);
				defaultSettings.setNumberOfPasswordRemember(3);
				defaultSettings.setEnableFailedAttempts(Boolean.TRUE);
				defaultSettings.setPasswordLength(8);
				defaultSettings.setFailedAttempts(6);
				defaultSettings.setTenantId(tenantId);
				passwordSettings = passwordSettingDao.saveOrUpdate(defaultSettings);
			} catch (Exception e) {
				LOG.error("error while seting default setting:" + e.getMessage(), e);
			}
		}

		return passwordSettings;
	}

	@Override
	@Transactional(readOnly = false)
	public PasswordSettings saveOrUpdate(PasswordSettings defaultSettings) {
		return passwordSettingDao.saveOrUpdate(defaultSettings);
	}

	@Override
	@Transactional(readOnly = false)
	public PasswordSettings getPasswordRegex(String tenantId) {
		LOG.info(" tenantId " + tenantId);
		return buildPasswordRegex(getPasswordSettingsByTenantId(tenantId));
	}

	private PasswordSettings buildPasswordRegex(PasswordSettings passwordSetting) {
		if (passwordSetting != null) {
			String message = "";
			String regx = "^";
			boolean flag = false;
			if (passwordSetting.getContainOneNumbers()) {
				flag = true;
				if (flag) {
					message += " Password should contain at least  one digit,";
				}
				regx += "(?=.*\\d)";
				LOG.info("passwordSetting.getContainOneNumbers()");
			}

			if (passwordSetting.getContainOneLowerCaseLetters()) {
				if (flag) {
					message += " one small case letter, ";
				} else {
					message += " Password should contain at least  one small case letter,";
				}
				flag = true;
				regx += "(?=.*[a-z])";
				LOG.info(" passwordSetting.getContainOneLowerCaseLetters()");
			}
			if (passwordSetting.getContainOneUpperCaseLetters()) {
				if (flag) {
					message += " one capital case letter, ";
				} else {
					message += " Password should contain at least  one  capital case letter,";
				}
				flag = true;
				regx += "(?=.*[A-Z])";
				LOG.info("passwordSetting.getContainOneUpperCaseLetters()");
			}

			if (passwordSetting.getContainNonAlphanumeric()) {
				if (flag) {
					message += " one special character,";
				} else {
					message += " Password should contain at least  one special character,";

				}
				flag = true;
				regx += "(?=.*[$@$!%*?&])";
				LOG.info(passwordSetting.getContainNonAlphanumeric());
			}
			if (flag) {
				message += " password should contain minimum " + passwordSetting.getPasswordLength() + " character";
			} else {
				message = "Password should contain minimum " + passwordSetting.getPasswordLength() + " character";
			}

			passwordSetting.setMessage(message);
			passwordSetting.setRegx(regx + "[A-Za-z\\d$@$!%*?&]" + "{" + passwordSetting.getPasswordLength() + ",64}$");
		} else {
			LOG.info("ELSE");
			passwordSetting = new PasswordSettings();
			passwordSetting.setMessage("Password should contain at least one digit, one small case, one capital case letter and a special character.");
			passwordSetting.setRegx("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$");
		}
		return passwordSetting;
	}

	@Override
	public String buildPasswordRegexMessage(String tenantId) {
		PasswordSettings passwordSetting = getPasswordSettingsByTenantId(tenantId);
		boolean flag = false;
		if (passwordSetting != null) {
			String message = "";
			if (passwordSetting.getContainOneNumbers()) {
				flag = true;
				if (flag) {
					message += " Password should contain at least  one digit,";
				}
			}
			if (passwordSetting.getContainOneLowerCaseLetters()) {
				if (flag) {
					message += "one small case letter, ";
				} else {
					message += " Password should contain at least  one small case letter,";
				}
				flag = true;
			}
			if (passwordSetting.getContainOneUpperCaseLetters()) {
				if (flag) {
					message += "one capital case letter, ";
				} else {
					message += " Password should contain at least  one  capital case letter,";
				}
				flag = true;
			}

			if (passwordSetting.getContainNonAlphanumeric()) {
				if (flag) {
					message += "one special character";
				} else {
					message += " Password should contain at least  one special character,";

				}
				flag = true;

			}
			if (flag) {
				message += " and password should contain minimum " + passwordSetting.getPasswordLength() + " character";
			} else {
				message = "password should contain minimum " + passwordSetting.getPasswordLength() + " character";
			}
			LOG.info(message);
			return message;
		} else {
			return "Password should contain at least one digit, one small case, one capital case letter and a special character.";
		}
	}

}
