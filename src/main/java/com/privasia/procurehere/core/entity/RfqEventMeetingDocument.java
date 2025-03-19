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
@Table(name = "PROC_RFQ_MEETING_DOC")
public class RfqEventMeetingDocument extends EventMeetingDocument implements Serializable {

	private static final long serialVersionUID = -7430321938224234714L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_MEETING_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_MEET_DOC") )
	private RfqEventMeeting rfxEventMeeting;

	public RfqEventMeetingDocument() {
	}

	public RfqEventMeetingDocument(EventDocumentPojo documentPojo) {
		this.setCredContentType(documentPojo.getCredContentType());
		this.setFileData(getFileData());
		this.setFileName(getFileName());
		this.setRefId(getRefId());
		this.setTenantId(getTenantId());
	}
	
	public RfqEventMeetingDocument(String id, String fileName, String credContentType, String tenantId, Integer fileSizeInKb) {
		super.setId(id);
		super.setFileName(fileName);
		super.setCredContentType(credContentType);
		super.setTenantId(tenantId);
		super.setFileSizeInKb(fileSizeInKb);
	}

	/**
	 * @return the rfxEventMeeting
	 */
	public RfqEventMeeting getRfxEventMeeting() {
		return rfxEventMeeting;
	}

	/**
	 * @param rfxEventMeeting the rfxEventMeeting to set
	 */
	public void setRfxEventMeeting(RfqEventMeeting rfxEventMeeting) {
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
		RfqEventMeetingDocument other = (RfqEventMeetingDocument) obj;
		if (rfxEventMeeting == null) {
			if (other.rfxEventMeeting != null)
				return false;
		} else if (!rfxEventMeeting.equals(other.rfxEventMeeting))
			return false;
		return true;
	}

}
