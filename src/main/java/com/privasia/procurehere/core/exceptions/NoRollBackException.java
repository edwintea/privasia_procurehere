/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * 
 * @author sarang
 *
 * throw exception without roll-back transaction 
 *
 */
public class NoRollBackException extends Throwable {

	private static final long serialVersionUID = 7248897173487424479L;

	public NoRollBackException() {
		super();
	}

	public NoRollBackException(String message) {
		super(message);
	}

	public NoRollBackException(Throwable cause) {
		super(cause);
	}

	public NoRollBackException(String message, Throwable cause) {
		super(message, cause);
	}

}
