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

@Entity
@Table(name = "PROC_RFQ_MEETING_CONTACT")
public class RfqEventMeetingContact extends EventMeetingContact implements Serializable {


	private static final long serialVersionUID = -8931789006915771138L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_MEET_CON") )
	private RfqEventMeeting rfxEventMeeting;

	public RfqEventMeetingContact() {
	}

	public RfqEventMeetingContact(String id, String name, String email, String phone) {
		setId(id);
		setContactName(name);
		setContactEmail(email);
		setContactNumber(phone);
	}


	/**
	 * @return the rfxEventMeeting
	 */
	public RfqEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RfqEventMeeting rfxEventMeeting) {
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
		return "RfpEventMeetingContact [ " + toLogString() + "]";
	}
}
