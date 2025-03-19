package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuditActionType;
import org.hibernate.annotations.Type;

/**
 * @author Teja
 */
@MappedSuperclass
public class EventAudit {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = true)
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true)
	private Supplier supplier;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private AuditActionType action;

	@Column(name = "THE_DESCRIPTION", length = 3000)
	private String description;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "SUMMARY_SNAPSHOT")
	private byte[] summarySnapshot;

	public EventAudit() {
	}

	public EventAudit(User actionBy, Date actionDate, AuditActionType action, String description) {
		if (actionBy != null) {
			actionBy.getLoginId();
			this.actionBy = actionBy;
		}
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
	}

	public EventAudit(Buyer buyer, User actionBy, Date actionDate, AuditActionType action, String description, byte[] summarySnapshot) {
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.buyer = buyer;
		this.summarySnapshot = summarySnapshot;
	}

	public EventAudit(Buyer buyer, User actionBy, Date actionDate, AuditActionType action, String description) {
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.buyer = buyer;
	}

	public EventAudit(Supplier supplier, User actionBy, Date actionDate, AuditActionType action, String description) {
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.supplier = supplier;
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
	public AuditActionType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(AuditActionType action) {
		this.action = action;
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
	 * @return the summarySnapshot
	 */
	public byte[] getSummarySnapshot() {
		return summarySnapshot;
	}

	/**
	 * @param summarySnapshot the summarySnapshot to set
	 */
	public void setSummarySnapshot(byte[] summarySnapshot) {
		this.summarySnapshot = summarySnapshot;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((actionBy == null) ? 0 : actionBy.hashCode());
		result = prime * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		EventAudit other = (EventAudit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (actionBy == null) {
			if (other.actionBy != null)
				return false;
		} else if (!actionBy.equals(other.actionBy))
			return false;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	public String toLogString() {
		return "EventAudit [id=" + id + ", actionDate=" + actionDate + ",action=" + action + ",description=" + description + " ]";
	}

}