/**
 * 
 */
package com.privasia.procurehere.core.enums.converter;

import javax.persistence.AttributeConverter;

import com.privasia.procurehere.core.enums.AuctionType;

/**
 * @author Nitin Otageri
 */
public class AuctionTypeCoverter implements AttributeConverter<AuctionType, String> {

	@Override
	public String convertToDatabaseColumn(AuctionType value) {
		if (value == null)
			return null;
		return AuctionType.getValue(value);
	}

	@Override
	public AuctionType convertToEntityAttribute(String dbData) {
		if (dbData != null) {
			return AuctionType.fromString(dbData);
		} else {
			return null;
		}
		// throw new IllegalArgumentException("Unknown : " + dbData);
	}
}
