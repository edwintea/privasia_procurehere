package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.PoShare;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_SUPPLIER_SETTINGS")
public class SupplierSettings implements Serializable {

	private static final long serialVersionUID = -129053121491123930L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{buyerSettings.timeZone.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPLIER_TIME_ZONE", nullable = false, foreignKey = @ForeignKey(name = "FK_SUP_SET_TIMEZONE"))
	private TimeZone timeZone;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SUP_SET_SUP_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUPP_SET_MOD_BY"))
	private User modifiedBy;

	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Column(name = "IS_ACCOUNT_CLOSE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isClose = Boolean.FALSE;

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

	@Column(name = "IS_REQUEST_BACKUP")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isBackup = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "USER_REQUESTED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUPPSET_REQUEST_BY"))
	private User requestedBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "PO_SHARE", length = 20)
	private PoShare poShare;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_SUPPLIER_FINANCE_ID", nullable = true, foreignKey = @ForeignKey(name = "PROC_SUP_FIN_ID"))
	private FinanceCompany financeCompany;
	
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileAttatchment;

	@Column(name = "FILE_NAME", length = 200)
	private String fileName;

	@Column(name = "FILE_SIZE_KB", length = 200)
	private Integer fileSizeKb;

	@Column(name = "CONTENT_TYPE", length = 160)
	private String contentType;
	
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
	 * @return the fileAttatchment
	 */
	public byte[] getFileAttatchment() {
		return fileAttatchment;
	}

	/**
	 * @param fileAttatchment the fileAttatchment to set
	 */
	public void setFileAttatchment(byte[] fileAttatchment) {
		this.fileAttatchment = fileAttatchment;
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
	 * @return the fileSizeKb
	 */
	public Integer getFileSizeKb() {
		return fileSizeKb;
	}

	/**
	 * @param fileSizeKb the fileSizeKb to set
	 */
	public void setFileSizeKb(Integer fileSizeKb) {
		this.fileSizeKb = fileSizeKb;
	}

	/**
	 * @return the financeCompany
	 */
	public FinanceCompany getFinanceCompany() {
		return financeCompany;
	}

	/**
	 * @param financeCompany the financeCompany to set
	 */
	public void setFinanceCompany(FinanceCompany financeCompany) {
		this.financeCompany = financeCompany;
	}

	/**
	 * @return the poShare
	 */
	public PoShare getPoShare() {
		return poShare;
	}

	/**
	 * @param poShare the poShare to set
	 */
	public void setPoShare(PoShare poShare) {
		this.poShare = poShare;
	}

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

	/**
	 * @param cancalRequestDate the cancalRequestDate to set
	 */
	public void setCancalRequestDate(Date cancalRequestDate) {
		this.cancalRequestDate = cancalRequestDate;
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
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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

	public Boolean getIsBackup() {
		return isBackup;
	}

	public void setIsBackup(Boolean isBackup) {
		this.isBackup = isBackup;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
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
		SupplierSettings other = (SupplierSettings) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		return true;
	}

	public String toLogString() {
		return "SupplierSettings [id=" + id + ", timeZone=" + timeZone + ", supplier=" + supplier.getId() + ", modifiedDate=" + modifiedDate + "]";
	}

}
