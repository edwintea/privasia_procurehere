package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author sudesha created for Search and Filter Supplier
 */

public class SearchFilterSupplierPojo implements Serializable{

	private static final long serialVersionUID = -4014459832308922L;
	private String companyname;;
	private String country;
	private String personincharge;
	private String status;
	private Boolean isRegistered;

	private String cusStatus;

	/**
	 * @return the companyname
	 */
	public String getCompanyname() {
		return companyname;
	}

	/**
	 * @param companyname the companyname to set
	 */
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
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

	/**
	 * @return the personincharge
	 */
	public String getPersonincharge() {
		return personincharge;
	}

	/**
	 * @param personincharge the personincharge to set
	 */
	public void setPersonincharge(String personincharge) {
		this.personincharge = personincharge;
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

	public Boolean getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(Boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	
	/**
	 * @return the cusStatus
	 */
	public String getCusStatus() {
		return cusStatus;
	}

	/**
	 * @param cusStatus the cusStatus to set
	 */
	public void setCusStatus(String cusStatus) {
		this.cusStatus = cusStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchFilterSupplierPojo [companyname=" + companyname + ", country=" + country + ", personincharge=" + personincharge + ", status=" + status + ", cusStatus=" + cusStatus + "]";
	}

}
