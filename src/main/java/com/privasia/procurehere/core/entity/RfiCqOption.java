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
@Table(name = "PROC_RFI_EVENT_CQ_ITEM_OPTION")
public class RfiCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = 390955920779113849L;

	@ManyToOne(optional = false, cascade = {CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFI_CQ_OPT_CQ_ITM") )
	private RfiCqItem cqItem;

	public RfiCqOption() {
	}

	public RfiCqOption copyFrom(RfiCqItem newCqItem) {
		RfiCqOption newOption = new RfiCqOption();
		newOption.setOrder(getOrder());
		newOption.setScoring(getScoring());
		newOption.setValue(getValue());
		newOption.setCqItem(newCqItem);
		return newOption;
	}

	
	public RfiCqOption(RfiSupplierCqOption option) {
		this.setOrder(option.getOrder());
		this.setScoring(option.getScoring());
		this.setValue(option.getValue());
		this.setId(option.getId());
	}
	
	public RfiCqOption(String id, String value) {
		this.setId(id);
		this.setValue(value);
	}

	

	/**
	 * @return the cqItem
	 */
	public RfiCqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(RfiCqItem cqItem) {
		this.cqItem = cqItem;
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
		result = prime * result + ((cqItem == null) ? 0 : cqItem.hashCode());
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
		RfiCqOption other = (RfiCqOption) obj;
		if (cqItem == null) {
			if (other.cqItem != null)
				return false;
		} else if (!cqItem.equals(other.cqItem))
			return false;
		return true;
	}

}
