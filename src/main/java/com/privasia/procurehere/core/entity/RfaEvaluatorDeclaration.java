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
 * @author pooja
 */
@Entity
@Table(name = "PROC_RFA_EVALUATOR_DECLARATION")
public class RfaEvaluatorDeclaration extends EvaluatorDeclarationAcceptance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3436735581156562157L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFA_ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFA_DECL_ENV_ID"))
	private RfaEnvelop envelope;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFA_DECL_USER_ID"))
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_EVAL_DECL_RFA_EVENT_ID"))
	private RfaEvent event;

	public RfaEvaluatorDeclaration() {
		super();
		this.setIsLeadEvaluator(Boolean.FALSE);

	}

	/**
	 * @return the envelope
	 */
	public RfaEnvelop getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope the envelope to set
	 */
	public void setEnvelope(RfaEnvelop envelope) {
		this.envelope = envelope;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
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

}
