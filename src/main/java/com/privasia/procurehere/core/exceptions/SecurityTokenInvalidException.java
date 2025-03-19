/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Nitin Otageri
 *
 */
public class SecurityTokenInvalidException extends Exception {

	private static final long serialVersionUID = -3612862492530027289L;

	/**
	 * 
	 */
	public SecurityTokenInvalidException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SecurityTokenInvalidException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SecurityTokenInvalidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SecurityTokenInvalidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SecurityTokenInvalidException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
