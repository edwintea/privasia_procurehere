/**
 * 
 */
package com.privasia.procurehere.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;

/**
 * @author Nitin Otageri
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
	
	private static final Logger LOG = LogManager.getLogger(JwtAuthenticationProvider.class);

	private final JwtSettings jwtSettings;

	@Autowired
	public JwtAuthenticationProvider(JwtSettings jwtSettings) {
		this.jwtSettings = jwtSettings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

		
		Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());
		String subject = jwsClaims.getBody().getSubject();
		List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
		String tenantId = jwsClaims.getBody().get("tenantId", String.class);
		TenantType tenantType = TenantType.valueOf(jwsClaims.getBody().get("tenantType", String.class));
		String userId = jwsClaims.getBody().get("id", String.class);
		UserType userType =  UserType.valueOf(jwsClaims.getBody().get("userType", String.class));
		
		LOG.info("ID : " + userId);
		LOG.info("Tenant ID : " + tenantId);
		LOG.info("Tenant Type : " + tenantType);
		LOG.info("user Type : " + userType);
		
		User user = new User();
		user.setLoginId(subject);
		AuthenticatedUser context = new AuthenticatedUser(user);
		context.setTenantId(tenantId);
		context.setTenantType(tenantType);
		context.setGrantedAuthorities(scopes);
		context.setUserType(userType);
		context.setId(userId);

		return new JwtAuthenticationToken(context, context.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
