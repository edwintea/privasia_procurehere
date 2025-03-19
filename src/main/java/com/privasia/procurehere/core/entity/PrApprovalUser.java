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
 * @author parveen
 */
@Entity
@Table(name = "PROC_PR_APPROVAL_USER")
public class PrApprovalUser extends ApprovalUser implements Serializable {

	private static final long serialVersionUID = 6645431526826989444L;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PR_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_PR_APPROVAL_USER_AP_ID"))
	private PrApproval approval;

	public PrApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public PrApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setId(user.getId());
			this.setUser(user);
		}
	}

	/**
	 * @return the approval
	 */
	public PrApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(PrApproval approval) {
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

	public String toLogString() {
		return "PrApprovalUser [ " + super.toLogString() + "]";
	}

}
