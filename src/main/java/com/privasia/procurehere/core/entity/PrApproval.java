package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PR_APPROVAL")
public class PrApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = -2144789931106911487L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PR_ID", foreignKey = @ForeignKey(name = "FK_PR_APPROVAL_ID"))
	private Pr pr;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<PrApprovalUser> approvalUsers;

	/**
	 * @return the pr
	 */
	public Pr getPr() {
		return pr;
	}

	/**
	 * @param pr the pr to set
	 */
	public void setPr(Pr pr) {
		this.pr = pr;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<PrApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<PrApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
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
		return "PrApproval [ " + super.toLogString() + "]";
	}

}
