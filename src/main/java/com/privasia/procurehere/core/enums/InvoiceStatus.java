package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author ravi
 */
public enum InvoiceStatus {
	DRAFT,  INVOICED, ACCEPTED, DECLINED, CANCELLED;

	public static InvoiceStatus fromString(String value) {

		try {
			for (InvoiceStatus status : InvoiceStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Invoice STATUS : " + value);
		}
	}
}
