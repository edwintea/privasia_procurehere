package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

public enum NotesTypes {
	COMPLAINT("Complaint"), REGISTERED("Registered"), TRANSACTED("Transacted");

	private String value;

	/**
	 * @param value as type
	 */
	NotesTypes(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

	/**
	 * @param value
	 * @return
	 */
	public static NotesTypes fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(COMPLAINT.toString())) {
				return NotesTypes.COMPLAINT;
			} else if (StringUtils.checkString(value).equals(REGISTERED.toString())) {
				return NotesTypes.REGISTERED;
			} else if (StringUtils.checkString(value).equals(TRANSACTED.toString())) {
				return NotesTypes.TRANSACTED;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for notes : " + value);
		}
	}

	public static String getValue(NotesTypes type) {

		switch (type) {
		case COMPLAINT:
			return COMPLAINT.value;
		case REGISTERED:
			return REGISTERED.value;
		case TRANSACTED:
			return TRANSACTED.value;
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
