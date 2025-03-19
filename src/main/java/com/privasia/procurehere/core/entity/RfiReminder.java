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
@Table(name = "PROC_RFI_REMINDER")
public class RfiReminder extends EventReminder implements Serializable {

	private static final long serialVersionUID = 9002379345598645877L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_REM"))
	private RfiEvent rfiEvent;

	@Column(name = "START_REMINDER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean startReminder = Boolean.FALSE;

	public RfiReminder() {
		this.startReminder = Boolean.FALSE;
	}

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
		return "RfiReminder [rfiReminder=" + super.toString() + "]";
	}

}
