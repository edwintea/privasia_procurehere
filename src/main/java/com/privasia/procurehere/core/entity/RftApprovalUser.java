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
 * @author Priyanka Singh
 */
@Entity
@Table(name = "PROC_RFT_APPROVAL_USER")
public class RftApprovalUser extends ApprovalUser implements Serializable {

	private static final long serialVersionUID = -2148431551930395707L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFT_EVENT_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_RFT_APPROVAL_USER_AP_ID"))
	private RftEventApproval approval;

	public RftApprovalUser copyFrom() {
		RftApprovalUser newAppUser = new RftApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	public RftApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RftApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public RftEventApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RftEventApproval approval) {
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
		return "RftApprovalUser [ " + super.toLogString() + "]";
	}

}
