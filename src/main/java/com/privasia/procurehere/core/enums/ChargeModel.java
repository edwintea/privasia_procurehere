/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
public enum ChargeModel {
	FLAT_FEE("Flat Fee"), PER_UNIT("Per Unit");

	private String value;

	/**
	 * @param value as type
	 */
	ChargeModel(String value) {
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
	 * @return chargeModel of {@link ChargeModel}
	 */
	public static ChargeModel fromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(FLAT_FEE.toString())) {
				return ChargeModel.FLAT_FEE;
			} else if (StringUtils.checkString(value).equals(PER_UNIT.toString())) {
				return ChargeModel.PER_UNIT;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Charge Model: " + value);
		}
	}

	public static String getValue(ChargeModel chargeModel) {

		switch (chargeModel) {
		case FLAT_FEE:
			return FLAT_FEE.value;
		case PER_UNIT:
			return PER_UNIT.value;
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
