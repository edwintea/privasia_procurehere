package com.privasia.procurehere.core.entity;

/**
 * @author anshul
 */

import java.io.Serializable;
import java.util.Objects;

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
@Table(name = "PROC_CONTRACT_COMMENTS")
public class ContractComment extends Comments implements Serializable {

	private static final long serialVersionUID = 2880986924995708369L;

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
	@JoinColumn(name = "PRODUCT_CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_CONT_COMM_CONT_ID"))
	private ProductContract productContract;

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
	 * @return the productContract
	 */
	public ProductContract getProductContract() {
		return productContract;
	}

	/**
	 * @param productContract the productContract to set
	 */
	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContractComment other = (ContractComment) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "ContractComment [id=" + id + ", approved=" + approved + ", productContract=" + productContract + ", approvalUserId=" + approvalUserId + "]";
	}



}
