/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Aishwarya
 */
@Entity
@Table(name = "PROC_RFA_AWARD_APPROVAL")
public class RfaEventAwardApproval extends EventApproval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4622100134072607244L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVE_ID_AWARD_APPR"))
	private RfaEvent event;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<RfaAwardApprovalUser> approvalUsers;

	public RfaEventAwardApproval copyFrom() {
		RfaEventAwardApproval newApp = new RfaEventAwardApproval();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {
			newApp.setApprovalUsers(new ArrayList<RfaAwardApprovalUser>());
			for (RfaAwardApprovalUser appUser : getApprovalUsers()) {
				RfaAwardApprovalUser newAppUser = appUser.copyFrom();
				newApp.getApprovalUsers().add(newAppUser);
			}
		}
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {

		}
		return newApp;
	}

	/**
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<RfaAwardApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<RfaAwardApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public String toLogString() {
		return "RfaEventAwardApproval [ " + super.toLogString() + "]";
	}

}
