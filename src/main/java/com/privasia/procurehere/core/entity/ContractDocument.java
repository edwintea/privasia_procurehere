package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_CONTRACT_DOCUMENTS")
public class ContractDocument {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_CONTRACT_DOC_CON_ID"))
	private ProductContract productContract;
	
	@Column(name = "DESCRIPTION", length = 300)
	@Size(min = 0, max = 300, message = "{event.doc.file.maxlimit}")
	private String description;
	
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA", nullable = false)
	private byte[] fileData;
	
	@Column(name = "FILE_NAME", nullable = false, length = 500)
	private String fileName;
	
	@Column(name = "CONTENT_TYPE", length = 160)
	private String contentType;
	
	@Column(name = "FILE_SIZE", length = 10)
	private Integer fileSizeInKb;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPLOAD_DATE", nullable = false)
	private Date uploadDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UPLOADED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_CONT_DOC_UPLOADED_BY"))
	private User uploadedBy;
	
	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	public ContractDocument() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ContractDocument(String id, String fileName, String contentType, ProductContract productContract, Integer fileSizeInKb, Date uploadDate, String description) {
		this.productContract = productContract;
		if (productContract != null) {
			productContract.getContractReferenceNumber();
		}
		setContentType(contentType);
		setFileName(fileName);
		setId(id);
		setFileSizeInKb(fileSizeInKb);
		setUploadDate(uploadDate);
		this.description = description;
	}

	public ContractDocument(String id, String fileName, String contentType, ProductContract productContract, Integer fileSizeInKb, Date uploadDate, String description, User uploadedBy) {
		this.productContract = productContract;
		if (productContract != null) {
			productContract.getContractReferenceNumber();
		}
		setContentType(contentType);
		setFileName(fileName);
		setId(id);
		setFileSizeInKb(fileSizeInKb);
		setUploadDate(uploadDate);
		this.description = description;
		if(uploadedBy != null) {
			this.uploadedBy = new User();
			this.uploadedBy.setName(uploadedBy.getName());
		}
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
	
	public ProductContract getProductContract() {
		return productContract;
	}

	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
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
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	
}