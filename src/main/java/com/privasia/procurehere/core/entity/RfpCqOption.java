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
@Table(name = "PROC_RFP_EVENT_CQ_ITEM_OPTION")
public class RfpCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = 390955920779113849L;

	@ManyToOne(optional = false, cascade = {CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFp_CQ_OPT_CQ_ITM") )
	private RfpCqItem rfpCqItem;
	
	public RfpCqOption() {
	}
	
	public RfpCqOption copyFrom(RfpCqItem newCqItem) {
		RfpCqOption newOption = new RfpCqOption();
		newOption.setOrder(getOrder());
		newOption.setScoring(getScoring());
		newOption.setValue(getValue());
		newOption.setRfpCqItem(newCqItem);
		return newOption;
	}


	public RfpCqOption(RfpSupplierCqOption option) {
		this.setOrder(option.getOrder());
		this.setScoring(option.getScoring());
		this.setValue(option.getValue());
		this.setId(option.getId());
	}
	
	public RfpCqOption(String id, String value) {
		this.setId(id);
		this.setValue(value);
	}

	/**
	 * @return the rfpCqItem
	 */
	public RfpCqItem getRfpCqItem() {
		return rfpCqItem;
	}

	/**
	 * @param rfpCqItem the rfpCqItem to set
	 */
	public void setRfpCqItem(RfpCqItem rfpCqItem) {
		this.rfpCqItem = rfpCqItem;
	}

	/**
	 * /* (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((rfpCqItem == null) ? 0 : rfpCqItem.hashCode());
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
		RfpCqOption other = (RfpCqOption) obj;
		if (rfpCqItem == null) {
			if (other.rfpCqItem != null)
				return false;
		} else if (!rfpCqItem.equals(other.rfpCqItem))
			return false;
		return true;
	}

}
