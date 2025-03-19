package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.dao.impl.BusinessUnitDaoImpl;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.FilterTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

public class ApprovedRejectEventPojo implements Serializable {

	private static final long serialVersionUID = 7608176230675883956L;

	private String id;
	private static final Logger LOG = LogManager.getLogger(ApprovedRejectEventPojo.class);
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date actionDate;

	private String type;

	private String createdBy;

	private String eventName;

	private String referenceNumber;

	private String status;

	private String auctionType;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	private Boolean isApproved;

	private String unitName;

	private String supplierName;

	private String actionType;

	private String userComment;

	private String actionDateMb;
	private String createdDateMb;

	private String eventId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEnd;

	private String eventType;

	private SubmissionStatusType submissionStatus;

	private String creatorName;

	private String buyerName;

	private String eStartDate;

	private String eEndDate;

	public ApprovedRejectEventPojo() {
	}

	public ApprovedRejectEventPojo(String id, Date actionDate, String type, String createdBy, String eventName, String referenceNumber, String status, String auctionType, Date createdDate, Boolean isApproved, String unitName, String mySupplierName, String openSupplier) {
		super();
		this.id = id;
		this.actionDate = actionDate;
		this.type = type;
		this.createdBy = createdBy;
		this.eventName = eventName;
		this.referenceNumber = referenceNumber;
		this.status = status;
		if (type.equals("RFA")) {
			if (StringUtils.checkString(auctionType).length() > 0)
				this.auctionType = FilterTypes.fromString(auctionType).name();
		} else {
			this.auctionType = auctionType;
		}
		this.createdDate = createdDate;
		this.isApproved = isApproved;
		this.unitName = unitName;
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			this.supplierName = mySupplierName;
		} else if (StringUtils.checkString(openSupplier).length() > 0) {
			this.supplierName = openSupplier;
		}

	}

	// dummayFlag for uniqueness of constructor
	public ApprovedRejectEventPojo(String id, Date actionDate, String type, String createdBy, String eventName, String referenceNumber, String status, String auctionType, Date createdDate, Boolean isApproved, String unitName, String mySupplierName, String openSupplier, String actionType, String userComment, Boolean dummayFlag) {
		super();
		this.id = id;
		this.actionDate = actionDate;
		this.type = type;
		this.createdBy = createdBy;
		this.eventName = eventName;
		this.referenceNumber = referenceNumber;
		this.status = status;
		if (type.equals("RFA")) {
			this.auctionType = FilterTypes.fromString(auctionType).name();
		} else {
			this.auctionType = auctionType;
		}
		this.createdDate = createdDate;
		this.isApproved = isApproved;
		this.unitName = unitName;
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			this.supplierName = mySupplierName;
		} else if (StringUtils.checkString(openSupplier).length() > 0) {
			this.supplierName = openSupplier;
		}
		this.actionType = actionType;
		this.userComment = userComment;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	public ApprovedRejectEventPojo(String id, String type, String createdBy, String eventName, String referenceNumber, String status, Date createdDate, String unitName, String auctionType, String mySupplierName, String openSupplier) {
		this.id = id;
		this.type = type;
		this.createdBy = createdBy;
		this.eventName = eventName;
		this.referenceNumber = referenceNumber;
		this.status = status;
		LOG.info("Event Type " + type);
		if (type.equals("RFA")) {
			if (StringUtils.checkString(auctionType).length() > 0)
				this.auctionType = FilterTypes.fromString(auctionType).name();
		} else {
			this.auctionType = auctionType;
		}
		this.createdDate = createdDate;
		this.unitName = unitName;
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			this.supplierName = mySupplierName;
		} else if (StringUtils.checkString(openSupplier).length() > 0) {
			this.supplierName = openSupplier;
		}
	}

	// Used for mobile supplier app event list
	public ApprovedRejectEventPojo(String id, String eventId, String eventName, Date eventStart, Date eventEnd, String referenceNumber, EventStatus status, RfxTypes type, SubmissionStatusType submissionStatus, User eventOwner, String buyerName, Date createdDate) {
		this.id = id;
		this.eventId = eventId;
		this.eventName = eventName;
		this.eDate = eventStart;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eStartDate = "";
		this.eEndDate = "";
		this.type = type.name();
		this.referenceNumber = referenceNumber;
		this.status = status != null ? status.name() : "";
		this.submissionStatus = submissionStatus;
		this.creatorName = eventOwner.getName();
		this.buyerName = buyerName;
		this.createdDate = createdDate;
	}

	/**
	 * @return the eStartDate
	 */
	public String geteStartDate() {
		return eStartDate;
	}

	/**
	 * @param eStartDate the eStartDate to set
	 */
	public void seteStartDate(String eStartDate) {
		this.eStartDate = eStartDate;
	}

	/**
	 * @return the eEndDate
	 */
	public String geteEndDate() {
		return eEndDate;
	}

	/**
	 * @param eEndDate the eEndDate to set
	 */
	public void seteEndDate(String eEndDate) {
		this.eEndDate = eEndDate;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the eDate
	 */
	public Date geteDate() {
		return eDate;
	}

	/**
	 * @param eDate the eDate to set
	 */
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	/**
	 * @return the eventStart
	 */
	public Date getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart the eventStart to set
	 */
	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public Date getEventEnd() {
		return eventEnd;
	}

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
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
	 * @return the creatorName
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * @param creatorName the creatorName to set
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getActionDateMb() {
		return actionDateMb;
	}

	public void setActionDateMb(String actionDateMb) {
		this.actionDateMb = actionDateMb;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the auctionType
	 */
	public String getAuctionType() {
		return auctionType;
	}

	/**
	 * @param auctionType the auctionType to set
	 */
	public void setAuctionType(String auctionType) {
		this.auctionType = auctionType;
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
	 * @return the isApproved
	 */
	public Boolean getIsApproved() {
		return isApproved;
	}

	/**
	 * @param isApproved the isApproved to set
	 */
	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName the unitName to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the actionType
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * @return the userComment
	 */
	public String getUserComment() {
		return userComment;
	}

	/**
	 * @param userComment the userComment to set
	 */
	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public String getCreatedDateMb() {
		return createdDateMb;
	}

	public void setCreatedDateMb(String createdDateMb) {
		this.createdDateMb = createdDateMb;
	}

	@Override
	public String toString() {
		return "ApprovedRejectEventPojo [id=" + id + ", actionDate=" + actionDate + ", type=" + type + ", createdBy=" + createdBy + ", eventName=" + eventName + ", referenceNumber=" + referenceNumber + ", status=" + status + ", auctionType=" + auctionType + ", createdDate=" + createdDate + ", isApproved=" + isApproved + ", unitName=" + unitName + ", supplierName=" + supplierName + ", actionType=" + actionType + ", userComment=" + userComment + ", actionDateMb=" + actionDateMb + ", eventStart=" + eventStart + ", eventEnd=" + eventEnd + ", eventType=" + eventType + ", submissionStatus=" + submissionStatus + ", creatorName=" + creatorName + ", buyerName=" + buyerName + "]";
	}
}