/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Nitin Otageri
 *
 */
public class SecurityTokenException extends Exception {

	private static final long serialVersionUID = -1194781435479370446L;

	/**
	 * 
	 */
	public SecurityTokenException() {
	}

	/**
	 * @param message
	 */
	public SecurityTokenException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SecurityTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SecurityTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SecurityTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
