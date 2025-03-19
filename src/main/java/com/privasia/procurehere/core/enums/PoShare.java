/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author YOGESH
 */
public enum PoShare {

	NONE("None"), ALL("All"), BUYER("to Buyer");

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

	private PoShare(String value) {
		this.value = value;
	}

	public static PoShare fromString(String value) {

		try {
			for (PoShare status : PoShare.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Po Share : " + value);
		}
	}

}
