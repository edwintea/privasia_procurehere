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
 * @author jayshree
 *
 */
@Entity
@Table(name = "PROC_RFP_SUSPENSION_APPR_USER")
public class RfpSuspensionApprovalUser extends ApprovalUser implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1446192044032278992L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFP_EVENT_SUSP_APPR_ID", foreignKey = @ForeignKey(name = "FK_RFP_SUSP_APPR_USER_ID"))
	private RfpEventSuspensionApproval approval;
	
	public RfpSuspensionApprovalUser copyFrom() {
		RfpSuspensionApprovalUser newAppUser = new RfpSuspensionApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	public RfpSuspensionApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RfpSuspensionApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public RfpEventSuspensionApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RfpEventSuspensionApproval approval) {
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
		return "RfpSuspensionApprovalUser [" + super.toLogString() + "]";
	}

}
