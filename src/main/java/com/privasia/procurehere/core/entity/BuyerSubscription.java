package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionCancelReason;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.utils.CustomDateSerializer;
import org.hibernate.annotations.Type;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_BUYER_SUBSCRIPTION")
public class BuyerSubscription implements Serializable {

	private static final long serialVersionUID = -3328965603380710905L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_SUB_ID"))
	private Buyer buyer;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUBS_BUY_PLAN_ID"))
	private BuyerPlan plan;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "NEXT_SUBSCRIPTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_NEXT_SUBS_ID"))
	private BuyerSubscription nextSubscription;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RANGE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SUBS_RANGE_ID"))
	private PlanRange range;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PERIOD_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUBS_PERIOD_ID"))
	private PlanPeriod planPeriod;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_TYPE", length = 32, nullable = false)
	private PlanType planType;

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

	@Column(name = "EVENT_QUANTITY", length = 6)
	private Integer eventQuantity;

	@Column(name = "USER_QUANTITY", length = 6)
	private Integer userQuantity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVATED_DATE")
	private Date activatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CANCELLED_DATE")
	private Date cancelledDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBSCRIPTION_CANCEL_REASON")
	private SubscriptionCancelReason subscriptionCancelReason;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "buyerSubscription")
	@OrderBy("createdDate")
	private List<PaymentTransaction> paymentTransactions;

	@Column(name = "PRICE_AMOUNT", precision = 20, scale = 4, nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "PRICE_DISCOUNT", precision = 20, scale = 4, nullable = true)
	private BigDecimal priceDiscount;

	@Column(name = "PROMO_CODE_DISCOUNT", precision = 20, scale = 4, nullable = true)
	private BigDecimal promoCodeDiscount;

	@Column(name = "TOTAL_PRICE_AMOUNT", precision = 20, scale = 4, nullable = false)
	private BigDecimal totalPriceAmount;

	@Column(name = "IMMEDIATE_EFFECT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean immediateEffect;

	@Column(name = "PAYMENT_PROFILE_ID", length = 64)
	private String paymentProfileId;

	@Column(name = "AUTO_CHARGE_SUBSCRIPTION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoChargeSubscription;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RECURRING_CANCEL_DATE")
	private Date recurringCancelDate;

	@Column(name = "RECURRING_CANCEL_REMARKS", length = 2000)
	private String recurringCancelRemarks;

	@Column(name = "IS_TRIAL")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isTrial = Boolean.FALSE;

	@Transient
	Integer chargeMonths;

	public BuyerSubscription() {
		this.immediateEffect = Boolean.FALSE;
		this.isTrial = Boolean.FALSE;
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
	 * @return the plan
	 */
	public BuyerPlan getPlan() {
		return plan;
	}

	/**
	 * @param plan the plan to set
	 */
	public void setPlan(BuyerPlan plan) {
		this.plan = plan;
	}

	/**
	 * @return the nextSubscription
	 */
	public BuyerSubscription getNextSubscription() {
		return nextSubscription;
	}

	/**
	 * @param nextSubscription the nextSubscription to set
	 */
	public void setNextSubscription(BuyerSubscription nextSubscription) {
		this.nextSubscription = nextSubscription;
	}

	/**
	 * @return the range
	 */
	public PlanRange getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(PlanRange range) {
		this.range = range;
	}

	/**
	 * @return the planPeriod
	 */
	public PlanPeriod getPlanPeriod() {
		return planPeriod;
	}

	/**
	 * @param planPeriod the planPeriod to set
	 */
	public void setPlanPeriod(PlanPeriod planPeriod) {
		this.planPeriod = planPeriod;
	}

	/**
	 * @return the planType
	 */
	public PlanType getPlanType() {
		return planType;
	}

	/**
	 * @param planType the planType to set
	 */
	public void setPlanType(PlanType planType) {
		this.planType = planType;
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
	 * @return the eventQuantity
	 */
	public Integer getEventQuantity() {
		return eventQuantity;
	}

	/**
	 * @param eventQuantity the eventQuantity to set
	 */
	public void setEventQuantity(Integer eventQuantity) {
		this.eventQuantity = eventQuantity;
	}

	/**
	 * @return the userQuantity
	 */
	public Integer getUserQuantity() {
		return userQuantity;
	}

	/**
	 * @param userQuantity the userQuantity to set
	 */
	public void setUserQuantity(Integer userQuantity) {
		this.userQuantity = userQuantity;
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
	 * @return the paymentTransactions
	 */
	public List<PaymentTransaction> getPaymentTransactions() {
		return paymentTransactions;
	}

	/**
	 * @param paymentTransactions the paymentTransactions to set
	 */
	public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
		this.paymentTransactions = paymentTransactions;
	}

	/**
	 * @return the priceAmount
	 */
	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	/**
	 * @param priceAmount the priceAmount to set
	 */
	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	/**
	 * @return the priceDiscount
	 */
	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}

	/**
	 * @param priceDiscount the priceDiscount to set
	 */
	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
	}

	/**
	 * @return the promoCodeDiscount
	 */
	public BigDecimal getPromoCodeDiscount() {
		return promoCodeDiscount;
	}

	/**
	 * @param promoCodeDiscount the promoCodeDiscount to set
	 */
	public void setPromoCodeDiscount(BigDecimal promoCodeDiscount) {
		this.promoCodeDiscount = promoCodeDiscount;
	}

	/**
	 * @return the totalPriceAmount
	 */
	public BigDecimal getTotalPriceAmount() {
		return totalPriceAmount;
	}

	/**
	 * @param totalPriceAmount the totalPriceAmount to set
	 */
	public void setTotalPriceAmount(BigDecimal totalPriceAmount) {
		this.totalPriceAmount = totalPriceAmount;
	}

	/**
	 * @return the chargeMonths
	 */
	public Integer getChargeMonths() {
		return chargeMonths;
	}

	/**
	 * @param chargeMonths the chargeMonths to set
	 */
	public void setChargeMonths(Integer chargeMonths) {
		this.chargeMonths = chargeMonths;
	}

	/**
	 * @return the immediateEffect
	 */
	public Boolean getImmediateEffect() {
		return immediateEffect;
	}

	/**
	 * @param immediateEffect the immediateEffect to set
	 */
	public void setImmediateEffect(Boolean immediateEffect) {
		this.immediateEffect = immediateEffect;
	}

	/**
	 * @return the paymentProfileId
	 */
	public String getPaymentProfileId() {
		return paymentProfileId;
	}

	/**
	 * @param paymentProfileId the paymentProfileId to set
	 */
	public void setPaymentProfileId(String paymentProfileId) {
		this.paymentProfileId = paymentProfileId;
	}

	/**
	 * @return the autoChargeSubscription
	 */
	public Boolean getAutoChargeSubscription() {
		return autoChargeSubscription;
	}

	/**
	 * @param autoChargeSubscription the autoChargeSubscription to set
	 */
	public void setAutoChargeSubscription(Boolean autoChargeSubscription) {
		this.autoChargeSubscription = autoChargeSubscription;
	}

	/**
	 * @return the recurringCancelDate
	 */
	public Date getRecurringCancelDate() {
		return recurringCancelDate;
	}

	/**
	 * @param recurringCancelDate the recurringCancelDate to set
	 */
	public void setRecurringCancelDate(Date recurringCancelDate) {
		this.recurringCancelDate = recurringCancelDate;
	}

	/**
	 * @return the recurringCancelRemarks
	 */
	public String getRecurringCancelRemarks() {
		return recurringCancelRemarks;
	}

	/**
	 * @param recurringCancelRemarks the recurringCancelRemarks to set
	 */
	public void setRecurringCancelRemarks(String recurringCancelRemarks) {
		this.recurringCancelRemarks = recurringCancelRemarks;
	}

	/**
	 * @return the isTrial
	 */
	public Boolean getIsTrial() {
		return isTrial;
	}

	/**
	 * @param isTrial the isTrial to set
	 */
	public void setIsTrial(Boolean isTrial) {
		this.isTrial = isTrial;
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
		result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
		result = prime * result + ((cancelledDate == null) ? 0 : cancelledDate.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((eventQuantity == null) ? 0 : eventQuantity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((planType == null) ? 0 : planType.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((subscriptionCancelReason == null) ? 0 : subscriptionCancelReason.hashCode());
		result = prime * result + ((subscriptionStatus == null) ? 0 : subscriptionStatus.hashCode());
		result = prime * result + ((userQuantity == null) ? 0 : userQuantity.hashCode());
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
		BuyerSubscription other = (BuyerSubscription) obj;
		if (activatedDate == null) {
			if (other.activatedDate != null)
				return false;
		} else if (!activatedDate.equals(other.activatedDate))
			return false;
		if (cancelledDate == null) {
			if (other.cancelledDate != null)
				return false;
		} else if (!cancelledDate.equals(other.cancelledDate))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (eventQuantity == null) {
			if (other.eventQuantity != null)
				return false;
		} else if (!eventQuantity.equals(other.eventQuantity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (planType != other.planType)
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
		if (userQuantity == null) {
			if (other.userQuantity != null)
				return false;
		} else if (!userQuantity.equals(other.userQuantity))
			return false;
		return true;
	}

	public String toLogString() {
		return "BuyerSubscription [currencyCode=" + currencyCode + ", plan=" + plan + ", nextSubscription=" + nextSubscription + ", range=" + range + ", planPeriod=" + planPeriod + ", planType=" + planType + ", subscriptionStatus=" + subscriptionStatus + ", startDate=" + startDate + ", endDate=" + endDate + ", eventQuantity=" + eventQuantity + ", userQuantity=" + userQuantity + ", createdDate=" + createdDate + ", activatedDate=" + activatedDate + ", cancelledDate=" + cancelledDate + ", subscriptionCancelReason=" + subscriptionCancelReason + ", paymentTransactions=" + paymentTransactions + ", priceAmount=" + priceAmount + ", priceDiscount=" + priceDiscount + ", promoCodeDiscount=" + promoCodeDiscount + ", totalPriceAmount=" + totalPriceAmount + ", immediateEffect=" + immediateEffect + ", paymentProfileId=" + paymentProfileId + ", autoChargeSubscription=" + autoChargeSubscription + ", recurringCancelDate=" + recurringCancelDate + ", recurringCancelRemarks=" + recurringCancelRemarks + ", chargeMonths=" + chargeMonths + "]";
	}

}
