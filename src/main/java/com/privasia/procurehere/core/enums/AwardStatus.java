/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Kapil
 */
public enum AwardStatus {
	PENDING, APPROVED, DRAFT;

	public static AwardStatus fromString(String value) {
		try {
			if (StringUtils.checkString(value).equals("PENDING")) {
				return AwardStatus.PENDING;
			} else if (StringUtils.checkString(value).equals("APPROVED")) {
				return AwardStatus.APPROVED;
			} else if (StringUtils.checkString(value).equals("DRAFT")) {
				return AwardStatus.DRAFT;
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for AwardStatus : " + value);
		}
	}

}
