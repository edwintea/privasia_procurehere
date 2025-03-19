/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
//@Entity
//@Table(name = "PROC_SUBSCRIPTION")
public class Subscription implements Serializable {

	private static final long serialVersionUID = -5604783654276601687L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUBS_BUYER_ID"))
	private Buyer buyer;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUBS_PLAN_ID"))
	private Plan plan;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "NEXT_SUBSCRIPTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUBS_NEXT_SUBS_ID"))
	private Subscription nextSubscription;

	@Column(name = "PLAN_QUANTITY", length = 6)
	private Integer planQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBSCRIPTION_STATUS")
	private SubscriptionStatus subscriptionStatus;

	@Temporal(TemporalType.DATE)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.DATE)
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

	@Column(name = "USER_LIMIT", length = 6)
	private Integer userLimit;

	@Column(name = "REMAINING_BILLING_CYCLES", length = 6)
	private Integer remainingBillingCycles;

	@Column(name = "PO_NUMBER", length = 64)
	private String poNumber;

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

	@Column(name = "INVOICE_NOTES", length = 256)
	private String invoiceNotes;

	@Column(name = "DUE_INVOICES_COUNT", length = 6)
	private Integer dueInvoicesCount;

	@Temporal(TemporalType.DATE)
	@Column(name = "DUE_SINCE")
	private Date dueSince;

	@Column(name = "TOTAL_DUES", length = 6)
	private Integer totalDues;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PAY_TRANSACTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUBS_PAY_TX_ID"))
	private PaymentTransaction paymentTransaction;

	@Column(name = "CONVERTED_TO_PAID")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean convertedToPaid = Boolean.FALSE;
	
	@Column(name = "REMINDER_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean reminderSent = Boolean.FALSE;

	public Subscription() {
		convertedToPaid = Boolean.FALSE;
		totalDues = 0;
		dueInvoicesCount = 0;
		remainingBillingCycles = 0;
		noOfEvents = 0;
		planQuantity = 0;
		reminderSent = Boolean.FALSE;
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
	public Plan getPlan() {
		return plan;
	}

	/**
	 * @param plan the plan to set
	 */
	public void setPlan(Plan plan) {
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
	 * @return the remainingBillingCycles
	 */
	public Integer getRemainingBillingCycles() {
		return remainingBillingCycles;
	}

	/**
	 * @param remainingBillingCycles the remainingBillingCycles to set
	 */
	public void setRemainingBillingCycles(Integer remainingBillingCycles) {
		this.remainingBillingCycles = remainingBillingCycles;
	}

	/**
	 * @return the poNumber
	 */
	public String getPoNumber() {
		return poNumber;
	}

	/**
	 * @param poNumber the poNumber to set
	 */
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
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
	 * @return the invoiceNotes
	 */
	public String getInvoiceNotes() {
		return invoiceNotes;
	}

	/**
	 * @param invoiceNotes the invoiceNotes to set
	 */
	public void setInvoiceNotes(String invoiceNotes) {
		this.invoiceNotes = invoiceNotes;
	}

	/**
	 * @return the dueInvoicesCount
	 */
	public Integer getDueInvoicesCount() {
		return dueInvoicesCount;
	}

	/**
	 * @param dueInvoicesCount the dueInvoicesCount to set
	 */
	public void setDueInvoicesCount(Integer dueInvoicesCount) {
		this.dueInvoicesCount = dueInvoicesCount;
	}

	/**
	 * @return the dueSince
	 */
	public Date getDueSince() {
		return dueSince;
	}

	/**
	 * @param dueSince the dueSince to set
	 */
	public void setDueSince(Date dueSince) {
		this.dueSince = dueSince;
	}

	/**
	 * @return the totalDues
	 */
	public Integer getTotalDues() {
		return totalDues;
	}

	/**
	 * @param totalDues the totalDues to set
	 */
	public void setTotalDues(Integer totalDues) {
		this.totalDues = totalDues;
	}

	/**
	 * @return the paymentTransaction
	 */
	public PaymentTransaction getPaymentTransaction() {
		return paymentTransaction;
	}

	/**
	 * @param paymentTransaction the paymentTransaction to set
	 */
	public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
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
	 * @return the nextSubscription
	 */
	public Subscription getNextSubscription() {
		return nextSubscription;
	}

	/**
	 * @param nextSubscription the nextSubscription to set
	 */
	public void setNextSubscription(Subscription nextSubscription) {
		this.nextSubscription = nextSubscription;
	}

	/**
	 * @return the reminderSent
	 */
	public Boolean getReminderSent() {
		return reminderSent;
	}

	/**
	 * @param reminderSent the reminderSent to set
	 */
	public void setReminderSent(Boolean reminderSent) {
		this.reminderSent = reminderSent;
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
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((eventLimit == null) ? 0 : eventLimit.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((invoiceNotes == null) ? 0 : invoiceNotes.hashCode());
		result = prime * result + ((planQuantity == null) ? 0 : planQuantity.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((subscriptionCancelReason == null) ? 0 : subscriptionCancelReason.hashCode());
		result = prime * result + ((subscriptionStatus == null) ? 0 : subscriptionStatus.hashCode());
		result = prime * result + ((totalDues == null) ? 0 : totalDues.hashCode());
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
		Subscription other = (Subscription) obj;
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
		if (invoiceNotes == null) {
			if (other.invoiceNotes != null)
				return false;
		} else if (!invoiceNotes.equals(other.invoiceNotes))
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
		if (totalDues == null) {
			if (other.totalDues != null)
				return false;
		} else if (!totalDues.equals(other.totalDues))
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
		return "Subscription [id=" + id + ", currencyCode=" + currencyCode + ", planQuantity=" + planQuantity + ", subscriptionStatus=" + subscriptionStatus + ", startDate=" + startDate + ", endDate=" + endDate + ", trialQuantity=" + trialQuantity + ", trialStartDate=" + trialStartDate + ", trialEndDate=" + trialEndDate + ", eventLimit=" + eventLimit + ", userLimit=" + userLimit + ", remainingBillingCycles=" + remainingBillingCycles + ", poNumber=" + poNumber + ", createdDate=" + createdDate + ", activatedDate=" + activatedDate + ", cancelledDate=" + cancelledDate + ", subscriptionCancelReason=" + subscriptionCancelReason + ", invoiceNotes=" + invoiceNotes + ", dueInvoicesCount=" + dueInvoicesCount + ", dueSince=" + dueSince + ", totalDues=" + totalDues + "]";
	}

}
