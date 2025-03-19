/**
 * 
 */
package com.privasia.procurehere.core.enums;

import org.jfree.util.UnitType;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
public enum FooterType {

	DELIVERY_ORDER("Delivery Order"), INVOICE("Invoice");

	String value;

	FooterType(String value) {
		this.value = value;
	}

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

	/**
	 * @param value as boat type
	 * @return unitType of {@link UnitType}
	 */
	public static FooterType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(DELIVERY_ORDER.toString())) {
				return FooterType.DELIVERY_ORDER;
			} else if (StringUtils.checkString(value).equals(INVOICE.toString())) {
				return FooterType.INVOICE;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Footer Type : " + value);
		}
	}

	public static String getValue(FooterType type) {

		switch (type) {
		case DELIVERY_ORDER:
			return DELIVERY_ORDER.value;
		case INVOICE:
			return INVOICE.value;
		default:
			return null;
		}
	}

}
