/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Ravi
 */
@MappedSuperclass
public class EventCorrespondenceAddress implements Serializable {

	private static final long serialVersionUID = -8664503131899507060L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ADRESS_LINE1", length = 250)
	private String line1;

	@Column(name = "ADRESS_LINE2", length = 250)
	private String line2;

	@Column(name = "ADRESS_LINE3", length = 250)
	private String line3;

	@Column(name = "CITY", length = 250)
	private String city;

	@Column(name = "ZIP", length = 32)
	private String zip;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "STATE")
	private State state;


	public RftEventCorrespondenceAddress copyForRft() {
		RftEventCorrespondenceAddress correspondenceAddress = new RftEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}

	public RfaEventCorrespondenceAddress copyForRfa() {
		RfaEventCorrespondenceAddress correspondenceAddress = new RfaEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}

	
	public RfiEventCorrespondenceAddress copyForRfi() {
		RfiEventCorrespondenceAddress correspondenceAddress = new RfiEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}

	public RfpEventCorrespondenceAddress copyForRfp() {
		RfpEventCorrespondenceAddress correspondenceAddress = new RfpEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}

	public RfqEventCorrespondenceAddress copyForRfq() {
		RfqEventCorrespondenceAddress correspondenceAddress = new RfqEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
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
	 * @return the line3
	 */
	public String getLine3() {
		return line3;
	}

	/**
	 * @param line3 the line3 to set
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
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

 

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
		result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
		result = prime * result + ((line3 == null) ? 0 : line3.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventCorrespondenceAddress other = (EventCorrespondenceAddress) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (line1 == null) {
			if (other.line1 != null)
				return false;
		} else if (!line1.equals(other.line1))
			return false;
		if (line2 == null) {
			if (other.line2 != null)
				return false;
		} else if (!line2.equals(other.line2))
			return false;
		if (line3 == null) {
			if (other.line3 != null)
				return false;
		} else if (!line3.equals(other.line3))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	public String toLogString() {
		return "EventAddress [  line1=" + line1 + ", line2=" + line2 + ", line3=" + line3 + ", city=" + city + ", zip=" + zip + "]";
	}

}
