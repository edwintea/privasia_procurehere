/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author jayshree
 */
@Entity
@Table(name = "PROC_TAT_REPORT")
public class TatReport {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "BUSINESS_UNIT", length = 150)
	private String businessUnit;

	@Column(name = "COST_CENTER", length = 150)
	private String costCenter;

	@Column(name = "GROUP_CODE", length = 150)
	private String groupCode;

	@Column(name = "REQUEST_OWNER", length = 400)
	private String requestOwner;

	@Column(name = "SOURCING_FORM_NAME", length = 250)
	private String sourcingFormName;

	@Column(name = "FORM_DESCRIPTION", length = 1250)
	private String formDescription;

	@Column(name = "FORM_ID", length = 128)
	private String formId;

	@Enumerated(EnumType.STRING)
	@Column(name = "REQUEST_STATUS")
	private SourcingFormStatus requestStatus;

	@Column(name = "PROCUREMENT_METHOD", length = 150)
	private String procurementMethod;

	@Column(name = "PROCUREMENT_CATEGORIES", length = 150)
	private String procurementCategories;

	@Column(name = "SAP_PR_ID", length = 128)
	private String sapPrId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQ_CREATED_DATE")
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQ_FINISHED_DATE")
	private Date finishDate;

	@Column(name = "AVAILABLE_BUDGET", precision = 20, scale = 4)
	private BigDecimal availableBudget;

	@Column(name = "ESTIMATED_BUDGET", precision = 20, scale = 4)
	private BigDecimal estimatedBudget;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQ_REJECTED_DATE")
	private Date reqRejectedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FIRST_APPROVED_DATE")
	private Date firstApprovedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_APPROVED_DATE")
	private Date lastApprovedDate;

	@Column(name = "REQ_APPROVAL_DAYS_COUNT", precision = 5, scale = 2)
	private BigDecimal reqApprovalDaysCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVENT_TYPE")
	private RfxTypes eventType;

	@Column(name = "EVENT_ID", length = 64)
	private String eventId;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private EventStatus status;

	@Column(name = "EVENT_NAME", length = 350)
	private String eventName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_CREATED_DATE", nullable = false)
	private Date eventCreatedDate;

	@Column(name = "EVENT_OWNER", length = 420)
	private String eventOwner;

	@Column(name = "EVENT_REFERANCE_NUMBER", length = 128)
	private String referanceNumber;

	@Column(name = "INVITED_SUPPLIER_COUNT", length = 8)
	private Integer invitedSupplierCount;

	@Column(name = "ACCEPTED_SUPPLIER_COUNT", length = 8)
	private Integer acceptedSupplierCount;

	@Column(name = "SUBMITED_SUPPLIER_COUNT", length = 8)
	private Integer submitedSupplierCount;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EV_FINISH_DATE")
	private Date eventFinishDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EV_FIRST_APPROVED_DATE")
	private Date eventFirstApprovedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EV_LAST_APPROVED_DATE")
	private Date eventLastApprovedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EV_REJECTED_DATE")
	private Date eventRejectedDate;

	@Column(name = "EV_APPROVAL_DAYS_COUNT", precision = 5, scale = 2)
	private BigDecimal eventApprovalDaysCount;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_START")
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_END")
	private Date eventEnd;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_PUBLISH_DATE")
	private Date eventPublishDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_FIRST_MEETING_DATE")
	private Date eventFirstMeetingDate;

	@Column(name = "EVENT_OPEN_DURATION", precision = 5, scale = 2)
	private BigDecimal eventOpenDuration;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FIRST_ENVELOP_OPEN_DATE")
	private Date firstEnvelopOpenDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVALUATION_COMPLETED_DATE")
	private Date evaluationCompletedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UNMSKING_DATE")
	private Date unmskingDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_CONCLUDE_DATE")
	private Date eventConcludeDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_AWARD_DATE")
	private Date eventAwardDate;

	@Column(name = "AWARDED_SUPPLIER", length = 4000)
	private String awardedSupplier;

	@Column(name = "SAP_PO_ID", length = 128)
	private String sapPoId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SAP_PO_CREATED_DATE")
	private Date sapPocreatedDate;

	@Column(name = "AWARD_VALUE", length = 4000)
	private String awardValue;

	@Column(name = "PAPER_APPROVAL_DAYS_COUNT", precision = 5, scale = 2)
	private BigDecimal paperApprovalDaysCount;

	@Column(name = "OVERALL_TAT", precision = 7, scale = 2)
	private BigDecimal overallTat;

	@Column(name = "TANANT_ID", length = 128)
	private String tenantId;

	@Column(name = "REQUEST_GEN_ID", length = 64)
	private String requestGeneratedId;

	@Column(name = "EVENT_GEN_ID", length = 64)
	private String eventGeneratedId;

	@Column(name = "BASE_CURRENCY", length = 150)
	private String baseCurrency;

	@Column(name = "REQ_DECIMAL", length = 8)
	private String reqDecimal;

	@Column(name = "EVENT_DECIMAL", length = 8)
	private String eventDecimal;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EV_FINISHED_DATE")
	private Date eventFinishedDate;

	@Transient
	private String ids;

	@Transient
	private String strReqCreatedDate;

	@Transient
	private String strReqFinishDate;

	@Transient
	private String strReqRejectedDate;

	@Transient
	private String strFirstApprovedDate;

	@Transient
	private String strLastApprovedDate;

	@Transient
	private String strEventCreatedDate;

	@Transient
	private String strEventFinishDate;

	@Transient
	private String strEventFirstApprovedDate;

	@Transient
	private String strEventLastApprovedDate;

	@Transient
	private String strEventRejectedDate;

	@Transient
	private String strEventStart;

	@Transient
	private String strEventEnd;

	@Transient
	private String strEventPublishDate;

	@Transient
	private String strEventFirstMeetingDate;

	@Transient
	private String strFirstEnvelopOpenDate;

	@Transient
	private String strEvaluationCompletedDate;

	@Transient
	private String strUnmskingDate;

	@Transient
	private String strEventConcludeDate;

	@Transient
	private String strEventAwardDate;

	@Transient
	private String strSapPocreatedDate;

	public TatReport() {

	}

	public TatReport(String id, Date eventStart, Date eventEnd) {
		this.id = id;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
	}

	public TatReport(String id, String formId, SourcingFormStatus requestStatus, Date createdDate, Date lastApprovedDate, String eventId, EventStatus status, Date eventCreatedDate, Date eventLastApprovedDate, Date eventStart, Date eventEnd, Date eventFirstMeetingDate, Date evaluationCompletedDate, Date unmskingDate, Date eventConcludeDate, Date eventAwardDate, Date sapPocreatedDate, BigDecimal paperApprovalDaysCount, BigDecimal overallTat, String eventGeneratedId, String requestGeneratedId) {
		this.id = id;
		this.formId = formId;
		this.requestStatus = requestStatus;
		this.createdDate = createdDate;
		this.lastApprovedDate = lastApprovedDate;
		this.eventId = eventId;
		this.status = status;
		this.eventCreatedDate = eventCreatedDate;
		this.eventLastApprovedDate = eventLastApprovedDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventFirstMeetingDate = eventFirstMeetingDate;
		this.evaluationCompletedDate = evaluationCompletedDate;
		this.unmskingDate = unmskingDate;
		this.eventConcludeDate = eventConcludeDate;
		this.eventAwardDate = eventAwardDate;
		this.sapPocreatedDate = sapPocreatedDate;
		this.paperApprovalDaysCount = paperApprovalDaysCount;
		this.overallTat = overallTat;
		this.requestGeneratedId = requestGeneratedId;
		this.eventGeneratedId = eventGeneratedId;
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
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the requestOwner
	 */
	public String getRequestOwner() {
		return requestOwner;
	}

	/**
	 * @param requestOwner the requestOwner to set
	 */
	public void setRequestOwner(String requestOwner) {
		this.requestOwner = requestOwner;
	}

	/**
	 * @return the sourcingFormName
	 */
	public String getSourcingFormName() {
		return sourcingFormName;
	}

	/**
	 * @param sourcingFormName the sourcingFormName to set
	 */
	public void setSourcingFormName(String sourcingFormName) {
		this.sourcingFormName = sourcingFormName;
	}

	/**
	 * @return the formDescription
	 */
	public String getFormDescription() {
		return formDescription;
	}

	/**
	 * @param formDescription the formDescription to set
	 */
	public void setFormDescription(String formDescription) {
		this.formDescription = formDescription;
	}

	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the requestStatus
	 */
	public SourcingFormStatus getRequestStatus() {
		return requestStatus;
	}

	/**
	 * @param requestStatus the requestStatus to set
	 */
	public void setRequestStatus(SourcingFormStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	/**
	 * @return the procurementMethod
	 */
	public String getProcurementMethod() {
		return procurementMethod;
	}

	/**
	 * @param procurementMethod the procurementMethod to set
	 */
	public void setProcurementMethod(String procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	/**
	 * @return the procurementCategories
	 */
	public String getProcurementCategories() {
		return procurementCategories;
	}

	/**
	 * @param procurementCategories the procurementCategories to set
	 */
	public void setProcurementCategories(String procurementCategories) {
		this.procurementCategories = procurementCategories;
	}

	/**
	 * @return the sapPrId
	 */
	public String getSapPrId() {
		return sapPrId;
	}

	/**
	 * @param sapPrId the sapPrId to set
	 */
	public void setSapPrId(String sapPrId) {
		this.sapPrId = sapPrId;
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
	 * @return the finishDate
	 */
	public Date getFinishDate() {
		return finishDate;
	}

	/**
	 * @param finishDate the finishDate to set
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	/**
	 * @return the availableBudget
	 */
	public BigDecimal getAvailableBudget() {
		return availableBudget;
	}

	/**
	 * @param availableBudget the availableBudget to set
	 */
	public void setAvailableBudget(BigDecimal availableBudget) {
		this.availableBudget = availableBudget;
	}

	/**
	 * @return the estimatedBudget
	 */
	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	/**
	 * @param estimatedBudget the estimatedBudget to set
	 */
	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
	}

	/**
	 * @return the reqRejectedDate
	 */
	public Date getReqRejectedDate() {
		return reqRejectedDate;
	}

	/**
	 * @param reqRejectedDate the reqRejectedDate to set
	 */
	public void setReqRejectedDate(Date reqRejectedDate) {
		this.reqRejectedDate = reqRejectedDate;
	}

	/**
	 * @return the firstApprovedDate
	 */
	public Date getFirstApprovedDate() {
		return firstApprovedDate;
	}

	/**
	 * @param firstApprovedDate the firstApprovedDate to set
	 */
	public void setFirstApprovedDate(Date firstApprovedDate) {
		this.firstApprovedDate = firstApprovedDate;
	}

	/**
	 * @return the lastApprovedDate
	 */
	public Date getLastApprovedDate() {
		return lastApprovedDate;
	}

	/**
	 * @param lastApprovedDate the lastApprovedDate to set
	 */
	public void setLastApprovedDate(Date lastApprovedDate) {
		this.lastApprovedDate = lastApprovedDate;
	}

	/**
	 * @return the reqApprovalDaysCount
	 */
	public BigDecimal getReqApprovalDaysCount() {
		return reqApprovalDaysCount;
	}

	/**
	 * @param reqApprovalDaysCount the reqApprovalDaysCount to set
	 */
	public void setReqApprovalDaysCount(BigDecimal reqApprovalDaysCount) {
		this.reqApprovalDaysCount = reqApprovalDaysCount;
	}

	/**
	 * @return the eventType
	 */
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
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
	 * @return the status
	 */
	public EventStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(EventStatus status) {
		this.status = status;
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
	 * @return the eventCreatedDate
	 */
	public Date getEventCreatedDate() {
		return eventCreatedDate;
	}

	/**
	 * @param eventCreatedDate the eventCreatedDate to set
	 */
	public void setEventCreatedDate(Date eventCreatedDate) {
		this.eventCreatedDate = eventCreatedDate;
	}

	/**
	 * @return the eventOwner
	 */
	public String getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	/**
	 * @return the referanceNumber
	 */
	public String getReferanceNumber() {
		return referanceNumber;
	}

	/**
	 * @param referanceNumber the referanceNumber to set
	 */
	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	/**
	 * @return the invitedSupplierCount
	 */
	public Integer getInvitedSupplierCount() {
		return invitedSupplierCount;
	}

	/**
	 * @param invitedSupplierCount the invitedSupplierCount to set
	 */
	public void setInvitedSupplierCount(Integer invitedSupplierCount) {
		this.invitedSupplierCount = invitedSupplierCount;
	}

	/**
	 * @return the acceptedSupplierCount
	 */
	public Integer getAcceptedSupplierCount() {
		return acceptedSupplierCount;
	}

	/**
	 * @param acceptedSupplierCount the acceptedSupplierCount to set
	 */
	public void setAcceptedSupplierCount(Integer acceptedSupplierCount) {
		this.acceptedSupplierCount = acceptedSupplierCount;
	}

	/**
	 * @return the submitedSupplierCount
	 */
	public Integer getSubmitedSupplierCount() {
		return submitedSupplierCount;
	}

	/**
	 * @param submitedSupplierCount the submitedSupplierCount to set
	 */
	public void setSubmitedSupplierCount(Integer submitedSupplierCount) {
		this.submitedSupplierCount = submitedSupplierCount;
	}

	/**
	 * @return the eventFinishDate
	 */
	public Date getEventFinishDate() {
		return eventFinishDate;
	}

	/**
	 * @param eventFinishDate the eventFinishDate to set
	 */
	public void setEventFinishDate(Date eventFinishDate) {
		this.eventFinishDate = eventFinishDate;
	}

	/**
	 * @return the eventFirstApprovedDate
	 */
	public Date getEventFirstApprovedDate() {
		return eventFirstApprovedDate;
	}

	/**
	 * @param eventFirstApprovedDate the eventFirstApprovedDate to set
	 */
	public void setEventFirstApprovedDate(Date eventFirstApprovedDate) {
		this.eventFirstApprovedDate = eventFirstApprovedDate;
	}

	/**
	 * @return the eventLastApprovedDate
	 */
	public Date getEventLastApprovedDate() {
		return eventLastApprovedDate;
	}

	/**
	 * @param eventLastApprovedDate the eventLastApprovedDate to set
	 */
	public void setEventLastApprovedDate(Date eventLastApprovedDate) {
		this.eventLastApprovedDate = eventLastApprovedDate;
	}

	/**
	 * @return the eventRejectedDate
	 */
	public Date getEventRejectedDate() {
		return eventRejectedDate;
	}

	/**
	 * @param eventRejectedDate the eventRejectedDate to set
	 */
	public void setEventRejectedDate(Date eventRejectedDate) {
		this.eventRejectedDate = eventRejectedDate;
	}

	/**
	 * @return the eventApprovalDaysCount
	 */
	public BigDecimal getEventApprovalDaysCount() {
		return eventApprovalDaysCount;
	}

	/**
	 * @param eventApprovalDaysCount the eventApprovalDaysCount to set
	 */
	public void setEventApprovalDaysCount(BigDecimal eventApprovalDaysCount) {
		this.eventApprovalDaysCount = eventApprovalDaysCount;
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
	 * @return the eventPublishDate
	 */
	public Date getEventPublishDate() {
		return eventPublishDate;
	}

	/**
	 * @param eventPublishDate the eventPublishDate to set
	 */
	public void setEventPublishDate(Date eventPublishDate) {
		this.eventPublishDate = eventPublishDate;
	}

	/**
	 * @return the eventFirstMeetingDate
	 */
	public Date getEventFirstMeetingDate() {
		return eventFirstMeetingDate;
	}

	/**
	 * @param eventFirstMeetingDate the eventFirstMeetingDate to set
	 */
	public void setEventFirstMeetingDate(Date eventFirstMeetingDate) {
		this.eventFirstMeetingDate = eventFirstMeetingDate;
	}

	/**
	 * @return the eventOpenDuration
	 */
	public BigDecimal getEventOpenDuration() {
		return eventOpenDuration;
	}

	/**
	 * @param eventOpenDuration the eventOpenDuration to set
	 */
	public void setEventOpenDuration(BigDecimal eventOpenDuration) {
		this.eventOpenDuration = eventOpenDuration;
	}

	/**
	 * @return the firstEnvelopOpenDate
	 */
	public Date getFirstEnvelopOpenDate() {
		return firstEnvelopOpenDate;
	}

	/**
	 * @param firstEnvelopOpenDate the firstEnvelopOpenDate to set
	 */
	public void setFirstEnvelopOpenDate(Date firstEnvelopOpenDate) {
		this.firstEnvelopOpenDate = firstEnvelopOpenDate;
	}

	/**
	 * @return the evaluationCompletedDate
	 */
	public Date getEvaluationCompletedDate() {
		return evaluationCompletedDate;
	}

	/**
	 * @param evaluationCompletedDate the evaluationCompletedDate to set
	 */
	public void setEvaluationCompletedDate(Date evaluationCompletedDate) {
		this.evaluationCompletedDate = evaluationCompletedDate;
	}

	/**
	 * @return the unmskingDate
	 */
	public Date getUnmskingDate() {
		return unmskingDate;
	}

	/**
	 * @param unmskingDate the unmskingDate to set
	 */
	public void setUnmskingDate(Date unmskingDate) {
		this.unmskingDate = unmskingDate;
	}

	/**
	 * @return the eventConcludeDate
	 */
	public Date getEventConcludeDate() {
		return eventConcludeDate;
	}

	/**
	 * @param eventConcludeDate the eventConcludeDate to set
	 */
	public void setEventConcludeDate(Date eventConcludeDate) {
		this.eventConcludeDate = eventConcludeDate;
	}

	/**
	 * @return the eventAwardDate
	 */
	public Date getEventAwardDate() {
		return eventAwardDate;
	}

	/**
	 * @param eventAwardDate the eventAwardDate to set
	 */
	public void setEventAwardDate(Date eventAwardDate) {
		this.eventAwardDate = eventAwardDate;
	}

	/**
	 * @return the awardedSupplier
	 */
	public String getAwardedSupplier() {
		return awardedSupplier;
	}

	/**
	 * @param awardedSupplier the awardedSupplier to set
	 */
	public void setAwardedSupplier(String awardedSupplier) {
		this.awardedSupplier = awardedSupplier;
	}

	/**
	 * @return the sapPoId
	 */
	public String getSapPoId() {
		return sapPoId;
	}

	/**
	 * @param sapPoId the sapPoId to set
	 */
	public void setSapPoId(String sapPoId) {
		this.sapPoId = sapPoId;
	}

	/**
	 * @return the sapPocreatedDate
	 */
	public Date getSapPocreatedDate() {
		return sapPocreatedDate;
	}

	/**
	 * @param sapPocreatedDate the sapPocreatedDate to set
	 */
	public void setSapPocreatedDate(Date sapPocreatedDate) {
		this.sapPocreatedDate = sapPocreatedDate;
	}

	/**
	 * @return the awardValue
	 */
	public String getAwardValue() {
		return awardValue;
	}

	/**
	 * @param awardValue the awardValue to set
	 */
	public void setAwardValue(String awardValue) {
		this.awardValue = awardValue;
	}

	/**
	 * @return the paperApprovalDaysCount
	 */
	public BigDecimal getPaperApprovalDaysCount() {
		return paperApprovalDaysCount;
	}

	/**
	 * @param paperApprovalDaysCount the paperApprovalDaysCount to set
	 */
	public void setPaperApprovalDaysCount(BigDecimal paperApprovalDaysCount) {
		this.paperApprovalDaysCount = paperApprovalDaysCount;
	}

	/**
	 * @return the overallTat
	 */
	public BigDecimal getOverallTat() {
		return overallTat;
	}

	/**
	 * @param overallTat the overallTat to set
	 */
	public void setOverallTat(BigDecimal overallTat) {
		this.overallTat = overallTat;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the requestGeneratedId
	 */
	public String getRequestGeneratedId() {
		return requestGeneratedId;
	}

	/**
	 * @param requestGeneratedId the requestGeneratedId to set
	 */
	public void setRequestGeneratedId(String requestGeneratedId) {
		this.requestGeneratedId = requestGeneratedId;
	}

	/**
	 * @return the eventGeneratedId
	 */
	public String getEventGeneratedId() {
		return eventGeneratedId;
	}

	/**
	 * @param eventGeneratedId the eventGeneratedId to set
	 */
	public void setEventGeneratedId(String eventGeneratedId) {
		this.eventGeneratedId = eventGeneratedId;
	}

	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the reqDecimal
	 */
	public String getReqDecimal() {
		return reqDecimal;
	}

	/**
	 * @param reqDecimal the reqDecimal to set
	 */
	public void setReqDecimal(String reqDecimal) {
		this.reqDecimal = reqDecimal;
	}

	/**
	 * @return the eventDecimal
	 */
	public String getEventDecimal() {
		return eventDecimal;
	}

	/**
	 * @param eventDecimal the eventDecimal to set
	 */
	public void setEventDecimal(String eventDecimal) {
		this.eventDecimal = eventDecimal;
	}

	/**
	 * @return the eventFinishedDate
	 */
	public Date getEventFinishedDate() {
		return eventFinishedDate;
	}

	/**
	 * @param eventFinishedDate the eventFinishedDate to set
	 */
	public void setEventFinishedDate(Date eventFinishedDate) {
		this.eventFinishedDate = eventFinishedDate;
	}

	/**
	 * @return the ids
	 */
	public String getIds() {
		return ids;
	}

	/**
	 * @param ids the ids to set
	 */
	public void setIds(String ids) {
		this.ids = ids;
	}

	/**
	 * @return the strReqCreatedDate
	 */
	public String getStrReqCreatedDate() {
		return strReqCreatedDate;
	}

	/**
	 * @param strReqCreatedDate the strReqCreatedDate to set
	 */
	public void setStrReqCreatedDate(String strReqCreatedDate) {
		this.strReqCreatedDate = strReqCreatedDate;
	}

	/**
	 * @return the strReqFinishDate
	 */
	public String getStrReqFinishDate() {
		return strReqFinishDate;
	}

	/**
	 * @param strReqFinishDate the strReqFinishDate to set
	 */
	public void setStrReqFinishDate(String strReqFinishDate) {
		this.strReqFinishDate = strReqFinishDate;
	}

	/**
	 * @return the strReqRejectedDate
	 */
	public String getStrReqRejectedDate() {
		return strReqRejectedDate;
	}

	/**
	 * @param strReqRejectedDate the strReqRejectedDate to set
	 */
	public void setStrReqRejectedDate(String strReqRejectedDate) {
		this.strReqRejectedDate = strReqRejectedDate;
	}

	/**
	 * @return the strFirstApprovedDate
	 */
	public String getStrFirstApprovedDate() {
		return strFirstApprovedDate;
	}

	/**
	 * @param strFirstApprovedDate the strFirstApprovedDate to set
	 */
	public void setStrFirstApprovedDate(String strFirstApprovedDate) {
		this.strFirstApprovedDate = strFirstApprovedDate;
	}

	/**
	 * @return the strLastApprovedDate
	 */
	public String getStrLastApprovedDate() {
		return strLastApprovedDate;
	}

	/**
	 * @param strLastApprovedDate the strLastApprovedDate to set
	 */
	public void setStrLastApprovedDate(String strLastApprovedDate) {
		this.strLastApprovedDate = strLastApprovedDate;
	}

	/**
	 * @return the strEventCreatedDate
	 */
	public String getStrEventCreatedDate() {
		return strEventCreatedDate;
	}

	/**
	 * @param strEventCreatedDate the strEventCreatedDate to set
	 */
	public void setStrEventCreatedDate(String strEventCreatedDate) {
		this.strEventCreatedDate = strEventCreatedDate;
	}

	/**
	 * @return the strEventFinishDate
	 */
	public String getStrEventFinishDate() {
		return strEventFinishDate;
	}

	/**
	 * @param strEventFinishDate the strEventFinishDate to set
	 */
	public void setStrEventFinishDate(String strEventFinishDate) {
		this.strEventFinishDate = strEventFinishDate;
	}

	/**
	 * @return the strEventFirstApprovedDate
	 */
	public String getStrEventFirstApprovedDate() {
		return strEventFirstApprovedDate;
	}

	/**
	 * @param strEventFirstApprovedDate the strEventFirstApprovedDate to set
	 */
	public void setStrEventFirstApprovedDate(String strEventFirstApprovedDate) {
		this.strEventFirstApprovedDate = strEventFirstApprovedDate;
	}

	/**
	 * @return the strEventLastApprovedDate
	 */
	public String getStrEventLastApprovedDate() {
		return strEventLastApprovedDate;
	}

	/**
	 * @param strEventLastApprovedDate the strEventLastApprovedDate to set
	 */
	public void setStrEventLastApprovedDate(String strEventLastApprovedDate) {
		this.strEventLastApprovedDate = strEventLastApprovedDate;
	}

	/**
	 * @return the strEventRejectedDate
	 */
	public String getStrEventRejectedDate() {
		return strEventRejectedDate;
	}

	/**
	 * @param strEventRejectedDate the strEventRejectedDate to set
	 */
	public void setStrEventRejectedDate(String strEventRejectedDate) {
		this.strEventRejectedDate = strEventRejectedDate;
	}

	/**
	 * @return the strEventStart
	 */
	public String getStrEventStart() {
		return strEventStart;
	}

	/**
	 * @param strEventStart the strEventStart to set
	 */
	public void setStrEventStart(String strEventStart) {
		this.strEventStart = strEventStart;
	}

	/**
	 * @return the strEventEnd
	 */
	public String getStrEventEnd() {
		return strEventEnd;
	}

	/**
	 * @param strEventEnd the strEventEnd to set
	 */
	public void setStrEventEnd(String strEventEnd) {
		this.strEventEnd = strEventEnd;
	}

	/**
	 * @return the strEventPublishDate
	 */
	public String getStrEventPublishDate() {
		return strEventPublishDate;
	}

	/**
	 * @param strEventPublishDate the strEventPublishDate to set
	 */
	public void setStrEventPublishDate(String strEventPublishDate) {
		this.strEventPublishDate = strEventPublishDate;
	}

	/**
	 * @return the strEventFirstMeetingDate
	 */
	public String getStrEventFirstMeetingDate() {
		return strEventFirstMeetingDate;
	}

	/**
	 * @param strEventFirstMeetingDate the strEventFirstMeetingDate to set
	 */
	public void setStrEventFirstMeetingDate(String strEventFirstMeetingDate) {
		this.strEventFirstMeetingDate = strEventFirstMeetingDate;
	}

	/**
	 * @return the strFirstEnvelopOpenDate
	 */
	public String getStrFirstEnvelopOpenDate() {
		return strFirstEnvelopOpenDate;
	}

	/**
	 * @param strFirstEnvelopOpenDate the strFirstEnvelopOpenDate to set
	 */
	public void setStrFirstEnvelopOpenDate(String strFirstEnvelopOpenDate) {
		this.strFirstEnvelopOpenDate = strFirstEnvelopOpenDate;
	}

	/**
	 * @return the strEvaluationCompletedDate
	 */
	public String getStrEvaluationCompletedDate() {
		return strEvaluationCompletedDate;
	}

	/**
	 * @param strEvaluationCompletedDate the strEvaluationCompletedDate to set
	 */
	public void setStrEvaluationCompletedDate(String strEvaluationCompletedDate) {
		this.strEvaluationCompletedDate = strEvaluationCompletedDate;
	}

	/**
	 * @return the strUnmskingDate
	 */
	public String getStrUnmskingDate() {
		return strUnmskingDate;
	}

	/**
	 * @param strUnmskingDate the strUnmskingDate to set
	 */
	public void setStrUnmskingDate(String strUnmskingDate) {
		this.strUnmskingDate = strUnmskingDate;
	}

	/**
	 * @return the strEeventConcludeDate
	 */
	public String getStrEventConcludeDate() {
		return strEventConcludeDate;
	}

	/**
	 * @param strEeventConcludeDate the strEeventConcludeDate to set
	 */
	public void setStrEventConcludeDate(String strEeventConcludeDate) {
		this.strEventConcludeDate = strEeventConcludeDate;
	}

	/**
	 * @return the strEventAwardDate
	 */
	public String getStrEventAwardDate() {
		return strEventAwardDate;
	}

	/**
	 * @param strEventAwardDate the strEventAwardDate to set
	 */
	public void setStrEventAwardDate(String strEventAwardDate) {
		this.strEventAwardDate = strEventAwardDate;
	}

	/**
	 * @return the strSapPocreatedDate
	 */
	public String getStrSapPocreatedDate() {
		return strSapPocreatedDate;
	}

	/**
	 * @param strSapPocreatedDate the strSapPocreatedDate to set
	 */
	public void setStrSapPocreatedDate(String strSapPocreatedDate) {
		this.strSapPocreatedDate = strSapPocreatedDate;
	}

	@Override
	public String toString() {
		return "TatReport [id=" + id + ", businessUnit=" + businessUnit + ", costCenter=" + costCenter + ", groupCode=" + groupCode + ", requestOwner=" + requestOwner + ", sourcingFormName=" + sourcingFormName + ", formDescription=" + formDescription + ", formId=" + formId + ", requestStatus=" + requestStatus + ", procurementMethod=" + procurementMethod + ", procurementCategories=" + procurementCategories + ", sapPrId=" + sapPrId + ", createdDate=" + createdDate + ", finishDate=" + finishDate + ", availableBudget=" + availableBudget + ", estimatedBudget=" + estimatedBudget + ", reqRejectedDate=" + reqRejectedDate + ", firstApprovedDate=" + firstApprovedDate + ", lastApprovedDate=" + lastApprovedDate + ", reqApprovalDaysCount=" + reqApprovalDaysCount + ", eventType=" + eventType + ", eventId=" + eventId + ", status=" + status + ", eventName=" + eventName + ", eventCreatedDate=" + eventCreatedDate + ", eventOwner=" + eventOwner + ", referanceNumber=" + referanceNumber + ", invitedSupplierCount=" + invitedSupplierCount + ", acceptedSupplierCount=" + acceptedSupplierCount + ", submitedSupplierCount=" + submitedSupplierCount + ", eventFinishDate=" + eventFinishDate + ", eventFirstApprovedDate=" + eventFirstApprovedDate + ", eventLastApprovedDate=" + eventLastApprovedDate + ", eventRejectedDate=" + eventRejectedDate + ", eventApprovalDaysCount=" + eventApprovalDaysCount + ", eventStart=" + eventStart + ", eventEnd=" + eventEnd + ", eventPublishDate=" + eventPublishDate + ", eventFirstMeetingDate=" + eventFirstMeetingDate + ", eventOpenDuration=" + eventOpenDuration + ", firstEnvelopOpenDate=" + firstEnvelopOpenDate + ", evaluationCompletedDate=" + evaluationCompletedDate + ", unmskingDate=" + unmskingDate + ", eventConcludeDate=" + eventConcludeDate + ", eventAwardDate=" + eventAwardDate + ", awardedSupplier=" + awardedSupplier + ", sapPoId=" + sapPoId + ", sapPocreatedDate=" + sapPocreatedDate + ", awardValue=" + awardValue + ", paperApprovalDaysCount=" + paperApprovalDaysCount + ", overallTat=" + overallTat + ", tenantId=" + tenantId + ", requestGeneratedId=" + requestGeneratedId + ", eventGeneratedId=" + eventGeneratedId + ", baseCurrency=" + baseCurrency + ", reqDecimal=" + reqDecimal + ", eventDecimal=" + eventDecimal + ", eventFinishedDate=" + eventFinishedDate + ", ids=" + ids + ", strReqCreatedDate=" + strReqCreatedDate + ", strReqFinishDate=" + strReqFinishDate + ", strReqRejectedDate=" + strReqRejectedDate + ", strFirstApprovedDate=" + strFirstApprovedDate + ", strLastApprovedDate=" + strLastApprovedDate + ", strEventCreatedDate=" + strEventCreatedDate + ", strEventFinishDate=" + strEventFinishDate + ", strEventFirstApprovedDate=" + strEventFirstApprovedDate + ", strEventLastApprovedDate=" + strEventLastApprovedDate + ", strEventRejectedDate=" + strEventRejectedDate + ", strEventStart=" + strEventStart + ", strEventEnd=" + strEventEnd + ", strEventPublishDate=" + strEventPublishDate + ", strEventFirstMeetingDate=" + strEventFirstMeetingDate + ", strFirstEnvelopOpenDate=" + strFirstEnvelopOpenDate + ", strEvaluationCompletedDate=" + strEvaluationCompletedDate + ", strUnmskingDate=" + strUnmskingDate + ", strEventConcludeDate=" + strEventConcludeDate + ", strEventAwardDate=" + strEventAwardDate + ", strSapPocreatedDate=" + strSapPocreatedDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventGeneratedId == null) ? 0 : eventGeneratedId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((requestGeneratedId == null) ? 0 : requestGeneratedId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TatReport other = (TatReport) obj;
		if (eventGeneratedId == null) {
			if (other.eventGeneratedId != null)
				return false;
		} else if (!eventGeneratedId.equals(other.eventGeneratedId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (requestGeneratedId == null) {
			if (other.requestGeneratedId != null)
				return false;
		} else if (!requestGeneratedId.equals(other.requestGeneratedId))
			return false;
		return true;
	}

}