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
 * @author anshul
 */
@Entity
@Table(name = "PROC_CONTRACT_APPROVAL")
public class ContractApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = 9108301116720435343L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_CONTRACT_APP_CONT_ID"))
	private ProductContract productContract;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<ContractApprovalUser> approvalUsers;

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
	 * @return the approvalUsers
	 */
	public List<ContractApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<ContractApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

}
