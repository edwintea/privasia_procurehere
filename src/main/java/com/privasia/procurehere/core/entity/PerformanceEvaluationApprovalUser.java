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
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_PER_EVAL_APPROVAL_USER")
public class PerformanceEvaluationApprovalUser extends ApprovalUser implements Serializable {

	private static final long serialVersionUID = 8525332085696089433L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PER_EVALUATION_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_PER_EVAL_APPR_USER_APP_ID"))
	private PerformanceEvaluationApproval approval;
	
	public PerformanceEvaluationApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public PerformanceEvaluationApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public PerformanceEvaluationApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(PerformanceEvaluationApproval approval) {
		this.approval = approval;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toLogString() {
		return "PerformanceEvaluationApprovalUser  [ " + super.toLogString() + "]";
	}
	
	
}
