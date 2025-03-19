package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_BUYER_SETTINGS")

@SqlResultSetMapping(name = "eventReportResult", classes = { @ConstructorResult(targetClass = DraftEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "modifiedDate"), @ColumnResult(name = "type"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "unitName"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventUser"), @ColumnResult(name = "status") }) })
public class BuyerSettings implements Serializable {

	private static final long serialVersionUID = -1576460839193143480L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{buyerSettings.timeZone.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUYER_TIME_ZONE", nullable = false, foreignKey = @ForeignKey(name = "FK_BUYERSET_TIMEZONE"))
	private TimeZone timeZone;

	@NotNull(message = "{buyerSettings.currency.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BASIC_CURRENCY", nullable = false, foreignKey = @ForeignKey(name = "FK_BUYERSET_CURRENCY"))
	private Currency currency;

	@NotNull(message = "{buyerSettings.decimal.not.empty}")
	@Column(name = "BUYER_SET_DECIMAL", nullable = false, length = 64)
	private String decimal;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_BUYERSET_CREATED_BY"))
	private User createdBy;

	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYERSET_MODIFIED_BY"))
	private User modifiedBy;

	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Column(name = "ERP_NOTIFICATION_EMAILS", length = 600)
	private String erpNotificationEmails;

	@Column(name = "IS_ACCOUNT_CLOSE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isClose = Boolean.FALSE;

	@Column(name = "IS_REQUEST_BACKUP")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isBackup = Boolean.FALSE;

	@Column(name = "CLOSE_REQUEST_DATE", nullable = true)
	private Date closeRequestDate;

	@Column(name = "REQUEST_CANCAL_DATE", nullable = true)
	private Date cancalRequestDate;

	@Column(name = "IS_DATA_EXPORT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isExport = Boolean.FALSE;

	@Column(name = "EXPORT_DATE", nullable = true)
	private Date exportDate;

	@Column(name = "EXPORT_URL", nullable = true, length = 1000)
	private String exportURL;

	@Column(name = "BUYER_KEY", nullable = true, length = 64)
	private String buyerKey;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "USER_REQUESTED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYERSET_REQUEST_BY"))
	private User requestedBy;

	@Column(name = "RFI_PUBLISH_URL", nullable = true, length = 1000)
	private String rfiPublishUrl;

	@Column(name = "RFQ_PUBLISH_URL", nullable = true, length = 1000)
	private String rfqPublishUrl;

	@Column(name = "RFT_PUBLISH_URL", nullable = true, length = 1000)
	private String rftPublishUrl;

	@Column(name = "RFI_UPDATE_PUBLISH_URL", nullable = true, length = 1000)
	private String rfiUpdatePublishUrl;

	@Column(name = "RFQ_UPDATE_PUBLISH_URL", nullable = true, length = 1000)
	private String rfqUpdatePublishUrl;

	@Column(name = "RFT_UPDATE_PUBLISH_URL", nullable = true, length = 1000)
	private String rftUpdatePublishUrl;

	@Column(name = "AUTO_CREATE_PO")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoCreatePo = Boolean.FALSE;

	@Column(name = "AUTO_PUBLISH_PO")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoPublishPo = Boolean.FALSE;

	@Column(name = "BUYER_STRIPE_PUBLISH_KEY", length = 500, nullable = true)
	private String stripePublishKey;

	@Column(name = "BUYER_STRIPE_SECRET_KEY", length = 500, nullable = true)
	private String stripeSecretKey;

	@Column(name = "IS_ENA_UNIT_COST_REL")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableUnitAndCostCorrelation = Boolean.FALSE;

	@Column(name = "IS_ENA_UNIT_GRPCOD_REL")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableUnitAndGrpCodeCorrelation = Boolean.FALSE;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileAttatchment;

	@Column(name = "FILE_NAME", length = 200)
	private String fileName;

	@Column(name = "CONTENT_TYPE", length = 160)
	private String contentType;

	@Column(name = "FILE_SIZE_KB", length = 200)
	private Integer fileSizeKb;

	@Transient
	private String mode;

	/**
	 * @return the requestedBy
	 */
	public User getRequestedBy() {
		return requestedBy;
	}

	/**
	 * @param requestedBy the requestedBy to set
	 */
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	/**
	 * @return the exportURL
	 */
	public String getExportURL() {
		return exportURL;
	}

	/**
	 * @param exportURL the exportURL to set
	 */
	public void setExportURL(String exportURL) {
		this.exportURL = exportURL;
	}

	/**
	 * @return the isExport
	 */
	public Boolean getIsExport() {
		return isExport;
	}

	/**
	 * @param isExport the isExport to set
	 */
	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}

	/**
	 * @return the exportDate
	 */
	public Date getExportDate() {
		return exportDate;
	}

	/**
	 * @param exportDate the exportDate to set
	 */
	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
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
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return
	 */
	public byte[] getFileAttatchment() {
		return fileAttatchment;
	}

	/**
	 * @param fileAttatchment
	 */
	public void setFileAttatchment(byte[] fileAttatchment) {
		this.fileAttatchment = fileAttatchment;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return
	 */
	public Integer getFileSizeKb() {
		return fileSizeKb;
	}

	/**
	 * @param fileSizeKb
	 */
	public void setFileSizeKb(Integer fileSizeKb) {
		this.fileSizeKb = fileSizeKb;
	}

	/**
	 * @return the isClose
	 */
	public Boolean getIsClose() {
		return isClose;
	}

	/**
	 * @param isClose the isClose to set
	 */
	public void setIsClose(Boolean isClose) {
		this.isClose = isClose;
	}

	/**
	 * @return the erpNotificationEmails
	 */
	public String getErpNotificationEmails() {
		return erpNotificationEmails;
	}

	/**
	 * @param erpNotificationEmails the erpNotificationEmails to set
	 */
	public void setErpNotificationEmails(String erpNotificationEmails) {
		this.erpNotificationEmails = erpNotificationEmails;
	}

	/**
	 * @return the closeRequestDate
	 */
	public Date getCloseRequestDate() {
		return closeRequestDate;
	}

	/**
	 * @param closeRequestDate the closeRequestDate to set
	 */
	public void setCloseRequestDate(Date closeRequestDate) {
		this.closeRequestDate = closeRequestDate;
	}

	/**
	 * @return the cancalRequestDate
	 */
	public Date getCancalRequestDate() {
		return cancalRequestDate;
	}

	public Boolean getIsBackup() {
		return isBackup;
	}

	public void setIsBackup(Boolean isBackup) {
		this.isBackup = isBackup;
	}

	/**
	 * @return the buyerKey
	 */
	public String getBuyerKey() {
		return buyerKey;
	}

	/**
	 * @param buyerKey the buyerKey to set
	 */
	public void setBuyerKey(String buyerKey) {
		this.buyerKey = buyerKey;
	}

	/**
	 * @param cancalRequestDate the cancalRequestDate to set
	 */
	public void setCancalRequestDate(Date cancalRequestDate) {
		this.cancalRequestDate = cancalRequestDate;
	}

	/**
	 * @return the rfiPublishUrl
	 */
	public String getRfiPublishUrl() {
		return rfiPublishUrl;
	}

	/**
	 * @param rfiPublishUrl the rfiPublishUrl to set
	 */
	public void setRfiPublishUrl(String rfiPublishUrl) {
		this.rfiPublishUrl = rfiPublishUrl;
	}

	/**
	 * @return the rfqPublishUrl
	 */
	public String getRfqPublishUrl() {
		return rfqPublishUrl;
	}

	/**
	 * @param rfqPublishUrl the rfqPublishUrl to set
	 */
	public void setRfqPublishUrl(String rfqPublishUrl) {
		this.rfqPublishUrl = rfqPublishUrl;
	}

	/**
	 * @return the rftPublishUrl
	 */
	public String getRftPublishUrl() {
		return rftPublishUrl;
	}

	/**
	 * @param rftPublishUrl the rftPublishUrl to set
	 */
	public void setRftPublishUrl(String rftPublishUrl) {
		this.rftPublishUrl = rftPublishUrl;
	}

	/**
	 * @return the rfiUpdatePublishUrl
	 */
	public String getRfiUpdatePublishUrl() {
		return rfiUpdatePublishUrl;
	}

	/**
	 * @param rfiUpdatePublishUrl the rfiUpdatePublishUrl to set
	 */
	public void setRfiUpdatePublishUrl(String rfiUpdatePublishUrl) {
		this.rfiUpdatePublishUrl = rfiUpdatePublishUrl;
	}

	/**
	 * @return the rfqUpdatePublishUrl
	 */
	public String getRfqUpdatePublishUrl() {
		return rfqUpdatePublishUrl;
	}

	/**
	 * @param rfqUpdatePublishUrl the rfqUpdatePublishUrl to set
	 */
	public void setRfqUpdatePublishUrl(String rfqUpdatePublishUrl) {
		this.rfqUpdatePublishUrl = rfqUpdatePublishUrl;
	}

	/**
	 * @return the rftUpdatePublishUrl
	 */
	public String getRftUpdatePublishUrl() {
		return rftUpdatePublishUrl;
	}

	/**
	 * @param rftUpdatePublishUrl the rftUpdatePublishUrl to set
	 */
	public void setRftUpdatePublishUrl(String rftUpdatePublishUrl) {
		this.rftUpdatePublishUrl = rftUpdatePublishUrl;
	}

	/**
	 * @return the autoCreatePo
	 */
	public Boolean getAutoCreatePo() {
		return autoCreatePo;
	}

	/**
	 * @param autoCreatePo the autoCreatePo to set
	 */
	public void setAutoCreatePo(Boolean autoCreatePo) {
		this.autoCreatePo = autoCreatePo;
	}

	/**
	 * @return the autoPublishPo
	 */
	public Boolean getAutoPublishPo() {
		return autoPublishPo;
	}

	/**
	 * @param autoPublishPo the autoPublishPo to set
	 */
	public void setAutoPublishPo(Boolean autoPublishPo) {
		this.autoPublishPo = autoPublishPo;
	}

	public String getStripePublishKey() {
		return stripePublishKey;
	}

	public void setStripePublishKey(String stripePublishKey) {
		this.stripePublishKey = stripePublishKey;
	}

	public String getStripeSecretKey() {
		return stripeSecretKey;
	}

	public void setStripeSecretKey(String stripeSecretKey) {
		this.stripeSecretKey = stripeSecretKey;
	}

	/**
	 * @return the enableUnitAndCostCorrelation
	 */
	public Boolean getEnableUnitAndCostCorrelation() {
		return enableUnitAndCostCorrelation;
	}

	/**
	 * @param enableUnitAndCostCorrelation the enableUnitAndCostCorrelation to set
	 */
	public void setEnableUnitAndCostCorrelation(Boolean enableUnitAndCostCorrelation) {
		this.enableUnitAndCostCorrelation = enableUnitAndCostCorrelation;
	}

	/**
	 * @return the enableUnitAndGrpCodeCorrelation
	 */
	public Boolean getEnableUnitAndGrpCodeCorrelation() {
		return enableUnitAndGrpCodeCorrelation;
	}

	/**
	 * @param enableUnitAndGrpCodeCorrelation the enableUnitAndGrpCodeCorrelation to set
	 */
	public void setEnableUnitAndGrpCodeCorrelation(Boolean enableUnitAndGrpCodeCorrelation) {
		this.enableUnitAndGrpCodeCorrelation = enableUnitAndGrpCodeCorrelation;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((decimal == null) ? 0 : decimal.hashCode());
		result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuyerSettings other = (BuyerSettings) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (decimal == null) {
			if (other.decimal != null)
				return false;
		} else if (!decimal.equals(other.decimal))
			return false;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BuyerSettings [id=" + id + ", timeZone=" + timeZone + ", currency=" + currency + ", decimal=" + decimal + "]";
	}

}
