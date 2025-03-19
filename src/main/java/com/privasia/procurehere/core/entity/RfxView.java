package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.converter.RfxTypesCoverter;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * <p>
 * CREATE OR REPLACE VIEW PROC_RFX_EVENTS AS<br />
 * SELECT e.*, s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME,
 * s.SUPPLIER_BID_SUBMITTED_TIME, 'Request for Information' as EVENT_TYPE FROM PROC_RFI_EVENTS e left outer join
 * PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID<br />
 * UNION<br />
 * SELECT e.*, s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME,
 * s.SUPPLIER_BID_SUBMITTED_TIME, 'Request for Tender' as EVENT_TYPE FROM PROC_RFT_EVENTS e left outer join
 * PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID<br />
 * UNION<br />
 * SELECT e.*, s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME,
 * s.SUPPLIER_BID_SUBMITTED_TIME, 'Request for Proposal' as EVENT_TYPE FROM PROC_RFP_EVENTS e left outer join
 * PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID<br />
 * UNION<br />
 * SELECT e.*, s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME,
 * s.SUPPLIER_BID_SUBMITTED_TIME, 'Request for Quotation' as EVENT_TYPE FROM PROC_RFQ_EVENTS e left outer join
 * PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID
 * </p>
 * 
 * @author Najeer
 */
@Entity
@Table(name = "PROC_RFX_EVENTS")
@Immutable
@Subselect("select * from PROC_RFX_EVENTS")
@SqlResultSetMappings( //
{ @SqlResultSetMapping(name = "draftEventResult", classes = { @ConstructorResult(targetClass = DraftEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "type"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "unitName"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventUser") }) }), //
		@SqlResultSetMapping(name = "supplierInvitedEventResult", classes = { @ConstructorResult(targetClass = RfxView.class, columns = { @ColumnResult(name = "ID"), @ColumnResult(name = "EVENT_NAME"), @ColumnResult(name = "REFERANCE_NUMBER"), @ColumnResult(name = "EVENT_START"), @ColumnResult(name = "EVENT_END"), @ColumnResult(name = "STATUS"), @ColumnResult(name = "EVENT_TYPE") }) }) })
public class RfxView extends Event implements Serializable {

	private static final long serialVersionUID = 3040584450680924150L;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true)
	private Supplier supplier;

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

	@Column(name = "EVENT_TYPE")
	@Convert(converter = RfxTypesCoverter.class)
	private RfxTypes type;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUBMISSION_STATUS")
	private SubmissionStatusType submissionStatus;

	public RfxView() {

	}

	public RfxView(String id, Date eventPublishDate, String eventName, RfxTypes type, Date eventEnd) {
		setId(id);
		setEventPublishDate(eventPublishDate);
		setEventName(eventName);
		setType(type);
		setEventEnd(eventEnd);
	}

	public RfxView(String id, Date eventPublishDate, String eventName, RfxTypes type, Date eventEnd, String referanceNumber) {
		setId(id);
		setEventPublishDate(eventPublishDate);
		setEventName(eventName);
		setType(type);
		setEventEnd(eventEnd);
		setReferanceNumber(referanceNumber);
	}

	public RfxView(String id, Date eventStart, String eventName, String referanceNumber, RfxTypes type) {
		setId(id);
		setEventStart(eventStart);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setType(type);
	}

	public RfxView(String id, Date eventStart, String eventName, String referanceNumber, RfxTypes type, User eventOwner) {
		setId(id);
		setEventStart(eventStart);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setType(type);
		eventOwner.getName();
		setEventOwner(eventOwner);
	}

	public RfxView(String id, Date eventStart, Date eventEnd, String eventName, String referanceNumber, EventStatus status, RfxTypes type) {
		setId(id);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setStatus(status);
		setType(type);
	}

	public RfxView(String id, String eventName, String referanceNumber, Date eventStart, Date eventEnd, String status, String type) {
		setId(id);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setStatus(EventStatus.valueOf(status));
		setType(RfxTypes.valueOf(type));
	}

	public RfxView(String id, String eventId, Date eventStart, Date eventEnd, String eventName, String referanceNumber, EventStatus status, RfxTypes type) {
		setId(id);
		this.setEventId(eventId);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
		setStatus(status);
		setType(type);
	}

	public RfxView(String id, Date eventPublishDate, String eventName, String refranceNumber, RfxTypes type, Date eventEnd, String communicationEmail, boolean emailNotifications, String tenantId, String name, String userID, String businessUnit) {
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
		createdBy.setEmailNotifications(emailNotifications);
		setCreatedBy(createdBy);
		setTenantId(tenantId);
		BusinessUnit unit = new BusinessUnit();
		unit.setDisplayName(businessUnit);
		unit.setUnitName(businessUnit);
		setBusinessUnit(unit);
	}

	public RfxView(String id, Date eventPublishDate, String eventName, String refranceNumber, RfxTypes type, Date eventEnd, Date eventStart, String communicationEmail, boolean emailNotifications, String tenantId, String name, String userID, String businessUnit) {
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
		createdBy.setEmailNotifications(emailNotifications);
		setCreatedBy(createdBy);

		User eventOwner = new User();
		eventOwner.setId(userID);
		eventOwner.setName(name);
		eventOwner.setTenantId(tenantId);
		eventOwner.setCommunicationEmail(communicationEmail);
		eventOwner.setEmailNotifications(emailNotifications);
		setEventOwner(eventOwner);

		setTenantId(tenantId);
		BusinessUnit unit = new BusinessUnit();
		unit.setDisplayName(businessUnit);
		unit.setUnitName(businessUnit);
		setBusinessUnit(unit);
	}

	public RfxView(String id, Date eventPublishDate, String eventName, String refranceNumber, RfxTypes type, Date eventEnd, Date eventStart, String communicationEmail,
				String tenantId, String name, String userID, String businessUnit, EventVisibilityType eventVisibility, String eventId, Boolean emailNotifications) {
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
		createdBy.setEmailNotifications(emailNotifications);
		setCreatedBy(createdBy);

		User eventOwner = new User();
		eventOwner.setId(userID);
		eventOwner.setName(name);
		eventOwner.setTenantId(tenantId);
		eventOwner.setCommunicationEmail(communicationEmail);
		eventOwner.setEmailNotifications(emailNotifications);
		setEventOwner(eventOwner);

		setTenantId(tenantId);
		BusinessUnit unit = new BusinessUnit();
		unit.setDisplayName(businessUnit);
		unit.setUnitName(businessUnit);
		setBusinessUnit(unit);
		setEventVisibility(eventVisibility);
		setEventId(eventId);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RfxView other = (RfxView) obj;
		if (type != other.type)
			return false;
		return true;
	}

}
