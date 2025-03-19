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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author parveen
 */

@Entity
@Table(name = "PROC_RFT_EVENT_MEETINGS")
public class RftEventMeeting extends EventMeeting implements Serializable {
	private static final long serialVersionUID = 4589959378010397971L;

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	public interface EventMeeting {
	}

	// @NotEmpty(message = "{rftEventMeeting.inviteSuppliers}", groups = { EventMeeting.class })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_EVENT_MEET_SUPPLIERS", joinColumns = @JoinColumn(name = "MEETING_ID"), inverseJoinColumns = @JoinColumn(name = "SUPPLIER_ID"))
	private List<Supplier> inviteSuppliers;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_MEETING"))
	private RftEvent rfxEvent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEventMeetingDocument> rfxEventMeetingDocument;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEventMeetingContact> rfxEventMeetingContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEventMeetingReminder> rfxEventMeetingReminder;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftSupplierMeetingAttendance> rfxEventMeetingAttendance;

	@Transient
	private RftSupplierMeetingAttendance supplierAttendance;

	@Transient
	private List<RftEventMeetingDocument> eventMeetingDocument;

	public RftEventMeeting createShallowCopy() {
		RftEventMeeting ic = new RftEventMeeting();
		ic.setAppointmentDateTime(getAppointmentDateTime());
		ic.setMeetingType(getMeetingType());
		ic.setAppointmentTime(getAppointmentTime());
		ic.setCancelReason(getCancelReason());
		ic.setId(getId());
		ic.setPastMeeting(getPastMeeting());
		ic.setRemarks(getRemarks());
		ic.setStatus(getStatus());
		ic.setTitle(getTitle());
		ic.setVenue(getVenue());
		ic.setMeetingAttendMandatory(getMeetingAttendMandatory());
		ic.setRfxEventMeetingContacts(getRfxEventMeetingContacts());
		return ic;
	}

	/**
	 * @return the inviteSuppliers
	 */
	public List<Supplier> getInviteSuppliers() {
		try {
			if (inviteSuppliers != null) {
				for (Supplier supplier : inviteSuppliers) {
					supplier.getCompanyName();
				}
			}
		} catch (Exception e) {
		}
		return inviteSuppliers;
	}

	/**
	 * @param inviteSuppliers the inviteSuppliers to set
	 */
	public void setInviteSuppliers(List<Supplier> inviteSuppliers) {
		this.inviteSuppliers = inviteSuppliers;
	}

	/**
	 * @return the rfxEvent
	 */
	public RftEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RftEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the supplierAttendance
	 */
	public RftSupplierMeetingAttendance getSupplierAttendance() {
		return supplierAttendance;
	}

	/**
	 * @return the rfxEventMeetingDocument
	 */
	public List<RftEventMeetingDocument> getRfxEventMeetingDocument() {
		return rfxEventMeetingDocument;
	}

	/**
	 * @param rfxEventMeetingDocument the rfxEventMeetingDocument to set
	 */
	public void setRfxEventMeetingDocument(List<RftEventMeetingDocument> rfxEventMeetingDocument) {
		if (this.rfxEventMeetingDocument == null) {
			this.rfxEventMeetingDocument = new ArrayList<RftEventMeetingDocument>();
		}
		// else {
		// this.rfxEventMeetingDocument.clear();
		// }
		if (rfxEventMeetingDocument != null) {
			this.rfxEventMeetingDocument.addAll(rfxEventMeetingDocument);
		}
	}

	/**
	 * @return the rfxEventMeetingContacts
	 */
	public List<RftEventMeetingContact> getRfxEventMeetingContacts() {
		return rfxEventMeetingContacts;
	}

	/**
	 * @param rfxEventMeetingContacts the rfxEventMeetingContacts to set
	 */
	public void setRfxEventMeetingContacts(List<RftEventMeetingContact> rfxEventMeetingContacts) {
		if (this.rfxEventMeetingContacts == null) {
			this.rfxEventMeetingContacts = new ArrayList<RftEventMeetingContact>();
		} else {
			this.rfxEventMeetingContacts.clear();
		}
		if (rfxEventMeetingContacts != null) {
			this.rfxEventMeetingContacts.addAll(rfxEventMeetingContacts);
		}
	}

	/**
	 * @return the rfxEventMeetingReminder
	 */
	public List<RftEventMeetingReminder> getRfxEventMeetingReminder() {
		return rfxEventMeetingReminder;
	}

	/**
	 * @param rfxEventMeetingReminder the rfxEventMeetingReminder to set
	 */
	public void setRfxEventMeetingReminder(List<RftEventMeetingReminder> rfxEventMeetingReminder) {
		if (this.rfxEventMeetingReminder == null) {
			this.rfxEventMeetingReminder = new ArrayList<RftEventMeetingReminder>();
		} else {
			this.rfxEventMeetingReminder.clear();
		}
		if (rfxEventMeetingReminder != null) {
			this.rfxEventMeetingReminder.addAll(rfxEventMeetingReminder);
		}
	}

	/**
	 * @return the rfxEventMeetingAttendance
	 */
	public List<RftSupplierMeetingAttendance> getRfxEventMeetingAttendance() {
		return rfxEventMeetingAttendance;
	}

	/**
	 * @param rfxEventMeetingAttendance the rfxEventMeetingAttendance to set
	 */
	public void setRfxEventMeetingAttendance(List<RftSupplierMeetingAttendance> rfxEventMeetingAttendance) {
		if (this.rfxEventMeetingAttendance == null) {
			this.rfxEventMeetingAttendance = new ArrayList<RftSupplierMeetingAttendance>();
		} else {
			this.rfxEventMeetingAttendance.clear();
		}
		if (rfxEventMeetingAttendance != null) {
			this.rfxEventMeetingAttendance.addAll(rfxEventMeetingAttendance);
		}
	}

	/**
	 * @param supplierAttendance the supplierAttendance to set
	 */
	public void setSupplierAttendance(RftSupplierMeetingAttendance supplierAttendance) {
		this.supplierAttendance = supplierAttendance;
	}

	/**
	 * @return the eventMeetingDocument
	 */
	public List<RftEventMeetingDocument> getEventMeetingDocument() {
		return eventMeetingDocument;
	}

	/**
	 * @param eventMeetingDocument the eventMeetingDocument to set
	 */
	public void setEventMeetingDocument(List<RftEventMeetingDocument> eventMeetingDocument) {
		this.eventMeetingDocument = eventMeetingDocument;
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
	public String toString() {
		return "RftEventMeeting [ " + super.toLogString() + "]";
	}

}
