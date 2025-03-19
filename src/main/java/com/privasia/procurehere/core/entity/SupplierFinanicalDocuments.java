package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Arrays;
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

@Entity
@Table(name = "FINANICAL_DOCUMENTS")
public class SupplierFinanicalDocuments implements Serializable {

	private static final long serialVersionUID = -8109623548085186418L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "FILE_DATA", nullable = false)
	private byte[] fileData;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFD_SUPPLIER_ID"))
	private Supplier supplier;

	@Column(name = "FILE_NAME", length = 500, nullable = false)
	private String fileName;

	@Column(name = "FIN_DOC_CONTENT_TYPE", length = 160, nullable = false)
	private String financialDocContentType;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPLOAD_DATE", nullable = false)
	private Date uploadDate;

	@Column(name = "FILE_DESC", length = 500, nullable = true)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UPLOADED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SFD_UPLOADED_BY"))
	private User uploadedBy;

	public SupplierFinanicalDocuments(String id, String fileName, Date uploadDate, String description) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.uploadDate = uploadDate;
		this.description = description;
	}

	public SupplierFinanicalDocuments() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFinancialDocContentType() {
		return financialDocContentType;
	}

	public void setFinancialDocContentType(String financialDocContentType) {
		this.financialDocContentType = financialDocContentType;
	}

	public User getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(User uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	@Override
	public String toString() {
		return "SupplierFinanicalDocuments [id=" + id + ", fileData=" + Arrays.toString(fileData) + ", supplier=" + supplier + ", fileName=" + fileName + ", financialDocContentType=" + financialDocContentType + ", uploadDate=" + uploadDate + ", description=" + description + "]";
	}

}