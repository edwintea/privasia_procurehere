package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_FORM_APPROVAL_USER_REQ")
public class SourcingFormApprovalUserRequest extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 361213448319329479L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_SF_REQ_USER_AP_ID"))
	private SourcingFormApprovalRequest approvalRequest;

	public SourcingFormApprovalUserRequest() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public SourcingFormApprovalUserRequest(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public SourcingFormApprovalUserRequest copyFrom() {
		SourcingFormApprovalUserRequest newAppUser = new SourcingFormApprovalUserRequest();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	/**
	 * @return the approvalRequest
	 */
	public SourcingFormApprovalRequest getApprovalRequest() {
		return approvalRequest;
	}

	/**
	 * @param approvalRequest the approvalRequest to set
	 */
	public void setApprovalRequest(SourcingFormApprovalRequest approvalRequest) {
		this.approvalRequest = approvalRequest;
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
		return "SourcingFormApprovalUserRequest [" + super.toLogString() + "]";
	}
}