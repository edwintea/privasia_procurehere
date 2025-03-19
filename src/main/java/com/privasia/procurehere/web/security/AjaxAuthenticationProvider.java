/**
 * 
 */
package com.privasia.procurehere.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOG = LogManager.getLogger(AjaxAuthenticationProvider.class);
	
	BCryptPasswordEncoder encoder;
	UserDao userDao;

	@Autowired
	public AjaxAuthenticationProvider(final UserDao userDao, final BCryptPasswordEncoder encoder) {
		this.userDao = userDao;
		this.encoder = encoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.notNull(authentication, "No authentication data provided");

		LOG.info("Performing Ajax Authentication...");
		
		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		if(StringUtils.checkString(username).length() == 0 || StringUtils.checkString(password).length() == 0) {
			LOG.error("Empty credentials supplied : " + username + "/" + password);
			throw new UsernameNotFoundException("User not found: " + username);
		}
		
		User user = userDao.findByUser(username);

		if(user == null) {
			LOG.error("Invalid credentials provided. User not found....");
			throw new UsernameNotFoundException("User not found: " + username);
		}
		
		if (!encoder.matches(password, user.getPassword())) {
			LOG.error("Authentication Failed. Username or Password not valid....");
			throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
		}

//		if (user.getRoles() == null)
//			throw new InsufficientAuthenticationException("User has no roles assigned");

		AuthenticatedUser userContext = new AuthenticatedUser(user);

		if(userContext.getTenantType() != TenantType.BUYER && userContext.getTenantType() != TenantType.SUPPLIER) {
			LOG.warn("User '" + username + "' tenant type is : " + userContext.getTenantType());
			throw new BadCredentialsException("Only Buyer and Supplier accounts are allowed to use mobile app.");
		} 
		
		return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
