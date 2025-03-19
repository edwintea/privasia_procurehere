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
 * @author parveen
 */

@Entity
@Table(name = "PROC_RFT_MEETING_DOC")
public class RftEventMeetingDocument extends EventMeetingDocument implements Serializable {

	private static final long serialVersionUID = -2188757246395753793L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_MEET_DOC") )
	private RftEventMeeting rfxEventMeeting;

	public RftEventMeetingDocument() {
	}

	public RftEventMeetingDocument(EventDocumentPojo documentPojo) {
		this.setCredContentType(documentPojo.getCredContentType());
		this.setFileData(getFileData());
		this.setFileName(getFileName());
		this.setRefId(getRefId());
		this.setTenantId(getTenantId());
	}

	public RftEventMeetingDocument(String id, String fileName, String credContentType, String tenantId, Integer fileSizeInKb) {
		super.setId(id);
		super.setFileName(fileName);
		super.setCredContentType(credContentType);
		super.setTenantId(tenantId);
		super.setFileSizeInKb(fileSizeInKb);
	}
	
	/**
	 * @return the rfxEventMeeting
	 */
	public RftEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RftEventMeeting rfxEventMeeting) {
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
		RftEventMeetingDocument other = (RftEventMeetingDocument) obj;
		if (rfxEventMeeting == null) {
			if (other.rfxEventMeeting != null)
				return false;
		} else if (!rfxEventMeeting.equals(other.rfxEventMeeting))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RftEventMeetingDocument [fileName=" + getFileName() + ", refId=" + getRefId() + "]";
	}

}
