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
 *
 */
@Entity
@Table(name = "PROC_RFP_AWARD_APPROVAL")
public class RfpEventAwardApproval extends EventApproval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7209217033924706419L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVE_ID_AWARD_APPR"))
	private RfpEvent event;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<RfpAwardApprovalUser> approvalUsers;

	public RfpEventAwardApproval copyFrom() {
		RfpEventAwardApproval newApp = new RfpEventAwardApproval();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {
			newApp.setApprovalUsers(new ArrayList<RfpAwardApprovalUser>());
			for (RfpAwardApprovalUser appUser : getApprovalUsers()) {
				RfpAwardApprovalUser newAppUser = appUser.copyFrom();
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
	public RfpEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfpEvent event) {
		this.event = event;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<RfpAwardApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<RfpAwardApprovalUser> approvalUsers) {
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
		return "RfpEventAwardApproval [ " + super.toLogString() + "]";
	}
	
}
