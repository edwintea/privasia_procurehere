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
@Table(name = "PROC_RFT_REMINDER")
public class RftReminder extends EventReminder implements Serializable {

	private static final long serialVersionUID = 7152336937819836508L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_REM"))
	private RftEvent rftEvent;

	@Column(name = "START_REMINDER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean startReminder = Boolean.FALSE;

	public RftReminder() {
		this.startReminder = Boolean.FALSE;
	}

	/**
	 * @return the rftEvent
	 */
	public RftEvent getRftEvent() {
		return rftEvent;
	}

	/**
	 * @param rftEvent the rftEvent to set
	 */
	public void setRftEvent(RftEvent rftEvent) {
		this.rftEvent = rftEvent;
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
		return "RftReminder [rftReminder=" + super.toString() + "]";
	}

}
