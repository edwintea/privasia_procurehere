/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Nitin Otageri
 *
 */
public class NotAuthorizedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7964507669407235039L;

	
	public NotAuthorizedException() {
		super();
	}

	public NotAuthorizedException(String message) {
		super(message);
	}

	public NotAuthorizedException(Throwable cause) {
		super(cause);
	}

	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

}
