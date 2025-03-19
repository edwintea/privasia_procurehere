/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Nitin Otageri
 */
public class SubscriptionException extends Exception {

	private static final long serialVersionUID = 7248897173487424479L;

	public SubscriptionException() {
		super();
	}

	public SubscriptionException(String message) {
		super(message);
	}

	public SubscriptionException(Throwable cause) {
		super(cause);
	}

	public SubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

}
