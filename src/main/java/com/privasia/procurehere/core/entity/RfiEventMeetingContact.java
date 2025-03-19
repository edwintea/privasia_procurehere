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
@Table(name = "PROC_RFI_MEETING_CONTACT")
public class RfiEventMeetingContact extends EventMeetingContact implements Serializable {

	private static final long serialVersionUID = 3363749097992239191L;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade={CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_MEET_CON") )
	private RfiEventMeeting rfxEventMeeting;

	public RfiEventMeetingContact() {
	}

	public RfiEventMeetingContact(String id, String name, String email, String phone) {
		setId(id);
		setContactName(name);
		setContactEmail(email);
		setContactNumber(phone);
	}



	/**
	 * @return the rfxEventMeeting
	 */
	public RfiEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RfiEventMeeting rfxEventMeeting) {
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
		return "RfiEventMeetingContact [ " + toLogString() + "]";
	}
}
