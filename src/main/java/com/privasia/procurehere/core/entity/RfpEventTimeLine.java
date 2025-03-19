package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.EventTimelineType;

/**
 * @author Teja
 */
@Entity
@Table(name = "PROC_RFP_EVENT_TIMELINE")
public class RfpEventTimeLine extends EventTimeline implements Serializable {

	private static final long serialVersionUID = 891067774570531261L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFP_TIMELINE_EVNT_ID"))
	private RfpEvent event;

	public RfpEventTimeLine() {
	}

	public RfpEventTimeLine(Buyer buyer, RfpEvent event, Date activityDate, EventTimelineType activity, String description) {
		super(buyer, activityDate, activity, description);
		this.event = event;
	}

	/**
	 * @return the event
	 */
	public RfpEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfpEvent event) {
		this.event = event;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
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

	public String toLogString() {
		return "RfpEventTimeLine [toLogString()=" + super.toLogString() + "]";
	}

}
