/**
 * 
 */
package com.privasia.procurehere.web.security;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;

/**
 * @author Nitin Otageri
 */
@Component("ajaxAwareAuthenticationSuccessHandler")
public class AjaxAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger LOG = LogManager.getLogger(AjaxAwareAuthenticationSuccessHandler.class);

	private final ObjectMapper mapper;
	private final JwtTokenFactory tokenFactory;

	@Autowired
	UserDao userDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	public AjaxAwareAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory) {
		this.mapper = mapper;
		this.tokenFactory = tokenFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	@Transactional(readOnly = false)
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		AuthenticatedUser userContext = (AuthenticatedUser) authentication.getPrincipal();

		String deviceId = (String) request.getAttribute("deviceId");
		LOG.info("Authentication Successful for Device Id : " + deviceId);

		// Update Last Login
		String forwardedProto = request.getHeader("X-FORWARDED-PROTO");
		LOG.info("PROXY PROTO : " + forwardedProto);
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		} else {
			LOG.info("Application server is behind a Apache mod proxy....");
		}
		LOG.info("Mobile User login from Remote IP : " + ipAddress);
		User dbUser = userDao.findById(userContext.getId());
		if (dbUser != null) {
			dbUser.setLastLoginTime(new Date());
			dbUser.setFailedAttempts(0);
			dbUser.setLocked(false);
			dbUser.setLoginIpAddress(ipAddress);
			dbUser.setDeviceId(deviceId);
			userDao.update(dbUser);
		}
		String timeZone = getTimeZone(dbUser);

		JwtToken accessToken = tokenFactory.createAccessJwtToken(userContext, timeZone);
		JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);

		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("token", accessToken.getToken());
		tokenMap.put("refreshToken", refreshToken.getToken());

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(response.getWriter(), tokenMap);

		clearAuthenticationAttributes(request);
	}

	private String getTimeZone(User user) {
		String timeZone = "GMT+8:00";
		if (user.getBuyer() != null) {
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZone = settings.getTimeZone().getTimeZone();
			} else {
				timeZone = "GMT+8:00";
			}
		} else if (user.getSupplier() != null) {
			SupplierSettings settings = supplierSettingsDao.getSupplierSettingsByTenantId(user.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZone = settings.getTimeZone().getTimeZone();
			} else {
				timeZone = "GMT+8:00";
			}
		} else if (user.getOwner() != null) {
			OwnerSettings settings = ownerSettingsDao.getOwnersettingsByTenantId(user.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZone = settings.getTimeZone().getTimeZone();
			} else {
				timeZone = "GMT+8:00";
			}
		} else {
			timeZone = "GMT+8:00";
		}
		return timeZone;

	}

	/**
	 * Removes temporary authentication-related data which may have been stored in the session during the authentication
	 * process..
	 */
	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}

		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
}
