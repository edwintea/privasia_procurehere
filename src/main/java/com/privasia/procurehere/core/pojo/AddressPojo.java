/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author Nitin Otageri
 */
public class AddressPojo implements Serializable {

	private static final long serialVersionUID = 7403534733368204804L;

	@ApiModelProperty(notes = "Title", required = true)
	private String title;

	@ApiModelProperty(notes = "Address Line 1", required = true)
	private String line1;

	@ApiModelProperty(notes = "Address Line 2", required = true)
	private String line2;

	@ApiModelProperty(notes = "City", required = true)
	private String city;

	@ApiModelProperty(notes = "Country Code", required = true)
	private String countryCode;

	@ApiModelProperty(notes = "State", required = true)
	private String state;

	@ApiModelProperty(notes = "zipCode", required = true)
	private String zipCode;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AddressPojo [title=" + title + ", line1=" + line1 + ", line2=" + line2 + ", city=" + city + ", countryCode=" + countryCode + ", state=" + state + ", zipcode=" + zipCode + "]";
	}

}
