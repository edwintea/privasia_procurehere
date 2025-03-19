package com.privasia.procurehere.core.pojo;

import java.math.BigDecimal;
import java.util.Map;

public class CurrencyConversion {
	private String base;
	private String date;
	private Map<String, BigDecimal> rates;

	public CurrencyConversion() {
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the rates
	 */
	public Map<String, BigDecimal> getRates() {
		return rates;
	}

	/**
	 * @param rates the rates to set
	 */
	public void setRates(Map<String, BigDecimal> rates) {
		this.rates = rates;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Conversion [base=" + this.getBase() + ", date=" + this.getDate() + ", rates=" + this.getRates() + "]";
	}
}