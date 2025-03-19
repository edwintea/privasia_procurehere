/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFP_EVENT_CONTACTS")
public class RfpEventContact extends EventContact implements Serializable {

	private static final long serialVersionUID = -8129244870600223301L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVENT_CONTACT") )
	private RfpEvent rfxEvent;

	@Transient
	private String eventId;

	public RfpEventContact copyFrom(RfpEvent rfpEvent) {
		RfpEventContact contact = new RfpEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setRfxEvent(rfpEvent);
		contact.setTitle(getTitle());
		return contact;
	}
	
	public RfpEventContact copyFrom(RftEvent copyEvent) {
		RfpEventContact contact = new RfpEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		//contact.setRfxEvent(copyEvent);
		contact.setTitle(getTitle());
		return contact;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfpEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfpEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public String toLogString() {
		return "RfpEventContact [ " + super.toLogString() + "]";
	}

	

}
