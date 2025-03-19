/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Nitin Otageri
 */
public class RecaptchaServiceException extends Exception {

	private static final long serialVersionUID = 1911454220034782159L;

	public RecaptchaServiceException() {
		super();
	}

	public RecaptchaServiceException(String message) {
		super(message);
	}

	public RecaptchaServiceException(Throwable cause) {
		super(cause);
	}

	public RecaptchaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
