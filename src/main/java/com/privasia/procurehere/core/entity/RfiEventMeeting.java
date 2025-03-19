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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFI_MEETINGS")
public class RfiEventMeeting extends com.privasia.procurehere.core.entity.EventMeeting implements Serializable {

	private static final long serialVersionUID = 4352621526424380914L;

	public interface EventMeeting {
	}

	@JsonIgnore
	//@NotEmpty(message = "{rftEventMeeting.inviteSuppliers}", groups = { EventMeeting.class })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_EVENT_MEET_SUPPLIERS", joinColumns = @JoinColumn(name = "MEETING_ID"), inverseJoinColumns = @JoinColumn(name = "SUPPLIER_ID"))
	private List<Supplier> inviteSuppliers;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_MEETING"))
	private RfiEvent rfxEvent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEventMeetingDocument> rfxEventMeetingDocument;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEventMeetingContact> rfxEventMeetingContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEventMeetingReminder> rfxEventMeetingReminder;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEventMeeting", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiSupplierMeetingAttendance> rfxEventMeetingAttendance;

	@Transient
	private RfiSupplierMeetingAttendance supplierAttendance;

	@Transient
	private List<RfiEventMeetingDocument> eventMeetingDocument;

	public RfiEventMeeting createShallowCopy() {
		RfiEventMeeting ic = new RfiEventMeeting();
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
	public RfiEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfiEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the rfxEventMeetingDocument
	 */
	public List<RfiEventMeetingDocument> getRfxEventMeetingDocument() {
		return rfxEventMeetingDocument;
	}

	/**
	 * @param rfxEventMeetingDocument the rfxEventMeetingDocument to set
	 */
	public void setRfxEventMeetingDocument(List<RfiEventMeetingDocument> rfxEventMeetingDocument) {
		if (this.rfxEventMeetingDocument == null) {
			this.rfxEventMeetingDocument = new ArrayList<RfiEventMeetingDocument>();
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
	public List<RfiEventMeetingContact> getRfxEventMeetingContacts() {
		return rfxEventMeetingContacts;
	}

	/**
	 * @param rfxEventMeetingContacts the rfxEventMeetingContacts to set
	 */
	public void setRfxEventMeetingContacts(List<RfiEventMeetingContact> rfxEventMeetingContacts) {
		if (this.rfxEventMeetingContacts == null) {
			this.rfxEventMeetingContacts = new ArrayList<RfiEventMeetingContact>();
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
	public List<RfiEventMeetingReminder> getRfxEventMeetingReminder() {
		return rfxEventMeetingReminder;
	}

	/**
	 * @param rfxEventMeetingReminder the rfxEventMeetingReminder to set
	 */
	public void setRfxEventMeetingReminder(List<RfiEventMeetingReminder> rfxEventMeetingReminder) {
		if (this.rfxEventMeetingReminder == null) {
			this.rfxEventMeetingReminder = new ArrayList<RfiEventMeetingReminder>();
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
	public List<RfiSupplierMeetingAttendance> getRfxEventMeetingAttendance() {
		return rfxEventMeetingAttendance;
	}

	/**
	 * @param rfxEventMeetingAttendance the rfxEventMeetingAttendance to set
	 */
	public void setRfxEventMeetingAttendance(List<RfiSupplierMeetingAttendance> rfxEventMeetingAttendance) {
		if (this.rfxEventMeetingAttendance == null) {
			this.rfxEventMeetingAttendance = new ArrayList<RfiSupplierMeetingAttendance>();
		} else {
			this.rfxEventMeetingAttendance.clear();
		}
		if (rfxEventMeetingAttendance != null) {
			this.rfxEventMeetingAttendance.addAll(rfxEventMeetingAttendance);
		}
	}

	/**
	 * @return the supplierAttendance
	 */
	public RfiSupplierMeetingAttendance getSupplierAttendance() {
		return supplierAttendance;
	}

	/**
	 * @param supplierAttendance the supplierAttendance to set
	 */
	public void setSupplierAttendance(RfiSupplierMeetingAttendance supplierAttendance) {
		this.supplierAttendance = supplierAttendance;
	}

	/**
	 * @return the eventMeetingDocument
	 */
	public List<RfiEventMeetingDocument> getEventMeetingDocument() {
		return eventMeetingDocument;
	}

	/**
	 * @param eventMeetingDocument the eventMeetingDocument to set
	 */
	public void setEventMeetingDocument(List<RfiEventMeetingDocument> eventMeetingDocument) {
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
		return "RfiEventMeeting [ " + toLogString() + "]";
	}
}
