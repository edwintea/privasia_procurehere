/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENT_CQ_ITEM_OPTION")
public class RfaCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = -2792940376825082581L;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFA_CQ_OPT_CQ_ITM"))
	private RfaCqItem rfaCqItem;

	public RfaCqOption copyFrom(RfaCqItem newCqItem) {
		RfaCqOption newOption = new RfaCqOption();
		newOption.setOrder(getOrder());
		newOption.setScoring(getScoring());
		newOption.setValue(getValue());
		newOption.setRfaCqItem(newCqItem);
		return newOption;
	}

	public RfaCqOption() {
	}

	public RfaCqOption(RfaSupplierCqOption option) {
		this.setOrder(option.getOrder());
		this.setScoring(option.getScoring());
		this.setValue(option.getValue());
		this.setId(option.getId());
	}

	public RfaCqOption(String id, String value) {
		this.setId(id);
		this.setValue(value);
	}

	/**
	 * @return the rfaCqItem
	 */
	public RfaCqItem getRfaCqItem() {
		return rfaCqItem;
	}

	/**
	 * @param rfaCqItem the rfaCqItem to set
	 */
	public void setRfaCqItem(RfaCqItem rfaCqItem) {
		this.rfaCqItem = rfaCqItem;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((rfaCqItem == null) ? 0 : rfaCqItem.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RfaCqOption other = (RfaCqOption) obj;
		if (rfaCqItem == null) {
			if (other.rfaCqItem != null)
				return false;
		} else if (!rfaCqItem.equals(other.rfaCqItem))
			return false;
		return true;
	}

}
