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
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOUR_FORM_CQ_ITEM_OPT_REQ")
public class SourcingFormRequestCqOption extends CqOption implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2640015517823022629L;
	
	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REQ_CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SF_REQ_CQ_OPT_CQ_ITM"))
	private SourcingFormRequestCqItem formCqItemRequest;
	
	public SourcingFormRequestCqOption() {
	}

	/**
	 * @return the formCqItemRequest
	 */
	public SourcingFormRequestCqItem getFormCqItemRequest() {
		return formCqItemRequest;
	}

	/**
	 * @param formCqItemRequest the formCqItemRequest to set
	 */
	public void setFormCqItemRequest(SourcingFormRequestCqItem formCqItemRequest) {
		this.formCqItemRequest = formCqItemRequest;
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
		return "SourcingCqOption [" + super.toLogString() + "]";
	}

}
