package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.converter.RfxTypesCoverter;
import com.privasia.procurehere.core.utils.CustomDateSerializer;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * <p>
 * CREATE OR REPLACE VIEW PROC_RFX_SIMPLE_EVENTS AS 
 * SELECT e.ID,e.CONCLUDE_BY, e.IS_ERP_ENABLE, e.DELIVERY_DATE,e.CONCLUDE_DATE, e.URGENT_EVENT, e.START_MESSAGE_SENT, e.SUSPEND_REMARKS, e.CANCEL_REASON, e.PREVIOUS_EVENT, e.NEXT_EVENT, e.PREVIOUS_EVENT_TYPE, e.NEXT_EVENT_TYPE, e.CONCLUDE_REMARKS, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.BUSINESS_UNIT_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,  e.SUSPENSION_TYPE, 'Request for Information' as EVENT_TYPE ,e.IS_AWARDED ,e.ERP_DOC_NO ,e.VIEW_SUPPLIER_NAME,e.ALLOW_CLOSE_ENVELOPE,e.ALLOW_ADD_SUPPLIER,e.ALLOW_TO_SUSPEND_EVENT,e.PREVIOUS_REQUEST,e.DEPOSIT,e.DEPOSIT_CURRENCY,e.DISABLE_MASKING,e.UNMASKED_BY,e.INTERNAL_REMARKS,e.MINIMUM_SUPPLIER_RATING,e.MAXIMUM_SUPPLIER_RATING,e.EVENT_PUSH_DATE,e.AWARD_DATE  FROM PROC_RFI_EVENTS e  
 * UNION 
 * SELECT e.ID,e.CONCLUDE_BY, e.IS_ERP_ENABLE, e.DELIVERY_DATE,e.CONCLUDE_DATE, e.URGENT_EVENT, e.START_MESSAGE_SENT, e.SUSPEND_REMARKS, e.CANCEL_REASON, e.PREVIOUS_EVENT, e.NEXT_EVENT, e.PREVIOUS_EVENT_TYPE, e.NEXT_EVENT_TYPE, e.CONCLUDE_REMARKS, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.BUSINESS_UNIT_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,  e.SUSPENSION_TYPE, 'Request for Tender' as EVENT_TYPE ,e.IS_AWARDED ,e.ERP_DOC_NO ,e.VIEW_SUPPLIER_NAME,e.ALLOW_CLOSE_ENVELOPE,e.ALLOW_ADD_SUPPLIER,e.ALLOW_TO_SUSPEND_EVENT,e.PREVIOUS_REQUEST,e.DEPOSIT,e.DEPOSIT_CURRENCY,e.DISABLE_MASKING,e.UNMASKED_BY,e.INTERNAL_REMARKS,e.MINIMUM_SUPPLIER_RATING,e.MAXIMUM_SUPPLIER_RATING,e.EVENT_PUSH_DATE, e.AWARD_DATE   FROM PROC_RFT_EVENTS e  
 * UNION 
 * SELECT e.ID,e.CONCLUDE_BY, e.IS_ERP_ENABLE, e.DELIVERY_DATE,e.CONCLUDE_DATE, e.URGENT_EVENT, e.START_MESSAGE_SENT, e.SUSPEND_REMARKS, e.CANCEL_REASON, e.PREVIOUS_EVENT, e.NEXT_EVENT, e.PREVIOUS_EVENT_TYPE, e.NEXT_EVENT_TYPE, e.CONCLUDE_REMARKS, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.BUSINESS_UNIT_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,  e.SUSPENSION_TYPE, 'Request for Proposal' as EVENT_TYPE ,e.IS_AWARDED ,e.ERP_DOC_NO ,e.VIEW_SUPPLIER_NAME,e.ALLOW_CLOSE_ENVELOPE,e.ALLOW_ADD_SUPPLIER,e.ALLOW_TO_SUSPEND_EVENT,e.PREVIOUS_REQUEST,e.DEPOSIT,e.DEPOSIT_CURRENCY,e.DISABLE_MASKING ,e.UNMASKED_BY,e.INTERNAL_REMARKS,e.MINIMUM_SUPPLIER_RATING,e.MAXIMUM_SUPPLIER_RATING,e.EVENT_PUSH_DATE,e.AWARD_DATE  FROM PROC_RFP_EVENTS  e
 * UNION 
 * SELECT e.ID,e.CONCLUDE_BY, e.IS_ERP_ENABLE, e.DELIVERY_DATE,e.CONCLUDE_DATE, e.URGENT_EVENT, e.START_MESSAGE_SENT, e.SUSPEND_REMARKS, e.CANCEL_REASON, e.PREVIOUS_EVENT, e.NEXT_EVENT, e.PREVIOUS_EVENT_TYPE, e.NEXT_EVENT_TYPE, e.CONCLUDE_REMARKS, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.BUSINESS_UNIT_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,  e.SUSPENSION_TYPE, 'Request for Quotation' as EVENT_TYPE,e.IS_AWARDED ,e.ERP_DOC_NO ,e.VIEW_SUPPLIER_NAME,e.ALLOW_CLOSE_ENVELOPE,e.ALLOW_ADD_SUPPLIER,e.ALLOW_TO_SUSPEND_EVENT,e.PREVIOUS_REQUEST,e.DEPOSIT,e.DEPOSIT_CURRENCY,e.DISABLE_MASKING ,e.UNMASKED_BY,e.INTERNAL_REMARKS,e.MINIMUM_SUPPLIER_RATING,e.MAXIMUM_SUPPLIER_RATING,e.EVENT_PUSH_DATE,e.AWARD_DATE  FROM PROC_RFQ_EVENTS  e
 * UNION 
 * SELECT e.ID,e.CONCLUDE_BY, e.IS_ERP_ENABLE, e.DELIVERY_DATE,e.CONCLUDE_DATE, e.URGENT_EVENT, e.START_MESSAGE_SENT, e.SUSPEND_REMARKS, e.CANCEL_REASON, e.PREVIOUS_EVENT, e.NEXT_EVENT, e.PREVIOUS_EVENT_TYPE, e.NEXT_EVENT_TYPE, e.CONCLUDE_REMARKS, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.BUSINESS_UNIT_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,  e.SUSPENSION_TYPE, 'Request for Auction' as EVENT_TYPE ,e.IS_AWARDED ,e.ERP_DOC_NO ,e.VIEW_SUPPLIER_NAME,e.ALLOW_CLOSE_ENVELOPE,e.ALLOW_ADD_SUPPLIER,e.ALLOW_TO_SUSPEND_EVENT,e.PREVIOUS_REQUEST,e.DEPOSIT,e.DEPOSIT_CURRENCY,e.DISABLE_MASKING,e.UNMASKED_BY,e.INTERNAL_REMARKS,e.MINIMUM_SUPPLIER_RATING,e.MAXIMUM_SUPPLIER_RATING,e.EVENT_PUSH_DATE,e.AWARD_DATE   FROM PROC_RFA_EVENTS  e
 *
 * </p>
 * 
 * @author ravi
 */
