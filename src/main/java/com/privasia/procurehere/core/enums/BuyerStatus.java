/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
public enum BuyerStatus {
	UNPAID, PENDING, ACTIVE, SUSPENDED, ALL, CLOSED;

	public static BuyerStatus fromString(String value) {

		try {
			for (BuyerStatus status : BuyerStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Buyer STATUS : " + value);
		}
	}
}
