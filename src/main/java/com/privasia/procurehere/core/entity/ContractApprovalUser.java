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
 * @author anshul
 */
@Entity
@Table(name = "PROC_CONTRACT_APPROVAL_USER")
public class ContractApprovalUser extends ApprovalUser implements Serializable {

	private static final long serialVersionUID = 8016910047691194857L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CONTRACT_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_CONT_APPR_USR_CONT_ID"))
	private ContractApproval approval;

	public ContractApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public ContractApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setId(user.getId());
			this.setUser(user);
		}
	}

	/**
	 * @return the approval
	 */
	public ContractApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(ContractApproval approval) {
		this.approval = approval;
	}

	@Override
	public String toString() {
		return "ContractApprovalUser [approval=" + approval + "]";
	}

	
}
