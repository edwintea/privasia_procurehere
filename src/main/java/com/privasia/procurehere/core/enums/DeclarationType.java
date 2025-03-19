/**
 * 
 */
package com.privasia.procurehere.core.enums;

import org.jfree.util.UnitType;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
public enum DeclarationType {

	EVALUATION_PROCESS("Evaluation Process"), SUPPLIER_ACCEPTANCE("Supplier Acceptance of Terms & Conditions");

	String value;

	DeclarationType(String value) {
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

	/**
	 * @param value as boat type
	 * @return unitType of {@link UnitType}
	 */
	public static DeclarationType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(EVALUATION_PROCESS.toString())) {
				return DeclarationType.EVALUATION_PROCESS;
			} else if (StringUtils.checkString(value).equals(SUPPLIER_ACCEPTANCE.toString())) {
				return DeclarationType.SUPPLIER_ACCEPTANCE;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for declaration Type : " + value);
		}
	}

	public static String getValue(DeclarationType type) {

		switch (type) {
		case EVALUATION_PROCESS:
			return EVALUATION_PROCESS.value;
		case SUPPLIER_ACCEPTANCE:
			return SUPPLIER_ACCEPTANCE.value;
		default:
			return null;
		}
	}

}
