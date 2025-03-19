package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Parveen
 */
public enum MeetingType {
	GENERAL_MEETING("General Meeting"), SITE_VISIT("Site Visit");

	private String value;

	/**
	 * @param value as type
	 */
	MeetingType(String value) {
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
	public static MeetingType convertFromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(SITE_VISIT.toString())) {
				return MeetingType.SITE_VISIT;
			} else if (StringUtils.checkString(value).equals(GENERAL_MEETING.toString())) {
				return MeetingType.GENERAL_MEETING;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for MeetingType : " + value);
		}
	}

	public static String convertToString(MeetingType type) {

		switch (type) {
		case GENERAL_MEETING:
			return GENERAL_MEETING.value;
		case SITE_VISIT:
			return SITE_VISIT.value;
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