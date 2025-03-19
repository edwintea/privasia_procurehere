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
 * @author shubham
 */
@Entity
@Table(name = "PROC_BUDGET_APPROVAL_USER")
public class BudgetApprovalUser extends ApprovalUser implements Serializable {

	private static final long serialVersionUID = -7087072177624571971L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUDGET_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_APPROVAL_USER_BUDGET_ID"))
	private BudgetApproval approval;

	public BudgetApprovalUser() {
	}

	public BudgetApprovalUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public BudgetApproval getApproval() {
		return approval;
	}

	public void setApproval(BudgetApproval approval) {
		this.approval = approval;
	}

}
