/**
 * 
 */
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
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.enums.SubscriptionCancelReason;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_BUYER_PACKAGE")
public class BuyerPackage implements Serializable {

	private static final long serialVersionUID = 9093405382249063934L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_PACK_BUYER_ID"))
	private Buyer buyer;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_PACK_PLAN_ID"))
	private BuyerPlan plan;

	@Column(name = "PLAN_QUANTITY", length = 6)
	private Integer planQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBSCRIPTION_STATUS")
	private SubscriptionStatus subscriptionStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "TRIAL_QUANTITY", length = 6)
	private Integer trialQuantity;

	@Temporal(TemporalType.DATE)
	@Column(name = "TRIAL_START_DATE")
	private Date trialStartDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "TRIAL_END_DATE")
	private Date trialEndDate;

	@Column(name = "EVENT_LIMIT", length = 6)
	private Integer eventLimit;

	@Digits(integer = 9, fraction = 0, message = "{buyer.numberofevents.length}")
	@Column(name = "NO_OF_EVENTS")
	private Integer noOfEvents;

	@Digits(integer = 9, fraction = 0, message = "No of Users")
	@Column(name = "NO_OF_USERS")
	private Integer noOfUsers;

	@Column(name = "USER_LIMIT", length = 6)
	private Integer userLimit;

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

	@Column(name = "EXP_REM_BFR_30_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore30Day = Boolean.FALSE;

	@Column(name = "EXP_REM_BFR_15_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore15Day = Boolean.FALSE;

	@Column(name = "EXP_REM_BFR_7_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore7Day = Boolean.FALSE;

	@Column(name = "EXP_REM_BFR_6_MONTH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore6Month = Boolean.FALSE;

	@Column(name = "EXP_REM_BFR_3_MONTH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore3Month = Boolean.FALSE;

	@Column(name = "USER_DA_REM_BFR_2_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean userDaRemBefore2Day = Boolean.FALSE;

	@Column(name = "USER_DA_REM_BFR_30_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean userDaRemBefore30Day = Boolean.FALSE;

	@Column(name = "USER_DA_REM_BFR_15_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean userDaRemBefore15Day = Boolean.FALSE;

	@Column(name = "USER_DA_REM_BFR_7_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean userDaRemBefore7Day = Boolean.FALSE;

	@Column(name = "NO_OF_APPROVERS")
	private Integer noOfApprovers;
	
	public BuyerPackage() {
		this.convertedToPaid = Boolean.FALSE;
		this.userLimit = 0;
		this.eventLimit = 0;
		this.noOfEvents = 0;
		this.noOfUsers = 0;
		this.noOfApprovers = 0;
		this.trialQuantity = 0;
		this.planQuantity = 0;
		this.remBefore30Day = Boolean.FALSE;
		this.remBefore15Day = Boolean.FALSE;
		this.remBefore7Day = Boolean.FALSE;
		this.remBefore6Month = Boolean.FALSE;
		this.remBefore3Month = Boolean.FALSE;
		this.userDaRemBefore2Day = Boolean.FALSE;
		this.userDaRemBefore30Day = Boolean.FALSE;
		this.userDaRemBefore15Day = Boolean.FALSE;
		this.userDaRemBefore7Day = Boolean.FALSE;
	}

	/*
	 * public BuyerPackage(Subscription subscription) {
	 * this();
	 * this.activatedDate = subscription.getActivatedDate();
	 * this.cancelledDate = subscription.getCancelledDate();
	 * this.convertedToPaid = subscription.getConvertedToPaid();
	 * this.currencyCode = subscription.getCurrencyCode();
	 * this.endDate = subscription.getEndDate();
	 * this.eventLimit = subscription.getEventLimit();
	 * this.noOfEvents = subscription.getNoOfEvents();
	 * if (noOfEvents == null) {
	 * noOfEvents = 0;
	 * }
	 * this.noOfUsers = 1; // Admin account is counted as 1 account
	 * this.plan = subscription.getPlan();
	 * this.planQuantity = subscription.getPlanQuantity();
	 * this.startDate = subscription.getStartDate();
	 * this.subscriptionCancelReason = subscription.getSubscriptionCancelReason();
	 * this.subscriptionStatus = subscription.getSubscriptionStatus();
	 * this.trialEndDate = subscription.getTrialEndDate();
	 * this.trialQuantity = subscription.getTrialQuantity();
	 * this.trialStartDate = subscription.getTrialStartDate();
	 * this.userLimit = subscription.getUserLimit();
	 * this.buyer = subscription.getBuyer();
	 * }
	 */
	/*
	 * public BuyerPackage(BuyerSubscription subscription) {
	 * this();
	 * this.activatedDate = subscription.getActivatedDate();
	 * this.cancelledDate = subscription.getCancelledDate();
	 * this.currencyCode = subscription.getCurrencyCode();
	 * this.endDate = subscription.getEndDate();
	 * this.eventLimit = subscription.getEventQuantity();
	 * this.noOfEvents = 0;
	 * this.noOfUsers = 1; // Admin account is counted as 1 account
	 * this.plan = subscription.getPlan();
	 * this.startDate = subscription.getStartDate();
	 * this.subscriptionCancelReason = subscription.getSubscriptionCancelReason();
	 * this.subscriptionStatus = subscription.getSubscriptionStatus();
	 * this.userLimit = subscription.getUserQuantity();
	 * this.buyer = subscription.getBuyer();
	 * }
	 */

	public BuyerPackage(BuyerSubscription subscription) {
		this();
		this.activatedDate = subscription.getActivatedDate();
		this.cancelledDate = subscription.getCancelledDate();
		this.currencyCode = subscription.getCurrencyCode();
		this.endDate = subscription.getEndDate();
		this.eventLimit = subscription.getEventQuantity();
		this.noOfEvents = 0;
		this.noOfUsers = 1; // Admin account is counted as 1 account
		this.plan = subscription.getPlan();
		this.startDate = subscription.getStartDate();
		this.subscriptionCancelReason = subscription.getSubscriptionCancelReason();
		this.subscriptionStatus = subscription.getSubscriptionStatus();
		this.userLimit = subscription.getUserQuantity();
		this.buyer = subscription.getBuyer();
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
	 * @return the trialQuantity
	 */
	public Integer getTrialQuantity() {
		return trialQuantity;
	}

	/**
	 * @param trialQuantity the trialQuantity to set
	 */
	public void setTrialQuantity(Integer trialQuantity) {
		this.trialQuantity = trialQuantity;
	}

	/**
	 * @return the trialStartDate
	 */
	public Date getTrialStartDate() {
		return trialStartDate;
	}

	/**
	 * @param trialStartDate the trialStartDate to set
	 */
	public void setTrialStartDate(Date trialStartDate) {
		this.trialStartDate = trialStartDate;
	}

	/**
	 * @return the trialEndDate
	 */
	public Date getTrialEndDate() {
		return trialEndDate;
	}

	/**
	 * @param trialEndDate the trialEndDate to set
	 */
	public void setTrialEndDate(Date trialEndDate) {
		this.trialEndDate = trialEndDate;
	}

	/**
	 * @return the eventLimit
	 */
	public Integer getEventLimit() {
		return eventLimit;
	}

	/**
	 * @param eventLimit the eventLimit to set
	 */
	public void setEventLimit(Integer eventLimit) {
		this.eventLimit = eventLimit;
	}

	/**
	 * @return the noOfEvents
	 */
	public Integer getNoOfEvents() {
		return noOfEvents;
	}

	/**
	 * @param noOfEvents the noOfEvents to set
	 */
	public void setNoOfEvents(Integer noOfEvents) {
		this.noOfEvents = noOfEvents;
	}

	/**
	 * @return the userLimit
	 */
	public Integer getUserLimit() {
		return userLimit;
	}

	/**
	 * @param userLimit the userLimit to set
	 */
	public void setUserLimit(Integer userLimit) {
		this.userLimit = userLimit;
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

	/**
	 * @return the noOfUsers
	 */
	public Integer getNoOfUsers() {
		return noOfUsers;
	}

	/**
	 * @param noOfUsers the noOfUsers to set
	 */
	public void setNoOfUsers(Integer noOfUsers) {
		this.noOfUsers = noOfUsers;
	}

	/**
	 * @return the remBefore30Day
	 */
	public Boolean getRemBefore30Day() {
		return remBefore30Day;
	}

	/**
	 * @param remBefore30Day the remBefore30Day to set
	 */
	public void setRemBefore30Day(Boolean remBefore30Day) {
		this.remBefore30Day = remBefore30Day;
	}

	/**
	 * @return the remBefore15Day
	 */
	public Boolean getRemBefore15Day() {
		return remBefore15Day;
	}

	/**
	 * @param remBefore15Day the remBefore15Day to set
	 */
	public void setRemBefore15Day(Boolean remBefore15Day) {
		this.remBefore15Day = remBefore15Day;
	}

	/**
	 * @return the remBefore7Day
	 */
	public Boolean getRemBefore7Day() {
		return remBefore7Day;
	}

	/**
	 * @param remBefore7Day the remBefore7Day to set
	 */
	public void setRemBefore7Day(Boolean remBefore7Day) {
		this.remBefore7Day = remBefore7Day;
	}

	/**
	 * @return the userDaRemBefore2Day
	 */
	public Boolean getUserDaRemBefore2Day() {
		return userDaRemBefore2Day;
	}

	/**
	 * @param userDaRemBefore2Day the userDaRemBefore2Day to set
	 */
	public void setUserDaRemBefore2Day(Boolean userDaRemBefore2Day) {
		this.userDaRemBefore2Day = userDaRemBefore2Day;
	}

	/**
	 * @return the remBefore6Month
	 */
	public Boolean getRemBefore6Month() {
		return remBefore6Month;
	}

	/**
	 * @param remBefore6Month the remBefore6Month to set
	 */
	public void setRemBefore6Month(Boolean remBefore6Month) {
		this.remBefore6Month = remBefore6Month;
	}

	/**
	 * @return the remBefore3Month
	 */
	public Boolean getRemBefore3Month() {
		return remBefore3Month;
	}

	/**
	 * @param remBefore3Month the remBefore3Month to set
	 */
	public void setRemBefore3Month(Boolean remBefore3Month) {
		this.remBefore3Month = remBefore3Month;
	}

	/**
	 * @return the userDaRemBefore30Day
	 */
	public Boolean getUserDaRemBefore30Day() {
		return userDaRemBefore30Day;
	}

	/**
	 * @param userDaRemBefore30Day the userDaRemBefore30Day to set
	 */
	public void setUserDaRemBefore30Day(Boolean userDaRemBefore30Day) {
		this.userDaRemBefore30Day = userDaRemBefore30Day;
	}

	/**
	 * @return the userDaRemBefore15Day
	 */
	public Boolean getUserDaRemBefore15Day() {
		return userDaRemBefore15Day;
	}

	/**
	 * @param userDaRemBefore15Day the userDaRemBefore15Day to set
	 */
	public void setUserDaRemBefore15Day(Boolean userDaRemBefore15Day) {
		this.userDaRemBefore15Day = userDaRemBefore15Day;
	}

	/**
	 * @return the userDaRemBefore7Day
	 */
	public Boolean getUserDaRemBefore7Day() {
		return userDaRemBefore7Day;
	}

	/**
	 * @param userDaRemBefore7Day the userDaRemBefore7Day to set
	 */
	public void setUserDaRemBefore7Day(Boolean userDaRemBefore7Day) {
		this.userDaRemBefore7Day = userDaRemBefore7Day;
	}

	/**
	 * @return the noOfApprovers
	 */
	public Integer getNoOfApprovers() {
		return noOfApprovers;
	}

	/**
	 * @param noOfApprovers the noOfApprovers to set
	 */
	public void setNoOfApprovers(Integer noOfApprovers) {
		this.noOfApprovers = noOfApprovers;
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
		result = prime * result + ((cancelledDate == null) ? 0 : cancelledDate.hashCode());
		result = prime * result + ((convertedToPaid == null) ? 0 : convertedToPaid.hashCode());
		result = prime * result + ((convertedToPaidDate == null) ? 0 : convertedToPaidDate.hashCode());
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((eventLimit == null) ? 0 : eventLimit.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((noOfEvents == null) ? 0 : noOfEvents.hashCode());
		result = prime * result + ((noOfUsers == null) ? 0 : noOfUsers.hashCode());
		result = prime * result + ((planQuantity == null) ? 0 : planQuantity.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((subscriptionCancelReason == null) ? 0 : subscriptionCancelReason.hashCode());
		result = prime * result + ((subscriptionStatus == null) ? 0 : subscriptionStatus.hashCode());
		result = prime * result + ((trialEndDate == null) ? 0 : trialEndDate.hashCode());
		result = prime * result + ((trialQuantity == null) ? 0 : trialQuantity.hashCode());
		result = prime * result + ((trialStartDate == null) ? 0 : trialStartDate.hashCode());
		result = prime * result + ((userLimit == null) ? 0 : userLimit.hashCode());
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
		BuyerPackage other = (BuyerPackage) obj;
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
		if (eventLimit == null) {
			if (other.eventLimit != null)
				return false;
		} else if (!eventLimit.equals(other.eventLimit))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (noOfEvents == null) {
			if (other.noOfEvents != null)
				return false;
		} else if (!noOfEvents.equals(other.noOfEvents))
			return false;
		if (noOfUsers == null) {
			if (other.noOfUsers != null)
				return false;
		} else if (!noOfUsers.equals(other.noOfUsers))
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
		if (trialEndDate == null) {
			if (other.trialEndDate != null)
				return false;
		} else if (!trialEndDate.equals(other.trialEndDate))
			return false;
		if (trialQuantity == null) {
			if (other.trialQuantity != null)
				return false;
		} else if (!trialQuantity.equals(other.trialQuantity))
			return false;
		if (trialStartDate == null) {
			if (other.trialStartDate != null)
				return false;
		} else if (!trialStartDate.equals(other.trialStartDate))
			return false;
		if (userLimit == null) {
			if (other.userLimit != null)
				return false;
		} else if (!userLimit.equals(other.userLimit))
			return false;
		return true;
	}

	public String toLogString() {
		return "BuyerPackage [id=" + id + ", currencyCode=" + currencyCode + ", plan=" + plan + ", planQuantity=" + planQuantity + ", subscriptionStatus=" + subscriptionStatus + ", startDate=" + startDate + ", endDate=" + endDate + ", trialQuantity=" + trialQuantity + ", trialStartDate=" + trialStartDate + ", trialEndDate=" + trialEndDate + ", eventLimit=" + eventLimit + ", noOfEvents=" + noOfEvents + ", userLimit=" + userLimit + ", activatedDate=" + activatedDate + ", cancelledDate=" + cancelledDate + ", subscriptionCancelReason=" + subscriptionCancelReason + ", convertedToPaid=" + convertedToPaid + ", convertedToPaidDate=" + convertedToPaidDate + "]";
	}

}
