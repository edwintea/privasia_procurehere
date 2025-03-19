/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Arc
 */
public class ApplicationException extends Exception {

	private static final long serialVersionUID = 7248897173487424479L;

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

}
