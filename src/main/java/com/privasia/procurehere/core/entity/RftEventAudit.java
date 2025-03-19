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
import com.privasia.procurehere.core.enums.AuditActionType;

/**
 * @author Teja
 */
@Entity
@Table(name = "PROC_RFT_EVENT_AUDIT")
public class RftEventAudit extends EventAudit implements Serializable {

	private static final long serialVersionUID = -8027111085131786125L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_AUDIT_EVNT_ID"))
	private RftEvent event;

	public RftEventAudit() {
	}

	public RftEventAudit(RftEvent event, User actionBy, Date actionDate, AuditActionType action, String description) {
		super(actionBy, actionDate, action, description);
		this.event = event;
	}

	public RftEventAudit(Buyer buyer, RftEvent event, User actionBy, Date actionDate, AuditActionType action, String description, byte[] summarySnapshot) {
		super(buyer, actionBy, actionDate, action, description, summarySnapshot);
		this.event = event;
	}

	public RftEventAudit(Supplier supplier, RftEvent event, User actionBy, Date actionDate, AuditActionType action, String description) {
		super(supplier, actionBy, actionDate, action, description);
		this.event = event;
	}

	public RftEventAudit(String id, User actionBy, RftEvent event, Buyer buyer, Date actionDate, AuditActionType action, String description) {
		super(actionBy, actionDate, action, description);
		setId(id);
		if (buyer != null) {
			setBuyer(new Buyer());
			getBuyer().setId(buyer.getId());
		}
		this.event = event;
	}

	public RftEventAudit(String id, User actionBy, RftEvent event,   Date actionDate, AuditActionType action, String description) {
		super(actionBy, actionDate, action, description);
		setId(id);
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toLogString() {
		return "RftEventAudit [toLogString()=" + toLogString() + "]";
	}

}
