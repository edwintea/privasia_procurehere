/**
 * 
 */
package com.privasia.procurehere.web.security;

import org.springframework.stereotype.Component;

/**
 * @author Nitin Otageri
 */
@Component
public class BloomFilterTokenVerifier implements TokenVerifier {

	@Override
	public boolean verify(String jti) {
		return true;
	}
}
