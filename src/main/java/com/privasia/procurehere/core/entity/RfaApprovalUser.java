package com.privasia.procurehere.core.entity;

/**
 * @author Priyanka Singh
 */
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
@Table(name = "PROC_RFA_APPROVAL_USER")
public class RfaApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2272717409604539563L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFA_EVENT_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_RFA_APPROVAL_USER_AP_ID"))
	private RfaEventApproval approval;

	public RfaApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RfaApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}
	
	public RfaApprovalUser copyFrom() {
		RfaApprovalUser newAppUser = new RfaApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	/**
	 * @return the approval
	 */
	public RfaEventApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RfaEventApproval approval) {
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
		return "RfaApprovalUser  [ " + super.toLogString() + "]";
	}


}
