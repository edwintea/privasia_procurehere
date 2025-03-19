package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class ActiveEventPojo implements Serializable {

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

	private User createdBy;
	private RfxTypes type;

	private String unitName;
	private String referenceNumber;
	private String sysEventId;
	private String eventUser;

	public ActiveEventPojo() {
	}

	public ActiveEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd) {
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
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.type = RfxTypes.fromString(type);
	}

	public ActiveEventPojo(String id, String eventName, Date createdDate, Date modifiedDate, User createdBy, RfxTypes type) {
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

	public ActiveEventPojo(Event event) {
		this.eventName = event.getEventName();
		this.createdBy = event.getCreatedBy();
		this.createdDate = event.getCreatedDate();
		this.modifiedDate = event.getModifiedDate();
	}

	public ActiveEventPojo(String id, String eventName, String createdBy, Date createdDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.type = RfxTypes.fromString(type);
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.eventUser = eventUser;
		this.sysEventId = sysEventId;
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

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getSysEventId() {
		return sysEventId;
	}

	public void setSysEventId(String sysEventId) {
		this.sysEventId = sysEventId;
	}

	public String getEventUser() {
		return eventUser;
	}

	public void setEventUser(String eventUser) {
		this.eventUser = eventUser;
	}

}