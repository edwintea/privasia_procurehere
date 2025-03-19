package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SUPP_FORM_SUB_APPROVAL")
public class SupplierFormSubmitionApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = -8918368695144298904L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_SUB_ID", foreignKey = @ForeignKey(name = "FK_FORM_SUB_APPR_ID"))
	private SupplierFormSubmition supplierFormSubmition;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", orphanRemoval = true, cascade = { CascadeType.ALL })
	private List<SupplierFormSubmitionApprovalUser> approvalUsers;

	@Column(name = "BATCH_NO")
	private Integer batchNo = 0;

	/**
	 * @return the supplierFormSubmition
	 */
	public SupplierFormSubmition getSupplierFormSubmition() {
		return supplierFormSubmition;
	}

	/**
	 * @param supplierFormSubmition the supplierFormSubmition to set
	 */
	public void setSupplierFormSubmition(SupplierFormSubmition supplierFormSubmition) {
		this.supplierFormSubmition = supplierFormSubmition;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<SupplierFormSubmitionApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<SupplierFormSubmitionApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	/**
	 * @return the batchNo
	 */
	public Integer getBatchNo() {
		return batchNo;
	}

	/**
	 * @param batchNo the batchNo to set
	 */
	public void setBatchNo(Integer batchNo) {
		this.batchNo = batchNo;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toLogString() {
		return "SupplierFormSubmitionApproval [" + super.toLogString() + "]";
	}

}
