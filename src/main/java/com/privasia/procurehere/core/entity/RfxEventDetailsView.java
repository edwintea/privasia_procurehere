package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.converter.RfxTypesCoverter;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_RFX_EVENT_DETAILS")
@Immutable
@Subselect("select * from PROC_RFX_EVENT_DETAILS")
public class RfxEventDetailsView implements Serializable {

	private static final long serialVersionUID = -3512478823294172309L;

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
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true)
	private BusinessUnit businessUnit;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@Column(name = "START_MESSAGE_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean startMessageSent;

	@Column(name = "EVENT_TYPE")
	@Convert(converter = RfxTypesCoverter.class)
	private RfxTypes type;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true)
	private Supplier supplier;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public User getEventOwner() {
		return eventOwner;
	}

	public void setEventOwner(User eventOwner) {
		this.eventOwner = eventOwner;
	}

	public String getReferanceNumber() {
		return referanceNumber;
	}

	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Date getEventStart() {
		return eventStart;
	}

	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	public Date getEventEnd() {
		return eventEnd;
	}

	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public User getActionBy() {
		return actionBy;
	}

	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
	}

	public Boolean getStartMessageSent() {
		return startMessageSent;
	}

	public void setStartMessageSent(Boolean startMessageSent) {
		this.startMessageSent = startMessageSent;
	}

	public RfxTypes getType() {
		return type;
	}

	public void setType(RfxTypes type) {
		this.type = type;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

}
