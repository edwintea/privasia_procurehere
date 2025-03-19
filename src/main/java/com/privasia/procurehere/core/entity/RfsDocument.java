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

/**
 * @author sudesha
 */

@Entity
@Table(name = "PROC_RFS_DOCUMENTS")
public class RfsDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = 7078972320588693888L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFS_ID", foreignKey = @ForeignKey(name = "FK_RFS_DOCUMENT"))
	private SourcingFormRequest sourcingFormRequest;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOC_UPLOAD_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_DOC_UPLD_BY"))
	private User uploadBy;

	@Column(name = "IS_INTERNAL ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean internal = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean editorMember = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean loggedInMember = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean approverMember = Boolean.FALSE;

	public RfsDocument() {
	}

	public RfsDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, SourcingFormRequest sourcingFormRequest, User uploadBy) {
		this.sourcingFormRequest = sourcingFormRequest;
		if (sourcingFormRequest != null) {
			sourcingFormRequest.getSourcingFormName();
		}
		this.uploadBy = uploadBy;
		if (uploadBy != null) {
			uploadBy.getName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
	}

	public RfsDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, SourcingFormRequest sourcingFormRequest, String uploadById, String uploadByLoginId) {
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
		User uploadBy = new User();
		uploadBy.setId(uploadById);
		uploadBy.setLoginId(uploadByLoginId);
		this.uploadBy = uploadBy;
	}

	public RfsDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Boolean internal, SourcingFormRequest sourcingFormRequest, User uploadBy) {
		this.sourcingFormRequest = sourcingFormRequest;
		if (sourcingFormRequest != null) {
			sourcingFormRequest.getSourcingFormName();
		}
		this.uploadBy = uploadBy;
		if (uploadBy != null) {
			uploadBy.getName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
		setInternal(internal);
	}

	public RfsDocument copyFrom() {
		RfsDocument newDoc = new RfsDocument();
		newDoc.setCredContentType(getCredContentType());
		newDoc.setFileSizeInKb(getFileSizeInKb());
		newDoc.setFileSize(getFileSize());
		newDoc.setDescription(getDescription());
		newDoc.setFileData(getFileData());
		newDoc.setFileName(getFileName());
		newDoc.setUploadDate(getUploadDate());
		return newDoc;
	}

	public RfsDocument(String id, String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
	}

	public RfsDocument(String id, String fileName, Integer fileSizeInKb, String credContentType) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
		setCredContentType(credContentType);
	}

	public RfsDocument createMobileShallowCopy() {
		RfsDocument ic = new RfsDocument();
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

	/**
	 * @return the uploadBy
	 */
	public User getUploadBy() {
		return uploadBy;
	}

	/**
	 * @param uploadBy the uploadBy to set
	 */
	public void setUploadBy(User uploadBy) {
		this.uploadBy = uploadBy;
	}

	public Boolean getEditorMember() {
		return editorMember;
	}

	public void setEditorMember(Boolean editorMember) {
		this.editorMember = editorMember;
	}

	public Boolean getLoggedInMember() {
		return loggedInMember;
	}

	public void setLoggedInMember(Boolean loggedInMember) {
		this.loggedInMember = loggedInMember;
	}

	public Boolean getApproverMember() {
		return approverMember;
	}

	public void setApproverMember(Boolean approverMember) {
		this.approverMember = approverMember;
	}

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
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
