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
@Table(name = "PROC_RFI_EVT_SUPP_CQ_OPT")
public class RfiSupplierCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = -6012695129969842164L;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFI_SUP_CQ_ITM_OPT"))
	private RfiSupplierCqItem cqItem;

	public RfiSupplierCqOption() {
	}

	public RfiSupplierCqOption(String id, String value) {
		this.setId(id);
		this.setValue(value);
	}

	public RfiSupplierCqOption(String id, String value, String cqId, Integer scoring) {
		this.setId(id);
		this.setValue(value);
		RfiSupplierCqItem cq = new RfiSupplierCqItem();
		cq.setId(cqId);
		this.cqItem = cq;
		this.setScoring(scoring);
	}

	/**
	 * @return the cqItem
	 */
	public RfiSupplierCqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(RfiSupplierCqItem cqItem) {
		this.cqItem = cqItem;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode() * prime;
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

}
