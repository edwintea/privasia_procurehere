package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.PoDocumentType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_PO_SNAPSHOT_DOCS")
public class POSnapshotDocument implements Serializable {

	private static final long serialVersionUID = 8109000307298805081L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "assigned")
	@Column(name = "ID", length = 64)
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "DOC_TYPE", length = 32)
	private PoDocumentType docType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PO_ID", foreignKey = @ForeignKey(name = "FK_PUR_ORD_SNAP_DOCUMENT"))
	private Po po;

	@Column(name = "IS_INTERNAL ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean internal = Boolean.FALSE;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileData;

	@Column(name = "FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "DESCRIPTION", length = 300)
	private String description;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPLOAD_DATE")
	private Date uploadDate;

	@Column(name = "CONTENT_TYPE", length = 160)
	private String credContentType;

	@Column(name = "FILE_SIZE", length = 10)
	private Integer fileSizeInKb;

	public POSnapshotDocument() {
	}

	public POSnapshotDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Po po, PoDocumentType docType) {
		this.po = po;
		if (po != null) {
			po.getName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
		this.docType = docType;
	}

	public POSnapshotDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Po po, Boolean internal) {
		this.po = po;
		if (po != null) {
			po.getName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
		setInternal(internal);
	}

	public POSnapshotDocument(String id, String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the docType
	 */
	public PoDocumentType getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(PoDocumentType docType) {
		this.docType = docType;
	}

	/**
	 * @return the po
	 */
	public Po getPo() {
		return po;
	}

	/**
	 * @param po the po to set
	 */
	public void setPo(Po po) {
		this.po = po;
	}

	/**
	 * @return the internal
	 */
	public Boolean getInternal() {
		return internal;
	}

	/**
	 * @param internal the internal to set
	 */
	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	/**
	 * @return the fileData
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @param fileData the fileData to set
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the uploadDate
	 */
	public Date getUploadDate() {
		return uploadDate;
	}

	/**
	 * @param uploadDate the uploadDate to set
	 */
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	/**
	 * @return the credContentType
	 */
	public String getCredContentType() {
		return credContentType;
	}

	/**
	 * @param credContentType the credContentType to set
	 */
	public void setCredContentType(String credContentType) {
		this.credContentType = credContentType;
	}

	/**
	 * @return the fileSizeInKb
	 */
	public Integer getFileSizeInKb() {
		return fileSizeInKb;
	}

	/**
	 * @param fileSizeInKb the fileSizeInKb to set
	 */
	public void setFileSizeInKb(Integer fileSizeInKb) {
		this.fileSizeInKb = fileSizeInKb;
	}

}
