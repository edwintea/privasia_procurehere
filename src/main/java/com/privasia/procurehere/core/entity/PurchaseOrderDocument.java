package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.PoDocumentType;
import org.hibernate.annotations.Type;

/**
 * @author ravi
 */

@Entity
@Table(name = "PROC_PUR_ORD_DOCUMENTS")
public class PurchaseOrderDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = -6897895244870252306L;

	@Enumerated(EnumType.STRING)
	@Column(name = "DOC_TYPE", length = 32)
	private PoDocumentType docType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PO_ID", foreignKey = @ForeignKey(name = "FK_PUR_ORD_DOCUMENT"))
	private Po po;

	@Column(name = "IS_INTERNAL ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean internal = Boolean.FALSE;

	public PurchaseOrderDocument() {
		this.internal = Boolean.FALSE;
	}

	public PurchaseOrderDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Po po, PoDocumentType docType) {
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

	public PurchaseOrderDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Po po, Boolean internal) {
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

	public PurchaseOrderDocument copyFrom() {
		PurchaseOrderDocument newDoc = new PurchaseOrderDocument();
		newDoc.setCredContentType(getCredContentType());
		newDoc.setDescription(getDescription());
		newDoc.setFileData(getFileData());
		newDoc.setFileName(getFileName());
		newDoc.setUploadDate(getUploadDate());
		return newDoc;
	}

	public PurchaseOrderDocument(String id, String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
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
