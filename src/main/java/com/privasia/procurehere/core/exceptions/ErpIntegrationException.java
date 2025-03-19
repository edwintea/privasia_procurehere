/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author nitin
 */
public class ErpIntegrationException extends Exception {

	private static final long serialVersionUID = 7248897173487424479L;

	public ErpIntegrationException() {
		super();
	}

	public ErpIntegrationException(String message) {
		super(message);
	}

	public ErpIntegrationException(Throwable cause) {
		super(cause);
	}

	public ErpIntegrationException(String message, Throwable cause) {
		super(message, cause);
	}

}
