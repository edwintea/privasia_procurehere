package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.AwardCriteria;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Priyanka Singh
 */

@MappedSuperclass
public class EventAward implements Serializable {

	private static final long serialVersionUID = 991923104767238119L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "AWARD_CRITERIA")
	private AwardCriteria awardCriteria;

	@Column(name = "AWARD_REMARKS", length = 1000)
	private String awardRemarks;

	@DecimalMax("99999999999999.999999")
	@Column(name = "GRAND_TOTAL_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal grandTotalPrice;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@DecimalMax("99999999999999.999999")
	@Column(name = "TOTAL_AWARD_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal totalAwardPrice;

	@DecimalMax("9999999999.999999")
	@Column(name = "TOTAL_SUPPLIER_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal totalSupplierPrice;

	@Column(name = "IS_TRANSFERRED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean transferred = Boolean.FALSE;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "AWARD_DOCUMENT")
	private byte[] fileData;

	@Column(name = "AWARD_ATTACH_FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "AWARD_ATTACH_CONT_TYPE", length = 160)
	private String credContentType;

	@Transient
	private MultipartFile attachment;

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
	 * @return the awardCriteria
	 */
	public AwardCriteria getAwardCriteria() {
		return awardCriteria;
	}

	/**
	 * @param awardCriteria the awardCriteria to set
	 */
	public void setAwardCriteria(AwardCriteria awardCriteria) {
		this.awardCriteria = awardCriteria;
	}

	/**
	 * @return the awardRemarks
	 */
	public String getAwardRemarks() {
		return awardRemarks;
	}

	/**
	 * @param awardRemarks the awardRemarks to set
	 */
	public void setAwardRemarks(String awardRemarks) {
		this.awardRemarks = awardRemarks;
	}

	/**
	 * @return the grandTotalPrice
	 */
	public BigDecimal getGrandTotalPrice() {
		return grandTotalPrice;
	}

	/**
	 * @param grandTotalPrice the grandTotalPrice to set
	 */
	public void setGrandTotalPrice(BigDecimal grandTotalPrice) {
		this.grandTotalPrice = grandTotalPrice;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public BigDecimal getTotalAwardPrice() {
		return totalAwardPrice;
	}

	public void setTotalAwardPrice(BigDecimal totalAwardPrice) {
		this.totalAwardPrice = totalAwardPrice;
	}

	public BigDecimal getTotalSupplierPrice() {
		return totalSupplierPrice;
	}

	public void setTotalSupplierPrice(BigDecimal totalSupplierPrice) {
		this.totalSupplierPrice = totalSupplierPrice;
	}

	/**
	 * @return the attachment
	 */
	public MultipartFile getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(MultipartFile attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the transferred
	 */
	public Boolean getTransferred() {
		return transferred;
	}

	/**
	 * @param transferred the transferred to set
	 */
	public void setTransferred(Boolean transferred) {
		this.transferred = transferred;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCredContentType() {
		return credContentType;
	}

	public void setCredContentType(String credContentType) {
		this.credContentType = credContentType;
	}

	@Override
	public String toString() {
		return "EventAward [id=" + id + ", awardCriteria=" + awardCriteria + ", awardRemarks=" + awardRemarks + ", grandTotalPrice=" + grandTotalPrice + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate + "]";
	}

}
