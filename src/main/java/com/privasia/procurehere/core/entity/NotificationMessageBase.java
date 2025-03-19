package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@MappedSuperclass
public class NotificationMessageBase {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@Column(name = "MESSAGE_SUBJECT", length = 250, nullable = false)
	private String subject;

	@Column(name = "MESSAGE_TEXT", length = 2000, nullable = false)
	private String message;

	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.REFRESH)
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.REFRESH)
	@JoinColumn(name = "MESSAGE_TO", nullable = true)
	private User messageTo;

	@Column(name = "TENANT_ID", length = 64, nullable = true)
	private String tenantId;

	@Column(name = "URL_LINK", length = 2000, nullable = true)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(name = "NOTIFICATIONTYPE", length = 50, nullable = false)
	private NotificationType notificationType;

	@Column(name = "IS_PROCESSED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean processed = false;

	@Column(name = "PROCESSED_DATE", nullable = true)
	private Date processedDate;

	public NotificationMessageBase() {
	}

	public NotificationMessageBase(NotificationMessage notificationMessage) {
		this.setCreatedBy(notificationMessage.getCreatedBy());
		this.setCreatedDate(notificationMessage.getCreatedDate());
		this.setMessage(notificationMessage.getMessage());
		this.setMessageTo(notificationMessage.getMessageTo());
		this.setNotificationType(notificationMessage.getNotificationType());
		this.setProcessed(false);
		this.setProcessedDate(null);
		this.setSubject(notificationMessage.getSubject());
		this.setTenantId(notificationMessage.getTenantId());
		this.setUrl(notificationMessage.getUrl());
	}

	/**
	 * @return the processed
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	/**
	 * @return the processedDate
	 */
	public Date getProcessedDate() {
		return processedDate;
	}

	/**
	 * @param processedDate the processedDate to set
	 */
	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
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
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * @return the messageTo
	 */
	public User getMessageTo() {
		return messageTo;
	}

	/**
	 * @param messageTo the messageTo to set
	 */
	public void setMessageTo(User messageTo) {
		this.messageTo = messageTo;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the notificationType
	 */
	public NotificationType getNotificationType() {
		return notificationType;
	}

	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String toLogString() {
		return "NotificationMessageBase [id=" + id + ", subject=" + subject + ", message=" + message + ", processed=" + processed + ", processedDate=" + processedDate + ", createdDate=" + createdDate + ", tenantId=" + tenantId + ", url=" + url + ", notificationType=" + notificationType + "]";
	}

}