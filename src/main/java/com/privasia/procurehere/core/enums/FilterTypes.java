package com.privasia.procurehere.core.enums;

import org.jfree.util.UnitType;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
public enum FilterTypes {
	REQUEST("REQUEST"), PR("PR"), RFI("RFI"), RFP("RFP"), RFQ("RFQ"), RFT("RFT"), RFA("RFA"), ALL_RFX("ALL_RFX"), FORWARD_ENGISH("Forward English Auction"),
	REVERSE_ENGISH("Reverse English Auction"), FORWARD_SEALED_BID("Forward Sealed Bid"), REVERSE_SEALED_BID("Reverse Sealed Bid"),
	FORWARD_DUTCH("Forward Dutch Auction"), REVERSE_DUTCH("Reverse Dutch Auction"),CONTRACT("Contract");

	private String value;

	/**
	 * @param value as type
	 */
	FilterTypes(String value) {
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
	public static FilterTypes fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(AuctionType.FORWARD_ENGISH.toString())) {
				return FilterTypes.FORWARD_ENGISH;
			} else if (StringUtils.checkString(value).equals(AuctionType.REVERSE_ENGISH.toString())) {
				return FilterTypes.REVERSE_ENGISH;
			} else if (StringUtils.checkString(value).equals(AuctionType.FORWARD_SEALED_BID.toString())) {
				return FilterTypes.FORWARD_SEALED_BID;
			} else if (StringUtils.checkString(value).equals(AuctionType.REVERSE_SEALED_BID.toString())) {
				return FilterTypes.REVERSE_SEALED_BID;
			} else if (StringUtils.checkString(value).equals(AuctionType.FORWARD_DUTCH.toString())) {
				return FilterTypes.FORWARD_DUTCH;
			} else if (StringUtils.checkString(value).equals(AuctionType.REVERSE_DUTCH.toString())) {
				return FilterTypes.REVERSE_DUTCH;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Filter Types : " + value);
		}
	}

}
