/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
public enum EnvelopType {
	OPEN("Open"), CLOSED("Closed");

	private String value;

	/**
	 * @param value as type
	 */
	EnvelopType(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

	/**
	 * @param value
	 * @return
	 */
	public static EnvelopType convertFromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(OPEN.toString())) {
				return EnvelopType.OPEN;
			} else if (StringUtils.checkString(value).equals(CLOSED.toString())) {
				return EnvelopType.CLOSED;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for EnvelopType : " + value);
		}
	}

	/**
	 * @param type
	 * @return
	 */
	public static String convertToString(EnvelopType type) {
		switch (type) {
		case OPEN:
			return OPEN.value;
		case CLOSED:
			return CLOSED.value;
		default:
			return null;
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
