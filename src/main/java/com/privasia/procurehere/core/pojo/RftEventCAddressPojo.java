package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.RftEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.State;

public class RftEventCAddressPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2851816398670520424L;

	private String id;

	private String line1;

	private String line2;

	private String city;

	private String zip;

	private State state;
	
	private String country;

	public RftEventCAddressPojo(RftEventCorrespondenceAddress rftEventCorrespondenceAddress) {
		super();
		this.id =rftEventCorrespondenceAddress.getId();
		this.line1 = rftEventCorrespondenceAddress.getLine1();
		this.line2 = rftEventCorrespondenceAddress.getLine2();
		this.city = rftEventCorrespondenceAddress.getCity();
		this.zip = rftEventCorrespondenceAddress.getZip();
		this.state = rftEventCorrespondenceAddress.getState();
		this.country = rftEventCorrespondenceAddress.getState().getCountry().getCountryName();
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
	 * @return the state for
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
