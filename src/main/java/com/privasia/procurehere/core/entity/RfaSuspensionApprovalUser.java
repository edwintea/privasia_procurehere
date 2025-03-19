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
@Table(name = "PROC_RFA_SUSPENSION_APPR_USER")
public class RfaSuspensionApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3920541332308657388L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFA_EVENT_SUSP_APPR_ID", foreignKey = @ForeignKey(name = "FK_RFA_SUSP_APPR_USER_ID"))
	private RfaEventSuspensionApproval approval;
	
	public RfaSuspensionApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RfaSuspensionApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}
	
	public RfaSuspensionApprovalUser copyFrom() {
		RfaSuspensionApprovalUser newAppUser = new RfaSuspensionApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	/**
	 * @return the approval
	 */
	public RfaEventSuspensionApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RfaEventSuspensionApproval approval) {
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
		return "RfaSuspensionApprovalUser  [ " + super.toLogString() + "]";
	}
}
