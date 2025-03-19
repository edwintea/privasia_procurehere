package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Parveen
 */
public enum PrStatus {
	DRAFT, COMPLETE, PENDING, APPROVED, REJECTED, CANCELED, SUSPENDED, ACTIVE, CLOSED, TRANSFERRED, DELIVERED, FINISHED, _UNKNOWN, ALL;

	public static PrStatus fromString(String value) {

		try {
			for (PrStatus status : PrStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for PR STATUS : " + value);
		}
	}
}
