/**
 * 
 */
package com.privasia.procurehere.core.enums;

/**
 * @author Arc
 */
public enum IdSettingPattern {
	
	STANDARD("STANDARD (PRE/2021/00001)"),
	PRE_DATE_DEL_BU_DEL_NNNNN("PREDDMMYYYY/BU/NNNNN (PR2021/BU/00001)"),
	PRE_DATE_DEL_NNNNN_DEL_BU("PREDDMMYYYY/NNNN/BU (PRE2021/00001/BU)"),
	PRE_DEL_BU_DEL_DATE_DEL_NNNN("PRE/BU/DDMMYYYY/NNNN (PRE/BU/2021/00001)"),
	PRE_DEL_DATE_DEL_NNNN_DEL_BU("PRE/DDMMYYYY/NNNN/BU (PRE/2021/00001/BU)"),
	PRE_DEL_BU_DEL_DATE_NNNN("PRE/BU/DDMMYYYYNNNN (PRE/BU/2021001)"),
	PRE_DEL_DATE_NNNN_DEL_BU("PRE/DDMMYYYYNNNN/BU (PRE/2021001/BU)"),
	BU_DEL_PRE_DEL_DATE_NNNN("BU/PRE/DDMMYYYYNNNN (BU/PRE/2021001)");
	
	private String value;

	/**
	 * @param value as type
	 */
	IdSettingPattern(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	public String toString() {
		return value;
	}

	//private static IdSettingPattern[] IdSettingPatternValues = { PRE_DATE_DEL_NNNNN_DEL_BU, PRE_DEL_BU_DEL_DATE_DEL_NNNN, PRE_DEL_DATE_DEL_NNNN_DEL_BU,PRE_DEL_BU_DEL_DATE_NNNN,PRE_DEL_DATE_NNNN_DEL_BU};

	public static IdSettingPattern getIdSettingType(String value) {
		for (IdSettingPattern idSettingPattern : IdSettingPattern.values()) {
			if (value == idSettingPattern.getValue()) {
				return idSettingPattern;
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
