/**
 * 
 */
package com.privasia.procurehere.core.enums;

import org.jfree.util.UnitType;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
public enum RfxTypes {
	RFI("Request for Information"), RFP("Request for Proposal"), RFQ("Request for Quotation"), RFT("Request for Tender"), RFA("Request for Auction"),PO("Purchase Order");

	private String value;

	/**
	 * @param value as type
	 */
	RfxTypes(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

	private static RfxTypes[] rfxTypesValues = { RFI, RFP, RFQ, RFT, RFA,PO };

	public static RfxTypes getRfxTypes(String value) {
		for (RfxTypes rfx : rfxTypesValues) {
			if (value == rfx.getValue()) {
				return rfx;
			}
		}
		return null;
	}

	/**
	 * @param value as boat type
	 * @return unitType of {@link UnitType}
	 */
	public static RfxTypes fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(RFI.toString())) {
				return RfxTypes.RFI;
			} else if (StringUtils.checkString(value).equals(RFP.toString())) {
				return RfxTypes.RFP;
			} else if (StringUtils.checkString(value).equals(RFQ.toString())) {
				return RfxTypes.RFQ;
			} else if (StringUtils.checkString(value).equals(RFT.toString())) {
				return RfxTypes.RFT;
			} else if (StringUtils.checkString(value).equals(RFA.toString())) {
				return RfxTypes.RFA;
			} else if (StringUtils.checkString(value).equals(PO.toString())) {
				return RfxTypes.PO;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for RFX : " + value);
		}
	}

	public static String getValue(RfxTypes type) {

		switch (type) {
		case RFI:
			return RFI.value;
		case RFP:
			return RFP.value;
		case RFQ:
			return RFQ.value;
		case RFT:
			return RFT.value;
		case RFA:
			return RFA.value;
		case PO:
			return PO.value;
		default:
			return null;
		}
	}

	public static RfxTypes fromStringToRfxType(String value) {

		try {
			if (StringUtils.checkString(value).equals("RFI")) {
				return RfxTypes.RFI;
			} else if (StringUtils.checkString(value).equals("RFP")) {
				return RfxTypes.RFP;
			} else if (StringUtils.checkString(value).equals("RFQ")) {
				return RfxTypes.RFQ;
			} else if (StringUtils.checkString(value).equals("RFT")) {
				return RfxTypes.RFT;
			} else if (StringUtils.checkString(value).equals("RFA")) {
				return RfxTypes.RFA;
			} else if (StringUtils.checkString(value).equals("PO")) {
				return RfxTypes.PO;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for RFX : " + value);
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
