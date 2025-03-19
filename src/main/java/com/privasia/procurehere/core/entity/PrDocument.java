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
 * @author parveen
 */

@Entity
@Table(name = "PROC_PR_DOCUMENTS")
public class PrDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = 7078972320588693888L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PR_ID", foreignKey = @ForeignKey(name = "FK_PR_DOCUMENT"))
	private Pr pr;

	public PrDocument() {
	}

	public PrDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, Pr pr) {
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
	}

	public PrDocument copyFrom() {
		PrDocument newDoc = new PrDocument();
		newDoc.setCredContentType(getCredContentType());
		newDoc.setDescription(getDescription());
		newDoc.setFileData(getFileData());
		newDoc.setFileName(getFileName());
		newDoc.setUploadDate(getUploadDate());
		return newDoc;
	}

	public PrDocument( String id , String fileName, Integer fileSizeInKb) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
	}
	
	public PrDocument( String id , String fileName, Integer fileSizeInKb, String credContentType) {
		setId(id);
		setFileName(fileName);
		setFileSizeInKb(fileSizeInKb);
		setCredContentType(credContentType);
	}
	
	public PrDocument createMobileShallowCopy() {
		PrDocument ic = new PrDocument();
		ic.setId(getId());
		ic.setFileName(getFileName());
		ic.setFileSize(getFileSizeInKb());
		ic.setCredContentType(getCredContentType());
		return ic;
	}
	
	/**
	 * @return the pr
	 */
	public Pr getPr() {
		return pr;
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
