package com.privasia.procurehere.core.entity;

/**
 * @author RT-Kapil
 */

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_RFA_REMINDER")
public class RfaReminder extends EventReminder implements Serializable {

	private static final long serialVersionUID = 1585411361572485135L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_REM"))
	private RfaEvent rfaEvent;

	@Column(name = "START_REMINDER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean startReminder = Boolean.FALSE;

	public RfaReminder() {
		this.startReminder = Boolean.FALSE;
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

	/**
	 * @return the startReminder
	 */
	public Boolean getStartReminder() {
		return startReminder;
	}

	/**
	 * @param startReminder the startReminder to set
	 */
	public void setStartReminder(Boolean startReminder) {
		this.startReminder = startReminder;
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
		return "RfaReminder [toLogString : " + super.toString() + "]";
	}

}
