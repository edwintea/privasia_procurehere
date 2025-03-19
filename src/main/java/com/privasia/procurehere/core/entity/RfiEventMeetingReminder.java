package com.privasia.procurehere.core.entity;

/**
 * @author parveen
 */

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PROC_RFI_MEETING_REMINDER")
public class RfiEventMeetingReminder extends EventMeetingReminder implements Serializable {

	private static final long serialVersionUID = -4170737327636335458L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_MEET_REM") )
	private RfiEventMeeting rfxEventMeeting;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_MEETING_REM") )
	private RfiEvent rfiEvent;

	public RfiEventMeetingReminder() {

	}

	public RfiEventMeetingReminder(Date reminderDate) {
		setReminderDate(reminderDate);
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

	/**
	 * @return the rfiEvent
	 */
	public RfiEvent getRfiEvent() {
		return rfiEvent;
	}

	/**
	 * @param rfiEvent the rfiEvent to set
	 */
	public void setRfiEvent(RfiEvent rfiEvent) {
		this.rfiEvent = rfiEvent;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RftEventMeetingReminder [ " + toLogString() + "]";
	}
}
