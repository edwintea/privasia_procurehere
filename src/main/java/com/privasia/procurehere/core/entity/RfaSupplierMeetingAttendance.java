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

@Entity
@Table(name = "PROC_RFA_SUP_MEET_ATTN")
public class RfaSupplierMeetingAttendance extends SupplierMeetingAttendance implements Serializable {

	private static final long serialVersionUID = -7693426432753771831L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_SUP_MET_ATT_EVNT"), nullable = false)
	private RfaEvent rfaEvent;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFA_SUP_MET_ATT_MEET"), nullable = false)
	private RfaEventMeeting rfaEventMeeting;

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
		final int prime = 31;
		int result = super.hashCode();
		return prime * result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RfaSupplierMeetingAttendance [toLogString :" + super.toLogString() + "]";
	}
}
