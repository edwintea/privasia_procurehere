package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SUPP_FORM_APPROVAL")
public class SupplierFormApproval implements Serializable {

	private static final long serialVersionUID = -8918368695144298904L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "APPROVAL_LEVEL", length = 1, nullable = false)
	private Integer level;

	@Enumerated(EnumType.STRING)
	@Column(name = "APPROVAL_TYPE")
	private ApprovalType approvalType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_ID", foreignKey = @ForeignKey(name = "FK_FORM_APPR_ID"))
	private SupplierForm supplierForm;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<SupplierFormApprovalUser> approvalUsers;

	public SupplierFormApproval copyFrom() {
		SupplierFormApproval newApp = new SupplierFormApproval();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {
			newApp.setApprovalUsers(new ArrayList<SupplierFormApprovalUser>());
			for (SupplierFormApprovalUser appUser : getApprovalUsers()) {
				SupplierFormApprovalUser newAppUser = appUser.copyFrom();
				newApp.getApprovalUsers().add(newAppUser);
			}
		}
		return newApp;
	}

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
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the approvalType
	 */
	public ApprovalType getApprovalType() {
		return approvalType;
	}

	/**
	 * @param approvalType the approvalType to set
	 */
	public void setApprovalType(ApprovalType approvalType) {
		this.approvalType = approvalType;
	}

	/**
	 * @return the supplierForm
	 */
	public SupplierForm getSupplierForm() {
		return supplierForm;
	}

	/**
	 * @param supplierForm the supplierForm to set
	 */
	public void setSupplierForm(SupplierForm supplierForm) {
		this.supplierForm = supplierForm;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<SupplierFormApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<SupplierFormApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((approvalType == null) ? 0 : approvalType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierFormApproval other = (SupplierFormApproval) obj;
		if (approvalType != other.approvalType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplierFormApproval [id=" + id + ", level=" + level + ", approvalType=" + approvalType + ", supplierForm=" + supplierForm + "]";
	}

}
