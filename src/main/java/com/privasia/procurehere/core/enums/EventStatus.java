/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Kapil
 */
public enum EventStatus {
	DRAFT, PENDING, APPROVED, REJECTED, CANCELED, SUSPENDED, ACTIVE, CLOSED, FINISHED, COMPLETE;

	public static EventStatus fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals("DRAFT")) {
				return EventStatus.DRAFT;
			} else if (StringUtils.checkString(value).equals("PENDING")) {
				return EventStatus.PENDING;
			} else if (StringUtils.checkString(value).equals("APPROVED")) {
				return EventStatus.APPROVED;
			} else if (StringUtils.checkString(value).equals("REJECTED")) {
				return EventStatus.REJECTED;
			} else if (StringUtils.checkString(value).equals("CANCELED")) {
				return EventStatus.CANCELED;

			} else if (StringUtils.checkString(value).equals("SUSPENDED")) {
				return EventStatus.SUSPENDED;
			} else if (StringUtils.checkString(value).equals("ACTIVE")) {
				return EventStatus.ACTIVE;
			} else if (StringUtils.checkString(value).equals("CLOSED")) {
				return EventStatus.CLOSED;
			} else if (StringUtils.checkString(value).equals("FINISHED")) {
				return EventStatus.FINISHED;

			} else if (StringUtils.checkString(value).equals("COMPLETE")) {
				return EventStatus.COMPLETE;
			
			} 
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for STATUS : " + value);
		}
	}

}
