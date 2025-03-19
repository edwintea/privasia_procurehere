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
@Table(name = "PROC_SOUR_FORM_CQ_ITEM_OPT")
public class SourcingFormCqOption extends CqOption implements Serializable {

	private static final long serialVersionUID = 7276321843922053557L;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SOURCING_CQ_OPT_CQ_ITM"))
	private SourcingTemplateCqItem formCqItem;

	public SourcingFormCqOption() {
	}

	public SourcingFormCqOption(SourcingFormRequestCqOption option) {
		this.setOrder(option.getOrder());
		this.setScoring(option.getScoring());
		this.setValue(option.getValue());
		this.setId(option.getId());
	}
	
	public SourcingFormCqOption(String id, String value, Integer order) {
		this.setId(id);
		this.setValue(value);
		this.setOrder(order);
	}

	/**
	 * @return the formCqItem
	 */
	public SourcingTemplateCqItem getFormCqItem() {
		return formCqItem;
	}

	/**
	 * @param formCqItem the formCqItem to set
	 */
	public void setFormCqItem(SourcingTemplateCqItem formCqItem) {
		this.formCqItem = formCqItem;
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

	public SourcingFormCqOption copyFrom(SourcingTemplateCqItem newCqItem) {
		SourcingFormCqOption newOption = new SourcingFormCqOption();
		newOption.setOrder(getOrder());
		newOption.setScoring(getScoring());
		newOption.setValue(getValue());
		newOption.setFormCqItem(newCqItem);
		return newOption;
	}

}
