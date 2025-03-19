
package com.privasia.procurehere.core.enums;

public enum LimitType {
	GREATER_THAN(">"), LESS_THAN("<"), EQUALS("="), LESS_THAN_OR_EQUAL("<="), GREATER_THAN_OR_EQUAL(">=");
	
	String value;
	
	private LimitType(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	
}
