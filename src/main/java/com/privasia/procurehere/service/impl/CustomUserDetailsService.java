/**
 * 
 */
package com.privasia.procurehere.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.PasswordSettingService;

/**
 * <p>
 * This class is used by Spring Security during user authentication.
 * </p>
 * 
 * @author Nitin Otageri
 */
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LogManager.getLogger(CustomUserDetailsService.class);

	@Autowired
	UserDao userDao;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	PasswordSettingService passwordSettingsService;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang .String)
	 */
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		LOG.info(">>>> Getting user details for user : " + username);

		if (StringUtils.checkString(username).length() == 0)
			throw new UsernameNotFoundException("Invalid username of password specified. Bad credentials.");

		AuthenticatedUser authenticatedUser = null;
		User user = userDao.findByUser(username.toUpperCase());

		if (user == null)
			throw new UsernameNotFoundException("Invalid username of password specified. Bad credentials.");
		user = userDao.findById(user.getId());
		LOG.info(">> Found user...");
		if (user != null && user.getBuyer() != null) {
			// ErpSetup erpConfig = erpSetupDao.getErpConfigBytenantId(user.getBuyer().getId());
			// if (erpConfig != null) {
			if (user.getBuyer().getErpEnable() != null) {
				user.setIsBuyerErpEnable(user.getBuyer().getErpEnable());
			}
			if (user.getBuyer().getRegistrationOfCountry() != null) {
				user.getBuyer().getRegistrationOfCountry().getCountryCode();
			}
		}

		PasswordSettings settings = passwordSettingsService.getPasswordSettingsByTenantId(user.getTenantId());
		if (settings != null) {
			user.setPasswordExpiryInDays(settings.getPasswordExpiryInDays());
		}
		if (user != null) {
			if (user.getSupplier() != null) {
				if (SupplierStatus.CLOSED == user.getSupplier().getStatus()) {
					throw new UsernameNotFoundException("Invalid username of password specified. Bad credentials.");
				}
				if (user.getSupplier().getRegistrationOfCountry() != null) {
					user.getSupplier().getRegistrationOfCountry().getCountryCode();
				}
			}

			if (TenantType.BUYER == user.getTenantType()) {
				if (user.getBuyer() != null && user.getBuyer().getCurrentSubscription() != null && user.getBuyer().getCurrentSubscription().getPlan() != null) {
					authenticatedUser = new AuthenticatedUser(user, user.getUserRole().getRoleName(), (StringUtils.checkString(user.getBuyer().getCurrentSubscription().getPlan().getPlanName()).equalsIgnoreCase("FREETRIAL") || Boolean.TRUE == user.getShowWizardTutorial()));
				} else {
					authenticatedUser = new AuthenticatedUser(user, user.getUserRole().getRoleName());
				}
			} else {
				// if(user.getBuyer() != null && user.getBuyer().getCurrentSubscription() != null &&
				// user.getBuyer().getCurrentSubscription().getPlan() != null) {
				// authenticatedUser = new AuthenticatedUser(user,
				// StringUtils.checkString(user.getBuyer().getCurrentSubscription().getPlan().getPlanName()).equalsIgnoreCase("FREETRIAL"));
				// } else {
				authenticatedUser = new AuthenticatedUser(user, user.getUserRole().getRoleName());
				// }
			}
		}
		return authenticatedUser;
	}

}