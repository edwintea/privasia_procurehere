package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.User;

public class DraftsPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6809939819213651830L;
	
	private String eventName;
	private Date createdDate;
	private Date modifiedDate;
	private User createdBy;
	
	
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
	
	public DraftsPojo(Event event) {
		this.eventName=event.getEventName();
		this.createdBy=event.getCreatedBy() ;
		this.createdDate=event.getCreatedDate();
		this.modifiedDate=event.getModifiedDate();
		
		
	}
	
	

	
	
	

}
