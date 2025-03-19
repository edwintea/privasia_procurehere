/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nitin Otageri
 *
 */
public class SecurityTokenExpiredException extends Exception {

	private static final long serialVersionUID = 2424561266078142562L;

	private Date expiryDate;

	public SecurityTokenExpiredException(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @param message
	 */
	public SecurityTokenExpiredException(String message, Date expiryDate) {
		super(message);
		this.expiryDate = expiryDate;
	}

	/**
	 * @param cause
	 */
	public SecurityTokenExpiredException(Throwable cause, Date expiryDate) {
		super(cause);
		this.expiryDate = expiryDate;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SecurityTokenExpiredException(String message, Throwable cause, Date expiryDate) {
		super(message, cause);
		this.expiryDate = expiryDate;
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SecurityTokenExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if(expiryDate != null) {
			message += " Token expired at : " + df.format(expiryDate);
		}
		return message;
	}
	
}
