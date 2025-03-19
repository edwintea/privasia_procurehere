/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
public enum SourcingFormStatus {
	DRAFT, PENDING, APPROVED, REJECTED, CANCELED, _UNKNOWN, ALL, FINISHED, CONCLUDED;
	public static SourcingFormStatus fromString(String value) {
		try {
			if (StringUtils.checkString(value).equals("DRAFT")) {
				return SourcingFormStatus.DRAFT;
			} else if (StringUtils.checkString(value).equals("PENDING")) {
				return SourcingFormStatus.PENDING;
			} else if (StringUtils.checkString(value).equals("APPROVED")) {
				return SourcingFormStatus.APPROVED;
			} else if (StringUtils.checkString(value).equals("REJECTED")) {
				return SourcingFormStatus.REJECTED;
			} else if (StringUtils.checkString(value).equals("CANCELED")) {
				return SourcingFormStatus.CANCELED;
			} else if (StringUtils.checkString(value).equals("FINISHED")) {
				return SourcingFormStatus.FINISHED;
			} else if (StringUtils.checkString(value).equals("CONCLUDED")) {
				return SourcingFormStatus.CONCLUDED;
			} else if (StringUtils.checkString(value).equals("ALL")) {
				return SourcingFormStatus.ALL;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for STATUS : " + value);
		}
	}

}
