package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_BUYER_NOTIFICATION", indexes = { @Index(columnList = "MESSAGE_TO", name = "PROC_BUY_NOTIF_MSG_TO_IDX") })
public class BuyerNotificationMessage extends NotificationMessageBase implements Serializable {

	private static final long serialVersionUID = -3377517004889548737L;

	public BuyerNotificationMessage() {
	}

	public BuyerNotificationMessage(NotificationMessage notificationMessage) {
		super(notificationMessage);
	}

	public String toLogString() {
		return "BuyerNotificationMessage [toLogString()=" + super.toLogString() + "]";
	}

}