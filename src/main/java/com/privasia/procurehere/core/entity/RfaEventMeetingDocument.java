package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.pojo.EventDocumentPojo;

/**
 * @author RT-Kapil
 * @author Ravi
 */

@Entity
@Table(name = "PROC_RFA_MEETING_DOC")
public class RfaEventMeetingDocument extends EventMeetingDocument implements Serializable {

	private static final long serialVersionUID = -480988466506917467L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "RFA_EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_MEET_DOC") )
	private RfaEventMeeting rfaEventMeeting;

	@Transient
	private String refId;

	public RfaEventMeetingDocument() {
	}

	public RfaEventMeetingDocument(EventDocumentPojo documentPojo) {
		this.setCredContentType(documentPojo.getCredContentType());
		this.setFileData(getFileData());
		this.setFileName(getFileName());
		this.setRefId(getRefId());
		this.setTenantId(getTenantId());
	}

	public RfaEventMeetingDocument(String id, String fileName, String credContentType, String tenantId, Integer fileSizeInKb) {
		super.setId(id);
		super.setFileName(fileName);
		super.setCredContentType(credContentType);
		super.setTenantId(tenantId);
		super.setFileSizeInKb(fileSizeInKb);
	}
	 

	/**
	 * @return the rfaEventMeeting
	 */
	public RfaEventMeeting getRfaEventMeeting() {
		return rfaEventMeeting;
	}

	/**
	 * @param rfaEventMeeting the rfaEventMeeting to set
	 */
	public void setRfaEventMeeting(RfaEventMeeting rfaEventMeeting) {
		this.rfaEventMeeting = rfaEventMeeting;
	}

	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param refId the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}

	 

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfaEventMeetingDocument [toLogString :" + super.toString() + "]";
	}

}
