package com.privasia.procurehere.core.entity;

/**
 * @author Priyanka Singh
 */
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
@Table(name = "PROC_RFA_SUPPLIER_TEAM")
public class RfaSupplierTeamMember extends EventTeamMember implements Serializable {

	private static final long serialVersionUID = -7275645102028806610L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_SUPPLIER_TEAM"))
	private RfaEventSupplier eventSupplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_SUP_TEAM_EVNT_ID"))
	private RfaEvent event;

	/**
	 * @return the eventSupplier
	 */
	public RfaEventSupplier getEventSupplier() {
		return eventSupplier;
	}

	/**
	 * @param eventSupplier the eventSupplier to set
	 */
	public void setEventSupplier(RfaEventSupplier eventSupplier) {
		this.eventSupplier = eventSupplier;
	}

	/**
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
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
		return super.equals(obj);
	}

	public String toLogString() {
		return "RfaSupplierTeamMember [" + super.toLogString() + "]";
	}

}
