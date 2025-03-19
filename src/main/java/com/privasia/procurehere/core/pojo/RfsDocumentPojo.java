package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.privasia.procurehere.core.entity.EventDocument;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * @author Sana
 */

@Entity
@Table(name = "PROC_RFS_DOCUMENTS")
public class RfsDocumentPojo extends EventDocument implements Serializable {

	private static final long serialVersionUID = 7078972320588693888L;

	private String sourcingFormRequestId;

	private String uploadById;

	private String uploadByLoginId;

	private String uploadByName;

	private Boolean editorMember = Boolean.FALSE;

	private Boolean loggedInMember = Boolean.FALSE;

	private Boolean approverMember = Boolean.FALSE;

	private Boolean internal = Boolean.TRUE;

	public RfsDocumentPojo() {
	}

	public RfsDocumentPojo(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, String sourcingFormRequestId, String uploadById, String uploadByLoginId, String uploadByName, Boolean internal) {
		setId(id);
		setFileName(fileName);
		setDescription(description);
		setUploadDate(uploadDate);
		setCredContentType(credContentType);
		setFileSizeInKb(fileSizeInKb);
		this.sourcingFormRequestId = sourcingFormRequestId;
		this.uploadById = uploadById;
		this.uploadByLoginId = uploadByLoginId;
		this.uploadByName = uploadByName;
		this.internal = internal;
	}

	public String getSourcingFormRequestId() {
		return sourcingFormRequestId;
	}

	public void setSourcingFormRequestId(String sourcingFormRequestId) {
		this.sourcingFormRequestId = sourcingFormRequestId;
	}

	public String getUploadById() {
		return uploadById;
	}

	public void setUploadById(String uploadById) {
		this.uploadById = uploadById;
	}

	public String getUploadByLoginId() {
		return uploadByLoginId;
	}

	public void setUploadByLoginId(String uploadByLoginId) {
		this.uploadByLoginId = uploadByLoginId;
	}

	public String getUploadByName() {
		return uploadByName;
	}

	public void setUploadByName(String uploadByName) {
		this.uploadByName = uploadByName;
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
}
