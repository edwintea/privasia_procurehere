package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_RFT_DOCUMENTS")
public class RftEventDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = -882805476289659468L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_DOC"))
	private RftEvent rfxEvent;

	@Column(name = "IS_INTERNAL ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean internal = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOC_UPLOAD_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_DOC_UPLD_BY"))
	private User uploadBy;

	@Transient
	private String uploadByName;

	public RftEventDocument() {
	}

	public RftEventDocument(String id, String fileName, String description, Date uploadDate, String credContentType, RftEvent rfxEvent, byte[] fileData) {
		this.rfxEvent = rfxEvent;
		if (rfxEvent != null) {
			rfxEvent.getEventName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setFileData(fileData);
		setId(id);
		setUploadDate(uploadDate);
		setFileSize((fileData.length) / 1024);
	}

	public RftEventDocument(String id, String fileName, String description, Date uploadDate, String credContentType, RftEvent rfxEvent, Integer fileSizeInKb) {
		this.rfxEvent = rfxEvent;
		if (rfxEvent != null) {
			rfxEvent.getEventName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
	}

	public RftEventDocument copyFrom(RftEvent oldevent) {
		RftEventDocument newDocs = new RftEventDocument();
		newDocs.setFileData(getFileData());
		newDocs.setFileSizeInKb(getFileSizeInKb());
		newDocs.setFileSize(getFileSize());
		newDocs.setFileName(getFileName());
		newDocs.setDescription(getDescription());
		newDocs.setCredContentType(getCredContentType());
		newDocs.setUploadDate(getUploadDate());
		newDocs.setInternal(getInternal());
		newDocs.setRfxEvent(oldevent);
		newDocs.setUploadBy(uploadBy);
		return newDocs;
	}

	public RftEventDocument(String id, String fileName, String description, Date uploadDate, String credContentType, RftEvent rfxEvent, Integer fileSizeInKb,Boolean internal, User uploadBy, String uploadByName) {
		this.rfxEvent = rfxEvent;
		if (rfxEvent != null) {
			rfxEvent.getEventName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
		setInternal(internal);
		setUploadByName(uploadByName);
		this.uploadBy = uploadBy;
		if (uploadBy != null) {
			uploadBy.getName();
		}
		setUploadBy(uploadBy);
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

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	public User getUploadBy() {
		return uploadBy;
	}

	public void setUploadBy(User uploadBy) {
		this.uploadBy = uploadBy;
	}

	public String getUploadByName() {
		return uploadByName;
	}

	public void setUploadByName(String uploadByName) {
		this.uploadByName = uploadByName;
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

	public String toLogString() {
		return "RftEventDocument [" + toLogString() + "]";
	}

}
