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

/**
 * @author parveen
 */

@Entity
@Table(name = "PROC_PO_DOCUMENTS")
public class PoDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = -2183132189846045138L;

	@Enumerated(EnumType.STRING)
	@Column(name = "DOC_TYPE", length = 32)
	private PoDocumentType docType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PR_ID", foreignKey = @ForeignKey(name = "FK_PO_DOCUMENT"))
	private Pr pr;

	public PoDocument() {
	}

	public PoDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Pr pr, PoDocumentType docType) {
		this.pr = pr;
		if (pr != null) {
			pr.getName();
		}
		setCredContentType(credContentType);
		setDescription(description);
		setFileName(fileName);
		setId(id);
		setUploadDate(uploadDate);
		setFileSizeInKb(fileSizeInKb);
		this.docType = docType;
	}

	public PoDocument copyFrom() {
		PoDocument newDoc = new PoDocument();
		newDoc.setCredContentType(getCredContentType());
		newDoc.setDescription(getDescription());
		newDoc.setFileData(getFileData());
		newDoc.setFileName(getFileName());
		newDoc.setUploadDate(getUploadDate());
		return newDoc;
	}

	public PoDocument(String id, String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
	}

	/*public PoDocument createMobileShallowCopy() {
		PoDocument ic = new PoDocument();
		ic.setId(getId());
		ic.setFileName(getFileName());
		ic.setFileSize(getFileSizeInKb());
		return ic;
	}*/

	/**
	 * @return the pr
	 */
	public Pr getPr() {
		return pr;
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
	 * @param pr the pr to set
	 */
	public void setPr(Pr pr) {
		this.pr = pr;
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