@Entity
@Table(name = "rfx_simple_view_events")
@Immutable
@Subselect("select * from rfx_simple_view_events")
public class RfxSimpleView implements Serializable {

	private static final long serialVersionUID = 3040584450680924150L;

	@Column(name = "EVENT_TYPE")
	@Convert(converter = RfxTypesCoverter.class)
	private RfxTypes type;

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

	@Column(name = "PARTICIPATION_FEES", precision = 12, scale = 6)
	private BigDecimal participationFees;

	@Column(name = "PARTICIPATION_FEE_CURRENCY", nullable = true)
	private String currencyId;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private EventStatus status;

	@Column(name = "EVENT_DESCRIPTION", length = 2000)
	@Size(max = 2000, message = "{event.description.length}")
	private String eventDescription;

	@Column(name = "CURRENCY_ID", nullable = true)
	private String baseCurrencyId;

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

	@Column(name = "TEMPLATE_ID", nullable = true)
	private String templateId;

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

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true)
	private BusinessUnit businessUnit;

	@Column(name = "URGENT_EVENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean urgentEvent;

	@Column(name = "IS_AWARDED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean awarded = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONCLUDE_DATE")
	private Date concludeDate;

	@Size(min = 0, max = 64)
	@Column(name = "PREVIOUS_REQUEST", nullable = true, length = 64)
	String previousRequestId;

	@Column(name = "DEPOSIT", precision = 12, scale = 6)
	private BigDecimal deposit;

	@Column(name = "AWARD_DATE", nullable = true)
	private Date awardDate;

	@Column(name = "MINIMUM_SUPPLIER_RATING", nullable = true)
	private BigDecimal minimumSupplierRating;

	@Column(name = "MAXIMUM_SUPPLIER_RATING", nullable = true)
	private BigDecimal maximumSupplierRating;

	public RfxSimpleView() {

	}

	public RfxSimpleView(String id, Date eventPublishDate, String eventName, RfxTypes type, Date eventEnd) {
		setId(id);
		setEventPublishDate(eventPublishDate);
		setEventName(eventName);
		setType(type);
		setEventEnd(eventEnd);
	}

	public RfxSimpleView(String id, Date eventPublishDate, String eventName, RfxTypes type, Date eventEnd, String referanceNumber) {
		setId(id);
		setEventPublishDate(eventPublishDate);
		setEventName(eventName);
		setType(type);
		setEventEnd(eventEnd);
		setReferanceNumber(referanceNumber);
	}

	public RfxSimpleView(String id, Date eventStart, String eventName, String referanceNumber, RfxTypes type) {
		setId(id);
		setEventStart(eventStart);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setType(type);
	}

	public RfxSimpleView(String id, Date eventStart, String eventName, String referanceNumber, RfxTypes type, User eventOwner) {
		setId(id);
		setEventStart(eventStart);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setType(type);
		eventOwner.getName();
		setEventOwner(eventOwner);
	}

	public RfxSimpleView(String id, Date eventStart, Date eventEnd, String eventName, String referanceNumber, EventStatus status, RfxTypes type) {
		setId(id);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setStatus(status);
		setType(type);
	}

	public RfxSimpleView(String id, String eventName, String referanceNumber, Date eventStart, Date eventEnd, String status, String type) {
		setId(id);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setStatus(EventStatus.valueOf(status));
		setType(RfxTypes.valueOf(type));
	}

	public RfxSimpleView(String id, String eventId, Date eventStart, Date eventEnd, String eventName, String referanceNumber, EventStatus status, RfxTypes type) {
		setId(id);
		this.setEventId(eventId);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setStatus(status);
		setType(type);
	}

	public RfxSimpleView(String id, Date eventPublishDate, String eventName, String refranceNumber, RfxTypes type, Date eventEnd, String communicationEmail, String tenantId, String name, String userID, String businessUnit) {
		setId(id);
		setEventPublishDate(eventPublishDate);
		setEventName(eventName);
		setType(type);
		setEventEnd(eventEnd);
		setReferanceNumber(refranceNumber);
		User createdBy = new User();
		createdBy.setId(userID);
		createdBy.setName(name);
		createdBy.setTenantId(tenantId);
		createdBy.setCommunicationEmail(communicationEmail);
		setTenantId(tenantId);
		BusinessUnit unit = new BusinessUnit();
		unit.setDisplayName(businessUnit);
		unit.setUnitName(businessUnit);
		setBusinessUnit(unit);
	}

	public RfxSimpleView(String id, Date eventPublishDate, String eventName, String refranceNumber, RfxTypes type, Date eventEnd, Date eventStart, String communicationEmail, String tenantId, String name, String userID, String businessUnit) {
		setId(id);
		setEventPublishDate(eventPublishDate);
		setEventName(eventName);
		setType(type);
		setEventEnd(eventEnd);
		setEventStart(eventStart);
		setReferanceNumber(refranceNumber);
		User createdBy = new User();
		createdBy.setId(userID);
		createdBy.setName(name);
		createdBy.setTenantId(tenantId);
		createdBy.setCommunicationEmail(communicationEmail);
 
		User eventOwner = new User();
		eventOwner.setId(userID);
		eventOwner.setName(name);
		eventOwner.setTenantId(tenantId);
		eventOwner.setCommunicationEmail(communicationEmail);
		setEventOwner(eventOwner);

		setTenantId(tenantId);
		BusinessUnit unit = new BusinessUnit();
		unit.setDisplayName(businessUnit);
		unit.setUnitName(businessUnit);
		setBusinessUnit(unit);
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
	 * @return the currencyId
	 */
	public String getCurrencyId() {
		return currencyId;
	}

	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
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
	 * @return the baseCurrencyId
	 */
	public String getBaseCurrencyId() {
		return baseCurrencyId;
	}

	/**
	 * @param baseCurrencyId the baseCurrencyId to set
	 */
	public void setBaseCurrencyId(String baseCurrencyId) {
		this.baseCurrencyId = baseCurrencyId;
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
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
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
	 * @return the awardDate
	 */
	public Date getAwardDate() {
		return awardDate;
	}

	/**
	 * @param awardDate the awardDate to set
	 */
	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}

	/**
	 * @return the minimumSupplierRating
	 */
	public BigDecimal getMinimumSupplierRating() {
		return minimumSupplierRating;
	}

	/**
	 * @param minimumSupplierRating the minimumSupplierRating to set
	 */
	public void setMinimumSupplierRating(BigDecimal minimumSupplierRating) {
		this.minimumSupplierRating = minimumSupplierRating;
	}

	/**
	 * @return the maximumSupplierRating
	 */
	public BigDecimal getMaximumSupplierRating() {
		return maximumSupplierRating;
	}

	/**
	 * @param maximumSupplierRating the maximumSupplierRating to set
	 */
	public void setMaximumSupplierRating(BigDecimal maximumSupplierRating) {
		this.maximumSupplierRating = maximumSupplierRating;
	}

}
