package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.State;

/**
 * 
 * @author Vipul
 *
 */
public class BuyerAddressPojo {

	private String id;
	
	private String title;

	private String line1;

	private String line2;

	private String city;

	private String zip;

	private State state;
	
	private String country;
	
	private String stateName;
	
	private String status;

	public BuyerAddressPojo(BuyerAddress buyerAddress) {
		super();
		this.id = buyerAddress.getId();
		this.title=buyerAddress.getTitle();
		this.line1 = buyerAddress.getLine1();
		this.line2 = buyerAddress.getLine2();
		this.city = buyerAddress.getCity();
		this.zip = buyerAddress.getZip();
		this.state = buyerAddress.getState().createShallowCopy();
		this.country=buyerAddress.getState().getCountry().getCountryName();
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}

	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public BuyerAddressPojo() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	// toString is using to show data into jsp
	@Override
	public String toString() {
		return   title + " , " + line1  + " , " + city + " , " + zip + " , " + state.getStateName() + " , " + country ;
	}
}
