/**
 * 
 */
package com.privasia.procurehere.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.exceptions.AuthMethodNotSupportedException;
import com.privasia.procurehere.core.exceptions.ErrorCode;
import com.privasia.procurehere.core.exceptions.ErrorResponse;
import com.privasia.procurehere.core.exceptions.JwtExpiredTokenException;

/**
 * @author Nitin Otageri
 */
@Component("ajaxAwareAuthenticationFailureHandler")
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper mapper;

	@Autowired
	public AjaxAwareAuthenticationFailureHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.security.web.authentication.AuthenticationFailureHandler#onAuthenticationFailure(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		if (e instanceof BadCredentialsException) {
			mapper.writeValue(response.getWriter(), ErrorResponse.of("Invalid username or password", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		} else if (e instanceof JwtExpiredTokenException) {
			mapper.writeValue(response.getWriter(), ErrorResponse.of("Token has expired", ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
		} else if (e instanceof AuthMethodNotSupportedException) {
			mapper.writeValue(response.getWriter(), ErrorResponse.of(e.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		}

		mapper.writeValue(response.getWriter(), ErrorResponse.of("Authentication failed : " + e.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
	}

}
