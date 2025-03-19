/**
 * 
 */
package com.privasia.procurehere.core.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.privasia.procurehere.core.enums.EnvelopType;

/**
 * @author arc
 */
@Converter
public class EnvelopTypeConverter implements AttributeConverter<EnvelopType, String> {

	@Override
	public String convertToDatabaseColumn(EnvelopType value) {
		if (value == null)
			return null;
		return EnvelopType.convertToString(value);
	}

	@Override
	public EnvelopType convertToEntityAttribute(String dbData) {
		if (dbData != null)
			return EnvelopType.convertFromString(dbData);
		throw new IllegalArgumentException("Unknown" + dbData);
	}

}