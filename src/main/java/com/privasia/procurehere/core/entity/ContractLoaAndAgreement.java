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
import com.privasia.procurehere.core.utils.StringUtils;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_LOA_AGREEMENT_DOCUMENTS")
public class ContractLoaAndAgreement {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_CONTRACT_CON_ID"))
	private ProductContract productContract;

	@Column(name = "LOA_DESCRIPTION", length = 300)
	@Size(min = 0, max = 300, message = "{event.doc.file.maxlimit}")
	private String loaDescription;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "LOA_FILE_DATA")
	private byte[] loaFileData;

	@Column(name = "LOA_FILE_NAME", length = 500)
	private String loaFileName;

	@Column(name = "LOA_CONTENT_TYPE", length = 160)
	private String loaContentType;

	@Column(name = "LOA_FILE_SIZE", length = 10)
	private Integer loaFileSizeInKb;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOA_UPLOAD_DATE")
	private Date loaUploadDate;

	@Column(name = "AGR_DESCRIPTION", length = 300)
	@Size(min = 0, max = 300, message = "{event.doc.file.maxlimit}")
	private String agreementDescription;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "AGR_FILE_DATA")
	private byte[] agreementFileData;

	@Column(name = "AGR_FILE_NAME", length = 500)
	private String agreementFileName;

	@Column(name = "AGR_CONTENT_TYPE", length = 160)
	private String agreementContentType;

	@Column(name = "AGR_FILE_SIZE", length = 10)
	private Integer agreementFileSizeInKb;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AGR_UPLOAD_DATE")
	private Date agreementUploadDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "LOA_UPLOADED_BY", foreignKey = @ForeignKey(name = "FK_LOA_DOC_UPLOAD_BY"))
	private User loaUploadedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "AGREEMENT_UPLOADED_BY", foreignKey = @ForeignKey(name = "FK_AGR_DOC_UPLOAD_BY"))
	private User agreementUploadedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "LOA_DATE", nullable = true)
	private Date loaDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "AGREEMENT_DATE")
	private Date agreementDate;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	public ContractLoaAndAgreement() {
		super();
	}

	public ContractLoaAndAgreement(String id, ProductContract productContract, String loaDescription, byte[] loaFileData, String loaFileName, String loaContentType, Integer loaFileSizeInKb, Date loaUploadDate, String agreementDescription, byte[] agreementFileData, String agreementFileName, String agreementContentType, Integer agreementFileSizeInKb, Date agreementUploadDate, String tenantId) {
		super();
		this.id = id;
		this.productContract = productContract;
		this.loaDescription = loaDescription;
		this.loaFileData = loaFileData;
		this.loaFileName = loaFileName;
		this.loaContentType = loaContentType;
		this.loaFileSizeInKb = loaFileSizeInKb;
		this.loaUploadDate = loaUploadDate;
		this.agreementDescription = agreementDescription;
		this.agreementFileData = agreementFileData;
		this.agreementFileName = agreementFileName;
		this.agreementContentType = agreementContentType;
		this.agreementFileSizeInKb = agreementFileSizeInKb;
		this.agreementUploadDate = agreementUploadDate;
		this.tenantId = tenantId;
	}

	public ContractLoaAndAgreement(String id, String loaDescription, String loaFileName, String loaContentType, Integer loaFileSizeInKb, Date loaDate, String agreementDescription, String agreementFileName, String agreementContentType, Integer agreementFileSizeInKb, Date agreementDate, String lub, String aub, Date loaUploadDate,Date agreementUploadDate) {
		this.id = id;
		this.loaDescription = loaDescription;
		this.loaFileName = loaFileName;
		this.loaContentType = loaContentType;
		this.loaFileSizeInKb = loaFileSizeInKb;
		this.loaDate = loaDate;
		this.agreementDescription = agreementDescription;
		this.agreementFileName = agreementFileName;
		this.agreementContentType = agreementContentType;
		this.agreementFileSizeInKb = agreementFileSizeInKb;
		this.agreementDate = agreementDate;
		if(StringUtils.checkString(lub).length() >0) {
			User luBy = new User();
			luBy.setName(lub);
			this.loaUploadedBy = luBy;
		}
		if(StringUtils.checkString(aub).length() >0) {
			User auBy = new User();
			auBy.setName(aub);
			this.agreementUploadedBy = auBy;
		}
		this.loaUploadDate = loaUploadDate;
		this.agreementUploadDate = agreementUploadDate;
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
	 * @return the productContract
	 */
	public ProductContract getProductContract() {
		return productContract;
	}

	/**
	 * @param productContract the productContract to set
	 */
	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
	}

	/**
	 * @return the loaDescription
	 */
	public String getLoaDescription() {
		return loaDescription;
	}

	/**
	 * @param loaDescription the loaDescription to set
	 */
	public void setLoaDescription(String loaDescription) {
		this.loaDescription = loaDescription;
	}

	/**
	 * @return the loaFileData
	 */
	public byte[] getLoaFileData() {
		return loaFileData;
	}

	/**
	 * @param loaFileData the loaFileData to set
	 */
	public void setLoaFileData(byte[] loaFileData) {
		this.loaFileData = loaFileData;
	}

	/**
	 * @return the loaFileName
	 */
	public String getLoaFileName() {
		return loaFileName;
	}

	/**
	 * @param loaFileName the loaFileName to set
	 */
	public void setLoaFileName(String loaFileName) {
		this.loaFileName = loaFileName;
	}

	/**
	 * @return the loaContentType
	 */
	public String getLoaContentType() {
		return loaContentType;
	}

	/**
	 * @param loaContentType the loaContentType to set
	 */
	public void setLoaContentType(String loaContentType) {
		this.loaContentType = loaContentType;
	}

	/**
	 * @return the loaFileSizeInKb
	 */
	public Integer getLoaFileSizeInKb() {
		return loaFileSizeInKb;
	}

	/**
	 * @param loaFileSizeInKb the loaFileSizeInKb to set
	 */
	public void setLoaFileSizeInKb(Integer loaFileSizeInKb) {
		this.loaFileSizeInKb = loaFileSizeInKb;
	}

	/**
	 * @return the loaUploadDate
	 */
	public Date getLoaUploadDate() {
		return loaUploadDate;
	}

	/**
	 * @param loaUploadDate the loaUploadDate to set
	 */
	public void setLoaUploadDate(Date loaUploadDate) {
		this.loaUploadDate = loaUploadDate;
	}

	/**
	 * @return the agreementDescription
	 */
	public String getAgreementDescription() {
		return agreementDescription;
	}

	/**
	 * @param agreementDescription the agreementDescription to set
	 */
	public void setAgreementDescription(String agreementDescription) {
		this.agreementDescription = agreementDescription;
	}

	/**
	 * @return the agreementFileData
	 */
	public byte[] getAgreementFileData() {
		return agreementFileData;
	}

	/**
	 * @param agreementFileData the agreementFileData to set
	 */
	public void setAgreementFileData(byte[] agreementFileData) {
		this.agreementFileData = agreementFileData;
	}

	/**
	 * @return the agreementFileName
	 */
	public String getAgreementFileName() {
		return agreementFileName;
	}

	/**
	 * @param agreementFileName the agreementFileName to set
	 */
	public void setAgreementFileName(String agreementFileName) {
		this.agreementFileName = agreementFileName;
	}

	/**
	 * @return the agreementContentType
	 */
	public String getAgreementContentType() {
		return agreementContentType;
	}

	/**
	 * @param agreementContentType the agreementContentType to set
	 */
	public void setAgreementContentType(String agreementContentType) {
		this.agreementContentType = agreementContentType;
	}

	/**
	 * @return the agreementFileSizeInKb
	 */
	public Integer getAgreementFileSizeInKb() {
		return agreementFileSizeInKb;
	}

	/**
	 * @param agreementFileSizeInKb the agreementFileSizeInKb to set
	 */
	public void setAgreementFileSizeInKb(Integer agreementFileSizeInKb) {
		this.agreementFileSizeInKb = agreementFileSizeInKb;
	}

	/**
	 * @return the agreementUploadDate
	 */
	public Date getAgreementUploadDate() {
		return agreementUploadDate;
	}

	/**
	 * @param agreementUploadDate the agreementUploadDate to set
	 */
	public void setAgreementUploadDate(Date agreementUploadDate) {
		this.agreementUploadDate = agreementUploadDate;
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
	 * @return the loaUploadedBy
	 */
	public User getLoaUploadedBy() {
		return loaUploadedBy;
	}

	/**
	 * @return the agreementUploadedBy
	 */
	public User getAgreementUploadedBy() {
		return agreementUploadedBy;
	}

	/**
	 * @param loaUploadedBy the loaUploadedBy to set
	 */
	public void setLoaUploadedBy(User loaUploadedBy) {
		this.loaUploadedBy = loaUploadedBy;
	}

	/**
	 * @param agreementUploadedBy the agreementUploadedBy to set
	 */
	public void setAgreementUploadedBy(User agreementUploadedBy) {
		this.agreementUploadedBy = agreementUploadedBy;
	}

	/**
	 * @return the loaDate
	 */
	public Date getLoaDate() {
		return loaDate;
	}

	/**
	 * @return the agreementDate
	 */
	public Date getAgreementDate() {
		return agreementDate;
	}

	/**
	 * @param loaDate the loaDate to set
	 */
	public void setLoaDate(Date loaDate) {
		this.loaDate = loaDate;
	}

	/**
	 * @param agreementDate the agreementDate to set
	 */
	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}
	
}