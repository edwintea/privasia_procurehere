/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author pooja
 */
public class RfqEventPublishRequest extends EventPublishRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4853504889936345329L;

	@JsonProperty("TenderNo")
	private String tenderNo;

	@JsonProperty("TarikhTenderDibuka")
	private String eventStartDate;

	@JsonProperty("TarikhTenderDitutup")
	private String eventEndDate;

	@JsonProperty("TajukTender")
	private String tajukTender;

	@JsonProperty("DeskripsiTender")
	private String deskripsiTender;

	/**
	 * @return the tenderNo
	 */
	public String getTenderNo() {
		return tenderNo;
	}

	/**
	 * @param tenderNo the tenderNo to set
	 */
	public void setTenderNo(String tenderNo) {
		this.tenderNo = tenderNo;
	}

	/**
	 * @return the eventStartDate
	 */
	public String getEventStartDate() {
		return eventStartDate;
	}

	/**
	 * @param eventStartDate the eventStartDate to set
	 */
	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	/**
	 * @return the eventEndDate
	 */
	public String getEventEndDate() {
		return eventEndDate;
	}

	/**
	 * @param eventEndDate the eventEndDate to set
	 */
	public void setEventEndDate(String eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	/**
	 * @return the tajukTender
	 */
	public String getTajukTender() {
		return tajukTender;
	}

	/**
	 * @param tajukTender the tajukTender to set
	 */
	public void setTajukTender(String tajukTender) {
		this.tajukTender = tajukTender;
	}

	/**
	 * @return the deskripsiTender
	 */
	public String getDeskripsiTender() {
		return deskripsiTender;
	}

	/**
	 * @param deskripsiTender the deskripsiTender to set
	 */
	public void setDeskripsiTender(String deskripsiTender) {
		this.deskripsiTender = deskripsiTender;
	}

}