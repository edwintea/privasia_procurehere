package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Giridhar
 */
public class EvaluationMeetingPojo implements Serializable {

	private static final long serialVersionUID = 5108726683296471670L;
	private String title;
	private String status;
	private String remarks;
	private String venue;
	private Date appointmentDateTime;
	private List<EvaluationMeetingContactsPojo> meetingContacts;
	private List<EvaluationSuppliersPojo> meeingInviteSupplier;
	private List<EvaluationDocumentPojo> meetingDocuments;
	private String response;
	private String mandatoryMeeting;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the venue
	 */
	public String getVenue() {
		return venue;
	}

	/**
	 * @param venue the venue to set
	 */
	public void setVenue(String venue) {
		this.venue = venue;
	}

	/**
	 * @return the appointmentDateTime
	 */
	public Date getAppointmentDateTime() {
		return appointmentDateTime;
	}

	/**
	 * @param appointmentDateTime the appointmentDateTime to set
	 */
	public void setAppointmentDateTime(Date appointmentDateTime) {
		this.appointmentDateTime = appointmentDateTime;
	}

	/**
	 * @return the meetingContacts
	 */
	public List<EvaluationMeetingContactsPojo> getMeetingContacts() {
		return meetingContacts;
	}

	/**
	 * @param meetingContacts the meetingContacts to set
	 */
	public void setMeetingContacts(List<EvaluationMeetingContactsPojo> meetingContacts) {
		this.meetingContacts = meetingContacts;
	}

	/**
	 * @return the meeingInviteSupplier
	 */
	public List<EvaluationSuppliersPojo> getMeeingInviteSupplier() {
		return meeingInviteSupplier;
	}

	/**
	 * @param meeingInviteSupplier the meeingInviteSupplier to set
	 */
	public void setMeeingInviteSupplier(List<EvaluationSuppliersPojo> meeingInviteSupplier) {
		this.meeingInviteSupplier = meeingInviteSupplier;
	}

	/**
	 * @return the meetingDocuments
	 */
	public List<EvaluationDocumentPojo> getMeetingDocuments() {
		return meetingDocuments;
	}

	/**
	 * @param meetingDocuments the meetingDocuments to set
	 */
	public void setMeetingDocuments(List<EvaluationDocumentPojo> meetingDocuments) {
		this.meetingDocuments = meetingDocuments;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * @return the mandatoryMeeting
	 */
	public String getMandatoryMeeting() {
		return mandatoryMeeting;
	}

	/**
	 * @param mandatoryMeeting the mandatoryMeeting to set
	 */
	public void setMandatoryMeeting(String mandatoryMeeting) {
		this.mandatoryMeeting = mandatoryMeeting;
	}

}
