package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

public class PendingEventPojo implements Serializable {

	private static final long serialVersionUID = -2598705125320835038L;

	private String id;
	private String eventName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEnd;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date publishDate;

	private User createdBy;
	private RfxTypes type;
	private EventVisibilityType visibilityType;
	private String referenceNumber;
	private String eventId;
	private BigDecimal grandTotal = BigDecimal.ZERO;

	private String unitName;
	private String eventUser;
	private String prUserName;

	private String creatorName;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eDate;
	private String status;
	private String eventType;
	private Boolean urgentEvent;
	private SubmissionStatusType submissionStatus;
	private String buyerName;
	private String decimal;

	private String eStartDate;

	private String eEndDate;

	private Boolean urgentPr;
	
	private String currency;

	public PendingEventPojo() {
	}

	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String referenceNumber, BigDecimal grandTotal) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.referenceNumber = referenceNumber;
		this.grandTotal = grandTotal;
	}

	public PendingEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, String referenceNumber) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.eventEnd = eventEnd;
		this.eventStart = eventStart;
		this.referenceNumber = referenceNumber;
		this.type = RfxTypes.fromString(type);
	}

	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String referenceNumber) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.eventId = eventId;
		this.referenceNumber = referenceNumber;
	}

	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, Date publishDate, String visibilityType, String referenceNumber) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.type = RfxTypes.fromString(type);
		this.eventId = eventId;
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		if (StringUtils.checkString(visibilityType).length() > 0) {
			this.visibilityType = EventVisibilityType.valueOf(visibilityType);
		}
		this.referenceNumber = referenceNumber;
	}

	public PendingEventPojo(String id, String eventName, Date createdDate, Date modifiedDate, User createdBy, RfxTypes type) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			createdBy.getLoginId();
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdBy = createdBy;
		this.type = type;
	}

	public PendingEventPojo(Event event) {
		this.eventName = event.getEventName();
		this.createdBy = event.getCreatedBy();
		this.createdDate = event.getCreatedDate();
		this.modifiedDate = event.getModifiedDate();
	}

	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, Date publishDate, String visibilityType, String referenceNumber, String unitName) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.type = RfxTypes.fromString(type);
		this.eventId = eventId;
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		if (StringUtils.checkString(visibilityType).length() > 0) {
			this.visibilityType = EventVisibilityType.valueOf(visibilityType);
		}
		this.referenceNumber = referenceNumber;
		this.unitName = unitName;
	}

	public PendingEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.eventEnd = eventEnd;
		this.eventStart = eventStart;
		this.referenceNumber = referenceNumber;
		this.type = RfxTypes.fromString(type);
		this.unitName = unitName;
	}

	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String referenceNumber, BigDecimal grandTotal, String unitName, String prUserName) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.referenceNumber = referenceNumber;
		this.grandTotal = grandTotal;
		this.unitName = unitName;
		this.eventId = eventId;
		this.prUserName = prUserName;

	}

	public PendingEventPojo(String id, String eventName, String createdBy, Date createdDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.eventEnd = eventEnd;
		this.eventStart = eventStart;
		this.referenceNumber = referenceNumber;
		this.type = RfxTypes.fromString(type);
		this.unitName = unitName;
		this.eventId = sysEventId;
		this.eventUser = eventUser;
	}

	// Used for mobile my To Do list
	public PendingEventPojo(String id, String eventId, String eventName, Date edate, String referenceNumber, String status, String unitName, String creatorName, String type, Boolean urgentEvent) {
		this.id = id;
		this.eventId = eventId;
		this.eventName = eventName;
		this.eDate = edate;
		this.referenceNumber = referenceNumber;
		this.status = status;
		this.unitName = unitName;
		this.creatorName = creatorName;
		this.eventType = type;
		this.grandTotal = null;
		this.urgentEvent = urgentEvent;
	}

	// Used for mobile my To Do list
	public PendingEventPojo(String id, String eventId, String eventName, Date edate, String referenceNumber, String status, String unitName, String creatorName, String type) {
		this.id = id;
		this.eventId = eventId;
		this.eventName = eventName;
		this.eDate = edate;
		this.referenceNumber = referenceNumber;
		this.status = status;
		this.unitName = unitName;
		this.creatorName = creatorName;
		this.eventType = type;
		this.grandTotal = null;
	}

	// Used for mobile supplier To Do list
	public PendingEventPojo(String id, String eventId, String eventName, Date createdDate, Date eventStart, Date eventEnd, String referenceNumber, EventStatus status, RfxTypes type, SubmissionStatusType submissionStatus, User eventOwner, String buyerName) {
		this.id = id;
		this.eventId = eventId;
		this.eventName = eventName;
		this.eDate = eventStart;
		this.createdDate = createdDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eStartDate = "";
		this.eEndDate = "";
		this.eventType = type.name();
		this.referenceNumber = referenceNumber;
		this.status = status != null ? status.name() : "";
		this.submissionStatus = submissionStatus;
		this.creatorName = eventOwner.getName();
		this.buyerName = buyerName;

	}

	// this is for supplier PO
	public PendingEventPojo(String id, String poName, Date poCreatedDate, String referenceNumber, BusinessUnit businessUnit, PrStatus status) {
		this.id = id;
		// this.createdBy = createdBy;
		this.eventName = poName;
		this.referenceNumber = referenceNumber;
		this.status = status.name();
		this.createdDate = poCreatedDate;
		this.unitName = businessUnit.getUnitName();
		this.eventType = "PR";
	}

	public PendingEventPojo(String id, String poName, Date poCreatedDate, String referenceNumber, BusinessUnit businessUnit, PoStatus status) {
		this.id = id;
		// this.createdBy = createdBy;
		this.eventName = poName;
		this.referenceNumber = referenceNumber;
		this.status = status.name();
		this.createdDate = poCreatedDate;
		this.unitName = businessUnit.getUnitName();
		this.eventType = "PR";
	}

	// PH-1622
	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String referenceNumber, BigDecimal grandTotal, String unitName, String prUserName, String decimal) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.referenceNumber = referenceNumber;
		this.grandTotal = grandTotal;
		this.unitName = unitName;
		this.eventId = eventId;
		this.prUserName = prUserName;
		this.decimal = decimal;
	}

	// PH-2126
	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, String referenceNumber, BigDecimal grandTotal, String unitName, String prUserName, String decimal, Boolean urgentPr) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.referenceNumber = referenceNumber;
		this.grandTotal = grandTotal;
		this.unitName = unitName;
		this.eventId = eventId;
		this.prUserName = prUserName;
		this.decimal = decimal;
		this.urgentPr = urgentPr;
	}
	
	public PendingEventPojo(String id, String eventId, String eventName, String createdBy, Date createdDate, Date modifiedDate, BigDecimal grandTotal, String unitName, String currency, String prUserName, String decimal) {
		this.id = id;
		this.eventId = eventId;
		this.eventName = eventName;
		this.creatorName = createdBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.currency = currency;
		this.grandTotal = grandTotal;
		this.unitName = unitName;
		this.eventId = eventId;
		this.prUserName = prUserName;
		this.decimal = decimal;
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
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
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
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	 * @return the type
	 */
	public RfxTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RfxTypes type) {
		this.type = type;
	}

	/**
	 * @return the publishDate
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return the visibilityType
	 */
	public EventVisibilityType getVisibilityType() {
		return visibilityType;
	}

	/**
	 * @param visibilityType the visibilityType to set
	 */
	public void setVisibilityType(EventVisibilityType visibilityType) {
		this.visibilityType = visibilityType;
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
	 * @return the grandTotal
	 */
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getEventUser() {
		return eventUser;
	}

	public void setEventUser(String eventUser) {
		this.eventUser = eventUser;
	}

	public String getPrUserName() {
		return prUserName;
	}

	public void setPrUserName(String prUserName) {
		this.prUserName = prUserName;
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
	 * @return the urgentEvent
	 */
	public Boolean getUrgentEvent() {
		return urgentEvent;
	}

	/**
	 * @param urgentEvent the urgentEvent to set
	 */
	public void setUrgentEvent(Boolean urgentEvent) {
		this.urgentEvent = urgentEvent;
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
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public Boolean getUrgentPr() {
		return urgentPr;
	}

	public void setUrgentPr(Boolean urgentPr) {
		this.urgentPr = urgentPr;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	

}