/**
 * 
 */
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
import com.privasia.procurehere.core.utils.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFT_EVENT_BQ_ITEM")
public class RftBqItem extends BqItem implements Serializable {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	private static final long serialVersionUID = 7901786296713024961L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_EVENT_BQ_ITEM"))
	private RftEvent rfxEvent;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVNT_BQI_BQ"))
	private RftEventBq bq;

	@ManyToOne(optional = true, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_EVNT_BQ_PARENT"))
	private RftBqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("order")
	private List<RftBqItem> children;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftBqEvaluationComments> evaluationComments;

	public RftBqItem createMobileShallowCopy() {
		RftBqItem item = new RftBqItem();
		item.setId(getId());
		item.setItemName(getItemName());
		item.setItemDescription(getItemDescription());
		item.setQuantity(getQuantity());
		item.setUom(getUom() != null ? getUom().createMobileShallowCopy() : null);
		item.setLevel(getLevel());
		item.setOrder(getOrder());
		return item;
	}

	public RftBqItem copyForRft(RftEventBq newBq) {
		RftBqItem bqItem = new RftBqItem();
		bqItem.setItemName(getItemName());
		LOG.info("++++++RftBqItem+++++++++++getItemName()++++++++++++" + getItemName());
		bqItem.setLevel(getLevel());
		bqItem.setOrder(getOrder());
		bqItem.setBq(newBq);
		bqItem.setUom(getUom());
		bqItem.setQuantity(getQuantity());
		bqItem.setUnitPrice(getUnitPrice());
		bqItem.setUnitPriceType(getUnitPriceType());
		bqItem.setItemDescription(getItemDescription());
		bqItem.setPriceType(getPriceType());
		bqItem.setField1(getField1());
		bqItem.setField2(getField2());
		bqItem.setField3(getField3());
		bqItem.setField4(getField4());

		bqItem.setField5(getField5());
		bqItem.setField6(getField6());
		bqItem.setField7(getField7());
		bqItem.setField8(getField8());
		bqItem.setField9(getField9());
		bqItem.setField10(getField10());
		bqItem.setTotalAmount(getTotalAmount());
		bqItem.setTax(getTax());
		bqItem.setTaxType(getTaxType());
		bqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		return bqItem;
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

	public RfqBqItem copyForRfq(RfqEventBq newBq) {
		RfqBqItem bq = new RfqBqItem();
		bq.setItemName(getItemName());
		bq.setLevel(getLevel());
		bq.setOrder(getOrder());
		bq.setUom(getUom());
		bq.setBq(newBq);
		bq.setQuantity(getQuantity());
		bq.setUnitPriceType(getUnitPriceType());
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

	public RftBqItem() {
	}

	public RftBqItem(RftEvent rftEvent, RftEventBq bq, RftBqItem parent, List<RftBqItem> children) {
		this.rfxEvent = rftEvent;
		this.bq = bq;
		this.parent = parent;
		this.children = children;
	}

	/**
	 * @return the rfxEvent
	 */
	public RftEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RftEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the parent
	 */
	public RftBqItem getParent() {
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
	public void setParent(RftBqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the bq
	 */
	public RftEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RftEventBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the children
	 */
	public List<RftBqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RftBqItem> children) {
		this.children = children;
	}

	/**
	 * @return the evaluationComments
	 */
	public List<RftBqEvaluationComments> getEvaluationComments() {
		return evaluationComments;
	}

	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	public void setEvaluationComments(List<RftBqEvaluationComments> evaluationComments) {
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

	public RftBqItem createShallowCopy() {
		RftBqItem ic = new RftBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setUnitPrice(getUnitPrice());
		ic.setPriceType(getPriceType());
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

	public RftBqItem createSearchShallowCopy() {
		RftBqItem ic = new RftBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setPriceType(getPriceType());
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
		if (getParent() != null) {
			RftBqItem parent = new RftBqItem();
			parent.setId(getParent().getId());
			ic.setParent(parent);
		}
		return ic;
	}
}
