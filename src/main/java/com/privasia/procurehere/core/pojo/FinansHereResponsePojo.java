/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author nitin
 */
public class FinansHereResponsePojo implements Serializable {

	private static final long serialVersionUID = 6051700850938861595L;

	private String message;

	private String status;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}