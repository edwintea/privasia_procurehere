package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
public enum PoStatus {
	DRAFT,COMPLETED,APPROVED,REJECTED,SUSPENDED,CLOSED,READY,ACTIVE, ORDERED, ACCEPTED, DECLINED, CANCELLED, REVISE, PENDING,TRANSFERRED, DELIVERED, FINISHED, _UNKNOWN, ALL;
	public static PoStatus fromString(String value) {

		try {
			for (PoStatus status : PoStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for PO STATUS : " + value);
		}
	}
}
