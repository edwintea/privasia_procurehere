package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_TIME_ZONE")
public class TimeZone implements Serializable {

	private static final long serialVersionUID = 723904943969001971L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{timeZone.empty}")
	@Size(min = 1, max = 64, message = "{timeZone.length}")
	@Column(name = "TIME_ZONE", nullable = false, length = 64)
	private String timeZone;

	@Size(min = 1, max = 64, message = "{timeZone.description.length}")
	@Column(name = "TIME_ZONE_DESCRIPTION", nullable = true, length = 64)
	private String timeZoneDescription;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "{timeZone.status.empty}")
	@Column(name = "STATUS", nullable = false)
	private Status status;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_CODE", nullable = true, foreignKey = @ForeignKey(name = "FK_TIMEZONE_COUNTRY_CODE"))
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_TIMEZONE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATED_DATE", nullable = true)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_TIMEZONE_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	
	public TimeZone() {
		this.status = Status.ACTIVE;
	}
	
	public TimeZone(String id, String timeZone, String timeZoneDescription) {
		this.id = id;
		this.timeZone = timeZone;
		this.timeZoneDescription = timeZoneDescription;
	}
	
	public String getFullTimeZone(){
		return timeZone + " - " + timeZoneDescription;
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
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
		result = prime * result + ((timeZoneDescription == null) ? 0 : timeZoneDescription.hashCode());
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
		TimeZone other = (TimeZone) obj;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		if (timeZoneDescription == null) {
			if (other.timeZoneDescription != null)
				return false;
		} else if (!timeZoneDescription.equals(other.timeZoneDescription))
			return false;
		return true;
	}

	public String toLogString() {
		return "TimeZone [id=" + id + ", timeZone=" + timeZone + ", timeZoneDescription=" + timeZoneDescription + ", status=" + status + "]";
	}

	@Override
	public String toString() {
		return timeZone + " - " + timeZoneDescription;
	}

}
