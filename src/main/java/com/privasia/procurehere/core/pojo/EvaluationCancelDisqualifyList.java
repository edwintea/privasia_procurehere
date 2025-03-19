package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Giridhar
 */
public class EvaluationCancelDisqualifyList implements Serializable {

	private static final long serialVersionUID = -1986309780755538226L;

	// Cancel Last Bid List

	private String name;
	private String cancelledBy;
	private String dateTime;
	private String eventName;
	private String justification;
	private Boolean isReverted;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cancelledBy
	 */
	public String getCancelledBy() {
		return cancelledBy;
	}

	/**
	 * @param cancelledBy the cancelledBy to set
	 */
	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	/**
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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
	 * @return the justification
	 */
	public String getJustification() {
		return justification;
	}

	/**
	 * @param justification the justification to set
	 */
	public void setJustification(String justification) {
		this.justification = justification;
	}

	/**
	 * @return the isReverted
	 */
	public Boolean getIsReverted() {
		return isReverted;
	}

	/**
	 * @param isReverted the isReverted to set
	 */
	public void setIsReverted(Boolean isReverted) {
		this.isReverted = isReverted;
	}

}
