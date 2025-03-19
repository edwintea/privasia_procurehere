package com.privasia.procurehere.core.pojo;

import java.util.Date;

import com.privasia.procurehere.core.enums.Status;

public class AnnouncementPojo {

	private String id;

	private String title;

	private String publicOrEmailContent;

	private String faxContent;

	private String smsContent;

	private Date announcementStart;

	private Date announcementEnd;

	private Status status;

	private String createdBy;

	private Date createdDate;

	private String modifiedBy;

	private Date modifiedDate;

	private String buyerId;

	private Boolean fax;

	private Boolean sms;

	private Boolean email;

	private Boolean isFaxSent;

	private Boolean isSmsSent;

	private Boolean isemailSent;

	private Boolean publicAnnouncement;

	public AnnouncementPojo(String id, String title, String publicOrEmailContent, String faxContent, String smsContent, Date announcementStart, Date announcementEnd, Status status, String createdBy, Date createdDate, String modifiedBy, Date modifiedDate, String buyer, Boolean fax, Boolean sms, Boolean email, Boolean isFaxSent, Boolean isSmsSent, Boolean isemailSent, Boolean publicAnnouncement) {
		super();
		this.id = id;
		this.title = title;
		this.publicOrEmailContent = publicOrEmailContent;
		this.faxContent = faxContent;
		this.smsContent = smsContent;
		this.announcementStart = announcementStart;
		this.announcementEnd = announcementEnd;
		this.status = status;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
		this.buyerId = buyer;
		this.fax = fax;
		this.sms = sms;
		this.email = email;
		this.isFaxSent = isFaxSent;
		this.isSmsSent = isSmsSent;
		this.isemailSent = isemailSent;
		this.publicAnnouncement = publicAnnouncement;
	}

	public AnnouncementPojo(String id, String title, String publicOrEmailContent, String faxContent, String smsContent, Date announcementStart, Date announcementEnd, Status status, Date createdDate, Date modifiedDate, String buyer, Boolean fax, Boolean sms, Boolean email, Boolean isFaxSent, Boolean isSmsSent, Boolean isemailSent, Boolean publicAnnouncement) {
		super();
		this.id = id;
		this.title = title;
		this.publicOrEmailContent = publicOrEmailContent;
		this.faxContent = faxContent;
		this.smsContent = smsContent;
		this.announcementStart = announcementStart;
		this.announcementEnd = announcementEnd;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.buyerId = buyer;
		this.fax = fax;
		this.sms = sms;
		this.email = email;
		this.isFaxSent = isFaxSent;
		this.isSmsSent = isSmsSent;
		this.isemailSent = isemailSent;
		this.publicAnnouncement = publicAnnouncement;
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
	 * @return the publicOrEmailContent
	 */
	public String getPublicOrEmailContent() {
		return publicOrEmailContent;
	}

	/**
	 * @param publicOrEmailContent the publicOrEmailContent to set
	 */
	public void setPublicOrEmailContent(String publicOrEmailContent) {
		this.publicOrEmailContent = publicOrEmailContent;
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
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
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
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
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
	 * @return the buyerId
	 */
	public String getBuyerId() {
		return buyerId;
	}

	/**
	 * @param buyerId the buyerId to set
	 */
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
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
	 * @return the isFaxSent
	 */
	public Boolean getIsFaxSent() {
		return isFaxSent;
	}

	/**
	 * @param isFaxSent the isFaxSent to set
	 */
	public void setIsFaxSent(Boolean isFaxSent) {
		this.isFaxSent = isFaxSent;
	}

	/**
	 * @return the isSmsSent
	 */
	public Boolean getIsSmsSent() {
		return isSmsSent;
	}

	/**
	 * @param isSmsSent the isSmsSent to set
	 */
	public void setIsSmsSent(Boolean isSmsSent) {
		this.isSmsSent = isSmsSent;
	}

	/**
	 * @return the isemailSent
	 */
	public Boolean getIsemailSent() {
		return isemailSent;
	}

	/**
	 * @param isemailSent the isemailSent to set
	 */
	public void setIsemailSent(Boolean isemailSent) {
		this.isemailSent = isemailSent;
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

}
