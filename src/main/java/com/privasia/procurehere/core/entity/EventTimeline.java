package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventTimelineType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Teja
 */
@MappedSuperclass
public class EventTimeline {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = true)
	private Buyer buyer;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "ACTIVITY_DATE", nullable = false)
	private Date activityDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTIVITY_TYPE", nullable = false)
	private EventTimelineType activity;

	@Column(name = "THE_DESCRIPTION", length = 3000)
	private String description;

	public EventTimeline() {
	}

	public EventTimeline(Date activityDate, EventTimelineType activity, String description) {
		this.activityDate = activityDate;
		this.activity = activity;
		this.description = description;
	}

	public EventTimeline(Buyer buyer, Date activityDate, EventTimelineType activity, String description) {
		this.activityDate = activityDate;
		this.activity = activity;
		this.description = description;
		this.buyer = buyer;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the activityDate
	 */
	public Date getActivityDate() {
		return activityDate;
	}

	/**
	 * @param activityDate the activityDate to set
	 */
	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	/**
	 * @return the activity
	 */
	public EventTimelineType getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(EventTimelineType activity) {
		this.activity = activity;
	}

	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((activityDate == null) ? 0 : activityDate.hashCode());
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventTimeline other = (EventTimeline) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (activityDate == null) {
			if (other.activityDate != null)
				return false;
		} else if (!activityDate.equals(other.activityDate))
			return false;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	public String toLogString() {
		return "EventTimeline [id=" + id + ", activityDate =" + activityDate + ",activity=" + activity + ",description=" + description + " ]";
	}

}