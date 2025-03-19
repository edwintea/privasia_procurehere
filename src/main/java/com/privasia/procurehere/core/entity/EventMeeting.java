/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.enums.converter.MeetingTypeConverter;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author parveen
 */
@MappedSuperclass
public class EventMeeting implements Serializable {

	private static final long serialVersionUID = 4352621526424380914L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{meeting.title.empty}")
	@Size(min = 1, max = 80, message = "{meeting.title.length}")
	@Column(name = "MEETING_TITLE", length = 80, nullable = true)
	private String title;

	@Convert(converter = MeetingTypeConverter.class)
	@Column(name = "MEETING_TYPE", length = 64, nullable = true)
	private MeetingType meetingType;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPOINTMENT_DATE_TIME")
	private Date appointmentDateTime;

	@Column(name = "MEETING_VENUE", length = 550)
	private String venue;

	@Column(name = "MEETING_REMARKS", length = 1050)
	private String remarks;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEETING_STATUS")
	private MeetingStatus status;

	@Column(name = "IS_PAST_MEETING")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean pastMeeting = Boolean.FALSE;

	@Column(name = "MEETING_CANCEL_REASON", length = 300)
	private String cancelReason;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "IS_MEETING_ATTEND_MANDTORY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean meetingAttendMandatory = Boolean.FALSE;

	@Transient
	private Date appointmentTime;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean selectAllSupplier = Boolean.FALSE;

	@Transient
	private Set<String> removedSuppliers;

	@Transient
	private Set<String> selectedSuppliers;

	public EventMeeting() {
		pastMeeting = Boolean.FALSE;
		meetingAttendMandatory = Boolean.FALSE;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the meetingType
	 */
	public MeetingType getMeetingType() {
		return meetingType;
	}

	/**
	 * @param meetingType the meetingType to set
	 */
	public void setMeetingType(MeetingType meetingType) {
		this.meetingType = meetingType;
	}

	/**
	 * @return the appointmentDateTime
	 */
	public Date getAppointmentDateTime() {
		return appointmentDateTime;
	}

	/**
	 * @param appointmentDateTime the appointmentDateTime to set
	 */
	public void setAppointmentDateTime(Date appointmentDateTime) {
		this.appointmentDateTime = appointmentDateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the venue
	 */
	public String getVenue() {
		return venue;
	}

	/**
	 * @param venue the venue to set
	 */
	public void setVenue(String venue) {
		this.venue = venue;
	}

	/**
	 * @return the appointmentTime
	 */
	public Date getAppointmentTime() {
		return appointmentTime;
	}

	/**
	 * @param appointmentTime the appointmentTime to set
	 */
	public void setAppointmentTime(Date appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	/**
	 * @return the status
	 */
	public MeetingStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(MeetingStatus status) {
		this.status = status;
	}

	/**
	 * @return the pastMeeting
	 */
	public Boolean getPastMeeting() {
		return pastMeeting;
	}

	/**
	 * @param pastMeeting the pastMeeting to set
	 */
	public void setPastMeeting(Boolean pastMeeting) {
		this.pastMeeting = pastMeeting;
	}

	/**
	 * @return the cancelReason
	 */
	public String getCancelReason() {
		return cancelReason;
	}

	/**
	 * @param cancelReason the cancelReason to set
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
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

	/**
	 * @return the meetingAttendMandatory
	 */
	public Boolean getMeetingAttendMandatory() {
		return meetingAttendMandatory;
	}

	/**
	 * @param meetingAttendMandatory the meetingAttendMandatory to set
	 */
	public void setMeetingAttendMandatory(Boolean meetingAttendMandatory) {
		this.meetingAttendMandatory = meetingAttendMandatory;
	}

	public Boolean getSelectAllSupplier() {
		return selectAllSupplier;
	}

	public void setSelectAllSupplier(Boolean selectAllSupplier) {
		this.selectAllSupplier = selectAllSupplier;
	}

	public Set<String> getRemovedSuppliers() {
		return removedSuppliers;
	}

	public void setRemovedSuppliers(Set<String> removedSuppliers) {
		this.removedSuppliers = removedSuppliers;
	}

	public Set<String> getSelectedSuppliers() {
		return selectedSuppliers;
	}

	public void setSelectedSuppliers(Set<String> selectedSuppliers) {
		this.selectedSuppliers = selectedSuppliers;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appointmentDateTime == null) ? 0 : appointmentDateTime.hashCode());
		result = prime * result + ((appointmentTime == null) ? 0 : appointmentTime.hashCode());
		result = prime * result + ((meetingType == null) ? 0 : meetingType.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((venue == null) ? 0 : venue.hashCode());
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
		EventMeeting other = (EventMeeting) obj;
		if (appointmentDateTime == null) {
			if (other.appointmentDateTime != null)
				return false;
		} else if (!appointmentDateTime.equals(other.appointmentDateTime))
			return false;
		if (appointmentTime == null) {
			if (other.appointmentTime != null)
				return false;
		} else if (!appointmentTime.equals(other.appointmentTime))
			return false;
		if (meetingType != other.meetingType)
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (status != other.status)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (venue == null) {
			if (other.venue != null)
				return false;
		} else if (!venue.equals(other.venue))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	public String toLogString() {
		return "EventMeeting [title=" + title + ", appointmentDateTime=" + appointmentDateTime + ", venue=" + venue + ", remarks=" + remarks + "]";
	}

}
