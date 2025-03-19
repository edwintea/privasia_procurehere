/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author arc
 */
public class JsonResponse implements Serializable{

	
 	private static final long serialVersionUID = -9120895399892387172L;
	private String statusCode = null;
	private String message = null;
	private Object result = null;

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

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
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

}
