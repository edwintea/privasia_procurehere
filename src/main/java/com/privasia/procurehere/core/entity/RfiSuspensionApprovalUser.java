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
@Table(name = "PROC_RFI_SUSPENSION_APPR_USER")
public class RfiSuspensionApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1469541008455897472L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RFI_EVENT_SUSP_APPR_ID", foreignKey = @ForeignKey(name = "FK_RFI_SUSP_APPR_USER_ID"))
	private RfiEventSuspensionApproval approval;
	
	public RfiSuspensionApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public RfiSuspensionApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public RfiSuspensionApprovalUser copyFrom() {
		RfiSuspensionApprovalUser newAppUser = new RfiSuspensionApprovalUser();
		newAppUser.setUser(getUser());
		return newAppUser;
	}

	/**
	 * @return the approval
	 */
	public RfiEventSuspensionApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(RfiEventSuspensionApproval approval) {
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
		return "RfiSuspensionApprovalUser [" + super.toLogString() + "]";
	}

}
