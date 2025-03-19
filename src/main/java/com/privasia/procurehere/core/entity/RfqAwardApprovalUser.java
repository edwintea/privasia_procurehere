/**
 * 
 */
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
 * @author Aishwarya
 *
 */
@Entity
@Table(name = "PROC_RFQ_AWARD_APPR_USER")
public class RfqAwardApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6613465394076558257L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFQ_EVENT_AWARD_APPR_ID", foreignKey = @ForeignKey(name = "FK_RFQ_AWARD_APPR_USER_ID"))
	private RfqEventAwardApproval approval;
	
	public RfqAwardApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RfqAwardApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RfqAwardApprovalUser copyFrom() {
		RfqAwardApprovalUser newAppUser = new RfqAwardApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	/**
	 * @return the approval
	 */
	public RfqEventAwardApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RfqEventAwardApproval approval) {
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
		return "RfqAwardApprovalUser [" + super.toLogString() + "]";
	}


}
