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
@Table(name = "PROC_RFT_EVALUATOR_DECLARATION")
public class RftEvaluatorDeclaration extends EvaluatorDeclarationAcceptance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4382721319509016435L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFT_ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFT_DECL_ENV_ID"))
	private RftEnvelop envelope;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_DECL_USER_ID"))
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_EVAL_DECL_RFT_EVENT_ID"))
	private RftEvent event;

	public RftEvaluatorDeclaration() {
		super();
		this.setIsLeadEvaluator(Boolean.FALSE);
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
	public RftEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RftEvent event) {
		this.event = event;
	}

}
