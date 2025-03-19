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
 * @author Teja
 */
@Entity
@Table(name = "PROC_RFT_EVENT_AWARD_AUDIT")
public class RftEventAwardAudit extends EventAwardAudit implements Serializable {

	private static final long serialVersionUID = -8027111085131786125L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_AWD_AUDIT_EVNT_ID"))
	private RftEvent event;

	public RftEventAwardAudit() {
	}

	/**
	 * @param buyer
	 * @param actionBy
	 * @param actionDate
	 * @param description
	 * @param summarySnapshot
	 */
	public RftEventAwardAudit(RftEvent event, Buyer buyer, User actionBy, Date actionDate, String description, byte[] snapshot, byte[] excelSnapshot) {
		super(buyer, actionBy, actionDate, description, snapshot, excelSnapshot);
		this.event = event;
	}

	public RftEventAwardAudit(String id, User actionBy, Date actionDate, String description, String fileName) {
		super(id, (actionBy == null ? null : actionBy.getName()), actionDate, description, fileName);
	}

	public RftEventAwardAudit(String id, User actionBy, Date actionDate, String description, String fileName, Boolean hasSnapshot, Boolean hasExcelSnapshot) {
		super(id, (actionBy == null ? null : actionBy.getName()), actionDate, description, fileName);
		this.setHasSnapshot(hasSnapshot);
		this.setHasExcelSnapshot(hasExcelSnapshot);
	}

	public RftEventAwardAudit(RftEvent event, Buyer buyer, User actionBy, Date actionDate, String description) {
		super(buyer, actionBy, actionDate, description);
		this.event = event;
	}

	/**
	 * @return the event
	 */
	public RftEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RftEvent event) {
		this.event = event;
	}

}
