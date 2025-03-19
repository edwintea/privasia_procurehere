/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENT_BQ_SUP_ITEM")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RfaSupplierBqItem extends BqItem implements Serializable {

	private static final long serialVersionUID = 2503886017008660826L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_BQ_SUP_ITEM"))
	private RfaEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_BQ_SUP_ID"))
	private Supplier supplier;

	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFA_EVNT_SUP_BQ_PARENT"))
	private RfaSupplierBqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("level, order")
	private List<RfaSupplierBqItem> children;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "SUPP_BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFA_EV_SUP_BQ"))
	private RfaSupplierBq supplierBq;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfaSupplierBqItem")
	List<RfaSupplierComment> comment;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BQ_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_RFA_EVENT_BQ_ITEM_ID"))
	private RfaBqItem bqItem;

	@Transient
	private String bqItemId;

	@Transient
	private List<String> taxTypeList;

	public RfaSupplierBqItem() {
	}

	public RfaSupplierBqItem copyForRfa(RfaSupplierBq newSupplierBq, Supplier supplier) {
		RfaSupplierBqItem bq = new RfaSupplierBqItem();
		bq.setEvent(getEvent());
		bq.setItemName(getItemName());
		bq.setLevel(getLevel());
		bq.setOrder(getOrder());
		bq.setUom(getUom());
		bq.setSupplier(supplier);
		bq.setBqItem(getBqItem());
		bq.setSupplierBq(newSupplierBq);
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

	public RfaSupplierBqItem(RfaBqItem item, Supplier supplier, RfaSupplierBq supplierBq) {
		// Children, Parent, id - Dont do
		this.setBqItem(item);
		this.setSupplierBq(supplierBq);
		this.event = item.getRfxEvent();
		this.setSupplier(supplier);
		this.setField1(item.getField1());
		this.setField2(item.getField2());
		this.setField3(item.getField3());
		this.setField4(item.getField4());

		this.setField5(item.getField5());
		this.setField6(item.getField6());
		this.setField7(item.getField7());
		this.setField8(item.getField8());
		this.setField9(item.getField9());
		this.setField10(item.getField10());

		this.setItemDescription(item.getItemDescription());
		this.setItemName(item.getItemName());
		this.setLevel(item.getLevel());
		this.setOrder(item.getOrder());
		this.setPriceType(item.getPriceType());
		this.setUnitPrice(item.getUnitPrice());
		this.setUnitPriceType(item.getUnitPriceType());
		this.setQuantity(item.getQuantity());
		this.setUom(item.getUom());

		BigDecimal qty = item.getQuantity();
		if (qty == null) {
			qty = BigDecimal.ZERO;
		}
		BigDecimal totalAmount = item.getUnitPrice() != null ? item.getUnitPrice().multiply(qty) : BigDecimal.ZERO;
		this.setTotalAmount(totalAmount);

		if (CollectionUtil.isNotEmpty(item.getChildren())) {
			this.children = new ArrayList<RfaSupplierBqItem>();
			for (RfaBqItem child : item.getChildren()) {
				RfaSupplierBqItem childBqItem = new RfaSupplierBqItem(child, supplier, supplierBq);
				childBqItem.setParent(this);
				this.children.add(childBqItem);
			}
		}

	}

	public RfaSupplierBqItem createSearchShallowCopy() {
		RfaSupplierBqItem ic = new RfaSupplierBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setPriceType(getPriceType());
		ic.setTotalAmount(getTotalAmount());
		ic.setTax(getTax());
		ic.setTaxType(getTaxType());
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
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

		ic.setBqItem(getBqItem());
		ic.setSupplierBq(getSupplierBq());
		if (getParent() != null) {
			RfaSupplierBqItem parent = new RfaSupplierBqItem();
			parent.setId(getParent().getId());
			ic.setParent(parent);
		}

		return ic;
	}

	public List<String> getTaxTypeList() {
		List<String> list = new ArrayList<>();
		for (TaxType tax : TaxType.values()) {
			list.add(tax.name());
		}
		return list;
	}

	/**
	 * @param taxTypeList the taxTypeList to set
	 */
	public void setTaxTypeList(List<String> taxTypeList) {
		this.taxTypeList = taxTypeList;
	}

	/**
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the parent
	 */
	public RfaSupplierBqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfaSupplierBqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<RfaSupplierBqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RfaSupplierBqItem> children) {
		this.children = children;
	}

	// /**
	// * @return the bq
	// */
	// public RfaEventBq getBq() {
	// return bq;
	// }
	//
	// /**
	// * @param bq the bq to set
	// */
	// public void setBq(RfaEventBq bq) {
	// this.bq = bq;
	// }

	/**
	 * @return the comment
	 */
	public List<RfaSupplierComment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<RfaSupplierComment> comment) {
		if (this.comment == null) {
			this.comment = new ArrayList<RfaSupplierComment>();
		} else {
			this.comment.clear();
		}
		if (comment != null) {
			this.comment.addAll(comment);
		}
	}

	/**
	 * @return the supplierBq
	 */
	public RfaSupplierBq getSupplierBq() {
		return supplierBq;
	}

	/**
	 * @param supplierBq the supplierBq to set
	 */
	public void setSupplierBq(RfaSupplierBq supplierBq) {
		this.supplierBq = supplierBq;
	}

	/**
	 * @return the bqItem
	 */
	public RfaBqItem getBqItem() {
		return bqItem;
	}

	/**
	 * @param bqItem the bqItem to set
	 */
	public void setBqItem(RfaBqItem bqItem) {
		this.bqItem = bqItem;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * @return the bqItemId
	 */
	public String getBqItemId() {
		return bqItemId;
	}

	/**
	 * @param bqItemId the bqItemId to set
	 */
	public void setBqItemId(String bqItemId) {
		this.bqItemId = bqItemId;
	}

	public RfaSupplierBqItem createShallowCopy() {
		RfaSupplierBqItem ic = new RfaSupplierBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setPriceType(getPriceType());
		ic.setTotalAmount(getTotalAmount());
		ic.setTax(getTax());
		ic.setTaxType(getTaxType());
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
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

		ic.setBqItem(getBqItem());
		ic.setSupplierBq(getSupplierBq());
		ic.setEvent(getEvent());

		return ic;
	}

	public RfaSupplierBqItem copyForRfa() {
		RfaSupplierBqItem supBqItem = new RfaSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}

	public RfpSupplierBqItem copyForRfp() {
		RfpSupplierBqItem supBqItem = new RfpSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}

	public RfqSupplierBqItem copyForRfq() {
		RfqSupplierBqItem supBqItem = new RfqSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}

	public RftSupplierBqItem copyForRft() {
		RftSupplierBqItem supBqItem = new RftSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
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
		return "RftSupplierBqItem [ " + super.toLogString() + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfaSupplierBqItem [tax=" + getTax() + ",  totalAmount=" + getTotalAmount() + ", comment=" + comment + "]";
	}

}
