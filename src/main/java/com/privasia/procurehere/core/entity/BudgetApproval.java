package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author shubham
 */
@Entity
@Table(name = "PROC_BUDGET_APPROVAL")
public class BudgetApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = -4996988599453563390L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUDGET_ID", foreignKey = @ForeignKey(name = "FK_BUDGET_APPR_BGT_ID"))
	private Budget budget;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<BudgetApprovalUser> approvalUsers;

	@Column(name = "BATCH_NO")
	private Integer batchNo = 0;

	public Integer getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(Integer batchNo) {
		this.batchNo = batchNo;
	}

	public Budget getBudget() {
		return budget;
	}

	public void setBudget(Budget budget) {
		this.budget = budget;
	}

	public List<BudgetApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	public void setApprovalUsers(List<BudgetApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

}
