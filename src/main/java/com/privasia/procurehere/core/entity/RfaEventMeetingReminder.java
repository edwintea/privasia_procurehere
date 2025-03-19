package com.privasia.procurehere.core.entity;

/**
 * @author RT-Kapil
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
@Table(name = "PROC_RFA_MEETING_REMINDER")
public class RfaEventMeetingReminder extends EventMeetingReminder implements Serializable {

	private static final long serialVersionUID = -1340506080808772674L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "RFA_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_MEET_REM"))
	private RfaEventMeeting rfaEventMeeting;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_MEETING_REM"))
	private RfaEvent rfaEvent;

	public RfaEventMeetingReminder() {

	}

	public RfaEventMeetingReminder(Date reminderDate) {
		setReminderDate(reminderDate);
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
		return "RfaEventMeetingReminder [toLogString : " + super.toLogString() + "]";
	}
}
