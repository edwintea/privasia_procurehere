package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;

/**
 * @author yogesh
 */
public class EventTimerPojo implements Serializable {

 
	private static final long serialVersionUID = 5103915494114688646L;
	private String id;
	private Date eventStart;
	private Date eventEnd;
	private Date auctionResumeDateTime;
	private EventStatus status;
	private SupplierPerformanceFormStatus formStatus;

	public EventTimerPojo() {

	}

	public EventTimerPojo(String id, Date eventStart, Date eventEnd, EventStatus status) {
		super();
		this.id = id;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.status = status;
	}

	public EventTimerPojo(String id, Date eventStart, Date eventEnd, Date auctionResumeDateTime, EventStatus status) {
		super();
		this.id = id;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.status = status;
	}
	
	public EventTimerPojo(String id, Date eventStart, Date eventEnd, SupplierPerformanceFormStatus formStatus) {
		super();
		this.id = id;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.formStatus = formStatus;
	}

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
	 * @return the status
	 */
	public EventStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(EventStatus status) {
		this.status = status;
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
	 * @return the auctionResumeDateTime
	 */
	public Date getAuctionResumeDateTime() {
		return auctionResumeDateTime;
	}

	/**
	 * @param auctionResumeDateTime the auctionResumeDateTime to set
	 */
	public void setAuctionResumeDateTime(Date auctionResumeDateTime) {
		this.auctionResumeDateTime = auctionResumeDateTime;
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
	 * @return the formStatus
	 */
	public SupplierPerformanceFormStatus getFormStatus() {
		return formStatus;
	}

	/**
	 * @param formStatus the formStatus to set
	 */
	public void setFormStatus(SupplierPerformanceFormStatus formStatus) {
		this.formStatus = formStatus;
	}

}