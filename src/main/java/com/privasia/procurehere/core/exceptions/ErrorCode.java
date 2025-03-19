/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Nitin Otageri
 */
public enum ErrorCode {
	GLOBAL(2), AUTHENTICATION(10), JWT_TOKEN_EXPIRED(498);

	private int errorCode;

	private ErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@JsonValue
	public int getErrorCode() {
		return errorCode;
	}
}
