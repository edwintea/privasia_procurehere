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
 * @author arc
 */
@Entity
@Table(name = "PROC_RFT_EVENT_CQ_ITEM_OPTION")
public class RftCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = 866072085901713637L;

	@ManyToOne(optional = false, cascade = {CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_CQ_OPT_CQ_ITM") )
	private RftCqItem rftCqItem;

	public RftCqOption() {
	}

	
	public RftCqOption copyFrom(RftCqItem newCqItem) {
		RftCqOption newOption = new RftCqOption();
		newOption.setOrder(getOrder());
		newOption.setScoring(getScoring());
		newOption.setValue(getValue());
		newOption.setRftCqItem(newCqItem);
		return newOption;
	}

	public RftCqOption(RftSupplierCqOption option) {
		this.setOrder(option.getOrder());
		this.setScoring(option.getScoring());
		this.setValue(option.getValue());
		this.setId(option.getId());
	}
	
	public RftCqOption(String id, String value) {
		this.setId(id);
		this.setValue(value);
	}

	/**
	 * @return the rftCqItem
	 */
	public RftCqItem getRftCqItem() {
		return rftCqItem;
	}

	/**
	 * @param rftCqItem the rftCqItem to set
	 */
	public void setRftCqItem(RftCqItem rftCqItem) {
		this.rftCqItem = rftCqItem;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
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
 
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RftCqOption [" + super.toLogString() + "]";
	}


}
