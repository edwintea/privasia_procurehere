/**
 * 
 */
package com.privasia.procurehere.web.servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmitionDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.FinanceSettingsService;

/**
 * @author Nitin Otageri
 */
@Component("appAuthenticationSuccessHandler")
public class AppAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static Logger LOG = LogManager.getLogger(AppAuthenticationSuccessHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Autowired
	UserDao userDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	FinanceSettingsService financeSettingsService;

	@Autowired
	private LocaleResolver localeResolver;

	@Autowired
	SupplierFormSubmitionDao supplierFormSubmitionDao;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.web.authentication. AuthenticationSuccessHandler
	 * #onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		// UPDATE THE LAST LOGIN TIME
		if (userDao != null) {
			AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

			String forwardedProto = request.getHeader("X-FORWARDED-PROTO");
			LOG.info("PROXY PROTO : " + forwardedProto);

			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			} else {
				LOG.info("Application server is behind a Apache mod proxy....");
			}
			LOG.info("User login from Remote IP : " + ipAddress);
			User dbUser = userDao.findById(user.getId());
			dbUser.setLastLoginTime(new Date());
			dbUser.setFailedAttempts(0);
			dbUser.setLocked(false);
			dbUser.setLoginIpAddress(ipAddress);
			userDao.update(dbUser);

			// Load the Profile Image
			try {

				if (dbUser.getShowWizardTutorial() != null) {
					request.getSession().setAttribute("showWizardTutorial", dbUser.getShowWizardTutorial());
				} else {
					if (Boolean.TRUE == user.getIsFreeTrial()) {
						request.getSession().setAttribute("showWizardTutorial", true);
					} else {
						request.getSession().setAttribute("showWizardTutorial", false);
					}
				}
				/**unsubscribe Email Notifications**/
				if(dbUser.getEmailNotifications() != null){
					request.getSession().setAttribute("unsubscribeEmailNotifications", dbUser.getEmailNotifications());
				}else{
					if(Boolean.TRUE == user.getIsUnSubscribeEmail()) {
						request.getSession().setAttribute("unsubscribeEmailNotifications", true);
					} else {
						request.getSession().setAttribute("unsubscribeEmailNotifications", false);
					}
				}

				request.getSession().setAttribute(Global.SESSION_IP_ADDRESS_KEY, ipAddress);

				// String pic = (String) request.getSession().getAttribute(Global.SESSION_PROFILE_PICTURE_KEY);
				// if (pic == null) {
				// LOG.info("Setting profile pic into session : " +
				// SecurityLibrary.getLoggedInUserProfilePicture());
				// request.getSession().setAttribute(Global.SESSION_PROFILE_PICTURE_KEY,
				// SecurityLibrary.getLoggedInUserProfilePicture());
				// }
				String timeZoneLocation = "Asia/Singapore";
				String timeZone = (String) request.getSession().getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (timeZone == null) {
					if (user.getBuyer() != null) {
						BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
						if (settings != null && settings.getTimeZone() != null) {
							timeZone = settings.getTimeZone().getTimeZone();
							timeZoneLocation = settings.getTimeZone().getTimeZoneDescription();
						} else {
							timeZone = "GMT+8:00";
						}
					} else if (user.getSupplier() != null) {
						SupplierSettings settings = supplierSettingsDao.getSupplierSettingsByTenantId(user.getTenantId());
						if (settings != null && settings.getTimeZone() != null) {
							timeZone = settings.getTimeZone().getTimeZone();
							timeZoneLocation = settings.getTimeZone().getTimeZoneDescription();
						} else {
							timeZone = "GMT+8:00";
						}
					} else if (user.getOwner() != null) {
						OwnerSettings settings = ownerSettingsDao.getOwnersettingsByTenantId(user.getTenantId());
						if (settings != null && settings.getTimeZone() != null) {
							timeZone = settings.getTimeZone().getTimeZone();
							timeZoneLocation = settings.getTimeZone().getTimeZoneDescription();
						} else {
							timeZone = "GMT+8:00";
						}
					} else if (user.getFinanceCompany() != null) {
						FinanceCompanySettings settings = financeSettingsService.getFinanceSettingsByTenantId(user.getTenantId());
						if (settings != null && settings.getTimeZone() != null) {
							timeZone = settings.getTimeZone().getTimeZone();
							timeZoneLocation = settings.getTimeZone().getTimeZoneDescription();
						} else {
							timeZone = "GMT+8:00";
						}
					} else {
						timeZone = "GMT+8:00";
					}
					request.getSession().setAttribute(Global.SESSION_TIME_ZONE_KEY, timeZone);
					request.getSession().setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, timeZoneLocation);

					try {
						Locale locale = null;
						if (user == null || (user != null && StringUtils.checkString(user.getLanguageCode()).length() == 0)) {
							locale = Locale.forLanguageTag("en");
						} else {
							LOG.info("LangCode " + user.getLanguageCode());
							locale = Locale.forLanguageTag(user.getLanguageCode());
						}
						localeResolver.setLocale(request, response, locale);

					} catch (Exception e) {

					}

				}
			} catch (Exception e) {
				LOG.error("Error fetching profile image: " + e.getMessage(), e);
			}
		}

		handle(request, response, authentication);
		clearAuthenticationAttributes(request);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
		// UrlTool.addParamToURL(requestUrl, WebConstants.CSRF_TOKEN, ctoken,
		// true);
		LOG.info("Auth success request : " + (savedRequest == null ? null : savedRequest.getRedirectUrl()));
		String targetUrl = determineTargetUrl(authentication, (savedRequest == null ? null : savedRequest.getRedirectUrl()));

		if (response.isCommitted()) {
			LOG.warn("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		LOG.info("Redirecting user to : " + targetUrl);
		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	/**
	 * Builds the target URL according to the logic defined in the main class Javadoc.
	 */
	protected String determineTargetUrl(Authentication authentication, String originalRequestUrl) {
		LOG.info("Checking user type to redirect to respective dashboards...");
		try {
			boolean isAdmin = false;
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			for (GrantedAuthority grantedAuthority : authorities) {
				// LOG.info("Granted Access : " +
				// grantedAuthority.getAuthority());
				if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
				} else if (grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN")) {
					isAdmin = true;
				}
			}

			if (isAdmin) {
				LOG.info("User type is Super Admin!!!!");
			}

			AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

			LOG.info("User is : " + user.getLoginId());
			if (user.getSupplier() != null) {
				LOG.info("User type is SUPPLIER....");
				if (user.getSupplier().getTermsOfUseAccepted() == Boolean.FALSE) {
					LOG.info("Supplier did not accept terms of Use. Taking him to terms of use page.....");
					return "/supplier/supplierTermsOfUse";
				} else if (Boolean.FALSE == user.getSupplier().getRegistrationComplete()) {
					LOG.info("Supplier registration is still not complete. Taking him to registration completion page.....");
					return "/supplierProfile";
				} else if (Boolean.TRUE == user.getSupplier().getRegistrationComplete() && Boolean.TRUE == user.getSupplier().getIsOnboardingForm() &&  user.getSupplier().getOnBoardingFromsubmitedDate() == null) {
					LOG.info("ON BOARDIN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.>>> ");
					SupplierFormSubmition supplierFormSubmition = supplierFormSubmitionDao.findSupplierFormBySupplierId(user.getSupplier().getId());
					if (supplierFormSubmition != null && Boolean.TRUE == supplierFormSubmition.getIsOnboardingForm()) {
						LOG.info("Supplier did not accept supplier form. Taking him to supplier onboarding form .....");
						return "/supplier/onBoardSupplierFormSubmission/" + supplierFormSubmition.getId();
					} else {
						return originalRequestUrl != null ? originalRequestUrl : "/supplier/supplierDashboard";
					}
				} else {
					return originalRequestUrl != null ? originalRequestUrl : "/supplier/supplierDashboard";
				}
			} else if (user.getBuyer() != null) {
				LOG.info("User type is BUYER.....");
				if (Boolean.FALSE == user.getBuyer().getTermsOfUseAccepted()) {
					LOG.info("Buyer did not accept terms of Use. Taking him to terms of use page.....");
					return "/buyer/buyerTermsOfUse";
				} else if (Boolean.FALSE == user.getBuyer().getRegistrationComplete()) {
					LOG.info("Buyer profile setup is still not complete. Taking him to setup completion page.....");
					return "/buyer/buyerProfileSetup";
				} else {
					return originalRequestUrl != null ? originalRequestUrl : "/buyer/buyerDashboard";
				}
			} else if (isAdmin || user.getOwner() != null) {
				LOG.info("User type is OWNER.....");
				return originalRequestUrl != null ? originalRequestUrl : "/owner/ownerDashboard";
			} else if (user.getFinanceCompany() != null) {
				LOG.info("User type is Finance.....");
				return originalRequestUrl != null ? originalRequestUrl : "/finance/financeDashboard";
			} else {
				throw new IllegalStateException();
			}
		} catch (Exception e) {
			LOG.error("Error while determining the target URL after login : " + e.getMessage(), e);
			throw new IllegalStateException();
		}
	}

	// protected void clearAuthenticationAttributes(HttpServletRequest request)
	// {
	// HttpSession session = request.getSession(false);
	// if (session == null) {
	// return;
	// }
	// session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	// }

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	/**
	 * @return the userDao
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao the userDao to set
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
