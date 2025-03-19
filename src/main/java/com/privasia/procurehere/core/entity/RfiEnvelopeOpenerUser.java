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

/**
 * @author sana
 */

@Entity
@Table(name = "PROC_RFI_ENV_OPEN_USERS")
public class RfiEnvelopeOpenerUser extends RfxEnvelopeOpenerUser implements Serializable {

	private static final long serialVersionUID = -4775003997444083084L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_ENV_OPN_USR_EVT_ID"))
	private RfiEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFI_ENV_OPN_USR_EVLP_ID"))
	private RfiEnvelop envelope;

	public RfiEnvelopeOpenerUser() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RfiEnvelopeOpenerUser(User user) {
		this.setIsOpen(Boolean.FALSE);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RfiEnvelopeOpenerUser copyFrom() {
		RfiEnvelopeOpenerUser newTm = new RfiEnvelopeOpenerUser();
		newTm.setUser(getUser());
		newTm.setEvent(getEvent());
		// newTm.setIsOpen(getIsOpen());
		// newTm.setOpenDate(getOpenDate());
		// newTm.setCloseDate(getCloseDate());
		return newTm;
	}

	/**
	 * @return the event
	 */
	public RfiEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfiEvent event) {
		this.event = event;
	}

	/**
	 * @return the envelope
	 */
	public RfiEnvelop getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope the envelope to set
	 */
	public void setEnvelope(RfiEnvelop envelope) {
		this.envelope = envelope;
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
}
