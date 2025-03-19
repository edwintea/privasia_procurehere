package com.privasia.procurehere.core.entity;

/**
 * @author pooja
 */

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_SUPP_FORM_COMMENTS")
public class SupplierFormSubmitionComment extends Comments implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6914596082825911864L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@Column(name = "APPROVED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean approved = false;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORM_ID", foreignKey = @ForeignKey(name = "FK_SUPP_FORM_COMMENT_ID"))
	private SupplierFormSubmition supplierFormSubmition;

	@Column(name = "APPROVAL_USER_ID", length = 64, nullable = true)
	private String approvalUserId;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

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
	 * @return the approved
	 */
	public boolean isApproved() {
		return approved;
	}

	/**
	 * @param approved the approved to set
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	/**
	 * @return the approvalUserId
	 */
	public String getApprovalUserId() {
		return approvalUserId;
	}

	/**
	 * @param approvalUserId the approvalUserId to set
	 */
	public void setApprovalUserId(String approvalUserId) {
		this.approvalUserId = approvalUserId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierFormSubmitionComment other = (SupplierFormSubmitionComment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toLogString() {
		return "FormComment [comment=" + getComment() + ", createdDate=" + getCreatedDate() + "]";
	}

}
