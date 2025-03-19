/**
 * 
 */
package com.privasia.procurehere.core.enums;

/**
 * @author Priyanka Singh
 */
public enum AwardCriteria {

	LOWEST_TOTAL_PRICE("LOWEST TOTAL PRICE"), LOWEST_ITEMIZED_PRICE("LOWEST ITEMIZED PRICE"), HIGHEST_TOTAL_PRICE("HIGHEST TOTAL PRICE"),
	HIGHEST_ITEMIZED_PRICE("HIGHEST ITEMIZED PRICE"), MANUAL("MANUAL");

	String value;

	AwardCriteria(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
