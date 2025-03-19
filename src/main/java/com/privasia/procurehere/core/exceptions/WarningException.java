/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author ravi
 */
public class WarningException extends Exception {

	private static final long serialVersionUID = 7248897173487424479L;

	public WarningException() {
		super();
	}

	public WarningException(String message) {
		super(message);
	}

	public WarningException(Throwable cause) {
		super(cause);
	}

	public WarningException(String message, Throwable cause) {
		super(message, cause);
	}

}
