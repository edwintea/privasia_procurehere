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
@Table(name = "PROC_RFI_EVENT_TIMELINE")
public class RfiEventTimeLine extends EventTimeline implements Serializable {

	private static final long serialVersionUID = -7427744883457098078L;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_TIMELINE_EVNT_ID"))
	private RfiEvent event;

	public RfiEventTimeLine() {
	}

	public RfiEventTimeLine(Buyer buyer, RfiEvent event, Date activityDate, EventTimelineType activity, String description) {
		super(buyer, activityDate, activity, description);
		this.event = event;
	}

	/**
	 * @return the event
	 */
	public RfiEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfiEvent event) {
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
		return "RfiEventTimeLine [toLogString()=" + super.toLogString() + "]";
	}

}
