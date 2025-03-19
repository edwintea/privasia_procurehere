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
@Table(name = "PROC_RFQ_EVALUATOR_DECLARATION")
public class RfqEvaluatorDeclaration extends EvaluatorDeclarationAcceptance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4587697994380799287L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFQ_ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFQ_DECL_ENV_ID"))
	private RfqEnvelop envelope;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_DECL_USER_ID"))
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_EVAL_DECL_RFQ_EVENT_ID"))
	private RfqEvent event;

	public RfqEvaluatorDeclaration() {
		super();
		this.setIsLeadEvaluator(Boolean.FALSE);
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
	public RfqEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}

}
