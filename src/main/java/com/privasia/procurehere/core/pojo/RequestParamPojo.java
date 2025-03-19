package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.FilterTypes;

/**
 * @author parveen
 */
public class RequestParamPojo implements Serializable{

	
	private static final long serialVersionUID = 3155226418249888733L;

	private String envelopeId;

	private String eventId;

	private String key;

	private FilterTypes type;

	private String remarks;

	private boolean approve;
	
	private String currencyCode;
	
	private String startDate;
	
	private String endDate;

	/**
	 * @return the envelopeId
	 */
	public String getEnvelopeId() {
		return envelopeId;
	}

	/**
	 * @param envelopeId the envelopeId to set
	 */
	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the type
	 */
	public FilterTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(FilterTypes type) {
		this.type = type;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the approve
	 */
	public boolean isApprove() {
		return approve;
	}

	/**
	 * @param approve the approve to set
	 */
	public void setApprove(boolean approve) {
		this.approve = approve;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}