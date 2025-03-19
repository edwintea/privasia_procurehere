/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_MEETING_CONTACT")
public class RfaEventMeetingContact extends EventMeetingContact implements Serializable {

	private static final long serialVersionUID = -6024947488055281475L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "RFA_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_MEET_CON"))
	private RfaEventMeeting rfaEventMeeting;

	public RfaEventMeetingContact() {
	}

	public RfaEventMeetingContact(String id, String name, String email, String phone) {
		setId(id);
		setContactName(name);
		setContactEmail(email);
		setContactNumber(phone);
	}

	/**
	 * @return the rfaEventMeeting
	 */
	public RfaEventMeeting getRfaEventMeeting() {
		return rfaEventMeeting;
	}

	/**
	 * @param rfaEventMeeting the rfaEventMeeting to set
	 */
	public void setRfaEventMeeting(RfaEventMeeting rfaEventMeeting) {
		this.rfaEventMeeting = rfaEventMeeting;
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
		return "RfaEventMeetingContact [ toLogString :" + super.toLogString() + "]";
	}
}
