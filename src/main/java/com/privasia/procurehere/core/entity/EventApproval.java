package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.enums.ApprovalType;
import org.hibernate.annotations.Type;

/**
 * @author Priyanka Singh
 */
@MappedSuperclass
public class EventApproval implements Serializable {

	private static final long serialVersionUID = 9108301116720435343L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "APPROVAL_LEVEL", length = 1, nullable = false)
	private Integer level;

	@Enumerated(EnumType.STRING)
	@Column(name = "APPROVAL_TYPE")
	private ApprovalType approvalType;

	@Column(name = "IS_DONE", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean done = false;

	@Column(name = "ACTIVE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean active = false;

	@Column(name = "IS_ESCALATED" , nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean escalated = Boolean.FALSE;

	@Transient
	private List<ApprovalUser> users;

	public EventApproval() {
		this.done = false;
		active = false;
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
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the approvalType
	 */
	public ApprovalType getApprovalType() {
		return approvalType;
	}

	/**
	 * @param approvalType the approvalType to set
	 */
	public void setApprovalType(ApprovalType approvalType) {
		this.approvalType = approvalType;
	}

	/**
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * @param done the done to set
	 */
	public void setDone(boolean done) {
		this.done = done;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return
	 */
	public List<ApprovalUser> getUsers() {
		return this.users;
	}

	/**
	 * @param users
	 */
	public void addUsers(ApprovalUser users) {
		if (this.users == null) {
			this.users = new ArrayList<ApprovalUser>();
		}
		this.users.add(users);
	}
	
	/**
	 * @return the escalated
	 */
	public Boolean getEscalated() {
		return escalated;
	}

	/**
	 * @param escalated the escalated to set
	 */
	public void setEscalated(Boolean escalated) {
		this.escalated = escalated;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((approvalType == null) ? 0 : approvalType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
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
		EventApproval other = (EventApproval) obj;
		if (approvalType != other.approvalType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		return true;
	}
	
	public String toLogString() {
		return "Approval [id=" + id + ", level=" + level + ", approvalType=" + approvalType + ", done=" + done + ", active=" + active + "]";
	}
}
