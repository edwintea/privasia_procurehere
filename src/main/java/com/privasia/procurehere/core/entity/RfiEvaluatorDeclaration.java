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
@Table(name = "PROC_RFI_EVALUATOR_DECLARATION")
public class RfiEvaluatorDeclaration extends EvaluatorDeclarationAcceptance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4650216499512114082L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFI_ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFI_DECL_ENV_ID"))
	private RfiEnvelop envelope;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFI_DECL_USER_ID"))
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_EVAL_DECL_RFI_EVENT_ID"))
	private RfiEvent event;

	public RfiEvaluatorDeclaration() {
		super();
		this.setIsLeadEvaluator(Boolean.FALSE);
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
	public RfiEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfiEvent event) {
		this.event = event;
	}
}
