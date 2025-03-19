package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author sudesha
 */
public class SearchFilterEventPojo implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6790659338696997981L;
	private Date eventStart;
	private String eventIds;
	private Date eventEnd;
	private String nameofevent;
	private String referencenumber;
	private String eventid;
	private String eventtype ="ALL";
	private String eventowner;
	private String businessunit;
	private String status;
	private String leadingvendors;
	private BigDecimal leadingamount;
	private Integer acceptedvendors;
	private Integer submitedvendors;
	private String[] eventcategories;
	private boolean isAllSelected;
	private String searchValue;

	/**
	 * @return the eventStart
	 */
	public Date getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart the eventStart to set
	 */
	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public Date getEventEnd() {
		return eventEnd;
	}

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return the nameofevent
	 */
	public String getNameofevent() {
		return nameofevent;
	}

	/**
	 * @param nameofevent the nameofevent to set
	 */
	public void setNameofevent(String nameofevent) {
		this.nameofevent = nameofevent;
	}

	/**
	 * @return the referencenumber
	 */
	public String getReferencenumber() {
		return referencenumber;
	}

	/**
	 * @param referencenumber the referencenumber to set
	 */
	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	/**
	 * @return the eventid
	 */
	public String getEventid() {
		return eventid;
	}

	/**
	 * @param eventid the eventid to set
	 */
	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	/**
	 * @return the eventtype
	 */
	public String getEventtype() {
		return eventtype;
	}

	/**
	 * @param eventtype the eventtype to set
	 */
	public void setEventtype(String eventtype) {
		this.eventtype = eventtype;
	}

	/**
	 * @return the eventowner
	 */
	public String getEventowner() {
		return eventowner;
	}

	/**
	 * @param eventowner the eventowner to set
	 */
	public void setEventowner(String eventowner) {
		this.eventowner = eventowner;
	}

	/**
	 * @return the businessunit
	 */
	public String getBusinessunit() {
		return businessunit;
	}

	/**
	 * @param businessunit the businessunit to set
	 */
	public void setBusinessunit(String businessunit) {
		this.businessunit = businessunit;
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
	 * @return the leadingvendors
	 */
	public String getLeadingvendors() {
		return leadingvendors;
	}

	/**
	 * @param leadingvendors the leadingvendors to set
	 */
	public void setLeadingvendors(String leadingvendors) {
		this.leadingvendors = leadingvendors;
	}

	/**
	 * @return the leadingamount
	 */
	public BigDecimal getLeadingamount() {
		return leadingamount;
	}

	/**
	 * @param leadingamount the leadingamount to set
	 */
	public void setLeadingamount(BigDecimal leadingamount) {
		this.leadingamount = leadingamount;
	}

	/**
	 * @return the acceptedvendors
	 */
	public Integer getAcceptedvendors() {
		return acceptedvendors;
	}

	/**
	 * @param acceptedvendors the acceptedvendors to set
	 */
	public void setAcceptedvendors(Integer acceptedvendors) {
		this.acceptedvendors = acceptedvendors;
	}

	/**
	 * @return the submitedvendors
	 */
	public Integer getSubmitedvendors() {
		return submitedvendors;
	}

	/**
	 * @param submitedvendors the submitedvendors to set
	 */
	public void setSubmitedvendors(Integer submitedvendors) {
		this.submitedvendors = submitedvendors;
	}

	/**
	 * @return the eventcategories
	 */
	public String[] getEventcategories() {
		return eventcategories;
	}

	/**
	 * @param eventcategories the eventcategories to set
	 */
	public void setEventcategories(String[] eventcategories) {
		this.eventcategories = eventcategories;
	}

	/**
	 * @return the eventIds
	 */
	public String getEventIds() {
		return eventIds;
	}

	/**
	 * @param eventIds the eventIds to set
	 */
	public void setEventIds(String eventIds) {
		this.eventIds = eventIds;
	}

	/**
	 * @return the isAllSelected
	 */
	public boolean isAllSelected() {
		return isAllSelected;
	}

	/**
	 * @param isAllSelected the isAllSelected to set
	 */
	public void setAllSelected(boolean isAllSelected) {
		this.isAllSelected = isAllSelected;
	}

	/**
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchFilterEventPojo [eventStart=" + eventStart + ", eventEnd=" + eventEnd + ", nameofevent=" + nameofevent + ", referencenumber=" + referencenumber + ", eventid=" + eventid + ", eventtype=" + eventtype + ", eventowner=" + eventowner + ", businessunit=" + businessunit + ", status=" + status + ", leadingvendors=" + leadingvendors + ", leadingamount=" + leadingamount + ", acceptedvendors=" + acceptedvendors + ", submitedvendors=" + submitedvendors + ", eventcategories=" + eventcategories + "]";
	}

}