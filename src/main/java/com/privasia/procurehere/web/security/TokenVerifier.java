/**
 * 
 */
package com.privasia.procurehere.web.security;

/**
 * @author Nitin Otageri
 */
public interface TokenVerifier {
	public boolean verify(String jti);
}
