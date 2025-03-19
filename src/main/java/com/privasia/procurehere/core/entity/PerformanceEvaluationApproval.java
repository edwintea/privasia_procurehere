package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_PER_EVALUATION_APPROVAL")
public class PerformanceEvaluationApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = -2323266220643713820L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "SP_EVAL_USER_ID", foreignKey = @ForeignKey(name = "FK_PER_EVAL_APPR_SP_EVAL_ID"))
	private SupplierPerformanceEvaluatorUser evalutorUser;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<PerformanceEvaluationApprovalUser> approvalUsers;

	/**
	 * @return the evalutorUser
	 */
	public SupplierPerformanceEvaluatorUser getEvalutorUser() {
		return evalutorUser;
	}

	/**
	 * @param evalutorUser the evalutorUser to set
	 */
	public void setEvalutorUser(SupplierPerformanceEvaluatorUser evalutorUser) {
		this.evalutorUser = evalutorUser;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<PerformanceEvaluationApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<PerformanceEvaluationApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
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

	public String toLogString() {
		return "PerformanceEvaluationApproval [ " + super.toLogString() + "]";
	}
	
}
