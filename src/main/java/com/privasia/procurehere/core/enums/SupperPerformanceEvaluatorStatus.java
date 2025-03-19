/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Jayshree
 *
 */
public enum SupperPerformanceEvaluatorStatus {
	DRAFT, PENDING, APPROVED, SUSPENDED;
	
	public static SupperPerformanceEvaluatorStatus fromString(String value) {
		try {
			if (StringUtils.checkString(value).equals("DRAFT")) {
				return SupperPerformanceEvaluatorStatus.DRAFT;
			} else if (StringUtils.checkString(value).equals("PENDING")) {
				return SupperPerformanceEvaluatorStatus.PENDING;
			} else if (StringUtils.checkString(value).equals("APPROVED")) {
				return SupperPerformanceEvaluatorStatus.APPROVED;
			} else if (StringUtils.checkString(value).equals("SUSPENDED")) {
				return SupperPerformanceEvaluatorStatus.SUSPENDED;
			} 
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Evaluator Approval Status : " + value);
		}
	}
}
