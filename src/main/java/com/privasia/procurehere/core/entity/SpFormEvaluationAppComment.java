package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_SPF_EVAL_APP_COMMENTS")
public class SpFormEvaluationAppComment extends Comments implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1436386749583120973L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVALUATOR_USER_ID", foreignKey = @ForeignKey(name = "FK_SPF_COMM_EVAL_USER_ID"))
	private SupplierPerformanceEvaluatorUser evaluatorUser;

	@Column(name = "APPROVED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean approved = false;

	@Column(name = "APPROVAL_USER_ID", length = 64, nullable = true)
	private String approvalUserId;
	
	public SpFormEvaluationAppComment() {
		this.approved = Boolean.FALSE;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the evaluatorUser
	 */
	public SupplierPerformanceEvaluatorUser getEvaluatorUser() {
		return evaluatorUser;
	}

	/**
	 * @param evaluatorUser the evaluatorUser to set
	 */
	public void setEvaluatorUser(SupplierPerformanceEvaluatorUser evaluatorUser) {
		this.evaluatorUser = evaluatorUser;
	}

	/**
	 * @return the approved
	 */
	public boolean isApproved() {
		return approved;
	}

	/**
	 * @param approved the approved to set
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	/**
	 * @return the approvalUserId
	 */
	public String getApprovalUserId() {
		return approvalUserId;
	}

	/**
	 * @param approvalUserId the approvalUserId to set
	 */
	public void setApprovalUserId(String approvalUserId) {
		this.approvalUserId = approvalUserId;
	}

}