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
@Table(name = "PROC_RFI_SUP_MEET_ATTN")
public class RfiSupplierMeetingAttendance extends SupplierMeetingAttendance implements Serializable {

	private static final long serialVersionUID = -1021420409967656205L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_SUP_MET_ATT_EVNT") , nullable = false)
	private RfiEvent rfiEvent;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFI_SUP_MET_ATT_MEET") , nullable = false)
	private RfiEventMeeting rfxEventMeeting;

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
		return "RfiSupplierMeetingAttendance [toLogString :" + toLogString() + "]";
	}
}
