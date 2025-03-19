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
import com.privasia.procurehere.core.enums.GrnAuditType;
import com.privasia.procurehere.core.enums.GrnAuditVisibilityType;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_GRN_AUDIT")
public class GoodsReceiptNoteAudit implements Serializable {

	private static final long serialVersionUID = 3639409159201423978L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_AUDIT_ACTION_BY"))
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_AUDIT_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_AUDIT_SUPPLIER_ID"))
	private Supplier supplier;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private GrnAuditType action;

	@Column(name = "THE_DESCRIPTION", length = 3000)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "GRN_ID", foreignKey = @ForeignKey(name = "FK_GRN_AUDIT"))
	private GoodsReceiptNote goodsReceiptNote;

	@Enumerated(EnumType.STRING)
	@Column(name = "VISIBILITY_TYPE")
	private GrnAuditVisibilityType visibilityType;

	public GoodsReceiptNoteAudit() {
	}

	/**
	 * @param id
	 * @param actionBy
	 * @param buyer
	 * @param actionDate
	 * @param action
	 * @param description
	 */
	public GoodsReceiptNoteAudit(String id, String actionBy, Date actionDate, GrnAuditType action, String description) {
		this.id = id;
		User user = new User();
		user.setName(actionBy);
		this.actionBy = user;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
	}

	public GoodsReceiptNoteAudit(GoodsReceiptNote goodsReceiptNote, User actionBy, Date actionDate, GrnAuditType action, String description) {
		this.goodsReceiptNote = goodsReceiptNote;
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
	public GrnAuditType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(GrnAuditType action) {
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
	 * @return the goodsReceiptNote
	 */
	public GoodsReceiptNote getGoodsReceiptNote() {
		return goodsReceiptNote;
	}

	/**
	 * @param goodsReceiptNote the goodsReceiptNote to set
	 */
	public void setGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote) {
		this.goodsReceiptNote = goodsReceiptNote;
	}

	/**
	 * @return the visibilityType
	 */
	public GrnAuditVisibilityType getVisibilityType() {
		return visibilityType;
	}

	/**
	 * @param visibilityType the visibilityType to set
	 */
	public void setVisibilityType(GrnAuditVisibilityType visibilityType) {
		this.visibilityType = visibilityType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoodsReceiptNoteAudit other = (GoodsReceiptNoteAudit) obj;
		if (action != other.action)
			return false;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toLogString() {
		return "GoodsReceiptNoteAudit [id=" + id + ", actionDate=" + actionDate + ", action=" + action + ", description=" + description + "]";
	}
}