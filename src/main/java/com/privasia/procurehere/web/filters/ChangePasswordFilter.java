/**
 * 
 */
package com.privasia.procurehere.web.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.privasia.procurehere.core.pojo.AuthenticatedUser;

/**
 * @author Nitin Otageri
 */
public class ChangePasswordFilter extends OncePerRequestFilter implements Filter, InitializingBean {

	private static Logger LOG = LogManager.getLogger(ChangePasswordFilter.class);

//	protected final String ERRORS_KEY = "errors";
//	protected String changePasswordKey = "user.must.change.password";

	protected final String ERRORS_KEY = "info";
	protected String changePasswordKey = "Your password has expired. Please change the password";

	private String changePasswordUrl = null;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws ServletException {
		Assert.notNull(changePasswordUrl, "changePasswordUrl must be set.");
		Assert.notNull(changePasswordKey, "changePasswordKey must be set.");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		AuthenticatedUser userDetails = null;
		String requestURL = request.getRequestURL().toString();
//		LOG.info("REQUEST URL : " + requestURL);
//		if (requestURL.endsWith("ChangePassword") || requestURL.endsWith("changePassword")) {
			LOG.debug("changepasswordfilter URL: " + request.getRequestURL());
			try {
				Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				if (obj instanceof AuthenticatedUser) {
					userDetails = (AuthenticatedUser) obj;
				} else {
				}

				if (userDetails != null && userDetails.isPasswordExpired()) {
					// send user to change password page
					LOG.warn("Credentials Expired - sending to changepassword page.");

					int pos = requestURL.toLowerCase().indexOf("changepassword");
					if (pos == -1) {
						saveError(request, changePasswordKey);
						sendRedirect(request, response, changePasswordUrl);
						return;
					}
				}
			} catch (Exception e) {
				LOG.error("Error in Change Password Filter : " + e.getMessage());
			}
//		}
		filterChain.doFilter(request, response);
	}

	/**
	 * The URL to the Change Password page. It must begin with a slash and should be relative from the application's
	 * contextPath root (ex: /changepassword.do).
	 * 
	 * @param changePasswordUrl the changePasswordUrl to set
	 */
	public void setChangePasswordUrl(String changePasswordUrl) {
		this.changePasswordUrl = changePasswordUrl;
	}

	/**
	 * Allow subclasses to modify the redirection message.
	 * 
	 * @param request the request
	 * @param response the response
	 * @param url the URL to redirect to
	 * @throws IOException in the event of any failure
	 */
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = request.getContextPath() + url;
		}

		response.sendRedirect(response.encodeRedirectURL(url));
	}

	@SuppressWarnings("unchecked")
	public void saveError(HttpServletRequest request, String msg) {
		Set<String> errors = (Set<String>) request.getSession().getAttribute(ERRORS_KEY);

		if (errors == null) {
			errors = new HashSet<String>();
		}

		errors.add(msg);
		request.getSession().setAttribute(ERRORS_KEY, errors);
	}

	/**
	 * The message bundle key that will hold the "You must change your password" error message. The default key name is
	 * <b>user.must.change.password</b>.
	 * 
	 * @param changePasswordKey the changePasswordKey to set
	 */
	public void setChangePasswordKey(String changePasswordKey) {
		this.changePasswordKey = changePasswordKey;
	}

}
