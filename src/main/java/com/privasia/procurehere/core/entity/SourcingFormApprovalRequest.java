package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_APP_REQ")
public class SourcingFormApprovalRequest extends EventApproval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5824992894434278951L;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_ID", foreignKey = @ForeignKey(name = "FK_SF_REQ_APPROVAL_ID"))
	private SourcingFormRequest sourcingFormRequest;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approvalRequest", orphanRemoval = true, cascade = { CascadeType.ALL })
	private List<SourcingFormApprovalUserRequest> approvalUsersRequest;

	@Column(name = "BATCH_NO")
	private Integer batchNo = 0;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	public SourcingFormApprovalRequest copyFrom() {
		SourcingFormApprovalRequest newApp = new SourcingFormApprovalRequest();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		newApp.setBatchNo(getBatchNo());
		if (CollectionUtil.isNotEmpty(getApprovalUsersRequest())) {
			newApp.setApprovalUsersRequest(new ArrayList<SourcingFormApprovalUserRequest>());
			for (SourcingFormApprovalUserRequest appUser : getApprovalUsersRequest()) {
				SourcingFormApprovalUserRequest newAppUser = appUser.copyFrom();
				newApp.getApprovalUsersRequest().add(newAppUser);
			}
		}
		return newApp;
	}

	/**
	 * @return the sourcingFormRequest
	 */
	public SourcingFormRequest getSourcingFormRequest() {
		return sourcingFormRequest;
	}

	/**
	 * @param sourcingFormRequest the sourcingFormRequest to set
	 */
	public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
	}

	/**
	 * @return the approvalUsersRequest
	 */
	public List<SourcingFormApprovalUserRequest> getApprovalUsersRequest() {
		return approvalUsersRequest;
	}

	/**
	 * @param approvalUsersRequest the approvalUsersRequest to set
	 */
	public void setApprovalUsersRequest(List<SourcingFormApprovalUserRequest> approvalUsersRequest) {
		this.approvalUsersRequest = approvalUsersRequest;
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

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
	@Override
	public String toLogString() {
		return "SourcingFormApprovalRequest [" + super.toLogString() + "]";
	}

}
