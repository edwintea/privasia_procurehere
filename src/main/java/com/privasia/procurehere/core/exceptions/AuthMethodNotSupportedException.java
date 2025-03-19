/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * @author Nitin Otageri
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 557708785078488223L;

	public AuthMethodNotSupportedException(String msg) {
		super(msg);
	}
}
