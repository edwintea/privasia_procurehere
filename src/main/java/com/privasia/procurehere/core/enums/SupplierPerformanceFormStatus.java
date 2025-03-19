/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Jayshree
 *
 */
public enum SupplierPerformanceFormStatus {
	DRAFT, SCHEDULED, ACTIVE, SUSPENDED, CLOSED, CONCLUDED, CANCELED;
	
	public static SupplierPerformanceFormStatus fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals("DRAFT")) {
				return SupplierPerformanceFormStatus.DRAFT;
			} else if (StringUtils.checkString(value).equals("SCHEDULED")) {
				return SupplierPerformanceFormStatus.SCHEDULED;
			} else if (StringUtils.checkString(value).equals("ACTIVE")) {
				return SupplierPerformanceFormStatus.ACTIVE;
			} else if (StringUtils.checkString(value).equals("SUSPENDED")) {
				return SupplierPerformanceFormStatus.SUSPENDED;
			} else if (StringUtils.checkString(value).equals("CLOSED")) {
				return SupplierPerformanceFormStatus.CLOSED;
			} else if (StringUtils.checkString(value).equals("CONCLUDED")) {
				return SupplierPerformanceFormStatus.CONCLUDED;
			} else if (StringUtils.checkString(value).equals("CANCELED")) {
				return SupplierPerformanceFormStatus.CANCELED;
			} else if (StringUtils.checkString(value).equals("SUSPENDED")) {
				return SupplierPerformanceFormStatus.SUSPENDED;
			} 
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Supplier Performance Form Status : " + value);
		}
	}
}
