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
@Table(name = "PROC_RFQ_TEAM")
public class RfqTeamMember extends EventTeamMember implements Serializable {

	private static final long serialVersionUID = 7889402061260814774L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_TEAM"))
	private RfqEvent event;

	/**
	 * @return the event
	 */
	public RfqEvent getEvent() {
		return event;
	}

	public RfqTeamMember copyFrom() {
		RfqTeamMember newTm = new RfqTeamMember();
		newTm.setUser(getUser());
		newTm.setTeamMemberType(getTeamMemberType());
		return newTm;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((event == null) ? 0 : event.hashCode());
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
		/*
		 * RfqTeamMember other = (RfqTeamMember) obj; if (event == null) { if (other.event != null) return false; } else
		 * if (!event.equals(other.event)) return false;
		 */
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfqTeamMember [" + super.toLogString() + "]";
	}

}
