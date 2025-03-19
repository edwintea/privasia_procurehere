/**
 * 
 */
package com.privasia.procurehere.core.enums;

/**
 * @author Ravi
 */

public enum UserActivity {

	ADD("ADD"), UPDATE("UPDATE"), DELETE("DELETE");

	private String value;

	/**
	 * @param value as type
	 */
	UserActivity(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

}
