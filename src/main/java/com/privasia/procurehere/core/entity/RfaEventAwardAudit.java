package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Priyanka
 */
@Entity
@Table(name = "PROC_RFA_EVENT_AWARD_AUDIT")
public class RfaEventAwardAudit extends EventAwardAudit implements Serializable {

	private static final long serialVersionUID = -8027111085131786125L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFA_AWD_AUDIT_EVNT_ID"))
	private RfaEvent event;

	public RfaEventAwardAudit() {
	}

	/**
	 * @param buyer
	 * @param actionBy
	 * @param actionDate
	 * @param description
	 * @param summarySnapshot
	 */
	public RfaEventAwardAudit(RfaEvent event, Buyer buyer, User actionBy, Date actionDate, String description, byte[] snapshot, byte[] excelSnapshot) {
		super(buyer, actionBy, actionDate, description, snapshot, excelSnapshot);
		this.event = event;
	}

	public RfaEventAwardAudit(String id, User actionBy, Date actionDate, String description, String fileName) {
		super(id, (actionBy == null ? null : actionBy.getName()), actionDate, description, fileName);
	}

	public RfaEventAwardAudit(String id, User actionBy, Date actionDate, String description, String fileName, Boolean hasSnapshot, Boolean hasExcelSnapshot) {
		super(id, (actionBy == null ? null : actionBy.getName()), actionDate, description, fileName);
		this.setHasSnapshot(hasSnapshot);
		this.setHasExcelSnapshot(hasExcelSnapshot);
	}

	public RfaEventAwardAudit(RfaEvent event, Buyer buyer, User actionBy, Date actionDate, String description) {
		super(buyer, actionBy, actionDate, description);
		this.event = event;
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

}
