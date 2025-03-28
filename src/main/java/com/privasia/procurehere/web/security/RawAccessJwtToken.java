/**
 * 
 */
package com.privasia.procurehere.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;

import com.privasia.procurehere.core.exceptions.JwtExpiredTokenException;

/**
 * @author Nitin Otageri
 */
public class RawAccessJwtToken implements JwtToken {

	private static Logger logger = LogManager.getLogger(RawAccessJwtToken.class);

	private String token;

	public RawAccessJwtToken(String token) {
		this.token = token;
	}

	/**
	 * Parses and validates JWT Token signature.
	 * 
	 * @throws BadCredentialsException
	 * @throws JwtExpiredTokenException
	 */
	public Jws<Claims> parseClaims(String signingKey) throws JwtExpiredTokenException, BadCredentialsException {
		try {
			return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
			logger.error("Invalid JWT Token", ex);
			throw new BadCredentialsException("Invalid JWT token: ", ex);
		} catch (ExpiredJwtException expiredEx) {
			logger.error("JWT Token is expired", expiredEx);
			throw new JwtExpiredTokenException(this, "JWT Token expired", expiredEx);
		}
	}

	@Override
	public String getToken() {
		return token;
	}

}
