package com.privasia.procurehere.core.enums.converter;

import javax.persistence.AttributeConverter;

import com.privasia.procurehere.core.enums.ModuleType;
/**
 * 
 * @author pooja
 *
 */
public class ModuleTypeCoverter implements AttributeConverter<ModuleType, String> {

	@Override
	public String convertToDatabaseColumn(ModuleType value) {
			if(value==null)
				return null;
		return ModuleType.getValue(value);
	}

	@Override
	public ModuleType convertToEntityAttribute(String dbData) {
		if(dbData!=null){
			return ModuleType.fromString(dbData);
		}
		else{
		return null;
		}
	}

}
