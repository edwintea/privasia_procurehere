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
 * @author Priyanka Singh
 */
@Entity
@Table(name = "PROC_RFQ_EVALUATOR_USER")
public class RfqEvaluatorUser extends EvaluatorUser implements Serializable {

	private static final long serialVersionUID = -2148431551930395707L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFQ_ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVAL_USER_ENV_ID"))
	private RfqEnvelop envelope;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_EVAL_USER_ID"))
	private User user;

	public RfqEvaluatorUser() {
		super();
	}

	public RfqEvaluatorUser(User user) {
		this();
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RfqEvaluatorUser copyFrom() {
		RfqEvaluatorUser user = new RfqEvaluatorUser();
		user.setUser(getUser());
		return user;
	}

	public RfqEvaluatorUser shallowCopyMoblileEvaluator() {
		RfqEvaluatorUser user = new RfqEvaluatorUser();
		user.setEvalUser(getUser().createMobileShallowCopy());
		return user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the envelope
	 */
	public RfqEnvelop getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope
	 *            the envelope to set
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		RfqEvaluatorUser other = (RfqEvaluatorUser) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String toLogString() {
		return "RfqEvaluatorUser [user=" + user + ", toLogString()=" + super.toLogString() + "]";
	}

}
