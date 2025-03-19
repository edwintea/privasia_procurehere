/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author ravi
 */
public class EmailException extends Exception {

	private static final long serialVersionUID = -1914103586287431471L;

	public EmailException() {
		super();
	}

	public EmailException(String message) {
		super(message);
	}

	public EmailException(Throwable cause) {
		super(cause);
	}

	public EmailException(String message, Throwable cause) {
		super(message, cause);
	}

}
