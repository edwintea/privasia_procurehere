/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
import com.privasia.procurehere.core.utils.CustomDateSerializer;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */

@MappedSuperclass
@SqlResultSetMapping(name = "approvedRejectResult", classes = { @ConstructorResult(targetClass = ApprovedRejectEventPojo.class, columns = { @ColumnResult(name = "id", type = String.class), @ColumnResult(name = "actionDate", type = Date.class), @ColumnResult(name = "type", type = String.class), @ColumnResult(name = "createdBy", type = String.class), @ColumnResult(name = "eventName", type = String.class), @ColumnResult(name = "referenceNumber", type = String.class), @ColumnResult(name = "status", type = String.class), @ColumnResult(name = "auctionType", type = String.class), @ColumnResult(name = "createdDate", type = Date.class), @ColumnResult(name = "isApproved", type = Boolean.class), @ColumnResult(name = "unitName", type = String.class), @ColumnResult(name = "mySupplierName", type = String.class), @ColumnResult(name = "openSupplier", type = String.class), @ColumnResult(name = "actionType", type = String.class), @ColumnResult(name = "userComment", type = String.class), @ColumnResult(name = "dummayFlag", type = Boolean.class) }) })
public class Event implements Serializable {

	private static final long serialVersionUID = -9165680319849869387L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "EVENT_ID", length = 64)
	private String eventId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_OWNER", nullable = false)
	private User eventOwner;

	@Column(name = "REFERANCE_NUMBER", length = 64)
	@Size(min = 1, max = 64, message = "{event.referencenumber.length}")
	private String referanceNumber;

	@Column(name = "EVENT_NAME", length = 250)
	@Size(min = 1, max = 250, message = "{event.name.length}")
	private String eventName;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVENT_VISIBILITY")
	private EventVisibilityType eventVisibility;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_START")
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_END")
	private Date eventEnd;

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_PUBLISH_DATE")
	private Date eventPublishDate;

	@Column(name = "SUBMISSION_VALIDITY_DAYS", length = 3)
	@Digits(integer = 3, fraction = 0, message = "{event.days}")
	private Integer submissionValidityDays;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_CATEGORY", nullable = true)
	private IndustryCategory industryCategory;

	@Column(name = "PARTICIPATION_FEES", precision = 12, scale = 6)
	private BigDecimal participationFees;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PARTICIPATION_FEE_CURRENCY", nullable = true)
	private Currency participationFeeCurrency;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "DELIVERY_ADDRESS", nullable = true)
	private BuyerAddress deliveryAddress;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private EventStatus status;

	@Column(name = "EVENT_DESCRIPTION", length = 2000)
	@Size(max = 2000, message = "{event.description.length}")
	private String eventDescription;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CURRENCY_ID", nullable = true)
	private Currency baseCurrency;

	@Column(name = "BUYER_SET_DECIMAL", length = 8)
	private String decimal;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER", nullable = true)
	private CostCenter costCenter;

	@Column(name = "BUDGET_AMOUNT", precision = 22, scale = 6)
	private BigDecimal budgetAmount;

	@Column(name = "HISTORICAL_AMOUNT", precision = 22, scale = 6)
	private BigDecimal historicaAmount;

	@Column(name = "ESTIMATED_BUDGET", precision = 22, scale = 6)
	private BigDecimal estimatedBudget;

	@Column(name = "PAYMENT_TERM", length = 550)
	@Size(max = 550, message = "{event.paymentterm.length}")
	private String paymentTerm;

	@Column(name = "IS_DOCUMENT_REQ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean documentReq = Boolean.TRUE;

	@Column(name = "IS_MEETINGS_REQ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean meetingReq = Boolean.TRUE;

	@Column(name = "IS_QUESTIONNAIRES_REQ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean questionnaires = Boolean.TRUE;

	@Column(name = "IS_BILLOFQUANTITY_REQ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean billOfQuantity = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEMPLATE_ID", nullable = true)
	private RfxTemplate template;

	@Column(name = "IS_EVENT_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean eventDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_DOCUMENT_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean documentCompleted = Boolean.FALSE;

	@Column(name = "IS_SUPPLIER_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean supplierCompleted = Boolean.FALSE;

	@Column(name = "IS_MEETINGS_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean meetingCompleted = Boolean.FALSE;

	@Column(name = "IS_CQ_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean cqCompleted = Boolean.FALSE;

	@Column(name = "IS_BQ_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean bqCompleted = Boolean.FALSE;

	@Column(name = "IS_ENVELOP_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean envelopCompleted = Boolean.FALSE;

	@Column(name = "IS_SUMMARY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean summaryCompleted = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUSPENSION_TYPE")
	private SuspensionType suspensionType;

	@Size(max = 550)
	@Column(name = "CANCEL_REASON", length = 550)
	private String cancelReason;

	@Size(max = 550)
	@Column(name = "SUSPEND_REMARKS", length = 550)
	private String suspendRemarks;

	@Size(min = 0, max = 64)
	@Column(name = "PREVIOUS_EVENT", nullable = true, length = 64)
	String previousEventId;

	@Size(min = 0, max = 64)
	@Column(name = "NEXT_EVENT", nullable = true, length = 64)
	String nextEventId;

	@Enumerated(EnumType.STRING)
	@Column(name = "PREVIOUS_EVENT_TYPE", nullable = true)
	private RfxTypes previousEventType;

	@Enumerated(EnumType.STRING)
	@Column(name = "NEXT_EVENT_TYPE", nullable = true)
	private RfxTypes nextEventType;

	@Column(name = "START_MESSAGE_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean startMessageSent;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true)
	private BusinessUnit businessUnit;

	@Column(name = "URGENT_EVENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean urgentEvent;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELIVERY_DATE")
	private Date deliveryDate;

	@Column(name = "IS_AWARDED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean awarded = Boolean.FALSE;

	@Transient
	private Date eventPublishTime;

	@Column(name = "ERP_DOC_NO", length = 64)
	private String erpDocNo;

	@Column(name = "CONCLUDE_REMARKS", length = 550)
	@Size(min = 0, max = 550)
	private String concludeRemarks;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONCLUDE_DATE")
	private Date concludeDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONCLUDE_BY", nullable = true)
	private User concludeBy;

	@Column(name = "IS_ERP_ENABLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean erpEnable = Boolean.FALSE;

	@Column(name = "VIEW_SUPPLIER_NAME")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean viewSupplerName = Boolean.TRUE;

	@Column(name = "ALLOW_CLOSE_ENVELOPE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean closeEnvelope = Boolean.FALSE;

	@Column(name = "ALLOW_ADD_SUPPLIER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addSupplier = Boolean.FALSE;

	@Column(name = "ALLOW_TO_SUSPEND_EVENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowToSuspendEvent = Boolean.TRUE;

	@Size(min = 0, max = 64)
	@Column(name = "PREVIOUS_REQUEST", nullable = true, length = 64)
	private String previousRequestId;

	@Column(name = "DISABLE_MASKING", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean disableMasking = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UNMASKED_BY", nullable = true)
	private User unMaskedUser;

	@Column(name = "DEPOSIT", precision = 12, scale = 6)
	private BigDecimal deposit;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "DEPOSIT_CURRENCY", nullable = true)
	private Currency depositCurrency;

	@Column(name = "AWARD_DATE", nullable = true)
	private Date awardDate;

	@Column(name = "INTERNAL_REMARKS", length = 2000)
	@Size(max = 2000, message = "{internal.remarks.length}")
	private String internalRemarks;

	@Column(name = "EVENT_PUSH_DATE", nullable = true)
	private Date eventPushDate;

	@Column(name = "TRNSFR_AWRD_RESP_FLAG")
	@Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean transfrAwrdRespFlag = Boolean.FALSE;

	@Column(name = "SCHEDULE_OF_RATE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean scheduleOfRate = Boolean.FALSE;

	@Column(name = "SCHEDULE_OF_RATE_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sorCompleted = Boolean.FALSE;

	// this is added for PH-132 it is for the disabled copy form event which is created from inactive events
	@Transient
	boolean templateActive;

	// PH 262 to show auction type on event copy from previous
	@Transient
	private AuctionType rfaAuctionType;

	@Column(name = "MINIMUM_SUPPLIER_RATING", nullable = true)
	private BigDecimal minimumSupplierRating;

	@Column(name = "MAXIMUM_SUPPLIER_RATING", nullable = true)
	private BigDecimal maximumSupplierRating;

	@Column(name = "RFX_ENVELOPE_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfxEnvelopeReadOnly = Boolean.FALSE;

	@Column(name = "RFX_ENVELOPE_OPENING", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfxEnvelopeOpening = Boolean.FALSE;

	@Column(name = "RFX_ENV_OPENING_AFTER", nullable = true)
	private String rfxEnvOpeningAfter;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVALUATION_PROCESS_DECLARATION", nullable = true)
	private Declaration evaluationProcessDeclaration;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ACCEPT_DECLARATION", nullable = true)
	private Declaration supplierAcceptanceDeclaration;

	@Column(name = "IS_ENABLE_EVAL_DECLARATION", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableEvaluationDeclaration = Boolean.FALSE;

	@Column(name = "IS_ENABLE_SUPPLIER_DECLARATION", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSupplierDeclaration = Boolean.FALSE;

	@Column(name = "ENABLE_EVAL_CON_PREM")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableEvaluationConclusionUsers = Boolean.FALSE;

	@Column(name = "EVAL_CON_PREM_EVAL_COUNT", length = 10, nullable = true)
	private Integer evaluationConclusionEnvelopeEvaluatedCount;

	@Column(name = "EVAL_CON_PREM_NON_EVAL_COUNT", length = 10, nullable = true)
	private Integer evaluationConclusionEnvelopeNonEvaluatedCount;

	@Column(name = "EVAL_CON_PREM_DISQ_SUP_COUNT", length = 10, nullable = true)
	private Integer evaluationConclusionDisqualifiedSupplierCount;

	@Column(name = "EVAL_CON_PREM_PEND_SUP_COUNT", length = 10, nullable = true)
	private Integer evaluationConclusionRemainingSupplierCount;

	@Transient
	private String eventVisibilityDates;

	@Column(name = "IS_ENABLE_APPROVAL_REMINDER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableApprovalReminder = Boolean.FALSE;

	@Column(name = "REMINDER_HOURS", length = 2, nullable = true)
	private Integer reminderAfterHour;

	@Column(name = "REMINDER_COUNT", length = 2, nullable = true)
	private Integer reminderCount;

	@Column(name = "IS_NOTIFY_EVENT_OWNER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean notifyEventOwner = Boolean.FALSE;

	@Column(name = "IS_ENABLE_SUSPENSION_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSuspensionApproval = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "PROCUREMENT_METHOD", nullable = true)
	private ProcurementMethod procurementMethod;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "PROCUREMENT_CATEGORIES", nullable = true)
	private ProcurementCategories procurementCategories;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "GROUP_CODE", nullable = true)
	private GroupCode groupCode;

	@Column(name = "ALLOW_DISQ_SUP_DOWNLOAD", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowDisqualifiedSupplierDownload = Boolean.FALSE;
	
	public Event() {
		this.documentReq = Boolean.TRUE;
		this.meetingReq = Boolean.TRUE;
		this.questionnaires = Boolean.TRUE;
		this.billOfQuantity = Boolean.TRUE;
		this.documentCompleted = Boolean.FALSE;
		this.meetingCompleted = Boolean.FALSE;
		this.cqCompleted = Boolean.FALSE;
		this.bqCompleted = Boolean.FALSE;
		this.supplierCompleted = Boolean.FALSE;
		this.envelopCompleted = Boolean.FALSE;
		this.eventDetailCompleted = Boolean.FALSE;
		this.summaryCompleted = Boolean.FALSE;
		this.startMessageSent = Boolean.FALSE;
		this.urgentEvent = Boolean.FALSE;
		this.erpEnable = Boolean.FALSE;
		this.viewSupplerName = Boolean.TRUE;
		this.closeEnvelope = Boolean.FALSE;
		this.allowToSuspendEvent = Boolean.TRUE;
		this.enableEvaluationDeclaration = Boolean.FALSE;
		this.enableSupplierDeclaration = Boolean.FALSE;
		this.enableEvaluationConclusionUsers = Boolean.FALSE;
		this.enableApprovalReminder = Boolean.FALSE;
	}

	public Event createShallowCopy() {
		Event event = new Event();
		event.setBillOfQuantity(getBillOfQuantity());
		event.setBudgetAmount(getBudgetAmount());
		event.setCreatedBy(getCreatedBy());
		event.setCreatedDate(getCreatedDate());
		event.setDecimal(getDecimal());
		event.setDocumentReq(getDocumentReq());
		event.setEventDescription(getEventDescription());
		event.setEventEnd(getEventEnd());
		event.setEventName(getEventName());
		event.setEventStart(getEventStart());
		event.setEventVisibility(getEventVisibility());
		event.setEventId(getEventId());
		event.setHistoricaAmount(getHistoricaAmount());
		event.setIndustryCategory(getIndustryCategory());
		event.setMeetingReq(getMeetingReq());
		event.setParticipationFees(getParticipationFees());
		event.setPaymentTerm(getPaymentTerm());
		event.setQuestionnaires(getQuestionnaires());
		event.setReferanceNumber(getReferanceNumber());
		event.setStatus(getStatus());
		event.setSubmissionValidityDays(getSubmissionValidityDays());
		event.setTenantId(getTenantId());
		event.setErpEnable(getErpEnable());
		event.setTemplateActive(getTemplateActive());
		event.setRfaAuctionType(getRfaAuctionType());
		event.setEnableEvaluationConclusionUsers(getEnableEvaluationConclusionUsers());
		event.setEvaluationConclusionEnvelopeEvaluatedCount(getEvaluationConclusionEnvelopeEvaluatedCount());
		event.setEvaluationConclusionEnvelopeNonEvaluatedCount(getEvaluationConclusionEnvelopeNonEvaluatedCount());
		return event;
	}

	public boolean getIsOnGoing() {
		if (getStatus() == EventStatus.ACTIVE && getEventStart() != null && getEventStart().before(new Date())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param eventStart
	 * @param eventEnd
	 * @param status
	 */
	public Event(EventStatus status, Date eventStart, Date eventEnd) {
		super();
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.status = status;
	}

	/**
	 * @param id
	 * @param eventStart
	 * @param eventEnd
	 * @param status
	 */
	public Event(String id, Date eventStart, Date eventEnd, EventStatus status) {
		super();
		this.id = id;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.status = status;
	}

	/**
	 * @param id
	 * @param eventName
	 * @param status
	 */
	public Event(String id, String eventName, EventStatus status) {
		super();
		this.id = id;
		this.eventName = eventName;
		this.status = status;
	}

	public Event(String id, Date eventPublishDate, String eventName, Date eventEnd, String referanceNumber) {
		this.id = id;
		this.eventPublishDate = eventPublishDate;
		this.eventName = eventName;
		this.eventEnd = eventEnd;
		this.referanceNumber = referanceNumber;
	}

	public Event(String id, String eventId, Date eventPublishDate, String eventName, Date eventEnd, Date eventStart, EventStatus status, String referanceNumber, String createdById, String createdBy, String comunicationEmail, boolean emailNotificaitons) {
		this.id = id;
		this.eventId = eventId;
		this.eventPublishDate = eventPublishDate;
		this.eventName = eventName;
		this.eventEnd = eventEnd;
		this.eventStart = eventStart;
		this.status = status;
		this.referanceNumber = referanceNumber;
		if (StringUtils.checkString(createdBy).length() > 0) {
			User createdByUser = new User();
			createdByUser.setId(createdById);
			createdByUser.setName(createdBy);
			createdByUser.setCommunicationEmail(comunicationEmail);
			createdByUser.setEmailNotifications(emailNotificaitons);
			this.createdBy = createdByUser;
		}
	}

	/**
	 * @return the enableApprovalReminder
	 */
	public Boolean getEnableApprovalReminder() {
		return enableApprovalReminder;
	}

	/**
	 * @param enableApprovalReminder the enableApprovalReminder to set
	 */
	public void setEnableApprovalReminder(Boolean enableApprovalReminder) {
		this.enableApprovalReminder = enableApprovalReminder;
	}

	/**
	 * @return the reminderAfterHour
	 */
	public Integer getReminderAfterHour() {
		return reminderAfterHour;
	}

	/**
	 * @param reminderAfterHour the reminderAfterHour to set
	 */
	public void setReminderAfterHour(Integer reminderAfterHour) {
		this.reminderAfterHour = reminderAfterHour;
	}

	/**
	 * @return the reminderCount
	 */
	public Integer getReminderCount() {
		return reminderCount;
	}

	/**
	 * @param reminderCount the reminderCount to set
	 */
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}

	/**
	 * @return the rfxEnvelopeReadOnly
	 */
	public Boolean getRfxEnvelopeReadOnly() {
		return rfxEnvelopeReadOnly;
	}

	/**
	 * @param rfxEnvelopeReadOnly the rfxEnvelopeReadOnly to set
	 */
	public void setRfxEnvelopeReadOnly(Boolean rfxEnvelopeReadOnly) {
		this.rfxEnvelopeReadOnly = rfxEnvelopeReadOnly;
	}

	/**
	 * @return the rfxEnvelopeOpening
	 */
	public Boolean getRfxEnvelopeOpening() {
		return rfxEnvelopeOpening;
	}

	/**
	 * @param rfxEnvelopeOpening the rfxEnvelopeOpening to set
	 */
	public void setRfxEnvelopeOpening(Boolean rfxEnvelopeOpening) {
		this.rfxEnvelopeOpening = rfxEnvelopeOpening;
	}

	/**
	 * @return the rfxEnvOpeningAfter
	 */
	public String getRfxEnvOpeningAfter() {
		return rfxEnvOpeningAfter;
	}

	/**
	 * @param rfxEnvOpeningAfter the rfxEnvOpeningAfter to set
	 */
	public void setRfxEnvOpeningAfter(String rfxEnvOpeningAfter) {
		this.rfxEnvOpeningAfter = rfxEnvOpeningAfter;
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
	 * @return the eventOwner
	 */
	public User getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(User eventOwner) {
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
	 * @return the eventVisibility
	 */
	public EventVisibilityType getEventVisibility() {
		return eventVisibility;
	}

	/**
	 * @param eventVisibility the eventVisibility to set
	 */
	public void setEventVisibility(EventVisibilityType eventVisibility) {
		this.eventVisibility = eventVisibility;
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
	 * @return the submissionValidityDays
	 */
	public Integer getSubmissionValidityDays() {
		return submissionValidityDays;
	}

	/**
	 * @param submissionValidityDays the submissionValidityDays to set
	 */
	public void setSubmissionValidityDays(Integer submissionValidityDays) {
		this.submissionValidityDays = submissionValidityDays;
	}

	/**
	 * @return the industryCategory
	 */
	public IndustryCategory getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(IndustryCategory industryCategory) {
		this.industryCategory = industryCategory;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the participationFees
	 */
	public BigDecimal getParticipationFees() {
		return participationFees;
	}

	/**
	 * @param participationFees the participationFees to set
	 */
	public void setParticipationFees(BigDecimal participationFees) {
		this.participationFees = participationFees;
	}

	/**
	 * @return the participationFeeCurrency
	 */
	public Currency getParticipationFeeCurrency() {
		return participationFeeCurrency;
	}

	/**
	 * @param participationFeeCurrency the participationFeeCurrency to set
	 */
	public void setParticipationFeeCurrency(Currency participationFeeCurrency) {
		this.participationFeeCurrency = participationFeeCurrency;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @return the eventVisibilityDates
	 */
	public String getEventVisibilityDates() {
		return eventVisibilityDates;
	}

	/**
	 * @param eventVisibilityDates the eventVisibilityDates to set
	 */
	public void setEventVisibilityDates(String eventVisibilityDates) {
		this.eventVisibilityDates = eventVisibilityDates;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public EventStatus getStatus() {
		return status;
	}

	public void setStatus(EventStatus status) {
		this.status = status;
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
	 * @return the deliveryAddress
	 */
	public BuyerAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(BuyerAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the baseCurrency
	 */
	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
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

	/**
	 * @return the costCenter
	 */
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the budgetAmount
	 */
	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	/**
	 * @param budgetAmount the budgetAmount to set
	 */
	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	/**
	 * @return the historicaAmount
	 */
	public BigDecimal getHistoricaAmount() {
		return historicaAmount;
	}

	/**
	 * @param historicaAmount the historicaAmount to set
	 */
	public void setHistoricaAmount(BigDecimal historicaAmount) {
		this.historicaAmount = historicaAmount;
	}

	/**
	 * @return the paymentTerm
	 */
	public String getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm the paymentTerm to set
	 */
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the documentReq
	 */
	public Boolean getDocumentReq() {
		return documentReq;
	}

	/**
	 * @param documentReq the documentReq to set
	 */
	public void setDocumentReq(Boolean documentReq) {
		this.documentReq = documentReq;
	}

	/**
	 * @return the meetingReq
	 */
	public Boolean getMeetingReq() {
		return meetingReq;
	}

	/**
	 * @param meetingReq the meetingReq to set
	 */
	public void setMeetingReq(Boolean meetingReq) {
		this.meetingReq = meetingReq;
	}

	/**
	 * @return the questionnaires
	 */
	public Boolean getQuestionnaires() {
		return questionnaires;
	}

	/**
	 * @param questionnaires the questionnaires to set
	 */
	public void setQuestionnaires(Boolean questionnaires) {
		this.questionnaires = questionnaires;
	}

	/**
	 * @return the billOfQuantity
	 */
	public Boolean getBillOfQuantity() {
		return billOfQuantity;
	}

	/**
	 * @param billOfQuantity the billOfQuantity to set
	 */
	public void setBillOfQuantity(Boolean billOfQuantity) {
		this.billOfQuantity = billOfQuantity;
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
	 * @return the rfxTemplate
	 */
	public RfxTemplate getTemplate() {
		return template;
	}

	/**
	 * @param rfxTemplate the rfxTemplate to set
	 */
	public void setTemplate(RfxTemplate rfxTemplate) {
		this.template = rfxTemplate;
	}

	/**
	 * @return the documentCompleted
	 */
	public Boolean getDocumentCompleted() {
		return documentCompleted;
	}

	/**
	 * @param documentCompleted the documentCompleted to set
	 */
	public void setDocumentCompleted(Boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	/**
	 * @return the meetingCompleted
	 */
	public Boolean getMeetingCompleted() {
		return meetingCompleted;
	}

	/**
	 * @param meetingCompleted the meetingCompleted to set
	 */
	public void setMeetingCompleted(Boolean meetingCompleted) {
		this.meetingCompleted = meetingCompleted;
	}

	/**
	 * @return the cqCompleted
	 */
	public Boolean getCqCompleted() {
		return cqCompleted;
	}

	/**
	 * @param cqCompleted the cqCompleted to set
	 */
	public void setCqCompleted(Boolean cqCompleted) {
		this.cqCompleted = cqCompleted;
	}

	/**
	 * @return the bqCompleted
	 */
	public Boolean getBqCompleted() {
		return bqCompleted;
	}

	/**
	 * @param bqCompleted the bqCompleted to set
	 */
	public void setBqCompleted(Boolean bqCompleted) {
		this.bqCompleted = bqCompleted;
	}

	/**
	 * @return the supplierCompleted
	 */
	public Boolean getSupplierCompleted() {
		return supplierCompleted;
	}

	/**
	 * @param supplierCompleted the supplierCompleted to set
	 */
	public void setSupplierCompleted(Boolean supplierCompleted) {
		this.supplierCompleted = supplierCompleted;
	}

	/**
	 * @return the envelopCompleted
	 */
	public Boolean getEnvelopCompleted() {
		return envelopCompleted;
	}

	/**
	 * @param envelopCompleted the envelopCompleted to set
	 */
	public void setEnvelopCompleted(Boolean envelopCompleted) {
		this.envelopCompleted = envelopCompleted;
	}

	/**
	 * @return the eventDetailCompleted
	 */
	public Boolean getEventDetailCompleted() {
		return eventDetailCompleted;
	}

	/**
	 * @param eventDetailCompleted the eventDetailCompleted to set
	 */
	public void setEventDetailCompleted(Boolean eventDetailCompleted) {
		this.eventDetailCompleted = eventDetailCompleted;
	}

	/**
	 * @return the summaryCompleted
	 */
	public Boolean getSummaryCompleted() {
		return summaryCompleted;
	}

	/**
	 * @param summaryCompleted the summaryCompleted to set
	 */
	public void setSummaryCompleted(Boolean summaryCompleted) {
		this.summaryCompleted = summaryCompleted;
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
	 * @return the suspensionType
	 */
	public SuspensionType getSuspensionType() {
		return suspensionType;
	}

	/**
	 * @param suspensionType the suspensionType to set
	 */
	public void setSuspensionType(SuspensionType suspensionType) {
		this.suspensionType = suspensionType;
	}

	/**
	 * @return the eventPublishTime
	 */
	public Date getEventPublishTime() {
		return eventPublishTime;
	}

	/**
	 * @param eventPublishTime the eventPublishTime to set
	 */
	public void setEventPublishTime(Date eventPublishTime) {
		this.eventPublishTime = eventPublishTime;
	}

	/**
	 * @return the cancelReason
	 */
	public String getCancelReason() {
		return cancelReason;
	}

	/**
	 * @param cancelReason the cancelReason to set
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	 * @return the previousEventId
	 */
	public String getPreviousEventId() {
		return previousEventId;
	}

	/**
	 * @param previousEventId the previousEventId to set
	 */
	public void setPreviousEventId(String previousEventId) {
		this.previousEventId = previousEventId;
	}

	/**
	 * @return the concludeRemarks
	 */
	public String getConcludeRemarks() {
		return concludeRemarks;
	}

	/**
	 * @param concludeRemarks the concludeRemarks to set
	 */
	public void setConcludeRemarks(String concludeRemarks) {
		this.concludeRemarks = concludeRemarks;
	}

	/**
	 * @return the nextEventId
	 */
	public String getNextEventId() {
		return nextEventId;
	}

	/**
	 * @param nextEventId the nextEventId to set
	 */
	public void setNextEventId(String nextEventId) {
		this.nextEventId = nextEventId;
	}

	/**
	 * @return the previousEventType
	 */
	public RfxTypes getPreviousEventType() {
		return previousEventType;
	}

	/**
	 * @param previousEventType the previousEventType to set
	 */
	public void setPreviousEventType(RfxTypes previousEventType) {
		this.previousEventType = previousEventType;
	}

	/**
	 * @return the nextEventType
	 */
	public RfxTypes getNextEventType() {
		return nextEventType;
	}

	/**
	 * @param nextEventType the nextEventType to set
	 */
	public void setNextEventType(RfxTypes nextEventType) {
		this.nextEventType = nextEventType;
	}

	/**
	 * @return the suspendRemarks
	 */
	public String getSuspendRemarks() {
		return suspendRemarks;
	}

	/**
	 * @param suspendRemarks the suspendRemarks to set
	 */
	public void setSuspendRemarks(String suspendRemarks) {
		this.suspendRemarks = suspendRemarks;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the startMessageSent
	 */
	public Boolean getStartMessageSent() {
		return startMessageSent;
	}

	/**
	 * @param startMessageSent the startMessageSent to set
	 */
	public void setStartMessageSent(Boolean startMessageSent) {
		this.startMessageSent = startMessageSent;
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
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the awarded
	 */
	public Boolean getAwarded() {
		return awarded;
	}

	/**
	 * @param awarded the awarded to set
	 */
	public void setAwarded(Boolean awarded) {
		this.awarded = awarded;
	}

	/**
	 * @return the erpDocNo
	 */
	public String getErpDocNo() {
		return erpDocNo;
	}

	/**
	 * @param erpDocNo the erpDocNo to set
	 */
	public void setErpDocNo(String erpDocNo) {
		this.erpDocNo = erpDocNo;
	}

	/**
	 * @return the concludeDate
	 */
	public Date getConcludeDate() {
		return concludeDate;
	}

	/**
	 * @param concludeDate the concludeDate to set
	 */
	public void setConcludeDate(Date concludeDate) {
		this.concludeDate = concludeDate;
	}

	/**
	 * @return the concludeBy
	 */
	public User getConcludeBy() {
		return concludeBy;
	}

	/**
	 * @param concludeBy the concludeBy to set
	 */
	public void setConcludeBy(User concludeBy) {
		this.concludeBy = concludeBy;
	}

	public boolean getTemplateActive() {
		return templateActive;
	}

	public void setTemplateActive(boolean templateActive) {
		this.templateActive = templateActive;
	}

	public Boolean getViewSupplerName() {
		return viewSupplerName;
	}

	public void setViewSupplerName(Boolean viewSupplerName) {
		this.viewSupplerName = viewSupplerName;
	}

	public Boolean getCloseEnvelope() {
		return closeEnvelope;
	}

	public void setCloseEnvelope(Boolean closeEnvelope) {
		this.closeEnvelope = closeEnvelope;
	}

	public Boolean getAddSupplier() {
		return addSupplier;
	}

	public void setAddSupplier(Boolean addSupplier) {
		this.addSupplier = addSupplier;
	}

	public Boolean getAllowToSuspendEvent() {
		return allowToSuspendEvent;
	}

	public void setAllowToSuspendEvent(Boolean allowToSuspendEvent) {
		this.allowToSuspendEvent = allowToSuspendEvent;
	}

	/**
	 * @return the previousRequestId
	 */
	public String getPreviousRequestId() {
		return previousRequestId;
	}

	/**
	 * @param previousRequestId the previousRequestId to set
	 */
	public void setPreviousRequestId(String previousRequestId) {
		this.previousRequestId = previousRequestId;
	}

	/**
	 * @return the rfaAuctionType
	 */
	public AuctionType getRfaAuctionType() {
		return rfaAuctionType;
	}

	/**
	 * @param rfaAuctionType the rfaAuctionType to set
	 */
	public void setRfaAuctionType(AuctionType rfaAuctionType) {
		this.rfaAuctionType = rfaAuctionType;
	}

	public Boolean getDisableMasking() {
		return disableMasking;
	}

	public void setDisableMasking(Boolean disableMasking) {
		this.disableMasking = disableMasking;
	}

	/**
	 * @return the deposit
	 */
	public BigDecimal getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	/**
	 * @return the depositCurrency
	 */
	public Currency getDepositCurrency() {
		return depositCurrency;
	}

	/**
	 * @param depositCurrency the depositCurrency to set
	 */
	public void setDepositCurrency(Currency depositCurrency) {
		this.depositCurrency = depositCurrency;
	}

	/**
	 * @return the unMaskedUser
	 */
	public User getUnMaskedUser() {
		return unMaskedUser;
	}

	/**
	 * @param unMaskedUser the unMaskedUser to set
	 */
	public void setUnMaskedUser(User unMaskedUser) {
		this.unMaskedUser = unMaskedUser;
	}

	/**
	 * @return the internalRemarks
	 */
	public String getInternalRemarks() {
		return internalRemarks;
	}

	/**
	 * @param internalRemarks the internalRemarks to set
	 */
	public void setInternalRemarks(String internalRemarks) {
		this.internalRemarks = internalRemarks;
	}

	public Date getAwardDate() {
		return awardDate;
	}

	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}

	public Date getEventPushDate() {
		return eventPushDate;
	}

	public void setEventPushDate(Date eventPushDate) {
		this.eventPushDate = eventPushDate;
	}

	public BigDecimal getMinimumSupplierRating() {
		return minimumSupplierRating;
	}

	public void setMinimumSupplierRating(BigDecimal minimumSupplierRating) {
		this.minimumSupplierRating = minimumSupplierRating;
	}

	public BigDecimal getMaximumSupplierRating() {
		return maximumSupplierRating;
	}

	public void setMaximumSupplierRating(BigDecimal maximumSupplierRating) {
		this.maximumSupplierRating = maximumSupplierRating;
	}

	public Boolean getErpEnable() {
		return erpEnable;
	}

	public void setErpEnable(Boolean erpEnable) {
		this.erpEnable = erpEnable;
	}

	/**
	 * @return the evaluationProcessDeclaration
	 */
	public Declaration getEvaluationProcessDeclaration() {
		return evaluationProcessDeclaration;
	}

	/**
	 * @param evaluationProcessDeclaration the evaluationProcessDeclaration to set
	 */
	public void setEvaluationProcessDeclaration(Declaration evaluationProcessDeclaration) {
		this.evaluationProcessDeclaration = evaluationProcessDeclaration;
	}

	/**
	 * @return the supplierAcceptanceDeclaration
	 */
	public Declaration getSupplierAcceptanceDeclaration() {
		return supplierAcceptanceDeclaration;
	}

	/**
	 * @param supplierAcceptanceDeclaration the supplierAcceptanceDeclaration to set
	 */
	public void setSupplierAcceptanceDeclaration(Declaration supplierAcceptanceDeclaration) {
		this.supplierAcceptanceDeclaration = supplierAcceptanceDeclaration;
	}

	/**
	 * @return the enableEvaluationDeclaration
	 */
	public Boolean getEnableEvaluationDeclaration() {
		return enableEvaluationDeclaration;
	}

	/**
	 * @param enableEvaluationDeclaration the enableEvaluationDeclaration to set
	 */
	public void setEnableEvaluationDeclaration(Boolean enableEvaluationDeclaration) {
		this.enableEvaluationDeclaration = enableEvaluationDeclaration;
	}

	/**
	 * @return the enableSupplierDeclaration
	 */
	public Boolean getEnableSupplierDeclaration() {
		return enableSupplierDeclaration;
	}

	/**
	 * @param enableSupplierDeclaration the enableSupplierDeclaration to set
	 */
	public void setEnableSupplierDeclaration(Boolean enableSupplierDeclaration) {
		this.enableSupplierDeclaration = enableSupplierDeclaration;
	}

	/**
	 * @return the enableEvaluationConclusionUsers
	 */
	public Boolean getEnableEvaluationConclusionUsers() {
		return enableEvaluationConclusionUsers;
	}

	/**
	 * @param enableEvaluationConclusionUsers the enableEvaluationConclusionUsers to set
	 */
	public void setEnableEvaluationConclusionUsers(Boolean enableEvaluationConclusionUsers) {
		this.enableEvaluationConclusionUsers = enableEvaluationConclusionUsers;
	}

	/**
	 * @return the evaluationConclusionEnvelopeEvaluatedCount
	 */
	public Integer getEvaluationConclusionEnvelopeEvaluatedCount() {
		return evaluationConclusionEnvelopeEvaluatedCount;
	}

	/**
	 * @param evaluationConclusionEnvelopeEvaluatedCount the evaluationConclusionEnvelopeEvaluatedCount to set
	 */
	public void setEvaluationConclusionEnvelopeEvaluatedCount(Integer evaluationConclusionEnvelopeEvaluatedCount) {
		this.evaluationConclusionEnvelopeEvaluatedCount = evaluationConclusionEnvelopeEvaluatedCount;
	}

	/**
	 * @return the evaluationConclusionEnvelopeNonEvaluatedCount
	 */
	public Integer getEvaluationConclusionEnvelopeNonEvaluatedCount() {
		return evaluationConclusionEnvelopeNonEvaluatedCount;
	}

	/**
	 * @param evaluationConclusionEnvelopeNonEvaluatedCount the evaluationConclusionEnvelopeNonEvaluatedCount to set
	 */
	public void setEvaluationConclusionEnvelopeNonEvaluatedCount(Integer evaluationConclusionEnvelopeNonEvaluatedCount) {
		this.evaluationConclusionEnvelopeNonEvaluatedCount = evaluationConclusionEnvelopeNonEvaluatedCount;
	}

	/**
	 * @return the evaluationConclusionDisqualifiedSupplierCount
	 */
	public Integer getEvaluationConclusionDisqualifiedSupplierCount() {
		return evaluationConclusionDisqualifiedSupplierCount;
	}

	/**
	 * @param evaluationConclusionDisqualifiedSupplierCount the evaluationConclusionDisqualifiedSupplierCount to set
	 */
	public void setEvaluationConclusionDisqualifiedSupplierCount(Integer evaluationConclusionDisqualifiedSupplierCount) {
		this.evaluationConclusionDisqualifiedSupplierCount = evaluationConclusionDisqualifiedSupplierCount;
	}

	/**
	 * @return the evaluationConclusionRemainingSupplierCount
	 */
	public Integer getEvaluationConclusionRemainingSupplierCount() {
		return evaluationConclusionRemainingSupplierCount;
	}

	/**
	 * @param evaluationConclusionRemainingSupplierCount the evaluationConclusionRemainingSupplierCount to set
	 */
	public void setEvaluationConclusionRemainingSupplierCount(Integer evaluationConclusionRemainingSupplierCount) {
		this.evaluationConclusionRemainingSupplierCount = evaluationConclusionRemainingSupplierCount;
	}

	/**
	 * @return the notifyEventOwner
	 */
	public Boolean getNotifyEventOwner() {
		return notifyEventOwner;
	}

	/**
	 * @param notifyEventOwner the notifyEventOwner to set
	 */
	public void setNotifyEventOwner(Boolean notifyEventOwner) {
		this.notifyEventOwner = notifyEventOwner;
	}

	/**
	 * @return the enableSuspensionApproval
	 */
	public Boolean getEnableSuspensionApproval() {
		return enableSuspensionApproval;
	}

	/**
	 * @param enableSuspensionApproval the enableSuspensionApproval to set
	 */
	public void setEnableSuspensionApproval(Boolean enableSuspensionApproval) {
		this.enableSuspensionApproval = enableSuspensionApproval;
	}

	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
	}

	public ProcurementMethod getProcurementMethod() {
		return procurementMethod;
	}

	public void setProcurementMethod(ProcurementMethod procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	public ProcurementCategories getProcurementCategories() {
		return procurementCategories;
	}

	public void setProcurementCategories(ProcurementCategories procurementCategories) {
		this.procurementCategories = procurementCategories;
	}

	/**
	 * @return the groupCode
	 */
	public GroupCode getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(GroupCode groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the allowDisqualifiedSupplierDownload
	 */
	public Boolean getAllowDisqualifiedSupplierDownload() {
		return allowDisqualifiedSupplierDownload;
	}

	/**
	 * @param allowDisqualifiedSupplierDownload the allowDisqualifiedSupplierDownload to set
	 */
	public void setAllowDisqualifiedSupplierDownload(Boolean allowDisqualifiedSupplierDownload) {
		this.allowDisqualifiedSupplierDownload = allowDisqualifiedSupplierDownload;
	}

	public Boolean getTransfrAwrdRespFlag() {
		return transfrAwrdRespFlag;
	}

	public void setTransfrAwrdRespFlag(Boolean transfrAwrdRespFlag) {
		this.transfrAwrdRespFlag = transfrAwrdRespFlag;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		result = prime * result + ((referanceNumber == null) ? 0 : referanceNumber.hashCode());
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
		Event other = (Event) obj;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (eventName == null) {
			if (other.eventName != null)
				return false;
		} else if (!eventName.equals(other.eventName))
			return false;
		if (referanceNumber == null) {
			if (other.referanceNumber != null)
				return false;
		} else if (!referanceNumber.equals(other.referanceNumber))
			return false;
		return true;
	}

	public String toLogString() {
		try {
			return "Event [id=" + id + "eventId=" + eventId + ",  referanceNumber=" + referanceNumber + ", eventName=" + eventName + ", eventVisibility=" + eventVisibility + ", eventStart=" + eventStart + ", eventEnd=" + eventEnd + ", eventPublishDate=" + eventPublishDate + ", submissionValidityDays=" + submissionValidityDays + ", participationFees=" + participationFees + ", TenantId =" + tenantId + "]";
		} catch (Exception e) {
		}
		return "Event eventId=" + eventId;
	}

	public Boolean getScheduleOfRate() {
		return scheduleOfRate;
	}

	public void setScheduleOfRate(Boolean scheduleOfRate) {
		this.scheduleOfRate = scheduleOfRate;
	}

	public Boolean getSorCompleted() {
		return sorCompleted;
	}

	public void setSorCompleted(Boolean sorCompleted) {
		this.sorCompleted = sorCompleted;
	}
}
