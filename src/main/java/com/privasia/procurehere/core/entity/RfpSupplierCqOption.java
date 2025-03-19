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
@Table(name = "PROC_RFP_EVT_SUPP_CQ_OPT")
public class RfpSupplierCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = 8551904229069172437L;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFP_SUP_CQ_ITM_OPT"))
	private RfpSupplierCqItem cqItem;

	public RfpSupplierCqOption() {
	}

	public RfpSupplierCqOption(String id, String value) {
		this.setId(id);
		this.setValue(value);

	}

	public RfpSupplierCqOption(String id, String value, String cqId, Integer scoring) {
		this.setId(id);
		this.setValue(value);
		RfpSupplierCqItem cq = new RfpSupplierCqItem();
		cq.setId(cqId);
		this.cqItem = cq;
		this.setScoring(scoring);
	}

	/**
	 * @return the cqItem
	 */
	public RfpSupplierCqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(RfpSupplierCqItem cqItem) {
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
