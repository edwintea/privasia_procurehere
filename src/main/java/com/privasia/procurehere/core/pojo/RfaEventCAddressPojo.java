package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.RfaEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.State;

public class RfaEventCAddressPojo implements Serializable {

	private static final long serialVersionUID = -1308134739482565544L;

	private String id;

	private String line1;

	private String line2;

	private String city;

	private String zip;

	private State state;

	private String country;

	public RfaEventCAddressPojo(RfaEventCorrespondenceAddress rfaEventCorrespondenceAddress) {
		super();
		this.id = rfaEventCorrespondenceAddress.getId();
		this.line1 = rfaEventCorrespondenceAddress.getLine1();
		this.line2 = rfaEventCorrespondenceAddress.getLine2();
		this.city = rfaEventCorrespondenceAddress.getCity();
		this.zip = rfaEventCorrespondenceAddress.getZip();
		this.state = rfaEventCorrespondenceAddress.getState();
		this.country = rfaEventCorrespondenceAddress.getState().getCountry().getCountryName();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

}
