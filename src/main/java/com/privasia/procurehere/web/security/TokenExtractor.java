/**
 * 
 */
package com.privasia.procurehere.web.security;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * @author Nitin Otageri
 */
public interface TokenExtractor {

	/**
	 * @param payload
	 * @return
	 * @throws AuthenticationServiceException
	 */
	String extract(String payload) throws AuthenticationServiceException;
}
