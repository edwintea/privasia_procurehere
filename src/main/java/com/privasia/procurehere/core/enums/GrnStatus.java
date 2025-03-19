package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author ravi
 */
public enum GrnStatus {
	DRAFT, DELIVERED, ACCEPTED, DECLINED, CANCELLED,RECEIVED;

	public static GrnStatus fromString(String value) {
		try {
			for (GrnStatus status : GrnStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for GRN STATUS : " + value);
		}
	}
}
