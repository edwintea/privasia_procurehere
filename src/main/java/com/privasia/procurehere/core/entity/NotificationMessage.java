package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.privasia.procurehere.core.enums.NotificationType;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_NOTIFICATION_MESSAGE")
public class NotificationMessage extends NotificationMessageBase implements Serializable {

	private static final long serialVersionUID = 8508718033695279581L; 

	public NotificationMessage() {
	}

	public NotificationMessage(String id, String subject, String message, Date createdDate, String tenantId, String url, NotificationType notificationType, boolean processed, Date processedDate) {
		setId(id);
		setSubject(subject);
		setMessage(message);
		setCreatedDate(createdDate);
		setTenantId(tenantId);
		setUrl(url);
		setNotificationType(notificationType);
		setProcessed(processed);
		setProcessedDate(processedDate);
	}

	public String toLogString() {
		return "NotificationMessage [toLogString()=" + super.toLogString() + "]";
	}

}