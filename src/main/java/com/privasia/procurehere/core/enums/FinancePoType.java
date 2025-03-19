/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author YOGESH
 */
public enum FinancePoType {

	SHARED("Shared"), REQUESTED("Requested");

	private String value;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	private FinancePoType(String value) {
		this.value = value;
	}

	public static FinancePoType fromString(String value) {

		try {
			for (FinancePoType status : FinancePoType.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Finance Po Type : " + value);
		}
	}

}
