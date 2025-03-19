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
@Table(name = "PROC_RFT_ENV_OPEN_USERS")
public class RftEnvelopeOpenerUser extends RfxEnvelopeOpenerUser implements Serializable {

	private static final long serialVersionUID = -618610263535934505L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_ENV_OPN_USR_EVT_ID"))
	private RftEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFT_ENV_OPN_USR_EVLP_ID"))
	private RftEnvelop envelope;

	public RftEnvelopeOpenerUser() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RftEnvelopeOpenerUser(User user) {
		this.setIsOpen(Boolean.FALSE);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RftEnvelopeOpenerUser copyFrom() {
		RftEnvelopeOpenerUser newTm = new RftEnvelopeOpenerUser();
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
	public RftEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RftEvent event) {
		this.event = event;
	}

	/**
	 * @return the envelope
	 */
	public RftEnvelop getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope the envelope to set
	 */
	public void setEnvelope(RftEnvelop envelope) {
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
