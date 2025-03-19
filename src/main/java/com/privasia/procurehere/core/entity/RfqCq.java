package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFQ_EVENT_CQ")
public class RfqCq extends Cq implements Serializable {

	private static final long serialVersionUID = 5642953848453594339L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_CQ_EVENT_ID"))
	private RfqEvent rfxEvent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cq", cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<RfqCqItem> cqItems;

	@Transient
	private long mandatoryItemCount;
	
	@Transient
	private SupplierCqStatus supplierCqStatus;

	public RfqCq() {
	}

	public RfqCq(String name, long mandatoryItemCount) {
		setName(name);
		this.mandatoryItemCount = mandatoryItemCount;
	}

	public RfqCq copyFrom() {
		RfqCq newCq = new RfqCq();
		if (CollectionUtil.isNotEmpty(getCqItems())) {
			newCq.setCqItems(new ArrayList<RfqCqItem>());
			for (RfqCqItem cqItem : getCqItems()) {
				RfqCqItem newCqItem = cqItem.copyFrom(newCq);
				newCq.getCqItems().add(newCqItem);
			}
		}

		newCq.setName(getName());
		newCq.setCqOrder(getCqOrder());
		newCq.setDescription(getDescription());
		newCq.setCreatedDate(getCreatedDate());
		newCq.setModifiedDate(getModifiedDate());
		return newCq;
	}

	public RfqCq createMobileEnvelopShallowCopy() {
		RfqCq newCq = new RfqCq();
		newCq.setId(getId());
		newCq.setName(getName());
		newCq.setDescription(getDescription());
		newCq.setCreatedDate(null);
		newCq.setModifiedDate(null);
		return newCq;
	}

	/**
	 * @return the supplierCqStatus
	 */
	public SupplierCqStatus getSupplierCqStatus() {
		return supplierCqStatus;
	}

	/**
	 * @param supplierCqStatus the supplierCqStatus to set
	 */
	public void setSupplierCqStatus(SupplierCqStatus supplierCqStatus) {
		this.supplierCqStatus = supplierCqStatus;
	}

	/**
	 * @return the mandatoryItemCount
	 */
	public long getMandatoryItemCount() {
		return mandatoryItemCount;
	}

	/**
	 * @param mandatoryItemCount the mandatoryItemCount to set
	 */
	public void setMandatoryItemCount(long mandatoryItemCount) {
		this.mandatoryItemCount = mandatoryItemCount;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfqEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfqEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the cqItems
	 */
	public List<RfqCqItem> getCqItems() {
		return cqItems;
	}

	/**
	 * @param cqItems the cqItems to set
	 */
	public void setCqItems(List<RfqCqItem> cqItems) {
		if (this.cqItems == null) {
			this.cqItems = new ArrayList<RfqCqItem>();
		} else {
			this.cqItems.clear();
		}
		if (cqItems != null) {
			this.cqItems.addAll(cqItems);
		}
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
		return "Rfp CQ [ " + super.toLogString() + "]";
	}

}
