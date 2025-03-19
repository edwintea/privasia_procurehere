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
@Table(name = "PROC_RFT_SUSPENSION_APPR_USER")
public class RftSuspensionApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8798495162095855380L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFT_EVENT_SUSP_APPR_ID", foreignKey = @ForeignKey(name = "FK_RFT_SUSP_APPR_USER_ID"))
	private RftEventSuspensionApproval approval;
	
	public RftSuspensionApprovalUser copyFrom() {
		RftSuspensionApprovalUser newAppUser = new RftSuspensionApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	public RftSuspensionApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RftSuspensionApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public RftEventSuspensionApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RftEventSuspensionApproval approval) {
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
		return "RftSuspensionApprovalUser [ " + super.toLogString() + "]";
	}

}
