package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.enums.Status;

public class TimeZonePojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3426779386395021712L;
	
	private String id;

	private String timeZone;

	private String timeZoneDescription;
	
	private Country country;

	private Status status;
	
	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date createdDate;

	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date modifiedDate;
	
	
	public TimeZonePojo(TimeZone timeZone){
		this.id=timeZone.getId();
		this.timeZone=timeZone.getTimeZone();
		this.status=timeZone.getStatus();
		this.timeZoneDescription=timeZone.getTimeZoneDescription();
		this.country=timeZone.getCountry();
		this.createdBy = timeZone.getCreatedBy() != null ? timeZone.getCreatedBy().getLoginId() : null;
		this.createdDate = timeZone.getCreatedDate();
		this.modifiedBy = timeZone.getModifiedBy() != null ? timeZone.getModifiedBy().getLoginId() : null;
		this.modifiedDate = timeZone.getModifiedDate();
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
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}


	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}


	/**
	 * @return the timeZoneDescription
	 */
	public String getTimeZoneDescription() {
		return timeZoneDescription;
	}


	/**
	 * @param timeZoneDescription the timeZoneDescription to set
	 */
	public void setTimeZoneDescription(String timeZoneDescription) {
		this.timeZoneDescription = timeZoneDescription;
	}


	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}


	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}


	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
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
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}


	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
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

}
