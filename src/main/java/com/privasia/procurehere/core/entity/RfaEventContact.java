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
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENT_CONTACTS")
public class RfaEventContact extends EventContact implements Serializable {

	private static final long serialVersionUID = -8129856319452294614L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_CONTACT"))
	private RfaEvent rfaEvent;

	@Transient
	private String eventId;

	public RfaEventContact copyFrom(RfaEvent rfaEvent) {
		RfaEventContact contact = new RfaEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setRfaEvent(rfaEvent);
		contact.setTitle(getTitle());
		return contact;
	}

	/**
	 * @return the rfaEvent
	 */
	public RfaEvent getRfaEvent() {
		return rfaEvent;
	}

	/**
	 * @param rfaEvent the rfaEvent to set
	 */
	public void setRfaEvent(RfaEvent rfaEvent) {
		this.rfaEvent = rfaEvent;
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
		return "RfaEventContact [ " + super.toLogString() + "]";
	}

}
