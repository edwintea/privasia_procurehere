package com.privasia.procurehere.core.enums;

/**
 * @author pooja
 */
public enum RequestAssociateBuyerStatus {
	APPROVED("Approved"), PENDING("Pending"), REJECTED("Rejected");

	String value;

	RequestAssociateBuyerStatus(String value) {
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
