package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.pojo.EventDocumentPojo;

/**
 * @author Ravi
 */

@Entity
@Table(name = "PROC_RFI_MEETING_DOC")
public class RfiEventMeetingDocument extends EventMeetingDocument implements Serializable {

	private static final long serialVersionUID = 5491018790583438252L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_MEET_DOC") )
	private RfiEventMeeting rfxEventMeeting;

	public RfiEventMeetingDocument() {
	}

	public RfiEventMeetingDocument(EventDocumentPojo documentPojo) {
		this.setCredContentType(documentPojo.getCredContentType());
		this.setFileData(getFileData());
		this.setFileName(getFileName());
		this.setRefId(getRefId());
		this.setTenantId(getTenantId());
	}
	
	public RfiEventMeetingDocument(String id, String fileName, String credContentType, String tenantId, Integer fileSizeInKb) {
		super.setId(id);
		super.setFileName(fileName);
		super.setCredContentType(credContentType);
		super.setTenantId(tenantId);
		super.setFileSizeInKb(fileSizeInKb);
	}

	/**
	 * @return the rfxEventMeeting
	 */
	public RfiEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RfiEventMeeting rfxEventMeeting) {
		this.rfxEventMeeting = rfxEventMeeting;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((rfxEventMeeting == null) ? 0 : rfxEventMeeting.hashCode());
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
		RfiEventMeetingDocument other = (RfiEventMeetingDocument) obj;
		if (rfxEventMeeting == null) {
			if (other.rfxEventMeeting != null)
				return false;
		} else if (!rfxEventMeeting.equals(other.rfxEventMeeting))
			return false;
		return true;
	}

}
