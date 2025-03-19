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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_CQ")
public class SourcingTemplateCq extends Cq implements Serializable {

	private static final long serialVersionUID = -1260467163250157807L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_ID", foreignKey = @ForeignKey(name = "FK_SOURCING_TEM_CQ_FORM_ID"))
	private SourcingFormTemplate sourcingForm;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cq", cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SourcingTemplateCqItem> cqItems;

	public SourcingTemplateCq() {
	}

	/**
	 * @return the sourcingForm
	 */
	public SourcingFormTemplate getSourcingForm() {
		return sourcingForm;
	}

	/**
	 * @param sourcingForm the sourcingForm to set
	 */
	public void setSourcingForm(SourcingFormTemplate sourcingForm) {
		this.sourcingForm = sourcingForm;
	}

	/**
	 * @return the cqItems
	 */
	public List<SourcingTemplateCqItem> getCqItems() {
		return cqItems;
	}

	/**
	 * @param cqItems the cqItems to set
	 */
	public void setCqItems(List<SourcingTemplateCqItem> cqItems) {
		this.cqItems = cqItems;
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
		return "Sourcing Form CQ [ " + super.toLogString() + "]";
	}

	public SourcingTemplateCq copyFrom() {
		SourcingTemplateCq newCq = new SourcingTemplateCq();
		if (CollectionUtil.isNotEmpty(getCqItems())) {
			newCq.setCqItems(new ArrayList<SourcingTemplateCqItem>());
			for (SourcingTemplateCqItem cqItem : getCqItems()) {
				SourcingTemplateCqItem newCqItem = cqItem.copyFrom(newCq);
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

}
