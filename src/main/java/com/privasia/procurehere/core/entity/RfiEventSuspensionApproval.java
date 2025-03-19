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
@Table(name = "PROC_RFI_SUSPENSION_APPROVAL")
public class RfiEventSuspensionApproval  extends EventApproval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1907061015319992595L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVE_ID_SUSP_APPR"))
	private RfiEvent event;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<RfiSuspensionApprovalUser> approvalUsers;
	
	public RfiEventSuspensionApproval copyFrom() {
		RfiEventSuspensionApproval newApp = new RfiEventSuspensionApproval();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {
			newApp.setApprovalUsers(new ArrayList<RfiSuspensionApprovalUser>());
			for (RfiSuspensionApprovalUser appUser : getApprovalUsers()) {
				RfiSuspensionApprovalUser newAppUser = appUser.copyFrom();
				newApp.getApprovalUsers().add(newAppUser);
			}
		}
		return newApp;
	}

	/**
	 * @return the event
	 */
	public RfiEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfiEvent event) {
		this.event = event;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<RfiSuspensionApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<RfiSuspensionApprovalUser> approvalUsers) {
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toLogString() {
		return "RfiEventSuspensionApproval [" + super.toLogString() + "]";
	}

}
