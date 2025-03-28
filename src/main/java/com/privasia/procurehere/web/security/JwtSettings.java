/**
 * 
 */
package com.privasia.procurehere.web.security;

import org.springframework.context.annotation.Configuration;

/**
 * @author Nitin Otageri
 */
@Configuration
//@ConfigurationProperties(prefix = "demo.security.jwt")
public class JwtSettings {
	/**
	 * {@link JwtToken} will expire after this time.
	 */
	private Integer tokenExpirationTime = 15; // Number of minutes

	/**
	 * Token issuer.
	 */
	private String tokenIssuer = "procurehere.com";

	/**
	 * Key is used to sign {@link JwtToken}.
	 */
	private String tokenSigningKey = "xm8EV6Hy5RMFK4EEACIDAwQus"; // Make this key pom param based

	/**
	 * {@link JwtToken} can be refreshed during this timeframe.
	 */
	private Integer refreshTokenExpTime = 60; // Minutes

	public Integer getRefreshTokenExpTime() {
		return refreshTokenExpTime;
	}

	public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
		this.refreshTokenExpTime = refreshTokenExpTime;
	}

	public Integer getTokenExpirationTime() {
		return tokenExpirationTime;
	}

	public void setTokenExpirationTime(Integer tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
	}

	public String getTokenIssuer() {
		return tokenIssuer;
	}

	public void setTokenIssuer(String tokenIssuer) {
		this.tokenIssuer = tokenIssuer;
	}

	public String getTokenSigningKey() {
		return tokenSigningKey;
	}

	public void setTokenSigningKey(String tokenSigningKey) {
		this.tokenSigningKey = tokenSigningKey;
	}
}
