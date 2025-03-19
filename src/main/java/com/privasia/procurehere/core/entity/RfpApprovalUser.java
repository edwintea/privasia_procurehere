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

@Entity
@Table(name = "PROC_RFP_APPROVAL_USER")
public class RfpApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5013684385425526242L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFP_EVENT_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_RFP_APPROVAL_USER_AP_ID"))
	private RfpEventApproval approval;

	public RfpApprovalUser copyFrom() {
		RfpApprovalUser newAppUser = new RfpApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	public RfpApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RfpApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public RfpEventApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RfpEventApproval approval) {
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
		return "RfpApprovalUser [" + super.toLogString() + "]";
	}
}
