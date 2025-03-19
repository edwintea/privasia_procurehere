package com.privasia.procurehere.core.entity;

import java.io.Serializable;
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

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFP_EVENT_BQ_ITEM")
public class RfpBqItem extends BqItem implements Serializable {

	private static final long serialVersionUID = 3180486897048506346L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFP_EVENT_BQ_ITEM"))
	private RfpEvent rfxEvent;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFP_EVNT_BQI_BQ"))
	private RfpEventBq bq;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFP_EVNT_BQ_PARENT"))
	private RfpBqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("order")
	private List<RfpBqItem> children;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpBqEvaluationComments> evaluationComments;

	public RfpBqItem() {
	}

	public RfpBqItem(RfpEvent rftEvent, RfpEventBq bq, RfpBqItem parent, List<RfpBqItem> children) {
		this.rfxEvent = rftEvent;
		this.bq = bq;
		this.parent = parent;
		this.children = children;
	}

	public RfpBqItem createMobileShallowCopy() {
		RfpBqItem item = new RfpBqItem();
		item.setId(getId());
		item.setItemName(getItemName());
		item.setItemDescription(getItemDescription());
		item.setQuantity(getQuantity());
		item.setUom(getUom() != null ? getUom().createMobileShallowCopy() : null);
		item.setLevel(getLevel());
		item.setOrder(getOrder());
		return item;
	}

	public RfpBqItem copyForRfp(RfpEventBq newBq) {
		RfpBqItem bq = new RfpBqItem();
		bq.setItemName(getItemName());
		bq.setLevel(getLevel());
		bq.setOrder(getOrder());
		bq.setUom(getUom());
		bq.setBq(newBq);
		bq.setQuantity(getQuantity());
		bq.setUnitPrice(getUnitPrice());
		bq.setUnitPriceType(getUnitPriceType());
		bq.setItemDescription(getItemDescription());
		bq.setPriceType(getPriceType());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());

		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());
		bq.setTotalAmount(getTotalAmount());
		bq.setTax(getTax());
		bq.setTaxType(getTaxType());
		bq.setTotalAmountWithTax(getTotalAmountWithTax());
		return bq;
	}

	public RfaBqItem copyForRfa(RfaEventBq newBq) {
		RfaBqItem bq = new RfaBqItem();
		bq.setItemName(getItemName());
		bq.setLevel(getLevel());
		bq.setOrder(getOrder());
		bq.setUom(getUom());
		bq.setBq(newBq);
		bq.setQuantity(getQuantity());
		bq.setUnitPrice(getUnitPrice());
		bq.setUnitPriceType(getUnitPriceType());
		bq.setItemDescription(getItemDescription());
		bq.setPriceType(getPriceType());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());

		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());
		bq.setTotalAmount(getTotalAmount());
		bq.setTax(getTax());
		bq.setTaxType(getTaxType());
		bq.setTotalAmountWithTax(getTotalAmountWithTax());
		return bq;
	}

	public RfqBqItem copyForRfq(RfqEventBq newBq) {
		RfqBqItem bq = new RfqBqItem();
		bq.setItemName(getItemName());
		bq.setBq(newBq);
		bq.setLevel(getLevel());
		bq.setOrder(getOrder());
		bq.setUnitPriceType(getUnitPriceType());
		bq.setUom(getUom());
		bq.setQuantity(getQuantity());
		bq.setUnitPrice(getUnitPrice());
		bq.setItemDescription(getItemDescription());
		bq.setPriceType(getPriceType());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());

		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());
		bq.setTotalAmount(getTotalAmount());
		bq.setTax(getTax());
		bq.setTaxType(getTaxType());
		bq.setTotalAmountWithTax(getTotalAmountWithTax());
		return bq;
	}

	public RftBqItem copyForRft(RftEventBq newBq) {
		RftBqItem bq = new RftBqItem();
		bq.setItemName(getItemName());
		bq.setLevel(getLevel());
		bq.setUnitPriceType(getUnitPriceType());
		bq.setOrder(getOrder());
		bq.setUom(getUom());
		bq.setBq(newBq);
		bq.setQuantity(getQuantity());
		bq.setUnitPrice(getUnitPrice());
		bq.setItemDescription(getItemDescription());
		bq.setPriceType(getPriceType());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());

		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());
		bq.setTotalAmount(getTotalAmount());
		bq.setTax(getTax());
		bq.setTaxType(getTaxType());
		bq.setTotalAmountWithTax(getTotalAmountWithTax());
		return bq;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfpEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfpEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bq
	 */
	public RfpEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RfpEventBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the children
	 */
	public List<RfpBqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RfpBqItem> children) {
		this.children = children;
	}

	/**
	 * @return the parent
	 */
	public RfpBqItem getParent() {
		try {
			if (parent != null) {
				parent.getItemDescription();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfpBqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the evaluationComments
	 */
	public List<RfpBqEvaluationComments> getEvaluationComments() {
		return evaluationComments;
	}

	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	public void setEvaluationComments(List<RfpBqEvaluationComments> evaluationComments) {
		this.evaluationComments = evaluationComments;
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
		return "RftBqItem [ " + toLogString() + "]";
	}

	public RfpBqItem createShallowCopy() {
		RfpBqItem ic = new RfpBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setPriceType(getPriceType());
		ic.setUnitPrice(getUnitPrice());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());

		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());
		ic.setBq(getBq());

		return ic;
	}

	public RfpBqItem createSearchShallowCopy() {
		RfpBqItem ic = new RfpBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setPriceType(getPriceType());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setUnitPrice(getUnitPrice());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());

		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());
		ic.setBq(getBq());
		if (getParent() != null) {
			RfpBqItem parent = new RfpBqItem();
			parent.setId(getParent().getId());
			ic.setParent(parent);
		}
		return ic;
	}
}
