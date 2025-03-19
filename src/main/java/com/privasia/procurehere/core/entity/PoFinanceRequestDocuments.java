/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author nitin
 */
@Entity
@Table(name = "PROC_PO_FINANCE_REQ_DOC")
public class PoFinanceRequestDocuments implements Serializable {

	private static final long serialVersionUID = 2111897470835268985L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REQ_PO_DOC_REQ_ID"))
	private PoFinanceRequest poFinanceRequest;

	@Column(name = "DOCUMENT_NAME", length = 128, nullable = false)
	private String documentName;

	@Column(name = "DOCUMENT_DESC", length = 500, nullable = true)
	private String documentDescription;

	@Column(name = "FILE_NAME", length = 128, nullable = false)
	private String fileName;

	@Column(name = "FILE_SIZE", length = 15, nullable = true)
	private Long fileSize;

	@Column(name = "FILE_DATA", nullable = false)
	private byte[] fileData;

	@Column(name = "DOC_CONTENT_TYPE", length = 160, nullable = false)
	private String contentType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UPLOADED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_REQ_PO_DOC_UPLOAD_BY"))
	private User uploadedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "UPLOAD_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date uploadDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FIN_DOC_TYPE", nullable = false, foreignKey = @ForeignKey(name = "FK_PO_DOC_TYPE"))
	private FinanshereDocumentType documentType;

	
	public PoFinanceRequestDocuments() {
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
	 * @return the poFinanceRequest
	 */
	public PoFinanceRequest getPoFinanceRequest() {
		return poFinanceRequest;
	}

	/**
	 * @param poFinanceRequest the poFinanceRequest to set
	 */
	public void setPoFinanceRequest(PoFinanceRequest poFinanceRequest) {
		this.poFinanceRequest = poFinanceRequest;
	}

	/**
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * @param documentName the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/**
	 * @return the documentDescription
	 */
	public String getDocumentDescription() {
		return documentDescription;
	}

	/**
	 * @param documentDescription the documentDescription to set
	 */
	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
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
	 * @return the fileSize
	 */
	public Long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
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
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the uploadedBy
	 */
	public User getUploadedBy() {
		return uploadedBy;
	}

	/**
	 * @param uploadedBy the uploadedBy to set
	 */
	public void setUploadedBy(User uploadedBy) {
		this.uploadedBy = uploadedBy;
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
	 * @return the documentType
	 */
	public FinanshereDocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(FinanshereDocumentType documentType) {
		this.documentType = documentType;
	}

}
