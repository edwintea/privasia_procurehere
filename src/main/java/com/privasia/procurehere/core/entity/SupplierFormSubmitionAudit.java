package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierFormSubAuditVisibilityType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionAuditType;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SUPP_FORM_SUB_AUDIT")
public class SupplierFormSubmitionAudit implements Serializable {

	private static final long serialVersionUID = 6436449561202691063L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SFS_AUDIT_ACTION_BY"))
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SFS_AUDIT_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SFS_AUDIT_SUPPLIER_ID"))
	private Supplier supplier;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private SupplierFormSubmitionAuditType action;

	@Column(name = "THE_DESCRIPTION", length = 3000)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_SUB_ID", foreignKey = @ForeignKey(name = "FK_FORM_SUB_AUDIT"))
	private SupplierFormSubmition supplierFormSubmition;

	@Enumerated(EnumType.STRING)
	@Column(name = "VISIBILITY_TYPE")
	private SupplierFormSubAuditVisibilityType visibilityType;

	public SupplierFormSubmitionAudit() {
	}

	/**
	 * @param id
	 * @param actionBy
	 * @param buyer
	 * @param actionDate
	 * @param action
	 * @param description
	 */
	public SupplierFormSubmitionAudit(String id, String actionBy, Date actionDate, SupplierFormSubmitionAuditType action, String description) {
		this.id = id;
		User user = new User();
		user.setName(actionBy);
		this.actionBy = user;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
	}

	public SupplierFormSubmitionAudit(SupplierFormSubmition supplierFormSubmition, User actionBy, Date actionDate, SupplierFormSubmitionAuditType action, String description) {
		this.supplierFormSubmition = supplierFormSubmition;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
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
	 * @return the actionBy
	 */
	public User getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
	}

	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the action
	 */
	public SupplierFormSubmitionAuditType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(SupplierFormSubmitionAuditType action) {
		this.action = action;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the visibilityType
	 */
	public SupplierFormSubAuditVisibilityType getVisibilityType() {
		return visibilityType;
	}

	/**
	 * @param visibilityType the visibilityType to set
	 */
	public void setVisibilityType(SupplierFormSubAuditVisibilityType visibilityType) {
		this.visibilityType = visibilityType;
	}

}