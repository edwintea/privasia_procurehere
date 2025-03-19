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
@Table(name = "PROC_RFT_MEETING_REMINDER")
public class RftEventMeetingReminder extends EventMeetingReminder implements Serializable {

	private static final long serialVersionUID = 5254892314600655363L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_MEET_REM") )
	private RftEventMeeting rfxEventMeeting;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_MEETING_REM") )
	private RftEvent rftEvent;

	public RftEventMeetingReminder() {

	}

	public RftEventMeetingReminder(Date reminderDate) {
		setReminderDate(reminderDate);
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

	/**
	 * @return the rftEvent
	 */
	public RftEvent getRftEvent() {
		return rftEvent;
	}

	/**
	 * @param rftEvent the rftEvent to set
	 */
	public void setRftEvent(RftEvent rftEvent) {
		this.rftEvent = rftEvent;
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
