/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Ravi
 */
public class NotAllowedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1832379144162681158L;

	public NotAllowedException() {
		super();
	}

	public NotAllowedException(String message) {
		super(message);
	}

	public NotAllowedException(Throwable cause) {
		super(cause);
	}

	public NotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}

}
