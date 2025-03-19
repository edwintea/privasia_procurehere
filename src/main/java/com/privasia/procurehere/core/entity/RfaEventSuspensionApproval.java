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
 * @author jayshree
 *
 */
@Entity
@Table(name = "PROC_RFA_SUSPENSION_APPROVAL")
public class RfaEventSuspensionApproval extends EventApproval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4622100134072607244L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVE_ID_SUSP_APPR"))
	private RfaEvent event;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<RfaSuspensionApprovalUser> approvalUsers;
	
	public RfaEventSuspensionApproval copyFrom() {
		RfaEventSuspensionApproval newApp = new RfaEventSuspensionApproval();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {
			newApp.setApprovalUsers(new ArrayList<RfaSuspensionApprovalUser>());
			for (RfaSuspensionApprovalUser appUser : getApprovalUsers()) {
				RfaSuspensionApprovalUser newAppUser = appUser.copyFrom();
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
	public List<RfaSuspensionApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<RfaSuspensionApprovalUser> approvalUsers) {
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
		return "RfaEventSuspensionApproval [ " + super.toLogString() + "]";
	}
	
}
