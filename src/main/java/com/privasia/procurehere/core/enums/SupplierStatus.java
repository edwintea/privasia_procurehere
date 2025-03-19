/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
public enum SupplierStatus {
	PENDING, APPROVED, REJECTED, SUSPENDED, CLOSED, _UNKNOWN, ALL;

	public static SupplierStatus fromString(String value) {

		try {
			for (SupplierStatus status : SupplierStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Supplier STATUS : " + value);
		}
	}
}
