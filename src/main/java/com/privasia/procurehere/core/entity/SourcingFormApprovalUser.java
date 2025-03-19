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
@Table(name = "PROC_FORM_APPROVAL_USER")
public class SourcingFormApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 361213448319329479L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TEMPLATE_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_SF_APP_USER_AP_ID"))
	private SourcingTemplateApproval approval;

	public SourcingFormApprovalUser() {
		setApprovalStatus(ApprovalStatus.PENDING);
	}

	public SourcingFormApprovalUser(User user) {
		setApprovalStatus(ApprovalStatus.PENDING);
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public SourcingTemplateApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(SourcingTemplateApproval approval) {
		this.approval = approval;
	}

}