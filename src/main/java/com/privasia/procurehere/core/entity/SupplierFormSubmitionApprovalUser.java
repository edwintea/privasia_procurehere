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

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SUPP_FORM_SUB_APPR_USER")
public class SupplierFormSubmitionApprovalUser extends ApprovalUser implements Serializable {

	private static final long serialVersionUID = 5798726467150061956L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_SUB_APPR_ID", foreignKey = @ForeignKey(name = "FK_APPR_USER_SUB_FORM_ID"))
	private SupplierFormSubmitionApproval approval;

	public SupplierFormSubmitionApprovalUser() {
	}

	public SupplierFormSubmitionApprovalUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the approval
	 */
	public SupplierFormSubmitionApproval getApproval() {
		return approval;
	}

	/**
	 * @param approval the approval to set
	 */
	public void setApproval(SupplierFormSubmitionApproval approval) {
		this.approval = approval;
	}

	public String toLogString() {
		return "SupplierFormSubmitionApprovalUser [" + super.toLogString() + "]";
	}
}
