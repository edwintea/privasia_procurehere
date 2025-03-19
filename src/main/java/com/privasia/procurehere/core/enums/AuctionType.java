/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
public enum AuctionType {						
	FORWARD_ENGISH("Forward English Auction"), //
	REVERSE_ENGISH("Reverse English Auction"), //
	FORWARD_SEALED_BID("Forward Sealed Bid"), //
	REVERSE_SEALED_BID("Reverse Sealed Bid"), //
	FORWARD_DUTCH("Forward Dutch Auction"), //
	REVERSE_DUTCH("Reverse Dutch Auction");

	private String value;

	/**
	 * @param value as type
	 */
	AuctionType(String value) {
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
	public static AuctionType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(FORWARD_ENGISH.toString())) {
				return AuctionType.FORWARD_ENGISH;
			} else if (StringUtils.checkString(value).equals(REVERSE_ENGISH.toString())) {
				return AuctionType.REVERSE_ENGISH;
			} else if (StringUtils.checkString(value).equals(FORWARD_SEALED_BID.toString())) {
				return AuctionType.FORWARD_SEALED_BID;
			} else if (StringUtils.checkString(value).equals(REVERSE_SEALED_BID.toString())) {
				return AuctionType.REVERSE_SEALED_BID;
			} else if (StringUtils.checkString(value).equals(FORWARD_DUTCH.toString())) {
				return AuctionType.FORWARD_DUTCH;
			} else if (StringUtils.checkString(value).equals(REVERSE_DUTCH.toString())) {
				return AuctionType.REVERSE_DUTCH;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Auction Type : " + value);
		}
	}

	public static String getValue(AuctionType type) {

		switch (type) {
		case FORWARD_ENGISH:
			return FORWARD_ENGISH.value;
		case REVERSE_ENGISH:
			return REVERSE_ENGISH.value;
		case FORWARD_SEALED_BID:
			return FORWARD_SEALED_BID.value;
		case REVERSE_SEALED_BID:
			return REVERSE_SEALED_BID.value;
		case FORWARD_DUTCH:
			return FORWARD_DUTCH.value;
		case REVERSE_DUTCH:
			return REVERSE_DUTCH.value;
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
