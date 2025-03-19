/**
 * 
 */
package com.privasia.procurehere.web.security;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.utils.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Nitin Otageri
 */
@Component
public class JwtTokenFactory {
	private final JwtSettings settings;

	private String timeZone = "GMT+8:00";

	@Autowired
	public JwtTokenFactory(JwtSettings settings) {
		this.settings = settings;
	}

	/**
	 * Factory method for issuing new JWT Tokens.
	 * 
	 * @return
	 */
	public AccessJwtToken createAccessJwtToken(AuthenticatedUser userContext, String timeZone) {
		this.timeZone = timeZone;
		return createAccessJwtToken(userContext);
	}

	/**
	 * Factory method for issuing new JWT Tokens.
	 * 
	 * @return
	 */
	public AccessJwtToken createAccessJwtToken(AuthenticatedUser userContext) {
		if (StringUtils.isBlank(userContext.getUsername()))
			throw new IllegalArgumentException("Cannot create JWT Token without username");

		if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
			throw new IllegalArgumentException("User doesn't have any privileges");

		Claims claims = Jwts.claims().setSubject(userContext.getUsername());
		claims.put("scopes", userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
		claims.put("id", userContext.getId());
		claims.put("tenantId", userContext.getTenantId());
		claims.put("tenantType", userContext.getTenantType());
		claims.put("userName", userContext.getName());
		claims.put("companyName", userContext.getCompanyName());
		claims.put("userType", userContext.getUserType());
		claims.put("timeZone", this.timeZone);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		if (userContext.getLastLoginTime() != null) {
			df.setTimeZone(TimeZone.getTimeZone(this.timeZone));
			claims.put("lastLogin", df.format(userContext.getLastLoginTime()));
		}
		DateTime currentTime = new DateTime();

		String token = Jwts.builder().setClaims(claims).setIssuer(settings.getTokenIssuer()).setIssuedAt(currentTime.toDate()).setExpiration(currentTime.plusMinutes(settings.getTokenExpirationTime()).toDate()).signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey()).compact();

		return new AccessJwtToken(token, claims);
	}

	public JwtToken createRefreshToken(AuthenticatedUser userContext) {
		if (StringUtils.isBlank(userContext.getUsername())) {
			throw new IllegalArgumentException("Cannot create JWT Token without username");
		}

		DateTime currentTime = new DateTime();

		Claims claims = Jwts.claims().setSubject(userContext.getUsername());
		claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));
		claims.put("id", userContext.getId());
		claims.put("tenantId", userContext.getTenantId());
		claims.put("tenantType", userContext.getTenantType());
		claims.put("userName", userContext.getUsername());
		claims.put("companyName", userContext.getCompanyName());
		claims.put("userType", userContext.getUserType());
		claims.put("timeZone", this.timeZone);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		if (userContext.getLastLoginTime() != null) {
			df.setTimeZone(TimeZone.getTimeZone(this.timeZone));
			claims.put("lastLogin", df.format(userContext.getLastLoginTime()));
		}

		String token = Jwts.builder().setClaims(claims).setIssuer(settings.getTokenIssuer()).setId(UUID.randomUUID().toString()).setIssuedAt(currentTime.toDate()).setExpiration(currentTime.plusMinutes(settings.getRefreshTokenExpTime()).toDate()).signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey()).compact();

		return new AccessJwtToken(token, claims);
	}
}
