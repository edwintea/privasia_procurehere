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
@Table(name = "PROC_RFA_EVENT_MESSAGE")
public class RfaEventMessage extends EventMessage implements Serializable {

	private static final long serialVersionUID = 8224905572336453778L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_MSG_RFA_ID"))
	private RfaEvent event;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_EVNT_MESG_SUPP", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "RFA_EVENT_MESG_ID"))
	private List<Supplier> suppliers;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFA_EVT_MSG_PARENT"))
	private RfaEventMessage parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("createdDate")
	private List<RfaEventMessage> replies;

	@Column(name="SEND_BY_BUYER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sentByBuyer = Boolean.FALSE;
	
	@Column(name="SEND_BY_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sentBySupplier = Boolean.FALSE;
	
	
	public RfaEventMessage(String id, String subject, String message, Date createdDate, String fileName, RfaEvent event, User createdBy, RfaEventMessage parent, Buyer buyer, String tenantId, Boolean sentByBuyer, Boolean sentBySupplier) {
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
	
	public RfaEventMessage() {
		this.sentByBuyer = Boolean.FALSE;
		this.sentByBuyer = Boolean.FALSE;
	}

	/**
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
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
	public RfaEventMessage getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfaEventMessage parent) {
		this.parent = parent;
	}

	/**
	 * @return the replies
	 */
	public List<RfaEventMessage> getReplies() {
		return replies;
	}

	/**
	 * @param replies the replies to set
	 */
	public void setReplies(List<RfaEventMessage> replies) {
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
		return "RfaEventApproval [ " + super.toLogString() + "]";
	}

}
