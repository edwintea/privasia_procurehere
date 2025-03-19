package com.privasia.procurehere.core.entity;

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
@Table(name = "PROC_RFT_UNMASKED_USER")
public class RftUnMaskedUser extends RfxEventUnmaskUser  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7265707417595210909L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_UNMASKED_USER"))
	private RftEvent event;
	
	
	
	
	public RftUnMaskedUser() {
	}

	public RftUnMaskedUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}
	
	
	
	/**
	 * @return the event
	 */
	public RftEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RftEvent event) {
		this.event = event;
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
	@Override
	public String toLogString() {
		return "RftEventApproval [" + super.toLogString() + "]";
	}

	public RftUnMaskedUser copyFrom() {
		RftUnMaskedUser newTm = new RftUnMaskedUser();
		newTm.setUser(getUser());
		newTm.setEvent(getEvent());
		newTm.setUserUnmasked(getUserUnmasked());
		newTm.setUserUnmaskedTime(getUserUnmaskedTime());
		return newTm;
	}

}
