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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */
@MappedSuperclass
public class EventSupplier implements Serializable {

	private static final long serialVersionUID = 6345427046038121583L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true)
	private Supplier supplier;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PREVIEW_TIME")
	private Date previewTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUPPLIER_INVITED_TIME")
	private Date supplierInvitedTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUPPLIER_EVENT_READ_TIME")
	private Date supplierEventReadTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUPPLIER_BID_SUBMITTED_TIME")
	private Date supplierSubmittedTime;

	@Column(name = "IS_BID_SUBMITTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean submitted = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REJECTED_TIME")
	private Date rejectedTime;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "ACCEPTED_BY", nullable = true)
	private User acceptedBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "REJECTED_BY", nullable = true)
	private User rejectedBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUBMITED_BY", nullable = true)
	private User subbmitedBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBMISSION_STATUS")
	private SubmissionStatusType submissionStatus;

	@Column(name = "IS_DISQUALIFY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean disqualify = Boolean.FALSE;

	@Size(min = 0, max = 500)
	@Column(name = "DISQUALIFY_REMARKS", length = 500)
	private String disqualifyRemarks;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DISQUALIFIED_TIME")
	private Date disqualifiedTime;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "DISQUALIFIED_BY", nullable = true)
	private User disqualifiedBy;

	@Column(name = "NUMBER_OF_BIDS", length = 4)
	@Digits(integer = 4, fraction = 0)
	private Integer numberOfBids = 0;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUPP_AUCTION_ONLINE_TIME")
	private Date supplierAuctionOnlineTime;

	@Column(name = "NOTIFICATION_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean notificationSent = Boolean.FALSE;

	@Column(name = "CONFIRM_PRICE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean confirmPrice = Boolean.FALSE;

	@Column(name = "CONFIRM_PRICE_DATE")
	private Date confirmPriceDate;

	@Column(name = "IP_ADDRESS", length = 50, nullable = true)
	private String ipAddress;

	@Column(name = "FEE_PAID")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean feePaid = Boolean.FALSE;

	@Column(name = "DEPOSIT_PAID")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean depositPaid = Boolean.FALSE;

	@Column(name = "FEE_PAID_DATE")
	private Date feePaidDate;

	@Column(name = "DEPOSIT_PAID_DATE")
	private Date depositPaidDate;

	@Column(name = "FEE_REFERENCE", length = 150)
	private String feeReference;

	@Column(name = "DEPOSIT_REFERENCE", length = 150)
	private String depositReference;

	@Column(name = "IS_SELF_INVITED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean selfInvited = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUALIFIED_TIME", nullable = true)
	private Date reQualifiedTime;

	@Column(name = "FEE_REFERENCE_CLIENT_ID", length = 250)
	private String feeReferenceClientId;

	@Size(min = 0, max = 200)
	@Column(name = "REJECTION_REMARKS", length = 200)
	private String rejectionRemarks;

	@Column(name = "IS_REJECTED_AFTER_START")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isRejectedAfterStart = Boolean.FALSE;

	@Transient
	private String supplierCompanyName;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean revisedBidSubmitted = Boolean.FALSE;

	@Transient
	private Date feePaidTime;

	@Transient
	private Date depositPaidTime;

	public EventSupplier createShallowCopy() {
		EventSupplier supplier = new EventSupplier();
		supplier.setId(getId());
		Supplier obj = new Supplier();
		obj.setId(getSupplier().getId());
		obj.setCommunicationEmail(getSupplier().getCommunicationEmail());
		obj.setCompanyName(getSupplier().getCompanyName());
		obj.setCompanyContactNumber(getSupplier().getCompanyContactNumber());
		supplier.setSupplier(obj);
		return supplier;
	}

	public EventSupplier createMobileShallowCopy() {
		EventSupplier supplier = new EventSupplier();
		supplier.setSupplierCompanyName(getSupplier() != null ? getSupplier().getCompanyName() : null);
		supplier.setSupplierInvitedTime(null);
		supplier.setPreviewTime(getPreviewTime());
		supplier.setSupplierEventReadTime(getSupplierEventReadTime());
		supplier.setSupplierSubmittedTime(getSupplierSubmittedTime());
		supplier.setRevisedBidSubmitted(getRevisedBidSubmitted());
		supplier.setSubmitted(null);
		supplier.setDisqualify(null);
		supplier.setNumberOfBids(null);
		supplier.setNotificationSent(null);
		supplier.setConfirmPrice(null);
		supplier.setRevisedBidSubmitted(null);
		return supplier;
	}

	public EventSupplier() {
		this.submitted = Boolean.FALSE;
		this.notificationSent = Boolean.FALSE;
		this.numberOfBids = 0;
		this.setSupplierInvitedTime(new Date());
		this.confirmPrice = Boolean.FALSE;
		this.isRejectedAfterStart = Boolean.FALSE;
	}

	public RftEventSupplier copyForRft() {
		RftEventSupplier supplier = new RftEventSupplier();
		supplier.setSupplier(getSupplier());
		supplier.setSubmissionStatus(SubmissionStatusType.INVITED);
		return supplier;
	}

	public RfaEventSupplier copyForRfa() {
		RfaEventSupplier supplier = new RfaEventSupplier();
		supplier.setSupplier(getSupplier());
		supplier.setSubmissionStatus(SubmissionStatusType.INVITED);
		return supplier;
	}

	public RfiEventSupplier copyForRfi() {
		RfiEventSupplier supplier = new RfiEventSupplier();
		supplier.setSupplier(getSupplier());
		supplier.setSubmissionStatus(SubmissionStatusType.INVITED);
		return supplier;
	}

	public RfpEventSupplier copyForRfp() {
		RfpEventSupplier supplier = new RfpEventSupplier();
		supplier.setSupplier(getSupplier());
		supplier.setSubmissionStatus(SubmissionStatusType.INVITED);
		return supplier;
	}

	public RfqEventSupplier copyForRfq() {
		RfqEventSupplier supplier = new RfqEventSupplier();
		supplier.setSupplier(getSupplier());
		supplier.setSubmissionStatus(SubmissionStatusType.INVITED);
		return supplier;
	}
	
	public EventSupplier(String id, SubmissionStatusType submissionStatus) {
		this.id = id;
		this.submissionStatus = submissionStatus;
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
	 * @return the previewTime
	 */
	public Date getPreviewTime() {
		return previewTime;
	}

	/**
	 * @param previewTime the previewTime to set
	 */
	public void setPreviewTime(Date previewTime) {
		this.previewTime = previewTime;
	}

	/**
	 * @return the supplierInvitedTime
	 */
	public Date getSupplierInvitedTime() {
		return supplierInvitedTime;
	}

	/**
	 * @param supplierInvitedTime the supplierInvitedTime to set
	 */
	public void setSupplierInvitedTime(Date supplierInvitedTime) {
		this.supplierInvitedTime = supplierInvitedTime;
	}

	/**
	 * @return the supplierEventReadTime
	 */
	public Date getSupplierEventReadTime() {
		return supplierEventReadTime;
	}

	/**
	 * @param supplierEventReadTime the supplierEventReadTime to set
	 */
	public void setSupplierEventReadTime(Date supplierEventReadTime) {
		this.supplierEventReadTime = supplierEventReadTime;
	}

	/**
	 * @return the submitted
	 */
	public Boolean getSubmitted() {
		return submitted;
	}

	/**
	 * @param submitted the submitted to set
	 */
	public void setSubmitted(Boolean submitted) {
		this.submitted = submitted;
	}

	/**
	 * @return the supplierSubmittedTime
	 */
	public Date getSupplierSubmittedTime() {
		return supplierSubmittedTime;
	}

	/**
	 * @param supplierSubmittedTime the supplierSubmittedTime to set
	 */
	public void setSupplierSubmittedTime(Date supplierSubmittedTime) {
		this.supplierSubmittedTime = supplierSubmittedTime;
	}

	/**
	 * @return the rejectedTime
	 */
	public Date getRejectedTime() {
		return rejectedTime;
	}

	/**
	 * @param rejectedTime the rejectedTime to set
	 */
	public void setRejectedTime(Date rejectedTime) {
		this.rejectedTime = rejectedTime;
	}

	/**
	 * @return the acceptedBy
	 */
	public User getAcceptedBy() {
		return acceptedBy;
	}

	/**
	 * @param acceptedBy the acceptedBy to set
	 */
	public void setAcceptedBy(User acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	/**
	 * @return the rejectedBy
	 */
	public User getRejectedBy() {
		return rejectedBy;
	}

	/**
	 * @param rejectedBy the rejectedBy to set
	 */
	public void setRejectedBy(User rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	/**
	 * @return the subbmitedBy
	 */
	public User getSubbmitedBy() {
		return subbmitedBy;
	}

	/**
	 * @param subbmitedBy the subbmitedBy to set
	 */
	public void setSubbmitedBy(User subbmitedBy) {
		this.subbmitedBy = subbmitedBy;
	}

	/**
	 * @return the submissionStatus
	 */
	public SubmissionStatusType getSubmissionStatus() {
		return submissionStatus;
	}

	/**
	 * @param submissionStatus the submissionStatus to set
	 */
	public void setSubmissionStatus(SubmissionStatusType submissionStatus) {
		this.submissionStatus = submissionStatus;
	}

	/**
	 * @return the confirmPrice
	 */
	public Boolean getConfirmPrice() {
		return confirmPrice;
	}

	/**
	 * @param confirmPrice the confirmPrice to set
	 */
	public void setConfirmPrice(Boolean confirmPrice) {
		this.confirmPrice = confirmPrice;
	}

	/**
	 * @return the confirmPriceDate
	 */
	public Date getConfirmPriceDate() {
		return confirmPriceDate;
	}

	/**
	 * @param confirmPriceDate the confirmPriceDate to set
	 */
	public void setConfirmPriceDate(Date confirmPriceDate) {
		this.confirmPriceDate = confirmPriceDate;
	}

	/**
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		try {
			if (StringUtils.checkString(this.supplierCompanyName).length() > 0) {
				return this.supplierCompanyName;
			} else {
				return supplier != null ? supplier.getCompanyName() : "";
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	/**
	 * @return the disqualify
	 */
	public Boolean getDisqualify() {
		return disqualify;
	}

	/**
	 * @param disqualify the disqualify to set
	 */
	public void setDisqualify(Boolean disqualify) {
		this.disqualify = disqualify;
	}

	/**
	 * @return the disqualifyRemarks
	 */
	public String getDisqualifyRemarks() {
		return disqualifyRemarks;
	}

	/**
	 * @param disqualifyRemarks the disqualifyRemarks to set
	 */
	public void setDisqualifyRemarks(String disqualifyRemarks) {
		this.disqualifyRemarks = disqualifyRemarks;
	}

	/**
	 * @return the disqualifiedTime
	 */
	public Date getDisqualifiedTime() {
		return disqualifiedTime;
	}

	/**
	 * @param disqualifiedTime the disqualifiedTime to set
	 */
	public void setDisqualifiedTime(Date disqualifiedTime) {
		this.disqualifiedTime = disqualifiedTime;
	}

	/**
	 * @return the disqualifiedBy
	 */
	public User getDisqualifiedBy() {
		return disqualifiedBy;
	}

	public Date getReQualifiedTime() {
		return reQualifiedTime;
	}

	public void setReQualifiedTime(Date reQualifiedTime) {
		this.reQualifiedTime = reQualifiedTime;
	}

	/**
	 * @param disqualifiedBy the disqualifiedBy to set
	 */
	public void setDisqualifiedBy(User disqualifiedBy) {
		this.disqualifiedBy = disqualifiedBy;
	}

	/**
	 * @return the numberOfBids
	 */
	public Integer getNumberOfBids() {
		return numberOfBids;
	}

	/**
	 * @param numberOfBids the numberOfBids to set
	 */
	public void setNumberOfBids(Integer numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	/**
	 * @return the notificationSent
	 */
	public Boolean getNotificationSent() {
		return notificationSent;
	}

	/**
	 * @param notificationSent the notificationSent to set
	 */
	public void setNotificationSent(Boolean notificationSent) {
		this.notificationSent = notificationSent;
	}

	/**
	 * @return the supplierAuctionOnlineTime
	 */
	public Date getSupplierAuctionOnlineTime() {
		return supplierAuctionOnlineTime;
	}

	/**
	 * @param supplierAuctionOnlineTime the supplierAuctionOnlineTime to set
	 */
	public void setSupplierAuctionOnlineTime(Date supplierAuctionOnlineTime) {
		this.supplierAuctionOnlineTime = supplierAuctionOnlineTime;
	}

	/**
	 * @return the revisedBidSubmitted
	 */
	public Boolean getRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	/**
	 * @param revisedBidSubmitted the revisedBidSubmitted to set
	 */
	public void setRevisedBidSubmitted(Boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the feePaid
	 */
	public Boolean getFeePaid() {
		return feePaid;
	}

	/**
	 * @param feePaid the feePaid to set
	 */
	public void setFeePaid(Boolean feePaid) {
		this.feePaid = feePaid;
	}

	/**
	 * @return the depositPaid
	 */
	public Boolean getDepositPaid() {
		return depositPaid;
	}

	/**
	 * @param depositPaid the depositPaid to set
	 */
	public void setDepositPaid(Boolean depositPaid) {
		this.depositPaid = depositPaid;
	}

	/**
	 * @return the feePaidDate
	 */
	public Date getFeePaidDate() {
		return feePaidDate;
	}

	/**
	 * @param feePaidDate the feePaidDate to set
	 */
	public void setFeePaidDate(Date feePaidDate) {
		this.feePaidDate = feePaidDate;
	}

	/**
	 * @return the depositPaidDate
	 */
	public Date getDepositPaidDate() {
		return depositPaidDate;
	}

	/**
	 * @param depositPaidDate the depositPaidDate to set
	 */
	public void setDepositPaidDate(Date depositPaidDate) {
		this.depositPaidDate = depositPaidDate;
	}

	/**
	 * @return the feeReference
	 */
	public String getFeeReference() {
		return feeReference;
	}

	/**
	 * @param feeReference the feeReference to set
	 */
	public void setFeeReference(String feeReference) {
		this.feeReference = feeReference;
	}

	/**
	 * @return the depositReference
	 */
	public String getDepositReference() {
		return depositReference;
	}

	/**
	 * @param depositReference the depositReference to set
	 */
	public void setDepositReference(String depositReference) {
		this.depositReference = depositReference;
	}

	/**
	 * @return the feePaidTime
	 */
	public Date getFeePaidTime() {
		return feePaidTime;
	}

	/**
	 * @param feePaidTime the feePaidTime to set
	 */
	public void setFeePaidTime(Date feePaidTime) {
		this.feePaidTime = feePaidTime;
	}

	/**
	 * @return the depositPaidTime
	 */
	public Date getDepositPaidTime() {
		return depositPaidTime;
	}

	/**
	 * @param depositPaidTime the depositPaidTime to set
	 */
	public void setDepositPaidTime(Date depositPaidTime) {
		this.depositPaidTime = depositPaidTime;
	}

	/**
	 * @return the selfInvited
	 */
	public Boolean getSelfInvited() {
		return selfInvited;
	}

	/**
	 * @param selfInvited the selfInvited to set
	 */
	public void setSelfInvited(Boolean selfInvited) {
		this.selfInvited = selfInvited;
	}

	public String getFeeReferenceClientId() {
		return feeReferenceClientId;
	}

	public void setFeeReferenceClientId(String feeReferenceClientId) {
		this.feeReferenceClientId = feeReferenceClientId;
	}

	/**
	 * @return the rejectionRemarks
	 */
	public String getRejectionRemarks() {
		return rejectionRemarks;
	}

	/**
	 * @param rejectionRemarks the rejectionRemarks to set
	 */
	public void setRejectionRemarks(String rejectionRemarks) {
		this.rejectionRemarks = rejectionRemarks;
	}

	/**
	 * @return the isRejectedAfterStart
	 */
	public Boolean getIsRejectedAfterStart() {
		return isRejectedAfterStart;
	}

	/**
	 * @param isRejectedAfterStart the isRejectedAfterStart to set
	 */
	public void setIsRejectedAfterStart(Boolean isRejectedAfterStart) {
		this.isRejectedAfterStart = isRejectedAfterStart;
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
		result = prime * result + ((submitted == null) ? 0 : submitted.hashCode());
		result = prime * result + ((supplierEventReadTime == null) ? 0 : supplierEventReadTime.hashCode());
		result = prime * result + ((supplierInvitedTime == null) ? 0 : supplierInvitedTime.hashCode());
		result = prime * result + ((supplierSubmittedTime == null) ? 0 : supplierSubmittedTime.hashCode());
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
		EventSupplier other = (EventSupplier) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (submitted == null) {
			if (other.submitted != null)
				return false;
		} else if (!submitted.equals(other.submitted))
			return false;
		if (supplierEventReadTime == null) {
			if (other.supplierEventReadTime != null)
				return false;
		} else if (!supplierEventReadTime.equals(other.supplierEventReadTime))
			return false;
		if (supplierInvitedTime == null) {
			if (other.supplierInvitedTime != null)
				return false;
		} else if (!supplierInvitedTime.equals(other.supplierInvitedTime))
			return false;
		if (supplierSubmittedTime == null) {
			if (other.supplierSubmittedTime != null)
				return false;
		} else if (!supplierSubmittedTime.equals(other.supplierSubmittedTime))
			return false;
		return true;
	}

	public String toLogString() {
		return "EventSupplier [supplierInvitedTime=" + supplierInvitedTime + ", supplierEventReadTime=" + supplierEventReadTime + ", supplierSubmittedTime=" + supplierSubmittedTime + ", submitted=" + submitted + ", supplierCompanyName=" + supplierCompanyName + "]";
	}

}
