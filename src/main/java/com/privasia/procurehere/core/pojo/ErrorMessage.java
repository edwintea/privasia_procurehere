/**
 * 
 */
package com.privasia.procurehere.core.pojo;

/**
 * @author arc
 */
public class ErrorMessage {

	private String fieldName;
	private String message;

	public ErrorMessage(String fieldName, String message) {
		this.fieldName = fieldName;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		return message;
	}
}
