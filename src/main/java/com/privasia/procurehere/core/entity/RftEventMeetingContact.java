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
 * @author Ravi
 */

/**
 * @author Parveen
 */
@Entity
@Table(name = "PROC_RFT_MEETING_CONTACT")
public class RftEventMeetingContact extends EventMeetingContact implements Serializable {

	private static final long serialVersionUID = -5385547530400217417L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_MEET_CON") )
	private RftEventMeeting rfxEventMeeting;

	public RftEventMeetingContact() {
	}

	public RftEventMeetingContact(String id, String name, String email, String phone) {
		setId(id);
		setContactName(name);
		setContactEmail(email);
		setContactNumber(phone);
	}

	/**
	 * @return the rfxEventMeeting
	 */
	public RftEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RftEventMeeting rfxEventMeeting) {
		this.rfxEventMeeting = rfxEventMeeting;
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
		return "RftEventMeetingContact [ " + super.toLogString() + "]";
	}
}
