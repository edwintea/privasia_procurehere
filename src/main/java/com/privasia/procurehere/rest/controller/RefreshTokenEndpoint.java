/**
 * 
 */
package com.privasia.procurehere.rest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.privasia.procurehere.core.exceptions.ErrorCode;
import com.privasia.procurehere.core.exceptions.InvalidJwtToken;
import com.privasia.procurehere.core.exceptions.JwtExpiredTokenException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.web.config.SecurityConfig;
import com.privasia.procurehere.web.security.JwtSettings;
import com.privasia.procurehere.web.security.JwtToken;
import com.privasia.procurehere.web.security.JwtTokenFactory;
import com.privasia.procurehere.web.security.RawAccessJwtToken;
import com.privasia.procurehere.web.security.RefreshToken;
import com.privasia.procurehere.web.security.TokenExtractor;
import com.privasia.procurehere.web.security.TokenVerifier;

/**
 * @author Nitin Otageri
 */
@RestController
public class RefreshTokenEndpoint {

	private static Logger LOG = LogManager.getLogger(RefreshTokenEndpoint.class);

	@Autowired
	private JwtTokenFactory tokenFactory;

	@Autowired
	private JwtSettings jwtSettings;

	@Autowired
	private TokenVerifier tokenVerifier;

	@Autowired
	@Qualifier("jwtHeaderTokenExtractor")
	private TokenExtractor tokenExtractor;

	@Autowired
	@Qualifier("customUserDetailsService")
	UserDetailsService userDetailsService;

	@RequestMapping(value = "/api/auth/token", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String tokenPayload;
		try {
			tokenPayload = tokenExtractor.extract(request.getHeader(SecurityConfig.JWT_TOKEN_HEADER_PARAM));
		} catch (AuthenticationServiceException e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw e;
		}

		RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
		RefreshToken refreshToken;
		try {
			refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());
		} catch (JwtExpiredTokenException e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw e;
		} catch (BadCredentialsException e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw e;
		} catch (InvalidJwtToken e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw e;
		}

		String jti = refreshToken.getJti();
		if (!tokenVerifier.verify(jti)) {
			throw new InvalidJwtToken();
		}

		String subject = refreshToken.getSubject();

		AuthenticatedUser userContext = (AuthenticatedUser) userDetailsService.loadUserByUsername(subject);
		//		User user = userService.getUserByLoginId(subject);
		if (userContext == null) {
			throw new UsernameNotFoundException("User not found: " + subject);
		}

		// if (user.getRoles() == null)
		// throw new InsufficientAuthenticationException("User has no roles assigned");
		// List<GrantedAuthority> authorities = user.getRoles().stream().map(authority -> new
		// SimpleGrantedAuthority(authority.getRole().authority())).collect(Collectors.toList());

		// AuthenticatedUser userContext = UserContext.create(user.getUsername(), authorities);
		//		AuthenticatedUser userContext = new AuthenticatedUser(user);

		return tokenFactory.createAccessJwtToken(userContext);
	}

	@ExceptionHandler(AuthenticationServiceException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, String> handleException(AuthenticationServiceException e) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("errorCode", HttpStatus.BAD_REQUEST.toString());
		response.put("errorMessage", "Invalid Request or Token");
		return response;
	}

	/**
	 * @apiDefine TOKEN_BAD_REQUEST
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiErrorExample {json} Error-Response: HTTP/1.1 400 BAD REQUEST { "errorMessage": "Invalid Request or Token",
	 *                  "errorCode": "400" }
	 */
	@ExceptionHandler(InvalidJwtToken.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, String> handleException(InvalidJwtToken e) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("errorCode", HttpStatus.BAD_REQUEST.toString());
		response.put("errorMessage", "Invalid Request or Token");
		return response;
	}

	/**
	 * @apiDefine TOKEN_NOT_ACCEPTABLE
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiErrorExample {json} Error-Response: HTTP/1.1 401 NOT ACCEPTABLE { "errorMessage": "Token is already expired",
	 *                  "errorCode": "498" }
	 */
	@ExceptionHandler(JwtExpiredTokenException.class)
	@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
	public @ResponseBody Map<String, String> handleException(JwtExpiredTokenException e) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("errorCode", ErrorCode.JWT_TOKEN_EXPIRED.toString());
		response.put("errorMessage", "Token is already expired");
		return response;
	}

	/**
	 * @apiDefine TOKEN_UNAUTHORIZED
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiErrorExample {json} Error-Response: HTTP/1.1 401 Unauthorized { "errorMessage": "Access denied. Not
	 *                  authorized.", "errorCode": "401" }
	 */
	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public @ResponseBody Map<String, String> handleException(BadCredentialsException e) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("errorCode", HttpStatus.UNAUTHORIZED.toString());
		response.put("errorMessage", "Access denied. Not authorized.");
		return response;
	}
}
