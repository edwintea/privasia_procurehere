/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
public enum TransactionStatus {
	IN_PROGRESS("IN_PROGRESS"), SUCCESS("SUCCESS"), VOIDED("VOIDED"), FAILURE("FAILURE"), TIMEOUT("TIMEOUT"), NEEDS_ATTENTION("NEEDS_ATTENTION"),
	_UNKNOWN("_UNKNOWN");

	private String value;

	/**
	 * @param value as type
	 */
	TransactionStatus(String value) {
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
	public static TransactionStatus fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(IN_PROGRESS.toString())) {
				return TransactionStatus.IN_PROGRESS;
			} else if (StringUtils.checkString(value).equals(SUCCESS.toString())) {
				return TransactionStatus.SUCCESS;
			} else if (StringUtils.checkString(value).equals(VOIDED.toString())) {
				return TransactionStatus.VOIDED;
			} else if (StringUtils.checkString(value).equals(FAILURE.toString())) {
				return TransactionStatus.FAILURE;
			} else if (StringUtils.checkString(value).equals(TIMEOUT.toString())) {
				return TransactionStatus.TIMEOUT;
			} else if (StringUtils.checkString(value).equals(NEEDS_ATTENTION.toString())) {
				return TransactionStatus.NEEDS_ATTENTION;
			} else if (StringUtils.checkString(value).equals(_UNKNOWN.toString())) {
				return TransactionStatus._UNKNOWN;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Auction Type : " + value);
		}
	}

	public static String getValue(TransactionStatus type) {

		switch (type) {
		case IN_PROGRESS:
			return IN_PROGRESS.value;
		case SUCCESS:
			return SUCCESS.value;
		case VOIDED:
			return VOIDED.value;
		case FAILURE:
			return FAILURE.value;
		case TIMEOUT:
			return TIMEOUT.value;
		case NEEDS_ATTENTION:
			return NEEDS_ATTENTION.value;
		case _UNKNOWN:
			return _UNKNOWN.value;
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
