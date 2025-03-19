package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_RFT_EVENT_MESSAGE")
public class RftEventMessage extends EventMessage implements Serializable {

	private static final long serialVersionUID = 3107950886423698399L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_MSG_RFT_ID"))
	private RftEvent event;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_EVNT_MESG_SUPP", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "RFT_EVENT_MESG_ID"))
	private List<Supplier> suppliers;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_EVT_MSG_PARENT"))
	private RftEventMessage parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("createdDate DESC")
	private List<RftEventMessage> replies;

	@Column(name = "SEND_BY_BUYER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sentByBuyer = Boolean.FALSE;

	@Column(name = "SEND_BY_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sentBySupplier = Boolean.FALSE;

	public RftEventMessage(String id, String subject, String message, Date createdDate, String fileName, RftEvent event, User createdBy, RftEventMessage parent, Buyer buyer, String tenantId, Boolean sentByBuyer, Boolean sentBySupplier) {
		this.event = event;
		if (event != null) {
			event.getEventName();
		}
		setCreatedBy(createdBy.createStripCopy());
		setSubject(subject);
		setMessage(message);
		setCreatedDate(createdDate);
		setFileName(fileName);
		setId(id);
		setParent(parent);
		setBuyer(buyer.createShallowCopy());
		setTenantId(tenantId);
		setSentByBuyer(sentByBuyer);
		setSentBySupplier(sentBySupplier);

	}

	public RftEventMessage() {
		this.sentByBuyer = Boolean.FALSE;
		this.sentBySupplier = Boolean.FALSE;
	}

	/**
	 * @return the event
	 */
	public RftEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RftEvent event) {
		this.event = event;
	}

	/**
	 * @return the suppliers
	 */
	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the parent
	 */
	public RftEventMessage getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RftEventMessage parent) {
		this.parent = parent;
	}

	/**
	 * @return the replies
	 */
	public List<RftEventMessage> getReplies() {
		return replies;
	}

	/**
	 * @param replies the replies to set
	 */
	public void setReplies(List<RftEventMessage> replies) {
		this.replies = replies;
	}

	/**
	 * @return the sentByBuyer
	 */
	public boolean isSentByBuyer() {
		return sentByBuyer;
	}

	/**
	 * @param sentByBuyer the sentByBuyer to set
	 */
	public void setSentByBuyer(boolean sentByBuyer) {
		this.sentByBuyer = sentByBuyer;
	}

	/**
	 * @return the sentBySupplier
	 */
	public boolean isSentBySupplier() {
		return sentBySupplier;
	}

	/**
	 * @param sentBySupplier the sentBySupplier to set
	 */
	public void setSentBySupplier(boolean sentBySupplier) {
		this.sentBySupplier = sentBySupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public String toLogString() {
		return "RftEventMessage [ " + super.toLogString() + "]";
	}

}
