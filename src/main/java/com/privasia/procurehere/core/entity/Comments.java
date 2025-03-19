package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@MappedSuperclass
public class Comments {

	@Column(name = "USER_COMMENTS", length = 500, nullable = true)
	private String comment;

	// @JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@Transient
	private String createdDateApk;

	@Transient
	private String bqItemId;

	@Transient
	private String userName;

	@Transient
	private String loginName;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean transientIsApproved;
	
	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean flagForCommentOwner;

	/**
	 * @return the flagForCommentOwner
	 */
	public Boolean getFlagForCommentOwner() {
		return flagForCommentOwner;
	}

	/**
	 * @param flagForCommentOwner the flagForCommentOwner to set
	 */
	public void setFlagForCommentOwner(Boolean flagForCommentOwner) {
		this.flagForCommentOwner = flagForCommentOwner;
	}

	



	public Comments createMobileShallowCopy() {
		Comments ic = new Comments();
		ic.setComment(getComment());
		ic.setUserName(getCreatedBy() != null ? getCreatedBy().getName() : "");
//		ic.setCreatedDate(getCreatedDate());
		ic.setCreatedDateApk(getCreatedDateApk());
		ic.setTransientIsApproved(getTransientIsApproved());
		return ic;
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
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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
	 * @return the bqItemId
	 */
	public String getBqItemId() {
		return bqItemId;
	}

	/**
	 * @param bqItemId the bqItemId to set
	 */
	public void setBqItemId(String bqItemId) {
		this.bqItemId = bqItemId;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the transientIsApproved
	 */
	public Boolean getTransientIsApproved() {
		return transientIsApproved;
	}

	/**
	 * @param transientIsApproved the transientIsApproved to set
	 */
	public void setTransientIsApproved(Boolean transientIsApproved) {
		this.transientIsApproved = transientIsApproved;
	}
	
	public String getCreatedDateApk() {
		return createdDateApk;
	}

	public void setCreatedDateApk(String createdDateApk) {
		this.createdDateApk = createdDateApk;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Comments [comment=" + comment + ", createdBy=" + createdBy + ", createdDate=" + createdDate + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
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
		Comments other = (Comments) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		return true;
	}

}