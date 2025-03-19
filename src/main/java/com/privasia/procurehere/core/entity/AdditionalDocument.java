package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.DocumentReferenceType;

/**
 * @author sudesha
 */

@Entity
@Table(name = "PROC_ADDITIONAL_DOCUMENTS")
public class AdditionalDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = 7078972320588693888L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_ADDITIONAL_DOCUMENT"))
	private RftEvent rftEvent;

	@Enumerated(EnumType.STRING)
	@Column(name = "DOCUMENT_REFERENCE_TYPE")
	private DocumentReferenceType documentReferenceType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_ADD_DOC_CREATED_BY"))
	private User createdBy;

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}



	public AdditionalDocument() {
	}

	public AdditionalDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, RftEvent rftEvent) {
		this.rftEvent = rftEvent;
		if (rftEvent != null) {
			rftEvent.getEventName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
	}

	public AdditionalDocument copyFrom() {
		AdditionalDocument newDoc = new AdditionalDocument();
		newDoc.setCredContentType(getCredContentType());
		newDoc.setDescription(getDescription());
		newDoc.setFileData(getFileData());
		newDoc.setFileName(getFileName());
		newDoc.setUploadDate(getUploadDate());
		return newDoc;
	}

	public AdditionalDocument(String id, String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
	}

	public AdditionalDocument(String id, String fileName, Integer fileSizeInKb, String credContentType) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
		setCredContentType(credContentType);
	}

	public AdditionalDocument createMobileShallowCopy() {
		AdditionalDocument ic = new AdditionalDocument();
		ic.setId(getId());
		ic.setFileName(getFileName());
		ic.setFileSize(getFileSizeInKb());
		ic.setCredContentType(getCredContentType());
		return ic;
	}

	public RftEvent getRftEvent() {
		return rftEvent;
	}

	public void setRftEvent(RftEvent rftEvent) {
		this.rftEvent = rftEvent;
	}

	public DocumentReferenceType getDocumentReferenceType() {
		return documentReferenceType;
	}

	public void setDocumentReferenceType(DocumentReferenceType documentReferenceType) {
		this.documentReferenceType = documentReferenceType;
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

}
