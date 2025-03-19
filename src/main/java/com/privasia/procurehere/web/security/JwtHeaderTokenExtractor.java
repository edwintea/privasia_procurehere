/**
 * 
 */
package com.privasia.procurehere.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Component
public class JwtHeaderTokenExtractor implements TokenExtractor {

	private static final Logger LOG = LogManager.getLogger(JwtHeaderTokenExtractor.class);
	
	public static String HEADER_PREFIX = "Bearer ";

	@Override
	public String extract(String header) throws AuthenticationServiceException {
		try {
			if (StringUtils.isBlank(header)) {
				LOG.error("Authorization header is blank!");
				throw new AuthenticationServiceException("Authorization header cannot be blank!");
			}

			if (header.length() < HEADER_PREFIX.length()) {
				LOG.error("Invalid authorization header size. : " + header);
				throw new AuthenticationServiceException("Invalid authorization header size.");
			}
		} catch (AuthenticationServiceException e) {
			throw e;
		}

		return header.substring(HEADER_PREFIX.length(), header.length());
	}

}
