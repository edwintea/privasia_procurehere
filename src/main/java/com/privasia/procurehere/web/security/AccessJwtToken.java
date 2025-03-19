/**
 * 
 */
package com.privasia.procurehere.web.security;

import io.jsonwebtoken.Claims;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Nitin Otageri
 */
public class AccessJwtToken implements JwtToken {

	private final String rawToken;

	@JsonIgnore
	private Claims claims;

	protected AccessJwtToken(final String token, Claims claims) {
		this.rawToken = token;
		this.claims = claims;
	}

	public String getToken() {
		return this.rawToken;
	}

	public Claims getClaims() {
		return claims;
	}

}
