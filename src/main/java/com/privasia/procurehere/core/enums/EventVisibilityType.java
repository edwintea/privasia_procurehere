package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Parveen
 */
public enum EventVisibilityType {
	PRIVATE, PUBLIC,PARTIAL; 

	public static EventVisibilityType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(PRIVATE.toString())) {
				return EventVisibilityType.PRIVATE;
			} else if (StringUtils.checkString(value).equals(PUBLIC.toString())) {
				return EventVisibilityType.PUBLIC;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for EventVisibilityType : " + value);
		}
	}

	public static EventVisibilityType fromStringToEventVisibilityType(String value) {
		try {
			if (StringUtils.checkString(value).equals("PRIVATE")) {
				return EventVisibilityType.PRIVATE;
			} else if (StringUtils.checkString(value).equals("PUBLIC")) {
				return EventVisibilityType.PUBLIC;
			} else if (StringUtils.checkString(value).equals("PARTIAL")) {
				return EventVisibilityType.PARTIAL;
			}  else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Event Visibility : " + value);
		}
	}
	
}
