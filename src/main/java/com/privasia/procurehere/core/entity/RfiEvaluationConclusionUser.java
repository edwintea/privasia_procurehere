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
@Table(name = "PROC_RFI_EVAL_CON_USERS")
public class RfiEvaluationConclusionUser extends RfxEvaluationConclusionUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7265707417595210909L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVAL_CON_USER_EVT_ID"))
	private RfiEvent event;

	public RfiEvaluationConclusionUser() {
		this.setConcluded(Boolean.FALSE);
	}

	public RfiEvaluationConclusionUser(User user) {
		this.setConcluded(Boolean.FALSE);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
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
		return "RfiEvaluationConclusionUser [" + super.toLogString() + "]";
	}

	public RfiEvaluationConclusionUser copyFrom() {
		RfiEvaluationConclusionUser newTm = new RfiEvaluationConclusionUser();
		newTm.setUser(getUser());
		newTm.setEvent(getEvent());
		newTm.setConcluded(getConcluded());
		newTm.setConcludedTime(getConcludedTime());
		return newTm;
	}

}
