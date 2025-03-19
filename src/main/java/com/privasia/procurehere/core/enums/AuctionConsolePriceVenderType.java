package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Kapil
 */
public enum AuctionConsolePriceVenderType {
	SHOW_ALL("Show All"), SHOW_OWN("Show Own"), SHOW_LEADING("Show Leading"), SHOW_NONE("Show None"), SHOW_MY_RANK("Show My Rank");
	private String value;

	AuctionConsolePriceVenderType(String value) {
		this.value = value;
	}

	public static AuctionConsolePriceVenderType convertFromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(SHOW_ALL.value)) {
				return AuctionConsolePriceVenderType.SHOW_ALL;
			} else if (StringUtils.checkString(value).equals(SHOW_OWN.value)) {
				return AuctionConsolePriceVenderType.SHOW_OWN;
			} else if (StringUtils.checkString(value).equals(SHOW_NONE.value)) {
				return AuctionConsolePriceVenderType.SHOW_NONE;
			} else if (StringUtils.checkString(value).equals(SHOW_MY_RANK.value)) {
				return AuctionConsolePriceVenderType.SHOW_MY_RANK;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for AuctionConsolePriceVenderType : " + value);
		}
	}

	public static String convertToString(AuctionConsolePriceVenderType type) {

		switch (type) {
		case SHOW_ALL:
			return SHOW_ALL.value;
		case SHOW_OWN:
			return SHOW_OWN.value;
		case SHOW_NONE:
			return SHOW_NONE.value;
		case SHOW_MY_RANK:
			return SHOW_MY_RANK.value;
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
