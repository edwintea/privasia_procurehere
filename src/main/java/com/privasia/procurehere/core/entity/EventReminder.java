package com.privasia.procurehere.core.entity;

/**
 * @author RT-Kapil
 */

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@MappedSuperclass
public class EventReminder implements Serializable {

	private static final long serialVersionUID = -152143671195206696L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_REMINDER_DATE")
	private Date reminderDate;

	@Column(name = "REM_INTERVAL", length = 3)
	@Digits(integer = 3, fraction = 0, message = "{event.days}")
	private Integer interval;

	@Enumerated(EnumType.STRING)
	@Column(name = "INTERVAL_TYPE")
	private IntervalType intervalType;

	@Column(name = "REMINDER_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean reminderSent = Boolean.FALSE;

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
	 * @return the reminderDate
	 */
	public Date getReminderDate() {
		return reminderDate;
	}

	/**
	 * @param reminderDate the reminderDate to set
	 */
	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}

	/**
	 * @return the interval
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/**
	 * @return the intervalType
	 */
	public IntervalType getIntervalType() {
		return intervalType;
	}

	/**
	 * @param intervalType the intervalType to set
	 */
	public void setIntervalType(IntervalType intervalType) {
		this.intervalType = intervalType;
	}

	/**
	 * @return the reminderSent
	 */
	public Boolean getReminderSent() {
		return reminderSent;
	}

	/**
	 * @param reminderSent the reminderSent to set
	 */
	public void setReminderSent(Boolean reminderSent) {
		this.reminderSent = reminderSent;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reminderDate == null) ? 0 : reminderDate.hashCode());
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
		EventReminder other = (EventReminder) obj;
		if (reminderDate == null) {
			if (other.reminderDate != null)
				return false;
		} else if (!reminderDate.equals(other.reminderDate))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventReminder [reminderDate=" + reminderDate + ",id=" + id + "]";
	}

}
