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
@Table(name = "PROC_RFI_EVENT_AUDIT")
public class RfiEventAudit extends EventAudit implements Serializable {

	private static final long serialVersionUID = -6660675534367996278L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_AUDIT_EVNT_ID"))
	private RfiEvent event;

	public RfiEventAudit() {
	}

	public RfiEventAudit(RfiEvent event, User actionBy, Date actionDate, AuditActionType action, String description) {
		super(actionBy, actionDate, action, description);
		this.event = event;
	}

	public RfiEventAudit(Buyer buyer, RfiEvent event, User actionBy, Date actionDate, AuditActionType action, String description, byte[] summarySnapshot) {
		super(buyer, actionBy, actionDate, action, description, summarySnapshot);
		this.event = event;
	}

	public RfiEventAudit(Supplier supplier, RfiEvent event, User actionBy, Date actionDate, AuditActionType action, String description) {
		super(supplier, actionBy, actionDate, action, description);
		this.event = event;
	}

	public RfiEventAudit(String id, User actionBy, RfiEvent event, Buyer buyer, Date actionDate, AuditActionType action, String description) {
		super(actionBy, actionDate, action, description);
		setId(id);
		if (buyer != null) {
			setBuyer(new Buyer());
			getBuyer().setId(buyer.getId());
		}
		this.event = event;
	}

	public RfiEventAudit(String id, User actionBy, RfiEvent event, Date actionDate, AuditActionType action, String description) {
		super(actionBy, actionDate, action, description);
		setId(id);
		this.event = event;
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
		return "RfiEventAudit [toLogString()=" + toLogString() + "]";
	}

}
