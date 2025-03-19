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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

@Entity
@Table(name = "PROC_BUYER_ANNOUNCEMENT", indexes = { @Index(columnList = "BUYER_ID", name = "INDEX_ANNOUNCEMENT_BUYER_ID") })
public class Announcement implements Serializable {

	private static final long serialVersionUID = 4236369793040535394L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@NotEmpty(message = "{announcement.title.empty}")
	@Size(min = 1, max = 64, message = "{announcement.title.length}")
	@Column(name = "ANNOUNCEMENT_TITLE", nullable = false, length = 64)
	private String title;

	@Size(max = 4000, message = "{announcement.publicContent.length}")
	@Column(name = "PUBLIC_OR_EMAIL_CONTENT", length = 4000)
	private String publicOrEmailContent;

	@Size(max = 1000, message = "{announcement.faxContent.length}")
	@Column(name = "FAX_CONTENT", length = 1000)
	private String faxContent;

	@Size(max = 160, message = "{announcement.smsContent.length}")
	@Column(name = "SMS_CONTENT", length = 160)
	private String smsContent;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ANNOUNCEMENT_START")
	private Date announcementStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ANNOUNCEMENT_END")
	private Date announcementEnd;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_ANNOUNCE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_ANNOUNCE_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_ANNOUNCE_BUYER_ID"))
	private Buyer buyer;

	@Column(name = "FAX_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean fax = Boolean.FALSE;

	@Column(name = "SMS_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sms = Boolean.FALSE;

	@Column(name = "EMAIL_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean email = Boolean.FALSE;

	@Column(name = "IS_FAX_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isFaxSent = Boolean.FALSE;

	@Column(name = "IS_SMS_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isSmsSent = Boolean.FALSE;

	@Column(name = "IS_EMAIL_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isemailSent = Boolean.FALSE;

	@Column(name = "PUBLIC_SENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean publicAnnouncement = Boolean.FALSE;

	@Transient
	private Date announcementStartTime;

	@Transient
	private Date announcementEndTime;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyData;

	public Announcement() {
		this.fax = Boolean.FALSE;
		this.sms = Boolean.FALSE;
		this.email = Boolean.FALSE;
		this.publicAnnouncement = Boolean.FALSE;
	}

	/**
	 * DO NOT REGENERATE THIS GETTER/SETTER
	 * 
	 * @param publicOrEmailContent the publicOrEmailContent to set
	 */
	public void setPublicOrEmailContent(String publicOrEmailContent) {
		if (publicOrEmailContent != null) {
			this.publicOrEmailContent = StringUtils.checkString(publicOrEmailContent).replaceAll("&Acirc;", "").replaceAll("&#160;", " ");
		} else {
			this.publicOrEmailContent = null;
		}
	}

	/**
	 * @return the publicOrEmailContent
	 */
	public String getPublicOrEmailContent() {
		return publicOrEmailContent;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the faxContent
	 */
	public String getFaxContent() {
		return faxContent;
	}

	/**
	 * @param faxContent the faxContent to set
	 */
	public void setFaxContent(String faxContent) {
		this.faxContent = faxContent;
	}

	/**
	 * @return the smsContent
	 */
	public String getSmsContent() {
		return smsContent;
	}

	/**
	 * @param smsContent the smsContent to set
	 */
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	/**
	 * @return the publicAnnouncement
	 */
	public Boolean getPublicAnnouncement() {
		return publicAnnouncement;
	}

	/**
	 * @param publicAnnouncement the publicAnnouncement to set
	 */
	public void setPublicAnnouncement(Boolean publicAnnouncement) {
		this.publicAnnouncement = publicAnnouncement;
	}

	/**
	 * @return the announcementStart
	 */
	public Date getAnnouncementStart() {
		return announcementStart;
	}

	/**
	 * @param announcementStart the announcementStart to set
	 */
	public void setAnnouncementStart(Date announcementStart) {
		this.announcementStart = announcementStart;
	}

	/**
	 * @return the announcementEnd
	 */
	public Date getAnnouncementEnd() {
		return announcementEnd;
	}

	/**
	 * @param announcementEnd the announcementEnd to set
	 */
	public void setAnnouncementEnd(Date announcementEnd) {
		this.announcementEnd = announcementEnd;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
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
	 * @return the fax
	 */
	public Boolean getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(Boolean fax) {
		this.fax = fax;
	}

	/**
	 * @return the sms
	 */
	public Boolean getSms() {
		return sms;
	}

	/**
	 * @param sms the sms to set
	 */
	public void setSms(Boolean sms) {
		this.sms = sms;
	}

	/**
	 * @return the email
	 */
	public Boolean getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(Boolean email) {
		this.email = email;
	}

	/**
	 * @return the announcementStartTime
	 */
	public Date getAnnouncementStartTime() {
		return announcementStartTime;
	}

	/**
	 * @param announcementStartTime the announcementStartTime to set
	 */
	public void setAnnouncementStartTime(Date announcementStartTime) {
		this.announcementStartTime = announcementStartTime;
	}

	/**
	 * @return the announcementEndTime
	 */
	public Date getAnnouncementEndTime() {
		return announcementEndTime;
	}

	/**
	 * @param announcementEndTime the announcementEndTime to set
	 */
	public void setAnnouncementEndTime(Date announcementEndTime) {
		this.announcementEndTime = announcementEndTime;
	}

	/**
	 * @return the readOnlyData
	 */
	public Boolean getReadOnlyData() {
		return readOnlyData;
	}

	/**
	 * @param readOnlyData the readOnlyData to set
	 */
	public void setReadOnlyData(Boolean readOnlyData) {
		this.readOnlyData = readOnlyData;
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
		Announcement other = (Announcement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Announcement [id=" + id + ", title=" + title + ", publicOrEmailContent=" + publicOrEmailContent + ", faxContent=" + faxContent + ", smsContent=" + smsContent + ", announcementStart=" + announcementStart + ", announcementEnd=" + announcementEnd + ", status=" + status + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", fax=" + fax + ", sms=" + sms + ", email=" + email + ", publicAnnouncement=" + publicAnnouncement + ", announcementStartTime=" + announcementStartTime + ", announcementEndTime=" + announcementEndTime + ", readOnlyData=" + readOnlyData + "]";
	}

}
