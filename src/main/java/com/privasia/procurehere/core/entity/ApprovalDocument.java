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

/**
 * @author sudesha
 */

@Entity
@Table(name = "PROC_APPROVAL_DOCUMENTS")
public class ApprovalDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = 7078972320588693888L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFS_ID", foreignKey = @ForeignKey(name = "FK_APPROVAL_DOCUMENT"))
	private SourcingFormRequest sourcingFormRequest;

	public ApprovalDocument() {
	}

	public ApprovalDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
		if (sourcingFormRequest != null) {
			sourcingFormRequest.getSourcingFormName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
	}

	public ApprovalDocument copyFrom() {
		ApprovalDocument newDoc = new ApprovalDocument();
		newDoc.setCredContentType(getCredContentType());
		newDoc.setDescription(getDescription());
		newDoc.setFileData(getFileData());
		newDoc.setFileName(getFileName());
		newDoc.setUploadDate(getUploadDate());
		return newDoc;
	}

	public ApprovalDocument(String id, String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
	}

	public ApprovalDocument(String id, String fileName, Integer fileSizeInKb, String credContentType) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
		setCredContentType(credContentType);
	}

	public ApprovalDocument createMobileShallowCopy() {
		ApprovalDocument ic = new ApprovalDocument();
		ic.setId(getId());
		ic.setFileName(getFileName());
		ic.setFileSize(getFileSizeInKb());
		ic.setCredContentType(getCredContentType());
		return ic;
	}

	/**
	 * @return the sourcingFormRequest
	 */
	public SourcingFormRequest getSourcingFormRequest() {
		return sourcingFormRequest;
	}

	/**
	 * @param sourcingFormRequest the sourcingFormRequest to set
	 */
	public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
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
