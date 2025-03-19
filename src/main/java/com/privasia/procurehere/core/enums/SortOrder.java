/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
public enum SortOrder {
	ASC("asc"), DESC("desc");

	private String value;

	/**
	 * @param value as type
	 */
	SortOrder(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

	/**
	 * @param value as boat type
	 * @return unitType of {@link UnitType}
	 */
	public static SortOrder fromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(ASC.toString())) {
				return SortOrder.ASC;
			} else if (StringUtils.checkString(value).equals(DESC.toString())) {
				return SortOrder.DESC;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for SortOrder : " + value);
		}
	}

	public static String getValue(SortOrder type) {
		if (type.equals(SortOrder.ASC)) {
			return "asc";
		} else if (type.equals(SortOrder.DESC)) {
			return "desc";
		}
		return null;
	}

}
