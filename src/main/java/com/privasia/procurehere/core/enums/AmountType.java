/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Yogesh
 */
public enum AmountType {
	FEE, DEPOSIT;

	public static AmountType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals("FEE")) {
				return AmountType.FEE;
			} else if (StringUtils.checkString(value).equals("DEPOSIT")) {
				return AmountType.DEPOSIT;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for STATUS : " + value);
		}
	}

}
