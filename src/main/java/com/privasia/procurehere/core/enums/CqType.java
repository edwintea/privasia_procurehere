/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
public enum CqType {
	TEXT("Text"), CHOICE("Choice"), CHOICE_WITH_SCORE("Choice with Score"), CHECKBOX("Checkbox"), LIST("List"), DATE("Date"), NUMBER("Number"),
	PARAGRAPH("Paragraph"), DOCUMENT_DOWNLOAD_LINK("Document download link");

	private String value;

	private CqType(String value) {
		this.value = value;
	}

	/**
	 * @param value as CqType
	 * @return cqType of {@link CqType}
	 */
	public static CqType fromString(String value) {

		try {
			if (StringUtils.checkString(value).equals(TEXT.value)) {
				return CqType.TEXT;
			} else if (StringUtils.checkString(value).equals(CHOICE.value)) {
				return CqType.CHOICE;
			} else if (StringUtils.checkString(value).equals(CHOICE_WITH_SCORE.value)) {
				return CqType.CHOICE_WITH_SCORE;
			} else if (StringUtils.checkString(value).equals(CHECKBOX.value)) {
				return CqType.CHECKBOX;
			} else if (StringUtils.checkString(value).equals(LIST.value)) {
				return CqType.LIST;
			} else if (StringUtils.checkString(value).equals(NUMBER.value)) {
				return CqType.NUMBER;
			} else if (StringUtils.checkString(value).equals(DATE.value)) {
				return CqType.DATE;
			} else if (StringUtils.checkString(value).equals(PARAGRAPH.value)) {
				return CqType.PARAGRAPH;
			} else if (StringUtils.checkString(value).equals(DOCUMENT_DOWNLOAD_LINK.value)) {
				return CqType.DOCUMENT_DOWNLOAD_LINK;
			} else
				return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for CqType : " + value);
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
