package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
public enum ErpAuditType {
	PENDING, CREATED, DELETED, DUPLICATE, ERROR;

	public static ErpAuditType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(PENDING.toString())) {
				return ErpAuditType.PENDING;
			} else if (StringUtils.checkString(value).equals(CREATED.toString())) {
				return ErpAuditType.CREATED;
			} else if (StringUtils.checkString(value).equals(DELETED.toString())) {
				return ErpAuditType.DELETED;
			} else if (StringUtils.checkString(value).equals(DUPLICATE.toString())) {
				return ErpAuditType.DUPLICATE;
			} else if (StringUtils.checkString(value).equals(ERROR.toString())) {
				return ErpAuditType.ERROR;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Audit Type : " + value);
		}
	}
}
