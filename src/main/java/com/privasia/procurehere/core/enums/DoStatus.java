package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author ravi
 */
public enum DoStatus {
	DRAFT, DELIVERED, ACCEPTED, DECLINED, CANCELLED;

	public static DoStatus fromString(String value) {
		try {
			for (DoStatus status : DoStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for DO STATUS : " + value);
		}
	}
}
