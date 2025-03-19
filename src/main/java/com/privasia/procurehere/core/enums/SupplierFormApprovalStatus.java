package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */

public enum SupplierFormApprovalStatus {
	PENDING, APPROVED, REJECTED;

	public static SupplierFormApprovalStatus fromString(String value) {
		try {
			for (SupplierFormApprovalStatus status : SupplierFormApprovalStatus.values()) {
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
