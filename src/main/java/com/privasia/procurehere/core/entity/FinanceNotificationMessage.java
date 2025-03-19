package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_FINANCE_NOTIFICATION", indexes = { @Index(columnList = "MESSAGE_TO", name = "PROC_FIN_NOTIF_MSG_TO_IDX") })
public class FinanceNotificationMessage extends NotificationMessageBase implements Serializable {

	private static final long serialVersionUID = -4728141526268194287L;

	public FinanceNotificationMessage() {
	}

	public FinanceNotificationMessage(NotificationMessage notificationMessage) {
		super(notificationMessage);
	}

	public String toLogString() {
		return "FinanceNotificationMessage [toLogString()=" + super.toLogString() + "]";
	}

}