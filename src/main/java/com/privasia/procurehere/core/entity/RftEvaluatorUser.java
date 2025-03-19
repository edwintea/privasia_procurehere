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
@Table(name = "PROC_RFT_EVALUATOR_USER")
public class RftEvaluatorUser extends EvaluatorUser implements Serializable {

	private static final long serialVersionUID = -2148431551930395707L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFT_ENVELOPE_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVAL_USER_ENV_ID"))
	private RftEnvelop envelope;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVAL_USER_ID"))
	private User user;

	public RftEvaluatorUser() {
		super();
	}

	public RftEvaluatorUser(User user) {
		this();
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RftEvaluatorUser copyFrom() {
		RftEvaluatorUser user = new RftEvaluatorUser();
		user.setUser(getUser());
		return user;
	}

	public RftEvaluatorUser shallowCopyMoblileEvaluator() {
		RftEvaluatorUser user = new RftEvaluatorUser();
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
	 * @return the envelop
	 */
	public RftEnvelop getEnvelop() {
		return envelope;
	}

	/**
	 * @param envelop
	 *            the envelop to set
	 */
	public void setEnvelop(RftEnvelop envelope) {
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
		RftEvaluatorUser other = (RftEvaluatorUser) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String toLogString() {
		return "RftEvaluatorUser [user=" + user + ", toLogString()=" + super.toLogString() + "]";
	}

}
