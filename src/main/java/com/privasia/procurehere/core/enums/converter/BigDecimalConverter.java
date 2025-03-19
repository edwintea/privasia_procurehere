package com.privasia.procurehere.core.enums.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;

/**
 * @author parveen
 */

public class BigDecimalConverter implements Converter<String, BigDecimal> {

	@Override
	public BigDecimal convert(String value) {
		return new BigDecimal(value.replaceAll(",", ""));
	}

}
