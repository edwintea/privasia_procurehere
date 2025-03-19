/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Najeer
 */
public class OngoingEventPojo implements Serializable {

	private static final long serialVersionUID = 917772123457825482L;

	private static final Logger LOG = LogManager.getLogger(OngoingEventPojo.class);

	private String id;
	private String eventName;
	private int totalMessageCount;
	private int unreadMessageCount;
	private int totalBidCount;
	private int unreadBidCount;
	private int totalSupplierCount;
	private int readSupplierCount;
	private String timeLeft;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEnd;

	private String unitName;
	private String referenceNumber;
	private String sysEventId;
	private String eventUser;

	// private RfxTypes eventType;

	private RfxTypes type;

	public OngoingEventPojo() {
	}

	public OngoingEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser) {
		this.id = id;
		this.eventName = eventName;
		this.type = RfxTypes.fromString(type);
		this.eventEnd = eventEnd;
		this.eventStart = eventStart;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.eventUser = eventUser;
		this.sysEventId = sysEventId;
	}

	public OngoingEventPojo(String id, String eventName, int totalBidsReceived, int totalReadCount, String timeLeft, RfxTypes type) {
		this.id = id;
		this.eventName = eventName;
		this.totalBidCount = totalBidsReceived;
		this.totalMessageCount = totalReadCount;
		this.timeLeft = timeLeft;
		this.type = type;
	}

	public OngoingEventPojo(String id, String eventName, RfxTypes type) {
		this.id = id;
		this.eventName = eventName;
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
	 * @return the totalMessageCount
	 */
	public int getTotalMessageCount() {
		return totalMessageCount;
	}

	/**
	 * @param totalMessageCount the totalMessageCount to set
	 */
	public void setTotalMessageCount(int totalMessageCount) {
		this.totalMessageCount = totalMessageCount;
	}

	/**
	 * @return the unreadMessageCount
	 */
	public int getUnreadMessageCount() {
		return unreadMessageCount;
	}

	/**
	 * @param unreadMessageCount the unreadMessageCount to set
	 */
	public void setUnreadMessageCount(int unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
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
	 * @return the unreadBidCount
	 */
	public int getUnreadBidCount() {
		return unreadBidCount;
	}

	/**
	 * @param unreadBidCount the unreadBidCount to set
	 */
	public void setUnreadBidCount(int unreadBidCount) {
		this.unreadBidCount = unreadBidCount;
	}

	/**
	 * @return the totalSupplierCount
	 */
	public int getTotalSupplierCount() {
		return totalSupplierCount;
	}

	/**
	 * @param totalSupplierCount the totalSupplierCount to set
	 */
	public void setTotalSupplierCount(int totalSupplierCount) {
		this.totalSupplierCount = totalSupplierCount;
	}

	/**
	 * @return the readSupplierCount
	 */
	public int getReadSupplierCount() {
		return readSupplierCount;
	}

	/**
	 * @param readSupplierCount the readSupplierCount to set
	 */
	public void setReadSupplierCount(int readSupplierCount) {
		this.readSupplierCount = readSupplierCount;
	}

	/**
	 * @return the timeLeft
	 */
	public String getTimeLeft() {

		try {
			long seconds = (this.eventEnd.getTime() - new Date().getTime()) / 1000;

			int day = (int) TimeUnit.SECONDS.toDays(seconds);
			long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
			long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
			// long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
			timeLeft = "";
			if (day > 0) {
				timeLeft += day + " D, ";
			}
			if (hours > 0) {
				timeLeft += hours + " H, ";
			}
			if (minute > 0) {
				timeLeft += minute + " M, ";
			}

			if (timeLeft.length() > 0) {
				timeLeft = timeLeft.substring(0, timeLeft.length() - 2);
			}
		} catch (Exception e) {
			LOG.error("Error calculating time left for Ongoing event [" + id + "] : " + e.getMessage());
		}

		return timeLeft;
	}

	/**
	 * @param timeLeft the timeLeft to set
	 */
	public void setTimeLeft(String timeLeft) {
		this.timeLeft = timeLeft;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		OngoingEventPojo other = (OngoingEventPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
