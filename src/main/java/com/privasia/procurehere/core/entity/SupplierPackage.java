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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SubscriptionCancelReason;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.utils.CustomDateSerializer;
import org.hibernate.annotations.Type;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_SUPPLIER_PACKAGE")
public class SupplierPackage implements Serializable {

	private static final long serialVersionUID = -3306529112871102990L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_PACK_SUPPLIER_ID"))
	private Supplier supplier;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_PACK_PLAN_ID"))
	private SupplierPlan supplierPlan;

	@Column(name = "PLAN_QUANTITY", length = 6)
	private Integer planQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBSCRIPTION_STATUS")
	private SubscriptionStatus subscriptionStatus;

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "BUYER_LIMIT", length = 6)
	private Integer buyerLimit;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVATED_DATE")
	private Date activatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CANCELLED_DATE")
	private Date cancelledDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBSCRIPTION_CANCEL_REASON")
	private SubscriptionCancelReason subscriptionCancelReason;

	@Column(name = "CONVERTED_TO_PAID")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean convertedToPaid = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONVERTED_TO_PAID_DATE")
	private Date convertedToPaidDate;

	public SupplierPackage() {
		convertedToPaid = Boolean.FALSE;
		buyerLimit = 0;
		planQuantity = 0;
	}

	public SupplierPackage(SupplierSubscription subscription) {
		this();
		this.activatedDate = subscription.getActivatedDate();
		this.cancelledDate = subscription.getCancelledDate();
		this.currencyCode = subscription.getCurrencyCode();
		this.endDate = subscription.getEndDate();
		this.supplierPlan = subscription.getSupplierPlan();
		this.planQuantity = subscription.getPlanQuantity();
		this.startDate = subscription.getStartDate();
		this.subscriptionCancelReason = subscription.getSubscriptionCancelReason();
		this.subscriptionStatus = subscription.getSubscriptionStatus();
		this.buyerLimit = subscription.getBuyerLimit();
		this.supplier = subscription.getSupplier();
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
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the supplierPlan
	 */
	public SupplierPlan getSupplierPlan() {
		return supplierPlan;
	}

	/**
	 * @param supplierPlan the supplierPlan to set
	 */
	public void setSupplierPlan(SupplierPlan supplierPlan) {
		this.supplierPlan = supplierPlan;
	}

	/**
	 * @return the planQuantity
	 */
	public Integer getPlanQuantity() {
		return planQuantity;
	}

	/**
	 * @param planQuantity the planQuantity to set
	 */
	public void setPlanQuantity(Integer planQuantity) {
		this.planQuantity = planQuantity;
	}

	/**
	 * @return the subscriptionStatus
	 */
	public SubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	/**
	 * @param subscriptionStatus the subscriptionStatus to set
	 */
	public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the buyerLimit
	 */
	public Integer getBuyerLimit() {
		return buyerLimit;
	}

	/**
	 * @param buyerLimit the buyerLimit to set
	 */
	public void setBuyerLimit(Integer buyerLimit) {
		this.buyerLimit = buyerLimit;
	}

	/**
	 * @return the activatedDate
	 */
	public Date getActivatedDate() {
		return activatedDate;
	}

	/**
	 * @param activatedDate the activatedDate to set
	 */
	public void setActivatedDate(Date activatedDate) {
		this.activatedDate = activatedDate;
	}

	/**
	 * @return the cancelledDate
	 */
	public Date getCancelledDate() {
		return cancelledDate;
	}

	/**
	 * @param cancelledDate the cancelledDate to set
	 */
	public void setCancelledDate(Date cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	/**
	 * @return the subscriptionCancelReason
	 */
	public SubscriptionCancelReason getSubscriptionCancelReason() {
		return subscriptionCancelReason;
	}

	/**
	 * @param subscriptionCancelReason the subscriptionCancelReason to set
	 */
	public void setSubscriptionCancelReason(SubscriptionCancelReason subscriptionCancelReason) {
		this.subscriptionCancelReason = subscriptionCancelReason;
	}

	/**
	 * @return the convertedToPaid
	 */
	public Boolean getConvertedToPaid() {
		return convertedToPaid;
	}

	/**
	 * @param convertedToPaid the convertedToPaid to set
	 */
	public void setConvertedToPaid(Boolean convertedToPaid) {
		this.convertedToPaid = convertedToPaid;
	}

	/**
	 * @return the convertedToPaidDate
	 */
	public Date getConvertedToPaidDate() {
		return convertedToPaidDate;
	}

	/**
	 * @param convertedToPaidDate the convertedToPaidDate to set
	 */
	public void setConvertedToPaidDate(Date convertedToPaidDate) {
		this.convertedToPaidDate = convertedToPaidDate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activatedDate == null) ? 0 : activatedDate.hashCode());
		result = prime * result + ((buyerLimit == null) ? 0 : buyerLimit.hashCode());
		result = prime * result + ((cancelledDate == null) ? 0 : cancelledDate.hashCode());
		result = prime * result + ((convertedToPaid == null) ? 0 : convertedToPaid.hashCode());
		result = prime * result + ((convertedToPaidDate == null) ? 0 : convertedToPaidDate.hashCode());
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((planQuantity == null) ? 0 : planQuantity.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((subscriptionCancelReason == null) ? 0 : subscriptionCancelReason.hashCode());
		result = prime * result + ((subscriptionStatus == null) ? 0 : subscriptionStatus.hashCode());
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
		SupplierPackage other = (SupplierPackage) obj;
		if (activatedDate == null) {
			if (other.activatedDate != null)
				return false;
		} else if (!activatedDate.equals(other.activatedDate))
			return false;
		if (buyerLimit == null) {
			if (other.buyerLimit != null)
				return false;
		} else if (!buyerLimit.equals(other.buyerLimit))
			return false;
		if (cancelledDate == null) {
			if (other.cancelledDate != null)
				return false;
		} else if (!cancelledDate.equals(other.cancelledDate))
			return false;
		if (convertedToPaid == null) {
			if (other.convertedToPaid != null)
				return false;
		} else if (!convertedToPaid.equals(other.convertedToPaid))
			return false;
		if (convertedToPaidDate == null) {
			if (other.convertedToPaidDate != null)
				return false;
		} else if (!convertedToPaidDate.equals(other.convertedToPaidDate))
			return false;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (planQuantity == null) {
			if (other.planQuantity != null)
				return false;
		} else if (!planQuantity.equals(other.planQuantity))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (subscriptionCancelReason != other.subscriptionCancelReason)
			return false;
		if (subscriptionStatus != other.subscriptionStatus)
			return false;
		return true;
	}

	/**
	 * @return
	 */
	public String toLogString() {
		return "SupplierPackage [id=" + id + ", currencyCode=" + currencyCode + ", planQuantity=" + planQuantity + ", subscriptionStatus=" + subscriptionStatus + ", startDate=" + startDate + ", endDate=" + endDate + ", buyerLimit=" + buyerLimit + ", activatedDate=" + activatedDate + ", cancelledDate=" + cancelledDate + ", subscriptionCancelReason=" + subscriptionCancelReason + ", convertedToPaid=" + convertedToPaid + ", convertedToPaidDate=" + convertedToPaidDate + "]";
	}

}
