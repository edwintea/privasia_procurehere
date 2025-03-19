/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Arc
 */
public class SecurityRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 7248897173487424479L;

	public SecurityRuntimeException() {
		super();
	}

	public SecurityRuntimeException(String message) {
		super(message);
	}

	public SecurityRuntimeException(Throwable cause) {
		super(cause);
	}

	public SecurityRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
