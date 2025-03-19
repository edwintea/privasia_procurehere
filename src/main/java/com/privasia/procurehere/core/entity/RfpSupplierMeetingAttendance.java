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
@Table(name = "PROC_RFP_SUP_MEET_ATTN")
public class RfpSupplierMeetingAttendance extends SupplierMeetingAttendance implements Serializable {

	private static final long serialVersionUID = -3538532740364720587L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_SUP_MET_ATT_EVNT") , nullable = false)
	private RfpEvent rfxEvent;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFP_SUP_MET_ATT_MEET") , nullable = false)
	private RfpEventMeeting rfxEventMeeting;

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
	 * @return the rfxEventMeeting
	 */
	public RfpEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RfpEventMeeting rfxEventMeeting) {
		this.rfxEventMeeting = rfxEventMeeting;
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
		return "RfpSupplierMeetingAttendance [toLogString :" + toLogString() + "]";
	}
}
