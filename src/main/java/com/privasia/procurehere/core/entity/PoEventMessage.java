package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Edwin
 */
@Entity
@Table(name = "PROC_PO_EVENT_MESSAGE")
public class PoEventMessage extends EventMessage implements Serializable {

	private static final long serialVersionUID = 8224905572336453778L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_PO_EVENT_MSG_PO_ID"))
	private PoEvent event;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_PO_EVNT_MESG_SUPP", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "PO_EVENT_MESG_ID"))
	private List<Supplier> suppliers;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_EVT_MSG_PARENT"))
	private PoEventMessage parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("createdDate")
	private List<PoEventMessage> replies;

	@Column(name="SEND_BY_BUYER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sentByBuyer = Boolean.FALSE;

	@Column(name="SEND_BY_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sentBySupplier = Boolean.FALSE;

	public PoEventMessage(String id, String subject, String message, Date createdDate, String fileName, PoEvent event, User createdBy, PoEventMessage parent, Buyer buyer, String tenantId, Boolean sentByBuyer, Boolean sentBySupplier){
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



	public PoEventMessage() {
		this.sentByBuyer = Boolean.FALSE;
		this.sentBySupplier = Boolean.FALSE;
	}

	/**
	 * @return the event
	 */
	public PoEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(PoEvent event) {
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
	public PoEventMessage getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(PoEventMessage parent) {
		this.parent = parent;
	}

	/**
	 * @return the replies
	 */
	public List<PoEventMessage> getReplies() {
		return replies;
	}

	/**
	 * @param replies the replies to set
	 */
	public void setReplies(List<PoEventMessage> replies) {
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
		return "PoEventMessage [ " + super.toLogString() + "]";
	}

}
