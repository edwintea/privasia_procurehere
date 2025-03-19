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
@Table(name = "PROC_RFP_REMINDER")
public class RfpReminder extends EventReminder implements Serializable {

	private static final long serialVersionUID = 7152336937819836508L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVENT_REM"))
	private RfpEvent rfxEvent;

	@Column(name = "START_REMINDER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean startReminder = Boolean.FALSE;

	public RfpReminder() {
		this.startReminder = Boolean.FALSE;
	}

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
		return "RfpReminder [rfpReminder=" + super.toString() + "]";
	}

}
