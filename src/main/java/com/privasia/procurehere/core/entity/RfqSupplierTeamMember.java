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
@Table(name = "PROC_RFQ_SUPPLIER_TEAM")
public class RfqSupplierTeamMember extends EventTeamMember implements Serializable {

	private static final long serialVersionUID = -7275645102028806610L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_SUPPLIER_TEAM"))
	private RfqEventSupplier eventSupplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_SUP_TEAM_EVNT_ID"))
	private RfqEvent event;
	
	

	/**
	 * @return the eventSupplier
	 */
	public RfqEventSupplier getEventSupplier() {
		return eventSupplier;
	}



	/**
	 * @param eventSupplier the eventSupplier to set
	 */
	public void setEventSupplier(RfqEventSupplier eventSupplier) {
		this.eventSupplier = eventSupplier;
	}



	/**
	 * @return the event
	 */
	public RfqEvent getEvent() {
		return event;
	}



	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((eventSupplier == null) ? 0 : eventSupplier.hashCode());
		return result;
	}



	/* (non-Javadoc)
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
		RfqSupplierTeamMember other = (RfqSupplierTeamMember) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (eventSupplier == null) {
			if (other.eventSupplier != null)
				return false;
		} else if (!eventSupplier.equals(other.eventSupplier))
			return false;
		return true;
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfqSupplierTeamMember [" + super.toLogString() + "]";
	}

	
}
