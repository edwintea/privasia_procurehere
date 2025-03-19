/**
 * 
 */
package com.privasia.procurehere.web.security;

/**
 * @author Nitin Otageri
 */
public enum Scopes {
	REFRESH_TOKEN;

	public String authority() {
		return "ROLE_" + this.name();
	}
}
