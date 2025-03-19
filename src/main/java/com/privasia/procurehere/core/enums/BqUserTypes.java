package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

public enum BqUserTypes {
	BUYER("Buyer"), SUPPLIER("Supplier"), BOTH("Both");
	
	private String value;

	/**
	 * @param value as type
	 */
	BqUserTypes(String value) {
		this.value = value;
	}
	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BqUserTypes fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(BUYER.toString())) {
				return BqUserTypes.BUYER;
			} else if (StringUtils.checkString(value).equals(SUPPLIER.toString())) {
				return BqUserTypes.SUPPLIER;
			} else if (StringUtils.checkString(value).equals(BOTH.toString())) {
				return BqUserTypes.BOTH;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for BQ User Types : " + value);
		}
	}

	public static String getValue(BqUserTypes type) {

		switch (type) {
		case BUYER:
			return BUYER.value;
		case SUPPLIER:
			return SUPPLIER.value;
		case BOTH:
			return BOTH.value;
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
