package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;

public class GlobalSearchPojo implements Serializable {

	private static final long serialVersionUID = 3327697931619451550L;

	private String id;
	private String eventName;
	private String eventDescription;
	private String referenceNumber;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm a")
	private Date eventEndDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm a")
	private Date eventStartDate;
	private EventStatus status;
	private String eventOwner;
	private String createdBy;
	private RfxTypes type;

	private String unitName;
	private String eventId;

	private EventPermissions eventPermissions;

	private boolean visible = false;

	private boolean envelopType;

	public GlobalSearchPojo() {
	}

	public GlobalSearchPojo(String id, String eventName, String eventDescription, String referenceNumber, Date eventEndDate, Date eventStartDate, EventStatus status, String eventOwner, RfxTypes type) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.eventEndDate = eventEndDate;
		this.eventStartDate = eventStartDate;
		this.status = status;
		this.eventOwner = eventOwner;
		this.type = type;
	}

	public GlobalSearchPojo(String id, String eventName, String eventDescription, String referenceNumber, Date eventEndDate, Date eventStartDate, String status, String eventOwner, String type, String createdBy) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.eventEndDate = eventEndDate;
		this.eventStartDate = eventStartDate;
		this.status = EventStatus.valueOf(status);
		this.eventOwner = eventOwner;
		this.createdBy = createdBy;
		this.type = RfxTypes.fromString(type);
	}

	public GlobalSearchPojo(String id, String eventName, String eventDescription, String referenceNumber, Date eventEndDate, Date eventStartDate, String status, String eventOwner, String type, String createdBy, String eventId) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.eventEndDate = eventEndDate;
		this.eventStartDate = eventStartDate;
		this.status = EventStatus.valueOf(status);
		this.eventOwner = eventOwner;
		this.createdBy = createdBy;
		this.type = RfxTypes.fromString(type);
		this.eventId = eventId;
	}

	public GlobalSearchPojo(String id, String eventName, String eventDescription, String referenceNumber, Date eventEndDate, Date eventStartDate, String status, String eventOwner, String type, String createdBy, String eventId, String unitName) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.eventEndDate = eventEndDate;
		this.eventStartDate = eventStartDate;
		this.status = EventStatus.valueOf(status);
		this.eventOwner = eventOwner;
		this.createdBy = createdBy;
		this.type = RfxTypes.fromString(type);
		this.unitName = unitName;
		this.eventId = eventId;
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
	 * @return the eventEndDate
	 */
	public Date getEventEndDate() {
		return eventEndDate;
	}

	/**
	 * @param eventEndDate the eventEndDate to set
	 */
	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	/**
	 * @return the eventStartDate
	 */
	public Date getEventStartDate() {
		return eventStartDate;
	}

	/**
	 * @param eventStartDate the eventStartDate to set
	 */
	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
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

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the eventPermissions
	 */
	public EventPermissions getEventPermissions() {
		return eventPermissions;
	}

	/**
	 * @param eventPermissions the eventPermissions to set
	 */
	public void setEventPermissions(EventPermissions eventPermissions) {
		this.eventPermissions = eventPermissions;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the envelopType
	 */
	public boolean isEnvelopType() {
		return envelopType;
	}

	/**
	 * @param envelopType the envelopType to set
	 */
	public void setEnvelopType(boolean envelopType) {
		this.envelopType = envelopType;
	}

	public String toLogString() {
		return "GlobalSearchPojo [id=" + id + ", eventName=" + eventName + ", eventDescription=" + eventDescription + ", referenceNumber=" + referenceNumber + ", eventEndDate=" + eventEndDate + ", eventStartDate=" + eventStartDate + ", status=" + status + ", eventOwner=" + eventOwner + ", createdBy=" + createdBy + ", type=" + type + ", unitName=" + unitName + ", eventId=" + eventId + ", eventPermissions=" + eventPermissions + ", visible=" + visible + "]";
	}

}
