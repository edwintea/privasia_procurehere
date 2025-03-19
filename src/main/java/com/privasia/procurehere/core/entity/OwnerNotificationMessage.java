package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_OWNER_NOTIFICATION", indexes = { @Index(columnList = "MESSAGE_TO", name = "PROC_OWN_NOTIF_MSG_TO_IDX") })
public class OwnerNotificationMessage extends NotificationMessageBase implements Serializable {

	private static final long serialVersionUID = -4728141526268194287L;

	public OwnerNotificationMessage() {
	}

	public OwnerNotificationMessage(NotificationMessage notificationMessage) {
		super(notificationMessage);
	}

	public String toLogString() {
		return "OwnerNotificationMessage [toLogString()=" + super.toLogString() + "]";
	}

}