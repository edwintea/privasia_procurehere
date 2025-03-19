package com.privasia.procurehere.core.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.privasia.procurehere.core.enums.MeetingType;

/**
 * @author parveen
 */

@Converter
public class MeetingTypeConverter implements AttributeConverter<MeetingType, String> {

	@Override
	public String convertToDatabaseColumn(MeetingType value) {
		if (value == null)
			return null;
		return MeetingType.convertToString(value);
	}

	@Override
	public MeetingType convertToEntityAttribute(String dbData) {
		if (dbData != null)
			return MeetingType.convertFromString(dbData);
		throw new IllegalArgumentException("Unknown" + dbData);
	}

}
