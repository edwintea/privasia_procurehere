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
 * @author Priyanka Singh
 */
@Entity
@Table(name = "PROC_RFQ_EVENT_APPROVAL")
public class RfqEventApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = -8867221756717560750L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_APPROVAL"))
	private RfqEvent event;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<RfqApprovalUser> approvalUsers;
	
	public RfqEventApproval copyFrom() {
		RfqEventApproval newApp = new RfqEventApproval();
		newApp.setLevel(getLevel());
		newApp.setApprovalType(getApprovalType());
		if (CollectionUtil.isNotEmpty(getApprovalUsers())) {
			newApp.setApprovalUsers(new ArrayList<RfqApprovalUser>());
			for (RfqApprovalUser appUser : getApprovalUsers()) {
				RfqApprovalUser newAppUser = appUser.copyFrom();
				newApp.getApprovalUsers().add(newAppUser);
			}
		}
		return newApp;
	}
	

	/**
	 * @return the event
	 */
	public RfqEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<RfqApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<RfqApprovalUser> approvalUsers) {
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
		return "RfqEventApproval [ " + super.toLogString() + "]";
	}


}
