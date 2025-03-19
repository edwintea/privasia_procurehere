package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author parveen
 */
@MappedSuperclass
public class ApprovalUser implements Serializable {

	private static final long serialVersionUID = 8500938361440019891L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "APPROVAL_REMARKS", length = 500, nullable = true)
	private String remarks;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "APPROVAL_STATUS")
	private ApprovalStatus approvalStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NEXT_REMINDER_TIME")
	private Date nextReminderTime;
	
	@Column(name = "REMINDER_COUNT", length = 2, nullable = true)
	private Integer reminderCount;

	@Transient
	private String userName;

	@Transient
	private String userId;

	@Transient
	private String actionDateApk;
	
	public String getActionDateApk() {
		return actionDateApk;
	}

	public void setActionDateApk(String actionDateApk) {
		this.actionDateApk = actionDateApk;
	}

	public ApprovalUser() {
		this.approvalStatus = ApprovalStatus.PENDING;
	}

	public ApprovalUser createMobileShallowCopy() {
		ApprovalUser au = new ApprovalUser();
		au.setUserName(getUser() != null ? getUser().getName() : "");
		au.setUserId(getUser() != null ? getUser().getId() : "");
		au.setApprovalStatus(getApprovalStatus());
		au.setActionDate(getActionDate());
		au.setActionDateApk(getActionDateApk());
		au.setRemarks(getRemarks());
		return au;
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
	 * @return the remarks
	 */
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
	 * @return the approvalStatus
	 */
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	/**
	 * @param approvalStatus the approvalStatus to set
	 */
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the nextReminderTime
	 */
	public Date getNextReminderTime() {
		return nextReminderTime;
	}

	/**
	 * @param nextReminderTime the nextReminderTime to set
	 */
	public void setNextReminderTime(Date nextReminderTime) {
		this.nextReminderTime = nextReminderTime;
	}

	/**
	 * @return the reminderCount
	 */
	public Integer getReminderCount() {
		return reminderCount;
	}

	/**
	 * @param reminderCount the reminderCount to set
	 */
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		// result = prime * result + ((approvalStatus == null) ? 0 : approvalStatus.hashCode());
		// result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApprovalUser other = (ApprovalUser) obj;
		// if (actionDate == null) {
		// if (other.actionDate != null)
		// return false;
		// } else if (!actionDate.equals(other.actionDate))
		// return false;
		// if (approvalStatus != other.approvalStatus)
		// return false;
		// if (remarks == null) {
		// if (other.remarks != null)
		// return false;
		// } else if (!remarks.equals(other.remarks))
		// return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String toLogString() {
		return "ApprovalUser [id=" + id + ", remarks=" + remarks + ", user=" + user + ", approvalStatus=" + approvalStatus + ", actionDate=" + actionDate + "]";
	}

}
