/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author YOGESH
 */
public enum FinancePoStatus {

	SUBMITED("Submited"), FINANCED("Financed"), BANK_REJECTED("Bank Rejected"), BANK_COLLECTED("Bank Collected"),
	BANK_SETTLED("Bank Settled"), FINANCE_SETTLED("Referral Fee Settled With Privasia"),NEW("New");

	private String value;

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

	private FinancePoStatus(String value) {
		this.value = value;
	}

	public static FinancePoStatus fromString(String value) {

		try {
			for (FinancePoStatus status : FinancePoStatus.values()) {
				if (StringUtils.checkString(value).equals(status.toString())) {
					return status;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Finance Po Status : " + value);
		}
	}

}
