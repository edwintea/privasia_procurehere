/**
 * 
 */
package com.privasia.procurehere.core.enums;

/**
 * @author Arc
 */
public enum IdSettingType {
	BUSINESS_UNIT("Business Unit"), COMPANY_LEVEL("Company Level");

	private String value;

	/**
	 * @param value as type
	 */
	IdSettingType(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

	public static IdSettingType getIdSettingType(String value) {
		for (IdSettingType idSetting : IdSettingType.values()) {
			if (value == idSetting.getValue()) {
				return idSetting;
			}
		}
		return null;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
