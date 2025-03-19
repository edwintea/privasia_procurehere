package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */

public enum SupplierFormsStatus {
	DRAFT, ACTIVE, INACTIVE;

	public static SupplierFormsStatus fromString(String value) {
		try {
			for (SupplierFormsStatus status : SupplierFormsStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for SupplierForm STATUS : " + value);
		}
	}
}
