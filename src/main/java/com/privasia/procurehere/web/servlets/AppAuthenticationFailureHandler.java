/**
 * 
 */
package com.privasia.procurehere.web.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.PasswordSettingService;

/**
 * @author Nitin Otageri
 */
@Component("appAuthenticationFailureHandler")
public class AppAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static final Logger log = LogManager.getLogger(AppAuthenticationFailureHandler.class);

	@Autowired
	UserDao userDao;

	@Autowired
	PasswordSettingService passwordSettingService;

	@Autowired
	private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.web.authentication.AuthenticationFailureHandler#
	 * onAuthenticationFailure(javax. servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		HttpSession session = request.getSession(false);

		if (session != null) {
			request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
		}

		// Get the authenticated principle
		// String loginId = (String) exception.getAuthentication().getPrincipal();

		String usernameParameter = usernamePasswordAuthenticationFilter.getUsernameParameter();
		String loginId = request.getParameter(usernameParameter);

		log.warn("AUTH FAILURE HANDLER for user : " + loginId + " - " + exception.getMessage());

		// Fetch the user instance from db and update its last unsuccessful login time
		// and increment the failed attempt
		// counter.

		// User user = userDao.getUserByLoginIdNoTouch(loginId);
		if (StringUtils.checkString(loginId).length() > 0) {
			User user = userDao.getUserWithTenantCompaniesByLoginId(loginId);

			if (user != null && !"GUEST".equalsIgnoreCase(loginId)) {
				if (user.getOwner() != null) {
					updateLogInFailureAttempts(user, request);
				} else if (user.getFinanceCompany() != null
						&& Boolean.TRUE == user.getFinanceCompany().getRegistrationComplete()) {
					log.info("__________finance_______________");
					updateLogInFailureAttempts(user, request);
				} else if (user.getBuyer() != null && Boolean.TRUE == user.getBuyer().getRegistrationComplete()) {
					log.info("__________Buyer_______________");
					updateLogInFailureAttempts(user, request);
				} else if (user.getSupplier() != null && Boolean.TRUE == user.getSupplier().getRegistrationComplete()) {
					log.info("__________Supplier_______________");
					updateLogInFailureAttempts(user, request);
				} else {
					log.info("Skipping failed attempts account lock logic for user : " + loginId
							+ " due to incomplete registration");
				}
				// response.sendRedirect();
			} else {
				log.info("Skipping failed attempts account lock logic for user : " + loginId);
			}
		}
		new DefaultRedirectStrategy().sendRedirect(request, response, "/login?error=true");
	}

	private void updateLogInFailureAttempts(User user, HttpServletRequest request) {
		int failed = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
		// Update failed attempt into DB
		log.info("_________________________" + failed
				+ "_______________________________________________________________________________________________________");
		failed++;
		// If too many failed then dont go on incrementing the failed count. Keep it at
		// a certain small number.
		failed = (failed % (Global.LOGIN_FAILURE_ATTEMPTS + 2));
		user.setLastFailedLoginTime(new Date());
		user.setFailedAttempts(failed);
		log.debug("Bad credentials for user : " + user + " Failed login attempt count : " + failed);
		if (failed <= (Global.LOGIN_FAILURE_ATTEMPTS - 1)) { // Up to Second Last attempt
			// request.getSession().setAttribute("failedMessage", "You have " +
			// (Global.LOGIN_FAILURE_ATTEMPTS - failed)
			// + " invalid login attempt left before your account gets locked.");
			// } else if (failed == (Global.LOGIN_FAILURE_ATTEMPTS - 1)) { // Last attempt
			// request.getSession().setAttribute("failedMessage", "This will be your final
			// invalid login attempt. Your
			// account will be locked after this.");
		} else if (failed == Global.LOGIN_FAILURE_ATTEMPTS && !user.isLocked()) {
			request.getSession().setAttribute("failedMessage",
					"Your account has been locked, please contact administrator.");
		} else {
			request.getSession().setAttribute("failedMessage", "");
		}

		// Set the status to Locked if max attempts have reached. Do this only after
		// adding the session messages as
		// it will lead to double message being shown to the user.
		int failedAttempt = 0;
		PasswordSettings setting = passwordSettingService.getPasswordSettingsByTenantId(user.getTenantId());
		if (setting != null) {
			failedAttempt = setting.getFailedAttempts() != null ? setting.getFailedAttempts()
					: Global.LOGIN_FAILURE_ATTEMPTS;
		}
		if (failed >= (failedAttempt != 0 ? failedAttempt : Global.LOGIN_FAILURE_ATTEMPTS)) {
			user.setLocked(true);
		}

		userDao.update(user);
	}

}
