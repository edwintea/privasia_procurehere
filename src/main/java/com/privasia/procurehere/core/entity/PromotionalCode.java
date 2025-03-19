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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.UsageLimitType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.utils.CustomDateSerializer;

import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PROMOTIONAL_CODE")
public class PromotionalCode implements Serializable {

	private static final long serialVersionUID = -6070040109919726940L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@Column(name = "PROMO_CODE", length = 64, nullable = false)
	private String promoCode;

	@Column(name = "PROMO_NAME", length = 250, nullable = false)
	private String promoName;

	@Column(name = "PROMO_DISCOUNT", length = 6, nullable = false)
	private Integer promoDiscount;

	@Enumerated(EnumType.STRING)
	@Column(name = "DISCOUNT_TYPE", length = 32, nullable = false)
	private ValueType discountType;

	@Column(name = "USAGE_LIMIT", length = 6, nullable = true)
	private Integer usageLimit;

	@Enumerated(EnumType.STRING)
	@Column(name = "USAGE_LIMIT_TYPE", length = 32, nullable = true)
	private UsageLimitType usageLimitType;

	@Column(name = "USAGE_COUNT", length = 6, nullable = true)
	private Integer usageCount;

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "EFFECTIVE_DATE", nullable = false)
	private Date effectiveDate;

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "EXPIRY_DATE", nullable = false)
	private Date expiryDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PROMO_CODE_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PROMO_CODE_SUPPLIER_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUPPLIER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PROMO_CODE_SUPPLIER_PLAN_ID"))
	private SupplierPlan supplierPlan;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PROMO_CODE_BUYER_PLAN_ID"))
	private BuyerPlan buyerPlan;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", length = 32, nullable = false)
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_PROMO_CODE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@Column(name = "IN_USE", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean inUse = Boolean.FALSE;

	public PromotionalCode() {
		this.inUse = Boolean.FALSE;
		this.usageCount = 0;
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
	 * @return the promoCode
	 */
	public String getPromoCode() {
		return promoCode;
	}

	/**
	 * @param promoCode the promoCode to set
	 */
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	/**
	 * @return the promoName
	 */
	public String getPromoName() {
		return promoName;
	}

	/**
	 * @param promoName the promoName to set
	 */
	public void setPromoName(String promoName) {
		this.promoName = promoName;
	}

	/**
	 * @return the promoDiscount
	 */
	public Integer getPromoDiscount() {
		return promoDiscount;
	}

	/**
	 * @param promoDiscount the promoDiscount to set
	 */
	public void setPromoDiscount(Integer promoDiscount) {
		this.promoDiscount = promoDiscount;
	}

	/**
	 * @return the discountType
	 */
	public ValueType getDiscountType() {
		return discountType;
	}

	/**
	 * @param discountType the discountType to set
	 */
	public void setDiscountType(ValueType discountType) {
		this.discountType = discountType;
	}

	/**
	 * @return the effectiveDate
	 */
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * @param effectiveDate the effectiveDate to set
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
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

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the usageLimit
	 */
	public Integer getUsageLimit() {
		return usageLimit;
	}

	/**
	 * @param usageLimit the usageLimit to set
	 */
	public void setUsageLimit(Integer usageLimit) {
		this.usageLimit = usageLimit;
	}

	/**
	 * @return the usageLimitType
	 */
	public UsageLimitType getUsageLimitType() {
		return usageLimitType;
	}

	/**
	 * @param usageLimitType the usageLimitType to set
	 */
	public void setUsageLimitType(UsageLimitType usageLimitType) {
		this.usageLimitType = usageLimitType;
	}

	/**
	 * @return the usageCount
	 */
	public Integer getUsageCount() {
		return usageCount;
	}

	/**
	 * @param usageCount the usageCount to set
	 */
	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}

	/**
	 * @return the inUse
	 */
	public Boolean getInUse() {
		return inUse;
	}

	/**
	 * @param inUse the inUse to set
	 */
	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}

	/**
	 * @return the buyerPlan
	 */
	public BuyerPlan getBuyerPlan() {
		return buyerPlan;
	}

	/**
	 * @param buyerPlan the buyerPlan to set
	 */
	public void setBuyerPlan(BuyerPlan buyerPlan) {
		this.buyerPlan = buyerPlan;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((discountType == null) ? 0 : discountType.hashCode());
		result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((promoCode == null) ? 0 : promoCode.hashCode());
		result = prime * result + ((promoDiscount == null) ? 0 : promoDiscount.hashCode());
		result = prime * result + ((promoName == null) ? 0 : promoName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		PromotionalCode other = (PromotionalCode) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (discountType != other.discountType)
			return false;
		if (effectiveDate == null) {
			if (other.effectiveDate != null)
				return false;
		} else if (!effectiveDate.equals(other.effectiveDate))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (promoCode == null) {
			if (other.promoCode != null)
				return false;
		} else if (!promoCode.equals(other.promoCode))
			return false;
		if (promoDiscount == null) {
			if (other.promoDiscount != null)
				return false;
		} else if (!promoDiscount.equals(other.promoDiscount))
			return false;
		if (promoName == null) {
			if (other.promoName != null)
				return false;
		} else if (!promoName.equals(other.promoName))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	public String toLogString() {
		return "PromotionalCode [id=" + id + ", promoCode=" + promoCode + ", promoName=" + promoName + ", promoDiscount=" + promoDiscount + ", discountType=" + discountType + ", usageLimit=" + usageLimit + ", usageLimitType=" + usageLimitType + ", usageCount=" + usageCount + ", effectiveDate=" + effectiveDate + ", expiryDate=" + expiryDate + ", status=" + status + ", createdDate=" + createdDate + ", inUse=" + inUse + "]";

	}

	public void createShallowCopy(PromotionalCode promotionalCode1) {
		this.promoCode = promotionalCode1.getPromoCode();
		this.usageLimit = promotionalCode1.getUsageLimit();
		this.usageCount = promotionalCode1.getUsageCount();
		this.discountType = promotionalCode1.getDiscountType();
		this.promoDiscount = promotionalCode1.getPromoDiscount();
	}

}
