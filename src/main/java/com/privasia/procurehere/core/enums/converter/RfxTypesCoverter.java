/**
 * 
 */
package com.privasia.procurehere.core.enums.converter;

import javax.persistence.AttributeConverter;

import com.privasia.procurehere.core.enums.RfxTypes;

/**
 * @author arc
 */
public class RfxTypesCoverter implements AttributeConverter<RfxTypes, String> {

	@Override
	public String convertToDatabaseColumn(RfxTypes value) {
		if (value == null)
			return null;
		return RfxTypes.getValue(value);
	}

	@Override
	public RfxTypes convertToEntityAttribute(String dbData) {
		if (dbData != null) {
			return RfxTypes.fromString(dbData);
		} else {
			return null;
		}
//		throw new IllegalArgumentException("Unknown : " + dbData);
	}
}
