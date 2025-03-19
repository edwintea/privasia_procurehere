/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author pooja
 */
public class EventPublishResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7588627790091723650L;

	@JsonProperty("TenderID")
	private String tenderId;

	@JsonProperty("Return")
	private String returnResponse;

	/**
	 * @return the tenderId
	 */
	public String getTenderId() {
		return tenderId;
	}

	/**
	 * @param tenderId the tenderId to set
	 */
	public void setTenderId(String tenderId) {
		this.tenderId = tenderId;
	}

	/**
	 * @return the returnResponse
	 */
	public String getReturnResponse() {
		return returnResponse;
	}

	/**
	 * @param returnResponse the returnResponse to set
	 */
	public void setReturnResponse(String returnResponse) {
		this.returnResponse = returnResponse;
	}

}