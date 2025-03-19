/**
 * 
 */
package com.privasia.procurehere.core.enums.converter;

import javax.persistence.AttributeConverter;

import com.privasia.procurehere.core.enums.ChargeModel;

/**
 * @author Nitin Otageri
 */
public class ChargeModelCoverter implements AttributeConverter<ChargeModel, String> {

	@Override
	public String convertToDatabaseColumn(ChargeModel value) {
		if (value == null)
			return null;
		return ChargeModel.getValue(value);
	}

	@Override
	public ChargeModel convertToEntityAttribute(String dbData) {
		if (dbData != null) {
			return ChargeModel.fromString(dbData);
		} else {
			return null;
		}
//		throw new IllegalArgumentException("Unknown : " + dbData);
	}
}
