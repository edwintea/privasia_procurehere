package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

/**
 * @author priyanka
 */
@MappedSuperclass
public class EventAwardAudit {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = true)
	private Buyer buyer;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Column(name = "THE_DESCRIPTION", length = 3000)
	private String description;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "AUDIT_SNAPSHOT")
	private byte[] snapshot;

	@Column(name = "HAS_SNAPSHOT", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean hasSnapshot = Boolean.FALSE;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "AUDIT_EXCEL_SNAPSHOT")
	private byte[] excelSnapshot;

	@Column(name = "HAS_EXCEL_SNAPSHOT", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean hasExcelSnapshot = Boolean.FALSE;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "AWARD_ATTACH_FILE_DATA")
	private byte[] fileData;

	@Column(name = "AWARD_ATTACH_FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "AWARD_ATTACH_CONT_TYPE", length = 160)
	private String credContentType;

	public EventAwardAudit() {
	}

	public EventAwardAudit(Buyer buyer, User actionBy, Date actionDate, String description, byte[] snapshot, byte[] excelSnapshot) {
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.description = description;
		this.buyer = buyer;
		this.snapshot = snapshot;
		if(snapshot != null) {
			this.hasSnapshot = Boolean.TRUE;
		}
		this.excelSnapshot = excelSnapshot;
		if(excelSnapshot != null) {
			this.hasExcelSnapshot = Boolean.TRUE;
		}
	}

	public EventAwardAudit(String id, String actionBy, Date actionDate, String description, String fileName) {
		this.id = id;
		User user = new User();
		user.setName(actionBy);
		this.actionBy = user;
		this.actionDate = actionDate;
		this.description = description;
		this.fileName = fileName;
	}

	public EventAwardAudit(Buyer buyer, User actionBy, Date actionDate, String description) {
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.description = description;
		this.buyer = buyer;
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
	 * @return the actionBy
	 */
	public User getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
	}

	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
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
	 * @return the snapshot
	 */
	public byte[] getSnapshot() {
		return snapshot;
	}

	/**
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(byte[] snapshot) {
		this.snapshot = snapshot;
	}

	/**
	 * @return the excelSnapshot
	 */
	public byte[] getExcelSnapshot() {
		return excelSnapshot;
	}

	/**
	 * @param excelSnapshot the excelSnapshot to set
	 */
	public void setExcelSnapshot(byte[] excelSnapshot) {
		this.excelSnapshot = excelSnapshot;
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
	 * @return the credContentType
	 */
	public String getCredContentType() {
		return credContentType;
	}

	/**
	 * @param credContentType the credContentType to set
	 */
	public void setCredContentType(String credContentType) {
		this.credContentType = credContentType;
	}

	/**
	 * @return the hasSnapshot
	 */
	public Boolean getHasSnapshot() {
		return hasSnapshot;
	}

	/**
	 * @param hasSnapshot the hasSnapshot to set
	 */
	public void setHasSnapshot(Boolean hasSnapshot) {
		this.hasSnapshot = hasSnapshot;
	}

	/**
	 * @return the hasExcelSnapshot
	 */
	public Boolean getHasExcelSnapshot() {
		return hasExcelSnapshot;
	}

	/**
	 * @param hasExcelSnapshot the hasExcelSnapshot to set
	 */
	public void setHasExcelSnapshot(Boolean hasExcelSnapshot) {
		this.hasExcelSnapshot = hasExcelSnapshot;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventAwardAudit other = (EventAwardAudit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (actionBy == null) {
			if (other.actionBy != null)
				return false;
		} else if (!actionBy.equals(other.actionBy))
			return false;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	public String toLogString() {
		return "EventAudit [id=" + id + ", actionDate=" + actionDate + ",description=" + description + " ]";
	}
}