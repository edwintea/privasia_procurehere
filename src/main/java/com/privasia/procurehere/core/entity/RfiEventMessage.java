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
@Table(name = "PROC_RFI_EVENT_MESSAGE")
public class RfiEventMessage extends EventMessage implements Serializable {

	private static final long serialVersionUID = 8224905572336453778L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_MSG_RFI_ID"))
	private RfiEvent event;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_EVNT_MESG_SUPP", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "RFI_EVENT_MESG_ID"))
	private List<Supplier> suppliers;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_EVT_MSG_PARENT"))
	private RfiEventMessage parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("createdDate")
	private List<RfiEventMessage> replies;
	
	@Column(name="SEND_BY_BUYER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sentByBuyer = Boolean.FALSE;
	
	@Column(name="SEND_BY_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean sentBySupplier = Boolean.FALSE;
	
	
	public RfiEventMessage(String id, String subject, String message, Date createdDate, String fileName, RfiEvent event, User createdBy, RfiEventMessage parent, Buyer buyer, String tenantId, Boolean sentByBuyer, Boolean sentBySupplier){
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
	

	public RfiEventMessage() {
		this.sentByBuyer = Boolean.FALSE;
		this.sentBySupplier = Boolean.FALSE;
		
	}

	/**
	 * @return the event
	 */
	public RfiEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfiEvent event) {
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
	public RfiEventMessage getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfiEventMessage parent) {
		this.parent = parent;
	}

	/**
	 * @return the replies
	 */
	public List<RfiEventMessage> getReplies() {
		return replies;
	}

	/**
	 * @param replies the replies to set
	 */
	public void setReplies(List<RfiEventMessage> replies) {
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
		return "RfiEventApproval [ " + super.toLogString() + "]";
	}

}
