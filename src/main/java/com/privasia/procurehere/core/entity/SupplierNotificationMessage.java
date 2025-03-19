package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_SUPPLIER_NOTIFICATION", indexes = { @Index(columnList = "MESSAGE_TO", name = "PROC_SUP_NOTIF_MSG_TO_IDX") })
public class SupplierNotificationMessage extends NotificationMessageBase implements Serializable {

	private static final long serialVersionUID = -4728141526268194287L;

	public SupplierNotificationMessage() {
	}
	
	public SupplierNotificationMessage(NotificationMessage notificationMessage) {
		super(notificationMessage);
	}

	public String toLogString() {
		return "SupplierNotificationMessage [toLogString()=" + super.toLogString() + "]";
	}

}