package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class FinishedEventPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3548088942837137970L;

	private String id;
	private String eventName;
	private int totalBidCount;

	private String unitName;
	private String referenceNumber;
	private String sysEventId;
	private String eventUser;
	//4105
	private String businessUnitId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEnd;

	private RfxTypes type;

	public FinishedEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd) {
		this.id = id;
		this.eventName = eventName;
		this.type = RfxTypes.fromString(type);
		this.eventName = eventName;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
	}

	public FinishedEventPojo(String id, String eventName, Date eventStart, Date eventEnd, RfxTypes type) {
		this.id = id;
		this.eventName = eventName;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.type = type;
	}

	public FinishedEventPojo(String id, String eventName) {
		this.id = id;
		this.eventName = eventName;
	}

	public FinishedEventPojo(String id, String eventName, String createdBy, Date createdDate,  String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser) {
		this.id = id;
		this.eventName = eventName;
		this.type = RfxTypes.fromString(type);
		this.eventName = eventName;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = eventUser;
	}

	public FinishedEventPojo(String id, String eventName, String createdBy, Date createdDate,  String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser,String businessUnitId) {
		this.id = id;
		this.eventName = eventName;
		this.type = RfxTypes.fromString(type);
		this.eventName = eventName;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = eventUser;
		this.businessUnitId=businessUnitId;
	}

	public FinishedEventPojo() {
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
	 * @return the totalBidCount
	 */
	public int getTotalBidCount() {
		return totalBidCount;
	}

	/**
	 * @param totalBidCount the totalBidCount to set
	 */
	public void setTotalBidCount(int totalBidCount) {
		this.totalBidCount = totalBidCount;
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

	/**
	 * @return the businessUnitId
	 */
	public String getBusinessUnitId() {
		return businessUnitId;
	}

	/**
	 * @param businessUnitId the businessUnitId to set
	 */
	public void setBusinessUnitId(String businessUnitId) {
		this.businessUnitId = businessUnitId;
	}

}
