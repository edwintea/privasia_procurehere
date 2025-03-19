package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

public enum UserType {
	NORMAL_USER, APPROVER_USER, REQUESTOR_USER;

	public static UserType fromString(String value) {

		try {
			for (UserType status : UserType.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for USER TYPE : " + value);
		}
	}
}
