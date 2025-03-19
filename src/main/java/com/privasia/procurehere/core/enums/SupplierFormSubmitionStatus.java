package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sana
 */

public enum SupplierFormSubmitionStatus {
	PENDING, SUBMITTED, REJECTED, ACCEPTED;

	public static SupplierFormSubmitionStatus fromString(String value) {
		try {
			for (SupplierFormSubmitionStatus status : SupplierFormSubmitionStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for SupplierFormSubmition STATUS : " + value);
		}
	}
}
