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
@Table(name = "PROC_RFP_UNMASKED_USER")
public class RfpUnMaskedUser extends RfxEventUnmaskUser  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557368949839184392L;

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_UNMASKED_USER"))
	private RfpEvent event;

	
	public RfpUnMaskedUser() {
	}

	public RfpUnMaskedUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}
	
	
	/**
	 * @return the event
	 */
	public RfpEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfpEvent event) {
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
		return "RfpEventApproval [" + super.toLogString() + "]";
	}

	public RfpUnMaskedUser copyFrom() {
		RfpUnMaskedUser newTm = new RfpUnMaskedUser();
		newTm.setUser(getUser());
		newTm.setEvent(getEvent());
		newTm.setUserUnmasked(getUserUnmasked());
		newTm.setUserUnmaskedTime(getUserUnmaskedTime());
		return newTm;
	}

}
