package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Giridhar
 */
public enum PricingTypes {
	NORMAL_PRICE("Normal Price"), TRADE_IN_PRICE("Trade In Price"), BUYER_FIXED_PRICE("Buyer fix Price");

	private String value;

	/**
	 * @param value as type
	 */
	PricingTypes(String value) {
		this.value = value;
	}

	/**
	 * @param value
	 * @return
	 */
	public static PricingTypes convertFromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(NORMAL_PRICE.value)) {
				return PricingTypes.NORMAL_PRICE;
			} else if (StringUtils.checkString(value).equals(TRADE_IN_PRICE.value)) {
				return PricingTypes.TRADE_IN_PRICE;
			} else if (StringUtils.checkString(value).equals(BUYER_FIXED_PRICE.value)) {
				return PricingTypes.BUYER_FIXED_PRICE;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for PricingTypes : " + value);
		}
	}

	public static String convertToString(PricingTypes type) {

		switch (type) {
		case NORMAL_PRICE:
			return NORMAL_PRICE.value;
		case TRADE_IN_PRICE:
			return TRADE_IN_PRICE.value;
		case BUYER_FIXED_PRICE:
			return BUYER_FIXED_PRICE.value;
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
