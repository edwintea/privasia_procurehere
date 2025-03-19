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
@Table(name = "PROC_RFQ_ENV_OPEN_USERS")
public class RfqEnvelopeOpenerUser extends RfxEnvelopeOpenerUser implements Serializable {

	private static final long serialVersionUID = -4366763077410750801L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_ENV_OPN_USR_EVT_ID"))
	private RfqEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFQ_ENV_OPN_USR_EVLP_ID"))
	private RfqEnvelop envelope;

	public RfqEnvelopeOpenerUser() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RfqEnvelopeOpenerUser(User user) {
		this.setIsOpen(Boolean.FALSE);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RfqEnvelopeOpenerUser copyFrom() {
		RfqEnvelopeOpenerUser newTm = new RfqEnvelopeOpenerUser();
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
	public RfqEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}

	/**
	 * @return the envelope
	 */
	public RfqEnvelop getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope the envelope to set
	 */
	public void setEnvelope(RfqEnvelop envelope) {
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
